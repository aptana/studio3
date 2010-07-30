package com.aptana.portal.ui.dispatch.configurationProcessors;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS
{
	private static final String BUNDLE_NAME = "com.aptana.portal.ui.dispatch.configurationProcessors.messages"; //$NON-NLS-1$
	public static String GemsConfigurationProcessor_errorInvokingGemList;
	public static String GemsConfigurationProcessor_missingShellError;
	public static String GemsConfigurationProcessor_wrongGemsRequest;
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
	public static String InstallProcessor_wrongNumberOfInstallLinks;
	public static String RubyInstallProcessor_aptanaFileRubyComment;
	public static String SystemConfigurationProcessor_missingConfigurationItems;
	public static String SystemConfigurationProcessor_noShellCommandPath;
	public static String SystemConfigurationProcessor_wrongConfigurationAttributesStructure;
	public static String XAMPPInstallProcessor_missingXAMPPInstallURLs;
	static
	{
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages()
	{
	}
}
