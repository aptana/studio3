/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.deploy.heroku;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

public class HerokuPlugin extends AbstractUIPlugin
{

	private static final String PLUGIN_ID = "com.aptana.deploy.heroku"; //$NON-NLS-1$

	private static HerokuPlugin instance;

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

	public static HerokuPlugin getDefault()
	{
		return instance;
	}

	public static ImageDescriptor getImageDescriptor(String path)
	{
		ImageDescriptor desc = getDefault().getImageRegistry().getDescriptor(path);
		if (desc != null)
		{
			return desc;
		}
		desc = imageDescriptorFromPlugin(PLUGIN_ID, path);
		getDefault().getImageRegistry().put(path, desc);
		return desc;
	}
}
