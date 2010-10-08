/**
 * This file Copyright (c) 2005-2010 Aptana, Inc. This program is
 * dual-licensed under both the Aptana Public License and the GNU General
 * Public license. You may elect to use one or the other of these licenses.
 * 
 * This program is distributed in the hope that it will be useful, but
 * AS-IS and WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE, TITLE, or
 * NONINFRINGEMENT. Redistribution, except as permitted by whichever of
 * the GPL or APL you select, is prohibited.
 *
 * 1. For the GPL license (GPL), you can redistribute and/or modify this
 * program under the terms of the GNU General Public License,
 * Version 3, as published by the Free Software Foundation.  You should
 * have received a copy of the GNU General Public License, Version 3 along
 * with this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 * 
 * Aptana provides a special exception to allow redistribution of this file
 * with certain other free and open source software ("FOSS") code and certain additional terms
 * pursuant to Section 7 of the GPL. You may view the exception and these
 * terms on the web at http://www.aptana.com/legal/gpl/.
 * 
 * 2. For the Aptana Public License (APL), this program and the
 * accompanying materials are made available under the terms of the APL
 * v1.0 which accompanies this distribution, and is available at
 * http://www.aptana.com/legal/apl/.
 * 
 * You may view the GPL, Aptana's exception and additional terms, and the
 * APL in the file titled license.html at the root of the corresponding
 * plugin containing this source file.
 * 
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.html.contentassist;

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

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.text.contentassist.IContextInformationValidator;
import org.eclipse.swt.graphics.Image;

import com.aptana.editor.common.AbstractThemeableEditor;
import com.aptana.editor.common.CommonContentAssistProcessor;
import com.aptana.editor.common.contentassist.CommonCompletionProposal;
import com.aptana.editor.common.contentassist.LexemeProvider;
import com.aptana.editor.common.contentassist.UserAgentManager;
import com.aptana.editor.css.CSSSourceConfiguration;
import com.aptana.editor.html.Activator;
import com.aptana.editor.html.HTMLScopeScanner;
import com.aptana.editor.html.HTMLSourceConfiguration;
import com.aptana.editor.html.OpenTagCloser;
import com.aptana.editor.html.contentassist.index.HTMLIndexConstants;
import com.aptana.editor.html.contentassist.model.AttributeElement;
import com.aptana.editor.html.contentassist.model.ElementElement;
import com.aptana.editor.html.contentassist.model.EntityElement;
import com.aptana.editor.html.contentassist.model.ValueElement;
import com.aptana.editor.html.parsing.HTMLParseState;
import com.aptana.editor.html.parsing.lexer.HTMLTokenType;
import com.aptana.editor.js.JSSourceConfiguration;
import com.aptana.parsing.lexer.IRange;
import com.aptana.parsing.lexer.Lexeme;
import com.aptana.parsing.lexer.Range;

public class HTMLContentAssistProcessor extends CommonContentAssistProcessor
{
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

	private static final Image ELEMENT_ICON = Activator.getImage("/icons/element.png"); //$NON-NLS-1$
	private static final Image ATTRIBUTE_ICON = Activator.getImage("/icons/attribute.png"); //$NON-NLS-1$
	private static final Image EVENT_ICON = Activator.getImage("/icons/event.gif"); //$NON-NLS-1$
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

			String[] userAgents = element.getUserAgentNames();
			Image[] userAgentIcons = UserAgentManager.getInstance().getUserAgentImages(userAgents);

			for (String attribute : element.getAttributes())
			{
				proposals.add(createProposal(attribute, attribute + postfix, ATTRIBUTE_ICON, null, userAgentIcons,
						HTMLIndexConstants.CORE, offset, attribute.length() + length));
			}

			for (String event : element.getEvents())
			{
				proposals.add(createProposal(event, event + postfix, EVENT_ICON, null, userAgentIcons,
						HTMLIndexConstants.CORE, offset, event.length() + length));
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

			if (this._currentLexeme.getType() == HTMLTokenType.TAG_END) // '|>
			{
				replaceLength = 1; // replace the '>'
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
					offset = _currentLexeme.getStartingOffset();
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

			HTMLParseState state = null;

			for (ElementElement element : elements)
			{
				String[] userAgents = element.getUserAgentNames();
				Image[] userAgentIcons = UserAgentManager.getInstance().getUserAgentImages(userAgents);
				String replaceString = element.getName();

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
						replaceString += " >"; //$NON-NLS-1$
						cursorPosition += 1;
					}
					else if (state.isEmptyTagType(element.getName()))
					{
						replaceString += " />"; //$NON-NLS-1$
						// TODO Depending on tag, we should stick cursor inside the tag or after the end of tag
						cursorPosition += 3;
					}
					else
					{
						// If the tag doesn't exist in the doc, we get back that it's closed. We need to copy the
						// document and insert the tag into it
						IDocument doc = new Document(_document.get());
						try
						{
							doc.replace(offset, replaceLength, element.getName() + ">"); //$NON-NLS-1$
						}
						catch (BadLocationException e)
						{
							// ignore
						}
						if (!OpenTagCloser.tagClosed(doc, element.getName()))
						{
							replaceString += "></" + element.getName() + ">"; //$NON-NLS-1$ //$NON-NLS-2$
							// TODO Depending on the tag, we should add a "tabstop" inside the open part of the tag
							cursorPosition += 1;
						}
						else
						{
							replaceString += ">"; //$NON-NLS-1$
							cursorPosition += 1;
						}
					}
				}

				CommonCompletionProposal proposal = new CommonCompletionProposal(replaceString, offset, replaceLength,
						cursorPosition, ELEMENT_ICON, element.getName(), null, element.getDescription());

				proposal.setFileLocation(HTMLIndexConstants.CORE);
				proposal.setUserAgentImages(userAgentIcons);
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
		List<EntityElement> entities = this._queryHelper.getEntities();

		if (entities != null)
		{
			this.setEntityRange(lexemeProvider, offset);
			Image[] userAgentIcons = this.getAllUserAgentIcons();

			for (EntityElement entity : entities)
			{
				this.addProposal(proposals, entity.getName(), ELEMENT_ICON, entity.getDescription(), userAgentIcons,
						offset);
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
		Image[] userAgentIcons = this.getAllUserAgentIcons();
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
	private void addOpenTagPropsals(List<ICompletionProposal> proposals, LexemeProvider<HTMLTokenType> lexemeProvider,
			int offset)
	{
		LocationType location = this.getOpenTagLocationType(lexemeProvider, offset);

		switch (location)
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
	private List<ICompletionProposal> addCloseTagProposals(LexemeProvider<HTMLTokenType> lexemeProvider, int offset)
	{
		List<ICompletionProposal> proposals = new ArrayList<ICompletionProposal>();
		HTMLParseState state = null;
		// First see if there are any unclosed tags, suggest them first
		Set<String> unclosedElements = getUnclosedTagNames(offset);
		if (unclosedElements != null && !unclosedElements.isEmpty())
		{
			for (String unclosedElement : unclosedElements)
			{

				ElementElement element = this._queryHelper.getElement(unclosedElement);

				if (state == null)
				{
					state = new HTMLParseState();
					state.setEditState(_document.get(), null, 0, 0);
				}
				if (state.isEmptyTagType(element.getName()))
				{
					continue;
				}
				proposals.add(createCloseTagProposal(element, offset));
			}
			if (!proposals.isEmpty())
			{
				return proposals;
			}
		}

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
				proposals.add(createCloseTagProposal(element, offset));
			}
		}
		return proposals;
	}

	private CommonCompletionProposal createCloseTagProposal(ElementElement element, int offset)
	{
		String[] userAgents = element.getUserAgentNames();
		Image[] userAgentIcons = UserAgentManager.getInstance().getUserAgentImages(userAgents);
		String replaceString = element.getName();

		int cursorPosition = replaceString.length();
		int replaceLength = 0;
		CommonCompletionProposal proposal = new CommonCompletionProposal(replaceString, offset, replaceLength,
				cursorPosition, ELEMENT_ICON, element.getName(), null, element.getDescription());

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
					if (lessThanIndex == -1 || lessThanIndex >= src.length() - 1)
					{
						continue;
					}
					src = src.substring(lessThanIndex + 1).trim();
					String[] parts = src.split("\\W"); //$NON-NLS-1$
					if (parts == null || parts.length == 0)
					{
						continue;
					}
					String elementName = parts[0].toLowerCase();
					if (!unclosedElements.contains(elementName) && !OpenTagCloser.tagClosed(_document, elementName))
					{
						unclosedElements.add(elementName);
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

		List<ICompletionProposal> result = new ArrayList<ICompletionProposal>();

		switch (location)
		{
			case IN_OPEN_TAG:
				this.addOpenTagPropsals(result, lexemeProvider, offset);
				break;

			case IN_CLOSE_TAG:
				result.addAll(this.addCloseTagProposals(lexemeProvider, offset));
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
		return new char[] { '<', '\'', '"', '&' };
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
										String src = document.get(previousPartition.getOffset(),
												previousPartition.getLength()).trim();
										if (src.charAt(src.length() - 1) == '>')
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
}
