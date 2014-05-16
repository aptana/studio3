/**
 * Aptana Studio
 * Copyright (c) 2012-2013 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.projects.wizards;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IExecutableExtension;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.swt.widgets.Composite;

/**
 * @author Nam Le <nle@appcelerator.com>
 */
public interface IProjectWizardContributor extends IExecutableExtension
{

	/**
	 * Responsible for creating the project wizard pages
	 * 
	 * @return
	 */
	public IWizardPage createWizardPage(Object data);

	/**
	 * Performs any specific finalization on all wizard pages
	 * 
	 * @param page
	 */
	public void finalizeWizardPage(IWizardPage page);

	/**
	 * Responsible for contributing UI to the project creation page of the wizard
	 * 
	 * @param data
	 * @param page
	 * @param parent
	 */
	public void appendProjectCreationPage(Object data, IWizardPage page, Composite parent);

	/**
	 * Responsible for contributing UI to a Sample project creation page of the wizard
	 * 
	 * @param data
	 * @param page
	 * @param parent
	 */
	public void appendSampleProjectCreationPage(Object data, IWizardPage page, Composite parent);

	/**
	 * Called to update the UI for the contributor
	 * 
	 * @param data
	 */
	public void updateProjectCreationPage(Object data);

	/**
	 * Validates the project settings
	 * 
	 * @param data
	 * @return
	 */
	public IStatus validateProjectCreationPage(Object data);

	/**
	 * Returns the check whether this nature ids passed matches the contributor natureid
	 * 
	 * @param natureIds
	 * @return
	 */
	public boolean hasNatureId(String[] natureIds);

	/**
	 * Performs the work to finish the wizard. Provides an optional monitor to record progress
	 * 
	 * @param project
	 * @param monitor
	 * @return
	 */
	public IStatus performWizardFinish(IProject project, IProgressMonitor monitor);
}
