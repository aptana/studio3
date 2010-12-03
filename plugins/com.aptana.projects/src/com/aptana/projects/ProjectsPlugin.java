/**
 * This file Copyright (c) 2005-2010 Aptana, Inc. This program is
 * dual-licensed under both the Aptana Public License and the GNU General
 * Public license. You may elect to use one or the other of these licenses.
 * 
 * This program is distributed in the hope that it will be useful, but
 * AS-IS and WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE, TITLE, or
 * NONINFRINGEMENT. Redistribution, except as permitted by whichever of
 * the GPL or APL you select, is prohibited.
 *
 * 1. For the GPL license (GPL), you can redistribute and/or modify this
 * program under the terms of the GNU General Public License,
 * Version 3, as published by the Free Software Foundation.  You should
 * have received a copy of the GNU General Public License, Version 3 along
 * with this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 * 
 * Aptana provides a special exception to allow redistribution of this file
 * with certain other free and open source software ("FOSS") code and certain additional terms
 * pursuant to Section 7 of the GPL. You may view the exception and these
 * terms on the web at http://www.aptana.com/legal/gpl/.
 * 
 * 2. For the Aptana Public License (APL), this program and the
 * accompanying materials are made available under the terms of the APL
 * v1.0 which accompanies this distribution, and is available at
 * http://www.aptana.com/legal/apl/.
 * 
 * You may view the GPL, Aptana's exception and additional terms, and the
 * APL in the file titled license.html at the root of the corresponding
 * plugin containing this source file.
 * 
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.projects;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import com.aptana.core.build.UnifiedBuilder;
import com.aptana.core.util.ResourceUtil;

/**
 * The activator class controls the plug-in life cycle
 */
public class ProjectsPlugin extends AbstractUIPlugin
{

	// The plug-in ID
	public static final String PLUGIN_ID = "com.aptana.projects"; //$NON-NLS-1$

	// The shared instance
	private static ProjectsPlugin plugin;

	private Job addBuilderJob;

	/**
	 * The constructor
	 */
	public ProjectsPlugin()
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
		addBuilderJob = new Job("Adding unified builder to our projects")
		{
			protected IStatus run(IProgressMonitor monitor)
			{
				MultiStatus status = new MultiStatus(PLUGIN_ID, Status.OK, Status.OK_STATUS.getMessage(), null);
				IProject[] projects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
				SubMonitor sub = SubMonitor.convert(monitor, projects.length);
				for (IProject p : projects)
				{
					if (sub.isCanceled())
					{
						return Status.CANCEL_STATUS;
					}

					try
					{
						if (!p.isAccessible())
						{
							continue;
						}
						sub.subTask(p.getName());

						// FIXME These should be constants on the nature classes, but the dependencies would get
						// inverted...
						if (p.hasNature(WebProjectNature.ID) || p.hasNature("com.aptana.ruby.core.rubynature") //$NON-NLS-1$
								|| p.hasNature("org.radrails.rails.core.railsnature") //$NON-NLS-1$
								|| p.hasNature("com.aptana.editor.php.phpNature")) //$NON-NLS-1$
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
		addBuilderJob.schedule();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception
	{
		try
		{
			if (addBuilderJob != null)
			{
				addBuilderJob.cancel();
				addBuilderJob = null;
			}
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
	public static ProjectsPlugin getDefault()
	{
		return plugin;
	}

	public static ImageDescriptor getImageDescriptor(String string)
	{
		if (getDefault().getImageRegistry().getDescriptor(string) == null)
		{
			ImageDescriptor id = imageDescriptorFromPlugin(PLUGIN_ID, string);
			if (id != null)
			{
				getDefault().getImageRegistry().put(string, id);
			}
		}
		return getDefault().getImageRegistry().getDescriptor(string);
	}

	public static void logError(String msg, Throwable e)
	{
		getDefault().getLog().log(new Status(IStatus.ERROR, PLUGIN_ID, msg, e));
	}
}
