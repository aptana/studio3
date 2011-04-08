/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.deploy.internal.wizard;

import java.io.File;
import java.text.MessageFormat;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Platform;
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

import com.aptana.deploy.DeployPlugin;
import com.aptana.deploy.EngineYardAPI;
import com.aptana.deploy.preferences.DeployPreferenceUtil;
import com.aptana.deploy.preferences.IPreferenceConstants.DeployType;
import com.aptana.deploy.wizard.DeployWizard;

public class DeployWizardPage extends WizardPage
{

	public static final String NAME = "Deployment"; //$NON-NLS-1$
	private static final String FTP_IMG_PATH = "icons/ftp.png"; //$NON-NLS-1$
	private static final String EY_IMG_PATH = "icons/ey_small.png"; //$NON-NLS-1$

	private Button deployWithFTP;
	private Button deployWithCapistrano;
	private Button deployWithHeroku;
	private Button deployWithEngineYard;

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
		if (isRailsProject())
		{
			label.setText(Messages.DeployWizardPage_ProvidersLabel);

			// Deploy with Engine Yard
			if (!Platform.OS_WIN32.equals(Platform.getOS()))
			{

				deployWithEngineYard = new Button(composite, SWT.RADIO);
				deployWithEngineYard.setImage(DeployPlugin.getImage(EY_IMG_PATH));

				// disable the button if the project is currently deployed to Engine Yard
				boolean couldDeployWithEY = (type == null || type != DeployType.ENGINEYARD);
				deployWithEngineYard.setEnabled(couldDeployWithEY);
				setImageDescriptor(DeployPlugin.getImageDescriptor(EY_IMG_PATH));

				if (!couldDeployWithEY)
				{
					String app = DeployPreferenceUtil.getDeployEndpoint(project);
					if (app == null)
					{
						app = "Engine Yard"; //$NON-NLS-1$
					}
					deployWithEngineYard.setText(MessageFormat.format(
							Messages.DeployWizardPage_AlreadyDeployedToHeroku, app));
				}

				deployWithEngineYard.addSelectionListener(new SelectionAdapter()
				{
					@Override
					public void widgetSelected(SelectionEvent e)
					{
						setImageDescriptor(DeployPlugin.getImageDescriptor(EY_IMG_PATH));
					}
				});
			}

			label = new Label(composite, SWT.NONE);
			label.setText(Messages.DeployWizardPage_OtherDeploymentOptionsLabel);
		}
		else
		{
			label.setText(Messages.DeployWizardPage_DeploymentOptionsLabel);
		}

		// "Other" Deployment options radio button group
		deployWithFTP = new Button(composite, SWT.RADIO);
		deployWithFTP.setText(Messages.DeployWizardPage_FTPLabel);
		deployWithFTP.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				setImageDescriptor(DeployPlugin.getImageDescriptor(FTP_IMG_PATH));
			}
		});
		if ((deployWithHeroku == null || !deployWithHeroku.getEnabled())
				&& (deployWithEngineYard == null || !deployWithEngineYard.getEnabled()))
		{
			deployWithFTP.setSelection(true);
			setImageDescriptor(DeployPlugin.getImageDescriptor(FTP_IMG_PATH));
		}

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

	private boolean isRailsProject()
	{
		try
		{
			IProject project = ((DeployWizard) getWizard()).getProject();
			// project.hasNature(RailsProjectNature.ID)
			return project.hasNature("org.radrails.rails.core.railsnature"); //$NON-NLS-1$
		}
		catch (CoreException e)
		{
			DeployPlugin.logError(e);
		}
		return false;
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
		if (deployWithFTP != null && deployWithFTP.getSelection())
		{
			nextPage = new FTPDeployWizardPage(project);
		}
		else if (deployWithCapistrano != null && deployWithCapistrano.getSelection())
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
		else if (deployWithEngineYard != null && deployWithEngineYard.getSelection())
		{
			EngineYardAPI api = new EngineYardAPI();
			File credentials = EngineYardAPI.getCredentialsFile();
			// if credentials are valid, go to EngineYardDeployWizardPage
			if (credentials.exists() && api.authenticateFromCredentials().isOK())
			{
				nextPage = new EngineYardDeployWizardPage();
			}
			else
			{
				nextPage = new EngineYardLoginWizardPage();
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
