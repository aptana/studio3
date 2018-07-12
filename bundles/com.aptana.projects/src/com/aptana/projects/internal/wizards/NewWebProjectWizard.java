/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.projects.internal.wizards;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbench;

import com.aptana.core.build.UnifiedBuilder;
import com.aptana.core.projects.templates.TemplateType;
import com.aptana.projects.ProjectsPlugin;
import com.aptana.projects.WebProjectNature;
import com.aptana.projects.wizards.AbstractNewProjectWizard;
import com.aptana.projects.wizards.CommonWizardNewProjectCreationPage;
import com.aptana.projects.wizards.IWizardProjectCreationPage;

/**
 * New Web Project Wizard
 */
public class NewWebProjectWizard extends AbstractNewProjectWizard
{

	/**
	 * The wizard ID
	 */
	public static final String ID = "com.aptana.ui.wizards.NewWebProject"; //$NON-NLS-1$

	private static final String IMAGE = "icons/web_project_wiz.png"; //$NON-NLS-1$

	protected IWizardProjectCreationPage createMainPage()
	{
		CommonWizardNewProjectCreationPage mainPage = new CommonWizardNewProjectCreationPage(
				"basicNewProjectPage", selectedTemplate); //$NON-NLS-1$
		mainPage.setTitle(Messages.NewProjectWizard_ProjectPage_Title);
		mainPage.setDescription(Messages.NewProjectWizard_ProjectPage_Description);
		return mainPage;
	}

	protected TemplateType[] getProjectTemplateTypes()
	{
		return new TemplateType[] { TemplateType.WEB, TemplateType.ALL };
	}

	protected String getProjectCreateEventName()
	{
		return "project.create.web"; //$NON-NLS-1$
	}
	
	protected boolean isCLISessionInvalid(String errorMessage)
	{
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.wizards.newresource.BasicNewResourceWizard#init(org.eclipse.ui.IWorkbench,
	 * org.eclipse.jface.viewers.IStructuredSelection)
	 */
	@Override
	public void init(IWorkbench workbench, IStructuredSelection currentSelection)
	{
		super.init(workbench, currentSelection);
		setWindowTitle(Messages.NewProjectWizard_Title);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.wizards.newresource.BasicNewResourceWizard#initializeDefaultPageImageDescriptor()
	 */
	@Override
	protected void initializeDefaultPageImageDescriptor()
	{
		setDefaultPageImageDescriptor(ProjectsPlugin.getImageDescriptor(IMAGE));
	}

	protected String[] getProjectNatures()
	{
		return new String[] { WebProjectNature.ID };
	}

	protected String[] getProjectBuilders()
	{
		return new String[] { UnifiedBuilder.ID };
	}

	protected String getProjectCreationDescription()
	{
		return Messages.NewProjectWizard_CreateOp_Title;
	}
}
