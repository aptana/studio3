/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.ide.syncing.ui.navigator.actions;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS
{

	private static final String BUNDLE_NAME = "com.aptana.ide.syncing.ui.navigator.actions.messages"; //$NON-NLS-1$

	public static String DoubleClickAction_NewConnection;

	public static String NavigatorDownloadAction_LBL_Download;

	public static String NavigatorSynchronizeAction_LBL_Synchronize;

	public static String NavigatorUploadAction_LBL_Upload;

	public static String ProjectConnectionDisconnectAction_Disconnecting;

	public static String RemoteConnectionManagerAction_LBL_ConnectionManager;

	static
	{
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages()
	{
	}
}
