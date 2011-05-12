/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.deploy.engineyard.ui;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS
{
	private static final String BUNDLE_NAME = "com.aptana.deploy.engineyard.ui.messages"; //$NON-NLS-1$

	public static String EngineYardContributionItem_OpenSSHSubmenuLabel;
	public static String EngineYardContributionItem_RecipesSubmenuLabel;
	public static String EngineYardContributionItem_DeploymentSubmenuLabel;
	public static String EngineYardContributionItem_MaintenanceSubmenuLabel;

	static
	{
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages()
	{
	}
}
