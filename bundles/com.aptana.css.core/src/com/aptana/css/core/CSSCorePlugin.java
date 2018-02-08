/**
 * Aptana Studio
 * Copyright (c) 2013 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.css.core;

import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.jobs.Job;
import org.osgi.framework.BundleContext;

import com.aptana.css.core.index.CSSMetadataLoader;

/**
 * The activator class controls the plug-in life cycle
 */
public class CSSCorePlugin extends Plugin
{

	// The plug-in ID
	public static final String PLUGIN_ID = "com.aptana.css.core"; //$NON-NLS-1$

	// The shared instance
	private static CSSCorePlugin plugin;

	/**
	 * The constructor
	 */
	public CSSCorePlugin()
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

		Job job = new CSSMetadataLoader();
		job.schedule();
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
	public static CSSCorePlugin getDefault()
	{
		return plugin;
	}
}
