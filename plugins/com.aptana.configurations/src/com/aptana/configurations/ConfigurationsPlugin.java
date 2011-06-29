/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.configurations;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.BundleContext;

import com.aptana.core.logging.IdeLog;

/**
 * The activator class controls the plug-in life cycle
 */
public class ConfigurationsPlugin extends Plugin
{

	// The plug-in ID
	public static final String PLUGIN_ID = "com.aptana.configurations"; //$NON-NLS-1$

	// The shared instance
	private static ConfigurationsPlugin plugin;

	/**
	 * The constructor
	 */
	public ConfigurationsPlugin()
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
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 * 
	 * @return the shared instance
	 */
	public static ConfigurationsPlugin getDefault()
	{
		return plugin;
	}

	/**
	 * Log a particular status
	 * 
	 * @deprecated Use IdeLog instead
	 */
	public static void log(IStatus status)
	{
		IdeLog.log(getDefault(), status);
	}

	/**
	 * logError
	 * 
	 * @param e
	 * @deprecated Use IdeLog instead
	 */
	public static void log(Throwable e)
	{
		IdeLog.logError(getDefault(), e.getLocalizedMessage(), e);
	}

	/**
	 * logError
	 * 
	 * @deprecated Use IdeLog instead
	 * @param message
	 * @param e
	 */
	public static void logError(Throwable e)
	{
		IdeLog.logError(getDefault(), e.getLocalizedMessage(), e);
	}

	/**
	 * logError
	 * 
	 * @deprecated Use IdeLog instead
	 * @param message
	 * @param e
	 */
	public static void logError(String message, Throwable e)
	{
		IdeLog.logError(getDefault(), message, e);
	}

	/**
	 * logWarning
	 * 
	 * @deprecated Use IdeLog instead
	 * @param message
	 * @param e
	 */
	public static void logWarning(String message, Throwable e)
	{
		IdeLog.logWarning(getDefault(), message, e, null);
	}

	/**
	 * logInfo
	 * 
	 * @deprecated Use IdeLog instead
	 * @param message
	 */
	public static void logInfo(String message)
	{
		IdeLog.logInfo(getDefault(), message, null);
	}
}
