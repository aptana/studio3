/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the Eclipse Public License (EPL).
 * Please see the license-epl.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.formatter.epl;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class FormatterPlugin extends AbstractUIPlugin
{

	private static final int INTERNAL_ERROR = 10001;

	// The plug-in ID
	public static final String PLUGIN_ID = "com.aptana.formatter.epl"; //$NON-NLS-1$
	public static final boolean DEBUG = Boolean.valueOf(Platform.getDebugOption(PLUGIN_ID + "/debug")).booleanValue(); //$NON-NLS-1$

	// The shared instance
	private static FormatterPlugin plugin;

	/**
	 * The constructor
	 */
	public FormatterPlugin()
	{
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception
	{
		super.start(context);
		plugin = this;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
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
	public static FormatterPlugin getDefault()
	{
		return plugin;
	}

	public static void log(IStatus status)
	{
		getDefault().getLog().log(status);
	}

	public static void logError(String message)
	{
		logError(message, null);
	}

	public static void warn(String message, Throwable throwable)
	{
		log(new Status(IStatus.WARNING, PLUGIN_ID, INTERNAL_ERROR, message, throwable));
	}

	public static void logError(String message, Throwable throwable)
	{
		log(new Status(IStatus.ERROR, PLUGIN_ID, INTERNAL_ERROR, message, throwable));
	}

	public static void logError(Throwable t)
	{
		logError(t.getMessage(), t);
	}
}
