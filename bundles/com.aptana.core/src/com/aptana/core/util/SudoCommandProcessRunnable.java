/**
 * Aptana Studio
 * Copyright (c) 2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.core.util;

import java.io.PrintWriter;

import org.eclipse.core.runtime.IProgressMonitor;

/**
 * This writes the password into the input stream of the process.
 * 
 * @author pinnamuri
 */
public class SudoCommandProcessRunnable extends ProcessRunnable
{
	private char[] password;

	public SudoCommandProcessRunnable(Process p, IProgressMonitor monitor, boolean isErrRedirected, char[] password)
	{
		super(p, monitor, isErrRedirected);
		this.password = password;

	}

	public void preRunActions()
	{
		if (password == null || password.length == 0)
		{
			return;
		}
		PrintWriter pwdWriter = new PrintWriter(p.getOutputStream());
		pwdWriter.println(password); // writes the password to the prompt.
		pwdWriter.flush();
	}

}
