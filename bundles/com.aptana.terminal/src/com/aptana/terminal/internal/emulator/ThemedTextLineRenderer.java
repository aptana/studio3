/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
// $codepro.audit.disable staticFieldNamingConvention

package com.aptana.terminal.internal.emulator;

import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.tm.internal.terminal.textcanvas.ITextCanvasModel;
import org.eclipse.tm.internal.terminal.textcanvas.TextLineRenderer;
import org.eclipse.tm.terminal.model.LineSegment;
import org.eclipse.tm.terminal.model.Style;

import com.aptana.core.util.ArrayUtil;
import com.aptana.core.util.StringUtil;
import com.aptana.terminal.internal.hyperlink.HyperlinkManager;
import com.aptana.theme.ColorManager;
import com.aptana.theme.IThemeManager;
import com.aptana.theme.Theme;
import com.aptana.theme.ThemePlugin;

/**
 * @author Max Stepanov
 */
/* package */class ThemedTextLineRenderer extends TextLineRenderer
{

	private static ThemedStyleMap sThemedStyleMap = null;
	private HyperlinkManager hyperlinkManager;

	/**
	 * @param vt100TerminalControl
	 * @param model
	 * @param hyperlinkManager
	 */
	protected ThemedTextLineRenderer(ITextCanvasModel model, HyperlinkManager hyperlinkManager)
	{
		super(null, model);
		this.hyperlinkManager = hyperlinkManager;
		fStyleMap = getStyleMap();
	}

	synchronized static ThemedStyleMap getStyleMap()
	{
		if (sThemedStyleMap == null)
		{
			sThemedStyleMap = new ThemedStyleMap();
		}
		return sThemedStyleMap;
	}

	@Override
	protected Color getSelectionBackground()
	{
		Theme theme = getThemeManager().getCurrentTheme();
		return getColorManager().getColor(theme.getSelectionAgainstBG());
	}

	@Override
	protected Color getSelectionForeground()
	{
		Theme theme = getThemeManager().getCurrentTheme();
		return getColorManager().getColor(theme.getForeground());
	}

	protected IThemeManager getThemeManager()
	{
		return ThemePlugin.getDefault().getThemeManager();
	}

	protected ColorManager getColorManager()
	{
		return ThemePlugin.getDefault().getColorManager();
	}

	public void drawLine(ITextCanvasModel model, GC gc, int line, int x, int y, int colFirst, int colLast)
	{
		if (line < 0 || line >= getTerminalText().getHeight() || colFirst >= getTerminalText().getWidth()
				|| colFirst - colLast == 0)
		{
			fillBackground(gc, x, y, getCellWidth() * (colLast - colFirst), getCellHeight());
		}
		else
		{
			colLast = Math.min(colLast, getTerminalText().getWidth());
			LineSegment[] segments = getTerminalText().getLineSegments(line, colFirst, colLast - colFirst);
			for (int i = 0; i < segments.length; i++)
			{
				LineSegment segment = segments[i];
				Style style = segment.getStyle();
				setupGC(gc, style);
				String text = segment.getText();
				drawText(gc, x, y, colFirst, segment.getColumn(), text);
				drawCursor(model, gc, line, x, y, colFirst);
			}

			char[] chars = model.getTerminalText().getChars(line);
			IHyperlink[] links = this.hyperlinkManager.searchLineForHyperlinks(line);
			if (!ArrayUtil.isEmpty(links))
			{
				for (IHyperlink link : links)
				{
					IRegion region = link.getHyperlinkRegion();
					String text = StringUtil.EMPTY;
					if (chars != null)
					{
						try
						{
							text = new String(chars, region.getOffset(), region.getLength());
						}
						catch (Exception e)
						{
							// ignore errors?
						}
					}
					underlineText(gc, x, y, colFirst, region.getOffset(), text);
				}
			}
			if (fModel.hasLineSelection(line))
			{
				gc.setForeground(getSelectionForeground());
				gc.setBackground(getSelectionBackground());
				Point start = model.getSelectionStart();
				Point end = model.getSelectionEnd();

				if (chars == null)
				{
					return;
				}
				int offset = 0;
				if (start.y == line)
				{
					offset = start.x;
				}
				offset = Math.max(offset, colFirst);
				int len;
				if (end.y == line)
				{
					len = end.x - offset + 1;
				}
				else
				{
					len = chars.length - offset + 1;
				}
				len = Math.min(len, chars.length - offset);
				if (len > 0)
				{
					String text = new String(chars, offset, len);
					drawText(gc, x, y, colFirst, offset, text);
				}
			}
		}
	}
}
