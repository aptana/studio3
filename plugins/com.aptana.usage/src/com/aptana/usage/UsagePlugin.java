/**
 * Aptana Studio
 * Copyright (c) 2005-2013 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.usage;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.BundleContext;

import com.aptana.core.logging.IdeLog;
import com.aptana.core.util.EclipseUtil;

/**
 * The activator class controls the plug-in life cycle
 */
public class UsagePlugin extends Plugin
{

	// The plug-in ID
	public static final String PLUGIN_ID = "com.aptana.usage"; //$NON-NLS-1$
	// this is the incorrect id previously used; DO NOT USE it for future reference
	public static final String OLD_PLUGIN_ID = "com.aptana.db"; //$NON-NLS-1$

	// Events (since this is migrated from TiStudio, we will keep the constants as ti.*)
	private static final String STUDIO_START = "ti.start"; //$NON-NLS-1$
	private static final String STUDIO_END = "ti.end"; //$NON-NLS-1$

	// The shared instance
	private static UsagePlugin plugin;

	/**
	 * The constructor
	 */
	public UsagePlugin()
	{
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
		// Send ping when we exit studio
		if (!EclipseUtil.isTesting())
		{
			StudioAnalytics.getInstance().sendEvent(new AnalyticsEvent(STUDIO_END, STUDIO_END, null));
		}
		if (!Platform.inDevelopmentMode())
		{
			AptanaDB.getInstance().shutdown();
		}
		PingStartup.removeResourceListener();
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

	public static void logError(String message)
	{
		// Only logs analytics errors when in development
		if (Platform.inDevelopmentMode())
		{
			IdeLog.logError(getDefault(), message);
		}
	}

	public static void logError(Exception e)
	{
		// Only logs analytics errors when in development
		if (Platform.inDevelopmentMode())
		{
			IdeLog.logError(getDefault(), e);
		}
	}
}
