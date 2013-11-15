/**
 * Aptana Studio
 * Copyright (c) 2005-2013 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.usage;

import java.util.UUID;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.osgi.framework.BundleContext;
import org.osgi.service.prefs.BackingStoreException;

import com.aptana.core.logging.IdeLog;
import com.aptana.core.util.EclipseUtil;
import com.aptana.usage.internal.AnalyticsInfoManager;
import com.aptana.usage.internal.AptanaDB;
import com.aptana.usage.internal.SendPingJob;
import com.aptana.usage.preferences.IPreferenceConstants;

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

	private SendPingJob job;
	private AnalyticsInfoManager fAnalyticsInfoManager;

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
		job = new SendPingJob();
		job.schedule();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.core.runtime.Plugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception
	{
		try
		{
			if (job != null)
			{
				job.shutdown(); // tell job to stop, clean up and send end event
				job = null;
			}
			if (!Platform.inDevelopmentMode())
			{
				AptanaDB.getInstance().shutdown();
			}
		}
		finally
		{
			fAnalyticsInfoManager = null;
			plugin = null;
			super.stop(context);
		}
	}

	public static String getApplicationId()
	{
		String id = Platform.getPreferencesService().getString(PLUGIN_ID, IPreferenceConstants.P_IDE_ID, null, null);
		boolean save = false;
		if (id == null)
		{
			// see if there is an old id we could migrate
			id = Platform.getPreferencesService().getString(OLD_PLUGIN_ID, IPreferenceConstants.P_IDE_ID, null, null);
			if (id != null)
			{
				save = true;
			}
		}
		if (id == null)
		{
			id = UUID.randomUUID().toString();
			save = true;
		}
		if (save)
		{
			// saves the id in configuration scope so it's shared by all workspaces
			IEclipsePreferences prefs = EclipseUtil.configurationScope().getNode(PLUGIN_ID);
			prefs.put(IPreferenceConstants.P_IDE_ID, id);
			try
			{
				prefs.flush();
			}
			catch (BackingStoreException e)
			{
				logError(e);
			}
		}
		return id;
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

	public synchronized IAnalyticsInfoManager getAnalyticsInfoManager()
	{
		if (fAnalyticsInfoManager == null)
		{
			fAnalyticsInfoManager = new AnalyticsInfoManager();
		}
		return fAnalyticsInfoManager;
	}
}
