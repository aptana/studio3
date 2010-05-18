package com.aptana.deploy.wizard;

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
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.tm.internal.terminal.control.ITerminalListener;
import org.eclipse.tm.internal.terminal.provisional.api.ITerminalConnector;
import org.eclipse.tm.internal.terminal.provisional.api.TerminalConnectorExtension;
import org.eclipse.tm.internal.terminal.provisional.api.TerminalState;
import org.eclipse.ui.PlatformUI;

import com.aptana.core.util.ExecutableUtil;
import com.aptana.core.util.ProcessUtil;
import com.aptana.terminal.connector.LocalTerminalConnector;
import com.aptana.terminal.internal.emulator.VT100TerminalControl;

@SuppressWarnings("restriction")
public class InstallCapistranoGemPage extends WizardPage
{

	static final String NAME = "InstallCapistrano"; //$NON-NLS-1$
	private VT100TerminalControl fCtlTerminal;
	private IWizardPage fNextPage;
	protected Job checkGemInstalledJob;

	protected InstallCapistranoGemPage()
	{
		super(NAME, Messages.InstallCapistranoGemPage_Title, null);
	}

	@Override
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
				if (!fCtlTerminal.isEmpty())
				{
					fCtlTerminal.clearTerminal();
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
						fCtlTerminal.pasteString("sudo "); //$NON-NLS-1$
					}
				}

				// install gem
				fCtlTerminal.pasteString("gem install capistrano\n"); //$NON-NLS-1$

				// Poll to check if capistrano is installed
				if (checkGemInstalledJob == null)
				{
					checkGemInstalledJob = new Job("Checking if capistrano gem is installed")
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

									@Override
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
		Composite terminal = new Composite(composite, SWT.NONE);
		terminal.setLayout(new FillLayout());
		terminal.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		// TODO Can we prevent user input to this terminal?
		fCtlTerminal = new VT100TerminalControl(new ITerminalListener()
		{

			@Override
			public void setState(TerminalState state)
			{
				// do nothing
			}

			@Override
			public void setTerminalTitle(String title)
			{
				// do nothing
			}

		}, terminal, getTerminalConnectors());
		fCtlTerminal.setConnector(fCtlTerminal.getConnectors()[0]);
		fCtlTerminal.connectTerminal();

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
			if (fCtlTerminal != null)
			{
				fCtlTerminal.disposeTerminal();
			}
		}
		finally
		{
			super.dispose();
		}
	}

	private ITerminalConnector[] getTerminalConnectors()
	{
		ITerminalConnector connector = TerminalConnectorExtension.makeTerminalConnector(LocalTerminalConnector.ID);
		if (connector != null)
		{
			connector.getInitializationErrorMessage();
		}
		return new ITerminalConnector[] { connector };
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
		IPath path = ExecutableUtil.find("capify", true, null); //$NON-NLS-1$
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
