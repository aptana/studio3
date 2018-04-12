/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.ide.ui.io.dialogs;

import org.eclipse.jface.dialogs.Dialog;
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

import com.aptana.core.util.StringUtil;

/**
 * This class is used to prompt the user for a file name or extension to be cloaked.
 */
public class CloakExpressionDialog extends TitleAreaDialog
{

	private Text expressionText;
	private String expression;

	/**
	 * @param parentShell
	 *            the parent shell
	 */
	public CloakExpressionDialog(Shell parentShell)
	{
		this(parentShell, ""); //$NON-NLS-1$
	}

	public CloakExpressionDialog(Shell parentShell, String initialText)
	{
		super(parentShell);
		this.expression = initialText;
		setHelpAvailable(false);
	}

	public String getExpression()
	{
		return expression;
	}

	@Override
	protected void configureShell(Shell shell)
	{
		super.configureShell(shell);
		shell.setText(Messages.CloakExpressionDialog_LBL_ShellTitle);
	}

	@Override
	protected Control createDialogArea(Composite parent)
	{
		Composite main = new Composite(parent, SWT.NONE);
		main.setLayout(GridLayoutFactory.swtDefaults().numColumns(2).create());
		main.setLayoutData(GridDataFactory.fillDefaults().grab(true, true).create());

		Label label = new Label(main, SWT.NONE);
		label.setText(Messages.CloakExpressionDialog_LBL_Expression);
		label.setLayoutData(GridDataFactory.swtDefaults().create());

		expressionText = new Text(main, SWT.SINGLE | SWT.BORDER);
		expressionText.setLayoutData(GridDataFactory.fillDefaults().grab(true, false).create());
		expressionText.setText(expression);
		expressionText.addModifyListener(new ModifyListener()
		{

			public void modifyText(ModifyEvent event)
			{
				getButton(IDialogConstants.OK_ID).setEnabled(!StringUtil.isEmpty(expressionText.getText()));
			}
		});

		Dialog.applyDialogFont(main);

		setTitle(Messages.CloakExpressionDialog_LBL_Title);
		setMessage(Messages.CloakExpressionDialog_LBL_Message);

		return main;
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent)
	{
		super.createButtonsForButtonBar(parent);
		getButton(IDialogConstants.OK_ID).setEnabled(!StringUtil.isEmpty(expression));
	}

	@Override
	protected void okPressed()
	{
		expression = expressionText.getText().trim();
		super.okPressed();
	}
}
