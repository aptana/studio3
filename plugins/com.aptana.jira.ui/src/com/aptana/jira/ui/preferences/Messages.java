/**
 * Aptana Studio
 * Copyright (c) 2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.jira.ui.preferences;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS
{
	private static final String BUNDLE_NAME = "com.aptana.jira.ui.preferences.messages"; //$NON-NLS-1$

	public static String JiraPreferencePageProvider_ERR_EmptyPassword;
	public static String JiraPreferencePageProvider_ERR_EmptyUsername;
	public static String JiraPreferencePageProvider_ERR_InvalidInput_Title;
	public static String JiraPreferencePageProvider_ERR_LoginFailed_Title;
	public static String JiraPreferencePageProvider_LBL_Jira;
	public static String JiraPreferencePageProvider_LBL_Logout;
	public static String JiraPreferencePageProvider_LBL_Password;
	public static String JiraPreferencePageProvider_LBL_Signup;
	public static String JiraPreferencePageProvider_LBL_User;
	public static String JiraPreferencePageProvider_LBL_Username;
	public static String JiraPreferencePageProvider_LBL_Validate;
	public static String JiraPreferencePageProvider_Success_Message;
	public static String JiraPreferencePageProvider_Success_Title;
	public static String JiraPreferencePageProvider_ValidateCredentials;

	static
	{
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages()
	{
	}
}
