/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */

package com.aptana.ide.ui.io.auth;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.aptana.core.util.StringUtil;
import com.aptana.ide.ui.io.IOUIPlugin;

/**
 * @author Max Stepanov
 *
 */
public class PasswordPromptDialog extends TitleAreaDialog {

	private String title;
	private String message;
	private String login;
	private char[] password;
	private boolean savePassword;
	
	private Image titleImage;
	
	private Text loginText;
	private Text passwordText;
	private Button savePasswordButton;

	/**
	 * @param parentShell
	 */
	public PasswordPromptDialog(Shell parentShell, String title, String message) {
		super(parentShell);
		this.title = title;
		this.message = message;
	}

	/**
	 * @param login the login to set
	 */
	public void setLogin(String login) {
		this.login = login;
	}

	/**
	 * @param password
	 */
	public void setPassword(char[] password) {
		this.password = password;
	}

	/**
	 * @return password
	 */
	public char[] getPassword() {
		return password;
	}

	/**
	 * @param save password
	 */
	public void setSavePassword(boolean savePassword) {
		this.savePassword = savePassword;
	}

	/**
	 * @return save password
	 */
	public boolean getSavePassword() {
		return savePassword;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.TitleAreaDialog#createDialogArea(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite dialogArea = (Composite) super.createDialogArea(parent);
		
		titleImage = IOUIPlugin.getImageDescriptor("/icons/full/wizban/security.png").createImage(); //$NON-NLS-1$
		dialogArea.addDisposeListener(new DisposeListener() {
			public void widgetDisposed(DisposeEvent e) {
				if (titleImage != null) {
					setTitleImage(null);
					titleImage.dispose();
					titleImage = null;
				}
			}
		});
		
		setTitleImage(titleImage);
		setTitle(title);
		setMessage(message);

		Composite container = new Composite(dialogArea, SWT.NONE);
		container.setLayoutData(GridDataFactory.fillDefaults().grab(true, true).create());
		container.setLayout(GridLayoutFactory.swtDefaults()
				.margins(convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_MARGIN), convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_MARGIN))
				.spacing(convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_SPACING), convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_SPACING))
				.numColumns(2).create());

		/* row 1 */
		Label label = new Label(container, SWT.NONE);
		label.setLayoutData(GridDataFactory.swtDefaults().create());
		label.setText(StringUtil.makeFormLabel(Messages.PasswordPromptDialog_UserName));
		
		loginText = new Text(container, SWT.SINGLE | SWT.BORDER);
		loginText.setLayoutData(GridDataFactory.fillDefaults().grab(true, false).create());
		if (login != null) {
			loginText.setText(login);
		}
		loginText.setEnabled(false);

		/* row 2 */
		label = new Label(container, SWT.NONE);
		label.setLayoutData(GridDataFactory.swtDefaults().create());
		label.setText(StringUtil.makeFormLabel(Messages.PasswordPromptDialog_Password));

		passwordText = new Text(container, SWT.SINGLE | SWT.PASSWORD | SWT.BORDER);
		passwordText.setLayoutData(GridDataFactory.fillDefaults().grab(true, false).create());
		if (password != null) {
			passwordText.setText(String.copyValueOf(password));
		}

		/* row 3 */
		new Label(container, SWT.NONE)
			.setLayoutData(GridDataFactory.swtDefaults().create());

		savePasswordButton = new Button(container, SWT.CHECK);
		savePasswordButton.setLayoutData(GridDataFactory.fillDefaults().create());
		savePasswordButton.setText(Messages.PasswordPromptDialog_SavePassword);
		savePasswordButton.setSelection(savePassword);

		return dialogArea;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog#createButtonsForButtonBar(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		super.createButtonsForButtonBar(parent);
		getButton(OK).setText(Messages.PasswordPromptDialog_Login);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog#okPressed()
	 */
	@Override
	protected void okPressed() {
		password = passwordText.getText().toCharArray();
		savePassword = savePasswordButton.getSelection();
		super.okPressed();
	}
}
