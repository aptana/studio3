package com.aptana.deploy.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;

import com.aptana.deploy.wizard.DeployWizard;

public class DeployAction extends Action implements IObjectActionDelegate
{

	private ISelection selection;
	private IWorkbenchPart part;

	@Override
	public void run(IAction action)
	{
		// Instantiates and initializes the wizard
		DeployWizard wizard = new DeployWizard();
		wizard.init(part.getSite().getWorkbenchWindow().getWorkbench(), (IStructuredSelection) selection);

		// Instantiates the wizard container with the wizard and opens it
		Shell shell = part.getSite().getShell();
		if (shell == null)
		{
			shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
		}
		WizardDialog dialog = new WizardDialog(shell, wizard);
		dialog.setPageSize(350, 470);
		dialog.create();
		dialog.open();
	}

	@Override
	public void selectionChanged(IAction action, ISelection selection)
	{
		this.selection = selection;
	}

	@Override
	public void setActivePart(IAction action, IWorkbenchPart targetPart)
	{
		this.part = targetPart;

	}
}
