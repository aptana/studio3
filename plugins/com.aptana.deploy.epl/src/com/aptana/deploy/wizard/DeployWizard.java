/*******************************************************************************
 * Copyright (c) 2000, 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package com.aptana.deploy.wizard;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IWorkbench;

// FIXME Model after ImportExportWizard, which uses a tree with categories for wizards!
public class DeployWizard extends Wizard
{

	private IWorkbench workbench;
	private IStructuredSelection selection;
	private DeployWizardPage deployPage;

	/**
	 * Subclasses must implement this <code>IWizard</code> method to perform any special finish processing for their
	 * wizard.
	 */
	public boolean performFinish()
	{
		deployPage.saveWidgetValues();
		return true;
	}

	/**
	 * Creates the wizard's pages lazily.
	 */
	public void addPages()
	{
		deployPage = new DeployWizardPage(this.workbench, this.selection);
		addPage(deployPage);
	}

	/**
	 * Initializes the wizard.
	 * 
	 * @param aWorkbench
	 *            the workbench
	 * @param currentSelection
	 *            the current selectio
	 */
	public void init(IWorkbench aWorkbench, IStructuredSelection currentSelection)
	{
		this.workbench = aWorkbench;
		this.selection = currentSelection;

		setNeedsProgressMonitor(true);
	}
}