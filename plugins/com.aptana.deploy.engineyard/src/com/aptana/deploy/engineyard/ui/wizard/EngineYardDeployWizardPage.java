/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.deploy.engineyard.ui.wizard;

import java.net.URL;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
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

import com.aptana.core.logging.IdeLog;
import com.aptana.deploy.engineyard.EngineYardPlugin;
import com.aptana.ui.util.SWTUtils;

public class EngineYardDeployWizardPage extends WizardPage
{

	private static final String EY_ICON = "icons/ey_small_wizard.png"; //$NON-NLS-1$

	public static final String NAME = "EngineYardDeploy"; //$NON-NLS-1$

	protected EngineYardDeployWizardPage()
	{
		super(NAME, Messages.EngineYardDeployWizardPage_Title, EngineYardPlugin.getImageDescriptor(EY_ICON));
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
							IdeLog.logError(EngineYardPlugin.getDefault(), e);
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
		EngineYardDeployWizard wizard = (EngineYardDeployWizard) getWizard();
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
