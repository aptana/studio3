package com.aptana.explorer.internal.ui;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS
{
	private static final String BUNDLE_NAME = "com.aptana.explorer.internal.ui.messages"; //$NON-NLS-1$

	public static String FilteringProjectView_LBL_FilteringFor;

	public static String GitProjectView_AttachGitRepo_button;
	public static String GitProjectView_AttachGitRepo_jobTitle;
	public static String GitProjectView_BranchAlreadyExistsMessage;

	public static String GitProjectView_BranchDirtyTooltipMessage;

	public static String GitProjectView_BranchSubmenuLabel;
	public static String GitProjectView_ChangedFilesFilterTooltip;
	public static String GitProjectView_Collapse_tooltip;
	public static String GitProjectView_CommitTooltip;
	public static String GitProjectView_CreateBranchDialog_Message;
	public static String GitProjectView_CreateBranchDialog_Title;
	public static String GitProjectView_createNewBranchOption;
	public static String GitProjectView_Expand_tooltip;
	public static String GitProjectView_InvalidBranchNameMessage;
	public static String GitProjectView_LBL_ShowGitHubNetwork;
	public static String GitProjectView_NonEmptyBranchNameMessage;
	public static String GitProjectView_NoWhitespaceBranchNameMessage;
	public static String GitProjectView_PullChangesTooltipMessage;

	public static String GitProjectView_PullJobTitle;
	public static String GitProjectView_PullTooltip;
	public static String GitProjectView_PushChangesTooltipMessage;

	public static String GitProjectView_PushJobTitle;
	public static String GitProjectView_PushTooltip;
	public static String GitProjectView_ShowGitHubNetworkJobTitle;
	public static String GitProjectView_StashJobTitle;
	public static String GitProjectView_StashTooltip;
	public static String GitProjectView_SwitchToBranch;

	public static String GitProjectView_UnresolvedMerges_msg;
	public static String GitProjectView_UnstashJobTitle;
	public static String GitProjectView_UnstashTooltip;
	public static String GitProjectView_DiffTooltip;
	public static String GitProjectView_StageTooltip;
	public static String GitProjectView_StageJobTitle;
	public static String GitProjectView_UnstageTooltip;
	public static String GitProjectView_UnstageJobTitle;
	public static String GitProjectView_StatusTooltip;
	public static String GitProjectView_DisconnectTooltip;
	public static String GitProjectView_DisconnectJobTitle;
	public static String GitProjectView_RevertTooltip;
	public static String GitProjectView_RevertJobTitle;
	public static String GitProjectView_MergeBranch;
	public static String GitProjectView_MoreSubmenuLabel;
	public static String GitProjectView_DeleteBranch;
	public static String GitProjectView_AddRemoteTooltip;
	public static String GitProjectView_GitDiffDialogTitle;
	public static String GitProjectView_LBL_ShowInHistory;
	
	public static String SingleProjectView_CaseSensitive;
	public static String SingleProjectView_CreateAppMenuItem;
	public static String SingleProjectView_OpenBrowserItem;

	public static String SingleProjectView_OpenTerminalMenuItem_LBL;
	public static String SingleProjectView_RefreshJob_title;
	public static String SingleProjectView_Wildcard;
	public static String SingleProjectView_RegularExpression;
	public static String SingleProjectView_Run_TerminalTitle;
	public static String SingleProjectView_RunMenuTitle;
	public static String SingleProjectView_SwitchToApplication;
	public static String SingleProjectView_DeleteProjectMenuItem_LBL;
	public static String SingleProjectView_DeploySubmenuLabel;
	public static String SingleProjectView_DeployAppMenuItem;

	public static String SingleProjectView_DeployWizardItem;
	public static String SingleProjectView_InitialFileFilterText;
	public static String SingleProjectView_SharingSubmenuLabel;

	public static String SingleProjectView_SynchronizeItem;
	public static String SingleProjectView_AddCollaboratorItem;
	public static String SingleProjectView_RemoveCollaboratorItem;
	public static String SingleProjectView_DatabaseSubmenuLabel;

	public static String SingleProjectView_DownloadItem;
	public static String SingleProjectView_RakeDBMigrateItem;
	public static String SingleProjectView_PushLocalDBItem;
	public static String SingleProjectView_PullRemoteDBItem;
	public static String SingleProjectView_MaintenanceSubmenuLabel;
	public static String SingleProjectView_OnMaintenanceItem;
	public static String SingleProjectView_OffMaintenanceItem;
	public static String SingleProjectView_RemoteSubmenuLabel;
	public static String SingleProjectView_CommandLabel;

	public static String SingleProjectView_ConsoleItem;
	public static String SingleProjectView_RakeCommandItem;
	public static String SingleProjectView_ConfigVarsSubmenuLabel;
	public static String SingleProjectView_AddConfigVarsItem;
	public static String SingleProjectView_ClearConfigVarsItem;
	public static String SingleProjectView_AppInfoItem;

	public static String SingleProjectView_EmailAddressLabel;

	public static String SingleProjectView_FTPSettingItem;

	public static String SingleProjectView_NewAppNameLabel;
	public static String SingleProjectView_RenameAppItem;

	public static String SingleProjectView_UploadItem;

	public static String SingleProjectView_VariableNameLabel;
	
	static
	{
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages()
	{
	}
}
