/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.git.ui.internal.actions;

import java.text.MessageFormat;
import java.util.Set;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.model.IProcess;

import com.aptana.core.logging.IdeLog;
import com.aptana.core.util.ArrayUtil;
import com.aptana.core.util.StringUtil;
import com.aptana.git.core.GitPlugin;
import com.aptana.git.core.model.GitRepository;
import com.aptana.git.ui.GitUIPlugin;
import com.aptana.git.ui.internal.Launcher;

abstract class AbstractSimpleGitCommandHandler extends AbstractGitHandler
{

	@Override
	protected Object doExecute(ExecutionEvent event) throws ExecutionException
	{
		final Set<GitRepository> repos = getSelectedRepositories();
		if (repos == null || repos.isEmpty())
		{
			openError(Messages.CommitAction_NoRepo_Title, Messages.CommitAction_NoRepo_Message);
			return null;
		}
		if (!supportsMultipleRepoOperation() && repos.size() > 1)
		{
			openError(Messages.CommitAction_MultipleRepos_Title, Messages.CommitAction_MultipleRepos_Message);
			return null;
		}

		final String[] command = getCommand();
		if (ArrayUtil.isEmpty(command))
		{
			return null;
		}

		// Run one job per repo
		for (final GitRepository currentRepo : repos)
		{
			runCommandAsJob(command, currentRepo);
		}
		return null;
	}

	protected void runCommandAsJob(final String[] command, final GitRepository currentRepo)
	{
		StringBuilder jobName = new StringBuilder("git"); //$NON-NLS-1$
		for (String string : command)
		{
			jobName.append(' ').append(string);
		}
		jobName.append(" (").append(currentRepo.workingDirectory()).append(')'); //$NON-NLS-1$

		Job job = new Job(jobName.toString())
		{
			@Override
			protected IStatus run(IProgressMonitor monitor)
			{
				SubMonitor sub = SubMonitor.convert(monitor, 10000);
				if (sub.isCanceled())
				{
					return Status.CANCEL_STATUS;
				}

				if (!currentRepo.enterWriteProcess())
				{
					return new Status(IStatus.ERROR, GitPlugin.getPluginId(),
							Messages.GitLaunchDelegate_FailedToAcquireWriteLock);
				}
				try
				{
					ILaunch launch = Launcher.launch(currentRepo, sub.newChild(100), command);

					if (launch.getProcesses() == null || launch.getProcesses().length < 1)
					{
						// If something went wrong and there's no process (like unsaved files and user cancelled
						// dialog)
						return Status.OK_STATUS;
					}

					IProcess process = launch.getProcesses()[0];
					while (!launch.isTerminated())
					{
						Thread.yield();
						if (sub.isCanceled())
						{
							launch.terminate();
							return Status.CANCEL_STATUS;
						}
						sub.worked(1);
					}

					int exitValue = process.getExitValue();
					if (exitValue != 0)
					{
						String msg = MessageFormat
								.format("command returned non-zero exit value. wd: {0}, command: {1}", currentRepo.workingDirectory(), StringUtil.join(" ", command)); //$NON-NLS-1$ //$NON-NLS-2$
						IdeLog.logWarning(GitUIPlugin.getDefault(), msg);
					}
				}
				catch (CoreException e)
				{
					return e.getStatus();
				}
				catch (Throwable e)
				{
					return new Status(IStatus.ERROR, GitUIPlugin.getPluginId(), e.getMessage(), e);
				}
				finally
				{
					currentRepo.exitWriteProcess();
				}
				sub.setWorkRemaining(1000);
				postLaunch(currentRepo);
				sub.done();

				return Status.OK_STATUS;
			}
		};
		job.setPriority(Job.LONG);
		job.schedule();
	}

	protected boolean supportsMultipleRepoOperation()
	{
		return false;
	}

	protected abstract String[] getCommand();

	protected abstract void postLaunch(GitRepository repo);
}
