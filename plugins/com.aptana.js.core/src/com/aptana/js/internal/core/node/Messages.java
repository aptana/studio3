/**
 * Aptana Studio
 * Copyright (c) 2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.js.internal.core.node;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS
{
	private static final String BUNDLE_NAME = "com.aptana.js.internal.core.node.messages"; //$NON-NLS-1$

	public static String NodeJSService_BadURLError;
	public static String NodeJSService_CannotInstallOnLinuxMsg;
	public static String NodeJSService_CouldntGetVersionError;
	public static String NodeJSService_EmptySourcePath;
	public static String NodeJSService_FileDoesntExistError;
	public static String NodeJSService_InstallFailedError;
	public static String NodeJSService_InstallingJobTitle;
	public static String NodeJSService_InstallPrompt;
	public static String NodeJSService_InvalidLocation_0;
	public static String NodeJSService_InvalidVersionError;
	public static String NodeJSService_NoDirectory_0;
	public static String NodeJSService_NullPathError;

	public static String NodePackageManager_ConfigFailure;
	public static String NodePackageManager_ERR_NPMNotInstalled;
	public static String NodePackageManager_FailedInstallError;
	public static String NodePackageManager_FailedListingError;
	public static String NodePackageManager_FailedListPackageError;
	public static String NodePackageManager_FailedToDetermineInstalledVersion;
	public static String NodePackageManager_FailedToDetermineLatestVersion;
	public static String NodePackageManager_FailedUninstallError;
	public static String NodePackageManager_InstallingTaskName;
	public static String NodePackageManager_SudoPrompt;

	static
	{
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages()
	{
	}
}
