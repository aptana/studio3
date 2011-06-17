/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.deploy.redhat;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import com.aptana.core.logging.IdeLog;

public class RedHatPlugin extends AbstractUIPlugin
{

	private static final String PLUGIN_ID = "com.aptana.deploy.redhat"; //$NON-NLS-1$

	public static final String WIZARD_IMAGE = "icons/redhat.png"; //$NON-NLS-1$

	private static RedHatPlugin instance;

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

	private static RedHatPlugin getDefault()
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

	public static void logError(String string, CoreException e)
	{
		getDefault().getLog().log(new Status(IStatus.ERROR, getPluginIdentifier(), string, e));
	}

	/**
	 * logError
	 * 
	 * @deprecated Use IdeLog instead
	 * @param message
	 * @param e
	 */
	public static void logError(Throwable e)
	{
		IdeLog.logError(getDefault(), e.getLocalizedMessage(), e);
	}

	/**
	 * logError
	 * 
	 * @deprecated Use IdeLog instead
	 * @param message
	 * @param e
	 */
	public static void logError(String message, Throwable e)
	{
		IdeLog.logError(getDefault(), message, e);
	}

}
