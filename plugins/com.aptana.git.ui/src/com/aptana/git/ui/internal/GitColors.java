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
package com.aptana.git.ui.internal;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;

import com.aptana.theme.IThemeManager;
import com.aptana.theme.Theme;
import com.aptana.theme.ThemePlugin;

public class GitColors
{
	/**
	 * Default colors to use for staged/unstaged files when the theme doesn't define overrides.
	 */
	private static final RGB DEFAULT_RED_BG = new RGB(255, 238, 238);
	private static final RGB DEFAULT_RED_FG = new RGB(154, 11, 11);
	private static final RGB DEFAULT_GREEN_BG = new RGB(221, 255, 221);
	private static final RGB DEFAULT_GREEN_FG = new RGB(60, 168, 60);

	/**
	 * Default set to use when bg is very dark!
	 */
	private static final RGB DEFAULT_DARK_RED_BG = new RGB(74, 11, 11);
	private static final RGB DEFAULT_LIGHT_RED_FG = new RGB(255, 224, 224);
	private static final RGB DEFAULT_DARK_GREEN_BG = new RGB(0, 51, 0);
	private static final RGB DEFAULT_LIGHT_GREEN_FG = new RGB(212, 255, 212);

	/**
	 * The token used from the theme for staged file decorations.
	 */
	private static final String STAGED_TOKEN = "markup.inserted"; //$NON-NLS-1$

	/**
	 * The token used from the theme for unstaged file decorations.
	 */
	private static final String UNSTAGED_TOKEN = "markup.deleted"; //$NON-NLS-1$

	protected static Color greenFG()
	{
		if (getActiveTheme().hasEntry(STAGED_TOKEN))
		{
			return getActiveTheme().getForeground(STAGED_TOKEN);
		}
		if (currentThemeHasDarkBG())
		{
			return ThemePlugin.getDefault().getColorManager().getColor(DEFAULT_LIGHT_GREEN_FG);
		}
		return ThemePlugin.getDefault().getColorManager().getColor(DEFAULT_GREEN_FG);
	}

	public static Color greenBG()
	{
		if (getActiveTheme().hasEntry(STAGED_TOKEN))
		{
			return getActiveTheme().getBackground(STAGED_TOKEN);
		}
		if (currentThemeHasLightFG())
		{
			return ThemePlugin.getDefault().getColorManager().getColor(DEFAULT_DARK_GREEN_BG);
		}
		// TODO Test if current theme's bg is too close to color we return here?
		return ThemePlugin.getDefault().getColorManager().getColor(DEFAULT_GREEN_BG);
	}

	protected static Color redFG()
	{
		if (getActiveTheme().hasEntry(UNSTAGED_TOKEN))
		{
			return getActiveTheme().getForeground(UNSTAGED_TOKEN);
		}
		if (currentThemeHasDarkBG())
		{
			return ThemePlugin.getDefault().getColorManager().getColor(DEFAULT_LIGHT_RED_FG);
		}
		return ThemePlugin.getDefault().getColorManager().getColor(DEFAULT_RED_FG);
	}

	public static Color redBG()
	{
		if (getActiveTheme().hasEntry(UNSTAGED_TOKEN))
		{
			return getActiveTheme().getBackground(UNSTAGED_TOKEN);
		}
		if (currentThemeHasLightFG())
		{
			return ThemePlugin.getDefault().getColorManager().getColor(DEFAULT_DARK_RED_BG);
		}
		// TODO Test if current theme's bg is too close to color we return here?
		return ThemePlugin.getDefault().getColorManager().getColor(DEFAULT_RED_BG);
	}

	private static boolean currentThemeHasDarkBG()
	{
		return getActiveTheme().hasDarkBG();
	}

	private static boolean currentThemeHasLightFG()
	{
		return getActiveTheme().hasLightFG();
	}

	private static Theme getActiveTheme()
	{
		return getThemeManager().getCurrentTheme();
	}

	private static IThemeManager getThemeManager()
	{
		return ThemePlugin.getDefault().getThemeManager();
	}
}
