/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.git.ui.dialogs;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.StatusDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.aptana.git.core.model.GitRepository;
import com.aptana.git.ui.GitUIPlugin;

public class AddRemoteDialog extends StatusDialog
{

	private Text remoteURIText;
	private String remoteURI;
	private Button trackButton;
	private boolean track;
	private boolean dontAutoChangeTrack = false;
	private GitRepository repo;
	private String remoteName;
	private Text originNameText;

	public AddRemoteDialog(final Shell parentShell, final GitRepository repo, String remoteName, String defaultURI)
	{
		super(parentShell);
		this.repo = repo;
		this.remoteName = remoteName;
		this.remoteURI = defaultURI;
	}

	@Override
	protected void configureShell(Shell shell)
	{
		super.configureShell(shell);
		shell.setText(Messages.AddRemoteDialog_AddRemoteDialog_Title);
	}

	protected void validateInput()
	{
		String newText = originNameText.getText();
		if (newText == null || newText.trim().length() == 0)
		{
			updateStatus(new Status(IStatus.ERROR, GitUIPlugin.getPluginId(),
					Messages.AddRemoteDialog_NonEmptyRemoteNameMessage));
			return;
		}
		if (newText.trim().contains(" ") || newText.trim().contains("\t")) //$NON-NLS-1$ //$NON-NLS-2$
		{
			updateStatus(new Status(IStatus.ERROR, GitUIPlugin.getPluginId(),
					Messages.AddRemoteDialog_NoWhitespaceRemoteNameMessage));
		}
		// TODO What else do we need to do to verify the remote name?
		if (repo.remotes().contains(newText.trim()))
		{
			updateStatus(new Status(IStatus.ERROR, GitUIPlugin.getPluginId(),
					Messages.AddRemoteDialog_UniqueRemoteNameMessage));
			return;
		}
		updateStatus(Status.OK_STATUS);

		if (dontAutoChangeTrack)
		{
			return;
		}
		this.track = newText.equals("origin"); //$NON-NLS-1$
		trackButton.setSelection(this.track);
	}

	@Override
	protected Control createDialogArea(Composite parent)
	{
		Composite composite = (Composite) super.createDialogArea(parent);

		Label label = new Label(composite, SWT.WRAP);
		label.setText(Messages.AddRemoteDialog_AddRemoteDialog_Message);
		GridData data = new GridData(GridData.GRAB_HORIZONTAL | GridData.GRAB_VERTICAL | GridData.HORIZONTAL_ALIGN_FILL
				| GridData.VERTICAL_ALIGN_CENTER);
		data.widthHint = convertHorizontalDLUsToPixels(IDialogConstants.MINIMUM_MESSAGE_AREA_WIDTH);
		label.setLayoutData(data);
		label.setFont(parent.getFont());

		originNameText = new Text(composite, SWT.SINGLE | SWT.BORDER);
		originNameText.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL));
		originNameText.addModifyListener(new ModifyListener()
		{
			public void modifyText(ModifyEvent e)
			{
				validateInput();
			}
		});

		label = new Label(composite, SWT.NONE);
		label.setText(Messages.AddRemoteDialog_RemoteURILabel);

		remoteURIText = new Text(composite, SWT.SINGLE | SWT.BORDER);
		remoteURIText.setText(remoteURI);
		remoteURIText.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL));

		// Add an option to track! Default to "on" for remote of "origin"
		trackButton = new Button(composite, SWT.CHECK);
		trackButton.setText(Messages.AddRemoteDialog_TrackButtonLabel);
		trackButton.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				super.widgetSelected(e);
				track = trackButton.getSelection();
				dontAutoChangeTrack = true;
			}
		});

		return composite;
	}

	@Override
	protected void buttonPressed(int buttonId)
	{
		if (buttonId == IDialogConstants.OK_ID)
		{
			remoteName = originNameText.getText();
			remoteURI = remoteURIText.getText();
		}
		else
		{
			remoteName = null;
			remoteURI = null;
		}
		super.buttonPressed(buttonId);
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent)
	{
		super.createButtonsForButtonBar(parent);

		originNameText.setFocus();
		if (remoteName != null)
		{
			originNameText.setText(remoteName);
			originNameText.selectAll();
		}
	}

	@Override
	public void create()
	{
		super.create();
		validateInput();
	}

	public String getRemoteURL()
	{
		return remoteURI;
	}

	public boolean track()
	{
		return track;
	}

	public String getRemoteName()
	{
		return remoteName;
	}
}
