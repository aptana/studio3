/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.html.contentassist;

import java.net.URI;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileInfo;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.text.contentassist.IContextInformationValidator;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

import com.aptana.core.IURIMapper;
import com.aptana.editor.common.AbstractThemeableEditor;
import com.aptana.editor.common.CommonContentAssistProcessor;
import com.aptana.editor.common.contentassist.CommonCompletionProposal;
import com.aptana.editor.common.contentassist.LexemeProvider;
import com.aptana.editor.common.contentassist.UserAgentManager;
import com.aptana.editor.css.CSSSourceConfiguration;
import com.aptana.editor.html.HTMLPlugin;
import com.aptana.editor.html.HTMLScopeScanner;
import com.aptana.editor.html.HTMLSourceConfiguration;
import com.aptana.editor.html.HTMLTagUtil;
import com.aptana.editor.html.contentassist.index.HTMLIndexConstants;
import com.aptana.editor.html.contentassist.model.AttributeElement;
import com.aptana.editor.html.contentassist.model.ElementElement;
import com.aptana.editor.html.contentassist.model.EntityElement;
import com.aptana.editor.html.contentassist.model.EventElement;
import com.aptana.editor.html.contentassist.model.ValueElement;
import com.aptana.editor.html.parsing.HTMLParseState;
import com.aptana.editor.html.parsing.lexer.HTMLTokenType;
import com.aptana.editor.html.preferences.IPreferenceContants;
import com.aptana.editor.js.JSSourceConfiguration;
import com.aptana.editor.xml.TagUtil;
import com.aptana.parsing.lexer.IRange;
import com.aptana.parsing.lexer.Lexeme;
import com.aptana.parsing.lexer.Range;
import com.aptana.preview.ProjectPreviewUtil;
import com.aptana.webserver.core.EFSWebServerConfiguration;
import com.aptana.webserver.core.WebServerCorePlugin;

public class HTMLContentAssistProcessor extends CommonContentAssistProcessor
{
	private static final String DOCTYPE_PRECEDING_TEXT = "!"; //$NON-NLS-1$

	/**
	 * LocationType
	 */
	static enum LocationType
	{
		// coarse-grain locations
		ERROR,
		IN_OPEN_TAG,
		IN_CLOSE_TAG,
		IN_DOCTYPE,
		IN_COMMENT,
		IN_TEXT,
		IN_ELEMENT_NAME,

		// fine-grain locations
		IN_ATTRIBUTE_NAME,
		IN_ATTRIBUTE_VALUE
	};

	static final Image ELEMENT_ICON = HTMLPlugin.getImage("/icons/element.png"); //$NON-NLS-1$
	private static final Image ATTRIBUTE_ICON = HTMLPlugin.getImage("/icons/attribute.png"); //$NON-NLS-1$
	private static final Image EVENT_ICON = HTMLPlugin.getImage("/icons/event.gif"); //$NON-NLS-1$
	private static final Map<String, LocationType> locationMap;
	private static final Map<String, String> DOCTYPES;

	private HTMLIndexQueryHelper _queryHelper;
	private IContextInformationValidator _validator;
	private Lexeme<HTMLTokenType> _currentLexeme;
	private IRange _replaceRange;
	private IDocument _document;

	/**
	 * static initializer
	 */
	static
	{
		locationMap = new HashMap<String, LocationType>();
		locationMap.put(HTMLSourceConfiguration.DEFAULT, LocationType.IN_TEXT);
		locationMap.put(HTMLSourceConfiguration.HTML_COMMENT, LocationType.IN_COMMENT);
		locationMap.put(HTMLSourceConfiguration.HTML_DOCTYPE, LocationType.IN_DOCTYPE);

		locationMap.put(HTMLSourceConfiguration.HTML_SCRIPT, LocationType.IN_OPEN_TAG);
		locationMap.put(HTMLSourceConfiguration.HTML_STYLE, LocationType.IN_OPEN_TAG);
		locationMap.put(HTMLSourceConfiguration.HTML_TAG, LocationType.IN_OPEN_TAG);

		locationMap.put(JSSourceConfiguration.DEFAULT, LocationType.IN_TEXT);
		locationMap.put(CSSSourceConfiguration.DEFAULT, LocationType.IN_TEXT);
		locationMap.put(IDocument.DEFAULT_CONTENT_TYPE, LocationType.IN_TEXT);

		DOCTYPES = new HashMap<String, String>();
		DOCTYPES.put("HTML 5", "HTML"); //$NON-NLS-1$ //$NON-NLS-2$
		DOCTYPES.put("HTML 4.01 Strict", //$NON-NLS-1$
				"HTML PUBLIC \"-//W3C//DTD HTML 4.01//EN\"\n\"http://www.w3.org/TR/html4/strict.dtd\""); //$NON-NLS-1$
		DOCTYPES.put("HTML 4.01 Transitional", //$NON-NLS-1$
				"HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\"\n\"http://www.w3.org/TR/html4/loose.dtd\""); //$NON-NLS-1$
		DOCTYPES.put("HTML 4.01 Transitional (Quirks)", "HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\""); //$NON-NLS-1$ //$NON-NLS-2$
		DOCTYPES.put("HTML 4.01 Frameset", //$NON-NLS-1$
				"HTML PUBLIC \"-//W3C//DTD HTML 4.01 Frameset//EN\"\n\"http://www.w3.org/TR/html4/frameset.dtd\""); //$NON-NLS-1$
		DOCTYPES.put("XHTML 1.1", //$NON-NLS-1$
				"html PUBLIC \"-//W3C//DTD XHTML 1.1//EN\" \"http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd\""); //$NON-NLS-1$
		DOCTYPES.put("XHTML 1.0 Strict", //$NON-NLS-1$
				"html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\"\n\"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\""); //$NON-NLS-1$
		DOCTYPES.put("XHTML 1.0 Transitional", //$NON-NLS-1$
				"html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\"\n\"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\""); //$NON-NLS-1$
		DOCTYPES.put("XHTML 1.0 Frameset", //$NON-NLS-1$
				"html PUBLIC \"-//W3C//DTD XHTML 1.0 Frameset//EN\"\n\"http://www.w3.org/TR/xhtml1/DTD/xhtml1-frameset.dtd\""); //$NON-NLS-1$
		DOCTYPES.put("HTML 3.2", "HTML PUBLIC \"-//W3C//DTD HTML 3.2 Final//EN\""); //$NON-NLS-1$ //$NON-NLS-2$
		DOCTYPES.put("HTML 2.0", "HTML PUBLIC \"-//IETF//DTD HTML//EN\""); //$NON-NLS-1$ //$NON-NLS-2$
	}

	/**
	 * HTMLIndexContentAssistProcessor
	 * 
	 * @param editor
	 */
	public HTMLContentAssistProcessor(AbstractThemeableEditor editor)
	{
		super(editor);

		this._queryHelper = new HTMLIndexQueryHelper();
	}

	/**
	 * addAttributeAndEventProposals
	 * 
	 * @param lexemeProvider
	 * @param offset
	 */
	protected List<ICompletionProposal> addAttributeAndEventProposals(LexemeProvider<HTMLTokenType> lexemeProvider,
			int offset)
	{
		List<ICompletionProposal> proposals = new ArrayList<ICompletionProposal>();
		String elementName = this.getElementName(lexemeProvider, offset);
		ElementElement element = this._queryHelper.getElement(elementName);

		if (element != null)
		{
			int length = 2;
			String postfix = "=\"\""; //$NON-NLS-1$
			switch (this._currentLexeme.getType())
			{
				case EQUAL:
					int index = lexemeProvider.getLexemeFloorIndex(offset);

					if (index > 0)
					{
						this._replaceRange = this._currentLexeme = lexemeProvider.getLexeme(index - 1);
						postfix = ""; //$NON-NLS-1$
						length = 0;
					}
					break;

				case TAG_END:
					this._replaceRange = null;
					break;

				default:
					index = lexemeProvider.getLexemeFloorIndex(offset);
					Lexeme<HTMLTokenType> nextlexeme = lexemeProvider.getLexeme(index + 1);
					if (nextlexeme != null && nextlexeme.getType() == HTMLTokenType.EQUAL)
					{
						postfix = ""; //$NON-NLS-1$
						length = 0;
					}
					break;
			}

			List<String> userAgents = element.getUserAgentNames();
			Image[] userAgentIcons = UserAgentManager.getInstance().getUserAgentImages(userAgents);

			for (AttributeElement attribute : this._queryHelper.getAttributes(element))
			{
				String name = attribute.getName();
				CommonCompletionProposal p = this.createProposal( //
					name, //
					name + postfix, //
					ATTRIBUTE_ICON, //
					attribute.getDescription(), //
					userAgentIcons, //
					HTMLIndexConstants.CORE, //
					offset, //
					name.length() + length //
				);

				proposals.add(p);
			}

			for (EventElement event : this._queryHelper.getEvents(element))
			{
				String name = event.getName();
				CommonCompletionProposal p = this.createProposal( //
					name, //
					name + postfix, //
					EVENT_ICON, //
					event.getDescription(), //
					userAgentIcons, //
					HTMLIndexConstants.CORE, //
					offset, //
					name.length() + length //
				);

				proposals.add(p);
			}
		}

		return proposals;
	}

	/**
	 * addAttributeValueProposals
	 * 
	 * @param offset
	 * @param elementName
	 * @param attributeName
	 */
	private List<ICompletionProposal> addAttributeValueProposals(int offset, String elementName, String attributeName)
	{
		List<ICompletionProposal> proposals = new ArrayList<ICompletionProposal>();
		AttributeElement attribute = this._queryHelper.getAttribute(elementName, attributeName);

		if (attribute != null)
		{
			for (ValueElement value : attribute.getValues())
			{
				String name = value.getName();
				Image icon = ATTRIBUTE_ICON;
				String description = value.getDescription();
				Image[] userAgentIcons = this.getAllUserAgentIcons();

				this.addProposal(proposals, name, icon, description, userAgentIcons, offset);
			}
		}
		return proposals;
	}

	/**
	 * addAttributeValueProposals
	 * 
	 * @param proposals
	 * @param lexemeProvider
	 * @param offset
	 */
	private List<ICompletionProposal> addAttributeValueProposals(LexemeProvider<HTMLTokenType> lexemeProvider,
			int offset)
	{
		List<ICompletionProposal> proposals = new ArrayList<ICompletionProposal>();
		String attributeName = this.getAttributeName(lexemeProvider, offset);

		if (attributeName != null && attributeName.length() > 0)
		{
			switch (this._currentLexeme.getType())
			{
				case SINGLE_QUOTED_STRING:
				case DOUBLE_QUOTED_STRING:
					// trim off the quotes
					if (this._currentLexeme.getLength() >= 2)
					{
						int startingOffset = this._currentLexeme.getStartingOffset() + 1;
						int endingOffset = this._currentLexeme.getEndingOffset() - 1;

						this._replaceRange = new Range(startingOffset, endingOffset);
					}
					break;

				case EQUAL:
					this._replaceRange = new Range(offset, offset - 1);
					break;

				default:
					break;
			}

			if (attributeName.equals("id")) //$NON-NLS-1$
			{
				proposals.addAll(this.addIDProposals(offset));
			}
			else if (attributeName.equals("class")) //$NON-NLS-1$
			{
				proposals.addAll(this.addClassProposals(offset));
			}
			else if (attributeName.equals("src") || attributeName.equals("href")) //$NON-NLS-1$ //$NON-NLS-2$
			{
				proposals.addAll(this.addURIPathProposals(offset));
			}
			else
			{
				String elementName = this.getElementName(lexemeProvider, offset);

				proposals.addAll(this.addAttributeValueProposals(offset, elementName, attributeName));
			}
		}
		return proposals;
	}

	/**
	 * addClassProposals
	 * 
	 * @param offset
	 */
	protected List<ICompletionProposal> addClassProposals(int offset)
	{
		List<ICompletionProposal> proposals = new ArrayList<ICompletionProposal>();
		Map<String, String> classes = this._queryHelper.getClasses(this.getIndex());

		if (classes != null)
		{
			UserAgentManager manager = UserAgentManager.getInstance();
			String[] userAgents = manager.getActiveUserAgentIDs(); // classes can be used by all user agents
			Image[] userAgentIcons = manager.getUserAgentImages(userAgents);

			for (Entry<String, String> entry : classes.entrySet())
			{
				this.addProposal(proposals, entry.getKey(), ATTRIBUTE_ICON, null, userAgentIcons, entry.getValue(),
						offset);
			}
		}
		return proposals;
	}

	/**
	 * addURIPathProposals - Does incremental proposals for filepaths in the 'src'/'href' values.
	 * 
	 * @param offset
	 */
	protected List<ICompletionProposal> addURIPathProposals(int offset)
	{
		List<ICompletionProposal> proposals = new ArrayList<ICompletionProposal>();
		this._replaceRange = null;
		try
		{
			String valuePrefix = this._currentLexeme.getText();
			int length = offset - this._currentLexeme.getStartingOffset();
			valuePrefix = valuePrefix.substring(0, length);

			URI editorStoreURI = getURI();
			IFileStore editorStore = EFS.getStore(editorStoreURI);

			// Strip the quotes off the value prefix!
			if (valuePrefix.length() > 0 && (valuePrefix.charAt(0) == '"' || valuePrefix.charAt(0) == '\''))
			{
				valuePrefix = valuePrefix.substring(1);
				offset = this._currentLexeme.getStartingOffset() + 1;
			}

			// Based on prefix we need to choose project root (webroot), some other place, or current file as URI
			// base.
			IFileStore baseStore = null;
			if (valuePrefix.length() > 0 && valuePrefix.charAt(0) == '/')
			{
				baseStore = EFS.getStore(getProjectURI());

				// Get the project webroot
				IURIMapper serverConfiguration = ProjectPreviewUtil
						.getServerConfiguration(getProject());
				if (serverConfiguration == null)
				{
					for (IURIMapper server : WebServerCorePlugin.getDefault()
							.getServerConfigurationManager().getServerConfigurations())
					{
						if (server.resolve(editorStore) != null)
						{
							serverConfiguration = server;
							break;
						}
					}
				}
				if (serverConfiguration != null && serverConfiguration instanceof EFSWebServerConfiguration)
				{
					URI documentRoot = ((EFSWebServerConfiguration) serverConfiguration).getDocumentRoot();
					if (documentRoot != null)
					{
						baseStore = EFS.getStore(documentRoot);
					}
				}
				else
				{
					// HACK This is for Rails projects, when user hasn't specified special server preview
					IFileStore publicDir = baseStore.getChild("public"); //$NON-NLS-1$
					if (publicDir.fetchInfo().exists())
					{
						baseStore = publicDir;
					}
				}
				baseStore = baseStore.getChild(valuePrefix);
			}
			else
			{
				baseStore = editorStore.getParent();
				baseStore = baseStore.getChild(valuePrefix);
			}

			// replace from last slash on...
			int lastSlash = valuePrefix.lastIndexOf('/');
			if (lastSlash != -1)
			{
				offset += lastSlash + 1;
			}
			this._replaceRange = new Range(offset, this._currentLexeme.getEndingOffset() - 1);

			// TODO Handle when it's just an absolute URI!
			// else if ()
			// {
			//
			// }

			// Then we grab the filestore pointing to the parent and ask for the children!
			Image[] userAgentIcons = this.getAllUserAgentIcons();
			for (IFileStore f : baseStore.childStores(EFS.NONE, new NullProgressMonitor()))
			{
				String name = f.getName();
				// Don't include the current file in the list
				if (name.startsWith(".") || f.toURI().equals(editorStoreURI)) //$NON-NLS-1$
				{
					continue;
				}

				// Grab images based on whether it's a dir or not. For files can we determine if it matches some
				// content type and grab the icon for that?
				Image image = null;
				IFileInfo info = f.fetchInfo();
				if (info.isDirectory())
				{
					image = PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJ_FOLDER);
				}
				else
				{
					ImageDescriptor imageDesc = PlatformUI.getWorkbench().getEditorRegistry().getImageDescriptor(name);
					if (imageDesc != null)
					{
						image = imageDesc.createImage();
					}
					if (image == null)
					{
						image = PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJ_FILE);
					}
				}
				CommonCompletionProposal cp = createProposal(name, name, image, null, userAgentIcons, null, offset,
						name.length());
				proposals.add(cp);

			}
		}
		catch (CoreException e)
		{
			HTMLPlugin.logError(e);
		}

		return proposals;
	}

	/**
	 * addElementProposals
	 * 
	 * @param lexemeProvider
	 * @param offset
	 */
	protected List<ICompletionProposal> addElementProposals(LexemeProvider<HTMLTokenType> lexemeProvider, int offset)
	{
		List<ICompletionProposal> proposals = new ArrayList<ICompletionProposal>();
		List<ElementElement> elements = this._queryHelper.getElements();

		if (elements != null)
		{
			boolean close = true;
			int replaceLength = 0;
			int replaceOffset = offset;
			if (this._currentLexeme.getType() == HTMLTokenType.META) // DOCTYPE?
			{
				replaceOffset = this._currentLexeme.getStartingOffset();
				replaceLength = this._currentLexeme.getLength();

				// What if previous lexeme is "!", We need to replace that!
				int index = lexemeProvider.getLexemeIndex(_currentLexeme.getStartingOffset());
				Lexeme<HTMLTokenType> previousLexeme = lexemeProvider.getLexeme(index - 1);
				if (previousLexeme.getText().equals(DOCTYPE_PRECEDING_TEXT))
				{
					replaceOffset = previousLexeme.getStartingOffset();
					replaceLength = this._currentLexeme.getEndingOffset() - replaceOffset + 1;
				}
			}
			else if (this._currentLexeme.getType() == HTMLTokenType.TEXT
					&& this._currentLexeme.getText().equals(DOCTYPE_PRECEDING_TEXT)) // !
			{
				replaceOffset = this._currentLexeme.getStartingOffset();
				replaceLength = this._currentLexeme.getLength(); // replace the '!'

				int index = lexemeProvider.getLexemeIndex(_currentLexeme.getStartingOffset());
				Lexeme<HTMLTokenType> nextLexeme = lexemeProvider.getLexeme(index + 1);
				if (nextLexeme != null && nextLexeme.getType() == HTMLTokenType.TAG_END)
				{
					replaceLength = nextLexeme.getEndingOffset() - replaceOffset;
				}
			}
			else if (this._currentLexeme.getType() == HTMLTokenType.TAG_END) // '|>
			{
				replaceLength = 1; // replace the '>'
				// What if previous lexeme is "!", We need to replace that!
				int index = lexemeProvider.getLexemeIndex(_currentLexeme.getStartingOffset());
				Lexeme<HTMLTokenType> previousLexeme = lexemeProvider.getLexeme(index - 1);
				if (previousLexeme.getText().equals(DOCTYPE_PRECEDING_TEXT))
				{
					replaceOffset = previousLexeme.getStartingOffset();
					replaceLength += previousLexeme.getLength();
				}
			}
			else if (this._currentLexeme.getType() != HTMLTokenType.TAG_START) // as long as it's not: "<|<"
			{
				// We're on element name, replace it
				int index = lexemeProvider.getLexemeCeilingIndex(_currentLexeme.getEndingOffset() + 1);

				if (index == -1 || index >= lexemeProvider.size())
				{
					index = lexemeProvider.size() - 1;
				}

				Lexeme<HTMLTokenType> nextLexeme = lexemeProvider.getLexeme(index);

				if (nextLexeme != null) // && !nextLexeme.equals(_currentLexeme))
				{
					replaceOffset = _currentLexeme.getStartingOffset();
					replaceLength = _currentLexeme.getLength();

					if (nextLexeme.equals(this._currentLexeme) == false)
					{
						if (nextLexeme.getType() == HTMLTokenType.TAG_END)
						{
							// Followed by '>', so replace spaces plus end
							replaceLength += nextLexeme.getEndingOffset() - _currentLexeme.getEndingOffset();
						}
						else if (nextLexeme.getType() != HTMLTokenType.TAG_START)
						{
							// If there's an attribute we don't want to add ">" or close tag!
							close = false;
						}
					}
				}
			}

			// If user doesn't want tags closed for them, then don't do it!
			boolean addCloseTag = HTMLPlugin.getDefault().getPreferenceStore()
					.getBoolean(IPreferenceContants.HTML_AUTO_CLOSE_TAG_PAIRS);

			HTMLParseState state = null;
			for (ElementElement element : elements)
			{
				String replaceString = element.getName();
				List<Integer> positions = new ArrayList<Integer>();
				int cursorPosition = replaceString.length();
				if (close)
				{
					if (state == null)
					{
						state = new HTMLParseState();
						state.setEditState(_document.get(), null, 0, 0);
					}

					if (element.getName().charAt(0) == '!') // don't close DOCTYPE with a slash
					{
						cursorPosition += 1;
						// Don't add ">" unless we know we need it! Look at next Lexeme!
						int index = lexemeProvider.getLexemeIndex(_currentLexeme.getStartingOffset());
						Lexeme<HTMLTokenType> nextLexeme = lexemeProvider.getLexeme(index + 1);
						if (nextLexeme == null || nextLexeme.getType() == HTMLTokenType.TAG_START)
						{
							replaceString += " >"; //$NON-NLS-1$
						}
					}
					else if (state.isEmptyTagType(element.getName()))
					{
						replaceString += " />"; //$NON-NLS-1$
						// TODO Depending on tag, we should stick cursor inside the tag or after the end of tag. Right
						// now it's stuck at end of tag
						positions.add(cursorPosition + 3);
					}
					else
					{
						// If the tag doesn't exist in the doc, we get back that it's closed. We need to copy the
						// document and insert the tag into it
						IDocument doc = new Document(_document.get());
						try
						{
							doc.replace(replaceOffset, replaceLength, element.getName() + ">"); //$NON-NLS-1$
						}
						catch (BadLocationException e)
						{
							// ignore
						}
						if (addCloseTag && !TagUtil.tagClosed(doc, element.getName()))
						{
							replaceString += "></" + element.getName() + ">"; //$NON-NLS-1$ //$NON-NLS-2$
							positions.add(cursorPosition + 1);
							positions.add(cursorPosition + 4 + element.getName().length());
						}
						else
						{
							replaceString += ">"; //$NON-NLS-1$
							positions.add(cursorPosition + 1);
						}
					}
				}
				positions.add(0, cursorPosition);
				HTMLTagProposal proposal = new HTMLTagProposal(replaceString, replaceOffset, replaceLength, element,
						positions.toArray(new Integer[positions.size()]));
				proposals.add(proposal);
			}
		}
		return proposals;
	}

	/**
	 * addEntityProposals
	 * 
	 * @param result
	 * @param offset
	 */
	private void addEntityProposals(List<ICompletionProposal> proposals, LexemeProvider<HTMLTokenType> lexemeProvider,
			int offset)
	{
		boolean doIt = false;
		this.setEntityRange(lexemeProvider, offset);
		if (this._replaceRange == null)
		{
			doIt = true;
		}
		else
		{
			String text = null;
			try
			{
				text = this._document.get(this._replaceRange.getStartingOffset(), this._replaceRange.getLength());
			}
			catch (BadLocationException e)
			{
				// ignore
			}
			if (text != null && text.length() == 0)
			{
				text = this._currentLexeme.getText();
				this._replaceRange = this._currentLexeme;
			}
			if (text != null && text.startsWith("&")) //$NON-NLS-1$
			{
				doIt = true;
			}
		}

		if (doIt)
		{
			List<EntityElement> entities = this._queryHelper.getEntities();

			if (entities != null)
			{
				this.setEntityRange(lexemeProvider, offset);
				Image[] userAgentIcons = this.getAllUserAgentIcons();

				for (EntityElement entity : entities)
				{
					this.addProposal(proposals, entity.getName(), ELEMENT_ICON, entity.getDescription(),
							userAgentIcons, offset);
				}
			}
		}
	}

	/**
	 * addDoctypeProposals
	 * 
	 * @param result
	 * @param offset
	 */
	private void addDoctypeProposals(List<ICompletionProposal> proposals, LexemeProvider<HTMLTokenType> lexemeProvider,
			int offset)
	{
		this._replaceRange = null;
		// Replace all the way until we hit the end of the doctype tag!
		Lexeme<HTMLTokenType> ptr = _currentLexeme;
		Image[] userAgentIcons = this.getAllUserAgentIcons();

		if (ptr != null && ptr.getType() == HTMLTokenType.META && ptr.contains(offset))
		{
			proposals.addAll(addElementProposals(lexemeProvider, offset));
			return;
		}

		while (ptr != null && ptr.getType() != HTMLTokenType.TAG_END)
		{
			int index = lexemeProvider.getLexemeIndex(ptr.getStartingOffset());
			ptr = lexemeProvider.getLexeme(index + 1);
		}
		if (ptr != null)
		{
			this._replaceRange = new Range(_currentLexeme.getStartingOffset(), ptr.getStartingOffset() - 1);
		}

		for (Map.Entry<String, String> entry : DOCTYPES.entrySet())
		{
			String src = entry.getValue();
			String name = entry.getKey();
			CommonCompletionProposal proposal = createProposal(name, src, ELEMENT_ICON,
					MessageFormat.format("&lt;!DOCTYPE {0}&gt;", src), userAgentIcons, //$NON-NLS-1$
					HTMLIndexConstants.CORE, offset, src.length());
			if (src.equalsIgnoreCase("HTML")) // Make HTML 5 the default //$NON-NLS-1$
			{
				proposal.setIsSuggestedSelection(true);
				proposal.setIsDefaultSelection(true);
			}
			proposals.add(proposal);
		}
	}

	/**
	 * addIDProposals
	 * 
	 * @param offset
	 */
	protected List<ICompletionProposal> addIDProposals(int offset)
	{
		List<ICompletionProposal> proposals = new ArrayList<ICompletionProposal>();
		Map<String, String> ids = this._queryHelper.getIDs(this.getIndex());

		if (ids != null)
		{
			UserAgentManager manager = UserAgentManager.getInstance();
			String[] userAgents = manager.getActiveUserAgentIDs(); // classes can be used by all user agents
			Image[] userAgentIcons = manager.getUserAgentImages(userAgents);

			for (Entry<String, String> entry : ids.entrySet())
			{
				this.addProposal(proposals, entry.getKey(), ATTRIBUTE_ICON, null, userAgentIcons, entry.getValue(),
						offset);
			}
		}
		return proposals;
	}

	/**
	 * addOpenTagProposals
	 * 
	 * @param lexemeProvider
	 * @param offset
	 * @param result
	 */
	private void addOpenTagProposals(LocationType fineLocation, List<ICompletionProposal> proposals,
			LexemeProvider<HTMLTokenType> lexemeProvider, int offset)
	{
		switch (fineLocation)
		{
			case IN_ELEMENT_NAME:
				proposals.addAll(this.addElementProposals(lexemeProvider, offset));
				break;

			case IN_ATTRIBUTE_NAME:
				proposals.addAll(this.addAttributeAndEventProposals(lexemeProvider, offset));
				break;

			case IN_ATTRIBUTE_VALUE:
				proposals.addAll(this.addAttributeValueProposals(lexemeProvider, offset));
				break;

			default:
				break;
		}
	}

	/**
	 * addCloseTagProposals
	 * 
	 * @param lexemeProvider
	 * @param offset
	 * @param result
	 */
	private boolean addUnclosedTagProposals(LocationType fineLocation, List<ICompletionProposal> proposals,
			LexemeProvider<HTMLTokenType> lexemeProvider, int offset)
	{
		HTMLParseState state = null;
		boolean addedProposal = false;
		// First see if there are any unclosed tags, suggest them first
		Set<String> unclosedElements = getUnclosedTagNames(offset);
		if (unclosedElements != null && !unclosedElements.isEmpty())
		{
			for (String unclosedElement : unclosedElements)
			{
				ElementElement element = this._queryHelper.getElement(unclosedElement);
				if (element == null)
				{
					continue;
				}
				if (state == null)
				{
					state = new HTMLParseState();
					state.setEditState(_document.get(), null, 0, 0);
				}
				if (state.isEmptyTagType(element.getName()))
				{
					continue;
				}
				proposals.add(createCloseTagProposal(element, lexemeProvider, offset));
				addedProposal = true;
			}
		}
		return addedProposal;
	}

	/**
	 * addCloseTagProposals
	 * 
	 * @param lexemeProvider
	 * @param offset
	 * @param result
	 */
	private boolean addDefaultCloseTagProposals(LocationType fineLocation, List<ICompletionProposal> proposals,
			LexemeProvider<HTMLTokenType> lexemeProvider, int offset)
	{
		HTMLParseState state = null;
		boolean addedProposal = false;
		// Looks like no unclosed tags that make sense. Suggest every non-self-closing tag.
		List<ElementElement> elements = this._queryHelper.getElements();
		if (elements != null)
		{
			for (ElementElement element : elements)
			{
				if (state == null)
				{
					state = new HTMLParseState();
					state.setEditState(_document.get(), null, 0, 0);
				}
				if (state.isEmptyTagType(element.getName()))
				{
					continue;
				}
				proposals.add(createCloseTagProposal(element, lexemeProvider, offset));
				addedProposal = true;
			}
		}
		return addedProposal;
	}

	private CommonCompletionProposal createCloseTagProposal(ElementElement element,
			LexemeProvider<HTMLTokenType> lexemeProvider, int offset)
	{
		List<String> userAgents = element.getUserAgentNames();
		Image[] userAgentIcons = UserAgentManager.getInstance().getUserAgentImages(userAgents);
		String replaceString = "/" + element.getName(); //$NON-NLS-1$
		Lexeme<HTMLTokenType> firstLexeme = lexemeProvider.getFirstLexeme(); // Open of tag
		Lexeme<HTMLTokenType> tagLexeme = lexemeProvider.getLexeme(1); // Tag name
		Lexeme<HTMLTokenType> closeLexeme = lexemeProvider.getLexeme(2); // Close of tag

		int replaceLength = 0;
		if (tagLexeme != null && tagLexeme.contains(offset))
		{
			replaceLength += tagLexeme.getLength();
		}

		// We can be at: |<a, <|a, |</a, </|a, etc.
		// If our cursor is before the tag in the lexeme list, assume we aren't
		// modifying the current tag after the cursor, but rather inserting a whole new tag
		int replaceOffset = offset;

		// In this case, we see our offset is greater than the start of the
		// list, so we assume we are replacing
		if (offset > firstLexeme.getStartingOffset())
		{
			replaceOffset = firstLexeme.getStartingOffset() + 1;
			if ("</".equals(firstLexeme.getText())) //$NON-NLS-1$
			{
				// we'll replace the "/"
				replaceLength += 1;
			}
			if (tagLexeme != null && HTMLTagUtil.isTag(tagLexeme))
			{
				replaceLength += tagLexeme.getLength();
			}
			// current tag isn't closed, so we will close it for the user
			if (closeLexeme == null || !HTMLTokenType.TAG_END.equals(closeLexeme.getType()))
			{
				replaceString += ">"; //$NON-NLS-1$
			}
		}
		else
		{
			try
			{
				// add the close of the tag, since we're in a situation like <|<a>
				replaceString += ">"; //$NON-NLS-1$
				String previous = _document.get(offset - 1, 1);
				// situation like </|<a>
				if ("/".equals(previous)) { //$NON-NLS-1$
					replaceOffset -= 1;
					replaceLength += 1;
				}
			}
			catch (BadLocationException e)
			{
				// safe to ignore
			}
		}

		int cursorPosition = replaceString.length();

		CommonCompletionProposal proposal = new CommonCompletionProposal(replaceString, replaceOffset, replaceLength,
				cursorPosition, ELEMENT_ICON, "/" + element.getName(), null, element.getDescription()); //$NON-NLS-1$

		proposal.setFileLocation(HTMLIndexConstants.CORE);
		proposal.setUserAgentImages(userAgentIcons);
		return proposal;
	}

	protected Set<String> getUnclosedTagNames(int offset)
	{
		Set<String> unclosedElements = new HashSet<String>();
		try
		{
			ITypedRegion[] partitions = _document.computePartitioning(0, offset);
			for (ITypedRegion partition : partitions)
			{
				if (partition.getType().equals(HTMLSourceConfiguration.HTML_TAG))
				{
					String src = _document.get(partition.getOffset(), partition.getLength());
					int lessThanIndex = src.indexOf('<');

					// if '<' index outside current string, skip this partition
					if (lessThanIndex == -1 || lessThanIndex >= src.length() - 1)
					{
						continue;
					}

					// ignore tag containing offset, i.e. if cursor is at '|', <|a>, <a|> will not
					// include <a> as unclosed tag, but <a>| will.
					int greaterThanIndex = src.indexOf('>');
					if (greaterThanIndex == -1)
					{
						continue;
					}

					// get name of element and see if we are closed elsewhere in the document
					String tagName = TagUtil.getTagName(src);
					tagName = tagName.toLowerCase();
					if (!unclosedElements.contains(tagName) && !TagUtil.tagClosed(_document, tagName))
					{
						unclosedElements.add(tagName);
					}
				}
			}
		}
		catch (BadLocationException e)
		{
			// ignore
		}
		return unclosedElements;
	}

	/**
	 * addProposal
	 * 
	 * @param proposals
	 * @param name
	 * @param image
	 * @param description
	 * @param userAgents
	 * @param offset
	 */
	private void addProposal(List<ICompletionProposal> proposals, String name, Image image, String description,
			Image[] userAgents, int offset)
	{
		this.addProposal(proposals, name, image, description, userAgents, HTMLIndexConstants.CORE, offset);
	}

	/**
	 * addProposal
	 * 
	 * @param proposals
	 * @param name
	 * @param icon
	 * @param userAgents
	 * @param offset
	 */
	private void addProposal(List<ICompletionProposal> proposals, String name, Image image, String description,
			Image[] userAgents, String fileLocation, int offset)
	{
		CommonCompletionProposal proposal = createProposal(name, image, description, userAgents, fileLocation, offset);
		// add it to the list
		proposals.add(proposal);
	}

	private CommonCompletionProposal createProposal(String name, Image image, String description, Image[] userAgents,
			String fileLocation, int offset)
	{
		return createProposal(name, name, image, description, userAgents, fileLocation, offset, name.length());
	}

	protected CommonCompletionProposal createProposal(String displayName, String name, Image image, String description,
			Image[] userAgents, String fileLocation, int offset, int length)
	{
		IContextInformation contextInfo = null;

		// TEMP:
		int replaceLength = 0;

		if (this._replaceRange != null)
		{
			offset = this._replaceRange.getStartingOffset();
			replaceLength = this._replaceRange.getLength();
		}

		// build proposal
		CommonCompletionProposal proposal = new CommonCompletionProposal(name, offset, replaceLength, length, image,
				displayName, contextInfo, description);
		proposal.setFileLocation(fileLocation);
		proposal.setUserAgentImages(userAgents);
		return proposal;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.aptana.editor.common.CommonContentAssistProcessor#doComputeCompletionProposals(org.eclipse.jface.text.ITextViewer
	 * , int, char, boolean)
	 */
	@Override
	protected ICompletionProposal[] doComputeCompletionProposals(ITextViewer viewer, int offset, char activationChar,
			boolean autoActivated)
	{
		// tokenize the current document
		_document = viewer.getDocument();

		LexemeProvider<HTMLTokenType> lexemeProvider = this.createLexemeProvider(_document, offset);

		// store a reference to the lexeme at the current position
		this._replaceRange = this._currentLexeme = lexemeProvider.getFloorLexeme(offset);

		// first step is to determine if we're inside an open tag, close tag, text, etc.
		LocationType location = this.getCoarseLocationType(_document, lexemeProvider, offset);
		LocationType fineLocation = null;

		List<ICompletionProposal> result = new ArrayList<ICompletionProposal>();

		switch (location)
		{
			case IN_OPEN_TAG:
				fineLocation = this.getOpenTagLocationType(lexemeProvider, offset);
				this.addUnclosedTagProposals(fineLocation, result, lexemeProvider, offset);
				this.addOpenTagProposals(fineLocation, result, lexemeProvider, offset);
				break;

			case IN_CLOSE_TAG:
				fineLocation = this.getOpenTagLocationType(lexemeProvider, offset); // not actually used in this case,
																					// but resets _replaceRange
				boolean added = this.addUnclosedTagProposals(fineLocation, result, lexemeProvider, offset);
				if (!added)
				{
					this.addDefaultCloseTagProposals(fineLocation, result, lexemeProvider, offset);
				}
				break;

			case IN_TEXT:
				this.addEntityProposals(result, lexemeProvider, offset);
				break;

			case IN_DOCTYPE:
				this.addDoctypeProposals(result, lexemeProvider, offset);
				break;

			default:
				break;
		}

		// sort by display name
		Collections.sort(result, new Comparator<ICompletionProposal>()
		{
			public int compare(ICompletionProposal o1, ICompletionProposal o2)
			{
				return o1.getDisplayString().compareToIgnoreCase(o2.getDisplayString());
			}
		});

		// select the current proposal based on the current lexeme
		if (this._replaceRange != null)
		{
			try
			{
				String text = _document.get(this._replaceRange.getStartingOffset(), this._replaceRange.getLength());
				if (LocationType.IN_CLOSE_TAG.equals(location))
				{
					text = "/" + text; // proposals have "/" at the front //$NON-NLS-1$
				}

				this.setSelectedProposal(text, result);
			}
			catch (BadLocationException e)
			{
			}
		}

		return result.toArray(new ICompletionProposal[result.size()]);
	}

	/**
	 * createLexemeProvider
	 * 
	 * @param document
	 * @param offset
	 * @return
	 */
	LexemeProvider<HTMLTokenType> createLexemeProvider(IDocument document, int offset)
	{
		int documentLength = document.getLength();

		// account for last position returning an empty IDocument default partition
		int lexemeProviderOffset = (offset >= documentLength) ? documentLength - 1 : offset;

		return new LexemeProvider<HTMLTokenType>(document, lexemeProviderOffset, new HTMLScopeScanner())
		{
			@Override
			protected HTMLTokenType getTypeFromData(Object data)
			{
				return HTMLTokenType.get((String) data);
			}
		};
	}

	/**
	 * getAttributeName
	 * 
	 * @param lexemeProvider
	 * @param offset
	 * @return
	 */
	private String getAttributeName(LexemeProvider<HTMLTokenType> lexemeProvider, int offset)
	{
		String name = null;
		int index = lexemeProvider.getLexemeFloorIndex(offset);

		while (index >= 0)
		{
			Lexeme<HTMLTokenType> lexeme = lexemeProvider.getLexeme(index);

			if (lexeme.getType() == HTMLTokenType.EQUAL)
			{
				if (index >= 1)
				{
					lexeme = lexemeProvider.getLexeme(index - 1);

					if (lexeme != null)
					{
						name = lexeme.getText();
					}
				}

				break;
			}

			index--;
		}

		return name;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.common.CommonContentAssistProcessor#getCompletionProposalAutoActivationCharacters()
	 */
	@Override
	public char[] getCompletionProposalAutoActivationCharacters()
	{
		String chars = Platform.getPreferencesService().getString( //
				HTMLPlugin.PLUGIN_ID, //
				IPreferenceContants.HTML_ACTIVATION_CHARACTERS, //
				"", //$NON-NLS-1$
				null //
				);

		return (chars != null) ? chars.toCharArray() : null;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.common.CommonContentAssistProcessor#getContextInformationValidator()
	 */
	@Override
	public IContextInformationValidator getContextInformationValidator()
	{
		if (this._validator == null)
		{
			this._validator = new HTMLContextInformationValidator();
		}

		return this._validator;
	}

	/**
	 * getElementName
	 * 
	 * @param lexemeProvider
	 * @param offset
	 * @return
	 */
	private String getElementName(LexemeProvider<HTMLTokenType> lexemeProvider, int offset)
	{
		String result = null;
		int index = lexemeProvider.getLexemeFloorIndex(offset);

		LOOP: for (int i = index; i >= 0; i--)
		{
			Lexeme<HTMLTokenType> lexeme = lexemeProvider.getLexeme(i);

			switch (lexeme.getType())
			{
				case BLOCK_TAG:
				case INLINE_TAG:
				case STRUCTURE_TAG:
				case TAG_START:
					result = lexeme.getText();
					break LOOP;
			}
		}

		return result;
	}

	/**
	 * This method looks at the partition that contains the specified offset and from that partition type determines if
	 * the offset is: 1. Within an open tag 2. Within a close tag 3. Within a text area If the partition type is
	 * unrecognized, the ERROR location will be returned.
	 * 
	 * @param lexemeProvider
	 * @param offset
	 * @return
	 */
	LocationType getCoarseLocationType(IDocument document, LexemeProvider<HTMLTokenType> lexemeProvider, int offset)
	{
		LocationType result = LocationType.ERROR;

		try
		{
			ITypedRegion partition = document.getPartition(offset);
			String type = partition.getType();

			if (locationMap.containsKey(type))
			{
				result = locationMap.get(type);

				Lexeme<HTMLTokenType> firstLexeme = lexemeProvider.getFirstLexeme();
				Lexeme<HTMLTokenType> lastLexeme;

				if (firstLexeme != null)
				{
					switch (result)
					{
						case IN_OPEN_TAG:
							lastLexeme = lexemeProvider.getLastLexeme();

							if (lastLexeme != null && lastLexeme.getEndingOffset() == offset - 1)
							{
								result = LocationType.IN_TEXT;
							}
							else
							{
								if (firstLexeme.getStartingOffset() == offset)
								{
									// What if the preceding non-whitespace char isn't '>' and it isn't in the lexemes?
									// We should report in open tag still!
									if (offset == 0)
									{
										result = LocationType.IN_TEXT;
									}
									else
									{
										ITypedRegion previousPartition = document.getPartition(offset - 1);
										String src = document.get(previousPartition.getOffset(), previousPartition.getLength()).trim();
										
										if (src.length() == 0 || src.charAt(src.length() - 1) == '>' || (src.indexOf('<') == -1 && src.indexOf('>') == -1))
										{
											result = LocationType.IN_TEXT;
										}
									}
								}
								else if ("</".equals(firstLexeme.getText())) //$NON-NLS-1$
								{
									result = LocationType.IN_CLOSE_TAG;
								}
							}
							break;

						case IN_TEXT:
							if (firstLexeme.getStartingOffset() < offset) // && offset <= lastLexeme.getEndingOffset())
							{
								lastLexeme = lexemeProvider.getLastLexeme();

								if ("<".equals(firstLexeme.getText())) //$NON-NLS-1$
								{
									switch (lastLexeme.getType())
									{
										case TAG_END:
										case TAG_SELF_CLOSE:
											if (offset <= lastLexeme.getStartingOffset())
											{
												result = LocationType.IN_OPEN_TAG;
											}
											break;
										case META:
											if (lastLexeme.getText().equalsIgnoreCase("DOCTYPE")) //$NON-NLS-1$
											{
												result = LocationType.IN_DOCTYPE;
											}
											else
											{
												result = LocationType.IN_OPEN_TAG;
											}
											break;
										default:
											result = LocationType.IN_OPEN_TAG;
											break;
									}
								}
								else if ("</".equals(firstLexeme.getText())) //$NON-NLS-1$
								{
									switch (lastLexeme.getType())
									{
										case TAG_END:
										case TAG_SELF_CLOSE:
											if (offset <= lastLexeme.getStartingOffset())
											{
												result = LocationType.IN_CLOSE_TAG;
											}
											break;

										default:
											result = LocationType.IN_CLOSE_TAG;
											break;
									}
								}
							}
							break;

						default:
							break;
					}
				}
				else
				{
					result = LocationType.IN_TEXT;
				}
			}
		}
		catch (BadLocationException e)
		{
		}

		return result;
	}

	/**
	 * This method further refines a location within an open tag. The following locations types are identified: 1. In an
	 * element name 2. In an attribute name 3. In an attribute value If the location cannot be determined, the ERROR
	 * location is returned
	 * 
	 * @param lexemeProvider
	 * @param offset
	 * @return
	 */
	LocationType getOpenTagLocationType(LexemeProvider<HTMLTokenType> lexemeProvider, int offset)
	{
		LocationType result = LocationType.ERROR;

		int index = lexemeProvider.getLexemeIndex(offset);

		if (index < 0)
		{
			int candidateIndex = lexemeProvider.getLexemeFloorIndex(offset);
			Lexeme<HTMLTokenType> lexeme = lexemeProvider.getLexeme(candidateIndex);

			if (lexeme != null && lexeme.getEndingOffset() == offset - 1)
			{
				index = candidateIndex;
			}
			else
			{
				result = LocationType.IN_ATTRIBUTE_NAME;
			}
		}

		while (index >= 0)
		{
			Lexeme<HTMLTokenType> lexeme = lexemeProvider.getLexeme(index);

			switch (lexeme.getType())
			{
				case ATTRIBUTE:
				case CLASS:
				case ID:
					result = LocationType.IN_ATTRIBUTE_NAME;
					break;

				case EQUAL:
					result = (offset <= lexeme.getStartingOffset()) ? LocationType.IN_ATTRIBUTE_NAME
							: LocationType.IN_ATTRIBUTE_VALUE;
					break;

				case TAG_START:
					result = LocationType.IN_ELEMENT_NAME;
					break;

				case TAG_END:
					if (index >= 1)
					{
						Lexeme<HTMLTokenType> previous = lexemeProvider.getLexeme(index - 1);

						if (previous.getEndingOffset() < offset - 1)
						{
							result = LocationType.IN_ATTRIBUTE_NAME;
						}
					}
					break;

				case BLOCK_TAG:
				case STRUCTURE_TAG:
				case INLINE_TAG:
				case META:
					if (index >= 1)
					{
						Lexeme<HTMLTokenType> previous = lexemeProvider.getLexeme(index - 1);

						switch (previous.getType())
						{
							case BLOCK_TAG:
							case STRUCTURE_TAG:
							case INLINE_TAG:
							case META:
							case SINGLE_QUOTED_STRING:
							case DOUBLE_QUOTED_STRING:
								this._replaceRange = this._currentLexeme = lexeme;
								result = LocationType.IN_ATTRIBUTE_NAME;
								break;

							case TAG_START:
								this._replaceRange = this._currentLexeme = lexeme;
								result = LocationType.IN_ELEMENT_NAME;
								break;

							default:
								break;
						}
					}
					else
					{
						result = LocationType.IN_ELEMENT_NAME;
					}
					break;

				case SINGLE_QUOTED_STRING:
				case DOUBLE_QUOTED_STRING:
					if (lexeme.getEndingOffset() < offset)
					{
						result = LocationType.IN_ATTRIBUTE_NAME;
						this._replaceRange = null;
					}
					else
					{
						result = LocationType.IN_ATTRIBUTE_VALUE;
					}
					break;

				default:
					break;
			}

			if (result != LocationType.ERROR)
			{
				break;
			}
			else
			{
				index--;
			}
		}

		return result;
	}

	/**
	 * setEntityRange
	 * 
	 * @param lexemeProvider
	 * @param offset
	 */
	private void setEntityRange(LexemeProvider<HTMLTokenType> lexemeProvider, int offset)
	{
		int index = lexemeProvider.getLexemeFloorIndex(offset);
		Lexeme<HTMLTokenType> endingLexeme = lexemeProvider.getLexeme(index);
		Lexeme<HTMLTokenType> startingLexeme = endingLexeme;

		// find starting location
		for (int i = index; i >= 0; i--)
		{
			startingLexeme = lexemeProvider.getLexeme(i);

			if ("&".equals(startingLexeme.getText())) //$NON-NLS-1$
			{
				break;
			}
		}

		// check ending location
		index++;

		if (index < lexemeProvider.size())
		{
			Lexeme<HTMLTokenType> candidate = lexemeProvider.getLexeme(index);

			if (";".equals(candidate.getText())) //$NON-NLS-1$
			{
				endingLexeme = candidate;
			}
		}

		if (startingLexeme != null && endingLexeme != null)
		{
			this._replaceRange = new Range(startingLexeme.getStartingOffset(), endingLexeme.getEndingOffset() - 1);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.common.CommonContentAssistProcessor#triggerAdditionalAutoActivation(char, int,
	 * org.eclipse.jface.text.IDocument, int)
	 */
	public boolean triggerAdditionalAutoActivation(char c, int keyCode, IDocument document, int offset)
	{
		LexemeProvider<HTMLTokenType> lexemeProvider = this.createLexemeProvider(document, offset);

		// first step is to determine if we're inside an open tag, close tag, text, etc.
		LocationType location = this.getCoarseLocationType(document, lexemeProvider, offset);

		switch (location)
		{
			case IN_OPEN_TAG:
				// If we are inside an open tag and typing space or tab, assume we're wanting to add attributes
				if (c == ' ' || c == '\t')
				{
					return true;
				}
				else
				{
					// If that's not the case, check if we are actually typing the attribute name
					LocationType fineLocation = this.getOpenTagLocationType(lexemeProvider, offset);
					return (fineLocation == LocationType.IN_ATTRIBUTE_NAME);
				}
			default:
				return false;
		}
	}
}
