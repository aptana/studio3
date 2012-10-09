/**
 * Aptana Studio
 * Copyright (c) 2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.jira.ui.internal;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS
{
	private static final String BUNDLE_NAME = "com.aptana.jira.ui.internal.messages"; //$NON-NLS-1$

	public static String SubmitTicketDialog_DefaultMessage;
	public static String SubmitTicketDialog_ERR_EmptyActualResult;
	public static String SubmitTicketDialog_ERR_EmptyExpectedResult;
	public static String SubmitTicketDialog_ERR_EmptyStepsToReproduce;
	public static String SubmitTicketDialog_ERR_EmptySummary;
	public static String SubmitTicketDialog_ERR_EmptyType;
	public static String SubmitTicketDialog_LBL_ActualResult;
	public static String SubmitTicketDialog_LBL_DiagnosticLog;
	public static String SubmitTicketDialog_LBL_ExpectedResult;
	public static String SubmitTicketDialog_LBL_LogsToAttach;
	public static String SubmitTicketDialog_LBL_Screenshots;
	public static String SubmitTicketDialog_LBL_Severity;
	public static String SubmitTicketDialog_LBL_StepsToReproduce;
	public static String SubmitTicketDialog_LBL_StudioLog;
	public static String SubmitTicketDialog_LBL_Summary;
	public static String SubmitTicketDialog_LBL_Type;
	public static String SubmitTicketDialog_LBL_User;
	public static String SubmitTicketDialog_ShellTitle;
	public static String SubmitTicketDialog_Title;

	static
	{
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages()
	{
	}
}
