/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.ide.syncing.ui.internal;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS
{

	private static final String BUNDLE_NAME = "com.aptana.ide.syncing.ui.internal.messages"; //$NON-NLS-1$

	public static String SiteConnectionPropertiesWidget_ERR_DuplicateNames;
	public static String SiteConnectionPropertiesWidget_ERR_EmptyName;
	public static String SiteConnectionPropertiesWidget_ERR_InvalidFilesystemFolder;
	public static String SiteConnectionPropertiesWidget_ERR_InvalidProjectFolder;
	public static String SiteConnectionPropertiesWidget_ERR_NoProject;
	public static String SiteConnectionPropertiesWidget_ERR_NoRemote;
	public static String SiteConnectionPropertiesWidget_ERR_NoType;
	public static String SiteConnectionPropertiesWidget_LBL_DefaultDescription;
	public static String SiteConnectionPropertiesWidget_LBL_Destination;
	public static String SiteConnectionPropertiesWidget_LBL_Filesystem;
	public static String SiteConnectionPropertiesWidget_LBL_Folder;
	public static String SiteConnectionPropertiesWidget_LBL_Name;
	public static String SiteConnectionPropertiesWidget_LBL_Project;
	public static String SiteConnectionPropertiesWidget_LBL_Remote;
	public static String SiteConnectionPropertiesWidget_LBL_Source;
	public static String SiteConnectionPropertiesWidget_NoProject;
	public static String SiteConnectionPropertiesWidget_NoRemoteSite;

	static
	{
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages()
	{
	}
}
