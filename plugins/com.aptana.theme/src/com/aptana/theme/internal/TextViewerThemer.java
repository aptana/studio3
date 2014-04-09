/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.theme.internal;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.PaletteData;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Caret;
import org.eclipse.swt.widgets.Display;

import com.aptana.theme.ThemePlugin;
import com.aptana.ui.util.UIUtils;

/**
 * Applies theme colors to an ITextViewer's StyledText widget.
 * 
 * @author cwilliams
 */
public class TextViewerThemer extends ControlThemer
{

	private static final RGB BLACK = new RGB(0, 0, 0);

	private Image fCaretImage;
	private RGB fCaretColor;
	private Cursor fCursor;

	public TextViewerThemer(ITextViewer viewer)
	{
		super(viewer.getTextWidget());
	}

	@Override
	protected void applyTheme()
	{
		super.applyTheme();

		if (!controlIsDisposed())
		{
			// Don't always show the scrollbars, show them only when necessary
			getTextWidget().setAlwaysShowScrollBars(false);
			applyControlColors();
			overrideSelectionColor();
			overrideCursor();
			overrideCaretColor();
		}
	}

	/**
	 * This is a TextViewer (usually editor contents), so use the text font explicitly, not the view font we set up in
	 * parent class.
	 */
	protected Font getFont()
	{
		return JFaceResources.getTextFont();
	}

	private StyledText getTextWidget()
	{
		return (StyledText) getControl();
	}

	private void overrideSelectionColor()
	{
		if (getTextWidget() == null)
		{
			return;
		}

		// Force selection color
		Color existingSelectionBG = getTextWidget().getSelectionBackground();
		RGB selectionRGB = getCurrentTheme().getSelectionAgainstBG();
		if (!existingSelectionBG.getRGB().equals(selectionRGB))
		{
			getTextWidget().setSelectionBackground(getColorManager().getColor(selectionRGB));
		}

		if (!Platform.OS_MACOSX.equals(Platform.getOS()))
		{
			// Linux and windows need selection fg set or we just see a block of color.
			getTextWidget().setSelectionForeground(getForeground());
		}
	}

	private void overrideCursor()
	{
		if (getTextWidget() == null)
		{
			return;
		}

		Image cursorImage = null;
		if (getCurrentTheme().hasDarkBG())
		{
			cursorImage = UIUtils.getImage(ThemePlugin.getDefault(), ThemePlugin.IBEAM_WHITE);
		}
		else
		{
			cursorImage = UIUtils.getImage(ThemePlugin.getDefault(), ThemePlugin.IBEAM_BLACK);
		}

		Display display = getTextWidget().getDisplay();
		Cursor oldCursor = fCursor;

		fCursor = new Cursor(display, cursorImage.getImageData(), 7, 11);
		getTextWidget().setCursor(fCursor);

		if (oldCursor != null)
		{
			oldCursor.dispose();
		}
	}

	private void overrideCaretColor()
	{
		if (getTextWidget() == null)
		{
			return;
		}

		RGB caretColor = getCurrentTheme().getCaret();
		if (caretColor == null)
		{
			return;
		}

		Caret caret = getTextWidget().getCaret();
		// This is an ugly hack. Setting a black image doesn't work for some reason, but setting no image will cause it
		// to be black.
		if (caretColor.equals(BLACK))
		{
			caret.setImage(null);
			return;
		}

		// Shortcut for when color is same, don't do any heavy lifting
		if (this.fCaretImage != null && fCaretColor.equals(caretColor))
		{
			return;
		}

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

	@Override
	public void dispose()
	{
		if (fCursor != null)
		{
			fCursor.dispose();
			fCursor = null;
		}

		if (fCaretImage != null)
		{
			fCaretImage.dispose();
			fCaretImage = null;
		}
		fCaretColor = null;

		super.dispose();
	}
}
