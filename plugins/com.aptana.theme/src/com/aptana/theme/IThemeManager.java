/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.theme;

import java.util.Set;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.text.rules.IToken;

public interface IThemeManager
{

	/**
	 * Preference key used to store the timestamp of last theme change. Used to force a redraw of editors when theme
	 * changes (even if it remains same theme, but has been edited).
	 */
	public static final String THEME_CHANGED = "THEME_CHANGED"; //$NON-NLS-1$

	/**
	 * The font that we use to control the App Explorer, outline view and other views that will use the theming and will
	 * default to the text editor font.
	 */
	public static final String VIEW_FONT_NAME = "com.aptana.explorer.font"; //$NON-NLS-1$

	// FIXME Rather than having pref listeners register by knowing the node and everything, have them register through
	// this interface like Eclipse's IThemeManager

	// TODO Make arg the string id, rather than the theme object
	public void setCurrentTheme(Theme theme);

	public Theme getCurrentTheme();

	public void addTheme(Theme theme);

	public void removeTheme(Theme theme);

	public boolean isBuiltinTheme(String themeName);

	public Set<String> getThemeNames();

	public Theme getTheme(String name);

	public IToken getToken(String scope);

	/**
	 * Used to validate that theme name is ok to use.
	 * 
	 * @param name
	 * @return
	 */
	public IStatus validateThemeName(String name);

}
