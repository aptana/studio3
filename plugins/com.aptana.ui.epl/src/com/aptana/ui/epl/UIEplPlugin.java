/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the Eclipse Public License (EPL).
 * Please see the license-epl.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.ui.epl;

import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

public class UIEplPlugin extends AbstractUIPlugin
{

	// The plug-in ID
	public static final String PLUGIN_ID = "com.aptana.ui.epl"; //$NON-NLS-1$

	public static final boolean DEBUG = Boolean
			.valueOf(Platform.getDebugOption("com.aptana.ui.epl/debug")).booleanValue(); //$NON-NLS-1$

	// The shared instance
	private static UIEplPlugin plugin;

	public final static String ICON_PATH = "$nl$/icons/"; //$NON-NLS-1$
	public final static String IMG_TOOL_CLOSE = "close.gif"; //$NON-NLS-1$
	public final static String IMG_TOOL_CLOSE_HOT = "close_hot.gif"; //$NON-NLS-1$

	/**
	 * The constructor
	 */
	public UIEplPlugin()
	{
	}

	/**
	 * @see org.eclipse.core.runtime.Plugins#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception
	{
		super.start(context);
		plugin = this;
	}

	/**
	 * @see org.eclipse.core.runtime.Plugin#stop(org.osgi.framework.BundleContext)
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
	public static UIEplPlugin getDefault()
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

	protected void initializeImageRegistry(ImageRegistry reg)
	{
		createImageDescriptor(IMG_TOOL_CLOSE, reg);
		createImageDescriptor(IMG_TOOL_CLOSE_HOT, reg);

	}

	/**
	 * Creates the specified image descriptor and registers it
	 */
	private void createImageDescriptor(String id, ImageRegistry reg)
	{
		URL url = FileLocator.find(getBundle(), new Path(ICON_PATH).append(id), null);
		ImageDescriptor desc = ImageDescriptor.createFromURL(url);
		reg.put(id, desc);
	}

}
