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
