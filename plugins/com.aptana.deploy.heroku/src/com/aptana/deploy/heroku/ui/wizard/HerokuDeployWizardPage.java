/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.deploy.heroku.ui.wizard;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.aptana.deploy.heroku.HerokuPlugin;
import com.aptana.deploy.heroku.preferences.IPreferenceConstants;
import com.aptana.git.core.GitPlugin;
import com.aptana.git.core.model.GitRepository;
import com.aptana.ui.util.SWTUtils;

public class HerokuDeployWizardPage extends WizardPage
{
	public static final String NAME = "HerokuDeploy"; //$NON-NLS-1$

	private Text appName;
	private Button publishButton;

	protected HerokuDeployWizardPage()
	{
		super(NAME, Messages.HerokuDeployWizardPage_Title, null);
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
		label.setText(Messages.HerokuDeployWizardPage_ApplicationNameLabel);

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

		publishButton = new Button(composite, SWT.CHECK);
		publishButton.setText(Messages.HerokuDeployWizardPage_PublishApplicationLabel);
		publishButton.setSelection(Platform.getPreferencesService().getBoolean(HerokuPlugin.getPluginIdentifier(),
				IPreferenceConstants.HEROKU_AUTO_PUBLISH, true, null));

		if (doesntHaveGitRepo())
		{
			Label note = new Label(composite, SWT.WRAP);
			// We need this italic, we may need to set a font explicitly here to get it
			Font dialogFont = JFaceResources.getDialogFont();
			FontData[] data = SWTUtils.italicizedFont(JFaceResources.getDialogFont());
			final Font italic = new Font(dialogFont.getDevice(), data);
			note.setFont(italic);
			note.addDisposeListener(new DisposeListener()
			{
				public void widgetDisposed(DisposeEvent e)
				{
					if (!italic.isDisposed())
					{
						italic.dispose();
					}
				}
			});

			note.setLayoutData(new GridData(400, SWT.DEFAULT));
			note.setText(Messages.HerokuDeployWizardPage_NoGitRepoNote);
		}

		Dialog.applyDialogFont(composite);
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

	protected boolean doesntHaveGitRepo()
	{
		IProject project = getProject();
		if (project == null)
		{
			return false; // Seems like we have big issues if we ever got into this state...
		}
		GitRepository repo = GitPlugin.getDefault().getGitRepositoryManager()
				.getUnattachedExisting(project.getLocationURI());
		return repo == null;
	}

	protected IProject getProject()
	{
		HerokuDeployWizard wizard = (HerokuDeployWizard) getWizard();
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
			setErrorMessage(Messages.HerokuDeployWizardPage_EmotyApplicationNameError);
			return false;
		}

		setErrorMessage(null);
		return true;
	}

	public String getAppName()
	{
		return appName.getText();
	}

	public boolean publishImmediately()
	{
		return publishButton.getSelection();
	}
}
