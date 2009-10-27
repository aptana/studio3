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
	public static String CommitDialog_3;
	public static String CommitDialog_4;
	public static String CommitDialog_5;
	public static String CommitDialog_6;
	public static String CommitDialog_CannotMerge_Error;
	public static String CommitDialog_CommitButton_Label;
	public static String CommitDialog_deleted;
	public static String CommitDialog_EnterMessage_Error;
	public static String CommitDialog_MessageLabel;
	public static String CommitDialog_modified;
	public static String CommitDialog_new;
	public static String CommitDialog_PathColumnLabel;
	public static String CommitDialog_StageFilesFirst_Error;
	public static String DisconnectProviderOperation_DisconnectJob_Title;
	public static String PullAction_RefreshJob_Title;
	public static String RevertAction_RefreshJob_Title;
	static
	{
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages()
	{
	}
}
