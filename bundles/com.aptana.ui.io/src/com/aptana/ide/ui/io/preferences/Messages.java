/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.ide.ui.io.preferences;

import org.eclipse.osgi.util.NLS;

/**
 * NLS
 */
public final class Messages extends NLS
{

	private static final String BUNDLE_NAME = "com.aptana.ide.ui.io.preferences.messages";//$NON-NLS-1$

	public static String PermissionsGroup_All;
	public static String PermissionsGroup_Execute;
	public static String PermissionsGroup_Group;
	public static String PermissionsGroup_Read;
	public static String PermissionsGroup_Title;
	public static String PermissionsGroup_User;
	public static String PermissionsGroup_Write;
	public static String RemotePreferencePage_LBL_Description;
	public static String RemotePreferencePage_LBL_ReopenRemote;

	static
	{
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages()
	{
		// Do not instantiate
	}
}
