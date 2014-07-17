/**
 * Aptana Studio
 * Copyright (c) 2012-2014 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.core.util;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;

import com.aptana.core.CorePlugin;
import com.aptana.core.ShellExecutable;
import com.aptana.core.logging.IdeLog;

/**
 * This class manages prompting the password dialog to the user and return the password back to the caller. In case user
 * does not provide valid passwords or press cancel button, it returns empty password.
 * 
 * @author pinnamuri
 * @author cwilliams
 */
public class SudoManager
{
	private static final String DISREGARD_CACHED_CREDENTIALS = "-k"; //$NON-NLS-1$
	private static final String SUDO = "sudo"; //$NON-NLS-1$
	private static final String ECHO = "echo"; //$NON-NLS-1$
	private static final String SUDO_INPUT_PWD = "-S"; //$NON-NLS-1$
	private static final String ECHO_MESSAGE = "SUCCESS"; //$NON-NLS-1$
	private static final String NON_INTERACTIVE = "-n"; //$NON-NLS-1$
	private static final String PASSWORD_PROMPT_FLAG = "-p"; //$NON-NLS-1$
	private static final String END_OF_OPTIONS = "--"; //$NON-NLS-1$

	/**
	 * The prompt that is forced to appear on the sudo commands (if the sudo timeout is already expired).
	 */
	public static final String PROMPT_MSG = "password:"; //$NON-NLS-1$

	/**
	 * Authenticates based on the given password and returns true if authentication is successful. A null or empty
	 * password is treated as sudo not requiring a password (and in both cases we will cache the password as an empty
	 * char[]).
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
			environment.put(IProcessRunner.REDIRECT_ERROR_STREAM, StringUtil.EMPTY);

			ProcessRunnable runnable;
			// If the password is empty/null, don't add -S!
			if (password == null || password.length == 0)
			{
				password = new char[0]; // when we store the password, store it as "empty", not null
				// Just try running sudo -k echo SUCCESS with no password
				Process p = getProcessRunner().run(environment, SUDO, DISREGARD_CACHED_CREDENTIALS, NON_INTERACTIVE,
						END_OF_OPTIONS, ECHO, ECHO_MESSAGE);

				// Don't pass along password...
				runnable = new SudoProcessRunnable(p, null, ECHO_MESSAGE);
			}
			else
			{
				// Try running and pass password on STDIN
				Process p = getProcessRunner().run(environment, SUDO, DISREGARD_CACHED_CREDENTIALS, SUDO_INPUT_PWD,
						PASSWORD_PROMPT_FLAG, PROMPT_MSG, END_OF_OPTIONS, ECHO, ECHO_MESSAGE);
				runnable = new SudoProcessRunnable(p, password, ECHO_MESSAGE);
			}

			IStatus status = getResult(runnable);
			if (status.isOK())
			{
				return true;
			}
			IdeLog.log(CorePlugin.getDefault(), status);
		}
		catch (IOException e)
		{
			IdeLog.logError(CorePlugin.getDefault(), e.getMessage());
		}
		catch (InterruptedException e)
		{
			IdeLog.logError(CorePlugin.getDefault(), e.getMessage());
		}
		return false;
	}

	protected IStatus getResult(ProcessRunnable runnable) throws InterruptedException
	{
		Thread t = new Thread(runnable, "SudoManager authentication thread"); //$NON-NLS-1$
		t.start();
		t.join();
		IStatus status = runnable.getResult();
		return status;
	}

	protected IProcessRunner getProcessRunner()
	{
		return new ProcessRunner();
	}

	/**
	 * Returns the list of arguments required to prompt the user for password and allow to pass the password through
	 * standard input instead of terminal.
	 * 
	 * @param sudoPassword
	 * @return
	 */
	public List<String> getArguments(char[] sudoPassword)
	{
		if (PlatformUtil.isWindows())
		{
			return Collections.emptyList();
		}
		// FIXME Centralize this logic in a core SudoManager class?
		if (sudoPassword == null || sudoPassword.length == 0)
		{
			// Force non-interactive mode so that if sudo decides we do need a password it exits with an error instead
			// of hanging
			return CollectionsUtil.newList(SUDO, NON_INTERACTIVE, END_OF_OPTIONS);
		}
		return CollectionsUtil.newList(SUDO, PASSWORD_PROMPT_FLAG, PROMPT_MSG, SUDO_INPUT_PWD, END_OF_OPTIONS);
	}
}
