/**
 * Aptana Studio
 * Copyright (c) 2012-2013 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.core.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;

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
			InputStream inputStream = p.getInputStream();
			OutputStream outputStream = p.getOutputStream();
			PrintWriter pwdWriter = new PrintWriter(outputStream);
			br = new BufferedReader(new InputStreamReader(inputStream, IOUtil.UTF_8));
			String line = null;
			if (password != null)
			{
				pwdWriter.println(password); // writes the password to the prompt.
				pwdWriter.flush();
			}

			int status = 1;

			/*
			 * If the user provides wrong password, either we need to request for the other valid password and pass it
			 * to the sudo prompts. Otherwise, kill the existing sudo process and re-run the sudo process with a new
			 * valid password.
			 */
			while ((line = br.readLine()) != null)
			{
				builder.append(line).append('\n');
				if (line.contains(echoMessage))
				{
					// We're good, we got our success message, mark exit code of 0, break the loop
					status = 0;
					break;
				}
			}

			p.destroy(); // Force kill the sudo command as it is still expecting for the other 2 attempts
			this.status = new ProcessStatus(status, builder.toString(), builder.toString());
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

}
