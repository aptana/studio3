/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.git.ui.internal.preferences;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS
{
	private static final String BUNDLE_NAME = "com.aptana.git.ui.internal.preferences.messages"; //$NON-NLS-1$

	public static String GitExecutableLocationPage_AutoAttachProjectsLabel;
	public static String GitExecutableLocationPage_CalculatePullIndicatorLabel;
	public static String GitExecutableLocationPage_InvalidLocationErrorMessage;
	public static String GitExecutableLocationPage_LocationLabel;
	public static String GithubAccountPageProvider_EmptyPassword;
	public static String GithubAccountPageProvider_EmptyUsername;
	public static String GithubAccountPageProvider_InvalidInputTitle;
	public static String GithubAccountPageProvider_LoginFailed;
	public static String GithubAccountPageProvider_Logout_LBL;
	public static String GithubAccountPageProvider_Name;

	public static String GithubAccountPageProvider_NoteMessageToUsePersonalToken;
	public static String GithubAccountPageProvider_Password_LBL;
	public static String GithubAccountPageProvider_Signup_LBL;
	public static String GithubAccountPageProvider_User_LBL;
	public static String GithubAccountPageProvider_Username_LBL;
	public static String GithubAccountPageProvider_Validate_LBL;
	public static String GithubAccountPageProvider_ValidatingCredentials;
	public static String GitPreferencePage_IgnoreMissingGitLabel;

	static
	{
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages()
	{
	}
}
