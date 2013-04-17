/**
 * Aptana Studio
 * Copyright (c) 2013 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.samples.ui.project;

import java.io.File;

import org.eclipse.core.runtime.IPath;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.dialogs.WizardNewProjectCreationPage;

import com.aptana.projects.ProjectData;
import com.aptana.projects.ProjectsPlugin;

/**
 * Import sample project wizard page that adds the contributing services.
 * 
 * @author pinnamuri
 */
class SampleNewProjectCreationPage extends WizardNewProjectCreationPage
{
	private ProjectData projectData;
	private String[] natures;

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
		ProjectsPlugin.getDefault().getProjectWizardContributionManager()
				.contributeProjectCreationPage(natures, projectData, this, control);

		validatePage();
	}

	protected ProjectData getProjectData()
	{
		return projectData;
	}

	@Override
	protected boolean validatePage()
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
		if (file.exists())
		{
			setMessage(Messages.NewSampleProjectWizard_LocationExistsMessage, WARNING);
			return true;
		}

		setErrorMessage(null);
		setMessage(null);
		return true;
	}
}