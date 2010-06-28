package com.aptana.git.ui.internal.actions;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS
{
	private static final String BUNDLE_NAME = "com.aptana.git.ui.internal.actions.messages"; //$NON-NLS-1$
	public static String AbstractOperationAction_GenericFailed_Message;
	public static String CommitAction_MultipleRepos_Message;
	public static String CommitAction_MultipleRepos_Title;
	public static String CommitAction_NoRepo_Message;
	public static String CommitAction_NoRepo_Title;
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
	public static String CommitDialog_deleted;
	public static String CommitDialog_EnterMessage_Error;
	public static String CommitDialog_MessageLabel;
	public static String CommitDialog_modified;
	public static String CommitDialog_new;
	public static String CommitDialog_PathColumnLabel;
	public static String CommitDialog_StageFilesFirst_Error;
	public static String CommitDialog_StageSelected;
	public static String CommitDialog_StageSelectedMarker;
	public static String DisconnectProviderOperation_DisconnectJob_Title;
	public static String PullAction_RefreshJob_Title;
	public static String RevertAction_Label;
	public static String RevertAction_RefreshJob_Title;
	public static String SwitchBranchAction_BranchSwitch_Msg;
	public static String DeleteBranchAction_BranchDelete_Msg;
	public static String DeleteBranchAction_BranchDeletionFailed_Msg;
	public static String DeleteBranchAction_BranchDeletionFailed_Title;
	static
	{
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages()
	{
	}
}
