/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.projects.internal.wizards;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExecutableExtension;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.wizards.newresource.BasicNewProjectResourceWizard;
import org.eclipse.ui.wizards.newresource.BasicNewResourceWizard;

import com.aptana.projects.ProjectsPlugin;
import com.aptana.ui.wizards.WizardFolderImportPage;

public class PromoteToProjectWizard extends BasicNewResourceWizard implements IExecutableExtension
{

	

	private static final String IMAGE = "icons/importdir_wiz.png"; //$NON-NLS-1$

	private WizardFolderImportPage mainPage;
	private IProject newProject;
	private IConfigurationElement configElement;
	private String initialDirectoryPath;

	public PromoteToProjectWizard()
	{
		this(""); //$NON-NLS-1$
	}

	/**
	 * Constructor for ExternalProjectImportWizard.
	 * 
	 * @param initialPage
	 */
	public PromoteToProjectWizard(String initialDirectoryPath)
	{
		this.initialDirectoryPath = initialDirectoryPath;
		IDialogSettings workbenchSettings = ProjectsPlugin.getDefault().getDialogSettings();
		IDialogSettings section = workbenchSettings.getSection("PromoteToProjectWizard");//$NON-NLS-1$
		if (section == null)
		{
			section = workbenchSettings.addNewSection("PromoteToProjectWizard");//$NON-NLS-1$
		}
		setDialogSettings(section);
	}

	@Override
	public void addPages()
	{
		super.addPages();

		mainPage = new WizardFolderImportPage();
		mainPage.setDirectoryPath(initialDirectoryPath);
		mainPage.setTitle(Messages.PromoteToProjectWizard_PageTitle);
		mainPage.setDescription(Messages.PromoteToProjectWizard_PageDescription);
		addPage(mainPage);
	}

	@Override
	public boolean performFinish()
	{
		newProject = mainPage.createProject();
		if (newProject == null)
		{
			return false;
		}
		updatePerspective();
		selectAndReveal(newProject, PlatformUI.getWorkbench().getActiveWorkbenchWindow());
		return true;
	}

	public void setInitializationData(IConfigurationElement config, String propertyName, Object data)
			throws CoreException
	{
		configElement = config;
	}

	@Override
	public void init(IWorkbench workbench, IStructuredSelection currentSelection)
	{
		super.init(workbench, currentSelection);
		setNeedsProgressMonitor(true);
		setWindowTitle(Messages.PromoteToProjectWizard_WindowTitle);
	}

	@Override
	protected void initializeDefaultPageImageDescriptor()
	{
		setDefaultPageImageDescriptor(ProjectsPlugin.getImageDescriptor(IMAGE));
	}

	private void updatePerspective()
	{
		BasicNewProjectResourceWizard.updatePerspective(configElement);
	}
}
