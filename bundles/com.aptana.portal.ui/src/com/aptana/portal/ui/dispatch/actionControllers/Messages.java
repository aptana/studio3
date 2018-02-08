/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.portal.ui.dispatch.actionControllers;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS
{
	private static final String BUNDLE_NAME = "com.aptana.portal.ui.dispatch.actionControllers.messages"; //$NON-NLS-1$
	public static String InstallActionController_installing;
	public static String AbstractActionController_invocationError;
	public static String GemsActionController_computingGemsJobName;
	public static String ActionController_internalError;
	public static String ConsoleController_devToolboxConsoleName;
	public static String LaunchActionController_launchingJob;
	public static String PluginsActionController_computingInstalledPlugins;
	public static String PluginsActionController_installNewSoftware;
	static
	{
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages()
	{
	}
}
