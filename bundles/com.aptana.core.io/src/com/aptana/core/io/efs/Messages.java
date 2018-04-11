/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.core.io.efs;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS // NO_UCD
{

	private static final String BUNDLE_NAME = "com.aptana.core.io.efs.messages"; //$NON-NLS-1$

	public static String VirtualFile_ListingDirectory; // NO_UCD
	public static String VirtualFileSystem_ERR_FetchFileTree; // NO_UCD
	public static String SyncUtils_Copying;
	public static String SyncUtils_ERR_FailToClose;
	public static String SyncUtils_ERR_Reading;
	public static String SyncUtils_ERR_Writing;

	public static String WorkspaceFileSystem_FetchingTreeError;

	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
