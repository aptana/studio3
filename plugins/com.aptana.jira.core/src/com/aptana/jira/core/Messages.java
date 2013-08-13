/**
 * Aptana Studio
 * Copyright (c) 2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.jira.core;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS
{
	private static final String BUNDLE_NAME = "com.aptana.jira.core.messages"; //$NON-NLS-1$

	public static String JiraCorePlugin_LoadProjectProviders;

	public static String JiraIssueType_Bug;
	public static String JiraIssueType_Feature;
	public static String JiraIssueType_Improvement;

	public static String JiraManager_BadCredentialsErrMsg;

	public static String JiraManager_ERR_NoJiraExecutable;
	public static String JiraManager_ERR_NotLoggedIn;

	public static String JiraManager_UnknownErrMsg;

	public static String JiraIssueSeverity_Blocker;
	public static String JiraIssueSeverity_Major;
	public static String JiraIssueSeverity_Minor;
	public static String JiraIssueSeverity_None;
	public static String JiraIssueSeverity_Trivial;

	static
	{
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages()
	{
	}
}
