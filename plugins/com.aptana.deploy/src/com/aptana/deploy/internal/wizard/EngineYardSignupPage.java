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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
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
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.aptana.deploy.Activator;

public class EngineYardSignupPage extends WizardPage
{

	public static final String NAME = "EngineYardSignup"; //$NON-NLS-1$
	private static final String ENGINE_YARD_ICON = "icons/ey_small.png"; //$NON-NLS-1$

	private Text userId;
	private String startingUserId;

	protected EngineYardSignupPage(String startingUserId)
	{
		super(NAME, Messages.EngineYardSignupPage_Title, Activator.getImageDescriptor(ENGINE_YARD_ICON));
		this.startingUserId = startingUserId;
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
		label.setText(Messages.EngineYardSignupPage_EnterCredentialsLabel);

		Composite credentials = new Composite(composite, SWT.NONE);
		credentials.setLayout(new GridLayout(2, false));

		Label userIdLabel = new Label(credentials, SWT.NONE);
		userIdLabel.setText(Messages.EngineYardSignupPage_EmailAddressLabel);
		userId = new Text(credentials, SWT.SINGLE | SWT.BORDER);
		userId.setMessage(Messages.EngineYardSignupPage_EmailAddressExample);
		if (startingUserId != null && startingUserId.trim().length() > 0)
		{
			userId.setText(startingUserId);
		}
		GridData gd = new GridData(300, SWT.DEFAULT);
		userId.setLayoutData(gd);

		Label note = new Label(composite, SWT.WRAP);

		Font dialogFont = JFaceResources.getDialogFont();
		FontData[] data = dialogFont.getFontData();
		for (FontData dataElement : data)
			dataElement.setStyle(dataElement.getStyle() | SWT.ITALIC);
		Font italic = new Font(dialogFont.getDevice(), data);
		note.setFont(italic);

		gd = new GridData(400, SWT.DEFAULT);
		note.setLayoutData(gd);
		note.setText(Messages.EngineYardSignupPage_SignupNote);

		// Add signup button
		Button signup = new Button(composite, SWT.PUSH);
		signup.setText(Messages.EngineYardSignupPage_SignupButtonLabel);
		signup.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				//check if email address is valid
				if(!isEmailValid(userId.getText()))
				{
					MessageDialog.openError(getShell(), "Error", Messages.EngineYardSignupPage_InvalidEmail_Message); //$NON-NLS-1$
					return;
				}
				
				// basically just perform finish!
				if (getWizard().performFinish())
				{
					((WizardDialog) getContainer()).close();
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

	public String getUserID()
	{
		return this.userId.getText();
	}

	@Override
	public boolean isPageComplete()
	{

		String userId = this.userId.getText();
		if (userId == null || userId.trim().length() < 1)
		{
			setErrorMessage(Messages.EngineYardSignupPage_EmptyEmailAddressLabel);
			return false;
		}

		setErrorMessage(null);
		return true;
	}
	
	private Boolean isEmailValid(String email)
	{
		
		Pattern p = Pattern.compile("^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$", Pattern.CASE_INSENSITIVE); //$NON-NLS-1$
		
		Matcher m = p.matcher(email);
		
		if(m.matches())
			return true;
		
		return false;
	}
}
