package com.aptana.ide.filesystem.secureftp;

import org.eclipse.osgi.util.NLS;

/* package */ class Messages extends NLS {
	private static final String BUNDLE_NAME = "com.aptana.ide.filesystem.secureftp.messages"; //$NON-NLS-1$
	public static String FTPConnectionFileManager_already_initialized;
	public static String FTPConnectionFileManager_authenticating;
	public static String FTPConnectionFileManager_checking_connection;
	public static String FTPConnectionFileManager_closing_connection;
	public static String FTPConnectionFileManager_connecting;
	public static String FTPConnectionFileManager_connection_failed;
	public static String FTPConnectionFileManager_creating_directory_failed;
	public static String FTPConnectionFileManager_deleting_directory_failed;
	public static String FTPConnectionFileManager_deleting_failed;
	public static String FTPConnectionFileManager_disconnect_failed;
	public static String FTPConnectionFileManager_establishing_connection;
	public static String FTPConnectionFileManager_fetch_failed;
	public static String FTPConnectionFileManager_fetching_directory_failed;
	public static String FTPConnectionFileManager_ftp_auth;
	public static String FTPConnectionFileManager_gethering_file_details;
	public static String FTPConnectionFileManager_gethering_server_info;
	public static String FTPConnectionFileManager_initialization_failed;
	public static String FTPConnectionFileManager_initiating_download;
	public static String FTPConnectionFileManager_initiating_file_upload;
	public static String FTPConnectionFileManager_invalid_password;
	public static String FTPConnectionFileManager_listing_directory_failed;
	public static String FTPConnectionFileManager_not_initialized;
	public static String FTPConnectionFileManager_opening_file_failed;
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
