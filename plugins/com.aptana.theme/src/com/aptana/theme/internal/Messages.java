/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.theme.internal;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS
{

	private static final String BUNDLE_NAME = "com.aptana.theme.internal.messages"; //$NON-NLS-1$

	public static String ThemeManager_ERR_DuplicateTheme;
	public static String ThemeManager_ERR_NoThemeFound;
	public static String ThemeManager_ERR_ThemeNoName;
	public static String ThemeManager_InvalidCharInThemeName;
	public static String ThemeManager_NameAlreadyExistsMsg;
	public static String ThemeManager_NameNonEmptyMsg;

	static
	{
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}
}
