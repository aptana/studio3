/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.ui;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS
{

	private static final String BUNDLE_NAME = "com.aptana.ui.messages"; //$NON-NLS-1$

	public static String DialogUtils_doNotShowMessageAgain;
	public static String DialogUtils_HideMessage;

	public static String IDialogConstants_LBL_Apply;
	public static String IDialogConstants_LBL_Overwrite;

	public static String QuickMenuDialog_NoMatchesFound;

	public static String Sudo_AuthenticationDialog_Title;
	public static String Sudo_Invalid_Password_Prompt;

	public static String UIPlugin_automaticBuildsWarningMessage;
	public static String UIPlugin_automaticBuildsWarningTitle;

	public static String UIPlugin_ERR_FailToSetPref;
	public static String UIPlugin_ResetPerspective_Description;
	public static String UIPlugin_ResetPerspective_Title;

	public static String EclipseDiagnosticLog_version;
	public static String EclipseDiagnosticLog_host_os;
	public static String EclipseDiagnosticLog_install_dir;
	public static String EclipseDiagnosticLog_jre_home;
	public static String EclipseDiagnosticLog_jre_vendor;
	public static String EclipseDiagnosticLog_jre_version;
	public static String EclipseDiagnosticLog_language;
	public static String EclipseDiagnosticLog_os_arch;

	public static String EclipseDiagnosticLog_os_version;
	public static String EclipseDiagnosticLog_vm_args;
	public static String EclipseDiagnosticLog_workspace_dir;

	public static String SudoManager_MessagePrompt;

	public static String SudoUIManager_LoginErrorMessage;

	public static String SudoUIManager_OperationCancelledError;

	static
	{
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages()
	{
	}
}
