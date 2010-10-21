/**
 * This file Copyright (c) 2005-2010 Aptana, Inc. This program is
 * dual-licensed under both the Aptana Public License and the GNU General
 * Public license. You may elect to use one or the other of these licenses.
 * 
 * This program is distributed in the hope that it will be useful, but
 * AS-IS and WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE, TITLE, or
 * NONINFRINGEMENT. Redistribution, except as permitted by whichever of
 * the GPL or APL you select, is prohibited.
 *
 * 1. For the GPL license (GPL), you can redistribute and/or modify this
 * program under the terms of the GNU General Public License,
 * Version 3, as published by the Free Software Foundation.  You should
 * have received a copy of the GNU General Public License, Version 3 along
 * with this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 * 
 * Aptana provides a special exception to allow redistribution of this file
 * with certain other free and open source software ("FOSS") code and certain additional terms
 * pursuant to Section 7 of the GPL. You may view the exception and these
 * terms on the web at http://www.aptana.com/legal/gpl/.
 * 
 * 2. For the Aptana Public License (APL), this program and the
 * accompanying materials are made available under the terms of the APL
 * v1.0 which accompanies this distribution, and is available at
 * http://www.aptana.com/legal/apl/.
 * 
 * You may view the GPL, Aptana's exception and additional terms, and the
 * APL in the file titled license.html at the root of the corresponding
 * plugin containing this source file.
 * 
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.git.ui.actions;

import java.text.MessageFormat;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.jface.dialogs.MessageDialog;

import com.aptana.git.core.model.GitExecutable;
import com.aptana.git.core.model.GitRepository;
import com.aptana.git.ui.GitUIPlugin;
import com.aptana.git.ui.internal.Launcher;
import com.aptana.git.ui.internal.actions.Messages;

public abstract class SimpleGitCommandAction extends GitAction
{

	@Override
	public void run()
	{
		final Set<GitRepository> repos = getSelectedRepositories();
		if (repos == null || repos.isEmpty())
		{
			MessageDialog.openError(getShell(), Messages.CommitAction_NoRepo_Title,
					Messages.CommitAction_NoRepo_Message);
			return;
		}
		if (!supportsMultipleRepoOperation() && repos.size() > 1)
		{
			MessageDialog.openError(getShell(), Messages.CommitAction_MultipleRepos_Title,
					Messages.CommitAction_MultipleRepos_Message);
			return;
		}

		final String[] command = getCommand();
		if (command == null || command.length == 0)
		{
			return;
		}
		String baseJobName = "git"; //$NON-NLS-1$
		for (String string : command)
		{
			baseJobName += " " + string; //$NON-NLS-1$
		}

		// Run one job per repo
		for (final GitRepository currentRepo : repos)
		{
			String jobName = baseJobName;
			if (repos.size() > 1)
			{
				jobName += " (" + currentRepo.workingDirectory() + ")"; //$NON-NLS-1$ //$NON-NLS-2$
			}
			Job job = new Job(jobName)
			{
				@Override
				protected IStatus run(IProgressMonitor monitor)
				{
					SubMonitor sub = SubMonitor.convert(monitor, 10000);
					if (sub.isCanceled())
					{
						return Status.CANCEL_STATUS;
					}

					try
					{
						ILaunch launch = Launcher.launch(GitExecutable.instance().path().toOSString(),
								currentRepo.workingDirectory(), command);
						sub.worked(100);
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

						int exitValue = launch.getProcesses()[0].getExitValue();
						if (exitValue != 0)
						{
							return new Status(
									IStatus.ERROR,
									GitUIPlugin.getPluginId(),
									MessageFormat
											.format("command returned non-zero exit value. wd: {0}, command: {1}", currentRepo.workingDirectory(), command)); //$NON-NLS-1$
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
					sub.setWorkRemaining(1000);
					postLaunch(currentRepo);
					sub.done();

					return Status.OK_STATUS;
				}
			};
			job.setPriority(Job.LONG);
			job.schedule();
		}
	}

	protected boolean supportsMultipleRepoOperation()
	{
		return false;
	}

	protected abstract String[] getCommand();

	/**
	 * Hook for running code after the launch has terminated.
	 */
	protected abstract void postLaunch(GitRepository repo);

	protected void refreshRepoIndex(GitRepository repo)
	{
		if (repo != null)
		{
			repo.index().refreshAsync(); // queue up a refresh
		}
	}
}
