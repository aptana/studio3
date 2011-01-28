/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.ide.syncing.ui.old.views;

import com.aptana.filesystem.ftp.Policy;
import com.aptana.ide.syncing.ui.SyncingUIPlugin;
import com.aptana.ide.syncing.ui.preferences.IPreferenceConstants;

/**
 * @author Michael Xia (mxia@aptana.com)
 */
public class FilePrefUtils
{

	/**
	 * Returns the permissions for new directory.
	 * 
	 * @return the permissions for new directory
	 */
	public static long getDirectoryPermission()
	{
		return Policy.permissionsFromString(SyncingUIPlugin.getDefault().getPreferenceStore().getString(
				IPreferenceConstants.DIRECTORY_PERMISSION));
	}

	/**
	 * Returns the permissions for new file.
	 * 
	 * @return the permissions for new file
	 */
	public static long getFilePermission()
	{
		return Policy.permissionsFromString(SyncingUIPlugin.getDefault().getPreferenceStore().getString(
				IPreferenceConstants.FILE_PERMISSION));
	}

}
