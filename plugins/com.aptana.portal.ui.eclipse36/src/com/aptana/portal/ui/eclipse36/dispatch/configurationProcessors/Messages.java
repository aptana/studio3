/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.portal.ui.eclipse36.dispatch.configurationProcessors;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS
{
	private static final String BUNDLE_NAME = "com.aptana.portal.ui.eclipse36.dispatch.configurationProcessors.messages"; //$NON-NLS-1$
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
