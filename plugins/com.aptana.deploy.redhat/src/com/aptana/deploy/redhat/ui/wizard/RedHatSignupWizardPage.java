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
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IPageChangeProvider;
import org.eclipse.jface.dialogs.IPageChangedListener;
import org.eclipse.jface.dialogs.IPageChangingListener;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.PageChangedEvent;
import org.eclipse.jface.dialogs.PageChangingEvent;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.wizard.IWizardContainer;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.browser.IWebBrowser;
import org.eclipse.ui.browser.IWorkbenchBrowserSupport;
import org.eclipse.ui.statushandlers.IStatusAdapterConstants;
import org.eclipse.ui.statushandlers.StatusAdapter;
import org.eclipse.ui.statushandlers.StatusManager;

import com.aptana.deploy.redhat.RedHatAPI;
import com.aptana.ui.util.UIUtils;

public class RedHatSignupWizardPage extends WizardPage
{
	private static final String NAME = "RedHatSignup"; //$NON-NLS-1$

	private Text userId;
	private Text password;
	private Text confirmPassword;
	private Text namespace;

	private IWizardPage fNextPage;

	protected RedHatSignupWizardPage()
	{
		super(NAME, Messages.RedHatSignupWizardPage_Title, null);
	}

	public void createControl(Composite parent)
	{
		Composite composite = new Composite(parent, SWT.NULL);

		composite.setLayout(new GridLayout());
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));

		setControl(composite);

		initializeDialogUnits(parent);

		// Actual contents
		Label label = new Label(composite, SWT.NONE);
		label.setText(Messages.RedHatSignupWizardPage_EnterCredentialsLabel);

		Composite credentials = new Composite(composite, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		credentials.setLayout(layout);
		credentials.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		ModifyListener buttonUpdatingListener = new ModifyListener()
		{

			public void modifyText(ModifyEvent e)
			{
				getContainer().updateButtons();
			}
		};

		// domain/namespace
		Label namespaceLabel = new Label(credentials, SWT.NONE);
		namespaceLabel.setText(Messages.RedHatSignupWizardPage_NamespaceLabel);
		namespace = new Text(credentials, SWT.SINGLE | SWT.BORDER);
		namespace.setMessage(Messages.RedHatSignupWizardPage_NamespaceExample);
		GridData gd = GridDataFactory.fillDefaults().grab(true, false).create();
		namespace.setLayoutData(gd);
		namespace.addModifyListener(buttonUpdatingListener);

		// login/username
		Label userIdLabel = new Label(credentials, SWT.NONE);
		userIdLabel.setText(Messages.RedHatSignupWizardPage_UserIDLabel);
		userId = new Text(credentials, SWT.SINGLE | SWT.BORDER);
		userId.setMessage(Messages.RedHatSignupWizardPage_UserIDExample);
		userId.setLayoutData(gd);
		userId.addModifyListener(buttonUpdatingListener);

		// Password
		Label passwordLabel = new Label(credentials, SWT.NONE);
		passwordLabel.setText(Messages.RedHatSignupWizardPage_PasswordLabel);
		password = new Text(credentials, SWT.SINGLE | SWT.BORDER | SWT.PASSWORD);
		password.setLayoutData(gd);
		password.addModifyListener(buttonUpdatingListener);

		// Confirm password
		Label confirmPasswordLabel = new Label(credentials, SWT.NONE);
		confirmPasswordLabel.setText(Messages.RedHatSignupWizardPage_ConfirmPasswordLabel);
		confirmPassword = new Text(credentials, SWT.SINGLE | SWT.BORDER | SWT.PASSWORD);
		confirmPassword.setLayoutData(gd);
		confirmPassword.addModifyListener(buttonUpdatingListener);

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

		// Run the creation before moving on...
		((WizardDialog) getContainer()).addPageChangingListener(new IPageChangingListener()
		{

			public void handlePageChanging(PageChangingEvent event)
			{
				if (event.getTargetPage().equals(getNextPage()))
				{
					RedHatAPI api = new RedHatAPI(userId.getText(), password.getText());
					IStatus status = api.createDomain(namespace.getText());
					if (!status.isOK())
					{
						event.doit = false;
						StatusAdapter statusadap = new StatusAdapter(status);
						statusadap.setProperty(IStatusAdapterConstants.TITLE_PROPERTY, "Creation Error"); //$NON-NLS-1$
						StatusManager.getManager().handle(status, StatusManager.LOG | StatusManager.BLOCK);
					}
					else
					{
						api.writeCredentials();
					}
				}
			}
		});

		IStatus apiCheck = new RedHatAPI().verifyGemInstalled();
		if (!apiCheck.isOK())
		{
			MessageDialog dialog = new MessageDialog(getShell(), Messages.RedHatSignupWizardPage_GemNotInstalledTitle,
					null, Messages.RedHatSignupWizardPage_GemNotInstalledMessage, MessageDialog.ERROR, new String[] {
							IDialogConstants.OK_LABEL, IDialogConstants.CANCEL_LABEL }, 0);
			if (dialog.open() == 0)
			{
				// Open docs at http://wiki.appcelerator.org/display/tis/Red+Hat+Deployment
				UIUtils.getDisplay().asyncExec(new Runnable()
				{

					public void run()
					{
						IWorkbenchBrowserSupport support = PlatformUI.getWorkbench().getBrowserSupport();
						try
						{
							IWebBrowser browser = support.createBrowser(null);
							browser.openURL(new URL("http://wiki.appcelerator.org/display/tis/Red+Hat+Deployment")); //$NON-NLS-1$
						}
						catch (Exception e)
						{
							// ignores the exception
						}
					}
				});
			}
			// exit the wizard
			UIUtils.getDisplay().asyncExec(new Runnable()
			{

				public void run()
				{
					((WizardDialog) getContainer()).close();
				}
			});
		}
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
		String desiredNamespace = this.namespace.getText();
		// Verify alphanumeric, max 16 chars!
		if (desiredNamespace == null || desiredNamespace.trim().length() < 1
				|| !desiredNamespace.matches("[a-zA-Z0-9]{1,16}")) //$NON-NLS-1$
		{
			setErrorMessage(Messages.RedHatSignupWizardPage_InvalidNamespaceError);
			return false;
		}

		String userId = this.userId.getText();
		if (userId == null || userId.trim().length() < 6)
		{
			setErrorMessage(Messages.RedHatSignupWizardPage_EmptyUserIDError);
			return false;
		}

		String password = this.password.getText();
		if (password == null || password.trim().length() < 1)
		{
			setErrorMessage(Messages.RedHatSignupWizardPage_EmptyPasswordError);
			return false;
		}

		String confirmPassword = this.confirmPassword.getText();
		if (confirmPassword == null || confirmPassword.trim().length() < 1)
		{
			setErrorMessage(Messages.RedHatSignupWizardPage_EmptyConfirmPasswordError);
			return false;
		}
		if (!confirmPassword.equals(password))
		{
			setErrorMessage(Messages.RedHatSignupWizardPage_PasswordsDontMatchError);
			return false;
		}

		setErrorMessage(null);
		return true;
	}
}
