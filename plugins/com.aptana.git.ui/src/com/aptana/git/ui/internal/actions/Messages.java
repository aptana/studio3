/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.git.ui.internal.actions;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS
{
	private static final String BUNDLE_NAME = "com.aptana.git.ui.internal.actions.messages"; //$NON-NLS-1$

	public static String CommitAction_MultipleRepos_Message;
	public static String CommitAction_MultipleRepos_Title;
	public static String CommitAction_NoRepo_Message;
	public static String CommitAction_NoRepo_Title;

	public static String CommitDialog_BrowserWidgetFailedMsg;
	public static String CommitDialog_Changes;
	public static String CommitDialog_NoFileSelected;
	public static String CommitDialog_StageAll;
	public static String CommitDialog_StageAllMarker;
	public static String CommitDialog_StagedChanges;
	public static String CommitDialog_UnstageAll;
	public static String CommitDialog_UnstageAllMarker;
	public static String CommitDialog_UnstagedChanges;
	public static String CommitDialog_UnstageSelected;
	public static String CommitDialog_UnstageSelectedMarker;
	public static String CommitDialog_CannotMerge_Error;
	public static String CommitDialog_CommitButton_Label;
	public static String CommitDialog_CloseButton_Label;
	public static String CommitDialog_EnterMessage_Error;
	public static String CommitDialog_MessageLabel;
	public static String CommitDialog_PathColumnLabel;
	public static String CommitDialog_RevertLabel;
	public static String CommitDialog_StageFilesFirst_Error;
	public static String CommitDialog_StageSelected;
	public static String CommitDialog_StageSelectedMarker;

	public static String CreatePullRequestHandler_ExtractRepoNameFromRemoteFailedErr;

	public static String CreatePullRequestHandler_GetRemotesFailedErr;

	public static String CreatePullRequestHandler_NoRepoErr;

	public static String CreatePullRequestHandler_NotLoggedInErr;

	public static String CreatePullRequestHandler_PRSubmittedTitle;

	public static String CreatePullRequestHandler_PullRequestTitle;

	public static String CreatePullRequestHandler_RemoteOriginDoesntExistErr;

	public static String CreatePullRequestHandler_RepoAPIErr;

	public static String CreatePullRequestHandler_SubmitPRJobName;

	public static String CreatePullRequestHandler_SuccessMsg;

	public static String CreatePullRequestHandler_UnknownParentRepoOwnerName;

	public static String DisconnectHandler_Job_Title;
	public static String DisconnectProviderOperation_DisconnectJob_Title;

	public static String PullAction_RefreshJob_Title;

	public static String RevertAction_Label;
	public static String RevertAction_RefreshJob_Title;

	public static String SwitchBranchAction_BranchSwitch_Msg;

	public static String DeleteBranchAction_BranchDelete_Msg;
	public static String DeleteBranchAction_BranchDeletionFailed_Msg;
	public static String DeleteBranchAction_BranchDeletionFailed_Title;

	public static String DeleteBranchHandler_JobName;

	public static String DeleteRemoteAction_RemoteDeleted_Msg;

	public static String DeleteRemoteHandler_JobName;

	public static String GithubNetworkHandler_ViewName;
	public static String GitLaunchDelegate_FailedToAcquireWriteLock;

	static
	{
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages()
	{
	}
}
