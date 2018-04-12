/**
 * Aptana Studio
 * Copyright (c) 2005-2013 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.ui.preferences;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS
{

	private static final String BUNDLE_NAME = "com.aptana.ui.preferences.messages"; //$NON-NLS-1$

	public static String AptanaPreferencePage_Auto_Migrate_Projects;
	public static String AptanaPreferencePage_Auto_Refresh_Projects;

	public static String ProjectBuildPathPropertyPage_TableDescription;
	public static String ProjectBuildPathPropertyPage_LibraryColumnLabel;
	public static String ProjectBuildPathPropertyPage_PathColumnLabel;

	public static String ProjectBuildPathPropertyPage_up;
	public static String ProjectBuildPathPropertyPage_down;

	public static String TroubleshootingPreferencePage_DebugSpecificComponents;
	public static String TroubleshootingPreferencePage_LBL_DebuggingOutputLevel;
	public static String TroubleshootingPreferencePage_LBL_DebugLevel;
	public static String TroubleshootingPreferencePage_LBL_UnknownLoggingLevel;
	public static String TroubleshootingPreferencePage_LBL_AllDebuggingInformation;
	public static String TroubleshootingPreferencePage_LBL_ErrorsAndImportant;
	public static String TroubleshootingPreferencePage_LBL_OnlyError;
	public static String TroubleshootingPreferencePage_Level_All;
	public static String TroubleshootingPreferencePage_Level_Errors;
	public static String TroubleshootingPreferencePage_Level_Warnings;
	public static String TroubleshootingPreferencePage_SelectAll;
	public static String TroubleshootingPreferencePage_SelectNone;
	public static String TroubleshootingPreferencePage_ShowHiddenProcesses;
	public static String TroubleshootingPreferencePage_TroubleshootingPageHeader;

	static
	{
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages()
	{
	}
}
