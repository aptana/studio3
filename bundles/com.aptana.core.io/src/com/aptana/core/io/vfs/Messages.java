/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.core.io.vfs;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS // NO_UCD
{

	private static final String BUNDLE_NAME = "com.aptana.core.io.vfs.messages"; //$NON-NLS-1$

	public static String VirtualConnectionManager_NoMatchingConnectionForURI; // NO_UCD

	public static String BaseConnectionFileManager_symlink_resolve_failed;

	public static String BaseConnectionFileManager_cant_move;
	public static String BaseConnectionFileManager_creating_folder;
	public static String BaseConnectionFileManager_creating_folders;
	public static String BaseConnectionFileManager_deleting;

	public static String BaseConnectionFileManager_failed_change_directory;

	public static String BaseConnectionFileManager_file_already_exists;
	public static String BaseConnectionFileManager_file_is_directory;
	public static String BaseConnectionFileManager_gethering_details;

	public static String BaseConnectionFileManager_listing_directory;
	public static String BaseConnectionFileManager_moving;
	public static String BaseConnectionFileManager_no_such_file;
	public static String BaseConnectionFileManager_opening_file;
	public static String BaseConnectionFileManager_parent_doesnt_exist;
	public static String BaseConnectionFileManager_parent_is_not_directory;

	public static String BaseConnectionFileManager_PermissionDenied0;
	public static String BaseConnectionFileManager_putting_changes;

	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
