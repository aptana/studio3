package com.aptana.editor.common;

import java.lang.reflect.Field;
import java.text.MessageFormat;
import java.util.StringTokenizer;

import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.action.StatusLineLayoutData;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.IVerticalRuler;
import org.eclipse.jface.text.source.LineNumberRulerColumn;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.PaletteData;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Caret;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.ui.editors.text.EditorsUI;
import org.eclipse.ui.internal.editors.text.EditorsPlugin;
import org.eclipse.ui.texteditor.AbstractDecoratedTextEditor;
import org.eclipse.ui.texteditor.AbstractDecoratedTextEditorPreferenceConstants;
import org.eclipse.ui.texteditor.ChainedPreferenceStore;
import org.eclipse.ui.texteditor.IStatusField;
import org.eclipse.ui.texteditor.ITextEditorActionConstants;
import org.eclipse.ui.texteditor.SourceViewerDecorationSupport;
import org.eclipse.ui.texteditor.StatusLineContributionItem;
import org.osgi.service.prefs.BackingStoreException;

import com.aptana.editor.common.actions.ShowScopesAction;
import com.aptana.editor.common.peer.CharacterPairMatcher;
import com.aptana.editor.common.peer.PeerCharacterCloser;
import com.aptana.editor.common.preferences.IPreferenceConstants;
import com.aptana.editor.common.theme.ThemeUtil;
import com.aptana.editor.findbar.api.FindBarDecoratorFactory;
import com.aptana.editor.findbar.api.IFindBarDecorated;
import com.aptana.editor.findbar.api.IFindBarDecorator;

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

	private static final char[] PAIR_MATCHING_CHARS = new char[] { '(', ')', '{', '}', '[', ']', '`', '`', '\'', '\'',
			'"', '"' };

	// Adapter factory to adapt to IFindBarDecorated
	private static IAdapterFactory factory = new IAdapterFactory()
	{
		@SuppressWarnings("unchecked")
		public Class[] getAdapterList()
		{
			return new Class[] { AbstractThemeableEditor.class, IFindBarDecorated.class };
		}

		@SuppressWarnings("unchecked")
		public Object getAdapter(Object adaptableObject, Class adapterType)
		{
			if (adaptableObject instanceof AbstractThemeableEditor)
			{
				AbstractThemeableEditor abstractThemeableEditor = (AbstractThemeableEditor) adaptableObject;
				return abstractThemeableEditor.getAdapter(IFindBarDecorated.class);
			}
			return null;
		}
	};

	static
	{
		Platform.getAdapterManager().registerAdapters(factory, AbstractThemeableEditor.class);
	}
	private Image fCaretImage;
	private RGB fCaretColor;

	private ISelectionChangedListener selectionListener;

	private LineNumberRulerColumn fLineColumn;
	private Composite parent;

	/**
	 * AbstractThemeableEditor
	 */
	public AbstractThemeableEditor()
	{
		super();
	}

	@SuppressWarnings("unchecked")
	@Override
	public Object getAdapter(Class required)
	{
		if (IFindBarDecorated.class.equals(required))
		{
			return AbstractThemeableEditor.this.getFindBarDecorated();
		}
		return super.getAdapter(required);
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
		PeerCharacterCloser.install(getSourceViewer());
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

		return viewer;
	}

	@Override
	protected void configureSourceViewerDecorationSupport(SourceViewerDecorationSupport support)
	{
		super.configureSourceViewerDecorationSupport(support);

		support.setCharacterPairMatcher(new CharacterPairMatcher(PAIR_MATCHING_CHARS));
		support.setMatchingCharacterPainterPreferenceKeys(IPreferenceConstants.ENABLE_CHARACTER_PAIR_COLORING,
				IPreferenceConstants.CHARACTER_PAIR_COLOR);
	}

	protected void overrideSelectionColor()
	{
		if (getSourceViewer().getTextWidget() == null)
			return;

		// Force selection color
		getSourceViewer().getTextWidget().setSelectionBackground(
				CommonEditorPlugin.getDefault().getColorManager().getColor(ThemeUtil.getActiveTheme().getSelection()));

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

	protected void overrideCaretColor()
	{
		if (getSourceViewer().getTextWidget() == null)
			return;

		Caret caret = getSourceViewer().getTextWidget().getCaret();
		RGB caretColor = ThemeUtil.getActiveTheme().getCaret();
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
	}

	@Override
	protected void initializeViewerColors(ISourceViewer viewer)
	{
		ThemeUtil.getActiveTheme();
		if (viewer == null || viewer.getTextWidget() == null)
			return;
		super.initializeViewerColors(viewer);
	}

	@Override
	protected void handlePreferenceStoreChanged(PropertyChangeEvent event)
	{
		super.handlePreferenceStoreChanged(event);
		if (event.getProperty().equals(ThemeUtil.THEME_CHANGED))
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

	@Override
	protected void createActions()
	{
		super.createActions();
		setAction(ShowScopesAction.COMMAND_ID, ShowScopesAction.create(this, getSourceViewer()));
		getFindBarDecorator().installActions();
	}

	private IFindBarDecorated findBarDecorated;

	private IFindBarDecorated getFindBarDecorated()
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

	private IFindBarDecorator findBarDecorator;

	/**
	 * HACK! We force the position status line to recalculate it's length and relayout properly when the string it holds
	 * changes length. Standard Eclipse hard-codes an assumed length of 14 characters. So I guess if you have line and
	 * column number length > 11 (since they add " : " between them) it'll truncate. By providing a much more verbose
	 * string we hit that length really quickly.
	 */
	private int lastPositionLength = -1;

	private IFindBarDecorator getFindBarDecorator()
	{
		if (findBarDecorator == null)
		{
			findBarDecorator = FindBarDecoratorFactory.createFindBarDecorator(this, getStatusLineManager());
		}
		return findBarDecorator;
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

	@Override
	protected void updateStatusField(String category)
	{
		super.updateStatusField(category);
		// HACK!!!! We force the width to get recalculated on the line and column #
		if (ITextEditorActionConstants.STATUS_CATEGORY_INPUT_POSITION.equals(category))
		{
			IStatusField field = getStatusField(category);
			String text = getCursorPosition();
			if (text.length() != lastPositionLength)
			{
				lastPositionLength = text.length();
				try
				{
					Field label = StatusLineContributionItem.class.getDeclaredField("fLabel"); //$NON-NLS-1$
					label.setAccessible(true);
					CLabel clabel = (CLabel) label.get(field);
					if (clabel == null)
					{
						lastPositionLength = -1;
						return;
					}
					StatusLineLayoutData data = (StatusLineLayoutData) clabel.getLayoutData();

					Control control = clabel.getParent();

					GC gc = new GC(control);
					gc.setFont(control.getFont());
					int widthHint = gc.getFontMetrics().getAverageCharWidth() * text.length();
					widthHint += 3 * 2;
					gc.dispose();
					data.widthHint = widthHint;

					if (control instanceof Composite)
					{
						((Composite) control).layout();
					}
					// control.redraw();
				}
				catch (Exception e)
				{
					CommonEditorPlugin.logError(e);
				}
			}
		}
	}
}
