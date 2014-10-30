/**
 * Aptana Studio
 * Copyright (c) 2012-2014 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.ui;

import java.text.MessageFormat;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.window.IShellProvider;
import org.eclipse.swt.widgets.Shell;

import com.aptana.core.util.EclipseUtil;
import com.aptana.core.util.SudoManager;
import com.aptana.ui.dialogs.SudoPasswordPromptDialog;
import com.aptana.ui.util.UIUtils;

/**
 * This class manages prompting the password dialog to the user and return the password back to the caller. In case user
 * does not provide valid passwords or press cancel button, it returns empty password.
 * 
 * @author pinnamuri
 * @author cwilliams
 */
public class SudoUIManager
{

	// We can run -k alone to invalidate cached credentials (forcing prompt next time)
	// We can run -k <command> to run ignoring cached credentials (forcing prompt now)
	// If password is empty/null, how can we verify if user is allowed to run commands that way?

	private static int MAX_ATTEMPTS = 3;

	/**
	 * A null password mean we have nop stored "good" password. An empty array means sudo requires no password.
	 * Otherwise holds the password required for sudo.
	 */
	private char[] validPassword = null;

	/**
	 * This is responsible for prompting the dialog to the user and returns the password back to the caller. If the user
	 * cancels the dialog, then empty password is passed back to the caller.
	 * 
	 * @param promptMessage
	 * @return
	 * @throws CoreException
	 */
	public char[] getPassword() throws CoreException
	{
		// FIXME Handle when password is empty (meaning sudo doesn't require a password!)
		final IStatus[] status = new IStatus[] { Status.OK_STATUS };
		if (validPassword == null)
		{
			// If the system doesn't require a password, we don't need to pop the prompt at all!
			final SudoManager sudoMngr = new SudoManager();
			if (sudoMngr.authenticate(null))
			{
				// sudo doesn't require password!
				return new char[0];
			}

			UIUtils.getDisplay().syncExec(new Runnable()
			{
				public void run()
				{
					try
					{
						boolean retry;
						int retryAttempts = 0;
						String promptMessage = MessageFormat.format(Messages.SudoManager_MessagePrompt,
								EclipseUtil.getStudioPrefix());
						do
						{
							retryAttempts++;
							retry = false;
							SudoPasswordPromptDialog sudoDialog = new SudoPasswordPromptDialog(new IShellProvider()
							{

								public Shell getShell()
								{
									return UIUtils.getActiveShell();
								}
							}, promptMessage);
							int open = sudoDialog.open();
							if (open == Dialog.OK)
							{
								if (sudoMngr.authenticate(sudoDialog.getPassword()))
								{
									validPassword = sudoDialog.getPassword();
								}
								else
								{
									// Re-run the authentication dialog as long as user attempts to provide password.
									retry = true;
								}
							}
							else if (open == Dialog.CANCEL)
							{
								throw new CoreException(Status.CANCEL_STATUS);
							}
							promptMessage = Messages.Sudo_Invalid_Password_Prompt;
						}
						while (retry && retryAttempts < MAX_ATTEMPTS);
						if (validPassword == null && retryAttempts >= MAX_ATTEMPTS)
						{
							// User has exceeded the max attempts to provide the password.
							throw new CoreException(Status.CANCEL_STATUS);
						}
					}
					catch (CoreException e)
					{
						status[0] = e.getStatus();
					}
				}
			});

		}
		if (status[0] != Status.OK_STATUS)
		{
			throw new CoreException(status[0]);
		}
		return validPassword;
	}
}
