/**
 * Aptana Studio
 * Copyright (c) 2012-2013 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.projects.wizards;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.widgets.Composite;

import com.aptana.core.IFilter;
import com.aptana.core.logging.IdeLog;
import com.aptana.core.util.ArrayUtil;
import com.aptana.core.util.CollectionsUtil;
import com.aptana.projects.ProjectsPlugin;

/**
 * @author Nam Le <nle@appcelerator.com>
 */
public class ProjectWizardContributionManager
{
	private static final String ATTRIBUTE_ID = "id"; //$NON-NLS-1$
	private static final String ATTRIBUTE_CLASS = "class"; //$NON-NLS-1$
	private static final String ATTRIBUTE_PRIORITY = "priority"; //$NON-NLS-1$
	private static final String EXTENSION_POINT_NAME = "projectWizardContributors"; //$NON-NLS-1$

	private static final int DEFAULT_PRIORITY = 60;
	private List<IProjectWizardContributor> contributors;

	public ProjectWizardContributionManager()
	{
	}

	public IWizardPage[] createPages(Object data, String[] natureIds)
	{
		loadExtensions();
		List<IWizardPage> pages = new ArrayList<IWizardPage>();
		for (IProjectWizardContributor contributor : contributors)
		{
			if (!ArrayUtil.isEmpty(natureIds) && !contributor.hasNatureId(natureIds))
			{
				continue;
			}

			IWizardPage wizardPage = contributor.createWizardPage(data);
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

	public Set<IProjectWizardContributor> contributeSampleProjectCreationPage(String[] natureIds, Object data,
			WizardPage page, Composite parent)
	{
		loadExtensions();
		Set<IProjectWizardContributor> activeContributors = new HashSet<IProjectWizardContributor>();
		for (IProjectWizardContributor contributor : contributors)
		{
			if (!ArrayUtil.isEmpty(natureIds) && !contributor.hasNatureId(natureIds))
			{
				continue;
			}
			activeContributors.add(contributor);
			contributor.appendSampleProjectCreationPage(data, page, parent);
		}
		return activeContributors;
	}

	public Set<IProjectWizardContributor> contributeProjectCreationPage(String[] natureIds, Object data,
			WizardPage page, Composite parent)
	{
		loadExtensions();
		Set<IProjectWizardContributor> activeContributors = new HashSet<IProjectWizardContributor>();
		for (IProjectWizardContributor contributor : contributors)
		{
			if (!ArrayUtil.isEmpty(natureIds) && !contributor.hasNatureId(natureIds))
			{
				continue;
			}
			activeContributors.add(contributor);
			contributor.appendProjectCreationPage(data, page, parent);
		}
		return activeContributors;
	}

	public void updateProject(Object data, String[] natureIds)
	{
		loadExtensions();
		for (IProjectWizardContributor contributor : contributors)
		{
			if (!ArrayUtil.isEmpty(natureIds) && !contributor.hasNatureId(natureIds))
			{
				continue;
			}

			contributor.updateProjectCreationPage(data);
		}
	}

	public IStatus validateProject(Object data, String[] natureIds)
	{
		loadExtensions();
		for (IProjectWizardContributor contributor : contributors)
		{
			if (!ArrayUtil.isEmpty(natureIds) && !contributor.hasNatureId(natureIds))
			{
				continue;
			}

			IStatus status = contributor.validateProjectCreationPage(data);
			if (status.matches(IStatus.ERROR | IStatus.WARNING))
			{
				return status;
			}
		}

		return Status.OK_STATUS;
	}

	private synchronized void loadExtensions()
	{
		if (contributors == null)
		{
			IExtensionRegistry registry = Platform.getExtensionRegistry();
			IConfigurationElement[] elements = registry.getConfigurationElementsFor(ProjectsPlugin.PLUGIN_ID,
					EXTENSION_POINT_NAME);

			// Gather all the configuration elements and filter the same contributors based on their priority.
			Map<String, IConfigurationElement> registryMap = new HashMap<String, IConfigurationElement>(elements.length);
			for (IConfigurationElement element : elements)
			{
				String id = element.getAttribute(ATTRIBUTE_ID);
				if (registryMap.containsKey(id))
				{
					IConfigurationElement currentElement = registryMap.get(id);
					int currentPrioirty = DEFAULT_PRIORITY, newPriority = DEFAULT_PRIORITY;
					try
					{
						currentPrioirty = Integer.parseInt(currentElement.getAttribute(ATTRIBUTE_PRIORITY));
						newPriority = Integer.parseInt(element.getAttribute(ATTRIBUTE_PRIORITY));
					}
					catch (NumberFormatException nfe)
					{
						IdeLog.logError(ProjectsPlugin.getDefault(), nfe);
					}
					if (newPriority < currentPrioirty)
					{
						continue;
					}
				}
				registryMap.put(id, element);
			}

			contributors = new ArrayList<IProjectWizardContributor>(registryMap.size());
			for (IConfigurationElement element : registryMap.values())
			{
				try
				{
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

	public List<IProjectWizardContributor> getContributors(final String[] natureIds)
	{
		return CollectionsUtil.filter(contributors, new IFilter<IProjectWizardContributor>()
		{
			public boolean include(IProjectWizardContributor item)
			{
				return item.hasNatureId(natureIds);
			}
		});

	}

	public IStatus performProjectFinish(IProject project, IProgressMonitor monitor)
	{
		loadExtensions();
		String[] natureIds = null;
		try
		{
			natureIds = project.getDescription().getNatureIds();
		}
		catch (CoreException e)
		{
			IdeLog.log(ProjectsPlugin.getDefault(), e.getStatus());
		}
		SubMonitor sub = SubMonitor.convert(monitor, contributors == null ? 0 : contributors.size());
		for (IProjectWizardContributor contributor : contributors)
		{
			if (!ArrayUtil.isEmpty(natureIds) && !contributor.hasNatureId(natureIds))
			{
				continue;
			}
			IStatus status = contributor.performWizardFinish(project, sub.newChild(1));
			if (status != null && !status.isOK())
			{
				return status;
			}
		}

		return Status.OK_STATUS;
	}
}
