/*******************************************************************************
 * Copyright (c) 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package com.aptana.ide.security.internal.linux;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;

import com.aptana.ide.security.linux.Activator;

public class StorageLoginDialog extends TitleAreaDialog {

	private static final String DIALOG_SETTINGS_SECTION_NEW = "StorageLoginDialogNew"; //$NON-NLS-1$
	private static final String DIALOG_SETTINGS_SECTION_OLD = "StorageLoginDialogOld"; //$NON-NLS-1$

	private static final String HELP_ID = Activator.PLUGIN_ID + ".StorageLoginDialog"; //$NON-NLS-1$

	private static final ImageDescriptor dlgImageDescriptor = ImageDescriptor.createFromFile(StorageLoginDialog.class, "/icons/storage/login_wiz.png"); //$NON-NLS-1$

	protected Text password;
	protected Text confirm;

	protected Button showPassword;
	protected Button okButton;

	protected String generatedPassword;

	final protected boolean confirmPassword;
	final protected boolean passwordChange;

	private Image dlgTitleImage = null;

	public StorageLoginDialog(Shell parentShell, boolean confirmPassword, boolean passwordChange) {
		super(parentShell);
		this.confirmPassword = confirmPassword;
		this.passwordChange = passwordChange;
	}

	public String getPassword() {
		return generatedPassword;
	}

	protected void createButtonsForButtonBar(Composite parent) {
		okButton = createButton(parent, IDialogConstants.OK_ID, Messages.buttonLogin, true);
		okButton.setEnabled(false);
		createButton(parent, IDialogConstants.CANCEL_ID, Messages.buttonExit, false);
	}

	protected IDialogSettings getDialogBoundsSettings() {
		IDialogSettings settings = Activator.getDefault().getDialogSettings();
		String settingsID = (confirmPassword) ? DIALOG_SETTINGS_SECTION_NEW : DIALOG_SETTINGS_SECTION_OLD;
		IDialogSettings section = settings.getSection(settingsID);
		if (section == null)
			section = settings.addNewSection(settingsID);
		return section;
	}

	protected boolean isResizable() {
		return true;
	}

	protected void configureShell(Shell shell) {
		super.configureShell(shell);
		shell.setText(Messages.generalDialogTitle);
		PlatformUI.getWorkbench().getHelpSystem().setHelp(shell, HELP_ID);
	}

	protected Control createContents(Composite parent) {
		Control contents = super.createContents(parent);
		dlgTitleImage = dlgImageDescriptor.createImage();
		setTitleImage(dlgTitleImage);
		return contents;
	}

	protected Control createDialogArea(Composite parent) {
		Composite compositeTop = (Composite) super.createDialogArea(parent);

		String titleMsg;
		if (confirmPassword)
			titleMsg = Messages.passwordChangeTitle;
		else if (passwordChange)
			titleMsg = Messages.messageLoginChange;
		else
			titleMsg = Messages.dialogTitle;
		setTitle(titleMsg);

		Composite composite = new Composite(compositeTop, SWT.NONE);

		new Label(composite, SWT.LEFT).setText(Messages.labelPassword);
		password = new Text(composite, SWT.LEFT | SWT.BORDER);
		password.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent event) {
				okButton.setEnabled(validatePassword());
			}
		});

		if (confirmPassword) {
			new Label(composite, SWT.LEFT).setText(Messages.labelConfirm);
			confirm = new Text(composite, SWT.LEFT | SWT.BORDER);
			confirm.addModifyListener(new ModifyListener() {
				public void modifyText(ModifyEvent event) {
					okButton.setEnabled(validatePassword());
				}
			});
		} else
			confirm = null;

		new Label(composite, SWT.LEFT); // filler
		showPassword = new Button(composite, SWT.CHECK | SWT.RIGHT);
		showPassword.setText(Messages.showPassword);
		showPassword.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				passwordVisibility();
			}

			public void widgetDefaultSelected(SelectionEvent e) {
				passwordVisibility();
			}
		});
		showPassword.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));

		// by default don't display password as clear text
		showPassword.setSelection(false);
		passwordVisibility();
		
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		GridLayoutFactory.swtDefaults().numColumns(2).generateLayout(composite);

		return compositeTop;
	}

	protected void passwordVisibility() {
		boolean selected = showPassword.getSelection();
		if (selected) {
			password.setEchoChar('\0');
			if (confirm != null)
				confirm.setEchoChar('\0');
		} else {
			password.setEchoChar('*');
			if (confirm != null)
				confirm.setEchoChar('*');
		}
	}

	protected boolean validatePassword() {
		String password1 = password.getText();
		if ((password1 == null) || (password1.length() == 0)) {
			setMessage(Messages.messageEmptyPassword, IMessageProvider.ERROR);
			return false;
		}
		if (confirm != null) {
			String password2 = confirm.getText();
			if (!password1.equals(password2)) {
				setMessage(Messages.messageNoMatch, IMessageProvider.WARNING);
				return false;
			}
		}
		setMessage("", IMessageProvider.NONE); //$NON-NLS-1$
		return true;
	}

	protected void okPressed() {
		generatedPassword = password.getText();

		super.okPressed();
	}
}
