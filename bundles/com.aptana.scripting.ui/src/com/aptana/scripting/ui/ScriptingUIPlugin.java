/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.scripting.ui;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import com.aptana.core.util.EclipseUtil;
import com.aptana.scripting.ui.internal.KeybindingsManager;
import com.aptana.scripting.ui.internal.listeners.ExecutionListenerRegistrant;

/**
 * The activator class controls the plug-in life cycle
 */
public class ScriptingUIPlugin extends AbstractUIPlugin
{
	public static final String PLUGIN_ID = "com.aptana.scripting.ui"; //$NON-NLS-1$

	/**
	 * Context id set by workbench part to indicate they are scripting aware.
	 */
	public static final String SCRIPTING_CONTEXT_ID = "com.aptana.scripting.context"; //$NON-NLS-1$

	private static ScriptingUIPlugin plugin;

	/**
	 * Returns the image on the specified path.
	 * 
	 * @param path
	 *            the path to the image
	 * @return Image the image object
	 */
	public static Image getImage(String path)
	{
		ImageRegistry registry = getDefault().getImageRegistry();

		if (registry.get(path) == null)
		{
			ImageDescriptor id = getImageDescriptor(path);

			if (id != null)
			{
				registry.put(path, id);
			}
		}

		return registry.get(path);
	}

	/**
	 * Returns an image descriptor for the image file at the given plug-in relative path
	 * 
	 * @param path
	 *            the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String path)
	{
		return imageDescriptorFromPlugin(PLUGIN_ID, path);
	}

	/**
	 * The constructor
	 */
	public ScriptingUIPlugin()
	{
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception // $codepro.audit.disable declaredExceptions
	{
		super.start(context);
		plugin = this;

		Job startupJob = new Job("Start Ruble bundle manager") //$NON-NLS-1$
		{
			@Override
			protected IStatus run(IProgressMonitor monitor)
			{
				ExecutionListenerRegistrant.getInstance();

				// install key binding Manager
				KeybindingsManager.install();

				return Status.OK_STATUS;
			}
		};
		EclipseUtil.setSystemForJob(startupJob);
		startupJob.schedule();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception // $codepro.audit.disable declaredExceptions
	{
		try
		{
			KeybindingsManager.uninstall();
			ExecutionListenerRegistrant.shutdown();
		}
		catch (Exception e)
		{
			// ignore
		}
		finally
		{
			plugin = null;
			super.stop(context);
		}
	}

	/**
	 * Returns the shared instance
	 * 
	 * @return the shared instance
	 */
	public static ScriptingUIPlugin getDefault()
	{
		return plugin;
	}
}
