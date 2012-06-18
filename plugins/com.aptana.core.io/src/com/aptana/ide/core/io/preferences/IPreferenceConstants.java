/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.ide.core.io.preferences;

public interface IPreferenceConstants
{

	/**
	 * Preference for if the permissions should be updated when a file or folder is uploaded
	 */
	public static final String UPLOAD_UPDATE_PERMISSIONS = "UploadUpdatePermissions"; //$NON-NLS-1$

	/**
	 * Preference for if specific permissions should be applied when a file or folder is uploaded
	 */
	public static final String UPLOAD_SPECIFIC_PERMISSIONS = "UploadSpecificPermissions"; //$NON-NLS-1$

	/**
	 * Preference for the specific permissions to apply when a file is uploaded
	 */
	public static final String UPLOAD_FILE_PERMISSION = "FilePermission"; //$NON-NLS-1$

	/**
	 * Preference for the specific permissions to apply when a folder is uploaded
	 */
	public static final String UPLOAD_FOLDER_PERMISSION = "DirectoryPermission"; //$NON-NLS-1$

	/**
	 * Preference for if the permissions should be updated when a file or folder is uploaded
	 */
	public static final String DOWNLOAD_UPDATE_PERMISSIONS = "DownloadUpdatePermissions"; //$NON-NLS-1$

	/**
	 * Preference for if specific permissions should be applied when a file or folder is uploaded
	 */
	public static final String DOWNLOAD_SPECIFIC_PERMISSIONS = "DownloadSpecificPermissions"; //$NON-NLS-1$

	/**
	 * Preference for the specific permissions to apply when a file is uploaded
	 */
	public static final String DOWNLOAD_FILE_PERMISSION = "DownloadFilePermission"; //$NON-NLS-1$

	/**
	 * Preference for the specific permissions to apply when a folder is uploaded
	 */
	public static final String DOWNLOAD_FOLDER_PERMISSION = "DownloadDirectoryPermission"; //$NON-NLS-1$

	/**
	 * Preference for which files to be cloaked by default during file transfer operations
	 */
	public static final String GLOBAL_CLOAKING_EXTENSIONS = "GLOBAL_CLOAKING_EXTENSIONS"; //$NON-NLS-1$
}
