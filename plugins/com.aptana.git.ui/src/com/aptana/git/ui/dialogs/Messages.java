/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.git.ui.dialogs;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS
{
	private static final String BUNDLE_NAME = "com.aptana.git.ui.dialogs.messages"; //$NON-NLS-1$

	public static String AddRemoteDialog_RemoteURILabel;
	public static String AddRemoteDialog_AddRemoteDialog_Title;
	public static String AddRemoteDialog_AddRemoteDialog_Message;
	public static String AddRemoteDialog_NonEmptyRemoteNameMessage;
	public static String AddRemoteDialog_NoWhitespaceRemoteNameMessage;
	public static String AddRemoteDialog_TrackButtonLabel;
	public static String AddRemoteDialog_UniqueRemoteNameMessage;
	
	public static String CreateBranchDialog_AdvancedOptions_label;
	public static String CreateBranchDialog_CreateBranchDialog_Message;
	public static String CreateBranchDialog_CreateBranchDialog_Title;
	public static String CreateBranchDialog_InvalidBranchNameMessage;
	public static String CreateBranchDialog_NonEmptyBranchNameMessage;
	public static String CreateBranchDialog_NoWhitespaceBranchNameMessage;
	public static String CreateBranchDialog_BranchAlreadyExistsMessage;
	public static String CreateBranchDialog_StartPoint_label;
	public static String CreateBranchDialog_Track_label;

	public static String CreatePullRequestDialog_BodyFieldLabel;

	public static String CreatePullRequestDialog_EmptyTitleErrMsg;

	public static String CreatePullRequestDialog_Title;

	public static String CreatePullRequestDialog_TitleFieldLabel;

	public static String CreateTagDialog_Title;
	public static String CreateTagDialog_Message;
	public static String CreateTagDialog_NonEmptyTagNameMessage;
	public static String CreateTagDialog_NoWhitespaceTagNameMessage;
	public static String CreateTagDialog_TagAlreadyExistsMessage;
	public static String CreateTagDialog_InvalidTagNameMessage;
	public static String CreateTagDialog_Message_label;
	public static String CreateTagDialog_AdvancedOptions_label;
	public static String CreateTagDialog_StartPoint_label;

	public static String CompareWithDialog_Title;
	public static String CompareWithDialog_Message;
	public static String CompareWithDialog_NonEmptyRefMessage;
	public static String CompareWithDialog_Message_label;
	public static String CompareWithDialog_Ref_label;
	public static String CompareWithDialog_InvalidRefError;

	static
	{
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages()
	{
	}
}
