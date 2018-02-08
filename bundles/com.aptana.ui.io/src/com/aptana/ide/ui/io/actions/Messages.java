/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.ide.ui.io.actions;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS
{

	private static final String BUNDLE_NAME = "com.aptana.ide.ui.io.actions.messages"; //$NON-NLS-1$

	public static String ConnectionPropertiesAction_FailedToCreate;

	public static String CopyFilesOperation_Copy_Subtask;
	public static String CopyFilesOperation_CopyJob_Title;
	public static String CopyFilesOperation_DefaultNewName;
	public static String CopyFilesOperation_DefaultNewName_WithCount;
	public static String CopyFilesOperation_DestinationNotAccessible;
	public static String CopyFilesOperation_ERR_DestinationInSource;
	public static String CopyFilesOperation_ERR_FailedToCopy;
	public static String CopyFilesOperation_ERR_FailedToCopyToDest;
	public static String CopyFilesOperation_ERR_NameConflict;
	public static String CopyFilesOperation_ERR_NameExists;
	public static String CopyFilesOperation_ERR_SourceInDestination;
	public static String CopyFilesOperation_NameConflictDialog_Message;
	public static String CopyFilesOperation_NameConflictDialog_Title;
	public static String CopyFilesOperation_OverwriteWarning;
	public static String CopyFilesOperation_OverwriteTitle;
	public static String CopyFilesOperation_Status_OK;

	public static String DeleteConnectionAction_Confirm_Message;
	public static String DeleteConnectionAction_Confirm_Title;
	public static String DeleteConnectionAction_Deleting;
	public static String DeleteConnectionAction_DeletingConnections;
	public static String DeleteConnectionAction_FailedToDisconnect;

	public static String DisconnectAction_Disconnecting;

	public static String MoveFilesOperation_ERR_FailedToMove;
	public static String MoveFilesOperation_Subtask_Moving;

	static
	{
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages()
	{
	}
}
