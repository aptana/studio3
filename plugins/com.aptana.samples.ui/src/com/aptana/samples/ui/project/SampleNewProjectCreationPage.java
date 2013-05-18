/**
 * Aptana Studio
 * Copyright (c) 2013 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.samples.ui.project;

import java.io.File;
import java.util.Set;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.dialogs.WizardNewProjectCreationPage;

import com.aptana.projects.ProjectData;
import com.aptana.projects.ProjectsPlugin;
import com.aptana.projects.wizards.IProjectWizardContributor;
import com.aptana.projects.wizards.ProjectWizardContributionManager;
import com.aptana.ui.IValidationPage;

/**
 * Import sample project wizard page that adds the contributing services.
 * 
 * @author pinnamuri
 */
class SampleNewProjectCreationPage extends WizardNewProjectCreationPage implements IValidationPage
{
	private ProjectData projectData;
	private String[] natures;
	private Set<IProjectWizardContributor> contributors;

	public SampleNewProjectCreationPage(String pageName, String[] natures)
	{
		super(pageName);
		this.natures = natures;
		projectData = new ProjectData();
	}

	@Override
	public void createControl(Composite parent)
	{
		super.createControl(parent);
		Composite control = (Composite) getControl();
		ProjectWizardContributionManager projectWizardContributionManager = getProjectWizardContributionManager();
		contributors = projectWizardContributionManager.contributeSampleProjectCreationPage(natures, projectData, this,
				control);
		contributors.addAll(projectWizardContributionManager.contributeProjectCreationPage(natures, projectData, this,
				control));
		validatePage();
	}

	protected ProjectWizardContributionManager getProjectWizardContributionManager()
	{
		return ProjectsPlugin.getDefault().getProjectWizardContributionManager();
	}

	protected ProjectData getProjectData()
	{
		return projectData;
	}

	@Override
	public boolean validatePage()
	{
		if (!super.validatePage())
		{
			return false;
		}

		// Check if there's already a directory/files at the destination
		IPath location = getLocationPath();
		if (useDefaults())
		{
			// needs to append the project name since getLocationPath() returns the workspace path in this case
			location = location.append(getProjectName());
		}

		File file = location.toFile();
		boolean hasWarning = false;
		if (file.exists())
		{
			setMessage(Messages.NewSampleProjectWizard_LocationExistsMessage, WARNING);
			hasWarning = true;
		}

		// Collect all statuses from the contributors. We stop on the first error status, and flag the first warning
		// status.
		if (contributors != null)
		{
			for (IProjectWizardContributor contributor : contributors)
			{
				IStatus status = contributor.validateProjectCreationPage(projectData);
				if (status != null)
				{
					if (status.getSeverity() == IStatus.ERROR)
					{
						setErrorMessage(status.getMessage());
						return false;
					}
					if (!hasWarning && status.getSeverity() == IStatus.WARNING)
					{
						setMessage(status.getMessage(), WARNING);
						hasWarning = true;
					}
				}
			}
		}

		if (hasWarning)
		{
			return true;
		}

		setErrorMessage(null);
		setMessage(null);
		return true;
	}
}