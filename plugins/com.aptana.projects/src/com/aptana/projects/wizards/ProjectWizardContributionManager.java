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

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.wizard.IWizardPage;

import com.aptana.core.logging.IdeLog;
import com.aptana.core.util.ArrayUtil;
import com.aptana.projects.ProjectsPlugin;

/**
 * @author Nam Le <nle@appcelerator.com>
 */
public class ProjectWizardContributionManager
{
	private static final String EXTENSION_POINT_NAME = "projectWizardContributors"; //$NON-NLS-1$
	private List<IProjectWizardContributor> contributors = new ArrayList<IProjectWizardContributor>();

	public ProjectWizardContributionManager()
	{
		contributors.clear();
		IExtensionRegistry registry = Platform.getExtensionRegistry();
		IConfigurationElement[] elements = registry.getConfigurationElementsFor(ProjectsPlugin.PLUGIN_ID,
				EXTENSION_POINT_NAME);

		for (int i = 0; i < elements.length; i++)
		{
			final IConfigurationElement element = elements[i];
			try
			{
				IProjectWizardContributor contributorObject = (IProjectWizardContributor) element
						.createExecutableExtension("class"); //$NON-NLS-1$
				if (contributorObject == null)
				{
					continue;
				}

				String bundleId = element.getAttribute("dependentBundle"); //$NON-NLS-1$
				if (bundleId != null && Platform.getBundle(bundleId) == null)
				{
					continue;
				}
				contributorObject.setNatureId(element.getAttribute("natureId")); //$NON-NLS-1$
				contributors.add(contributorObject);
			}
			catch (CoreException e)
			{
				IdeLog.logError(ProjectsPlugin.getDefault(), e);
			}
		}
	}

	public IWizardPage[] createPages(String[] natureIds)
	{
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
}
