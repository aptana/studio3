/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */

package com.aptana.terminal.internal.emulator;

import java.util.Map;

import org.eclipse.swt.graphics.Color;
import org.eclipse.tm.internal.terminal.textcanvas.StyleMap;
import org.eclipse.tm.terminal.model.StyleColor;

import com.aptana.theme.Theme;
import com.aptana.theme.ThemePlugin;

/**
 * @author Max Stepanov
 */
/* package */class ThemedStyleMap extends StyleMap {

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.tm.internal.terminal.textcanvas.StyleMap#getColor(java.util.Map,
	 * org.eclipse.tm.terminal.model.StyleColor)
	 */
	@SuppressWarnings("rawtypes")
	@Override
	protected Color getColor(Map map, StyleColor color) {
		// Just grab colors straight from theme!
		String colorName = color.getName().toLowerCase();
		String ansiName = "ansi." + colorName; //$NON-NLS-1$
		Theme theme = ThemePlugin.getDefault().getThemeManager().getCurrentTheme();
		if (theme.hasEntry(ansiName)) {
			return theme.getForeground(ansiName);
		}
		if (StyleMap.WHITE_FOREGROUND.equals(colorName)) {
			ansiName = "ansi.white"; //$NON-NLS-1$
			if (theme.hasEntry(ansiName)) {
				return theme.getForeground(ansiName);
			}
		}
		boolean isForeground = map == fColorMapForeground; // $codepro.audit.disable useEquals
		if (StyleMap.BLACK.equals(colorName)) {
			return ThemePlugin.getDefault().getColorManager().getColor(theme.getForeground());
		}
		if (StyleMap.WHITE.equals(colorName)) {
			return ThemePlugin.getDefault().getColorManager()
					.getColor(isForeground ? theme.getForeground() : theme.getBackground());
		}

		// fall back to defaults...
		return super.getColor(map, color);
	}

	protected Color getBackgroundColor() {
		Theme theme = ThemePlugin.getDefault().getThemeManager().getCurrentTheme();
		return ThemePlugin.getDefault().getColorManager().getColor(theme.getBackground());
	}

}
