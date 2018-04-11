/**
 * Aptana Studio
 * Copyright (c) 2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.ui.dialogs;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.window.IShellProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.aptana.core.util.PlatformUtil;
import com.aptana.core.util.ProcessRunner;
import com.aptana.core.util.StringUtil;
import com.aptana.ui.IDialogConstants;
import com.aptana.ui.UIPlugin;

/**
 * This password prompt dialog tries to mimic the native mac password prompt dialog.
 * 
 * @author pinnamuri
 */
public class SudoPasswordPromptDialog extends Dialog
{

	private Text pwdText;
	private Shell shell;
	private char[] password;

	private static final String MAC_DIALOG_FONT = "Lucida Grande"; //$NON-NLS-1$
	private static final int MAC_DIALOG_FONT_SIZE = 13;
	private String promptMessage;
	private static final String SECURITY_IMAGE = "/icons/full/security.png"; //$NON-NLS-1$

	/**
	 * Command/options to run osascript.
	 */
	private static final String ARG_STATEMENT = "-e"; //$NON-NLS-1$
	private static final String OSASCRIPT = "osascript"; //$NON-NLS-1$

	public SudoPasswordPromptDialog(IShellProvider parentShell, String promptMessage)
	{
		super(parentShell);
		this.promptMessage = promptMessage + " " + Messages.SudoPasswordPromptDialog_MessagePrompt_Suffix; //$NON-NLS-1$
	}

	@Override
	protected void configureShell(Shell newShell)
	{
		this.shell = newShell;
		newShell.setLayout(new GridLayout());
	}

	@Override
	protected void okPressed()
	{
		password = pwdText.getText().toCharArray();
		super.okPressed();
	}

	public char[] getPassword()
	{
		return password;
	}

	@Override
	protected Point getInitialSize()
	{
		// Trying to get the size of the dialog similar to mac sudo prompt dialog.
		Point computeSize = shell.computeSize(440, SWT.DEFAULT);
		return computeSize;
	}

	@Override
	protected void initializeBounds()
	{
		super.initializeBounds();
		pwdText.setFocus();
	}

	@Override
	protected Control createDialogArea(Composite parent)
	{
		FontData msgPromptFontData = new FontData(MAC_DIALOG_FONT, MAC_DIALOG_FONT_SIZE, SWT.BOLD);
		final Font msgPromptFont = new Font(shell.getDisplay(), msgPromptFontData);

		// ----------------------------------------------------------
		// Composite for the header and prompt message.
		parent.setLayout(GridLayoutFactory.swtDefaults().numColumns(2).equalWidth(false).create());
		parent.setFont(msgPromptFont);

		Label authImageLbl = new Label(parent, SWT.None);
		final Image authImage = UIPlugin.getImageDescriptor(SECURITY_IMAGE).createImage();
		authImageLbl.setImage(authImage);

		Label promptMsg = new Label(parent, SWT.WRAP);
		promptMsg.setLayoutData(GridDataFactory.swtDefaults().grab(true, true).align(SWT.BEGINNING, SWT.BEGINNING)
				.create());
		promptMsg.setFont(parent.getFont());
		promptMsg.setText(promptMessage);

		// ----------------------------------------------------------
		// Now laying out UserName and Password fields.
		new Label(parent, SWT.NONE); // Dummy label to fill in.

		FontData fieldsFontData = new FontData(MAC_DIALOG_FONT, MAC_DIALOG_FONT_SIZE, SWT.NORMAL);
		final Font fieldsFont = new Font(shell.getDisplay(), fieldsFontData);

		Composite authDetails = new Composite(parent, SWT.None);
		authDetails.setLayout(GridLayoutFactory.swtDefaults().numColumns(2).equalWidth(false).create());
		authDetails.setLayoutData(GridDataFactory.swtDefaults().grab(true, false).align(SWT.FILL, SWT.BEGINNING)
				.create());
		authDetails.setFont(fieldsFont);

		Composite labels = new Composite(authDetails, SWT.None);
		labels.setLayout(GridLayoutFactory.swtDefaults().numColumns(1).equalWidth(true).create());

		Composite fieldsComp = new Composite(authDetails, SWT.None);
		fieldsComp.setLayout(GridLayoutFactory.swtDefaults().numColumns(1).equalWidth(true).create());
		fieldsComp
				.setLayoutData(GridDataFactory.swtDefaults().grab(true, true).align(SWT.FILL, SWT.BEGINNING).create());

		Label nameLbl = new Label(labels, SWT.None);
		nameLbl.setText(StringUtil.makeFormLabel(Messages.SudoPasswordPromptDialog_User));
		nameLbl.setLayoutData(GridDataFactory.swtDefaults().grab(true, false).align(SWT.END, SWT.BEGINNING).create());
		nameLbl.setFont(authDetails.getFont());

		Text nameText = new Text(fieldsComp, SWT.BORDER | SWT.READ_ONLY);
		nameText.setLayoutData(GridDataFactory.swtDefaults().grab(true, true).align(SWT.FILL, SWT.FILL).create());
		nameText.setText(System.getProperty("user.name")); //$NON-NLS-1$

		// Retrieves the full user name in password prompt dialog on Mac.
		if (PlatformUtil.isMac())
		{
			IStatus status = new ProcessRunner().runInBackground(OSASCRIPT, ARG_STATEMENT,
					"long user name of (system info)"); //$NON-NLS-1$
			if (status != null && status.isOK() && !StringUtil.isEmpty(status.getMessage()))
			{
				nameText.setText(status.getMessage());
			}
		}
		nameText.setForeground(parent.getDisplay().getSystemColor(SWT.COLOR_TITLE_INACTIVE_FOREGROUND));

		Label pwdLbl = new Label(labels, SWT.None);
		pwdLbl.setText(StringUtil.makeFormLabel(Messages.SudoPasswordPromptDialog_Password));
		pwdLbl.setLayoutData(GridDataFactory.swtDefaults().grab(true, false).align(SWT.END, SWT.BEGINNING).create());
		pwdLbl.setFont(authDetails.getFont());

		pwdText = new Text(fieldsComp, SWT.BORDER | SWT.PASSWORD);
		pwdText.setLayoutData(GridDataFactory.swtDefaults().grab(true, true).align(SWT.FILL, SWT.FILL).create());

		shell.addDisposeListener(new DisposeListener()
		{
			public void widgetDisposed(DisposeEvent e)
			{
				if (authImage != null)
				{
					authImage.dispose();
				}
				if (msgPromptFont != null)
				{
					msgPromptFont.dispose();
				}
				if (fieldsFont != null)
				{
					fieldsFont.dispose();
				}
			}
		});

		return parent;
	}

	@Override
	protected Control createButtonBar(Composite parent)
	{
		new Label(parent, SWT.None);
		Composite buttonsComp = new Composite(parent, SWT.None);
		// Intentionally make the numOfColmnns to 0 as they will be incremented in createButton methods.
		buttonsComp.setLayout(GridLayoutFactory.fillDefaults().numColumns(0).equalWidth(true).create());
		buttonsComp
				.setLayoutData(GridDataFactory.swtDefaults().grab(true, true).align(SWT.END, SWT.BEGINNING).create());

		createButton(buttonsComp, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);

		Button okBtn = createButton(buttonsComp, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
		okBtn.setLayoutData(GridDataFactory.swtDefaults().grab(true, false).align(SWT.FILL, SWT.BEGINNING).create());
		return parent;
	}
}
