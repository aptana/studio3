/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.deploy.capistrano.ui.wizard;

import java.net.URL;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
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
import org.eclipse.swt.widgets.Link;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.browser.IWebBrowser;
import org.eclipse.ui.browser.IWorkbenchBrowserSupport;

import com.aptana.core.logging.IdeLog;
import com.aptana.deploy.capistrano.CapistranoPlugin;
import com.aptana.terminal.widget.TerminalComposite;

public class CapifyProjectPage extends WizardPage
{

	public static final String NAME = "CapifyProject"; //$NON-NLS-1$
	private TerminalComposite terminalComposite;
	protected Job checkCapifiedJob;

	protected CapifyProjectPage()
	{
		super(NAME, Messages.CapifyProjectPage_Title, null);
	}

	public void createControl(Composite parent)
	{
		Composite composite = new Composite(parent, SWT.NULL);
		composite.setLayout(new GridLayout());
		composite.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_FILL | GridData.HORIZONTAL_ALIGN_FILL));
		setControl(composite);

		initializeDialogUnits(parent);

		// TODO What if file already exists? Do we disable the button? do we even show this page?

		// Actual contents
		Label label = new Label(composite, SWT.WRAP);
		label.setText(Messages.CapifyProjectPage_Description);
		label.setLayoutData(new GridData(500, SWT.DEFAULT));

		Button generateButton = new Button(composite, SWT.PUSH);
		generateButton.setText(Messages.CapifyProjectPage_GenerateButtonLabel);
		generateButton.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false));
		generateButton.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				if (!terminalComposite.isEmpty())
				{
					terminalComposite.clear();
				}
				// Send "capify" command to terminal
				terminalComposite.sendInput("capify .\n"); //$NON-NLS-1$

				// Poll to check if capistrano is installed
				if (checkCapifiedJob == null)
				{
					checkCapifiedJob = new Job("Checking if config/deploy.rb exists") //$NON-NLS-1$
					{
						protected IStatus run(IProgressMonitor monitor)
						{
							if (monitor != null && monitor.isCanceled())
							{
								return Status.CANCEL_STATUS;
							}
							if (isPageComplete())
							{
								PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable()
								{

									public void run()
									{
										getContainer().updateButtons(); // ok update the wizard TODO Just finish for
																		// them?
									}
								});
							}
							else
							{
								schedule(1000); // check again in a second
							}
							return Status.OK_STATUS;
						}
					};
					checkCapifiedJob.setSystem(true);
				}
				checkCapifiedJob.cancel();
				checkCapifiedJob.schedule(1000);
			}
		});

		// Terminal
		terminalComposite = new TerminalComposite(composite, SWT.NONE);
		terminalComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		// TODO Can we prevent user input to this terminal?
		terminalComposite.setWorkingDirectory(getProject().getLocation());
		terminalComposite.connect();

		// label/link
		Link link = new Link(composite, SWT.NONE);
		link.setText(Messages.CapifyProjectPage_LinkText);
		link.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				// Open browser window inside studio to url without closing wizard!
				IWorkbenchBrowserSupport support = PlatformUI.getWorkbench().getBrowserSupport();
				try
				{
					IWebBrowser browser = support.createBrowser(IWorkbenchBrowserSupport.AS_EDITOR, "capify", null, //$NON-NLS-1$
							null);
					browser.openURL(new URL(e.text));
				}
				catch (Exception e1)
				{
					IdeLog.logError(CapistranoPlugin.getDefault(), e1);
				}
			}
		});

		Dialog.applyDialogFont(composite);
	}

	@Override
	public IWizardPage getNextPage()
	{
		// The end of the line
		return null;
	}

	@Override
	public boolean isPageComplete()
	{
		// Check if the config/deploy.rb file exists!
		IProject project = getProject();
		IFile deployFile = project.getFile(new Path("config").append("deploy.rb")); //$NON-NLS-1$ //$NON-NLS-2$
		return deployFile != null && deployFile.exists();
	}

	protected IProject getProject()
	{
		return ((CapistranoDeployWizard) getWizard()).getProject();
	}

	@Override
	public void dispose()
	{
		try
		{
			if (checkCapifiedJob != null)
			{
				checkCapifiedJob.cancel();
				checkCapifiedJob = null;
			}
		}
		finally
		{
			super.dispose();
		}
	}
}
