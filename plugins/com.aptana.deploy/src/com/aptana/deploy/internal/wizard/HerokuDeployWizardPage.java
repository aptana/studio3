/**
 * This file Copyright (c) 2005-2010 Aptana, Inc. This program is
 * dual-licensed under both the Aptana Public License and the GNU General
 * Public license. You may elect to use one or the other of these licenses.
 * 
 * This program is distributed in the hope that it will be useful, but
 * AS-IS and WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE, TITLE, or
 * NONINFRINGEMENT. Redistribution, except as permitted by whichever of
 * the GPL or APL you select, is prohibited.
 *
 * 1. For the GPL license (GPL), you can redistribute and/or modify this
 * program under the terms of the GNU General Public License,
 * Version 3, as published by the Free Software Foundation.  You should
 * have received a copy of the GNU General Public License, Version 3 along
 * with this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 * 
 * Aptana provides a special exception to allow redistribution of this file
 * with certain other free and open source software ("FOSS") code and certain additional terms
 * pursuant to Section 7 of the GPL. You may view the exception and these
 * terms on the web at http://www.aptana.com/legal/gpl/.
 * 
 * 2. For the Aptana Public License (APL), this program and the
 * accompanying materials are made available under the terms of the APL
 * v1.0 which accompanies this distribution, and is available at
 * http://www.aptana.com/legal/apl/.
 * 
 * You may view the GPL, Aptana's exception and additional terms, and the
 * APL in the file titled license.html at the root of the corresponding
 * plugin containing this source file.
 * 
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.deploy.internal.wizard;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
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

import com.aptana.deploy.Activator;
import com.aptana.deploy.preferences.IPreferenceConstants;
import com.aptana.deploy.wizard.DeployWizard;
import com.aptana.git.core.GitPlugin;
import com.aptana.git.core.model.GitRepository;

public class HerokuDeployWizardPage extends WizardPage
{

	private static final String HEROKU_ICON = "icons/heroku.png"; //$NON-NLS-1$

	public static final String NAME = "HerokuDeploy"; //$NON-NLS-1$

	private Text appName;
	private Button publishButton;

	protected HerokuDeployWizardPage()
	{
		super(NAME, Messages.HerokuDeployWizardPage_Title, Activator.getImageDescriptor(HEROKU_ICON));
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
		publishButton.setSelection(Platform.getPreferencesService().getBoolean(Activator.getPluginIdentifier(),
				IPreferenceConstants.HEROKU_AUTO_PUBLISH, true, null));

		if (doesntHaveGitRepo())
		{
			Label note = new Label(composite, SWT.WRAP);
			// We need this italic, we may need to set a font explicitly here to get it
			Font dialogFont = JFaceResources.getDialogFont();
			FontData[] data = dialogFont.getFontData();
			for (FontData dataElement : data)
			{
				dataElement.setStyle(dataElement.getStyle() | SWT.ITALIC);
			}
			Font italic = new Font(dialogFont.getDevice(), data);
			note.setFont(italic);

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
		DeployWizard wizard = (DeployWizard) getWizard();
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
