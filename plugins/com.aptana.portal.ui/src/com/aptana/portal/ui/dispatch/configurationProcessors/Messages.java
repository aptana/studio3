package com.aptana.portal.ui.dispatch.configurationProcessors;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS
{
	private static final String BUNDLE_NAME = "com.aptana.portal.ui.dispatch.configurationProcessors.messages"; //$NON-NLS-1$
	public static String GemsConfigurationProcessor_errorInvokingGemList;
	public static String GemsConfigurationProcessor_missingShellError;
	public static String GemsConfigurationProcessor_wrongGemsRequest;
	public static String RubyInstallProcessor_missingRubyInstallURLs;
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
