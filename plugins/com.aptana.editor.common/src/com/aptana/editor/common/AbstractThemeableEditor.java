package com.aptana.editor.common;

import java.text.MessageFormat;
import java.util.StringTokenizer;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.IPaintPositionManager;
import org.eclipse.jface.text.IPainter;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.text.ITextViewerExtension;
import org.eclipse.jface.text.ITextViewerExtension2;
import org.eclipse.jface.text.ITextViewerExtension5;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.IVerticalRuler;
import org.eclipse.jface.text.source.LineNumberRulerColumn;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.jface.text.source.SourceViewerConfiguration;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.custom.LineBackgroundEvent;
import org.eclipse.swt.custom.LineBackgroundListener;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.PaletteData;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Caret;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.contexts.IContextService;
import org.eclipse.ui.editors.text.EditorsUI;
import org.eclipse.ui.internal.editors.text.EditorsPlugin;
import org.eclipse.ui.texteditor.AbstractDecoratedTextEditor;
import org.eclipse.ui.texteditor.AbstractDecoratedTextEditorPreferenceConstants;
import org.eclipse.ui.texteditor.ChainedPreferenceStore;
import org.eclipse.ui.texteditor.SourceViewerDecorationSupport;
import org.eclipse.ui.views.contentoutline.ContentOutline;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;
import org.osgi.service.prefs.BackingStoreException;

import com.aptana.editor.common.actions.FilterThroughCommandAction;
import com.aptana.editor.common.internal.peer.CharacterPairMatcher;
import com.aptana.editor.common.internal.peer.PeerCharacterCloser;
import com.aptana.editor.common.outline.CommonOutlinePage;
import com.aptana.editor.common.preferences.IPreferenceConstants;
import com.aptana.editor.common.scripting.snippets.ExpandSnippetVerifyKeyListener;
import com.aptana.editor.common.theme.IThemeManager;
import com.aptana.editor.findbar.api.FindBarDecoratorFactory;
import com.aptana.editor.findbar.api.IFindBarDecorated;
import com.aptana.editor.findbar.api.IFindBarDecorator;
import com.aptana.parsing.lexer.ILexeme;
import com.aptana.scripting.Activator;
import com.aptana.scripting.keybindings.ICommandElementsProvider;

/**
 * Provides a way to override the editor fg, bg caret, highlight and selection from what is set in global text editor
 * color prefs.
 * 
 * @author cwilliams
 * @author schitale
 */
@SuppressWarnings("restriction")
public abstract class AbstractThemeableEditor extends AbstractDecoratedTextEditor
{
	private static final int RULER_EDITOR_GAP = 5;

	private static final char[] DEFAULT_PAIR_MATCHING_CHARS = new char[] { '(', ')', '{', '}', '[', ']', '`', '`',
			'\'', '\'', '"', '"' };

	private Image fCaretImage;
	private RGB fCaretColor;

	private ISelectionChangedListener selectionListener;

	private LineNumberRulerColumn fLineColumn;
	private Composite parent;

	// FindBar
	private IFindBarDecorated findBarDecorated;
	private IFindBarDecorator findBarDecorator;

	private ICommandElementsProvider commandElementsProvider;

	/**
	 * This paints the entire line in the background color when there's only one bg color used on that line. To make
	 * things like block comments with a different bg color look more like Textmate.
	 */
	private LineBackgroundPainter fFullLineBackgroundPainter;

	private CommonOutlinePage fOutlinePage;
	private FileService fFileService;

	private boolean fCursorChangeListened;

	/**
	 * AbstractThemeableEditor
	 */
	public AbstractThemeableEditor()
	{
		super();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.texteditor.AbstractDecoratedTextEditor#createPartControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createPartControl(Composite parent)
	{
		this.parent = parent;
		Composite findBarComposite = getFindBarDecorator().createFindBarComposite(parent);
		super.createPartControl(findBarComposite);
		getFindBarDecorator().createFindBar(getSourceViewer());
		overrideThemeColors();
		PeerCharacterCloser.install(getSourceViewer(), getAutoClosePairCharacters());
		fCursorChangeListened = true;

		IContextService contextService = (IContextService) getSite().getService(IContextService.class);
		contextService.activateContext(Activator.CONTEXT_ID);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.texteditor.AbstractDecoratedTextEditor#getAdapter(java.lang.Class)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Object getAdapter(Class adapter)
	{
		// returns our custom adapter for the content outline page
		if (IContentOutlinePage.class.equals(adapter))
		{
			return getOutlinePage();
		}
		return super.getAdapter(adapter);
	}

	protected CommonOutlinePage getOutlinePage()
	{
		if (fOutlinePage == null)
		{
			fOutlinePage = new CommonOutlinePage(this, getOutlinePreferenceStore());
		}
		return fOutlinePage;
	}

	private void overrideThemeColors()
	{
		overrideSelectionColor();
		overrideCaretColor();
		overrideRulerColors();
	}

	private void overrideRulerColors()
	{
		// Use normal parent gray bg
		if (parent == null || fLineColumn == null)
			return;
		fLineColumn.setBackground(parent.getBackground());
	}

	@Override
	protected void initializeLineNumberRulerColumn(LineNumberRulerColumn rulerColumn)
	{
		super.initializeLineNumberRulerColumn(rulerColumn);
		this.fLineColumn = rulerColumn;
	}

	@Override
	protected ISourceViewer createSourceViewer(Composite parent, final IVerticalRuler ruler, int styles)
	{
		fAnnotationAccess = getAnnotationAccess();
		fOverviewRuler = createOverviewRuler(getSharedColors());

		ISourceViewer viewer = new SourceViewer(parent, ruler, getOverviewRuler(), isOverviewRulerVisible(), styles)
		{
			protected Layout createLayout()
			{
				return new RulerLayout(RULER_EDITOR_GAP);
			}
		};
		// ensure decoration support has been created and configured.
		getSourceViewerDecorationSupport(viewer);

		if (fFullLineBackgroundPainter == null)
		{
			if (viewer instanceof ITextViewerExtension2)
			{
				fFullLineBackgroundPainter = new LineBackgroundPainter(viewer);
				ITextViewerExtension2 extension = (ITextViewerExtension2) viewer;
				extension.addPainter(fFullLineBackgroundPainter);
			}
		}

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

	/**
	 * A class that colors the entire line in token bg if there's only one background color specified in styling. This
	 * extends block comment bg colors to entire line in the most common use case, rather than having the bg color
	 * revert to the editor bg on the preceding spaces and trailing newline and empty space.
	 * 
	 * @author cwilliams
	 */
	private static class LineBackgroundPainter implements IPainter, LineBackgroundListener
	{

		private ISourceViewer fViewer;
		private boolean fIsActive;

		public LineBackgroundPainter(ISourceViewer viewer)
		{
			this.fViewer = viewer;
		}

		@Override
		public void deactivate(boolean redraw)
		{
			// do nothing
		}

		/*
		 * @see IPainter#dispose()
		 */
		public void dispose()
		{
		}

		/*
		 * @see IPainter#paint(int)
		 */
		public void paint(int reason)
		{
			if (fViewer.getDocument() == null)
			{
				deactivate(false);
				return;
			}

			StyledText textWidget = fViewer.getTextWidget();
			// initialization
			if (!fIsActive)
			{
				textWidget.addLineBackgroundListener(this);
				fIsActive = true;
			}
		}

		@Override
		public void setPositionManager(IPaintPositionManager manager)
		{
			// do nothing
		}

		@Override
		public void lineGetBackground(LineBackgroundEvent event)
		{
			// FIXME What about when there's other style ranges but we begin and end on same bg color? Do we color the
			// line background anyways and force style ranges with null bg colors to specify the editor bg?
			StyledText textWidget = fViewer.getTextWidget();
			if (textWidget == null)
				return;
			String text = event.lineText;
			if (text == null || text.length() == 0)
				return;
			int offset = event.lineOffset;
			int leadingWhitespace = 0;
			while (Character.isWhitespace(text.charAt(0)))
			{
				leadingWhitespace++;
				text = text.substring(1);
				if (text.length() <= 0)
					break;
			}
			int length = text.length();
			if (length > 0)
			{
				StyleRange[] ranges = textWidget.getStyleRanges(offset + leadingWhitespace, length);

				if (ranges != null && ranges.length == 1)
				{
					event.lineBackground = ranges[0].background;
				}
			}
		}
	}

	protected void overrideSelectionColor()
	{
		if (getSourceViewer().getTextWidget() == null)
			return;

		// Force selection color
		getSourceViewer().getTextWidget().setSelectionBackground(
				CommonEditorPlugin.getDefault().getColorManager().getColor(
						getThemeManager().getCurrentTheme().getSelection()));

		if (selectionListener != null)
			return;
		final boolean defaultHighlightCurrentLine = Platform.getPreferencesService().getBoolean(EditorsUI.PLUGIN_ID,
				AbstractDecoratedTextEditorPreferenceConstants.EDITOR_CURRENT_LINE, false, null);
		// Don't auto toggle the current line highlight if it's off (so it should remain off)
		if (!defaultHighlightCurrentLine)
			return;

		selectionListener = new ISelectionChangedListener()
		{

			@Override
			public void selectionChanged(SelectionChangedEvent event)
			{
				ISelection selection = event.getSelection();
				if (selection instanceof ITextSelection)
				{
					// Auto turn off line highlight when there's a selection > 0
					ITextSelection textSelection = (ITextSelection) selection;
					if (textSelection.getLength() > 0)
					{
						final boolean defaultHighlightCurrentLine = Platform.getPreferencesService().getBoolean(
								EditorsUI.PLUGIN_ID,
								AbstractDecoratedTextEditorPreferenceConstants.EDITOR_CURRENT_LINE, false, null);
						if (!defaultHighlightCurrentLine)
							return;
					}
					IEclipsePreferences prefs = new InstanceScope().getNode(CommonEditorPlugin.PLUGIN_ID);
					prefs.putBoolean(AbstractDecoratedTextEditorPreferenceConstants.EDITOR_CURRENT_LINE, textSelection
							.getLength() == 0);
					try
					{
						prefs.flush();
					}
					catch (BackingStoreException e)
					{
						// ignore
					}
				}
			}
		};
		getSelectionProvider().addSelectionChangedListener(selectionListener);
	}

	protected IThemeManager getThemeManager()
	{
		return CommonEditorPlugin.getDefault().getThemeManager();
	}

	protected void overrideCaretColor()
	{
		if (getSourceViewer().getTextWidget() == null)
			return;

		Caret caret = getSourceViewer().getTextWidget().getCaret();
		RGB caretColor = getThemeManager().getCurrentTheme().getCaret();
		if (caretColor == null)
			return;

		// Set the character pair matching color to this
		setCharacterPairColor(caretColor);

		// This is an ugly hack. Setting a black image doesn't work for some reason, but setting no image will cause it
		// to be black.
		if (caretColor.equals(new RGB(0, 0, 0)))
		{
			caret.setImage(null);
			return;
		}

		// Shortcut for when color is same, don't do any heavy lifting
		if (this.fCaretImage != null && fCaretColor.equals(caretColor))
			return;

		PaletteData data = new PaletteData(new RGB[] { caretColor });
		int x = caret.getSize().x;
		int y = caret.getSize().y;
		// Apparently the current caret may have invalid sizings
		// that will cause errors when an attempt to
		// change the color is made. So perform the check and catch
		// errors and exceptions so caret coloring
		// doesn't affect opening the editor.
		if (x > 0 && y > 0)
		{
			try
			{
				ImageData iData = new ImageData(x, y, 1, data);
				caret.setImage(null);
				if (this.fCaretImage != null)
				{
					this.fCaretImage.dispose();
					this.fCaretImage = null;
				}
				this.fCaretImage = new Image(caret.getDisplay(), iData);
				caret.setImage(this.fCaretImage);
				fCaretColor = caretColor;
			}
			catch (Error e)
			{
			}
			catch (Exception e)
			{
			}
		}

	}

	private void setCharacterPairColor(RGB rgb)
	{
		IEclipsePreferences prefs = new InstanceScope().getNode(CommonEditorPlugin.PLUGIN_ID);
		prefs.put(IPreferenceConstants.CHARACTER_PAIR_COLOR, MessageFormat.format(
				"{0},{1},{2}", rgb.red, rgb.green, rgb.blue)); //$NON-NLS-1$
		try
		{
			prefs.flush();
		}
		catch (BackingStoreException e)
		{
			CommonEditorPlugin.logError(e);
		}
	}

	@Override
	public void dispose()
	{
		if (fCaretImage != null)
		{
			fCaretImage.dispose();
			fCaretImage = null;
		}
		removeLineHighlightListener();
		super.dispose();
	}

	private void removeLineHighlightListener()
	{
		if (getSelectionProvider() != null)
		{
			getSelectionProvider().removeSelectionChangedListener(selectionListener);
		}
		selectionListener = null;
	}

	@Override
	protected void initializeEditor()
	{
		setPreferenceStore(new ChainedPreferenceStore(new IPreferenceStore[] {
				CommonEditorPlugin.getDefault().getPreferenceStore(), EditorsPlugin.getDefault().getPreferenceStore() }));
		fFileService = new FileService();
	}

	@Override
	protected void initializeViewerColors(ISourceViewer viewer)
	{
		getThemeManager().getCurrentTheme();
		if (viewer == null || viewer.getTextWidget() == null)
			return;
		super.initializeViewerColors(viewer);
	}

	@Override
	protected void handlePreferenceStoreChanged(PropertyChangeEvent event)
	{
		super.handlePreferenceStoreChanged(event);
		if (event.getProperty().equals(IThemeManager.THEME_CHANGED))
		{
			overrideThemeColors();
			getSourceViewer().invalidateTextPresentation();
		}
		if (event.getProperty().equals(AbstractDecoratedTextEditorPreferenceConstants.EDITOR_CURRENT_LINE))
		{
			if (selectionListener == null)
			{
				overrideSelectionColor();
			}
		}
	}

	public SourceViewerConfiguration getSourceViewerConfigurationNonFinal()
	{
		return getSourceViewerConfiguration();
	}

	public ISourceViewer getSourceViewerNonFinal()
	{
		return getSourceViewer();
	}

	public FileService getFileService()
	{
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

	public void select(ILexeme element, boolean checkIfOutlineActive)
	{
		if (element != null && (!checkIfOutlineActive || isOutlinePageActive()))
		{
			// disables listening to cursor change so we don't get into the loop of setting selections between editor
			// and outline
			fCursorChangeListened = false;
			selectAndReveal(element.getStartingOffset(), element.getLength());
		}
	}

	protected void handleCursorPositionChanged()
	{
		super.handleCursorPositionChanged();
		if (fCursorChangeListened)
		{
			if (isLinkedWithEditor())
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
		ISourceViewer sourceViewer = getSourceViewer();
		if (sourceViewer instanceof ITextViewerExtension)
		{
			((ITextViewerExtension) sourceViewer).prependVerifyKeyListener(new ExpandSnippetVerifyKeyListener(this));
		}
		getFindBarDecorator().installActions();
	}

	IFindBarDecorated getFindBarDecorated()
	{
		if (findBarDecorated == null)
		{
			findBarDecorated = new IFindBarDecorated()
			{
				public IFindBarDecorator getFindBarDecorator()
				{
					return AbstractThemeableEditor.this.getFindBarDecorator();
				}
			};
		}
		return findBarDecorated;
	}

	private IFindBarDecorator getFindBarDecorator()
	{
		if (findBarDecorator == null)
		{
			findBarDecorator = FindBarDecoratorFactory.createFindBarDecorator(this, getStatusLineManager());
		}
		return findBarDecorator;
	}

	ICommandElementsProvider getCommandElementsProvider()
	{
		if (commandElementsProvider == null)
		{
			commandElementsProvider = new CommandElementsProvider(this, getSourceViewer());
		}
		return commandElementsProvider;
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
		return null;
	}

	/**
	 * @return the preference store for outline page
	 */
	protected IPreferenceStore getOutlinePreferenceStore()
	{
		return CommonEditorPlugin.getDefault().getPreferenceStore();
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
}
