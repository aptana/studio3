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

import java.io.File;
import java.text.MessageFormat;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import com.aptana.deploy.Activator;
import com.aptana.deploy.EngineYardAPI;
import com.aptana.deploy.HerokuAPI;
import com.aptana.deploy.preferences.DeployPreferenceUtil;
import com.aptana.deploy.preferences.IPreferenceConstants.DeployType;
import com.aptana.deploy.wizard.DeployWizard;

public class DeployWizardPage extends WizardPage
{

	public static final String NAME = "Deployment"; //$NON-NLS-1$
	private static final String HEROKU_IMG_PATH = "icons/heroku.png"; //$NON-NLS-1$
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
			setImageDescriptor(Activator.getImageDescriptor(HEROKU_IMG_PATH));
			label.setText(Messages.DeployWizardPage_ProvidersLabel);
			// deploy with Heroku
			deployWithHeroku = new Button(composite, SWT.RADIO);
			deployWithHeroku.setImage(Activator.getImage(HEROKU_IMG_PATH));
			// disable the button if the project is currently deployed to Heroku
			boolean couldDeployWithHeroku = (type == null || type != DeployType.HEROKU);
			deployWithHeroku.setEnabled(couldDeployWithHeroku);
			deployWithHeroku.setSelection(couldDeployWithHeroku);
			if (!couldDeployWithHeroku)
			{
				String app = DeployPreferenceUtil.getDeployEndpoint(project);
				if (app == null)
				{
					app = "Heroku"; //$NON-NLS-1$
				}
				deployWithHeroku.setText(MessageFormat.format(Messages.DeployWizardPage_AlreadyDeployedToHeroku, app));
			}

			deployWithHeroku.addMouseListener(new MouseAdapter()
			{
				@Override
				public void mouseDown(MouseEvent e)
				{
					super.mouseDown(e);
					// If the image is clicked treat it like selecting and clicking Next button!
					Rectangle deployBounds = deployWithHeroku.getBounds();
					Rectangle imageBounds = deployWithHeroku.getImage().getBounds();
					int x = deployBounds.width - imageBounds.width;
					imageBounds.x = x;
					if (imageBounds.contains(e.x, e.y))
					{
						if (isPageComplete())
						{
							if (deployWithHeroku.getSelection())
								getContainer().showPage(getNextPage());
							else
								deployWithHeroku.setSelection(true);
						}
					}
				}
			});

			deployWithHeroku.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					setImageDescriptor(Activator.getImageDescriptor(HEROKU_IMG_PATH));
				}
			});

			// Deploy with Engine Yard
			if(!Platform.OS_WIN32.equals(Platform.getOS()))
			{
				
				deployWithEngineYard = new Button(composite, SWT.RADIO);
				deployWithEngineYard.setImage(Activator.getImage(EY_IMG_PATH));
				
				// disable the button if the project is currently deployed to Engine Yard
				boolean couldDeployWithEY = (type == null || type != DeployType.ENGINEYARD);
				deployWithEngineYard.setEnabled(couldDeployWithEY);
				if (!couldDeployWithHeroku)
				{
					deployWithEngineYard.setSelection(couldDeployWithEY);
					setImageDescriptor(Activator.getImageDescriptor(EY_IMG_PATH));
				}
				if (!couldDeployWithEY)
				{
					String app = DeployPreferenceUtil.getDeployEndpoint(project);
					if (app == null)
					{
						app = "Engine Yard"; //$NON-NLS-1$
					}
					deployWithEngineYard.setText(MessageFormat.format(Messages.DeployWizardPage_AlreadyDeployedToHeroku, app));
				}
	
				deployWithEngineYard.addSelectionListener(new SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent e) {
						setImageDescriptor(Activator.getImageDescriptor(EY_IMG_PATH));
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
		deployWithFTP.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				setImageDescriptor(Activator.getImageDescriptor(FTP_IMG_PATH));
			}
		});
		if ((deployWithHeroku == null || !deployWithHeroku.getEnabled()) &&( deployWithEngineYard == null || !deployWithEngineYard.getEnabled()))
		{
			deployWithFTP.setSelection(true);
			setImageDescriptor(Activator.getImageDescriptor(FTP_IMG_PATH));
		}

		deployWithCapistrano = new Button(composite, SWT.RADIO);
		deployWithCapistrano.setText(Messages.DeployWizardPage_CapistranoLabel);
		deployWithCapistrano.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
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
			Activator.logError(e);
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
		if (deployWithHeroku != null && deployWithHeroku.getSelection())
		{
			File credentials = HerokuAPI.getCredentialsFile();
			if (credentials.exists() && HerokuAPI.fromCredentials().authenticate().isOK())
			{
				nextPage = new HerokuDeployWizardPage();
			}
			else
			{
				nextPage = new HerokuLoginWizardPage();
			}
		}
		else if (deployWithFTP.getSelection())
		{
			nextPage = new FTPDeployWizardPage(project);
		}
		else if (deployWithCapistrano.getSelection())
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
		else if (deployWithEngineYard.getSelection())
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
