/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.usage;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;

/**
 * The activator class controls the plug-in life cycle
 */
public class UsagePlugin extends Plugin
{

	// The plug-in ID
	public static final String PLUGIN_ID = "com.aptana.usage"; //$NON-NLS-1$
	// this is the incorrect id previously used; DO NOT USE it for future reference
	public static final String OLD_PLUGIN_ID = "com.aptana.db"; //$NON-NLS-1$

	// The shared instance
	private static UsagePlugin plugin;
	
	/**
	 * The constructor
	 */
	public UsagePlugin() {
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.core.runtime.Plugins#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception
	{
		super.start(context);
		plugin = this;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.core.runtime.Plugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception
	{
		if (!Platform.inDevelopmentMode())
		{
			AptanaDB.getInstance().shutdown();
		}
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static UsagePlugin getDefault()
	{
		return plugin;
	}

	/**
	 * Retrieves the plug-in's version.
	 * 
	 * @return the plug-in's version or null if it could not be retrieved
	 */
	public static String getPluginVersion()
	{
		Bundle bundle = getDefault().getBundle();
		if (bundle == null)
		{
			return null;
		}
		return bundle.getHeaders().get(Constants.BUNDLE_VERSION).toString();
	}
}
