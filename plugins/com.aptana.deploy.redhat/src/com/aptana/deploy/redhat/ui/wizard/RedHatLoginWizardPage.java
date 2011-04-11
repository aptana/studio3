/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.deploy.redhat.ui.wizard;

import java.net.URL;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IPageChangeProvider;
import org.eclipse.jface.dialogs.IPageChangedListener;
import org.eclipse.jface.dialogs.PageChangedEvent;
import org.eclipse.jface.wizard.IWizardContainer;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.browser.IWebBrowser;
import org.eclipse.ui.browser.IWorkbenchBrowserSupport;

import com.aptana.deploy.ILoginValidator;
import com.aptana.deploy.redhat.RedHatAPI;

public class RedHatLoginWizardPage extends WizardPage implements ILoginValidator
{
	private static final String NAME = "RedHatLogin"; //$NON-NLS-1$

	private Text userId;
	private Text password;

	private IWizardPage fNextPage;

	protected RedHatLoginWizardPage()
	{
		super(NAME, Messages.RedHatLoginWizardPage_Title, null);
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
		label.setText(Messages.RedHatLoginWizardPage_EnterCredentialsLabel);

		Composite credentials = new Composite(composite, SWT.NONE);
		credentials.setLayout(new GridLayout(2, false));

		Label userIdLabel = new Label(credentials, SWT.NONE);
		userIdLabel.setText(Messages.RedHatLoginWizardPage_UserIDLabel);
		userId = new Text(credentials, SWT.SINGLE | SWT.BORDER);
		userId.setMessage(Messages.RedHatLoginWizardPage_UserIDExample);
		GridData gd = new GridData(300, SWT.DEFAULT);
		userId.setLayoutData(gd);
		userId.addModifyListener(new ModifyListener()
		{

			public void modifyText(ModifyEvent e)
			{
				getContainer().updateButtons();
			}
		});

		Label passwordLabel = new Label(credentials, SWT.NONE);
		passwordLabel.setText(Messages.RedHatLoginWizardPage_PasswordLabel);
		password = new Text(credentials, SWT.SINGLE | SWT.BORDER | SWT.PASSWORD);
		password.setMessage(Messages.RedHatLoginWizardPage_PasswordExample);
		password.setLayoutData(gd);
		password.addModifyListener(new ModifyListener()
		{

			public void modifyText(ModifyEvent e)
			{
				getContainer().updateButtons();
			}
		});

		Button checkAuth = new Button(credentials, SWT.PUSH);
		checkAuth.setText(Messages.RedHatLoginWizardPage_SubmitButtonLabel);
		checkAuth.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				if (validateLogin() && isPageComplete())
				{
					getContainer().showPage(getNextPage());
				}
			}
		});

		// Signup link
		Link link = new Link(composite, SWT.NONE);
		link.setText(Messages.RedHatLoginWizardPage_SignupLink);
		link.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				// FIXME Do they sign up on the web form, or do they need to create a domain?
				// Open the browser to the register URL
				IWorkbenchBrowserSupport support = PlatformUI.getWorkbench().getBrowserSupport();
				try
				{
					IWebBrowser browser = support.getExternalBrowser();
					browser.openURL(new URL("https://www.redhat.com/wapps/ugc/register.html")); //$NON-NLS-1$
				}
				catch (Exception t)
				{
					// ignores the exception
				}
			}
		});

		Dialog.applyDialogFont(composite);

		// Save credentials if user hits Next too!!!
		IWizardContainer container = getWizard().getContainer();
		((IPageChangeProvider) container).addPageChangedListener(new IPageChangedListener()
		{

			public void pageChanged(PageChangedEvent event)
			{
				Object selected = event.getSelectedPage();
				if (selected instanceof RedHatDeployWizardPage)
				{
					// If user has moved on to deploy page, write their credentials to a file
					RedHatAPI api = new RedHatAPI(userId.getText(), password.getText());
					api.writeCredentials();
				}
			}
		});
	}

	@Override
	public IWizardPage getNextPage()
	{
		if (fNextPage == null)
		{
			fNextPage = new RedHatDeployWizardPage();
			fNextPage.setWizard(getWizard());
		}
		return fNextPage;
	}

	@Override
	public boolean isPageComplete()
	{
		String userId = this.userId.getText();
		if (userId == null || userId.trim().length() < 1)
		{
			setErrorMessage(Messages.RedHatLoginWizardPage_EmptyUserIDError);
			return false;
		}

		String password = this.password.getText();
		if (password == null || password.trim().length() < 1)
		{
			setErrorMessage(Messages.RedHatLoginWizardPage_EmptyPasswordError);
			return false;
		}

		setErrorMessage(null);
		return true;
	}

	public boolean validateLogin()
	{
		RedHatAPI api = new RedHatAPI(userId.getText(), password.getText());
		IStatus status = api.authenticate();
		if (!status.isOK())
		{
			// TODO We have a list of error codes here:
			// http://wiki.appcelerator.org/display/tools/Simplified+Cloud+Deployment
			if (status.getCode() == 97)
			{
				// Credentials are wrong
				setErrorMessage(Messages.RedHatLoginWizardPage_InvalidCredentialsError);
			}
			else if (status.getCode() == 99)
			{
				// no user exists by that login, maybe because domain wasn't created?
				setErrorMessage(Messages.RedHatLoginWizardPage_InvalidCredentialsError);// TODO Create a different error
																						// message for this?
			}
			else
			{
				setErrorMessage(status.getMessage());
			}
			return false;
		}

		return true;
	}

}
