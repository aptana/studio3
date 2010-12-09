/**
 * This file Copyright (c) 2005-2010 Aptana, Inc. This program is
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
package com.aptana.ide.syncing.ui.old.views;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS
{

	private static final String BUNDLE_NAME = "com.aptana.ide.syncing.ui.old.views.messages"; //$NON-NLS-1$

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
	public static String SmartSyncDialog_Delete;
	public static String SmartSyncDialog_DeleteExtra;
	public static String SmartSyncDialog_DeleteExtraTooltip;
	public static String SmartSyncDialog_DirectionMode;
	public static String SmartSyncDialog_Download;
	public static String SmartSyncDialog_DownloadAll;
	public static String SmartSyncDialog_Endpoints;

	public static String SmartSyncDialog_ErrorMessage;
	public static String SmartSyncDialog_ErrorSmartSync;
	public static String SmartSyncDialog_ErrorSync;
	public static String SmartSyncDialog_FlatView;
	public static String SmartSyncDialog_InSync;
	public static String SmartSyncDialog_LocalTime;
	public static String SmartSyncDialog_Modified;
	public static String SmartSyncDialog_New;
	public static String SmartSyncDialog_NumFilesToDelete;
	public static String SmartSyncDialog_NumFilesToSkip;
	public static String SmartSyncDialog_NumFilesToUpdate;
	public static String SmartSyncDialog_OptionsToolTip;
	public static String SmartSyncDialog_Overwrite;
	public static String SmartSyncDialog_PermForDirectories;
	public static String SmartSyncDialog_PermForFiles;
	public static String SmartSyncDialog_Permissions;
	public static String SmartSyncDialog_PreviewDescription;
	public static String SmartSyncDialog_RemoteTime;
	public static String SmartSyncDialog_Retry;
	public static String SmartSyncDialog_RunInBackground;

	public static String SmartSyncDialog_searchText;
	public static String SmartSyncDialog_ShowDates;
	public static String SmartSyncDialog_ShowDiffs;
	public static String SmartSyncDialog_Skipped;
	public static String SmartSyncDialog_SkippedFilesInSync;
	public static String SmartSyncDialog_StartSync;
	public static String SmartSyncDialog_SyncError;
	public static String SmartSyncDialog_SyncInBackground;
	public static String SmartSyncDialog_Title;
	public static String SmartSyncDialog_TreeView;
	public static String SmartSyncDialog_UnknownHostError;
	public static String SmartSyncDialog_Update;
	public static String SmartSyncDialog_Upload;
	public static String SmartSyncDialog_UploadAll;
	public static String SmartSyncDialog_UseCrc;
	public static String SmartSyncDialog_ViewOptions;

	public static String SmartSyncEventManager_ERR_ErrorNotifyingSmartSyncListener;

	public static String SmartSyncEventManager_ERR_ExceptionNotifyingSmartSyncListener;

	public static String SmartSyncViewer_ColumnResourcesTooltip;
	public static String SmartSyncViewer_ColumnSkipTooltip;
	public static String SmartSyncViewer_LocalTimeTooltip;
	public static String SmartSyncViewer_RemoteTimeTooltip;

	static
	{
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages()
	{
	}
}
