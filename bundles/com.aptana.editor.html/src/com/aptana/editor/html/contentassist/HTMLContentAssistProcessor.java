/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.html.contentassist;

import java.net.URI;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileInfo;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextOperationTarget;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.text.contentassist.IContextInformationValidator;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

import com.aptana.core.IMap;
import com.aptana.core.logging.IdeLog;
import com.aptana.core.util.CollectionsUtil;
import com.aptana.core.util.StringUtil;
import com.aptana.editor.common.AbstractThemeableEditor;
import com.aptana.editor.common.CommonContentAssistProcessor;
import com.aptana.editor.common.contentassist.CommonCompletionProposal;
import com.aptana.editor.common.contentassist.ICommonCompletionProposal;
import com.aptana.editor.common.contentassist.ILexemeProvider;
import com.aptana.editor.common.contentassist.UserAgentManager;
import com.aptana.editor.css.CSSSourceConfiguration;
import com.aptana.editor.css.contentassist.CSSContentAssistProcessor;
import com.aptana.editor.html.HTMLPlugin;
import com.aptana.editor.html.HTMLSourceConfiguration;
import com.aptana.editor.html.HTMLTagScanner;
import com.aptana.editor.html.HTMLTagUtil;
import com.aptana.editor.html.IDebugScopes;
import com.aptana.editor.html.IHTMLEditorDebugScopes;
import com.aptana.editor.html.contentassist.index.IHTMLIndexConstants;
import com.aptana.editor.html.contentassist.model.AttributeElement;
import com.aptana.editor.html.contentassist.model.ElementElement;
import com.aptana.editor.html.contentassist.model.EntityElement;
import com.aptana.editor.html.contentassist.model.EventElement;
import com.aptana.editor.html.contentassist.model.ValueElement;
import com.aptana.editor.html.core.preferences.IPreferenceConstants;
import com.aptana.editor.html.parsing.HTMLParseState;
import com.aptana.editor.html.parsing.HTMLUtils;
import com.aptana.editor.html.parsing.lexer.HTMLLexemeProvider;
import com.aptana.editor.html.parsing.lexer.HTMLTokenType;
import com.aptana.editor.js.JSSourceConfiguration;
import com.aptana.editor.js.contentassist.JSContentAssistProcessor;
import com.aptana.editor.xml.TagUtil;
import com.aptana.parsing.lexer.IRange;
import com.aptana.parsing.lexer.Lexeme;
import com.aptana.parsing.lexer.Range;
import com.aptana.preview.ProjectPreviewUtil;
import com.aptana.ui.util.UIUtils;
import com.aptana.webserver.core.IServer;
import com.aptana.webserver.core.IServerManager;
import com.aptana.webserver.core.WebServerCorePlugin;

public class HTMLContentAssistProcessor extends CommonContentAssistProcessor
{
	private static final String CLASS = "class"; //$NON-NLS-1$
	private static final String ID = "id"; //$NON-NLS-1$

	private static final String DOCTYPE_PRECEDING_TEXT = "!"; //$NON-NLS-1$

	private static final class URIPathProposal extends CommonCompletionProposal
	{
		private final boolean isDirectory;

		private URIPathProposal(String replacementString, int replacementOffset, int replacementLength,
				boolean isDirectory, Image[] userAgentIcons)
		{
			super(replacementString, replacementOffset, replacementLength, replacementString.length(), null,
					replacementString, null, null);
			this.isDirectory = isDirectory;
			if (isDirectory)
			{
				setTriggerCharacters(new char[] { '/' });
			}
			setUserAgentImages(userAgentIcons);
		}

		@Override
		public synchronized Image getImage()
		{
			if (_image == null)
			{
				Image image = null;
				if (isDirectory)
				{
					image = PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJ_FOLDER);
				}
				else
				{
					// Try to get image based on filename.
					ImageDescriptor imageDesc = PlatformUI.getWorkbench().getEditorRegistry()
							.getImageDescriptor(getDisplayString());
					if (imageDesc != null)
					{
						image = imageDesc.createImage();
						final Image theImage = image;
						UIUtils.getDisplay().disposeExec(new Runnable()
						{

							public void run()
							{
								if (theImage != null && !theImage.isDisposed())
								{
									theImage.dispose();
								}
							}
						});
					}
					// fallback to generic file image
					if (image == null)
					{
						image = PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJ_FILE);
					}
				}
				_image = image;
			}
			return _image;
		}

		@Override
		public void apply(final ITextViewer viewer, char trigger, int stateMask, int offset)
		{
			super.apply(viewer, trigger, stateMask, offset);
			// HACK pop CA back up if user selected a folder, but do it on a delay so that the folder
			// proposal insertion can finish properly (like updating selection/offset)
			if (viewer instanceof ITextOperationTarget && isDirectory)
			{
				UIUtils.getDisplay().asyncExec(new Runnable()
				{
					public void run()
					{
						if (((ITextOperationTarget) viewer).canDoOperation(ISourceViewer.CONTENTASSIST_PROPOSALS))
						{
							((ITextOperationTarget) viewer).doOperation(ISourceViewer.CONTENTASSIST_PROPOSALS);
						}
					}
				});
			}
		}
	}

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
	static final Image ATTRIBUTE_ICON = HTMLPlugin.getImage("/icons/attribute.png"); //$NON-NLS-1$
	static final Image EVENT_ICON = HTMLPlugin.getImage("/icons/event.gif"); //$NON-NLS-1$
	private static final Map<String, LocationType> locationMap;
	private static final Map<String, String> DOCTYPES;

	private HTMLIndexQueryHelper _queryHelper;
	private IContextInformationValidator _validator;
	private Lexeme<HTMLTokenType> _currentLexeme;
	private IRange _replaceRange;
	private IDocument _document;

	private JSContentAssistProcessor fJSProcessor;
	private CSSContentAssistProcessor fCSSProcessor;

	/**
	 * static initializer
	 */
	static
	{
		// @formatter:off
		locationMap = CollectionsUtil.newTypedMap(
			String.class, LocationType.class,
			HTMLSourceConfiguration.DEFAULT, LocationType.IN_TEXT,
			HTMLSourceConfiguration.HTML_COMMENT, LocationType.IN_COMMENT,
			HTMLSourceConfiguration.HTML_DOCTYPE, LocationType.IN_DOCTYPE,

			HTMLSourceConfiguration.HTML_SCRIPT, LocationType.IN_OPEN_TAG,
			HTMLSourceConfiguration.HTML_STYLE, LocationType.IN_OPEN_TAG,
			HTMLSourceConfiguration.HTML_TAG, LocationType.IN_OPEN_TAG,
			HTMLSourceConfiguration.HTML_TAG_CLOSE, LocationType.IN_CLOSE_TAG,

			JSSourceConfiguration.DEFAULT, LocationType.IN_TEXT,
			CSSSourceConfiguration.DEFAULT, LocationType.IN_TEXT,
			IDocument.DEFAULT_CONTENT_TYPE, LocationType.IN_TEXT
		);
		// @formatter:on

		// @formatter:off
		DOCTYPES = CollectionsUtil.newMap(
			"HTML 5", "HTML", //$NON-NLS-1$ //$NON-NLS-2$
			"HTML 4.01 Strict", //$NON-NLS-1$
					"HTML PUBLIC \"-//W3C//DTD HTML 4.01//EN\"\n\"http://www.w3.org/TR/html4/strict.dtd\"", //$NON-NLS-1$ // $codepro.audit.disable platformSpecificLineSeparator
			"HTML 4.01 Transitional", //$NON-NLS-1$
					"HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\"\n\"http://www.w3.org/TR/html4/loose.dtd\"", //$NON-NLS-1$ // $codepro.audit.disable platformSpecificLineSeparator
			"HTML 4.01 Transitional (Quirks)", "HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\"", //$NON-NLS-1$ //$NON-NLS-2$ // $codepro.audit.disable platformSpecificLineSeparator
			"HTML 4.01 Frameset", //$NON-NLS-1$
					"HTML PUBLIC \"-//W3C//DTD HTML 4.01 Frameset//EN\"\n\"http://www.w3.org/TR/html4/frameset.dtd\"", //$NON-NLS-1$ // $codepro.audit.disable platformSpecificLineSeparator
			"XHTML 1.1", //$NON-NLS-1$
					"html PUBLIC \"-//W3C//DTD XHTML 1.1//EN\" \"http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd\"", //$NON-NLS-1$ // $codepro.audit.disable platformSpecificLineSeparator
			"XHTML 1.0 Strict", //$NON-NLS-1$
					"html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\"\n\"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\"", //$NON-NLS-1$ // $codepro.audit.disable platformSpecificLineSeparator
			"XHTML 1.0 Transitional", //$NON-NLS-1$
					"html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\"\n\"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\"", //$NON-NLS-1$ // $codepro.audit.disable platformSpecificLineSeparator
			"XHTML 1.0 Frameset", //$NON-NLS-1$
					"html PUBLIC \"-//W3C//DTD XHTML 1.0 Frameset//EN\"\n\"http://www.w3.org/TR/xhtml1/DTD/xhtml1-frameset.dtd\"", //$NON-NLS-1$ // $codepro.audit.disable platformSpecificLineSeparator
			"HTML 3.2", "HTML PUBLIC \"-//W3C//DTD HTML 3.2 Final//EN\"", //$NON-NLS-1$ //$NON-NLS-2$
			"HTML 2.0", "HTML PUBLIC \"-//IETF//DTD HTML//EN\"" //$NON-NLS-1$ //$NON-NLS-2$
		);
		// @formatter:on
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
	protected List<ICompletionProposal> addAttributeAndEventProposals(ILexemeProvider<HTMLTokenType> lexemeProvider,
			int offset)
	{
		List<ICompletionProposal> proposals = new ArrayList<ICompletionProposal>();
		String elementName = this.getElementName(lexemeProvider, offset);
		ElementElement element = this._queryHelper.getElement(elementName);

		if (element != null)
		{
			String postfix = "=\"\""; //$NON-NLS-1$
			switch (this._currentLexeme.getType())
			{
				case EQUAL:
					int index = lexemeProvider.getLexemeFloorIndex(offset);

					if (index > 0)
					{
						this._replaceRange = this._currentLexeme = lexemeProvider.getLexeme(index - 1);
						postfix = ""; //$NON-NLS-1$
					}
					break;

				case TAG_END:
				case TAG_SELF_CLOSE:
				case INLINE_TAG:
					this._replaceRange = null;
					break;

				default:
					index = lexemeProvider.getLexemeFloorIndex(offset);
					Lexeme<HTMLTokenType> nextlexeme = lexemeProvider.getLexeme(index + 1);
					if (nextlexeme != null && nextlexeme.getType() == HTMLTokenType.EQUAL)
					{
						postfix = ""; //$NON-NLS-1$
					}
					break;
			}

			int replaceLength = 0;
			if (this._replaceRange != null)
			{
				offset = this._replaceRange.getStartingOffset();
				replaceLength = this._replaceRange.getLength();
			}
			List<String> userAgentList = element.getUserAgentNames();
			String[] userAgents = userAgentList.toArray(new String[userAgentList.size()]);
			Image[] userAgentIcons = UserAgentManager.getInstance().getUserAgentImages(getProject(), userAgents);

			if (IdeLog.isTraceEnabled(HTMLPlugin.getDefault(), IDebugScopes.CONTENT_ASSIST))
			{
				IdeLog.logTrace(
						HTMLPlugin.getDefault(),
						MessageFormat
								.format("Current element: {0}, Current lexeme: {1}, Replace offset: {2}. Replace length: {3}", elementName, _currentLexeme, offset, replaceLength), //$NON-NLS-1$
						IDebugScopes.CONTENT_ASSIST);
			}

			for (AttributeElement attribute : this._queryHelper.getAttributes(element))
			{
				String name = attribute.getName();
				String replaceString = name + postfix;
				int[] positions;
				if (postfix.length() == 0)
				{
					positions = new int[] { replaceString.length() };
				}
				else
				{
					positions = new int[] { replaceString.length() - 1, replaceString.length() };
				}
				HTMLAttributeProposal p = new HTMLAttributeProposal(attribute, name + postfix, userAgentIcons, offset,
						replaceLength, positions);
				proposals.add(p);
			}

			for (EventElement event : this._queryHelper.getEvents(element))
			{
				String name = event.getName();
				String replaceString = name + postfix;
				int[] positions;
				if (postfix.length() == 0)
				{
					positions = new int[] { replaceString.length() };
				}
				else
				{
					positions = new int[] { replaceString.length() - 1, replaceString.length() };
				}
				HTMLEventProposal p = new HTMLEventProposal(event, name + postfix, userAgentIcons, offset,
						replaceLength, positions);
				proposals.add(p);
			}
		}
		else
		{
			IdeLog.logInfo(HTMLPlugin.getDefault(),
					MessageFormat.format("Current element: {0}, Current lexeme: {1}", elementName, _currentLexeme), //$NON-NLS-1$
					IDebugScopes.CONTENT_ASSIST);
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
		AttributeElement attribute = this._queryHelper.getAttribute(elementName, attributeName);
		if (attribute != null)
		{
			final Image[] userAgents = UserAgentManager.getInstance().getUserAgentImages(getProject(),
					getActiveUserAgentIds());
			final IRange range = _replaceRange != null ? _replaceRange : new Range(offset, offset - 1);

			return CollectionsUtil.map(attribute.getValues(), new IMap<ValueElement, ICompletionProposal>()
			{
				public ICompletionProposal map(ValueElement value)
				{
					return new HTMLAttributeValueProposal(value, range, userAgents);
				}
			});
		}
		return Collections.emptyList();
	}

	/**
	 * addAttributeValueProposals
	 * 
	 * @param proposals
	 * @param lexemeProvider
	 * @param offset
	 */
	private List<ICompletionProposal> addAttributeValueProposals(ILexemeProvider<HTMLTokenType> lexemeProvider,
			int offset)
	{
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
						Range range = null;
						if (ID.equals(attributeName) || CLASS.equals(attributeName))
						{
							range = HTMLUtils.getAttributeValueRange(this._currentLexeme, offset);
						}
						if (range == null)
						{
							int startingOffset = this._currentLexeme.getStartingOffset() + 1;
							int endingOffset = this._currentLexeme.getEndingOffset() - 1;

							range = new Range(startingOffset, endingOffset);
						}
						this._replaceRange = range;
					}
					break;

				case EQUAL:
					this._replaceRange = new Range(offset, offset - 1);
					break;

				// https://aptana.lighthouseapp.com/projects/35272/tickets/1640-coosing-attribute-value-in-html-ca-can-overwrite-part-of-open-tag
				case TAG_END:
				case TAG_SELF_CLOSE:
					this._replaceRange = new Range(offset, offset - 1);
					break;

				default:
					// If the current lexeme and the attribute name don't match, make the replacement null
					if (!_currentLexeme.getText().equals(attributeName))
					{
						// We need to set replacement to null if we're up in front of the next attribute name, but to
						// current lexeme otherwise
						if (offset <= _currentLexeme.getStartingOffset())
						{
							this._replaceRange = null;
						}
						else
						{
							this._replaceRange = _currentLexeme;
						}
					}
					break;
			}

			if (attributeName.equals(ID))
			{
				return addIDProposals(offset);
			}
			else if (CLASS.equals(attributeName))
			{
				return addClassProposals(offset);
			}
			else if ("src".equals(attributeName) || "href".equals(attributeName)) //$NON-NLS-1$ //$NON-NLS-2$
			{
				return addURIPathProposals(offset);
			}
			else
			{
				String elementName = this.getElementName(lexemeProvider, offset);
				return addAttributeValueProposals(offset, elementName, attributeName);
			}
		}
		return Collections.emptyList();
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
			String[] userAgents = manager.getActiveUserAgentIDs(getProject()); // classes can be used by all user agents

			for (Entry<String, String> entry : classes.entrySet())
			{
				this.addProposal(proposals, entry.getKey(), ATTRIBUTE_ICON, null, userAgents, entry.getValue(), offset);
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
		this._replaceRange = null;

		try
		{
			String valuePrefix = this._currentLexeme.getText();
			int length = offset - this._currentLexeme.getStartingOffset();
			valuePrefix = valuePrefix.substring(0, length);

			// Strip the quotes off the value prefix!
			if (valuePrefix.length() > 0 && (valuePrefix.charAt(0) == '"' || valuePrefix.charAt(0) == '\''))
			{
				valuePrefix = valuePrefix.substring(1);
				offset = this._currentLexeme.getStartingOffset() + 1;
			}

			URI editorStoreURI = getURI();
			IFileStore editorStore = null;
			if (editorStoreURI != null)
			{
				editorStore = EFS.getStore(editorStoreURI);
			}

			// Based on prefix we need to choose project root (webroot), some other place, or current file as URI
			// base.
			IFileStore baseStore = null;
			if (valuePrefix.length() > 0 && valuePrefix.charAt(0) == '/')
			{
				URI projectUri = getProjectURI();
				if (projectUri != null)
				{
					baseStore = EFS.getStore(projectUri);

					// Get the project webroot
					IServer serverConfiguration = ProjectPreviewUtil.getServerConfiguration(getProject());
					if (serverConfiguration == null)
					{
						for (IServer server : getServerManager().getServers())
						{
							if (server.resolve(editorStore) != null)
							{
								serverConfiguration = server;
								break;
							}
						}
					}
					if (serverConfiguration != null)
					{
						URI documentRoot = serverConfiguration.getDocumentRoot();
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
				}
			}
			// Try to handle absolute URIs with schemes...
			else if (valuePrefix.contains(":/")) //$NON-NLS-1$
			{
				if (valuePrefix.endsWith(":/")) //$NON-NLS-1$
				{
					// Busted URI, just return empty!
					return Collections.emptyList();
				}
				else if ("file://".equals(valuePrefix)) //$NON-NLS-1$
				{
					baseStore = EFS.getLocalFileSystem().getStore(Path.ROOT);
					offset += valuePrefix.length();
					valuePrefix = StringUtil.EMPTY;
				}
				else
				{
					try
					{
						URI parsed = null;
						int lastSlash = valuePrefix.lastIndexOf('/');
						if (lastSlash != -1 && lastSlash < valuePrefix.length() - 1)
						{
							parsed = URI.create(valuePrefix.substring(0, lastSlash));
							offset += lastSlash + 1;
							valuePrefix = valuePrefix.substring(lastSlash + 1);
						}
						else
						{
							parsed = URI.create(valuePrefix);
							offset += valuePrefix.length();
							valuePrefix = StringUtil.EMPTY;
						}
						baseStore = EFS.getStore(parsed);
					}
					catch (Exception e)
					{
						// Busted URI
						return Collections.emptyList();
					}
				}
			}
			// Assume relative to file...
			else
			{
				if (editorStore != null)
				{
					baseStore = editorStore.getParent();
				}
			}
			// For performance reasons, bail early before we start trying to list the children if we're not even able to
			// get children of this URI type
			if (baseStore == null || !efsFileSystemCanGrabChildren(baseStore.toURI().getScheme()))
			{
				return Collections.emptyList();
			}

			// Should we hit remote URIs to try and suggest children paths?
			boolean hitRemote = Platform.getPreferencesService().getBoolean(HTMLPlugin.PLUGIN_ID,
					IPreferenceConstants.HTML_REMOTE_HREF_PROPOSALS,
					IPreferenceConstants.DEFAULT_REMOTE_HREF_PROPOSALS_VALUE, null);
			if (!hitRemote && isRemoteURI(baseStore))
			{
				return Collections.emptyList();
			}

			// replace from last slash on...
			int lastSlash = valuePrefix.lastIndexOf('/');
			if (lastSlash != -1)
			{
				IFileStore possibleChild = baseStore.getChild(valuePrefix.substring(0, lastSlash));
				if (possibleChild.fetchInfo().exists())
				{
					baseStore = possibleChild;
				}
				else
				{
					// Child is invalid/non-existant, we should just punt.
					// http://jira.appcelerator.org/browse/APSTUD-3862
					return Collections.emptyList();
				}
				offset += lastSlash + 1;
				valuePrefix = valuePrefix.substring(lastSlash + 1);
			}
			this._replaceRange = new Range(offset, this._currentLexeme.getEndingOffset() - 1);

			return suggestChildrenOfFileStore(offset, valuePrefix, editorStoreURI, baseStore);
		}
		catch (CoreException e)
		{
			IdeLog.logError(HTMLPlugin.getDefault(), e);
		}

		return Collections.emptyList();
	}

	protected IServerManager getServerManager()
	{
		return WebServerCorePlugin.getDefault().getServerManager();
	}

	/**
	 * @param offset
	 * @param valuePrefix
	 * @param editorStoreURI
	 *            The URI of the current file. We use this to eliminate it from list of possible completions.
	 * @param parent
	 *            The parent we're grabbing children for.
	 * @return
	 * @throws CoreException
	 */
	protected List<ICompletionProposal> suggestChildrenOfFileStore(int offset, String valuePrefix, URI editorStoreURI,
			IFileStore parent) throws CoreException
	{
		IFileStore[] children = parent.childStores(EFS.NONE, new NullProgressMonitor());
		if (children == null || children.length == 0)
		{
			return Collections.emptyList();
		}

		List<ICompletionProposal> proposals = new ArrayList<ICompletionProposal>();
		Image[] userAgentIcons = this.getAllUserAgentIcons();
		for (IFileStore f : children)
		{
			String name = f.getName();
			// Don't include the current file in the list
			// FIXME this is a possible perf issue. We really only need to check for editor store on local URIs
			if (name.charAt(0) == '.' || f.toURI().equals(editorStoreURI))
			{
				continue;
			}
			if (valuePrefix != null && valuePrefix.length() > 0 && !name.startsWith(valuePrefix))
			{
				continue;
			}

			IFileInfo info = f.fetchInfo();
			boolean isDir = false;
			if (info.isDirectory())
			{
				isDir = true;
			}

			// build proposal
			int replaceOffset = offset;
			int replaceLength = 0;
			if (this._replaceRange != null)
			{
				replaceOffset = this._replaceRange.getStartingOffset();
				replaceLength = this._replaceRange.getLength();
			}

			CommonCompletionProposal proposal = new URIPathProposal(name, replaceOffset, replaceLength, isDir,
					userAgentIcons);
			proposals.add(proposal);
		}
		return proposals;
	}

	/**
	 * Make a best guess as to whether the IFileStore is local or remote. Should be local for LocalFile and
	 * WorkspaceFile.
	 * 
	 * @param baseStore
	 * @return
	 */
	private boolean isRemoteURI(IFileStore baseStore)
	{
		try
		{
			return baseStore.toLocalFile(EFS.NONE, new NullProgressMonitor()) == null;
		}
		catch (CoreException e)
		{
			IdeLog.logError(HTMLPlugin.getDefault(), e);
		}
		return true;
	}

	/**
	 * For performance reasons, this query method is used to exit early before offering herf/src path proposals based on
	 * a base URI. Specifically we don't try to suggest any for http/https since we can't grab the list of children.
	 * 
	 * @param scheme
	 * @return
	 */
	protected boolean efsFileSystemCanGrabChildren(String scheme)
	{
		if (scheme == null)
		{
			return false;
		}
		return !("http".equalsIgnoreCase(scheme) || "https".equalsIgnoreCase(scheme)); //$NON-NLS-1$ //$NON-NLS-2$
	}

	/**
	 * addElementProposals
	 * 
	 * @param lexemeProvider
	 * @param offset
	 */
	protected List<ICompletionProposal> addElementProposals(ILexemeProvider<HTMLTokenType> lexemeProvider, int offset)
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

					if (!nextLexeme.equals(this._currentLexeme))
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
					.getBoolean(IPreferenceConstants.HTML_AUTO_CLOSE_TAG_PAIRS);

			HTMLParseState state = null;
			String documentText = _document.get();
			for (ElementElement element : elements)
			{
				StringBuilder replacement = new StringBuilder(element.getName());
				List<Integer> positions = new ArrayList<Integer>();
				int cursorPosition = replacement.length();
				if (close)
				{
					if (state == null)
					{
						state = new HTMLParseState(documentText);
					}

					if (element.getName().charAt(0) == '!') // don't close DOCTYPE with a slash
					{
						cursorPosition += 1;
						// Don't add ">" unless we know we need it! Look at next Lexeme!
						int index = lexemeProvider.getLexemeIndex(_currentLexeme.getStartingOffset());
						Lexeme<HTMLTokenType> nextLexeme = lexemeProvider.getLexeme(index + 1);
						if (nextLexeme == null || nextLexeme.getType() == HTMLTokenType.TAG_START)
						{
							replacement.append(" >"); //$NON-NLS-1$
						}
					}
					else if (state.isEmptyTagType(element.getName()))
					{
						replacement.append(" />"); //$NON-NLS-1$
						// TODO Depending on tag, we should stick cursor inside the tag or after the end of tag. Right
						// now it's stuck at end of tag
						positions.add(cursorPosition + 3);
					}
					else
					{
						// If the tag doesn't exist in the doc, we get back that it's closed. We need to copy the
						// document and insert the tag into it
						IDocument doc = new Document(documentText);
						try
						{
							doc.replace(replaceOffset, replaceLength, element.getName() + ">"); //$NON-NLS-1$
						}
						catch (BadLocationException e)
						{
							IdeLog.logWarning(HTMLPlugin.getDefault(), MessageFormat.format(
									Messages.HTMLContentAssistProcessor_ErrorReplacingText, replaceOffset,
									element.getName() + ">"), e, IHTMLEditorDebugScopes.CONTENT_ASSIST); //$NON-NLS-1$
						}
						if (addCloseTag && !TagUtil.tagClosed(doc, element.getName()))
						{
							replacement.append("></").append(element.getName()).append('>'); //$NON-NLS-1$
							positions.add(cursorPosition + 1);
							positions.add(cursorPosition + 4 + element.getName().length());
						}
						else
						{
							replacement.append('>');
							positions.add(cursorPosition + 1);
						}
					}
				}
				positions.add(0, cursorPosition);
				HTMLTagProposal proposal = new HTMLTagProposal(replacement.toString(), replaceOffset, replaceLength,
						element, getProject(), positions.toArray(new Integer[positions.size()]));
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
	private void addEntityProposals(List<ICompletionProposal> proposals, ILexemeProvider<HTMLTokenType> lexemeProvider,
			int offset)
	{
		this.setEntityRange(offset);
		if (this._replaceRange == null)
		{
			return;
		}

		String text = null;
		int startingOffset = this._replaceRange.getStartingOffset();
		int length = this._replaceRange.getLength();
		try
		{
			text = this._document.get(startingOffset, length);
		}
		catch (BadLocationException e)
		{
			IdeLog.logWarning(
					HTMLPlugin.getDefault(),
					MessageFormat.format(Messages.HTMLContentAssistProcessor_ErrorFetchingText, startingOffset, length),
					e, IHTMLEditorDebugScopes.CONTENT_ASSIST);
		}
		if (text == null || text.charAt(0) != '&')
		{
			return;
		}

		List<EntityElement> entities = this._queryHelper.getEntities();
		if (entities != null)
		{
			String[] userAgentIds = getActiveUserAgentIds();

			for (EntityElement entity : entities)
			{
				this.addProposal(proposals, entity.getName(), ELEMENT_ICON, entity.getDescription(), userAgentIds,
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
	private void addDoctypeProposals(List<ICompletionProposal> proposals,
			ILexemeProvider<HTMLTokenType> lexemeProvider, int offset)
	{
		this._replaceRange = null;
		// Replace all the way until we hit the end of the doctype tag!
		Lexeme<HTMLTokenType> ptr = _currentLexeme;
		String[] userAgentIds = getActiveUserAgentIds();

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
					MessageFormat.format("&lt;!DOCTYPE {0}&gt;", src), userAgentIds, //$NON-NLS-1$
					IHTMLIndexConstants.CORE, offset, src.length());
			if (src.equalsIgnoreCase("HTML")) // Make HTML 5 the default //$NON-NLS-1$
			{
				proposal.setRelevance(ICommonCompletionProposal.RELEVANCE_MEDIUM);
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
			String[] userAgents = getActiveUserAgentIds();

			for (Entry<String, String> entry : ids.entrySet())
			{
				this.addProposal(proposals, entry.getKey(), ATTRIBUTE_ICON, null, userAgents, entry.getValue(), offset);
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
			ILexemeProvider<HTMLTokenType> lexemeProvider, int offset)
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
			ILexemeProvider<HTMLTokenType> lexemeProvider, int offset)
	{
		boolean addedProposal = false;
		// First see if there are any unclosed tags, suggest them first
		List<String> unclosedElements = HTMLTagUtil.getUnclosedTagNames(_document, offset);
		if (unclosedElements != null && !unclosedElements.isEmpty())
		{
			int relevance = ICommonCompletionProposal.RELEVANCE_HIGH - 1;
			for (String unclosedElement : unclosedElements)
			{
				ElementElement element = this._queryHelper.getElement(unclosedElement);
				if (element == null)
				{
					continue;
				}
				proposals.add(createCloseTagProposal(element, lexemeProvider, offset, relevance));
				addedProposal = true;
				relevance -= 1;
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
			ILexemeProvider<HTMLTokenType> lexemeProvider, int offset)
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
					state = new HTMLParseState(_document.get());
				}
				if (state.isEmptyTagType(element.getName()))
				{
					continue;
				}
				proposals.add(createCloseTagProposal(element, lexemeProvider, offset,
						ICommonCompletionProposal.RELEVANCE_HIGH));
				addedProposal = true;
			}
		}
		return addedProposal;
	}

	private CommonCompletionProposal createCloseTagProposal(ElementElement element,
			ILexemeProvider<HTMLTokenType> lexemeProvider, int offset, int relevance)
	{
		List<String> userAgentList = element.getUserAgentNames();
		String[] userAgents = userAgentList.toArray(new String[userAgentList.size()]);
		Image[] userAgentIcons = UserAgentManager.getInstance().getUserAgentImages(getProject(), userAgents);
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
			int newOffset = offset - 1;
			int length = 1;
			try
			{
				// add the close of the tag, since we're in a situation like <|<a>
				replaceString += ">"; //$NON-NLS-1$
				String previous = _document.get(newOffset, length);
				// situation like </|<a>
				if ("/".equals(previous)) { //$NON-NLS-1$
					replaceOffset -= 1;
					replaceLength += 1;
				}
			}
			catch (BadLocationException e)
			{
				IdeLog.logWarning(HTMLPlugin.getDefault(),
						MessageFormat.format(Messages.HTMLContentAssistProcessor_ErrorFetchingText, newOffset, length),
						e, IHTMLEditorDebugScopes.CONTENT_ASSIST);
			}
		}

		int cursorPosition = replaceString.length();

		CommonCompletionProposal proposal = new CommonCompletionProposal(replaceString, replaceOffset, replaceLength,
				cursorPosition, ELEMENT_ICON, "/" + element.getName(), null, element.getDescription()); //$NON-NLS-1$

		proposal.setFileLocation(IHTMLIndexConstants.CORE);
		proposal.setUserAgentImages(userAgentIcons);
		proposal.setRelevance(relevance);
		return proposal;
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
			String[] userAgentIds, int offset)
	{
		this.addProposal(proposals, name, image, description, userAgentIds, IHTMLIndexConstants.CORE, offset);
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
			String[] userAgentIds, String fileLocation, int offset)
	{
		if (isActiveByUserAgent(userAgentIds))
		{
			CommonCompletionProposal proposal = createProposal(name, image, description, userAgentIds, fileLocation,
					offset);
			// add it to the list
			proposals.add(proposal);
		}
	}

	/**
	 * createProposal
	 * 
	 * @param name
	 * @param image
	 * @param description
	 * @param userAgents
	 * @param fileLocation
	 * @param offset
	 * @return
	 */
	private CommonCompletionProposal createProposal(String name, Image image, String description,
			String[] userAgentIds, String fileLocation, int offset)
	{
		return createProposal(name, name, image, description, userAgentIds, fileLocation, offset, name.length());
	}

	/**
	 * createProposal
	 * 
	 * @param displayName
	 * @param name
	 * @param image
	 * @param description
	 * @param userAgents
	 * @param fileLocation
	 * @param offset
	 * @param length
	 * @return
	 */
	protected CommonCompletionProposal createProposal(String displayName, String name, Image image, String description,
			String[] userAgentIds, String fileLocation, int offset, int length)
	{
		IContextInformation contextInfo = null;

		// TEMP:
		int replaceLength = 0;

		if (this._replaceRange != null)
		{
			offset = this._replaceRange.getStartingOffset();
			replaceLength = this._replaceRange.getLength();
		}

		Image[] userAgents = UserAgentManager.getInstance().getUserAgentImages(getProject(), userAgentIds);

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
		this._document = viewer.getDocument();

		ILexemeProvider<HTMLTokenType> lexemeProvider = this.createLexemeProvider(_document, (offset > 0) ? offset - 1
				: offset);

		// store a reference to the lexeme at the current position
		Lexeme<HTMLTokenType> tempLexeme = lexemeProvider.getLexemeFromOffset(offset);
		if (tempLexeme != null)
		{
			this._replaceRange = this._currentLexeme = tempLexeme;
		}
		else
		{
			this._replaceRange = null;
			this._currentLexeme = lexemeProvider.getFloorLexeme(offset);
		}

		// first step is to determine if we're inside an open tag, close tag, text, etc.
		LocationType location = this.getCoarseLocationType(_document, lexemeProvider, offset);
		LocationType fineLocation = null;

		List<ICompletionProposal> result = new ArrayList<ICompletionProposal>();

		switch (location)
		{
			case IN_OPEN_TAG:
				fineLocation = this.getOpenTagLocationType(lexemeProvider, offset);

				if (fineLocation == LocationType.IN_ELEMENT_NAME)
				{
					this.addUnclosedTagProposals(fineLocation, result, lexemeProvider, offset);
				}
				// NOTE: The following is an ugly hack to get CA for JS and CSS inside of certain attributes. Ideally,
				// at some point in the future we will get JS and CSS partitions in these cases so we won't have to
				// rely on this code.
				else if (fineLocation == LocationType.IN_ATTRIBUTE_VALUE)
				{
					String elementName = this.getElementName(lexemeProvider, offset);
					String attributeName = this.getAttributeName(lexemeProvider, offset);
					IRange activeRange = this.getAttributeValueRange(lexemeProvider, offset);

					if (HTMLUtils.isCSSAttribute(attributeName))
					{
						if (fCSSProcessor == null)
						{
							fCSSProcessor = new CSSContentAssistProcessor(this.editor, activeRange);
						}
						else
						{
							fCSSProcessor.setActiveRange(activeRange);
						}
						return fCSSProcessor.computeCompletionProposals(viewer, offset, activationChar, autoActivated);
					}
					else if (HTMLUtils.isJSAttribute(elementName, attributeName))
					{
						if (fJSProcessor == null)
						{
							fJSProcessor = new JSContentAssistProcessor(this.editor, activeRange);
						}
						else
						{
							fJSProcessor.setActiveRange(activeRange);
						}
						return fJSProcessor.computeCompletionProposals(viewer, offset, activationChar, autoActivated);
					}
				}

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

		ICompletionProposal[] proposals = result.toArray(new ICompletionProposal[result.size()]);

		if (IdeLog.isTraceEnabled(HTMLPlugin.getDefault(), IDebugScopes.CONTENT_ASSIST))
		{
			IdeLog.logTrace(HTMLPlugin.getDefault(), MessageFormat.format("Generated {0} proposals", proposals.length), //$NON-NLS-1$
					IDebugScopes.CONTENT_ASSIST);
		}

		// select the current proposal based on the current lexeme
		if (this._replaceRange != null)
		{
			int startingOffset = this._replaceRange.getStartingOffset();
			int length = this._replaceRange.getLength();
			try
			{
				String text = _document.get(this._replaceRange.getStartingOffset(), this._replaceRange.getLength());

				if (location == LocationType.IN_CLOSE_TAG)
				{
					text = "/" + text; // proposals have "/" at the front //$NON-NLS-1$
				}

				setSelectedProposal(text, proposals);
			}
			catch (BadLocationException e)
			{
				IdeLog.logWarning(HTMLPlugin.getDefault(), MessageFormat.format(
						Messages.HTMLContentAssistProcessor_ErrorFetchingText, startingOffset, length), e,
						IHTMLEditorDebugScopes.CONTENT_ASSIST);
			}
		}

		// return results
		return proposals;
	}

	/**
	 * setSelectedProposal
	 * 
	 * @param prefix
	 * @param proposals
	 */
	protected void setSelectedProposal(String prefix, ICompletionProposal[] proposals)
	{
		if (prefix == null || prefix.equals(StringUtil.EMPTY) || proposals == null)
		{
			return;
		}

		for (ICompletionProposal proposal : proposals)
		{
			String displayString = proposal.getDisplayString();
			int comparison = displayString.compareToIgnoreCase(prefix);

			if (comparison >= 0)
			{
				if (displayString.toLowerCase().startsWith(prefix.toLowerCase()))
				{
					if (displayString.startsWith(prefix))
					{
						((ICommonCompletionProposal) proposal).setRelevance(ICommonCompletionProposal.RELEVANCE_HIGH);
					}
					else
					{
						((ICommonCompletionProposal) proposal).setRelevance(ICommonCompletionProposal.RELEVANCE_MEDIUM);
					}
				}
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.common.CommonContentAssistProcessor#dispose()
	 */
	@Override
	public void dispose()
	{
		super.dispose();
		if (fCSSProcessor != null)
		{
			fCSSProcessor.dispose();
		}
		if (fJSProcessor != null)
		{
			fJSProcessor.dispose();
		}
	}

	/**
	 * createLexemeProvider
	 * 
	 * @param document
	 * @param offset
	 * @return
	 */
	HTMLLexemeProvider createLexemeProvider(IDocument document, int offset)
	{
		int documentLength = document.getLength();

		// account for last position returning an empty IDocument default partition
		int lexemeProviderOffset = (offset >= documentLength) ? documentLength - 1 : offset;

		return new HTMLLexemeProvider(document, lexemeProviderOffset, new HTMLTagScanner(false));
	}

	/**
	 * getAttributeName
	 * 
	 * @param lexemeProvider
	 * @param offset
	 * @return
	 */
	private String getAttributeName(ILexemeProvider<HTMLTokenType> lexemeProvider, int offset)
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
	private String getElementName(ILexemeProvider<HTMLTokenType> lexemeProvider, int offset)
	{
		String result = null;
		int index = lexemeProvider.getLexemeFloorIndex(offset);

		for (int i = index; i >= 0; i--)
		{
			Lexeme<HTMLTokenType> lexeme = lexemeProvider.getLexeme(i);

			if (lexeme.getType() == HTMLTokenType.TAG_START)
			{
				Lexeme<HTMLTokenType> nextLexeme = lexemeProvider.getLexeme(i + 1);

				if (nextLexeme != null)
				{
					result = nextLexeme.getText();
				}

				break;
			}
		}

		return result;
	}

	/**
	 * getAttributeValueRange
	 * 
	 * @param lexemeProvider
	 * @param offset
	 * @return
	 */
	private IRange getAttributeValueRange(ILexemeProvider<HTMLTokenType> lexemeProvider, int offset)
	{
		Lexeme<HTMLTokenType> attribute = lexemeProvider.getLexemeFromOffset(offset);
		IRange result = Range.EMPTY;

		if (attribute != null)
		{
			switch (attribute.getType())
			{
				case DOUBLE_QUOTED_STRING:
				case SINGLE_QUOTED_STRING:
					result = new Range(attribute.getStartingOffset() + 1, attribute.getEndingOffset() - 1);
					break;
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
	LocationType getCoarseLocationType(IDocument document, ILexemeProvider<HTMLTokenType> lexemeProvider, int offset)
	{
		LocationType result = LocationType.ERROR;

		try
		{
			ITypedRegion partition = document.getPartition((offset > 0) ? offset - 1 : offset);
			String type = partition.getType();

			if (locationMap.containsKey(type))
			{
				// assume partition cleanly maps to a location type
				result = locationMap.get(type);

				// If the partition isn't empty, then we'll have at least one lexeme which we can use for any partion to
				// location mappings we need to fix up
				Lexeme<HTMLTokenType> firstLexeme = lexemeProvider.getFirstLexeme();

				if (firstLexeme != null)
				{
					Lexeme<HTMLTokenType> lastLexeme = lexemeProvider.getLastLexeme();
					HTMLTokenType lastLexemeType = lastLexeme.getType();

					switch (result)
					{
						case IN_OPEN_TAG:
						case IN_CLOSE_TAG:
							if (offset <= firstLexeme.getStartingOffset())
							{
								// if we're before the open/close tag, then we're in text
								result = LocationType.IN_TEXT;
							}
							else if (lastLexeme.getEndingOffset() < offset
									&& (lastLexemeType == HTMLTokenType.TAG_END || lastLexemeType == HTMLTokenType.TAG_SELF_CLOSE))
							{
								// if we after a tag end, then we're in text
								result = LocationType.IN_TEXT;
							}
							break;

						case IN_TEXT:
							// special case to support <!DOCTYPE
							if (firstLexeme.getType() == HTMLTokenType.TAG_START
									&& lastLexemeType == HTMLTokenType.META
									&& lastLexeme.getText().equalsIgnoreCase("DOCTYPE")) //$NON-NLS-1$
							{
								result = LocationType.IN_DOCTYPE;
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
			IdeLog.logWarning(HTMLPlugin.getDefault(),
					MessageFormat.format(Messages.HTMLContentAssistProcessor_ErrorFetchingPartition, offset), e,
					IHTMLEditorDebugScopes.CONTENT_ASSIST);
		}

		if (IdeLog.isTraceEnabled(HTMLPlugin.getDefault(), IDebugScopes.CONTENT_ASSIST))
		{
			IdeLog.logTrace(HTMLPlugin.getDefault(), MessageFormat.format("Coarse location: {0}", result), //$NON-NLS-1$
					IDebugScopes.CONTENT_ASSIST);
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
	LocationType getOpenTagLocationType(ILexemeProvider<HTMLTokenType> lexemeProvider, int offset)
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
				case ATTR_CLASS:
				case ATTR_ID:
				case ATTR_STYLE:
				case ATTR_SCRIPT:
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
				case TAG_SELF_CLOSE:
					if (index >= 1)
					{
						Lexeme<HTMLTokenType> previous = lexemeProvider.getLexeme(index - 1);

						if (previous.getEndingOffset() < offset - 1)
						{
							result = LocationType.IN_ATTRIBUTE_NAME;
						}
					}
					break;

				case ATTRIBUTE:
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
					if (lexeme.getEndingOffset() < offset && lexeme.getLength() > 1)
					{
						String text = lexeme.getText();
						char lastChar = (StringUtil.isEmpty(text)) ? '\0' : text.charAt(text.length() - 1);

						if (lastChar == '"' || lastChar == '\'')
						{
							result = LocationType.IN_ATTRIBUTE_NAME;
							this._replaceRange = null;
						}
						else
						{
							// unclosed attribute value, occurs at EOF
							result = LocationType.IN_ATTRIBUTE_VALUE;
						}
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

		if (IdeLog.isTraceEnabled(HTMLPlugin.getDefault(), IDebugScopes.CONTENT_ASSIST))
		{
			IdeLog.logTrace(HTMLPlugin.getDefault(),
					MessageFormat.format("Find location: {0}", result), IDebugScopes.CONTENT_ASSIST); //$NON-NLS-1$
		}

		return result;
	}

	/**
	 * setEntityRange - determine the range of the existing entity to replace. Just a '&' is enough to be considered.
	 * 
	 * @param offset
	 */
	private void setEntityRange(int offset)
	{
		try
		{
			int start = -1;
			int end = -1;
			// find starting location, start at previous character
			for (int i = offset - 1; i >= 0; i--)
			{
				char c = this._document.getChar(i);
				if ('&' == c)
				{
					start = i;
					break;
				}
				if (Character.isWhitespace(c) || c == '>')
				{
					this._replaceRange = null;
					return;
				}
			}

			// check ending location
			int length = this._document.getLength();
			for (int i = offset; i < length; i++)
			{
				char c = this._document.getChar(i);
				if (';' == c)
				{
					end = i;
					break;
				}
				if (Character.isWhitespace(c) || c == '<')
				{
					end = offset - 1;
					break;
				}
			}

			this._replaceRange = new Range(start, end);
		}
		catch (BadLocationException e)
		{
			this._replaceRange = null;
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.common.CommonContentAssistProcessor#triggerAdditionalAutoActivation(char, int,
	 * org.eclipse.jface.text.IDocument, int)
	 */
	public boolean isValidAutoActivationLocation(char c, int keyCode, IDocument document, int offset)
	{
		ILexemeProvider<HTMLTokenType> lexemeProvider = this.createLexemeProvider(document, offset);

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
				// If that's not the case, check if we are actually typing the attribute name
				LocationType fineLocation = this.getOpenTagLocationType(lexemeProvider, offset);
				return (fineLocation == LocationType.IN_ATTRIBUTE_NAME)
						|| (fineLocation == LocationType.IN_ATTRIBUTE_VALUE);
			default:
				return false;
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.common.CommonContentAssistProcessor#isValidIdentifier(char, int)
	 */
	public boolean isValidIdentifier(char c, int keyCode)
	{
		return ('A' <= keyCode && keyCode <= 'Z') || ('a' <= keyCode && keyCode <= 'z');
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.common.CommonContentAssistProcessor#getPreferenceNodeQualifier()
	 */
	protected String getPreferenceNodeQualifier()
	{
		return HTMLPlugin.PLUGIN_ID;
	}
}
