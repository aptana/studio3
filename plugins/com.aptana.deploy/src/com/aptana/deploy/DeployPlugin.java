/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.deploy;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

public class DeployPlugin extends AbstractUIPlugin
{

	private static final String PLUGIN_ID = "com.aptana.deploy"; //$NON-NLS-1$

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

	public static Image getImage(String string)
	{
		if (getDefault().getImageRegistry().get(string) == null)
		{
			ImageDescriptor id = imageDescriptorFromPlugin(PLUGIN_ID, string);
			if (id != null)
				getDefault().getImageRegistry().put(string, id);
		}
		return getDefault().getImageRegistry().get(string);
	}
}
