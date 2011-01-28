/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */

package com.aptana.terminal.internal.emulator;

import org.eclipse.swt.graphics.Color;
import org.eclipse.tm.internal.terminal.textcanvas.ITextCanvasModel;
import org.eclipse.tm.internal.terminal.textcanvas.TextLineRenderer;

import com.aptana.theme.Theme;
import com.aptana.theme.ThemePlugin;

/**
 * @author Max Stepanov
 */
/* package */class ThemedTextLineRenderer extends TextLineRenderer
{

	private static ThemedStyleMap sThemedStyleMap = null;

	/**
	 * @param model
	 */
	public ThemedTextLineRenderer(ITextCanvasModel model)
	{
		super(null, model);
		fStyleMap = getStyleMap();
	}

	static ThemedStyleMap getStyleMap()
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
		Theme theme = ThemePlugin.getDefault().getThemeManager().getCurrentTheme();
		return ThemePlugin.getDefault().getColorManager().getColor(theme.getSelectionAgainstBG());
	}

	@Override
	protected Color getSelectionForeground()
	{
		Theme theme = ThemePlugin.getDefault().getThemeManager().getCurrentTheme();
		return ThemePlugin.getDefault().getColorManager().getColor(theme.getForeground());
	}

}
