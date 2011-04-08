/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.deploy.internal.wizard;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import com.aptana.deploy.preferences.DeployPreferenceUtil;
import com.aptana.deploy.preferences.IPreferenceConstants.DeployType;

public class DeployWizardPage extends WizardPage
{

	public static final String NAME = "Deployment"; //$NON-NLS-1$

	private Button deployWithCapistrano;

	private IProject project;

	public DeployWizardPage(IProject project)
	{
		super(NAME, Messages.DeployWizardPage_Title, null);
		this.project = project;
	}

	public void createControl(Composite parent)
	{
		Composite composite = new Composite(parent, SWT.NULL);
		composite.setLayout(new GridLayout());
		composite.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_FILL | GridData.HORIZONTAL_ALIGN_FILL));
		setControl(composite);

		initializeDialogUnits(parent);

		// Actual contents
		Label label = new Label(composite, SWT.NONE);

		DeployType type = DeployPreferenceUtil.getDeployType(project);
		label.setText(Messages.DeployWizardPage_DeploymentOptionsLabel);

		deployWithCapistrano = new Button(composite, SWT.RADIO);
		deployWithCapistrano.setText(Messages.DeployWizardPage_CapistranoLabel);
		deployWithCapistrano.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				setImageDescriptor(null);
			}
		});

		Dialog.applyDialogFont(composite);
	}

	@Override
	public boolean canFlipToNextPage()
	{
		// user can always move on, and we don't want getNextPage() getting called quickly since it tries to actually
		// auth against Heroku...
		return true;
	}

	@Override
	public IWizardPage getNextPage()
	{
		// TODO Should hold onto "next" page and dispose it if user progress back and forth here since we keep
		// re-creating new objects for next page.
		IWizardPage nextPage = null;
		// Determine what page is next by the user's choice in the radio buttons
		if (deployWithCapistrano != null && deployWithCapistrano.getSelection())
		{
			if (InstallCapistranoGemPage.isCapistranoGemInstalled())
			{
				nextPage = new CapifyProjectPage();
			}
			else
			{
				nextPage = new InstallCapistranoGemPage();
			}
		}

		if (nextPage == null)
		{
			nextPage = super.getNextPage();
		}
		if (nextPage != null)
		{
			nextPage.setWizard(getWizard());
		}
		return nextPage;
	}

	@Override
	public IWizardPage getPreviousPage()
	{
		return null;
	}
}
