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
package com.aptana.theme.preferences;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS
{
	private static final String BUNDLE_NAME = "com.aptana.theme.preferences.messages"; //$NON-NLS-1$

	public static String ThemePreferencePage_AddTokenLabel;
	public static String ThemePreferencePage_BackgroundColumnLabel;
	public static String ThemePreferencePage_BackgroundLabel;
	public static String ThemePreferencePage_BoldButtonLabel;
	public static String ThemePreferencePage_CaretLabel;
	public static String ThemePreferencePage_DeleteThemeMsg;
	public static String ThemePreferencePage_DeleteThemeTitle;
	public static String ThemePreferencePage_FontName;
	public static String ThemePreferencePage_FontNameLabel;
	public static String ThemePreferencePage_FontStyleColumnLabel;
	public static String ThemePreferencePage_ForegroundColumnLabel;
	public static String ThemePreferencePage_ForegroundLabel;
	public static String ThemePreferencePage_ImportLabel;
	public static String ThemePreferencePage_ExportLabel;
	public static String ThemePreferencePage_InvasiveThemesLBL;
	public static String ThemePreferencePage_ItalicButtonLabel;
	public static String ThemePreferencePage_LineHighlightLabel;
	public static String ThemePreferencePage_NewThemeDefaultName;
	public static String ThemePreferencePage_NewThemeMsg;
	public static String ThemePreferencePage_NewThemeTitle;
	public static String ThemePreferencePage_RemoveTokenLabel;
	public static String ThemePreferencePage_RenameButtonLabel;
	public static String ThemePreferencePage_RenameThemeMsg;
	public static String ThemePreferencePage_RenameThemeTitle;
	public static String ThemePreferencePage_ScopeSelectoreLabel;
	public static String ThemePreferencePage_SelectFontButtonLabel;
	public static String ThemePreferencePage_SelectionLabel;
	public static String ThemePreferencePage_UnderlineButtonLabel;

	static
	{
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages()
	{
	}
}
