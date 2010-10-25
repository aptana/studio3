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

public class AddRemoteDialog extends InputDialog
{

	private Text remoteURIText;
	protected String remoteURI;

	public AddRemoteDialog(final Shell parentShell, String remoteName, String defaultURI)
	{
		super(parentShell, Messages.AddRemoteDialog_AddRemoteDialog_Title,
				Messages.AddRemoteDialog_AddRemoteDialog_Message, remoteName, new IInputValidator()
				{

					public String isValid(String newText)
					{
						if (newText == null || newText.trim().length() == 0)
							return Messages.AddRemoteDialog_NonEmptyRemoteNameMessage;
						if (newText.trim().contains(" ") || newText.trim().contains("\t")) //$NON-NLS-1$ //$NON-NLS-2$
							return Messages.AddRemoteDialog_NoWhitespaceRemoteNameMessage;
						// TODO What else do we need to do to verify the remote name?
						return null;
					}
				});
		remoteURI = defaultURI;
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

		return composite;
	}

	public String getRemoteURL()
	{
		return remoteURI;
	}
}
