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

import java.net.URL;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.browser.IWebBrowser;
import org.eclipse.ui.browser.IWorkbenchBrowserSupport;

import com.aptana.deploy.Activator;
import com.aptana.deploy.wizard.DeployWizard;

public class EngineYardDeployWizardPage extends WizardPage
{

	private static final String EY_ICON = "icons/ey_small.png"; //$NON-NLS-1$

	public static final String NAME = "EngineYardDeploy"; //$NON-NLS-1$

	protected EngineYardDeployWizardPage()
	{
		super(NAME, Messages.EngineYardDeployWizardPage_Title, Activator.getImageDescriptor(EY_ICON));
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
		label.setText(Messages.EngineYardDeployWizardPage_ApplicationNameLabel);

		Label note = new Label(composite, SWT.WRAP);
		Font dialogFont = JFaceResources.getDialogFont();
		FontData[] data = dialogFont.getFontData();
		for (FontData dataElement : data)
		{
			dataElement.setStyle(dataElement.getStyle() | SWT.ITALIC);
		}
		Font italic = new Font(dialogFont.getDevice(), data);
		note.setFont(italic);
		note.setLayoutData(new GridData(400, SWT.DEFAULT));
		note.setText(Messages.EngineYardDeployWizardPage_ApplicationNoteLabel);

		// Link to Engine Yard dashbaord
		Link link = new Link(composite, SWT.NONE);
		link.setText(Messages.EngineYardDeployWizardPage_ApplicationLinkLabel);
		link.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable()
				{

					public void run()
					{
						try
						{
							final String BROWSER_ID = "EngineYard-login"; //$NON-NLS-1$
							final URL url = new URL("https://cloud.engineyard.com/dashboard"); //$NON-NLS-1$

							final int style = IWorkbenchBrowserSupport.NAVIGATION_BAR
									| IWorkbenchBrowserSupport.LOCATION_BAR | IWorkbenchBrowserSupport.STATUS;
							
							IWorkbenchBrowserSupport workbenchBrowserSupport = PlatformUI.getWorkbench().getBrowserSupport();
							
							if(workbenchBrowserSupport.isInternalWebBrowserAvailable()) {
								IWebBrowser webBrowser = workbenchBrowserSupport.createBrowser(style, BROWSER_ID, null, null);
								if (webBrowser != null)
									webBrowser.openURL(url);
							}
							
						}
						catch (Exception e)
						{
							Activator.logError(e);
						}
					}
				});

				// close the wizard when the user clicks on the dashboard link
				((WizardDialog) getContainer()).close();

			}
		});

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
		setErrorMessage(null);
		return true;
	}
}
