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

import com.aptana.core.util.StringUtil;
import com.aptana.git.core.model.GitExecutable;
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
		if (command == null || command.length == 0)
		{
			return null;
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
							String msg = MessageFormat
									.format("command returned non-zero exit value. wd: {0}, command: {1}", currentRepo.workingDirectory(), StringUtil.join(" ", command)); //$NON-NLS-1$ //$NON-NLS-2$
							GitUIPlugin.logWarning(msg);
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
		return null;
	}

	protected boolean supportsMultipleRepoOperation()
	{
		return false;
	}

	protected abstract String[] getCommand();

	protected abstract void postLaunch(GitRepository repo);
}
