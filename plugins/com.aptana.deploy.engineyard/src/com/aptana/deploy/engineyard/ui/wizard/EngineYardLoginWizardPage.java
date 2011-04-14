/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.deploy.engineyard.ui.wizard;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
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

import com.aptana.deploy.ILoginValidator;
import com.aptana.deploy.engineyard.EngineYardAPI;
import com.aptana.deploy.engineyard.EngineYardPlugin;

public class EngineYardLoginWizardPage extends WizardPage implements ILoginValidator
{
	private static final String NAME = "EngineYardLogin"; //$NON-NLS-1$
	private static final String ENGINE_YARD_ICON = "icons/ey_small_wizard.png"; //$NON-NLS-1$

	private Text userId;
	private Text password;

	private IWizardPage fNextPage;

	protected EngineYardLoginWizardPage()
	{
		super(NAME, Messages.EngineYardLoginWizardPage_Title, EngineYardPlugin.getImageDescriptor(ENGINE_YARD_ICON));
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
		label.setText(Messages.EngineYardLoginWizardPage_EnterCredentialsLabel);

		Composite credentials = new Composite(composite, SWT.NONE);
		credentials.setLayout(new GridLayout(2, false));

		Label userIdLabel = new Label(credentials, SWT.NONE);
		userIdLabel.setText(Messages.EngineYardLoginWizardPage_EmailAddressLabel);
		userId = new Text(credentials, SWT.SINGLE | SWT.BORDER);
		userId.setMessage(Messages.EngineYardLoginWizardPage_EmailAddressExample);
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
		passwordLabel.setText(Messages.EngineYardLoginWizardPage_PasswordLabel);
		password = new Text(credentials, SWT.SINGLE | SWT.BORDER | SWT.PASSWORD);
		password.setMessage(Messages.EngineYardLoginWizardPage_PasswordExample);
		password.setLayoutData(gd);
		password.addModifyListener(new ModifyListener()
		{

			public void modifyText(ModifyEvent e)
			{
				getContainer().updateButtons();
			}
		});

		Button checkAuth = new Button(credentials, SWT.PUSH);
		checkAuth.setText(Messages.EngineYardLoginWizardPage_SubmitButtonLabel);
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
		link.setText(Messages.EngineYardLoginWizardPage_SignupLinkLabel);
		link.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				// Open the next dialog page where user can begin signup process!
				IWizardPage signupPage = new EngineYardSignupPage(userId.getText());
				signupPage.setWizard(getWizard());
				getContainer().showPage(signupPage);
			}
		});

		Dialog.applyDialogFont(composite);
	}

	@Override
	public IWizardPage getNextPage()
	{

		if (fNextPage == null)
		{
			fNextPage = new EngineYardDeployWizardPage();
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
			setErrorMessage(Messages.EngineYardLoginWizardPage_EmptyEmailAddressLabel);
			return false;
		}

		String password = this.password.getText();
		if (password == null || password.trim().length() < 1)
		{
			setErrorMessage(Messages.EngineYardLoginWizardPage_EmptyPasswordLabel);
			return false;
		}

		setErrorMessage(null);
		return true;
	}

	public boolean validateLogin()
	{
		// Try to verify credentials with Engine Yard and write them to a file
		EngineYardAPI api = new EngineYardAPI();
		if (!api.writeCredentials(userId.getText(), password.getText()))
		{
			MessageDialog.openError(getShell(), Messages.EngineYardLoginWizardPage_ErrorTitle,
					Messages.EngineYardLoginWizardPage_InvalidCredentails_Message);
			return false;
		}

		return true;
	}

}
