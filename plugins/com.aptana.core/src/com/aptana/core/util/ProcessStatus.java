/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.core.util;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import com.aptana.core.CorePlugin;

/**
 * A special subclass of IStatus that we can cast to and grab the stdout or stderr explicitly in cases where that is
 * needed. Otherwise it defaults to old behavior of returning stdout, and using stderr if exit code is not 0 and stdout
 * is empty. Please note that there is still no easy way to get combined stdout/stderr in chronologically like you'd get
 * with {@link ProcessBuilder#redirectErrorStream(boolean)}
 * 
 * @author cwilliams
 */
public class ProcessStatus extends Status
{

	private String stdout;
	private String stderr;

	public ProcessStatus(int exitCode, String stdout, String stderr)
	{
		super(exitCode == 0 ? IStatus.OK : IStatus.ERROR, CorePlugin.PLUGIN_ID, exitCode, generateMessage(exitCode,
				stdout, stderr), null);
		this.stdout = stdout;
		this.stderr = stderr;
	}

	private static String generateMessage(int exitCode, String stdOut, String stderr)
	{
		if (exitCode != 0 && StringUtil.isEmpty(stdOut))
		{
			return stderr;
		}
		// TODO We probably shouldn't be removing the newline automatically here.
		if (stdOut != null && stdOut.endsWith("\n")) //$NON-NLS-1$
		{
			return stdOut.substring(0, stdOut.length() - 1);
		}
		// Append any error line on top of the stdOut (one line only)
		if (!StringUtil.isEmpty(stderr))
		{
			String[] lines = stderr.split("[\n\r]+"); //$NON-NLS-1$
			for (int i = lines.length - 1; i >= 0; i--)
			{
				String line = lines[i];
				if (line.startsWith("[ERROR] :")) //$NON-NLS-1$
				{
					stdOut = line.substring(9).trim() + '\n' + stdOut;
					break;
				}
			}
		}
		return stdOut;
	}

	public String getStdErr()
	{
		return this.stderr;
	}

	public String getStdOut()
	{
		return this.stdout;
	}

	/**
	 * Returns an {@link IStatus} with the message holding the output of stderr.
	 * 
	 * @return
	 */
	public IStatus getStdErrStatus()
	{
		return new Status(getSeverity(), getPlugin(), getCode(), getStdErr(), null);
	}

}
