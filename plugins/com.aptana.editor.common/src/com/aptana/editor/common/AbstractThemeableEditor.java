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
package com.aptana.editor.common;

import java.lang.reflect.Field;
import java.text.MessageFormat;
import java.util.StringTokenizer;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.ITextViewerExtension;
import org.eclipse.jface.text.ITextViewerExtension5;
import org.eclipse.jface.text.TextViewer;
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
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.contexts.IContextService;
import org.eclipse.ui.internal.editors.text.EditorsPlugin;
import org.eclipse.ui.texteditor.ChainedPreferenceStore;
import org.eclipse.ui.texteditor.SourceViewerDecorationSupport;
import org.eclipse.ui.views.contentoutline.ContentOutline;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;

import com.aptana.editor.common.actions.FilterThroughCommandAction;
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
import com.aptana.editor.common.scripting.snippets.ExpandSnippetVerifyKeyListener;
import com.aptana.parsing.ast.IParseNode;
import com.aptana.parsing.lexer.IRange;
import com.aptana.scripting.Activator;
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
		PeerCharacterCloser.install(getSourceViewer(), getAutoClosePairCharacters());
		fCursorChangeListened = true;

		fSelectionChangedListener = new SelectionChangedListener();
		fSelectionChangedListener.install(getSelectionProvider());
		fThemeListener = new PropertyChangeListener();
		ThemePlugin.getDefault().getPreferenceStore().addPropertyChangeListener(fThemeListener);

		IContextService contextService = (IContextService) getSite().getService(IContextService.class);
		contextService.activateContext(Activator.CONTEXT_ID);
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
		ISourceViewer viewer = new ProjectionViewer(parent, ruler, getOverviewRuler(), isOverviewRulerVisible(), styles)
		{
			protected Layout createLayout()
			{
				return new RulerLayout(RULER_EDITOR_GAP);
			}

			@Override
			protected void handleDispose()
			{
				// HACK We force the widget command to be nulled out so it can be garbage collected. Might want to report a bug with eclipse to clean this up.
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
		};

		if (viewer instanceof ITextViewerExtension)
		{
			this.fKeyListener = new ExpandSnippetVerifyKeyListener(this);
			// add listener to our viewer
			((ITextViewerExtension) viewer).prependVerifyKeyListener(this.fKeyListener);
		}

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

	/**
	 * Return an array of character pairs used in our auto-closing of pairs. Even number chars are the start, odd are
	 * the end. Defaults to using the same characters as the pair matching.
	 * 
	 * @return
	 */
	protected char[] getAutoClosePairCharacters()
	{
		return getPairMatchingCharacters();
	}

	@Override
	public void dispose()
	{
		try
		{
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
			fFileService = null;
		}
		finally
		{
			super.dispose();
		}
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
		if (element != null && (!checkIfOutlineActive || isOutlinePageActive()))
		{
			// disables listening to cursor change so we don't get into the loop of setting selections between editor
			// and outline
			fCursorChangeListened = false;
			setSelectedElement(element);
		}
	}

	protected void setSelectedElement(IRange element)
	{
		int offset = element.getStartingOffset();
		int length = element.getLength();
		setHighlightRange(offset, length, false);
		selectAndReveal(offset, length);
	}

	protected void selectionChanged()
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

	@Override
	protected void createActions()
	{
		super.createActions();
		setAction(FilterThroughCommandAction.COMMAND_ID, FilterThroughCommandAction.create(this));
		this.fThemeableEditorFindBarExtension.createFindBarActions();
	}

	ICommandElementsProvider getCommandElementsProvider()
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
		String raw = super.getCursorPosition();
		StringTokenizer tokenizer = new StringTokenizer(raw, " :"); //$NON-NLS-1$
		String line = tokenizer.nextToken();
		String column = tokenizer.nextToken();
		return MessageFormat.format(Messages.AbstractThemeableEditor_CursorPositionLabel, line, column);
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
		IParseNode astNode = getASTNodeAt(caret);
		if (astNode == null)
		{
			return null;
		}
		return fOutlinePage.getOutlineItem(astNode);
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
		IParseNode root = getFileService().getParseResult();
		if (root == null)
		{
			return null;
		}
		return root.getNodeAtOffset(offset);
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
}
