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
package com.aptana.editor.common.extensions;

import java.lang.ref.WeakReference;
import java.text.MessageFormat;
import java.util.Iterator;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.text.ITextViewerExtension2;
import org.eclipse.jface.text.source.CompositeRuler;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.IVerticalRulerColumn;
import org.eclipse.jface.text.source.LineNumberRulerColumn;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.graphics.Color;
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
public class ThemeableEditorExtension
{

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
	 * This paints the entire line in the background color when there's only one bg color used on that line. To make
	 * things like block comments with a different bg color look more like Textmate.
	 */
	private LineBackgroundPainter fFullLineBackgroundPainter;

	/**
	 * A weak reference to the editor (so that it doesn't have to be passed on all methods of this class).
	 */
	private WeakReference<IThemeableEditor> fEditor;

	public ThemeableEditorExtension(IThemeableEditor editor)
	{
		this.fEditor = new WeakReference<IThemeableEditor>(editor);
	}

	// Public interface (clients are responsible for calling these methods as needed).

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
		else if (event.getProperty().equals(AbstractDecoratedTextEditorPreferenceConstants.EDITOR_CURRENT_LINE))
		{
			Object newValue = event.getNewValue();
			if (newValue instanceof Boolean)
			{
				boolean on = (Boolean) newValue;
				fFullLineBackgroundPainter.setHighlightLineEnabled(on);
			}
		}
	}

	public void createBackgroundPainter(ISourceViewer viewer)
	{
		if (fFullLineBackgroundPainter == null)
		{
			if (viewer instanceof ITextViewerExtension2)
			{
				boolean lineHighlight = Platform.getPreferencesService().getBoolean(EditorsUI.PLUGIN_ID,
						AbstractDecoratedTextEditorPreferenceConstants.EDITOR_CURRENT_LINE, true, null);
				fFullLineBackgroundPainter = new LineBackgroundPainter(viewer);
				fFullLineBackgroundPainter.setHighlightLineEnabled(lineHighlight);
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

		fLineColumn = null;
		fCaretColor = null;
		if (fEditor != null)
		{
			fEditor.clear();
			fEditor = null;
		}
		fParent = null;
		if (fFullLineBackgroundPainter != null)
		{
			fFullLineBackgroundPainter.deactivate(true);
			fFullLineBackgroundPainter.dispose();
			fFullLineBackgroundPainter = null;
		}
	}

	protected IThemeManager getThemeManager()
	{
		return ThemePlugin.getDefault().getThemeManager();
	}

	private void overrideSelectionColor()
	{
		IThemeableEditor editor = this.fEditor.get();
		if (editor == null)
		{
			return;
		}
		ISourceViewer sourceViewer = editor.getISourceViewer();
		if (sourceViewer == null || sourceViewer.getTextWidget() == null)
		{
			return;
		}

		// Force selection color
		Color existingSelectionBG = sourceViewer.getTextWidget().getSelectionBackground();
		RGB selectionRGB = getThemeManager().getCurrentTheme().getSelectionAgainstBG();
		if (!existingSelectionBG.getRGB().equals(selectionRGB))
		{
			sourceViewer.getTextWidget().setSelectionBackground(
					ThemePlugin.getDefault().getColorManager().getColor(selectionRGB));
		}

		if (!Platform.getOS().equals(Platform.OS_MACOSX))
		{
			// Linux and windows need selection fg set or we just see a block of color.
			sourceViewer.getTextWidget().setSelectionForeground(
					ThemePlugin.getDefault().getColorManager()
							.getColor(getThemeManager().getCurrentTheme().getForeground()));
		}
	}

	private void overrideCursor()
	{
		IThemeableEditor editor = this.fEditor.get();
		if (editor == null)
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
		if (editor == null)
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
				PaletteData data;
				if (getThemeManager().getCurrentTheme().hasDarkBG())
				{
					data = new PaletteData(new RGB[] { caretColor });
				}
				else
				{
					RGB inverted = new RGB(255 - caretColor.red, 255 - caretColor.green, 255 - caretColor.blue);
					data = new PaletteData(new RGB[] { inverted });
				}
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

	private void setCharacterPairColor(RGB rgb)
	{
		IEclipsePreferences prefs = new InstanceScope().getNode(CommonEditorPlugin.PLUGIN_ID);
		prefs.put(IPreferenceConstants.CHARACTER_PAIR_COLOR,
				MessageFormat.format("{0},{1},{2}", rgb.red, rgb.green, rgb.blue)); //$NON-NLS-1$
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
