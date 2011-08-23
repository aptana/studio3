/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
// $codepro.audit.disable exceptionUsage.exceptionCreation

package com.aptana.browser;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * @author Michael Xia
 */
/* package */class CustomSizeDialog extends TitleAreaDialog
{

	private Text fWidthText;
	private Text fHeightText;

	int fWidth;
	int fHeight;

	protected CustomSizeDialog(Shell parentShell)
	{
		super(parentShell);
		setHelpAvailable(false);
	}

	@Override
	protected void configureShell(Shell newShell)
	{
		super.configureShell(newShell);
		newShell.setText(Messages.WebBrowserViewer_CustomSize_Title);
	}

	@Override
	protected Control createContents(Composite parent)
	{
		Control control = super.createContents(parent);
		validate();
		return control;
	}

	@Override
	protected Control createDialogArea(Composite parent)
	{
		Composite main = new Composite(parent, SWT.NONE);
		main.setLayout(GridLayoutFactory.swtDefaults().numColumns(2).create());
		main.setLayoutData(GridDataFactory.fillDefaults().grab(true, true).create());

		(new Label(main, SWT.NONE)).setText(Messages.WebBrowserViewer_LBL_Width);
		fWidthText = new Text(main, SWT.BORDER | SWT.SINGLE);
		fWidthText.setLayoutData(GridDataFactory.fillDefaults().grab(true, false).create());
		ModifyListener listener = new ModifyListener()
		{

			public void modifyText(ModifyEvent e)
			{
				validate();
			}
		};
		fWidthText.addModifyListener(listener);

		(new Label(main, SWT.NONE)).setText(Messages.WebBrowserViewer_LBL_Height);
		fHeightText = new Text(main, SWT.BORDER | SWT.SINGLE);
		fHeightText.setLayoutData(GridDataFactory.fillDefaults().grab(true, false).create());
		fHeightText.addModifyListener(listener);

		setTitle(Messages.WebBrowserViewer_CustomSize_Title);
		setMessage(Messages.WebBrowserViewer_CustomSize_Message);

		return main;
	}

	@Override
	protected void okPressed()
	{
		fWidth = Integer.parseInt(fWidthText.getText());
		fHeight = Integer.parseInt(fHeightText.getText());
		super.okPressed();
	}

	private void validate()
	{
		String message = null;
		// checks the width first
		String text = fWidthText.getText();
		if (text.trim().length() == 0)
		{
			message = Messages.WebBrowserViewer_ERR_InvalidWidth;
		}
		else
		{
			try
			{
				int width = Integer.parseInt(text);
				if (width <= 0)
				{
					throw new NumberFormatException();
				}
			}
			catch (NumberFormatException e)
			{
				message = Messages.WebBrowserViewer_ERR_InvalidWidth;
			}
		}
		// then checks the height if width is valid
		if (message == null)
		{
			text = fHeightText.getText();
			if (text.trim().length() == 0)
			{
				message = Messages.WebBrowserViewer_ERR_InvalidHeight;
			}
			else
			{
				try
				{
					int height = Integer.parseInt(text);
					if (height <= 0)
					{
						throw new NumberFormatException();
					}
				}
				catch (NumberFormatException e)
				{
					message = Messages.WebBrowserViewer_ERR_InvalidHeight;
				}
			}
		}
		setErrorMessage(message);
		getButton(IDialogConstants.OK_ID).setEnabled(message == null);
	}
}
