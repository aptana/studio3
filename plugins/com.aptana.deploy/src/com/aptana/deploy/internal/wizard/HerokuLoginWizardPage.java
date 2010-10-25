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

import com.aptana.deploy.Activator;
import com.aptana.deploy.HerokuAPI;
import com.aptana.deploy.ILoginValidator;

public class HerokuLoginWizardPage extends WizardPage implements ILoginValidator
{
	private static final String NAME = "HerokuLogin"; //$NON-NLS-1$
	private static final String HEROKU_ICON = "icons/heroku.png"; //$NON-NLS-1$

	private Text userId;
	private Text password;

	private IWizardPage fNextPage;

	protected HerokuLoginWizardPage()
	{
		super(NAME, Messages.HerokuLoginWizardPage_Title, Activator.getImageDescriptor(HEROKU_ICON));
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
		label.setText(Messages.HerokuLoginWizardPage_EnterCredentialsLabel);

		Composite credentials = new Composite(composite, SWT.NONE);
		credentials.setLayout(new GridLayout(2, false));

		Label userIdLabel = new Label(credentials, SWT.NONE);
		userIdLabel.setText(Messages.HerokuLoginWizardPage_UserIDLabel);
		userId = new Text(credentials, SWT.SINGLE | SWT.BORDER);
		userId.setMessage(Messages.HerokuLoginWizardPage_UserIDExample);
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
		passwordLabel.setText(Messages.HerokuLoginWizardPage_PasswordLabel);
		password = new Text(credentials, SWT.SINGLE | SWT.BORDER | SWT.PASSWORD);
		password.setMessage(Messages.HerokuLoginWizardPage_PasswordExample);
		password.setLayoutData(gd);
		password.addModifyListener(new ModifyListener()
		{

			public void modifyText(ModifyEvent e)
			{
				getContainer().updateButtons();
			}
		});

		Button checkAuth = new Button(credentials, SWT.PUSH);
		checkAuth.setText(Messages.HerokuLoginWizardPage_SubmitButtonLabel);
		checkAuth.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
//				api.writeCredentials(); // we write them automatically via a page changed listener below...
				if (validateLogin() && isPageComplete())
				{
					getContainer().showPage(getNextPage());
				}
			}
		});

		// Signup link
		Link link = new Link(composite, SWT.NONE);
		link.setText(Messages.HerokuLoginWizardPage_SignupLink);
		link.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				// Open the next dialog page where user can begin signup process!
				IWizardPage signupPage = new HerokuSignupPage(userId.getText());
				signupPage.setWizard(getWizard());
				getContainer().showPage(signupPage);
			}
		});

		Dialog.applyDialogFont(composite);

		// Save credentials if user hit Next too!!!
		IWizardContainer container = getWizard().getContainer();
		((IPageChangeProvider) container).addPageChangedListener(new IPageChangedListener()
		{

			public void pageChanged(PageChangedEvent event)
			{
				Object selected = event.getSelectedPage();
				if (selected instanceof HerokuDeployWizardPage)
				{
					// If user has moved on to deploy page, write their credentials to a file
					HerokuAPI api = new HerokuAPI(userId.getText(), password.getText());
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
			fNextPage = new HerokuDeployWizardPage();
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
			setErrorMessage(Messages.HerokuLoginWizardPage_EmptyUserIDError);
			return false;
		}

		String password = this.password.getText();
		if (password == null || password.trim().length() < 1)
		{
			setErrorMessage(Messages.HerokuLoginWizardPage_EmptyPasswordError);
			return false;
		}

		setErrorMessage(null);
		return true;
	}
	
	public boolean validateLogin()
	{
		HerokuAPI api = new HerokuAPI(userId.getText(), password.getText());
		IStatus status = api.authenticate();
		if (!status.isOK())
		{
			setErrorMessage(status.getMessage());
			return false;
		}
		
		return true;
	}

}
