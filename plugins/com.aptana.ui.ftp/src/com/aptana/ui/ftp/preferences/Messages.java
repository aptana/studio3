/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.ui.ftp.preferences;

import org.eclipse.osgi.util.NLS;

/**
 * NLS
 */
public final class Messages extends NLS
{

	private static final String BUNDLE_NAME = "com.aptana.ui.ftp.preferences.messages";//$NON-NLS-1$

	public static String FTPPreferencePage_DirectoryGroupTitle;
	public static String FTPPreferencePage_ERR_Invalid_KeepAlive_Time;
	public static String FTPPreferencePage_FileGroupTitle;
	public static String FTPPreferencePage_LBL_KeepAlive;
	public static String FTPPreferencePage_Notes;

	static
	{
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages()
	{
		// Do not instantiate
	}
}
