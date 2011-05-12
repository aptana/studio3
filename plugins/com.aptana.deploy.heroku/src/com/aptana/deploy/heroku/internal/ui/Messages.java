/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.deploy.heroku.internal.ui;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS
{
	private static final String BUNDLE_NAME = "com.aptana.deploy.heroku.internal.ui.messages"; //$NON-NLS-1$

	public static String HerokuContributionItem_OpenBrowserItem;
	public static String HerokuContributionItem_SharingSubmenuLabel;
	public static String HerokuContributionItem_DatabaseSubmenuLabel;
	public static String HerokuContributionItem_RemoteSubmenuLabel;
	public static String HerokuContributionItem_ConfigVarsSubmenuLabel;
	public static String HerokuContributionItem_MaintenanceSubmenuLabel;

	static
	{
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages()
	{
	}
}
