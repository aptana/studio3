package com.aptana.portal.ui.eclipse35.dispatch.configurationProcessors;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS
{
	private static final String BUNDLE_NAME = "com.aptana.portal.ui.eclipse35.dispatch.configurationProcessors.messages"; //$NON-NLS-1$
	public static String PluginsConfigurationProcessor_artifactRepoNotFound;
	public static String PluginsConfigurationProcessor_cannotFindInstallationUnits;
	public static String PluginsConfigurationProcessor_locatingFeatures;
	public static String PluginsConfigurationProcessor_metadataRepoNotFound;
	public static String PluginsConfigurationProcessor_missingPluginNames;
	public static String PluginsConfigurationProcessor_wrongAttributesForConfigure;
	public static String PluginsConfigurationProcessor_wrongPluginDefinitionRequest;
	static
	{
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages()
	{
	}
}
