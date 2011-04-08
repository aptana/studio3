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

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizardNode;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWizard;
import org.eclipse.ui.internal.dialogs.WizardCollectionElement;
import org.eclipse.ui.internal.dialogs.WorkbenchWizardElement;
import org.eclipse.ui.internal.dialogs.WorkbenchWizardListSelectionPage;
import org.eclipse.ui.internal.dialogs.WorkbenchWizardNode;
import org.eclipse.ui.internal.registry.WizardsRegistryReader;
import org.eclipse.ui.model.AdaptableList;
import org.eclipse.ui.wizards.IWizardCategory;
import org.eclipse.ui.wizards.IWizardRegistry;

/**
 * The new wizard is responsible for allowing the user to choose which new (nested) wizard to run. The set of available
 * new wizards comes from the new extension point.
 */
public class NewDeployWizard extends Wizard
{
	private IWorkbench theWorkbench;

	private IStructuredSelection selection;

	// the list selection page
	class SelectionPage extends WorkbenchWizardListSelectionPage
	{
		SelectionPage(IWorkbench w, IStructuredSelection ss, AdaptableList e, String s)
		{
			super(w, ss, e, s, null);
		}

		protected IWizardNode createWizardNode(WorkbenchWizardElement element)
		{
			return new WorkbenchWizardNode(this, element)
			{
				public IWorkbenchWizard createWizard() throws CoreException
				{
					return wizardElement.createWizard();
				}
			};
		}
	}

	/**
	 * Creates the wizard's pages lazily.
	 */
	public void addPages()
	{
		addPage(new SelectionPage(this.theWorkbench, this.selection, getAvailableExportWizards(),
				"Select a deployment provider: "));
	}

	/**
	 * Returns the export wizards that are available for invocation.
	 */
	protected AdaptableList getAvailableExportWizards()
	{
		IWizardRegistry reg = DeployWizardRegistry.getInstance();
		IWizardCategory root = reg.getRootCategory();
		WizardCollectionElement otherCategory = (WizardCollectionElement) root.findCategory(new Path(
				WizardsRegistryReader.UNCATEGORIZED_WIZARD_CATEGORY));
		if (otherCategory == null)
		{
			return new AdaptableList();
		}
		return otherCategory.getWizardAdaptableList();
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
		this.theWorkbench = aWorkbench;
		this.selection = currentSelection;

		setWindowTitle("Deploy");
		// setDefaultPageImageDescriptor(DeployPlugin
		// .getImageDescriptor("iocns/deploy.png"));
		setNeedsProgressMonitor(true);
	}

	/**
	 * Subclasses must implement this <code>IWizard</code> method to perform any special finish processing for their
	 * wizard.
	 */
	public boolean performFinish()
	{
		((SelectionPage) getPages()[0]).saveWidgetValues();
		return true;
	}
}