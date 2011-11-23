/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.common;

import java.io.File;
import java.text.MessageFormat;
import java.util.StringTokenizer;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.MessageDialogWithToggle;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.ITextViewerExtension5;
import org.eclipse.jface.text.source.CommonLineNumberChangeRulerColumn;
import org.eclipse.jface.text.source.IChangeRulerColumn;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.IVerticalRuler;
import org.eclipse.jface.text.source.IVerticalRulerColumn;
import org.eclipse.jface.text.source.LineNumberRulerColumn;
import org.eclipse.jface.text.source.SourceViewerConfiguration;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.IPostSelectionProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.contexts.IContextService;
import org.eclipse.ui.editors.text.TextFileDocumentProvider;
import org.eclipse.ui.ide.FileStoreEditorInput;
import org.eclipse.ui.internal.editors.text.EditorsPlugin;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.texteditor.AbstractDecoratedTextEditorPreferenceConstants;
import org.eclipse.ui.texteditor.ChainedPreferenceStore;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.ui.texteditor.ITextEditorActionConstants;
import org.eclipse.ui.texteditor.SourceViewerDecorationSupport;
import org.eclipse.ui.texteditor.TextOperationAction;
import org.eclipse.ui.views.contentoutline.ContentOutline;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;
import org.eclipse.ui.views.properties.IPropertySheetPage;
import org.osgi.service.prefs.BackingStoreException;

import com.aptana.core.logging.IdeLog;
import com.aptana.core.util.EclipseUtil;
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
import com.aptana.editor.common.preferences.IPreferenceConstants;
import com.aptana.editor.common.properties.CommonEditorPropertySheetPage;
import com.aptana.editor.common.text.reconciler.IFoldingComputer;
import com.aptana.editor.common.text.reconciler.RubyRegexpFolder;
import com.aptana.editor.common.viewer.CommonProjectionViewer;
import com.aptana.parsing.ParserPoolFactory;
import com.aptana.parsing.ast.IParseNode;
import com.aptana.parsing.ast.IParseRootNode;
import com.aptana.parsing.lexer.IRange;
import com.aptana.scripting.ScriptingActivator;
import com.aptana.scripting.keybindings.ICommandElementsProvider;
import com.aptana.theme.ThemePlugin;
import com.aptana.ui.util.UIUtils;

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

	private static final char[] DEFAULT_PAIR_MATCHING_CHARS = new char[] { '(', ')', '{', '}', '[', ']', '`', '`',
			'\'', '\'', '"', '"' };

	private ICommandElementsProvider fCommandElementsProvider;

	private CommonOutlinePage fOutlinePage;

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

	private ControlListener fWordWrapControlListener;

	private CommonOccurrencesUpdater occurrencesUpdater;

	private Job linkWithEditorJob;

	/**
	 * AbstractThemeableEditor
	 */
	protected AbstractThemeableEditor()
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
		fPeerCharacterCloser.setAutoWrapEnabled(getPreferenceStore().getBoolean(
				IPreferenceConstants.EDITOR_WRAP_SELECTION));

		fCursorChangeListened = true;

		fSelectionChangedListener = new SelectionChangedListener();
		fSelectionChangedListener.install(getSelectionProvider());
		fThemeListener = new PropertyChangeListener();
		ThemePlugin.getDefault().getPreferenceStore().addPropertyChangeListener(fThemeListener);

		IContextService contextService = (IContextService) getSite().getService(IContextService.class);
		contextService.activateContext(ScriptingActivator.SCRIPTING_CONTEXT_ID);
		contextService.activateContext(ScriptingActivator.EDITOR_CONTEXT_ID);

		if (isWordWrapEnabled())
		{
			setWordWrapEnabled(true);
		}

		installOccurrencesUpdater();
	}

	protected void installOccurrencesUpdater()
	{
		// Initialize the occurrences annotations marker
		occurrencesUpdater = new CommonOccurrencesUpdater(this);
		occurrencesUpdater.initialize(getPreferenceStore());
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.texteditor.AbstractDecoratedTextEditor#getAdapter(java.lang.Class)
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public Object getAdapter(Class adapter)
	{
		if (SourceViewerConfiguration.class == adapter)
		{
			return getSourceViewerConfiguration();
		}
		else if (IContentOutlinePage.class == adapter)
		{
			// returns our custom adapter for the content outline page
			return getOutlinePage();
		}
		else if (ISourceViewer.class == adapter || ITextViewer.class == adapter)
		{
			return getSourceViewer();
		}
		else if (IPreferenceStore.class == adapter)
		{
			return getPluginPreferenceStore();
		}

		if (this.fThemeableEditorFindBarExtension != null)
		{
			Object adaptable = this.fThemeableEditorFindBarExtension.getFindBarDecoratorAdapter(adapter);
			if (adaptable != null)
			{
				return adaptable;
			}
		}

		if (adapter == IPropertySheetPage.class)
		{
			return new CommonEditorPropertySheetPage(getSourceViewer());
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

	public ITreeContentProvider getOutlineContentProvider()
	{
		return null;
	}

	public ILabelProvider getOutlineLabelProvider()
	{
		return null;
	}

	protected CommonOutlinePage createOutlinePage()
	{
		if (getOutlineContentProvider() == null || getOutlineLabelProvider() == null)
		{
			return null;
		}
		CommonOutlinePage outline = new CommonOutlinePage(this, getOutlinePreferenceStore());
		outline.setContentProvider(getOutlineContentProvider());
		outline.setLabelProvider(getOutlineLabelProvider());
		return outline;
	}

	protected abstract IPreferenceStore getPluginPreferenceStore();

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
		CommonProjectionViewer viewer = new CommonProjectionViewer(parent, ruler, getOverviewRuler(),
				isOverviewRulerVisible(), styles)
		{
			@SuppressWarnings("rawtypes")
			@Override
			public Object getAdapter(Class adapter)
			{
				if (AbstractThemeableEditor.class == adapter || ITextEditor.class == adapter)
				{
					return AbstractThemeableEditor.this;
				}
				return super.getAdapter(adapter);
			}

		};

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
			SourceViewerConfiguration svc = getSourceViewerConfiguration();
			if (svc instanceof CommonSourceViewerConfiguration)
			{
				((CommonSourceViewerConfiguration) svc).dispose();
			}
			if (fWordWrapControlListener != null)
			{
				ISourceViewer sourceViewer = getSourceViewer();
				if (sourceViewer != null)
				{
					StyledText textWidget = sourceViewer.getTextWidget();
					if (textWidget != null && !textWidget.isDisposed())
					{
						textWidget.removeControlListener(fWordWrapControlListener);
					}
				}
				fWordWrapControlListener = null;
			}

			if (occurrencesUpdater != null)
			{
				occurrencesUpdater.dispose();
				occurrencesUpdater = null;
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
			if (foldingActionsGroup != null)
			{
				foldingActionsGroup.dispose();
				foldingActionsGroup = null;
			}

			if (fOutlinePage != null)
			{
				fOutlinePage.dispose();
				fOutlinePage = null;
			}

			fCommandElementsProvider = null;
			fPeerCharacterCloser = null;
		}
		finally
		{
			super.dispose();
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.texteditor.AbstractTextEditor#init(org.eclipse.ui.IEditorSite, org.eclipse.ui.IEditorInput)
	 */
	@Override
	public void init(IEditorSite site, IEditorInput input) throws PartInitException
	{
		super.init(site, input);
		setEditorContextMenuId(getSite().getId());
	}

	@Override
	protected void initializeEditor()
	{
		setPreferenceStore(new ChainedPreferenceStore(new IPreferenceStore[] {
				CommonEditorPlugin.getDefault().getPreferenceStore(), EditorsPlugin.getDefault().getPreferenceStore() }));
	}

	@Override
	public void doSave(IProgressMonitor progressMonitor)
	{
		if (getEditorInput() instanceof UntitledFileStorageEditorInput)
		{
			// forces to show save as dialog on untitled file
			performSaveAs(progressMonitor);
		}
		else
		{
			super.doSave(progressMonitor);
		}
	}

	@Override
	protected void performSaveAs(IProgressMonitor progressMonitor)
	{
		progressMonitor = (progressMonitor == null) ? new NullProgressMonitor() : progressMonitor;
		IEditorInput input = getEditorInput();

		if (input instanceof UntitledFileStorageEditorInput)
		{
			Shell shell = getSite().getShell();

			// checks if user wants to save on the file system or in a workspace project
			boolean saveToProject = false;
			boolean byPassDialog = Platform.getPreferencesService().getBoolean(CommonEditorPlugin.PLUGIN_ID,
					IPreferenceConstants.REMEMBER_UNTITLED_FILE_SAVE_TYPE, false, null);
			if (byPassDialog)
			{
				// grabs from preferences
				saveToProject = Platform.getPreferencesService().getBoolean(CommonEditorPlugin.PLUGIN_ID,
						IPreferenceConstants.SAVE_UNTITLED_FILE_TO_PROJECT, false, null);
			}
			else
			{
				// asks the user
				MessageDialogWithToggle dialog = new MessageDialogWithToggle(shell,
						Messages.AbstractThemeableEditor_SaveToggleDialog_Title, null,
						Messages.AbstractThemeableEditor_SaveToggleDialog_Message, MessageDialog.NONE, new String[] {
								Messages.AbstractThemeableEditor_SaveToggleDialog_LocalFilesystem,
								Messages.AbstractThemeableEditor_SaveToggleDialog_Project }, 0, null, false);
				int code = dialog.open();
				if (code == SWT.DEFAULT)
				{
					return;
				}
				saveToProject = (code != IDialogConstants.INTERNAL_ID);
				if (dialog.getToggleState())
				{
					// the decision is remembered, so saves it
					IEclipsePreferences prefs = (EclipseUtil.instanceScope()).getNode(CommonEditorPlugin.PLUGIN_ID);
					prefs.putBoolean(IPreferenceConstants.REMEMBER_UNTITLED_FILE_SAVE_TYPE, true);
					prefs.putBoolean(IPreferenceConstants.SAVE_UNTITLED_FILE_TO_PROJECT, saveToProject);
					try
					{
						prefs.flush();
					}
					catch (BackingStoreException e)
					{
						IdeLog.logError(CommonEditorPlugin.getDefault(), e);
					}
				}
			}

			if (!saveToProject)
			{
				// saves to local filesystem
				FileDialog fileDialog = new FileDialog(shell, SWT.SAVE);
				String path = fileDialog.open();
				if (path == null)
				{
					progressMonitor.setCanceled(true);
					return;
				}

				// Check whether file exists and if so, confirm overwrite
				File localFile = new File(path);
				if (localFile.exists())
				{
					if (!MessageDialog.openConfirm(shell, Messages.AbstractThemeableEditor_ConfirmOverwrite_Title,
							MessageFormat.format(Messages.AbstractThemeableEditor_ConfirmOverwrite_Message, path)))
					{
						progressMonitor.setCanceled(true);
					}
				}

				IFileStore fileStore;
				try
				{
					fileStore = EFS.getStore(localFile.toURI());
				}
				catch (CoreException e)
				{
					IdeLog.logError(CommonEditorPlugin.getDefault(), e);
					MessageDialog.openError(shell, Messages.AbstractThemeableEditor_Error_Title,
							MessageFormat.format(Messages.AbstractThemeableEditor_Error_Message, path));
					return;
				}

				IDocumentProvider provider = getDocumentProvider();
				if (provider == null)
				{
					return;
				}

				IEditorInput newInput;
				IFile file = getWorkspaceFile(fileStore);
				if (file != null)
				{
					newInput = new FileEditorInput(file);
				}
				else
				{
					newInput = new FileStoreEditorInput(fileStore);
				}

				boolean success = false;
				try
				{
					provider.aboutToChange(newInput);
					provider.saveDocument(progressMonitor, newInput, provider.getDocument(input), true);
					success = true;
				}
				catch (CoreException e)
				{
					IStatus status = e.getStatus();
					if (status == null || status.getSeverity() != IStatus.CANCEL)
					{
						MessageDialog.openError(shell, Messages.AbstractThemeableEditor_Error_Title,
								MessageFormat.format(Messages.AbstractThemeableEditor_Error_Message, path));
					}
				}
				finally
				{
					provider.changed(newInput);
					if (success)
					{
						setInput(newInput);
					}
				}

				progressMonitor.setCanceled(!success);
			}
			else
			{
				super.performSaveAs(progressMonitor);
			}
		}
		else
		{
			super.performSaveAs(progressMonitor);
		}
	}

	private IFile getWorkspaceFile(IFileStore fileStore)
	{
		IWorkspaceRoot workspaceRoot = ResourcesPlugin.getWorkspace().getRoot();
		IFile[] files = workspaceRoot.findFilesForLocationURI(fileStore.toURI());
		if (files != null && files.length > 0)
		{
			return files[0];
		}
		return null;
	}

	public String getContentType()
	{
		try
		{
			IContentType contentType = ((TextFileDocumentProvider) getDocumentProvider())
					.getContentType(getEditorInput());
			if (contentType != null)
			{
				return contentType.getId();
			}
		}
		catch (Exception e)
		{
			IdeLog.logError(CommonEditorPlugin.getDefault(), e);
		}
		return null;
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
		// Add case when the global editor settings have changed
		String property = event.getProperty();

		super.handlePreferenceStoreChanged(event);
		this.fThemeableEditorColorsExtension.handlePreferenceStoreChanged(event);
		if (property.equals(IPreferenceConstants.EDITOR_PEER_CHARACTER_CLOSE))
		{
			fPeerCharacterCloser.setAutoInsertEnabled(Boolean.parseBoolean(StringUtil.getStringValue(event
					.getNewValue())));
		}
		else if (property.equals(IPreferenceConstants.EDITOR_WRAP_SELECTION))
		{
			fPeerCharacterCloser
					.setAutoWrapEnabled(Boolean.parseBoolean(StringUtil.getStringValue(event.getNewValue())));
		}
		else if (property.equals(IPreferenceConstants.EDITOR_ENABLE_FOLDING))
		{
			SourceViewerConfiguration config = getSourceViewerConfiguration();
			if (config instanceof CommonSourceViewerConfiguration)
			{
				((CommonSourceViewerConfiguration) config).forceReconcile();
			}
		}
		else if (IPreferenceConstants.USE_GLOBAL_DEFAULTS.equals(property))
		{
			// Update the tab settings when we modify the use global defaults preference
			IPreferenceStore store = getPreferenceStore();
			if (store != null)
			{
				getSourceViewer().getTextWidget().setTabs(
						store.getInt(AbstractDecoratedTextEditorPreferenceConstants.EDITOR_TAB_WIDTH));
			}
			if (isTabsToSpacesConversionEnabled())
			{
				installTabsToSpacesConverter();
			}
			else
			{
				uninstallTabsToSpacesConverter();
			}
			return;
		}
	}

	public Object computeHighlightedOutlineNode(int caretOffset)
	{
		return getOutlineElementAt(caretOffset);
	}

	public int getCaretOffset()
	{
		ISourceViewer sourceViewer = getSourceViewer();
		if (sourceViewer == null)
		{
			return -1;
		}
		StyledText styledText = sourceViewer.getTextWidget();
		if (styledText == null)
		{
			return -1;
		}

		if (sourceViewer instanceof ITextViewerExtension5)
		{
			ITextViewerExtension5 extension = (ITextViewerExtension5) sourceViewer;
			return extension.widgetOffset2ModelOffset(styledText.getCaretOffset());
		}
		int offset = sourceViewer.getVisibleRegion().getOffset();
		return offset + styledText.getCaretOffset();
	}

	public void select(IRange element, boolean checkIfOutlineActive)
	{
		try
		{
			if (element != null && (!checkIfOutlineActive || isOutlinePageActive()))
			{
				// disables listening to cursor change so we don't get into the loop of setting selections between
				// editor and outline
				fCursorChangeListened = false;
				setSelectedElement(element);
			}
		}
		catch (Exception e)
		{
			IdeLog.logError(CommonEditorPlugin.getDefault(), e);
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
			IdeLog.logError(CommonEditorPlugin.getDefault(), e);
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
					final int caretOffset = getCaretOffset();
					// runs the computation of which node in the outline tp select in a non-UI job
					if (linkWithEditorJob != null)
					{
						linkWithEditorJob.cancel();
					}

					linkWithEditorJob = new Job("Computing Outline node to select...") //$NON-NLS-1$
					{

						@Override
						protected IStatus run(IProgressMonitor monitor)
						{
							final Object outlineNode = computeHighlightedOutlineNode(caretOffset);
							UIUtils.getDisplay().asyncExec(new Runnable()
							{

								public void run()
								{
									getOutlinePage().select(outlineNode);
								}
							});
							return Status.OK_STATUS;
						}
					};
					linkWithEditorJob.setSystem(true);
					linkWithEditorJob.schedule();
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
			IdeLog.logError(CommonEditorPlugin.getDefault(), e);
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
			IdeLog.logError(CommonEditorPlugin.getDefault(), e);
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
			IdeLog.logError(CommonEditorPlugin.getDefault(), e);
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

	protected IDocument getDocument()
	{
		return getDocumentProvider().getDocument(getEditorInput());
	}

	/**
	 * FIXME Should we hang this here?
	 * 
	 * @return
	 */
	public IParseRootNode getAST()
	{
		try
		{
			IDocument document = getDocument();
			return ParserPoolFactory.parse(getContentType(), document.get());
		}
		catch (Exception e)
		{
			IdeLog.logError(CommonEditorPlugin.getDefault(), e);
		}
		return null;
	}

	protected IParseNode getASTNodeAt(int offset)
	{
		try
		{
			IParseNode root = getAST();
			if (root == null)
			{
				return null;
			}
			return root.getNodeAtOffset(offset);
		}
		catch (Exception e)
		{
			IdeLog.logError(CommonEditorPlugin.getDefault(), e);
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
	 * Returns true if the editor's preferences are set to fold.
	 * 
	 * @return True, if folding is on; False, in case it's off.
	 */
	public boolean isFoldingEnabled()
	{
		IPreferenceStore store = getPreferenceStore();
		return store != null && store.getBoolean(IPreferenceConstants.EDITOR_ENABLE_FOLDING);
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

	public boolean getWordWrapEnabled()
	{
		return getSourceViewer().getTextWidget().getWordWrap();
	}

	public void setWordWrapEnabled(boolean enabled)
	{
		StyledText textWidget = getSourceViewer().getTextWidget();
		textWidget.setWordWrap(enabled);
		fLineNumberRulerColumn.redraw();
	}

	@Override
	protected IVerticalRulerColumn createLineNumberRulerColumn()
	{
		fLineNumberRulerColumn = new CommonLineNumberChangeRulerColumn(getSharedColors());
		((IChangeRulerColumn) fLineNumberRulerColumn).setHover(createChangeHover());
		initializeLineNumberRulerColumn(fLineNumberRulerColumn);
		return fLineNumberRulerColumn;
	}

	private boolean isWordWrapEnabled()
	{
		return Platform.getPreferencesService().getBoolean(CommonEditorPlugin.PLUGIN_ID,
				IPreferenceConstants.ENABLE_WORD_WRAP, false, null);
	}
}
