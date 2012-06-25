/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.deploy.ui;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class DeployUIPlugin extends AbstractUIPlugin
{

	// The plug-in ID
	public static final String PLUGIN_ID = "com.aptana.deploy.ui"; //$NON-NLS-1$

	/**
	 * ID of the drop-down menu for the App Explorer's deployment options. Plugins can modify the menu using the
	 * "menu: com.aptana.explorer.deploy" URI.
	 */
	public static final String DEPLOY_MENU_ID = "com.aptana.explorer.deploy"; //$NON-NLS-1$

	/**
	 * The path to the icon for deploy menu
	 */
	public static final String DEPLOY_MENU_ICON = "icons/full/elcl16/deploy_package.png"; //$NON-NLS-1$

	/**
	 * The path to the hot icon for deploy menu
	 */
	public static final String DEPLOY_HOT_MENU_ICON = "icons/full/elcl16/deploy_package_hot.png"; //$NON-NLS-1$

	// The shared instance
	private static DeployUIPlugin plugin;

	/**
	 * The constructor
	 */
	public DeployUIPlugin()
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
	public static DeployUIPlugin getDefault()
	{
		return plugin;
	}

	public static Image getImage(String string)
	{
		if (getDefault().getImageRegistry().get(string) == null)
		{
			ImageDescriptor id = imageDescriptorFromPlugin(PLUGIN_ID, string);
			if (id != null)
			{
				getDefault().getImageRegistry().put(string, id);
			}
		}
		return getDefault().getImageRegistry().get(string);
	}
}
