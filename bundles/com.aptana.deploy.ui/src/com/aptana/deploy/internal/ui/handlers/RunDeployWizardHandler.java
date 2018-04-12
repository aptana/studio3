/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.deploy.internal.ui.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;

import com.aptana.deploy.internal.ui.handlers.DeployWizardDialog;
import com.aptana.deploy.internal.ui.handlers.Messages;
import com.aptana.deploy.ui.DeployUIPlugin;
import com.aptana.deploy.wizard.DeployWizard;

public class RunDeployWizardHandler extends AbstractHandler
{

	private static final String DEPLOY_WIZARD_SECTION = "DeployWizardAction"; //$NON-NLS-1$

	public Object execute(ExecutionEvent event) throws ExecutionException
	{
		IStructuredSelection selection = StructuredSelection.EMPTY;
		IWorkbenchPart activePart = HandlerUtil.getActivePart(event);
		if (activePart instanceof IEditorPart)
		{
			IEditorInput editorInput = ((IEditorPart) activePart).getEditorInput();
			if (editorInput instanceof IFileEditorInput)
			{
				selection = new StructuredSelection(((IFileEditorInput) editorInput).getFile());
			}
		}
		else
		{
			selection = (IStructuredSelection) HandlerUtil.getCurrentSelection(event);
		}

		// Instantiates and initializes the wizard
		DeployWizard wizard = new DeployWizard();
		wizard.init(activePart.getSite().getWorkbenchWindow().getWorkbench(), selection);
		wizard.setWindowTitle(Messages.DeployHandler_Wizard_Title);
		IDialogSettings workbenchSettings = DeployUIPlugin.getDefault().getDialogSettings();
		IDialogSettings wizardSettings = workbenchSettings.getSection(DEPLOY_WIZARD_SECTION);
		if (wizardSettings == null)
		{
			wizardSettings = workbenchSettings.addNewSection(DEPLOY_WIZARD_SECTION);
		}
		wizard.setDialogSettings(wizardSettings);
		wizard.setForcePreviousAndNextButtons(true);

		// Instantiates the wizard container with the wizard and opens it
		Shell shell = activePart.getSite().getShell();
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
