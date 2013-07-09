/**
 * Aptana Studio
 * Copyright (c) 2013 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.git.ui.dialogs;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.StatusDialog;
import org.eclipse.jface.layout.GridDataFactory;
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
import com.aptana.git.ui.GitUIPlugin;

/**
 * @author cwilliams
 */
public class CreatePullRequestDialog extends StatusDialog
{

	private Text bodyText;
	private String body;

	private Text titleText;
	private String title;

	public CreatePullRequestDialog(final Shell parentShell, String defaultTitle, String defaultBody)
	{
		super(parentShell);
		setTitle(Messages.CreatePullRequestDialog_Title);
		this.title = defaultTitle;
		this.body = defaultBody;
	}

	@Override
	protected Control createDialogArea(Composite parent)
	{
		Composite composite = (Composite) super.createDialogArea(parent);

		// Title
		Label titleLabel = new Label(composite, SWT.WRAP);
		titleLabel.setText(Messages.CreatePullRequestDialog_TitleFieldLabel);
		GridData data = new GridData(GridData.GRAB_HORIZONTAL | GridData.GRAB_VERTICAL | GridData.HORIZONTAL_ALIGN_FILL
				| GridData.VERTICAL_ALIGN_CENTER);
		data.widthHint = convertHorizontalDLUsToPixels(IDialogConstants.MINIMUM_MESSAGE_AREA_WIDTH);
		titleLabel.setLayoutData(data);
		titleLabel.setFont(parent.getFont());

		titleText = new Text(composite, SWT.SINGLE | SWT.BORDER);
		titleText.setText(title);
		titleText.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL));
		titleText.addModifyListener(new ModifyListener()
		{
			public void modifyText(ModifyEvent e)
			{
				title = titleText.getText();
				validate();
			}
		});

		// Body
		Label label = new Label(composite, SWT.NONE);
		label.setText(Messages.CreatePullRequestDialog_BodyFieldLabel);

		bodyText = new Text(composite, SWT.MULTI | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		bodyText.setText(body);
		GridDataFactory.fillDefaults().hint(SWT.DEFAULT, 300).applyTo(bodyText);
		bodyText.addModifyListener(new ModifyListener()
		{
			public void modifyText(ModifyEvent e)
			{
				body = bodyText.getText();
				validate();
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
		return title;
	}

	protected void validate()
	{
		if (StringUtil.isEmpty(title))
		{
			updateStatus(new Status(IStatus.ERROR, GitUIPlugin.getPluginId(),
					Messages.CreatePullRequestDialog_EmptyTitleErrMsg));
			return;
		}

		updateStatus(Status.OK_STATUS);
	}
}
