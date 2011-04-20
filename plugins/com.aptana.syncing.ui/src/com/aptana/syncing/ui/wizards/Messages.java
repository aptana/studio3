/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.syncing.ui.wizards;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS
{
	private static final String BUNDLE_NAME = "com.aptana.syncing.ui.wizards.messages"; //$NON-NLS-1$

	public static String ImportConnectionsPage_Description;
	public static String ImportConnectionsPage_ERR_EmptyPath;
	public static String ImportConnectionsPage_ERR_InvalidDirectory;
	public static String ImportConnectionsPage_ERR_InvalidFile;
	public static String ImportConnectionsPage_ERR_InvalidPath;
	public static String ImportConnectionsPage_LBL_Path;
	public static String ImportConnectionsPage_LBL_SourceType;
	public static String ImportConnectionsPage_SourceType_File;
	public static String ImportConnectionsPage_SourceType_Workspace;
	public static String ImportConnectionsPage_Title;

	public static String ImportConnectionsWizard_Title;

	static
	{
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages()
	{
	}
}
