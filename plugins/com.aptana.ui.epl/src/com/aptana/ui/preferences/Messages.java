/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the Eclipse Public License (EPL).
 * Please see the license-epl.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.ui.preferences;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS
{
	private static final String BUNDLE_NAME = "com.aptana.ui.preferences.messages"; //$NON-NLS-1$

	public static String TroubleshootingPreferencePage_DebugSpecificComponents;

	public static String TroubleshootingPreferencePage_LBL_DebuggingOutputLevel;
	public static String TroubleshootingPreferencePage_LBL_Errors;
	public static String TroubleshootingPreferencePage_LBL_All;
	public static String TroubleshootingPreferencePage_LBL_UnknownLoggingLevel;
	public static String TroubleshootingPreferencePage_LBL_AllDebuggingInformation;
	public static String TroubleshootingPreferencePage_LBL_ErrorsAndImportant;
	public static String TroubleshootingPreferencePage_LBL_OnlyError;
	public static String TroubleshootingPreferencePage_LBL_NoDebuggingOutput;

	public static String TroubleshootingPreferencePage_ShowHiddenProcesses;

	public static String TroubleshootingPreferencePage_TroubleshootingPageHeader;

	public static String GenericRootPage_genericPerferencesPageMessage;
	public static String GenericRootPage_noAvailablePages;
	public static String GenericRootPage_preferences;

	public static String PropertyAndPreferencePage_configureProjectSettings;

	public static String PropertyAndPreferencePage_configureWorkspaceSettings;

	public static String PropertyAndPreferencePage_enableProjectSpecific;

	static
	{
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages()
	{
	}
}
