package com.aptana.portal.ui.dispatch.configurationProcessors;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS
{
	private static final String BUNDLE_NAME = "com.aptana.portal.ui.dispatch.configurationProcessors.messages"; //$NON-NLS-1$
	public static String GemsConfigurationProcessor_errorInvokingGemList;
	public static String GemsConfigurationProcessor_missingShellError;
	public static String GemsConfigurationProcessor_wrongGemsRequest;
	public static String RubyInstallProcessor_browse;
	public static String RubyInstallProcessor_corruptedDevKitZip;
	public static String RubyInstallProcessor_couldNotLocateDevKit;
	public static String RubyInstallProcessor_couldNotLocateRubyinstaller;
	public static String RubyInstallProcessor_failedToinstallDevKit;
	public static String RubyInstallProcessor_failedToInstallRuby;
	public static String RubyInstallProcessor_failedToLock;
	public static String RubyInstallProcessor_failedToWrite;
	public static String RubyInstallProcessor_installationError_generic;
	public static String RubyInstallProcessor_installationError_installDirMissing;
	public static String RubyInstallProcessor_installationErrorMessage;
	public static String RubyInstallProcessor_installationErrorTitle;
	public static String RubyInstallProcessor_installerGroupTitle;
	public static String RubyInstallProcessor_installerMessage;
	public static String RubyInstallProcessor_installerProgressInfo;
	public static String RubyInstallProcessor_installerShellTitle;
	public static String RubyInstallProcessor_installerTitle;
	public static String RubyInstallProcessor_installingDevKitJobName;
	public static String RubyInstallProcessor_installingRubyTask;
	public static String RubyInstallProcessor_installingRubyTaskName;
	public static String RubyInstallProcessor_missingRubyInstallURLs;
	public static String RubyInstallProcessor_rubyInstallerJobName;
	public static String RubyInstallProcessor_seeErrorLog;
	public static String RubyInstallProcessor_unableToExtractDevKit;
	public static String RubyInstallProcessor_wrongNumberOfRubyInstallLinks;
	public static String SystemConfigurationProcessor_missingConfigurationItems;
	public static String SystemConfigurationProcessor_noShellCommandPath;
	public static String SystemConfigurationProcessor_wrongConfigurationAttributesStructure;
	static
	{
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages()
	{
	}
}
