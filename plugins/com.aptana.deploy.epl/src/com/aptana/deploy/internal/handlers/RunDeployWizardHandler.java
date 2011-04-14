/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 */
package com.aptana.deploy.internal.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;

import com.aptana.deploy.DeployPlugin;
import com.aptana.deploy.wizard.DeployWizard;

public class RunDeployWizardHandler extends AbstractHandler
{

	public Object execute(ExecutionEvent event) throws ExecutionException
	{
		IWorkbenchPart part = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActivePart();

		// Instantiates and initializes the wizard
		DeployWizard wizard = new DeployWizard();

		wizard.init(part.getSite().getWorkbenchWindow().getWorkbench(), (IStructuredSelection) part.getSite()
				.getSelectionProvider().getSelection());
		wizard.setWindowTitle(Messages.DeployHandler_Wizard_Title);
		IDialogSettings workbenchSettings = DeployPlugin.getDefault().getDialogSettings();
		IDialogSettings wizardSettings = workbenchSettings.getSection("DeployWizardAction"); //$NON-NLS-1$
		if (wizardSettings == null)
		{
			wizardSettings = workbenchSettings.addNewSection("DeployWizardAction"); //$NON-NLS-1$
		}
		wizard.setDialogSettings(wizardSettings);
		wizard.setForcePreviousAndNextButtons(true);

		// Instantiates the wizard container with the wizard and opens it
		Shell shell = part.getSite().getShell();
		if (shell == null)
		{
			shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
		}
		// FIXME Don't use this special dialog! move ILoginValidator stuff to page change listener stuff when necessary!
		DeployWizardDialog dialog = new DeployWizardDialog(shell, wizard);
		dialog.setPageSize(350, 500);
		dialog.setHelpAvailable(false);
		dialog.create();
		dialog.open();

		return null;
	}
}
