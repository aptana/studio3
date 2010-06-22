package com.aptana.editor.common.extensions;

import java.lang.ref.WeakReference;
import java.text.MessageFormat;
import java.util.Iterator;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.text.ITextViewerExtension2;
import org.eclipse.jface.text.source.CompositeRuler;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.IVerticalRulerColumn;
import org.eclipse.jface.text.source.LineNumberRulerColumn;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.PaletteData;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Caret;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.editors.text.EditorsUI;
import org.eclipse.ui.texteditor.AbstractDecoratedTextEditorPreferenceConstants;
import org.osgi.service.prefs.BackingStoreException;

import com.aptana.editor.common.CommonEditorPlugin;
import com.aptana.editor.common.preferences.IPreferenceConstants;
import com.aptana.theme.IThemeManager;
import com.aptana.theme.ThemePlugin;

/**
 * Used to override the colors of the editor (ruler, background, caret, etc.)
 */
public class ThemeableEditorExtension {

	/**
	 * The original parent of the editor.
	 */
	private Composite fParent;
	
	/**
	 * Caret image (updated as needed with the color)
	 */
	private Image fCaretImage;
	
	/**
	 * Color of the cursor
	 */
	private RGB fCaretColor;
	
	/**
	 * Cursor which should have the color changed
	 */
	private Cursor fCursor;

	/**
	 * The line column
	 */
	private LineNumberRulerColumn fLineColumn;

	/**
	 * Listener for selection changes in the editor (to update the selection color)
	 */
	private ISelectionChangedListener fSelectionListener;
	
	/**
	 * This paints the entire line in the background color when there's only one bg color used on that line. To make
	 * things like block comments with a different bg color look more like Textmate.
	 */
	private LineBackgroundPainter fFullLineBackgroundPainter;

	/**
	 * A weak reference to the editor (so that it doesn't have to be passed on all
	 * methods of this class).
	 */
	private WeakReference<IThemeableEditor> fEditor;


	public ThemeableEditorExtension(IThemeableEditor editor) {
		this.fEditor = new WeakReference<IThemeableEditor>(editor);
	}

	
	//Public interface (clients are responsible for calling these methods as needed).
	
	public void overrideThemeColors()
	{
		overrideSelectionColor();
		overrideCursor();
		overrideCaretColor();
		overrideRulerColors();
	}
	
	
	public void initializeLineNumberRulerColumn(LineNumberRulerColumn rulerColumn) 
	{
		this.fLineColumn = rulerColumn;
	}

	public void setParent(Composite parent) 
	{
		this.fParent = parent;
	}

	public void handlePreferenceStoreChanged(PropertyChangeEvent event) 
	{
		if (event.getProperty().equals(IThemeManager.THEME_CHANGED))
		{
			IThemeableEditor editor = this.fEditor.get();
			overrideThemeColors();
			editor.getISourceViewer().invalidateTextPresentation();
		}
		if (event.getProperty().equals(AbstractDecoratedTextEditorPreferenceConstants.EDITOR_CURRENT_LINE))
		{
			if (fSelectionListener == null)
			{
				overrideSelectionColor();
			}
		}
	}

	
	public void createBackgroundPainter(ISourceViewer viewer) 
	{
		if (fFullLineBackgroundPainter == null)
		{
			if (viewer instanceof ITextViewerExtension2)
			{
				fFullLineBackgroundPainter = new LineBackgroundPainter(viewer);
				ITextViewerExtension2 extension = (ITextViewerExtension2) viewer;
				extension.addPainter(fFullLineBackgroundPainter);
			}
		}
	}


	public void dispose() 
	{
		if (fCaretImage != null)
		{
			fCaretImage.dispose();
			fCaretImage = null;
		}

		if (fCursor != null)
		{
			fCursor.dispose();
			fCursor = null;
		}

		removeLineHighlightListener();
	}

	
	//Private interface
	
	
	protected IThemeManager getThemeManager()
	{
		return ThemePlugin.getDefault().getThemeManager();
	}
	
	private void overrideSelectionColor()
	{
		IThemeableEditor editor = this.fEditor.get();
		if(editor == null)
		{
			return;
		}
		ISourceViewer sourceViewer = editor.getISourceViewer();
		if (sourceViewer == null || sourceViewer.getTextWidget() == null)
		{
			return;
		}

		// Force selection color
		sourceViewer.getTextWidget().setSelectionBackground(
				ThemePlugin.getDefault().getColorManager().getColor(
						getThemeManager().getCurrentTheme().getSelection()));
		if (!Platform.getOS().equals(Platform.OS_MACOSX))
		{
			// Linux and windows need selection fg set or we just see a block of color.
			sourceViewer.getTextWidget().setSelectionForeground(
					ThemePlugin.getDefault().getColorManager().getColor(
							getThemeManager().getCurrentTheme().getForeground()));
		}

		if (fSelectionListener != null)
			return;
		final boolean defaultHighlightCurrentLine = Platform.getPreferencesService().getBoolean(EditorsUI.PLUGIN_ID,
				AbstractDecoratedTextEditorPreferenceConstants.EDITOR_CURRENT_LINE, false, null);
		// Don't auto toggle the current line highlight if it's off (so it should remain off)
		if (!defaultHighlightCurrentLine)
			return;

		fSelectionListener = new ISelectionChangedListener()
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
		ISelectionProvider selectionProvider = editor.getSelectionProvider();
		selectionProvider.addSelectionChangedListener(fSelectionListener);
	}
	

	private void overrideCursor()
	{
		IThemeableEditor editor = this.fEditor.get();
		if(editor == null)
		{
			return;
		}
		ISourceViewer sourceViewer = editor.getISourceViewer();
		if (sourceViewer.getTextWidget() == null)
			return;

		Image cursorImage = null;
		if (getThemeManager().getCurrentTheme().hasDarkBG())
		{
			cursorImage = CommonEditorPlugin.getImage(CommonEditorPlugin.IBEAM_WHITE);
		}
		else
		{
			cursorImage = CommonEditorPlugin.getImage(CommonEditorPlugin.IBEAM_BLACK);
		}

		Display display = sourceViewer.getTextWidget().getDisplay();
		Cursor oldCursor = fCursor;

		fCursor = new Cursor(display, cursorImage.getImageData(), 7, 11);
		sourceViewer.getTextWidget().setCursor(fCursor);

		if (oldCursor != null)
		{
			oldCursor.dispose();
		}
	}

	private void overrideCaretColor()
	{
		IThemeableEditor editor = this.fEditor.get();
		if(editor == null)
		{
			return;
		}
		ISourceViewer sourceViewer = editor.getISourceViewer();
		if (sourceViewer.getTextWidget() == null)
			return;

		Caret caret = sourceViewer.getTextWidget().getCaret();
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
	
	@SuppressWarnings("unchecked")
	private void overrideRulerColors()
	{
		IThemeableEditor editor = this.fEditor.get();

		// Use normal parent gray bg
		if (fParent == null || fLineColumn == null)
			return;
		fLineColumn.setBackground(fParent.getBackground());
		// force the colors for all the ruler columns (specifically so we force the folding bg to match).
		Iterator<IVerticalRulerColumn> iter = ((CompositeRuler) editor.getIVerticalRuler()).getDecoratorIterator();
		while (iter.hasNext())
		{
			IVerticalRulerColumn column = iter.next();
			column.getControl().setBackground(fParent.getBackground());
		}
	}

	
	private void removeLineHighlightListener()
	{
		IThemeableEditor editor = this.fEditor.get();
		ISelectionProvider selectionProvider = editor.getSelectionProvider();

		if (selectionProvider != null)
		{
			selectionProvider.removeSelectionChangedListener(fSelectionListener);
		}
		fSelectionListener = null;
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


}
