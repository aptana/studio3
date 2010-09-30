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
package com.aptana.filesystem.ftp.internal;

import org.eclipse.osgi.util.NLS;

/* package */ class Messages extends NLS {

	private static final String BUNDLE_NAME = "com.aptana.filesystem.ftp.internal.messages"; //$NON-NLS-1$

	public static String BaseFTPConnectionFileManager_symlink_resolve_failed;

	public static String BaseFTPConnectionFileManager_cant_move;
	public static String BaseFTPConnectionFileManager_connection_check_failed;
	public static String BaseFTPConnectionFileManager_creating_folder;
	public static String BaseFTPConnectionFileManager_creating_folders;
	public static String BaseFTPConnectionFileManager_deleting;
	public static String BaseFTPConnectionFileManager_file_already_exists;
	public static String BaseFTPConnectionFileManager_file_is_directory;
	public static String BaseFTPConnectionFileManager_gethering_details;
	public static String BaseFTPConnectionFileManager_listing_directory;
	public static String BaseFTPConnectionFileManager_moving;
	public static String BaseFTPConnectionFileManager_no_such_file;
	public static String BaseFTPConnectionFileManager_opening_file;
	public static String BaseFTPConnectionFileManager_parent_doesnt_exist;
	public static String BaseFTPConnectionFileManager_parent_is_not_directory;
	public static String BaseFTPConnectionFileManager_putting_changes;
	public static String BaseFTPConnectionFileManager_permission_denied;
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
