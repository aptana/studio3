/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.ui.dialogs;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS
{
	private static final String BUNDLE_NAME = "com.aptana.ui.dialogs.messages"; //$NON-NLS-1$

	public static String DiagnosticDialog_close_label;
	public static String DiagnosticDialog_copy_clipboard_label;
	public static String DiagnosticDialog_run_diagnostic_title;

	public static String MinimizableWizardDialog_hideLabel;

	public static String ProjectSelectionDialog_Message;
	public static String ProjectSelectionDialog_Title;

	public static String PasswordPromptDialog_Login;
	public static String PasswordPromptDialog_Password;
	public static String PasswordPromptDialog_SavePassword;
	public static String PasswordPromptDialog_UserName;

	public static String SaveAndLaunchPromptDialog_SaveMessage;

	public static String SudoPasswordPromptDialog_MessagePrompt_Suffix;
	public static String SudoPasswordPromptDialog_User;
	public static String SudoPasswordPromptDialog_Password;

	static
	{
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages()
	{
	}
}
