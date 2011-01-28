/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.deploy;

import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import com.aptana.deploy.preferences.DeployPreferenceUtil;
import com.aptana.deploy.preferences.IPreferenceConstants.DeployType;

public class Activator extends AbstractUIPlugin
{

	private static final String PLUGIN_ID = "com.aptana.deploy"; //$NON-NLS-1$

	private static Activator instance;

	private IResourceChangeListener resourceListener = new IResourceChangeListener()
	{

		public void resourceChanged(IResourceChangeEvent event)
		{
			if (event.getType() == IResourceChangeEvent.PRE_DELETE)
			{
				DeployPreferenceUtil.setDeployType(event.getResource().getProject(), DeployType.NONE);
			}
		}
	};

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext bundleContext) throws Exception
	{
		super.start(bundleContext);
		instance = this;
		ResourcesPlugin.getWorkspace().addResourceChangeListener(resourceListener);
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext bundleContext) throws Exception
	{
		ResourcesPlugin.getWorkspace().removeResourceChangeListener(resourceListener);
		instance = null;
		super.stop(bundleContext);
	}

	public static String getPluginIdentifier()
	{
		return PLUGIN_ID;
	}

	public static Activator getDefault()
	{
		return instance;
	}

	public static void logError(String message, Exception e)
	{
		getDefault().getLog().log(new Status(IStatus.ERROR, PLUGIN_ID, message, e));
	}

	public static void logError(Exception e)
	{
		if (e instanceof CoreException)
		{
			logError((CoreException) e);
		}
		else
		{
			logError(e.getMessage(), e);
		}
	}

	public static void logError(CoreException e)
	{
		getDefault().getLog().log(e.getStatus());
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
