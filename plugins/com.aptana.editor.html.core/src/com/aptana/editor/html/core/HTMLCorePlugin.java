/**
 * Appcelerator Titanium Studio
 * Copyright (c) 2013 by Appcelerator, Inc. All Rights Reserved.
 * Proprietary and Confidential - This source code is not for redistribution
 */

package com.aptana.editor.html.core;

import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.BundleContext;

public class HTMLCorePlugin extends Plugin
{

	// The plug-in ID
	public static final String PLUGIN_ID = "com.aptana.editor.html.core"; //$NON-NLS-1$

	// The shared instance
	private static HTMLCorePlugin plugin;

	/**
	 * The constructor
	 */
	public HTMLCorePlugin()
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
	public static HTMLCorePlugin getDefault()
	{
		return plugin;
	}
}
