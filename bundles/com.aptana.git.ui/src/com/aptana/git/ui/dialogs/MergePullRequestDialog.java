/**
 * Aptana Studio
 * Copyright (c) 2013 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.git.ui.dialogs;

import java.text.MessageFormat;

import org.eclipse.jface.dialogs.StatusDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.aptana.git.core.github.IGithubPullRequest;

/**
 * @author cwilliams
 */
public class MergePullRequestDialog extends StatusDialog
{

	private Text commitMsgText;
	private String commitMsg;

	private Button deleteBranchButton;
	private boolean deleteBranch;

	final private IGithubPullRequest pr;

	public MergePullRequestDialog(Shell parentShell, IGithubPullRequest pr)
	{
		super(parentShell);
		setTitle(MessageFormat.format("Merge Pull Request #{0}", pr.getNumber()));
		this.pr = pr;
		this.deleteBranch = true;
	}

	@Override
	protected Control createDialogArea(Composite parent)
	{
		Composite composite = (Composite) super.createDialogArea(parent);

		Label commitMsgLabel = new Label(composite, SWT.NONE);
		commitMsgLabel.setText("Commit message (optional) :");

		commitMsgText = new Text(composite, SWT.MULTI | SWT.BORDER | SWT.WRAP | SWT.V_SCROLL);
		commitMsgText.setLayoutData(GridDataFactory.fillDefaults().grab(true, true).hint(300, 100).create());
		commitMsgText.addModifyListener(new ModifyListener()
		{
			public void modifyText(ModifyEvent e)
			{
				commitMsg = commitMsgText.getText();
			}
		});

		// Add a checkbox for deleting branch at head
		deleteBranchButton = new Button(composite, SWT.CHECK);
		deleteBranchButton.setText(MessageFormat.format("Delete {0}:{1} branch if PR succesfully merged and closed.",
				pr.getHeadRepo().getOwner(), pr.getHeadRef()));
		deleteBranchButton.setSelection(deleteBranch);
		deleteBranchButton.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				deleteBranch = deleteBranchButton.getSelection();
			}
		});

		return composite;
	}

	public String getCommitMessage()
	{
		return commitMsg;
	}

	public boolean deleteBranch()
	{
		return deleteBranch;
	}
}
