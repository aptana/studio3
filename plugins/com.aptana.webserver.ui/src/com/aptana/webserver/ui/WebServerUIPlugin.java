/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
// $codepro.audit.disable declaredExceptions
// $codepro.audit.disable com.instantiations.assist.eclipse.analysis.audit.rule.effectivejava.enforceTheSingletonPropertyWithAPrivateConstructor
// $codepro.audit.disable staticFieldNamingConvention
package com.aptana.webserver.ui;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class WebServerUIPlugin extends AbstractUIPlugin
{

	// The plug-in ID
	public static final String PLUGIN_ID = "com.aptana.webserver.ui"; //$NON-NLS-1$

	// Generic server icon
	public static final String SERVER_ICON = "icons/obj16/server.png"; //$NON-NLS-1$

	// The shared instance
	private static WebServerUIPlugin plugin;

	/**
	 * The constructor
	 */
	public WebServerUIPlugin()
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
	public static WebServerUIPlugin getDefault()
	{
		return plugin;
	}

	@Override
	protected void initializeImageRegistry(ImageRegistry reg)
	{
		reg.put(SERVER_ICON, imageDescriptorFromPlugin(PLUGIN_ID, SERVER_ICON));
	}

	public static Image getImage(String iconPath)
	{
		ImageDescriptor desc = getDefault().getImageRegistry().getDescriptor(iconPath);
		if (desc == null)
		{
			desc = imageDescriptorFromPlugin(PLUGIN_ID, iconPath);
			getDefault().getImageRegistry().put(iconPath, desc);
		}
		return desc.createImage();
	}
}
