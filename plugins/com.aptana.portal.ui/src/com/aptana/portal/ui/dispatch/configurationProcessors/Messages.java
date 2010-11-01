/**
 * This file Copyright (c) 2005-2010 Aptana, Inc. This program is
 * dual-licensed under both the Aptana Public License and the GNU General
 * Public license. You may elect to use one or the other of these licenses.
 * 
 * This program is distributed in the hope that it will be useful, but
 * AS-IS and WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE, TITLE, or
 * NONINFRINGEMENT. Redistribution, except as permitted by whichever of
 * the GPL or APL you select, is prohibited.
 *
 * 1. For the GPL license (GPL), you can redistribute and/or modify this
 * program under the terms of the GNU General Public License,
 * Version 3, as published by the Free Software Foundation.  You should
 * have received a copy of the GNU General Public License, Version 3 along
 * with this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 * 
 * Aptana provides a special exception to allow redistribution of this file
 * with certain other free and open source software ("FOSS") code and certain additional terms
 * pursuant to Section 7 of the GPL. You may view the exception and these
 * terms on the web at http://www.aptana.com/legal/gpl/.
 * 
 * 2. For the Aptana Public License (APL), this program and the
 * accompanying materials are made available under the terms of the APL
 * v1.0 which accompanies this distribution, and is available at
 * http://www.aptana.com/legal/apl/.
 * 
 * You may view the GPL, Aptana's exception and additional terms, and the
 * APL in the file titled license.html at the root of the corresponding
 * plugin containing this source file.
 * 
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
	public static String InstallProcessor_browse;
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
