package com.aptana.deploy.wizard;

import java.net.URL;
import java.text.MessageFormat;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.Path;
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
import org.eclipse.swt.widgets.Link;
import org.eclipse.tm.internal.terminal.control.ITerminalListener;
import org.eclipse.tm.internal.terminal.provisional.api.ITerminalConnector;
import org.eclipse.tm.internal.terminal.provisional.api.TerminalConnectorExtension;
import org.eclipse.tm.internal.terminal.provisional.api.TerminalState;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.browser.IWebBrowser;
import org.eclipse.ui.browser.IWorkbenchBrowserSupport;

import com.aptana.deploy.Activator;
import com.aptana.terminal.connector.LocalTerminalConnector;
import com.aptana.terminal.internal.emulator.VT100TerminalControl;

@SuppressWarnings("restriction")
public class CapifyProjectPage extends WizardPage
{

	static final String NAME = "CapifyProject"; //$NON-NLS-1$
	private VT100TerminalControl fCtlTerminal;

	protected CapifyProjectPage()
	{
		super(NAME, Messages.CapifyProjectPage_Title, null);
	}

	@Override
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
				if (!fCtlTerminal.isEmpty())
				{
					fCtlTerminal.clearTerminal();
				}
				// Force a cd to project root
				fCtlTerminal.pasteString(MessageFormat.format("cd '{0}'\n", getProject().getLocation().toOSString())); //$NON-NLS-1$

				// Send "capify" command to terminal
				fCtlTerminal.pasteString("capify .\n"); //$NON-NLS-1$
			}
		});

		// Terminal
		Composite terminal = new Composite(composite, SWT.NONE);
		terminal.setLayout(new FillLayout());
		terminal.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_FILL | GridData.HORIZONTAL_ALIGN_FILL));

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
					Activator.logError(e1);
				}

			}
		});

		Dialog.applyDialogFont(composite);
	}

	@Override
	public void dispose()
	{
		try
		{
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
		return ((DeployWizard) getWizard()).getProject();
	}
}
