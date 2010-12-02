/**
 * Copyright (c) 2005-2010 Aptana, Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */
package com.aptana.index.core;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.core.runtime.jobs.Job;
import org.osgi.framework.BundleContext;

import com.aptana.core.util.ResourceUtil;

/**
 * The activator class controls the plug-in life cycle
 */
public class IndexActivator extends Plugin
{

	public static final String PLUGIN_ID = "com.aptana.index.core"; //$NON-NLS-1$
	private static IndexActivator plugin;

	/**
	 * Returns the shared instance
	 * 
	 * @return the shared instance
	 */
	public static IndexActivator getDefault()
	{
		return plugin;
	}

	/**
	 * logError
	 * 
	 * @param e
	 */
	public static void logError(CoreException e)
	{
		getDefault().getLog().log(e.getStatus());
	}

	/**
	 * logError
	 * 
	 * @param msg
	 * @param e
	 */
	public static void logError(String msg, Throwable e)
	{
		getDefault().getLog().log(new Status(IStatus.ERROR, PLUGIN_ID, msg, e));
	}

	/**
	 * The constructor
	 */
	public IndexActivator()
	{
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.core.runtime.Plugins#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception
	{
		super.start(context);
		plugin = this;

		// Run a job that iterates over the projects, looking for our natures and adding the unified builder
		Job job = new Job("Adding unified builder to our projects")
		{
			protected IStatus run(IProgressMonitor monitor)
			{
				MultiStatus status = new MultiStatus(PLUGIN_ID, Status.OK, Status.OK_STATUS.getMessage(), null);
				IProject[] projects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
				SubMonitor sub = SubMonitor.convert(monitor, projects.length);
				for (IProject p : projects)
				{
					try
					{
						if (!p.isAccessible())
						{
							continue;
						}
						sub.subTask(p.getName());

						// FIXME These should be constants on the nature classes, but the dependencies would get
						// inverted...
						if (p.hasNature("com.aptana.projects.webnature") //$NON-NLS-1$
								|| p.hasNature("com.aptana.ruby.core.rubynature") //$NON-NLS-1$
								|| p.hasNature("org.radrails.rails.core.railsnature")) //$NON-NLS-1$
						{
							ResourceUtil.addBuilder(p, UnifiedBuilder.ID);
						}
						status.add(Status.OK_STATUS);
					}
					catch (CoreException e)
					{
						status.add(e.getStatus());
					}
					finally
					{
						sub.worked(1);
					}
				}
				sub.done();
				return status;
			}
		};
		job.schedule();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.core.runtime.Plugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception
	{
		plugin = null;
		super.stop(context);
	}
}
