/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.common;

import java.lang.reflect.Field;
import java.text.MessageFormat;
import java.util.Map;
import java.util.StringTokenizer;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextViewerExtension;
import org.eclipse.jface.text.ITextViewerExtension5;
import org.eclipse.jface.text.TextViewer;
import org.eclipse.jface.text.formatter.FormattingContextProperties;
import org.eclipse.jface.text.formatter.IFormattingContext;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.IVerticalRuler;
import org.eclipse.jface.text.source.LineNumberRulerColumn;
import org.eclipse.jface.text.source.SourceViewerConfiguration;
import org.eclipse.jface.text.source.projection.ProjectionViewer;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.IPostSelectionProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.contexts.IContextService;
import org.eclipse.ui.internal.editors.text.EditorsPlugin;
import org.eclipse.ui.texteditor.ChainedPreferenceStore;
import org.eclipse.ui.texteditor.ITextEditorActionConstants;
import org.eclipse.ui.texteditor.SourceViewerDecorationSupport;
import org.eclipse.ui.texteditor.TextOperationAction;
import org.eclipse.ui.views.contentoutline.ContentOutline;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;

import com.aptana.core.resources.IUniformResource;
import com.aptana.core.util.StringUtil;
import com.aptana.editor.common.actions.FilterThroughCommandAction;
import com.aptana.editor.common.actions.FoldingActionsGroup;
import com.aptana.editor.common.extensions.FindBarEditorExtension;
import com.aptana.editor.common.extensions.IThemeableEditor;
import com.aptana.editor.common.extensions.ThemeableEditorExtension;
import com.aptana.editor.common.internal.AbstractFoldingEditor;
import com.aptana.editor.common.internal.peer.CharacterPairMatcher;
import com.aptana.editor.common.internal.peer.PeerCharacterCloser;
import com.aptana.editor.common.internal.scripting.CommandElementsProvider;
import com.aptana.editor.common.outline.CommonOutlinePage;
import com.aptana.editor.common.parsing.FileService;
import com.aptana.editor.common.preferences.IPreferenceConstants;
import com.aptana.editor.common.scripting.QualifiedContentType;
import com.aptana.editor.common.scripting.snippets.ExpandSnippetVerifyKeyListener;
import com.aptana.editor.common.text.reconciler.IFoldingComputer;
import com.aptana.editor.common.text.reconciler.RubyRegexpFolder;
import com.aptana.formatter.IScriptFormatterFactory;
import com.aptana.formatter.ScriptFormatterManager;
import com.aptana.formatter.preferences.PreferencesLookupDelegate;
import com.aptana.formatter.ui.ScriptFormattingContextProperties;
import com.aptana.parsing.ast.IParseNode;
import com.aptana.parsing.lexer.IRange;
import com.aptana.scripting.ScriptingActivator;
import com.aptana.scripting.keybindings.ICommandElementsProvider;
import com.aptana.theme.ThemePlugin;

/**
 * Provides a way to override the editor fg, bg caret, highlight and selection from what is set in global text editor
 * color prefs.
 * 
 * @author cwilliams
 * @author schitale
 */
@SuppressWarnings("restriction")
public abstract class AbstractThemeableEditor extends AbstractFoldingEditor implements IThemeableEditor
{

	private class SelectionChangedListener implements ISelectionChangedListener
	{

		public void install(ISelectionProvider selectionProvider)
		{
			if (selectionProvider == null)
			{
				return;
			}
			if (selectionProvider instanceof IPostSelectionProvider)
			{
				((IPostSelectionProvider) selectionProvider).addPostSelectionChangedListener(this);
			}
			else
			{
				selectionProvider.addSelectionChangedListener(this);
			}
		}

		public void uninstall(ISelectionProvider selectionProvider)
		{
			if (selectionProvider == null)
			{
				return;
			}
			if (selectionProvider instanceof IPostSelectionProvider)
			{
				((IPostSelectionProvider) selectionProvider).removePostSelectionChangedListener(this);
			}
			else
			{
				selectionProvider.removeSelectionChangedListener(this);
			}
		}

		public void selectionChanged(SelectionChangedEvent event)
		{
			AbstractThemeableEditor.this.selectionChanged();
		}
	}

	private class PropertyChangeListener implements IPropertyChangeListener
	{

		public void propertyChange(PropertyChangeEvent event)
		{
			handlePreferenceStoreChanged(event);
		}
	}

	private static final int RULER_EDITOR_GAP = 5;

	private static final char[] DEFAULT_PAIR_MATCHING_CHARS = new char[] { '(', ')', '{', '}', '[', ']', '`', '`',
			'\'', '\'', '"', '"' };

	private ICommandElementsProvider fCommandElementsProvider;

	private CommonOutlinePage fOutlinePage;
	private FileService fFileService;
	private ExpandSnippetVerifyKeyListener fKeyListener;

	private boolean fCursorChangeListened;
	private SelectionChangedListener fSelectionChangedListener;

	/**
	 * Manages what's needed to make the find bar work.
	 */
	private FindBarEditorExtension fThemeableEditorFindBarExtension;

	/**
	 * Manages what's needed to make the colors obey the current theme.
	 */
	private ThemeableEditorExtension fThemeableEditorColorsExtension;

	private IPropertyChangeListener fThemeListener;

	private PeerCharacterCloser fPeerCharacterCloser;

	private FoldingActionsGroup foldingActionsGroup;

	/**
	 * AbstractThemeableEditor
	 */
	public AbstractThemeableEditor()
	{
		super();
		fThemeableEditorFindBarExtension = new FindBarEditorExtension(this);
		fThemeableEditorColorsExtension = new ThemeableEditorExtension(this);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.common.extensions.IThemeableEditor#getISourceViewer()
	 */
	public final ISourceViewer getISourceViewer()
	{
		return super.getSourceViewer();
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.common.extensions.IThemeableEditor#getIVerticalRuler()
	 */
	public final IVerticalRuler getIVerticalRuler()
	{
		return super.getVerticalRuler();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.texteditor.AbstractDecoratedTextEditor#createPartControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createPartControl(Composite parent)
	{
		this.fThemeableEditorColorsExtension.setParent(parent);
		Composite findBarComposite = this.fThemeableEditorFindBarExtension.createFindBarComposite(parent);
		Assert.isNotNull(findBarComposite); // the find bar must be the new parent.
		super.createPartControl(findBarComposite);
		this.fThemeableEditorFindBarExtension.createFindBar(getSourceViewer());
		this.fThemeableEditorColorsExtension.overrideThemeColors();

		// TODO Let ERB editor override via subclass that does special handling of % pairing, where it only happens if
		// preceding char is '<'...
		fPeerCharacterCloser = new PeerCharacterCloser(getSourceViewer());
		fPeerCharacterCloser.install();
		fPeerCharacterCloser.setAutoInsertEnabled(getPreferenceStore().getBoolean(
				IPreferenceConstants.EDITOR_PEER_CHARACTER_CLOSE));
		fPeerCharacterCloser.setAutoInsertEnabled(getPreferenceStore().getBoolean(
				IPreferenceConstants.EDITOR_WRAP_SELECTION));

		fCursorChangeListened = true;

		fSelectionChangedListener = new SelectionChangedListener();
		fSelectionChangedListener.install(getSelectionProvider());
		fThemeListener = new PropertyChangeListener();
		ThemePlugin.getDefault().getPreferenceStore().addPropertyChangeListener(fThemeListener);

		IContextService contextService = (IContextService) getSite().getService(IContextService.class);
		contextService.activateContext(ScriptingActivator.SCRIPTING_CONTEXT_ID);
		contextService.activateContext(ScriptingActivator.EDITOR_CONTEXT_ID);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.texteditor.AbstractTextEditor#setFocus() This is to workaround the Eclipse SWT bug
	 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=303677f
	 */
	@Override
	public void setFocus()
	{
		super.setFocus();

		// The above Eclipse SWT bug only occurs on Mac OS Cocoa builds
		// "cocoa" is hardcoded because Platform.WS_COCOA was added
		// in Eclipse 3.5
		if (Platform.OS_MACOSX.equals(Platform.getOS()) && Platform.getWS().equals("cocoa")) //$NON-NLS-1$
		{
			final Shell shell = getSite().getShell();
			if (shell == null)
			{
				return;
			}
			Display display = shell.getDisplay();
			if (display == null)
			{
				return;
			}
			ISourceViewer sv = getSourceViewer();
			if (sv == null)
			{
				return;
			}
			if (display.getFocusControl() != sv.getTextWidget())
			{
				// Focus did not stick due to the bug above. This is most likely
				// because of the containing shell is not the active shell.
				if (shell != display.getActiveShell())
				{
					// Queue up a setFocus() when the containing shell activates.
					shell.addShellListener(new ShellAdapter()
					{
						@Override
						public void shellActivated(ShellEvent e)
						{
							// Cleanup
							shell.removeShellListener(this);

							// Set the focus
							AbstractThemeableEditor.this.setFocus();
						}
					});
				}
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.texteditor.AbstractDecoratedTextEditor#getAdapter(java.lang.Class)
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public Object getAdapter(Class adapter)
	{
		if (SourceViewerConfiguration.class.equals(adapter))
		{
			return getSourceViewerConfiguration();
		}
		// returns our custom adapter for the content outline page
		if (IContentOutlinePage.class.equals(adapter))
		{
			return getOutlinePage();
		}

		if (this.fThemeableEditorFindBarExtension != null)
		{
			Object adaptable = this.fThemeableEditorFindBarExtension.getFindBarDecoratorAdapter(adapter);
			if (adaptable != null)
			{
				return adaptable;
			}
		}
		return super.getAdapter(adapter);
	}

	public CommonOutlinePage getOutlinePage()
	{
		if (fOutlinePage == null)
		{
			fOutlinePage = createOutlinePage();
		}
		return fOutlinePage;
	}

	protected CommonOutlinePage createOutlinePage()
	{
		return new CommonOutlinePage(this, getOutlinePreferenceStore());
	}

	@Override
	protected void initializeLineNumberRulerColumn(LineNumberRulerColumn rulerColumn)
	{
		super.initializeLineNumberRulerColumn(rulerColumn);
		this.fThemeableEditorColorsExtension.initializeLineNumberRulerColumn(rulerColumn);
	}

	@Override
	protected ISourceViewer createSourceViewer(Composite parent, final IVerticalRuler ruler, int styles)
	{
		fAnnotationAccess = getAnnotationAccess();
		fOverviewRuler = createOverviewRuler(getSharedColors());

		// Need to make it a projection viewer now that we have folding...
		ProjectionViewer viewer = new ProjectionViewer(parent, ruler, getOverviewRuler(), isOverviewRulerVisible(), styles)
		{
			protected Layout createLayout()
			{
				return new RulerLayout(RULER_EDITOR_GAP);
			}

			@Override
			protected void handleDispose()
			{
				// HACK We force the widget command to be nulled out so it can be garbage collected. Might want to
				// report a bug with eclipse to clean this up.
				try
				{
					Field f = TextViewer.class.getDeclaredField("fWidgetCommand"); //$NON-NLS-1$
					if (f != null)
					{
						f.setAccessible(true);
						f.set(this, null);
					}
				}
				catch (Throwable t)
				{
					// ignore
				}
				finally
				{
					super.handleDispose();
				}
			}

			@SuppressWarnings("rawtypes")
			@Override
			public IFormattingContext createFormattingContext()
			{
				final IFormattingContext context = super.createFormattingContext();
				try
				{
					QualifiedContentType contentType = CommonEditorPlugin.getDefault().getDocumentScopeManager()
							.getContentType(getDocument(), 0);
					if (contentType != null && contentType.getPartCount() > 0)
					{
						String mainContentType = contentType.getParts()[0];
						// We need to make sure that in case the given content type is actually a nested language in
						// HTML, we look for the HTML formatter factory because it should be the 'Master' formatter.
						if (mainContentType.startsWith(CommonSourceViewerConfiguration.CONTENTTYPE_HTML_PREFIX))
						{
							mainContentType = CommonSourceViewerConfiguration.CONTENTTYPE_HTML_PREFIX;
						}
						final IScriptFormatterFactory factory = ScriptFormatterManager.getSelected(mainContentType);
						if (factory != null)
						{
							// The code above might change the content type that is used to
							// get the formatter, but we still need to save the original content-type so that the
							// IScriptFormatter instance will handle the any required parsing by calling the right
							// IParser.
							factory.setMainContentType(contentType.getParts()[0]);

							AbstractThemeableEditor abstractThemeableEditor = AbstractThemeableEditor.this;
							IResource file = (IResource) abstractThemeableEditor.getEditorInput().getAdapter(
									IResource.class);
							context
									.setProperty(ScriptFormattingContextProperties.CONTEXT_FORMATTER_ID, factory
											.getId());
							IProject project = (file != null) ? file.getProject() : null;
							Map preferences = factory.retrievePreferences(new PreferencesLookupDelegate(project));
							context.setProperty(FormattingContextProperties.CONTEXT_PREFERENCES, preferences);
						}
					}
				}
				catch (BadLocationException e)
				{
				}
				return context;
			}
		};

		this.fKeyListener = new ExpandSnippetVerifyKeyListener(this, viewer);
		// add listener to our viewer
		((ITextViewerExtension) viewer).prependVerifyKeyListener(this.fKeyListener);

		// ensure decoration support has been created and configured.
		getSourceViewerDecorationSupport(viewer);

		fThemeableEditorColorsExtension.createBackgroundPainter(viewer);

		return viewer;
	}

	@Override
	protected void configureSourceViewerDecorationSupport(SourceViewerDecorationSupport support)
	{
		super.configureSourceViewerDecorationSupport(support);

		support.setCharacterPairMatcher(new CharacterPairMatcher(getPairMatchingCharacters()));
		support.setMatchingCharacterPainterPreferenceKeys(IPreferenceConstants.ENABLE_CHARACTER_PAIR_COLORING,
				IPreferenceConstants.CHARACTER_PAIR_COLOR);
	}

	/**
	 * Return an array of character pairs used in our pair matching highlighter. Even number chars are the start, odd
	 * are the end.
	 * 
	 * @return
	 */
	protected char[] getPairMatchingCharacters()
	{
		return DEFAULT_PAIR_MATCHING_CHARS;
	}

	@Override
	public void dispose()
	{
		try
		{
			if (getSourceViewer() instanceof CommonSourceViewerConfiguration)
			{
				((CommonSourceViewerConfiguration) getSourceViewer()).dispose();
			}
			if (fKeyListener != null)
			{
				ISourceViewer viewer = this.getSourceViewer();

				if (viewer instanceof ITextViewerExtension)
				{
					((ITextViewerExtension) viewer).removeVerifyKeyListener(this.fKeyListener);
				}

				fKeyListener = null;
			}
			if (fSelectionChangedListener != null)
			{
				fSelectionChangedListener.uninstall(getSelectionProvider());
				fSelectionChangedListener = null;
			}
			if (fThemeListener != null)
			{
				ThemePlugin.getDefault().getPreferenceStore().removePropertyChangeListener(fThemeListener);
				fThemeListener = null;
			}

			if (fThemeableEditorColorsExtension != null)
			{
				fThemeableEditorColorsExtension.dispose();
				fThemeableEditorColorsExtension = null;
			}
			if (fThemeableEditorFindBarExtension != null)
			{
				fThemeableEditorFindBarExtension.dispose();
				fThemeableEditorFindBarExtension = null;
			}
			if (fOutlinePage != null)
			{
				fOutlinePage.dispose();
				fOutlinePage = null;
			}
			fCommandElementsProvider = null;
			if (fFileService != null)
			{
				fFileService.dispose();
				fFileService = null;
			}
			fPeerCharacterCloser = null;
		}
		finally
		{
			super.dispose();
		}
	}

	@Override
	protected void doSetInput(final IEditorInput input) throws CoreException
	{
		super.doSetInput(input);

		Object resource;
		if (input instanceof IFileEditorInput)
		{
			resource = ((IFileEditorInput) input).getFile();
		}
		else
		{
			resource = input.getAdapter(IUniformResource.class);
		}
		getFileService().setResource(resource);
	}

	@Override
	protected void initializeEditor()
	{
		setPreferenceStore(new ChainedPreferenceStore(new IPreferenceStore[] {
				CommonEditorPlugin.getDefault().getPreferenceStore(), EditorsPlugin.getDefault().getPreferenceStore() }));
	}

	protected FileService createFileService()
	{
		return new FileService(null);
	}

	@Override
	protected void initializeViewerColors(ISourceViewer viewer)
	{
		if (viewer == null || viewer.getTextWidget() == null)
			return;
		super.initializeViewerColors(viewer);
	}

	@Override
	protected void handlePreferenceStoreChanged(PropertyChangeEvent event)
	{
		super.handlePreferenceStoreChanged(event);
		this.fThemeableEditorColorsExtension.handlePreferenceStoreChanged(event);
		if (event.getProperty().equals(IPreferenceConstants.EDITOR_PEER_CHARACTER_CLOSE))
		{
			fPeerCharacterCloser.setAutoInsertEnabled(Boolean.parseBoolean(StringUtil.getStringValue(event
					.getNewValue())));
		}
		else if (event.getProperty().equals(IPreferenceConstants.EDITOR_WRAP_SELECTION))
		{
			fPeerCharacterCloser
					.setAutoWrapEnabled(Boolean.parseBoolean(StringUtil.getStringValue(event.getNewValue())));
		}
	}

	public synchronized FileService getFileService()
	{
		if (fFileService == null)
		{
			fFileService = createFileService();
		}
		return fFileService;
	}

	public Object computeHighlightedOutlineNode()
	{
		ISourceViewer sourceViewer = getSourceViewer();
		if (sourceViewer == null)
		{
			return null;
		}
		StyledText styledText = sourceViewer.getTextWidget();
		if (styledText == null)
		{
			return null;
		}

		int caret = 0;
		if (sourceViewer instanceof ITextViewerExtension5)
		{
			ITextViewerExtension5 extension = (ITextViewerExtension5) sourceViewer;
			caret = extension.widgetOffset2ModelOffset(styledText.getCaretOffset());
		}
		else
		{
			int offset = sourceViewer.getVisibleRegion().getOffset();
			caret = offset + styledText.getCaretOffset();
		}

		return getOutlineElementAt(caret);
	}

	public void select(IRange element, boolean checkIfOutlineActive)
	{
		try
		{
			if (element != null && (!checkIfOutlineActive || isOutlinePageActive()))
			{
				// disables listening to cursor change so we don't get into the loop of setting selections between
				// editor
				// and outline
				fCursorChangeListened = false;
				setSelectedElement(element);
			}
		}
		catch (Exception e)
		{
			CommonEditorPlugin.logError(e);
		}
	}

	protected void setSelectedElement(IRange element)
	{
		if (element == null)
		{
			return;
		}
		try
		{
			int offset = element.getStartingOffset();
			int length = element.getLength();
			setHighlightRange(offset, length, false);
			selectAndReveal(offset, length);
		}
		catch (Exception e)
		{
			CommonEditorPlugin.logError(e);
		}
	}

	protected void selectionChanged()
	{
		try
		{
			if (fCursorChangeListened)
			{
				if (hasOutlinePageCreated() && isLinkedWithEditor())
				{
					getOutlinePage().select(computeHighlightedOutlineNode());
				}
			}
			else
			{
				// re-enables listening to cursor change
				fCursorChangeListened = true;
			}
		}
		catch (Exception e)
		{
			CommonEditorPlugin.logError(e);
		}
	}

	@Override
	protected void createActions()
	{
		super.createActions();
		setAction(FilterThroughCommandAction.COMMAND_ID, FilterThroughCommandAction.create(this));
		this.fThemeableEditorFindBarExtension.createFindBarActions();

		// Code formatter setup
		Action action = new TextOperationAction(Messages.getBundleForConstructedKeys(),
				"Format.", this, ISourceViewer.FORMAT); //$NON-NLS-1$
		action.setActionDefinitionId(ICommonConstants.FORMATTER_ACTION_DEFINITION_ID);
		setAction(ICommonConstants.FORMATTER_ACTION_ID, action);
		markAsStateDependentAction(ICommonConstants.FORMATTER_ACTION_ID, true);
		markAsSelectionDependentAction(ICommonConstants.FORMATTER_ACTION_ID, true);

		// Folding setup
		foldingActionsGroup = new FoldingActionsGroup(this);
	}

	/*
	 * (non-Javadoc)
	 * @seeorg.eclipse.ui.texteditor.AbstractDecoratedTextEditor#rulerContextMenuAboutToShow(org.eclipse.jface.action.
	 * IMenuManager)
	 */
	@Override
	protected void rulerContextMenuAboutToShow(IMenuManager menu)
	{
		super.rulerContextMenuAboutToShow(menu);
		IMenuManager foldingMenu = new MenuManager(Messages.Folding_GroupName, "folding"); //$NON-NLS-1$
		menu.appendToGroup(ITextEditorActionConstants.GROUP_RULERS, foldingMenu);
		getFoldingActionsGroup().fillMenu(foldingMenu);
	}

	synchronized ICommandElementsProvider getCommandElementsProvider()
	{
		if (fCommandElementsProvider == null)
		{
			fCommandElementsProvider = new CommandElementsProvider(this, getSourceViewer());
		}
		return fCommandElementsProvider;
	}

	/**
	 * Returns a description of the cursor position.
	 * 
	 * @return a description of the cursor position
	 */
	protected String getCursorPosition()
	{
		String raw = null;
		try
		{
			raw = super.getCursorPosition();
			StringTokenizer tokenizer = new StringTokenizer(raw, " :"); //$NON-NLS-1$
			String line = tokenizer.nextToken();
			String column = tokenizer.nextToken();
			return MessageFormat.format(Messages.AbstractThemeableEditor_CursorPositionLabel, line, column);
		}
		catch (Exception e)
		{
			CommonEditorPlugin.logError(e);
		}
		return raw;
	}

	/**
	 * Retrieves the logical parse element closest to the caret position for the outline. Subclass should override.
	 * 
	 * @param caret
	 *            the caret position
	 * @return the closest logical parse element
	 */
	protected Object getOutlineElementAt(int caret)
	{
		try
		{
			if (fOutlinePage == null)
			{
				return null;
			}
			IParseNode astNode = getASTNodeAt(caret);
			if (astNode == null)
			{
				return null;
			}
			return fOutlinePage.getOutlineItem(astNode);
		}
		catch (Exception e)
		{
			CommonEditorPlugin.logError(e);
		}
		return null;
	}

	/**
	 * @return the preference store for outline page
	 */
	protected IPreferenceStore getOutlinePreferenceStore()
	{
		return CommonEditorPlugin.getDefault().getPreferenceStore();
	}

	protected IParseNode getASTNodeAt(int offset)
	{
		try
		{
			IParseNode root = getFileService().getParseResult();
			if (root == null)
			{
				return null;
			}
			return root.getNodeAtOffset(offset);
		}
		catch (Exception e)
		{
			CommonEditorPlugin.logError(e);
		}
		return null;
	}

	/**
	 * Returns the folding actions group for the editor.
	 * 
	 * @return The {@link FoldingActionsGroup} for this editor.
	 */
	protected FoldingActionsGroup getFoldingActionsGroup()
	{
		return foldingActionsGroup;
	}

	private boolean isLinkedWithEditor()
	{
		return getOutlinePreferenceStore().getBoolean(IPreferenceConstants.LINK_OUTLINE_WITH_EDITOR);
	}

	private boolean isOutlinePageActive()
	{
		IWorkbenchPart part = getActivePart();
		return part instanceof ContentOutline && ((ContentOutline) part).getCurrentPage() == fOutlinePage;
	}

	private IWorkbenchPart getActivePart()
	{
		IWorkbenchWindow window = getSite().getWorkbenchWindow();
		return window.getPartService().getActivePart();
	}

	/**
	 * Made public so we can set TM_SOFT_TABS for scripting
	 */
	@Override
	public boolean isTabsToSpacesConversionEnabled()
	{
		// Make public so we can grab the value
		return super.isTabsToSpacesConversionEnabled();
	}

	/**
	 * Added so we can set TM_TAB_SIZE for scripting.
	 * 
	 * @return
	 */
	public int getTabSize()
	{
		SourceViewerConfiguration config = getSourceViewerConfiguration();
		if (config != null)
		{
			return config.getTabWidth(getSourceViewer());
		}
		return 4;
	}

	@Override
	public boolean isSaveAsAllowed()
	{
		return true;
	}

	public boolean hasOutlinePageCreated()
	{
		return fOutlinePage != null;
	}

	/**
	 * Returns true if the editor's preferences are set to mark element occurrences.
	 * 
	 * @return True, if mark occurrences is on; False, in case it's off.
	 */
	public boolean isMarkingOccurrences()
	{
		IPreferenceStore store = getPreferenceStore();
		return store != null && store.getBoolean(IPreferenceConstants.EDITOR_MARK_OCCURRENCES);
	}

	/**
	 * Create the implementation of the folding computer. Default is to use regexp defined in bundle/ruble for this
	 * language. Can be overridden on a per-editor basis.
	 * 
	 * @param document
	 * @return
	 */
	public IFoldingComputer createFoldingComputer(IDocument document)
	{
		return new RubyRegexpFolder(this, document);
	}
}
