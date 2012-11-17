/**
 * Aptana Studio
 * Copyright (c) 2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.ui;

import java.io.IOException;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.Dialog;

import com.aptana.core.ShellExecutable;
import com.aptana.core.logging.IdeLog;
import com.aptana.core.util.ProcessRunnable;
import com.aptana.core.util.ProcessUtil;
import com.aptana.core.util.StringUtil;
import com.aptana.core.util.SudoProcessRunnable;
import com.aptana.ui.dialogs.SudoPasswordPromptDialog;
import com.aptana.ui.util.UIUtils;

/**
 * This class manages prompting the password dialog to the user and return the password back to the caller. In case user
 * does not provide valid passwords or press cancel button, it returns empty password.
 * 
 * @author pinnamuri
 */
public class SudoManager
{
	private char[] validPassword = new char[] {};
	private String SUDO = "sudo"; //$NON-NLS-1$
	private String ECHO = "echo"; //$NON-NLS-1$
	private String SUDO_INPUT_PWD = "-S"; //$NON-NLS-1$
	private String ECHO_MESSAGE = "SUCCESS"; //$NON-NLS-1$

	public SudoManager()
	{
	}

	/**
	 * Authenticates based on the given password and returns true if authentication is successful.
	 * 
	 * @param password
	 * @return
	 * @throws CoreException
	 */
	public boolean authenticate(char[] password) throws CoreException
	{
		try
		{
			Map<String, String> environment = ShellExecutable.getEnvironment();

			environment.put(ProcessUtil.REDIRECT_ERROR_STREAM, StringUtil.EMPTY);
			Process p = ProcessUtil.run(SUDO, null, environment, new String[] { SUDO_INPUT_PWD, ECHO, ECHO_MESSAGE });
			ProcessRunnable runnable = new SudoProcessRunnable(p, password, ECHO_MESSAGE);
			Thread t = new Thread(runnable, "SudoManager authentication thread"); //$NON-NLS-1$
			t.start();
			t.join();

			IStatus status = runnable.getResult();
			if (status.isOK())
			{
				validPassword = password;
				return true;
			}
		}
		catch (IOException e)
		{
			IdeLog.logError(UIPlugin.getDefault(), e.getMessage());
		}
		catch (InterruptedException e)
		{
			IdeLog.logError(UIPlugin.getDefault(), e.getMessage());
		}
		return false;
	}

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
		final IStatus[] status = new IStatus[] { Status.OK_STATUS };
		if (validPassword.length == 0)
		{
			UIUtils.getDisplay().syncExec(new Runnable()
			{
				public void run()
				{
					try
					{
						boolean retry;
						String promptMessage = Messages.SudoManager_MessagePrompt;
						do
						{
							retry = false;
							SudoPasswordPromptDialog sudoDialog = new SudoPasswordPromptDialog(UIUtils
									.getActiveWorkbenchWindow(), promptMessage);
							if (sudoDialog.open() == Dialog.OK && !authenticate(sudoDialog.getPassword()))
							{
								// Re-run the authentication dialog as long as user attempts to provide password.
								retry = true;
							}
							promptMessage = Messages.Sudo_Invalid_Password_Prompt;
						}
						while (retry);
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
