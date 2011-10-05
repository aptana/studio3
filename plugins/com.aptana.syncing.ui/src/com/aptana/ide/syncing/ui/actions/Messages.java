/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.ide.syncing.ui.actions;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS
{

	private static final String BUNDLE_NAME = "com.aptana.ide.syncing.ui.actions.messages"; //$NON-NLS-1$

	public static String BaseSyncAction_MessageTitle;
	public static String BaseSyncAction_RetrievingItems;
	public static String BaseSyncAction_Warning_NoCommonParent;

	public static String DownloadAction_ERR_FailToDownload;
	public static String DownloadAction_MessageTitle;
	public static String DownloadAction_PostMessage;

	public static String NewSiteAction_LBL_New;

	public static String SynchronizeAction_MessageTitle;
	public static String SynchronizeFilesAction_ERR_OpeningSyncDialog;
	public static String SynchronizeProjectAction_ERR_OpeningSyncDialog;

	public static String UploadAction_ERR_FailToUpload;
	public static String UploadAction_MessageTitle;
	public static String UploadAction_PostMessage;

	static
	{
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages()
	{
	}
}
