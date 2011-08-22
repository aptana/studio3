/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.ide.syncing.ui.views;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS
{

	private static final String BUNDLE_NAME = "com.aptana.ide.syncing.ui.views.messages"; //$NON-NLS-1$

	public static String ConnectionPointComposite_Column_Filename;
	public static String ConnectionPointComposite_Column_LastModified;
	public static String ConnectionPointComposite_Column_Size;
	public static String ConnectionPointComposite_LBL_Path;
	public static String ConnectionPointComposite_LBL_Transfer;
	public static String ConnectionPointComposite_TTP_Home;
	public static String ConnectionPointComposite_TTP_Refresh;

	public static String FTPManagerComposite_ERR_CreateNewSiteFailed;
	public static String FTPManagerComposite_ERR_EmptyName;
	public static String FTPManagerComposite_ERR_NameExists;
	public static String FTPManagerComposite_LBL_SaveAs;
	public static String FTPManagerComposite_LBL_Sites;
	public static String FTPManagerComposite_LBL_Source;
	public static String FTPManagerComposite_LBL_Target;
	public static String FTPManagerComposite_NameInput_Message;
	public static String FTPManagerComposite_NameInput_Title;
	public static String FTPManagerComposite_SyncErrorDialog_Message;
	public static String FTPManagerComposite_SyncErrorDialog_Title;
	public static String FTPManagerComposite_TTP_Edit;
	public static String FTPManagerComposite_TTP_SaveAs;
	public static String FTPManagerComposite_TTP_Synchronize;
	public static String FTPManagerComposite_TTP_TransferLeft;
	public static String FTPManagerComposite_TTP_TransferRight;

	static
	{
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages()
	{
	}
}
