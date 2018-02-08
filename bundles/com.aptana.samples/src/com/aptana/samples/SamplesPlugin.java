/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.samples;

import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.BundleContext;

import com.aptana.samples.internal.SamplesManager;

/**
 * The activator class controls the plug-in life cycle
 */
public class SamplesPlugin extends Plugin
{

	// The plug-in ID
	public static final String PLUGIN_ID = "com.aptana.samples"; //$NON-NLS-1$

	// The shared instance
	private static SamplesPlugin plugin;

	private ISamplesManager samplesManager;

	/**
	 * The constructor
	 */
	public SamplesPlugin()
	{
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.core.runtime.Plugin#start(org.osgi.framework.BundleContext)
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
		samplesManager = null;
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 * 
	 * @return the shared instance
	 */
	public static SamplesPlugin getDefault()
	{
		return plugin;
	}

	public synchronized ISamplesManager getSamplesManager()
	{
		if (samplesManager == null)
		{
			samplesManager = new SamplesManager();
		}
		return samplesManager;
	}
}
