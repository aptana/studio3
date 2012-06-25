/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.deploy.internal.ui.handlers;

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

	protected void nextPressed()
	{
		IWizardPage page = getCurrentPage();
		if (page instanceof ILoginValidator && !((ILoginValidator) page).validateLogin())
		{
			return;
		}

		super.nextPressed();
	}
}
