/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
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

	public static String FTPPreferencePage_ERR_Invalid_KeepAlive_Time;
	public static String FTPPreferencePage_LBL_Downloads;
	public static String FTPPreferencePage_LBL_KeepAlive;
	public static String FTPPreferencePage_LBL_Uploads;
	public static String FTPPreferencePage_Notes;

	public static String UpdatePermissionsComposite_ForFiles;
	public static String UpdatePermissionsComposite_ForFolders;
	public static String UpdatePermissionsComposite_LBL_ToSourcePermissions;
	public static String UpdatePermissionsComposite_LBL_ToSpecificPermissions;
	public static String UpdatePermissionsComposite_LBL_UpdatePermissions;

	static
	{
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages()
	{
		// Do not instantiate
	}
}
