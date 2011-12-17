/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.portal.ui.dispatch.configurationProcessors;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS
{

	private static final String BUNDLE_NAME = "com.aptana.portal.ui.dispatch.configurationProcessors.messages"; //$NON-NLS-1$

	public static String GemsConfigurationProcessor_errorInvokingGemList;
	public static String GemsConfigurationProcessor_missingShellError;
	public static String GemsConfigurationProcessor_wrongGemsRequest;

	public static String ImportJavaScriptLibraryDialog_emptyPathError;
	public static String ImportJavaScriptLibraryDialog_folderSelectionDialogMessage;
	public static String ImportJavaScriptLibraryDialog_folderSelectionDialogTitle;
	public static String ImportJavaScriptLibraryDialog_invalidPathError;
	public static String ImportJavaScriptLibraryDialog_locationLabel;
	public static String ImportJavaScriptLibraryDialog_noAccessibleProjectsError;
	public static String ImportJavaScriptLibraryDialog_projectLable;
	public static String ImportJavaScriptLibraryDialog_useDefaultLocation;
	public static String ImportJavaScriptLibraryDialog_wrongProjectRootError;

	public static String InstallerConfigurationProcessor_emptyURLsArrayError;
	public static String InstallerConfigurationProcessor_expectedArrayError;
	public static String InstallerConfigurationProcessor_expectedMapError;
	public static String InstallerConfigurationProcessor_expectedURLsArrayError;
	public static String InstallerConfigurationProcessor_unableToExtractZip;

	public static String InstallProcessor_couldNotLocateInstaller;
	public static String InstallProcessor_corruptedZip;
	public static String InstallProcessor_couldNotLocatePackage;
	public static String InstallProcessor_errorWhileInstalling;
	public static String InstallProcessor_extractingPackageTaskName;
	public static String InstallProcessor_failedToInstallSeeLog;
	public static String InstallProcessor_failedToInstall;
	public static String InstallProcessor_failedToWrite;
	public static String InstallProcessor_installationError_installDirMissing;
	public static String InstallProcessor_installationErrorMessage;
	public static String InstallProcessor_installationErrorTitle;
	public static String InstallProcessor_installerGroupTitle;
	public static String InstallProcessor_installerMessage;
	public static String InstallProcessor_installerProgressInfo;
	public static String InstallProcessor_installerShellTitle;
	public static String InstallProcessor_installerTitle;
	public static String InstallProcessor_extractingJobName;
	public static String InstallProcessor_installingTaskName;
	public static String InstallProcessor_executingTaskName;
	public static String InstallProcessor_missingInstallURLs;
	public static String InstallProcessor_installationSuccessful;
	public static String InstallProcessor_installerJobName;
	public static String InstallProcessor_seeErrorLog;
	public static String InstallProcessor_updatingTaskName;
	public static String InstallProcessor_InstallForAllUsers;
	public static String InstallProcessor_wrongNumberOfInstallLinks;

	public static String InstallerConfigurationProcessor_missingAttributeMap;
	public static String InstallerConfigurationProcessor_missingDownloadTargets;
	public static String InstallerOptionsDialog_creatingDirectoriesErrorMessage;
	public static String InstallerOptionsDialog_creatingDirectoriesErrorTitle;
	public static String InstallerOptionsDialog_emptyPathError;
	public static String InstallerOptionsDialog_inputDirectoryWillBeCreated;
	public static String InstallerOptionsDialog_nonExistingPathError;

	public static String JSLibraryInstallProcessor_directoriesCreationFailed;
	public static String JSLibraryInstallProcessor_directorySelection;
	public static String JSLibraryInstallProcessor_fileConflictMessage;
	public static String JSLibraryInstallProcessor_fileConflictTitle;
	public static String JSLibraryInstallProcessor_multipleErrorsWhileImportingJS;
	public static String JSLibraryInstallProcessor_overwriteQuestion;
	public static String JSLibraryInstallProcessor_unexpectedNull;

	public static String InstallProcessor_aptanaInstallationComment;
	public static String SystemConfigurationProcessor_missingConfigurationItems;
	public static String SystemConfigurationProcessor_noShellCommandPath;
	public static String SystemConfigurationProcessor_wrongConfigurationAttributesStructure;
	public static String XAMPPInstallProcessor_executeXAMPPAutoSetup;
	public static String XAMPPInstallProcessor_openXAMPPConsoleJobName;

	static
	{
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages()
	{
	}
}
