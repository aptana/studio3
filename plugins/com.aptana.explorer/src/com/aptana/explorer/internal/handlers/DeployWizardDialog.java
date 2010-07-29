package com.aptana.explorer.internal.handlers;

import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;

import com.aptana.deploy.ILoginValidator;

public class DeployWizardDialog extends WizardDialog
{

	public DeployWizardDialog(Shell parentShell, IWizard newWizard)
	{
		super(parentShell, newWizard);
	}
	
	protected void nextPressed(){
		
		IWizardPage page = getCurrentPage();
		
		if( page instanceof ILoginValidator && !((ILoginValidator) page).validateLogin())
		{
			return;
		}
		
		super.nextPressed();
	}

}
