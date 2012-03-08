/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the Eclipse Public License (EPL).
 * Please see the license-epl.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.index.core;

import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class IndexPlugin extends Plugin
{

	public static final String PLUGIN_ID = "com.aptana.index.core"; //$NON-NLS-1$

	private static IndexPlugin plugin;
	private IndexManager fManager;

	/**
	 * Returns the shared instance
	 * 
	 * @return the shared instance
	 */
	public static IndexPlugin getDefault()
	{
		return plugin;
	}

	/**
	 * The constructor
	 */
	public IndexPlugin()
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
		fManager = null;
		plugin = null;
		super.stop(context);
	}

	public synchronized IndexManager getIndexManager()
	{
		if (fManager == null)
		{
			fManager = new IndexManager();
		}
		return fManager;
	}
}
