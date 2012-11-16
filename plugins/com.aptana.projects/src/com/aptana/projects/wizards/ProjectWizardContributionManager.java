/**
 * Aptana Studio
 * Copyright (c) 2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.projects.wizards;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.swt.widgets.Composite;

import com.aptana.core.logging.IdeLog;
import com.aptana.core.util.ArrayUtil;
import com.aptana.projects.ProjectsPlugin;

/**
 * @author Nam Le <nle@appcelerator.com>
 */
public class ProjectWizardContributionManager
{
	private static final String ATTRIBUTE_DEPENDENT_BUNDLE = "dependentBundle"; //$NON-NLS-1$
	private static final String ATTRIBUTE_CLASS = "class"; //$NON-NLS-1$
	private static final String EXTENSION_POINT_NAME = "projectWizardContributors"; //$NON-NLS-1$
	private List<IProjectWizardContributor> contributors;

	public ProjectWizardContributionManager()
	{
	}

	public IWizardPage[] createPages(String[] natureIds)
	{
		loadExtensions();
		List<IWizardPage> pages = new ArrayList<IWizardPage>();
		for (IProjectWizardContributor contributor : contributors)
		{
			if (!ArrayUtil.isEmpty(natureIds) && !contributor.hasNatureId(natureIds))
			{
				continue;
			}

			IWizardPage wizardPage = contributor.createWizardPage();
			if (wizardPage != null)
			{
				pages.add(wizardPage);
			}
		}

		return pages.toArray(new IWizardPage[pages.size()]);
	}

	public void finalizeWizardPages(IWizardPage[] pages, String[] natureIds)
	{
		loadExtensions();
		for (IProjectWizardContributor contributor : contributors)
		{
			for (IWizardPage page : pages)
			{
				if (!ArrayUtil.isEmpty(natureIds) && !contributor.hasNatureId(natureIds))
				{
					continue;
				}

				contributor.finalizeWizardPage(page);
			}
		}
	}

	public void contributeProjectCreationPage(String[] natureIds, Composite parent)
	{
		loadExtensions();
		for (IProjectWizardContributor contributor : contributors)
		{
			if (!ArrayUtil.isEmpty(natureIds) && !contributor.hasNatureId(natureIds))
			{
				continue;
			}

			contributor.appendProjectCreationPage(parent);
		}
	}

	private synchronized void loadExtensions()
	{
		if (contributors == null)
		{
			IExtensionRegistry registry = Platform.getExtensionRegistry();
			IConfigurationElement[] elements = registry.getConfigurationElementsFor(ProjectsPlugin.PLUGIN_ID,
					EXTENSION_POINT_NAME);
			contributors = new ArrayList<IProjectWizardContributor>(elements.length);
			for (IConfigurationElement element : elements)
			{
				try
				{
					String bundleId = element.getAttribute(ATTRIBUTE_DEPENDENT_BUNDLE);
					if (bundleId != null && Platform.getBundle(bundleId) == null)
					{
						continue;
					}

					IProjectWizardContributor contributorObject = (IProjectWizardContributor) element
							.createExecutableExtension(ATTRIBUTE_CLASS);
					if (contributorObject == null)
					{
						continue;
					}

					contributors.add(contributorObject);
				}
				catch (CoreException e)
				{
					IdeLog.logError(ProjectsPlugin.getDefault(), e);
				}
			}
		}
	}

	public IStatus performProjectFinish(IProject project, IProgressMonitor monitor)
	{
		loadExtensions();
		for (IProjectWizardContributor contributor : contributors)
		{
			IStatus status = contributor.performWizardFinish(project, monitor);
			if (status != null && !status.isOK())
			{
				return status;
			}
		}

		return Status.OK_STATUS;
	}
}
