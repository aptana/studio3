/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.deploy.internal.wizard;

import java.io.File;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
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
import org.eclipse.ui.PlatformUI;

import com.aptana.core.util.ExecutableUtil;
import com.aptana.core.util.ProcessUtil;
import com.aptana.deploy.wizard.DeployWizard;
import com.aptana.terminal.widget.TerminalComposite;

public class InstallCapistranoGemPage extends WizardPage
{

	private static final String NAME = "InstallCapistrano"; //$NON-NLS-1$
	private TerminalComposite terminalComposite;
	private IWizardPage fNextPage;
	protected Job checkGemInstalledJob;

	protected InstallCapistranoGemPage()
	{
		super(NAME, Messages.InstallCapistranoGemPage_Title, null);
	}

	public void createControl(Composite parent)
	{
		Composite composite = new Composite(parent, SWT.NULL);
		composite.setLayout(new GridLayout());
		composite.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_FILL | GridData.HORIZONTAL_ALIGN_FILL));
		setControl(composite);

		initializeDialogUnits(parent);

		// Actual contents
		Label label = new Label(composite, SWT.WRAP);
		label.setText(Messages.InstallCapistranoGemPage_Description);
		label.setLayoutData(new GridData(500, SWT.DEFAULT));

		Button generateButton = new Button(composite, SWT.PUSH);
		generateButton.setText(Messages.InstallCapistranoGemPage_InstallGemLabel);
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

				// Need to check to see if we should run under sudo to install gem...
				if (!Platform.getOS().equals(Platform.OS_WIN32))
				{
					// TODO This code is pretty blase about possible nulls/errors/etc. Should probably try and make it
					// more bullet-proof.

					// grab the path to the gem executable dir
					IPath gemBin = ExecutableUtil.find("gem", true, null); //$NON-NLS-1$
					String output = ProcessUtil.outputForCommand(gemBin.toOSString(), null, "environment"); //$NON-NLS-1$
					final String searchString = "EXECUTABLE DIRECTORY:"; //$NON-NLS-1$
					int index = output.indexOf(searchString);
					output = output.substring(index + searchString.length());
					// find first newline...
					output = output.split("\r\n|\r|\n")[0].trim(); //$NON-NLS-1$
					// Now see if user has rights to write to this dir to determine if we need to run under sudo
					if (!new File(output).canWrite())
					{
						// Does not have permission
						terminalComposite.sendInput("sudo "); //$NON-NLS-1$
					}
				}

				// install gem
				terminalComposite.sendInput("gem install capistrano\n"); //$NON-NLS-1$

				// Poll to check if capistrano is installed
				if (checkGemInstalledJob == null)
				{
					checkGemInstalledJob = new Job("Checking if capistrano gem is installed") //$NON-NLS-1$
					{
						protected IStatus run(IProgressMonitor monitor)
						{
							if (monitor != null && monitor.isCanceled())
							{
								return Status.CANCEL_STATUS;
							}
							if (isCapistranoGemInstalled())
							{
								PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable()
								{

									public void run()
									{
										getContainer().updateButtons(); // ok update the wizard
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
					checkGemInstalledJob.setSystem(true);
				}
				checkGemInstalledJob.cancel();
				checkGemInstalledJob.schedule(1000);
			}
		});

		// Terminal
		terminalComposite = new TerminalComposite(composite, SWT.NONE);
		terminalComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		terminalComposite.setWorkingDirectory(getProject().getLocation());
		terminalComposite.connect();

		Dialog.applyDialogFont(composite);
	}

	@Override
	public void dispose()
	{
		try
		{
			if (checkGemInstalledJob != null)
			{
				checkGemInstalledJob.cancel();
				checkGemInstalledJob = null;
			}
		}
		finally
		{
			super.dispose();
		}
	}

	@Override
	public IWizardPage getNextPage()
	{
		if (fNextPage == null)
		{
			fNextPage = new CapifyProjectPage();
			fNextPage.setWizard(getWizard());
		}
		return fNextPage;
	}

	@Override
	public boolean isPageComplete()
	{
		return isCapistranoGemInstalled();
	}

	static boolean isCapistranoGemInstalled()
	{
		// Determine if capistrano is installed
		IPath path = ExecutableUtil.find("capify", false, null); //$NON-NLS-1$
		if (path != null && path.toFile().exists())
		{
			return true;
		}
		return false;
	}

	protected IProject getProject()
	{
		return ((DeployWizard) getWizard()).getProject();
	}
}
