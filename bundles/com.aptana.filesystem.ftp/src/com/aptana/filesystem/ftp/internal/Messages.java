/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.filesystem.ftp.internal;

import org.eclipse.osgi.util.NLS;

/* package */class Messages extends NLS {

	private static final String BUNDLE_NAME = "com.aptana.filesystem.ftp.internal.messages"; //$NON-NLS-1$

	public static String BaseFTPConnectionFileManager_connection_check_failed;

	public static String BaseFTPConnectionFileManager_ErrorDetectOwnerGroup;

	public static String BaseFTPConnectionFileManager_GetheringServerDetails;
	public static String FTPConnectionFileManager_already_initialized;
	public static String FTPConnectionFileManager_authenticating;
	public static String FTPConnectionFileManager_closing_connection;
	public static String FTPConnectionFileManager_connecting;
	public static String FTPConnectionFileManager_connection_failed;

	public static String FTPConnectionFileManager_CreateFile0Failed;
	public static String FTPConnectionFileManager_creating_directory_failed;
	public static String FTPConnectionFileManager_deleting_directory_failed;
	public static String FTPConnectionFileManager_deleting_failed;
	public static String FTPConnectionFileManager_disconnect_failed;
	public static String FTPConnectionFileManager_establishing_connection;
	public static String FTPConnectionFileManager_FailedAuthenticate;
	public static String FTPConnectionFileManager_FailedSetGroup;
	public static String FTPConnectionFileManager_FailedSetPermissions;
	public static String FTPConnectionFileManager_fetch_failed;
	public static String FTPConnectionFileManager_fetching_directory_failed;
	public static String FTPConnectionFileManager_ftp_auth;
	public static String FTPConnectionFileManager_gethering_file_details;
	public static String FTPConnectionFileManager_gethering_server_info;
	public static String FTPConnectionFileManager_HostNameNotFound;
	public static String FTPConnectionFileManager_initialization_failed;
	public static String FTPConnectionFileManager_initiating_download;
	public static String FTPConnectionFileManager_initiating_file_upload;
	public static String FTPConnectionFileManager_invalid_password;
	public static String FTPConnectionFileManager_listing_directory_failed;
	public static String FTPConnectionFileManager_not_initialized;
	public static String FTPConnectionFileManager_opening_file_write_failed;
	public static String FTPConnectionFileManager_opening_file_read_failed;
	public static String FTPConnectionFileManager_RemoteFolderNotFound;
	public static String FTPConnectionFileManager_renaming_failed;
	public static String FTPConnectionFileManager_server_tz_check;
	public static String FTPConnectionFileManager_set_modification_time_failed;
	public static String FTPConnectionFileManager_specify_password;

	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
