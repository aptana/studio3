/**
 * Aptana Studio
 * Copyright (c) 2013 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.git.ui.dialogs;

import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.aptana.core.util.StringUtil;

/**
 * @author cwilliams
 */
public class CreatePullRequestDialog extends InputDialog
{

	private Text bodyText;
	private String body;

	public CreatePullRequestDialog(final Shell parentShell)
	{
		super(parentShell, Messages.CreatePullRequestDialog_Title, Messages.CreatePullRequestDialog_TitleFieldLabel,
				StringUtil.EMPTY, new IInputValidator()
				{
					public String isValid(String title)
					{
						if (StringUtil.isEmpty(title))
						{
							return Messages.CreatePullRequestDialog_EmptyTitleErrMsg;
						}
						return null;
					}
				});
	}

	@Override
	protected Control createDialogArea(Composite parent)
	{
		Composite composite = (Composite) super.createDialogArea(parent);

		Label label = new Label(composite, SWT.NONE);
		label.setText(Messages.CreatePullRequestDialog_BodyFieldLabel);

		bodyText = new Text(composite, getInputTextStyle());
		bodyText.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL));
		bodyText.addModifyListener(new ModifyListener()
		{
			public void modifyText(ModifyEvent e)
			{
				body = bodyText.getText();
			}
		});
		return composite;
	}

	public String getBody()
	{
		return body;
	}

	public String getTitle()
	{
		return getValue();
	}
}
