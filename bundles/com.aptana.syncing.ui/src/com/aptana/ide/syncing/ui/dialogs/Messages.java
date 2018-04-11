/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */

package com.aptana.ide.syncing.ui.dialogs;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS
{

	private static final String BUNDLE_NAME = "com.aptana.ide.syncing.ui.dialogs.messages"; //$NON-NLS-1$

	public static String ChooseSiteConnectionDialog_LBL_Connection;
	public static String ChooseSiteConnectionDialog_LBL_Message;
	public static String ChooseSiteConnectionDialog_LBL_PropertyPage;
	public static String ChooseSiteConnectionDialog_LBL_RememberMyDecision;
	public static String ChooseSiteConnectionDialog_Title;

	public static String SiteConnectionsEditorDialog_DeleteConfirm_Message;
	public static String SiteConnectionsEditorDialog_DeleteConfirm_Title;
	public static String SiteConnectionsEditorDialog_DialogTitle;
	public static String SiteConnectionsEditorDialog_ERR_Duplicate;
	public static String SiteConnectionsEditorDialog_LBL_ConnectionGroup;
	public static String SiteConnectionsEditorDialog_LBL_Duplicate;
	public static String SiteConnectionsEditorDialog_LBL_NewConnection;
	public static String SiteConnectionsEditorDialog_Message;
	public static String SiteConnectionsEditorDialog_SaveConfirm_Message;
	public static String SiteConnectionsEditorDialog_SaveConfirm_Title;
	public static String SiteConnectionsEditorDialog_Title;
	public static String SiteConnectionsEditorDialog_UnresolvedWarning_Message;
	public static String SiteConnectionsEditorDialog_UnresolvedWarning_Title;

	static
	{
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages()
	{
	}
}
