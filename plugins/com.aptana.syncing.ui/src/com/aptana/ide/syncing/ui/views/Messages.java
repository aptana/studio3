/**
 * This file Copyright (c) 2005-2009 Aptana, Inc. This program is
 * dual-licensed under both the Aptana Public License and the GNU General
 * Public license. You may elect to use one or the other of these licenses.
 * 
 * This program is distributed in the hope that it will be useful, but
 * AS-IS and WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE, TITLE, or
 * NONINFRINGEMENT. Redistribution, except as permitted by whichever of
 * the GPL or APL you select, is prohibited.
 *
 * 1. For the GPL license (GPL), you can redistribute and/or modify this
 * program under the terms of the GNU General Public License,
 * Version 3, as published by the Free Software Foundation.  You should
 * have received a copy of the GNU General Public License, Version 3 along
 * with this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 * 
 * Aptana provides a special exception to allow redistribution of this file
 * with certain other free and open source software ("FOSS") code and certain additional terms
 * pursuant to Section 7 of the GPL. You may view the exception and these
 * terms on the web at http://www.aptana.com/legal/gpl/.
 * 
 * 2. For the Aptana Public License (APL), this program and the
 * accompanying materials are made available under the terms of the APL
 * v1.0 which accompanies this distribution, and is available at
 * http://www.aptana.com/legal/apl/.
 * 
 * You may view the GPL, Aptana's exception and additional terms, and the
 * APL in the file titled license.html at the root of the corresponding
 * plugin containing this source file.
 * 
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.ide.syncing.ui.views;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {

    private static final String BUNDLE_NAME = "com.aptana.ide.syncing.ui.views.messages"; //$NON-NLS-1$

    public static String ConnectionPointComposite_Column_Filename;
    public static String ConnectionPointComposite_Column_LastModified;
    public static String ConnectionPointComposite_Column_Size;
    public static String ConnectionPointComposite_LBL_Path;
    public static String ConnectionPointComposite_LBL_Transfer;
    public static String ConnectionPointComposite_TTP_Home;
    public static String ConnectionPointComposite_TTP_Refresh;
    public static String ConnectionPointComposite_TTP_Up;

    public static String FTPManagerComposite_ERR_CreateNewSiteFailed;
    public static String FTPManagerComposite_ERR_EmptyName;
    public static String FTPManagerComposite_ERR_NameExists;
    public static String FTPManagerComposite_LBL_SaveAs;
    public static String FTPManagerComposite_LBL_Sites;
    public static String FTPManagerComposite_LBL_Source;
    public static String FTPManagerComposite_LBL_Target;
    public static String FTPManagerComposite_NameInput_Message;
    public static String FTPManagerComposite_NameInput_Title;
    public static String FTPManagerComposite_TTP_Edit;
    public static String FTPManagerComposite_TTP_SaveAs;
    public static String FTPManagerComposite_TTP_TransferLeft;
    public static String FTPManagerComposite_TTP_TransferRight;

	/**
	 * DirectionToolBar_DownloadToolTip
	 */
	public static String DirectionToolBar_DownloadToolTip;

	/**
	 * DirectionToolBar_ForceDownloadToolTip
	 */
	public static String DirectionToolBar_ForceDownloadToolTip;

	/**
	 * DirectionToolBar_ForceUploadToolTip
	 */
	public static String DirectionToolBar_ForceUploadToolTip;

	/**
	 * DirectionToolBar_SyncToolTip
	 */
	public static String DirectionToolBar_SyncToolTip;

	/**
	 * DirectionToolBar_UploadToolTip
	 */
	public static String DirectionToolBar_UploadToolTip;

	/**
	 * SyncView_ConfirmDelete
	 */
	public static String SyncView_ConfirmDelete;

	/**
	 * SyncView_EditSiteConnection
	 */
	public static String SyncView_EditSiteConnection;

	/**
	 * SyncView_ShowHideRemoteConnections
	 */
	public static String SyncView_ShowHideRemoteConnections;

	/**
	 * SyncView_ShowHideLocalConnections
	 */
	public static String SyncView_ShowHideLocalConnections;

	/**
	 * SyncView_DeleteSiteConnection
	 */
	public static String SyncView_DeleteSiteConnection;

	/**
	 * SyncView_AddSiteConnection
	 */
	public static String SyncView_AddSiteConnection;

	/**
	 * SyncView_Synchronize
	 */
	public static String SyncView_Synchronize;

	/**
	 * SyncView_SynchronizeFiles
	 */
	public static String SyncView_SynchronizeFiles;

	/**
	 * SyncView_SyncConnectionLocationLabel
	 */
	public static String SyncView_SyncConnectionLocationLabel;

	/**
	 * SyncView_EndpointMissing
	 */
	public static String SyncView_EndpointMissing;

	/**
	 * SyncView_ErrorRefreshingSyncView
	 */
	public static String SyncView_ErrorRefreshingSyncView;

	/**
	 * SyncView_SyncViewInfo
	 */
	public static String SyncView_SyncViewInfo;

	/**
	 * SyncView_AreYouSureYouWishToDelete
	 */
	public static String SyncView_AreYouSureYouWishToDelete;

	public static String SmartSyncDialog_AdvancedOptions;
	public static String SmartSyncDialog_BothDirection;
	public static String SmartSyncDialog_Cancel;
	public static String SmartSyncDialog_Close;
	public static String SmartSyncDialog_CloseWhenDone;
	public static String SmartSyncDialog_ColumnLocal;
	public static String SmartSyncDialog_ColumnName;
	public static String SmartSyncDialog_ColumnRemote;
	public static String SmartSyncDialog_ColumnResources;
	public static String SmartSyncDialog_ColumnSkip;
	public static String SmartSyncDialog_Comparing; 
	public static String SmartSyncDialog_Complete;
	public static String SmartSyncDialog_Delete;
	public static String SmartSyncDialog_DeletedItems;
	public static String SmartSyncDialog_DeleteExtra;
	public static String SmartSyncDialog_DeleteExtraTooltip;
	public static String SmartSyncDialog_DeleteFiles;
	public static String SmartSyncDialog_DirectionMode;
	public static String SmartSyncDialog_DownDirectionTooltip;
	public static String SmartSyncDialog_Download;
	public static String SmartSyncDialog_DownloadAll;
	public static String SmartSyncDialog_ErrorMessage;
	public static String SmartSyncDialog_ErrorPoolSize;
	public static String SmartSyncDialog_ErrorSmartSync;
	public static String SmartSyncDialog_ErrorSync;
	public static String SmartSyncDialog_Filter;
	public static String SmartSyncDialog_FlatView;
	public static String SmartSyncDialog_InitialPoolSize;
	public static String SmartSyncDialog_InSync;
	public static String SmartSyncDialog_LocalTime;
	public static String SmartSyncDialog_MaxPoolSize;
	public static String SmartSyncDialog_Modified;
	public static String SmartSyncDialog_New;
	public static String SmartSyncDialog_NewItems;
	public static String SmartSyncDialog_NumFilesToDelete;
	public static String SmartSyncDialog_NumFilesToSkip;
	public static String SmartSyncDialog_NumFilesToUpdate;
	public static String SmartSyncDialog_OptionsToolTip;
	public static String SmartSyncDialog_OverallProgress;
	public static String SmartSyncDialog_Overwrite;
	public static String SmartSyncDialog_PermForDirectories;
	public static String SmartSyncDialog_PermForFiles;
	public static String SmartSyncDialog_Permissions;
	public static String SmartSyncDialog_Presentation;
	public static String SmartSyncDialog_PreviewDescription;
	public static String SmartSyncDialog_RemoteTime;
	public static String SmartSyncDialog_Retry;
	public static String SmartSyncDialog_RunInBackground;
	public static String SmartSyncDialog_ShowDates;
	public static String SmartSyncDialog_ShowDiffs;
	public static String SmartSyncDialog_Skipped;
	public static String SmartSyncDialog_SkippedFilesInSync;
	public static String SmartSyncDialog_SkippedItems;
	public static String SmartSyncDialog_StartSync;
	public static String SmartSyncDialog_SyncError;
    public static String SmartSyncDialog_SyncInBackground;
	public static String SmartSyncDialog_SyncingFile; 
	public static String SmartSyncDialog_Title;
	public static String SmartSyncDialog_TreeView;
	public static String SmartSyncDialog_UnknownHostError;
	public static String SmartSyncDialog_Update;
	public static String SmartSyncDialog_UpdatedItems;
	public static String SmartSyncDialog_Upload;
	public static String SmartSyncDialog_UploadAll;
	public static String SmartSyncDialog_UseCrc;
	public static String SmartSyncDialog_ViewOptions;
	public static String SmartSyncDialog_SyncInProgressTitle;
	public static String SmartSyncDialog_SyncInProgress;
	public static String SmartSyncDialog_ContinueLabel;
			
    public static String SyncExplorerWidget_Clear;
    public static String SyncExplorerWidget_ColumnBytes;
    public static String SyncExplorerWidget_ColumnFile;
    public static String SyncExplorerWidget_ColumnFilename;
    public static String SyncExplorerWidget_ColumnFromEndpoint;
    public static String SyncExplorerWidget_ColumnFromFolder;
    public static String SyncExplorerWidget_ColumnLastModified;
    public static String SyncExplorerWidget_ColumnSize;
    public static String SyncExplorerWidget_ColumnStatus;
    public static String SyncExplorerWidget_ColumnToEndpoint;
    public static String SyncExplorerWidget_ColumnToFolder;
    public static String SyncExplorerWidget_DisplayFileSize;
    public static String SyncExplorerWidget_DisplayLastModified;
    public static String SyncExplorerWidget_Endpoint1;
    public static String SyncExplorerWidget_Endpoint2;
    public static String SyncExplorerWidget_HideTabs;
    public static String SyncExplorerWidget_Log;
    public static String SyncExplorerWidget_MenuColumns;
    public static String SyncExplorerWidget_Options;
    public static String SyncExplorerWidget_Path;
    public static String SyncExplorerWidget_SelectWarningLabel;
    public static String SyncExplorerWidget_ShowTabs;
    public static String SyncExplorerWidget_SmartSyncButton;
    public static String SyncExplorerWidget_SmartSyncLabel;
    public static String SyncExplorerWidget_StatusFailed;
    public static String SyncExplorerWidget_StatusRunning;
    public static String SyncExplorerWidget_StatusSuccess;
    public static String SyncExplorerWidget_StatusTransfers;
    public static String SyncExplorerWidget_StatusWaiting;

    public static String SyncManagerFileLabelProvider_StatusLoading;
    public static String SyncManagerFileLabelProvider_Unknown;
    
	public static String SmartSyncDialog_CommentLabel;
	public static String SmartSyncDialog_WhatIsThisLink;
	public static String SmartSyncDialog_HintComment;
	public static String SmartSyncEventManager_ERR_ErrorNotifyingSmartSyncListener;

	public static String SmartSyncEventManager_ERR_ExceptionNotifyingSmartSyncListener;

	public static String SmartSyncViewer_ColumnResourcesTooltip;
	public static String SmartSyncViewer_ColumnSkipTooltip;
	public static String SmartSyncViewer_LocalTimeTooltip;
	public static String SmartSyncViewer_RemoteTimeTooltip;
	
    static {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

    private Messages() {
    }
}
