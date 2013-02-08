/**
 * Aptana Studio
 * Copyright (c) 2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.core.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;

import com.aptana.core.CorePlugin;
import com.aptana.core.logging.IdeLog;

/**
 * A Runnable which sniffs the output of a process to pipe the last line of output to the subTask for an
 * IProgressMonitor.
 * 
 * @author cwilliams
 */
public class ProcessRunnable implements Runnable
{
	protected Process p;
	protected IProgressMonitor monitor;
	protected IStatus status;
	protected boolean isErrRedirected;

	public ProcessRunnable(Process p, IProgressMonitor monitor, boolean isErrRedirected)
	{
		this.p = p;
		this.isErrRedirected = isErrRedirected;
		this.monitor = convertMonitor(monitor);
		this.status = Status.OK_STATUS;
	}

	protected IProgressMonitor convertMonitor(IProgressMonitor monitor)
	{
		return SubMonitor.convert(monitor, 100);
	}

	public IStatus getResult()
	{
		return status;
	}

	public void run()
	{
		StringBuilder builder = new StringBuilder();
		BufferedReader br = null;
		try
		{
			preRunActions();
			InputStream errorStream = p.getErrorStream();
			if (isErrRedirected)
			{
				// Since error stream is directed into input stream, we just need to read
				// through only input stream.
				errorStream = p.getInputStream();
			}
			br = new BufferedReader(new InputStreamReader(errorStream, IOUtil.UTF_8));
			String line = null;
			while ((line = br.readLine()) != null) // $codepro.audit.disable assignmentInCondition
			{
				if (monitor.isCanceled())
				{
					p.destroy();
					this.status = Status.CANCEL_STATUS;
					return;
				}

				builder.append(line).append('\n');
				handleLine(line);
			}

			String stdout = builder.toString();
			if (!isErrRedirected)
			{
				stdout = IOUtil.read(p.getInputStream(), IOUtil.UTF_8);
			}
			if (builder.length() > 0)
			{
				builder.deleteCharAt(builder.length() - 1);
			}
			this.status = new ProcessStatus(p.waitFor(), stdout, builder.toString());
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

	protected void handleLine(String line)
	{
		monitor.subTask(line);
	}

	protected void preRunActions()
	{
		// no-op
	}
}