/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
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

public class AddRemoteDialog extends InputDialog
{

	private Text remoteURIText;
	private String remoteURI;
	private Button trackButton;
	private boolean track;
	private boolean dontAutoChangeTrack = false;

	public AddRemoteDialog(final Shell parentShell, final GitRepository repo, String remoteName, String defaultURI)
	{
		super(parentShell, Messages.AddRemoteDialog_AddRemoteDialog_Title,
				Messages.AddRemoteDialog_AddRemoteDialog_Message, remoteName, new IInputValidator()
				{

					public String isValid(String newText)
					{
						if (newText == null || newText.trim().length() == 0)
						{
							return Messages.AddRemoteDialog_NonEmptyRemoteNameMessage;
						}
						if (newText.trim().contains(" ") || newText.trim().contains("\t")) //$NON-NLS-1$ //$NON-NLS-2$
						{
							return Messages.AddRemoteDialog_NoWhitespaceRemoteNameMessage;
						}
						// TODO What else do we need to do to verify the remote name?
						if (repo.remotes().contains(newText.trim()))
						{
							return Messages.AddRemoteDialog_UniqueRemoteNameMessage;
						}
						return null;
					}
				});
		remoteURI = defaultURI;
	}

	@Override
	protected void validateInput()
	{
		super.validateInput();
		if (dontAutoChangeTrack)
		{
			return;
		}
		this.track = getText().getText().equals("origin"); //$NON-NLS-1$
		trackButton.setSelection(this.track);
	}

	@Override
	protected Control createDialogArea(Composite parent)
	{
		Composite composite = (Composite) super.createDialogArea(parent);

		Label label = new Label(composite, SWT.NONE);
		label.setText(Messages.AddRemoteDialog_RemoteURILabel);

		remoteURIText = new Text(composite, getInputTextStyle());
		remoteURIText.setText(remoteURI);
		remoteURIText.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL));
		remoteURIText.addModifyListener(new ModifyListener()
		{
			public void modifyText(ModifyEvent e)
			{
				remoteURI = remoteURIText.getText();
				// TODO Validate the remote URI (can't match existing remote)
			}
		});

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
				dontAutoChangeTrack  = true;
			}
		});

		return composite;
	}

	public String getRemoteURL()
	{
		return remoteURI;
	}

	public boolean track()
	{
		return track;
	}
}
