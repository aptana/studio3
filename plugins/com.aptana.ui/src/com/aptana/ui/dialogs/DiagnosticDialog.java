/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.ui.dialogs;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TrayDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.aptana.ui.Messages;

public class DiagnosticDialog extends TrayDialog
{

	private static final int COPY_ID = 32;

	private Text fText;

	/**
	 * Constructor.
	 * 
	 * @param parentShell
	 *            the parent shell
	 */
	public DiagnosticDialog(Shell parentShell)
	{
		super(parentShell);
		setShellStyle(SWT.DIALOG_TRIM | SWT.RESIZE | SWT.APPLICATION_MODAL);
		setHelpAvailable(true);
		setBlockOnOpen(false);
	}

	/**
	 * Appends the specified text to the text area.
	 * 
	 * @param text
	 *            the text to append
	 */
	public void append(String text)
	{
		if (fText != null && !fText.isDisposed() && text != null)
		{
			fText.append(text);
		}
	}

	/**
	 * @see org.eclipse.jface.window.Window#configureShell(org.eclipse.swt.widgets.Shell)
	 */
	protected void configureShell(Shell newShell)
	{
		super.configureShell(newShell);
		newShell.setText(Messages.DiagnosticDialog_run_diagnostic_title);
	}

	/**
	 * @see org.eclipse.jface.dialogs.Dialog#createButtonsForButtonBar(org.eclipse.swt.widgets.Composite)
	 */
	protected void createButtonsForButtonBar(Composite parent)
	{
		// creates the Copy to Clipboard and Close buttons
		createButton(parent, COPY_ID, Messages.DiagnosticDialog_copy_clipboard_label, false);
		createButton(parent, IDialogConstants.OK_ID, Messages.DiagnosticDialog_close_label, true);
	}

	/**
	 * @see org.eclipse.jface.dialogs.Dialog#createDialogArea(org.eclipse.swt.widgets.Composite)
	 */
	protected Control createDialogArea(Composite parent)
	{
		Composite main = new Composite(parent, SWT.NONE);
		main.setLayout(new GridLayout());
		main.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));

		fText = new Text(main, SWT.BORDER | SWT.MULTI | SWT.READ_ONLY | SWT.WRAP | SWT.V_SCROLL);
		fText.setBackground(fText.getDisplay().getSystemColor(SWT.COLOR_WHITE));
		GridData gridData = new GridData(GridData.FILL, GridData.FILL, true, true);
		gridData.widthHint = 500;
		gridData.heightHint = 500;
		fText.setLayoutData(gridData);

		return main;
	}

	/**
	 * @see org.eclipse.jface.dialogs.Dialog#buttonPressed(int)
	 */
	protected void buttonPressed(int buttonId)
	{
		if (buttonId == COPY_ID)
		{
			fText.selectAll();
			fText.copy();
			fText.clearSelection();
		}
		else
		{
			super.buttonPressed(buttonId);
		}
	}

}
