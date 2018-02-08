/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */

package com.aptana.ui.ftp.dialogs;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {

	private static final String BUNDLE_NAME = "com.aptana.ui.ftp.dialogs.messages"; //$NON-NLS-1$

	public static String FTPConnectionPointPropertyDialog_ConfirmMessage;
	public static String FTPConnectionPointPropertyDialog_ConfirmTitle;
	public static String FTPConnectionPointPropertyDialog_DefaultErrorMsg;
	public static String FTPConnectionPointPropertyDialog_ERR_FailedCreate;
	public static String FTPConnectionPointPropertyDialog_ERR_InvalidHost;
	public static String FTPConnectionPointPropertyDialog_ERR_NameEmpty;
	public static String FTPConnectionPointPropertyDialog_ERR_NoUsername;
	public static String FTPConnectionPointPropertyDialog_ERR_NameExists;
	public static String FTPConnectionPointPropertyDialog_ErrorTitle;
	public static String FTPConnectionPointPropertyDialog_LBL_Edit;
	public static String FTPConnectionPointPropertyDialog_LBL_Example;
	public static String FTPConnectionPointPropertyDialog_LBL_GroupInfo;
	public static String FTPConnectionPointPropertyDialog_LBL_Options;
	public static String FTPConnectionPointPropertyDialog_LBL_Password;
	public static String FTPConnectionPointPropertyDialog_LBL_RemotePath;
	public static String FTPConnectionPointPropertyDialog_LBL_Save;
	public static String FTPConnectionPointPropertyDialog_LBL_Server;
	public static String FTPConnectionPointPropertyDialog_LBL_SiteName;
	public static String FTPConnectionPointPropertyDialog_LBL_Test;
	public static String FTPConnectionPointPropertyDialog_LBL_Username;
	public static String FTPConnectionPointPropertyDialog_Message_Browse;
	public static String FTPConnectionPointPropertyDialog_MessageTitle_Edit;
	public static String FTPConnectionPointPropertyDialog_MessageTitle_New;
	public static String FTPConnectionPointPropertyDialog_Succeed_Message;
	public static String FTPConnectionPointPropertyDialog_Succeed_Title;
	public static String FTPConnectionPointPropertyDialog_Task_Browse;
	public static String FTPConnectionPointPropertyDialog_Title;
	public static String FTPConnectionPointPropertyDialog_Title_Browse;
	public static String FTPConnectionPointPropertyDialog_Title_Edit;
	public static String FTPConnectionPointPropertyDialog_Title_New;

	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
