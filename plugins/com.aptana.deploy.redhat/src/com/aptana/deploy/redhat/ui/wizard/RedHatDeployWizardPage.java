/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.deploy.redhat.ui.wizard;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.aptana.deploy.redhat.RedHatAPI;
import com.aptana.deploy.redhat.RedHatPlugin;

public class RedHatDeployWizardPage extends WizardPage
{

	public static final String NAME = "RedHatDeploy"; //$NON-NLS-1$

	private Text appName;
	private Combo typeCombo;

	protected RedHatDeployWizardPage()
	{
		super(NAME, Messages.RedHatDeployWizardPage_Title, null);
	}

	public void createControl(Composite parent)
	{
		Composite composite = new Composite(parent, SWT.NULL);
		composite.setLayout(new GridLayout());
		composite.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_FILL | GridData.HORIZONTAL_ALIGN_FILL));
		setControl(composite);

		initializeDialogUnits(parent);

		// Actual contents
		Composite appSettings = new Composite(composite, SWT.NULL);
		appSettings.setLayout(new GridLayout(2, false));

		Label label = new Label(appSettings, SWT.NONE);
		label.setText(Messages.RedHatDeployWizardPage_ApplicationNameLabel);

		appName = new Text(appSettings, SWT.SINGLE | SWT.BORDER);
		appName.setLayoutData(new GridData(250, SWT.DEFAULT));
		// Set default name to project name
		appName.setText(getProjectName());
		appName.addModifyListener(new ModifyListener()
		{

			public void modifyText(ModifyEvent e)
			{
				getContainer().updateButtons();
			}
		});

		Label typeLabel = new Label(appSettings, SWT.NONE);
		typeLabel.setText("Type:");

		typeCombo = new Combo(appSettings, SWT.SINGLE | SWT.BORDER | SWT.READ_ONLY);
		typeCombo.setLayoutData(new GridData(250, SWT.DEFAULT));
		typeCombo.add(RedHatAPI.PHP_5_3_2);
		typeCombo.add(RedHatAPI.RACK_1_1_0);
		typeCombo.add(RedHatAPI.WSGI_3_2_1);
		typeCombo.setText(getDefaultType(getProject()));

		Dialog.applyDialogFont(composite);
	}

	private String getDefaultType(IProject project)
	{
		try
		{
			// FIXME Check real radrails/pydev/php project nature rateh rthan this hack?
			for (String natureId : project.getDescription().getNatureIds())
			{
				if (natureId.contains("ruby")) //$NON-NLS-1$
				{
					return RedHatAPI.RACK_1_1_0;
				}
				else if (natureId.contains("php")) //$NON-NLS-1$
				{
					return RedHatAPI.PHP_5_3_2;
				}
				else if (natureId.contains("python")) //$NON-NLS-1$
				{
					return RedHatAPI.WSGI_3_2_1;
				}
			}
		}
		catch (CoreException e)
		{
			RedHatPlugin.logError(e);
		}
		return RedHatAPI.PHP_5_3_2;
	}

	protected String getProjectName()
	{
		IProject project = getProject();
		if (project == null)
		{
			return ""; // Seems like we have big issues if we ever got into this state... //$NON-NLS-1$
		}
		return project.getName();
	}

	protected IProject getProject()
	{
		RedHatDeployWizard wizard = (RedHatDeployWizard) getWizard();
		return wizard.getProject();
	}

	@Override
	public IWizardPage getNextPage()
	{
		// This is the end of the line!
		return null;
	}

	@Override
	public boolean isPageComplete()
	{
		// Make sure the app name is not blank
		String app = this.appName.getText();
		if (app == null || app.trim().length() < 1)
		{
			setErrorMessage(Messages.RedHatDeployWizardPage_EmotyApplicationNameError);
			return false;
		}

		setErrorMessage(null);
		return true;
	}

	public String getAppName()
	{
		return appName.getText();
	}

	public String getType()
	{
		return typeCombo.getText();
	}

	public IPath getDestination()
	{
		return getProject().getLocation();
	}
}
