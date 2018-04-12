/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.ide.syncing.ui.preferences;

/**
 * @author Michael Xia (mxia@aptana.com)
 */
public interface IPreferenceConstants
{

	/**
	 * Preference to not show the confirmation dialog after upload is completed
	 */
	public static final String IGNORE_DIALOG_FILE_UPLOAD = "IGNORE_DIALOG_FILE_UPLOAD"; //$NON-NLS-1$

	/**
	 * Preference to not show the confirmation dialog after download is completed
	 */
	public static final String IGNORE_DIALOG_FILE_DOWNLOAD = "IGNORE_DIALOG_FILE_DOWNLOAD"; //$NON-NLS-1$

	/**
	 * Stores the initial path the sync export/import wizard should use
	 */
	public static final String EXPORT_INITIAL_PATH = "EXPORT_INITIAL_PATH"; //$NON-NLS-1$

	/**
	 * Preference for the default behavior of overwriting the file when exporting the connection settings
	 */
	public static final String EXPORT_OVEWRITE_FILE_WITHOUT_WARNING = "OVEWRITE_FILE_WITHOUT_WARNING"; //$NON-NLS-1$

	/**
	 * VIEW_MODE
	 */
	static final String VIEW_MODE = "VIEW_MODE"; //$NON-NLS-1$

	/**
	 * FLAT
	 */
	static final String VIEW_FLAT = "FLAT"; //$NON-NLS-1$

	/**
	 * TREE
	 */
	static final String VIEW_TREE = "TREE"; //$NON-NLS-1$

	/**
	 * DIRECTION_MODE
	 */
	static final String DIRECTION_MODE = "DIRECTION_MODE"; //$NON-NLS-1$

	/**
	 * DIRECTION_BOTH
	 */
	static final String DIRECTION_BOTH = "BOTH"; //$NON-NLS-1$

	/**
	 * DIRECTION_UPLOAD
	 */
	static final String DIRECTION_UPLOAD = "UPLOAD"; //$NON-NLS-1$

	/**
	 * DIRECTION_DOWNLOAD
	 */
	static final String DIRECTION_DOWNLOAD = "DOWNLOAD"; //$NON-NLS-1$

	/**
	 * DIRECTION_FORCE_UPLOAD
	 */
	static final String DIRECTION_FORCE_UPLOAD = "FORCE_UPLOAD"; //$NON-NLS-1$

	/**
	 * DIRECTION_FORCE_DOWNLOAD
	 */
	static final String DIRECTION_FORCE_DOWNLOAD = "FORCE_DOWNLOAD"; //$NON-NLS-1$

	/**
	 * DELETE_REMOTE_FILES
	 */
	static final String DELETE_REMOTE_FILES = "DELETE_REMOTE_FILES"; //$NON-NLS-1$

	/**
	 * DELETE_LOCAL_FILES
	 */
	static final String DELETE_LOCAL_FILES = "DELETE_LOCAL_FILES"; //$NON-NLS-1$

	/**
	 * SHOW_MODIFICATION_TIME
	 */
	static final String SHOW_MODIFICATION_TIME = "SHOW_MODIFICATION_TIME"; //$NON-NLS-1$

	/**
	 * COMPARE_IN_BACKGROUND
	 */
	static final String COMPARE_IN_BACKGROUND = "COMPARE_IN_BACKGROUND"; //$NON-NLS-1$

	/**
	 * USE_CRC
	 */
	static final String USE_CRC = "USE_CRC"; //$NON-NLS-1$

	/**
	 * Pref key for uploading the editor on save
	 */
	public static final String AUTO_SYNC = "AutoSyncChangesWithRemote"; //$NON-NLS-1$

	public enum SyncDirection
	{
		UPLOAD, DOWNLOAD, BOTH
	};

	public static final String AUTO_SYNC_DIRECTION = "AutoSyncDirection"; //$NON-NLS-1$
}
