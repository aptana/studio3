/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.deploy;

import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.BundleContext;

public class DeployPlugin extends Plugin
{

	private static final String PLUGIN_ID = "com.aptana.deploy"; //$NON-NLS-1$

	private static DeployPlugin instance;

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext bundleContext) throws Exception
	{
		super.start(bundleContext);
		instance = this;
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext bundleContext) throws Exception
	{
		instance = null;
		super.stop(bundleContext);
	}

	public static String getPluginIdentifier()
	{
		return PLUGIN_ID;
	}

	public static DeployPlugin getDefault()
	{
		return instance;
	}
}
