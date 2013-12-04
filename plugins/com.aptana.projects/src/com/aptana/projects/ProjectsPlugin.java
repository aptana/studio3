/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.projects;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import com.aptana.core.projects.templates.ProjectTemplate;
import com.aptana.core.projects.templates.TemplateType;
import com.aptana.core.util.CollectionsUtil;
import com.aptana.projects.internal.listeners.StudioProjectListenersManager;
import com.aptana.projects.listeners.IProjectListenersManager;
import com.aptana.projects.templates.IDefaultProjectTemplate;
import com.aptana.projects.templates.ProjectTemplatesManager;
import com.aptana.projects.wizards.ProjectWizardContributionManager;

/**
 * The activator class controls the plug-in life cycle
 */
public class ProjectsPlugin extends AbstractUIPlugin
{

	// The plug-in ID
	public static final String PLUGIN_ID = "com.aptana.projects"; //$NON-NLS-1$

	// The shared instance
	private static ProjectsPlugin plugin;

	private ProjectTemplatesManager templatesManager;
	private ProjectWizardContributionManager projectWizardContributionManager;
	private IProjectListenersManager projectListenersManager;

	private static class DefaultWebProjectTemplate extends ProjectTemplate implements IDefaultProjectTemplate
	{

		private static final String ID = "com.aptana.projects.web.default"; //$NON-NLS-1$

		public DefaultWebProjectTemplate()
		{
			super("default.zip", TemplateType.WEB, Messages.ProjectsPlugin_DefaultWebProjectTemplate_Name, //$NON-NLS-1$
					false, Messages.ProjectsPlugin_DefaultWebProjectTemplate_Description, null, ID, 1, CollectionsUtil
							.newList("Web")); //$NON-NLS-1$
		}

		@Override
		public IStatus apply(IProject project, boolean promptForOverwrite)
		{
			// just returns success
			return Status.OK_STATUS;
		}
	}

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
		getTemplatesManager().addTemplate(new DefaultWebProjectTemplate());
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception
	{
		plugin = null;
		if (templatesManager != null)
		{
			templatesManager.dispose();
			templatesManager = null;
		}
		projectWizardContributionManager = null;
		projectListenersManager = null;
		super.stop(context);
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

	public synchronized ProjectTemplatesManager getTemplatesManager()
	{
		if (templatesManager == null)
		{
			templatesManager = new ProjectTemplatesManager();
		}
		return templatesManager;
	}

	public synchronized ProjectWizardContributionManager getProjectWizardContributionManager()
	{
		if (projectWizardContributionManager == null)
		{
			projectWizardContributionManager = new ProjectWizardContributionManager();
		}

		return projectWizardContributionManager;
	}

	public synchronized IProjectListenersManager getProjectListenersManager()
	{
		if (projectListenersManager == null)
		{
			projectListenersManager = new StudioProjectListenersManager();
		}

		return projectListenersManager;
	}
}
