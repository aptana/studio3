/**
 * Aptana Studio
 * Copyright (c) 2012-2013 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.core.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import com.aptana.core.CorePlugin;
import com.aptana.core.logging.IdeLog;

/**
 * Responsible for scanning the output from sudo prompt.
 * 
 * @author pinnamuri
 */
public class SudoProcessRunnable extends ProcessRunnable
{

	private char[] password;
	private String echoMessage;

	public SudoProcessRunnable(Process p, char[] password, String echoMessage)
	{
		super(p, null, true);
		this.password = password;
		this.echoMessage = echoMessage;
	}

	public void run()
	{
		StringBuilder builder = new StringBuilder();
		BufferedReader br = null;
		try
		{
			br = new BufferedReader(new InputStreamReader(p.getInputStream(), IOUtil.UTF_8));
			if (password != null)
			{
				PrintWriter pwdWriter = new PrintWriter(p.getOutputStream());
				pwdWriter.println(password); // writes the password to the prompt.
				pwdWriter.flush();
			}

			int exitCode = 1;
			StringBuilder line = new StringBuilder();
			int charRead = 0;
			while ((charRead = br.read()) != -1)
			{
				char c = (char) charRead;
				line.append(c);
				if (c == '\n')
				{
					// end of line! record it, wipe our temp line builder, check for success
					String lineString = line.toString();
					line = new StringBuilder();
					builder.append(lineString);
					if (lineString.contains(echoMessage))
					{
						// We're good, we got our success message, mark exit code of 0, break the loop
						exitCode = 0;
						break;
					}
				}
				// Now ensure there's more to read. If not, then let's assume we're blocked on more input and fail
				if (!isReady(br, 3, 50))
				{
					exitCode = 1;
					break;
				}
			}

			p.destroy(); // Force kill the sudo command as it may still be expecting input for the other 2 attempts
			this.status = new ProcessStatus(exitCode, builder.toString(), builder.toString());
		}
		catch (Exception e)
		{
			IdeLog.logError(CorePlugin.getDefault(), e);
			this.status = new Status(IStatus.ERROR, CorePlugin.PLUGIN_ID, e.getMessage(), e);
		}
		finally
		{
			if (br != null)
			{
				try
				{
					br.close();
				}
				catch (Exception e)
				{
				}
			}
			monitor.done();
		}

	}

	/**
	 * Tries to determine in a non-blocking way if the underlying reader has more data to read. If it does we return
	 * true, otherwise we return false. This will attempt to wait up to the max attempts and will sleep for the given
	 * interval between attempts. If after that has elapsed it's still not ready (guaranteed not to block on next read),
	 * we return false.
	 * 
	 * @param br
	 * @param maxAttempts
	 * @param sleepInterval
	 * @return
	 * @throws IOException
	 */
	private boolean isReady(Reader br, int maxAttempts, long sleepInterval) throws IOException
	{
		int waitCount = 0;
		while (!br.ready())
		{
			if (waitCount >= maxAttempts)
			{
				return false;
			}
			try
			{
				Thread.sleep(sleepInterval);
			}
			catch (Exception e)
			{
				// ignore
			}
			waitCount++;
		}
		return true;
	}

}
