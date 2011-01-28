/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.git.ui.internal.sharing;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS
{
	private static final String BUNDLE_NAME = "com.aptana.git.ui.internal.sharing.messages"; //$NON-NLS-1$
	public static String ConnectProviderOperation_ConnectingProjectJob_Title;
	public static String ConnectProviderOperation_ConnectingProviderJob_Title;
	public static String ExistingOrNewPage_CreateButton_Label;
	public static String ExistingOrNewPage_Description;
	public static String ExistingOrNewPage_ErrorFailedToRefreshRepository;
	public static String ExistingOrNewPage_PathColumn_Label;
	public static String ExistingOrNewPage_ProjectColumn_Label;
	public static String ExistingOrNewPage_RepositoryColumn_Label;
	public static String ExistingOrNewPage_Title;
	public static String ExistingOrNewPage_UnabletoFindGitExecutableError;
	public static String SharingWizard_failed;
	public static String SharingWizard_Title;
	static
	{
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages()
	{
	}
}
