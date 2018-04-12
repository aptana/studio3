/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.findbar;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * This class controls the plug-in life cycle
 */
public class FindBarPlugin extends AbstractUIPlugin
{

	// The plug-in ID
	public static final String PLUGIN_ID = "com.aptana.editor.findbar"; //$NON-NLS-1$

	public static final String ICON_SEARCH_HISTORY = "icons/elcl16/search_history.png"; //$NON-NLS-1$
	public static final String ICON_SEARCH_CURRENT_FILE = "icons/elcl16/search_current_file.gif"; //$NON-NLS-1$
	public static final String ICON_SEARCH_OPEN_FILES = "icons/elcl16/search_open_files.gif"; //$NON-NLS-1$
	public static final String ICON_SEARCH_PROJECT = "icons/elcl16/search_project.gif"; //$NON-NLS-1$
	public static final String ICON_SEARCH_WORKSPACE = "icons/elcl16/search_workspace.gif"; //$NON-NLS-1$

	// The shared instance
	private static FindBarPlugin plugin;

	/**
	 * The constructor
	 */
	public FindBarPlugin()
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
	public static FindBarPlugin getDefault()
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

	public static void log(Throwable e)
	{
		Status s = new Status(IStatus.ERROR, FindBarPlugin.PLUGIN_ID, IStatus.ERROR, e.getMessage(), e);
		FindBarPlugin.getDefault().getLog().log(s);
	}
}
