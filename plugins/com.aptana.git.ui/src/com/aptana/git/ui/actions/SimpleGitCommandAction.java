package com.aptana.git.ui.actions;

import java.io.File;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.debug.core.ILaunch;

import com.aptana.git.core.model.GitExecutable;
import com.aptana.git.core.model.GitRepository;
import com.aptana.git.ui.internal.Launcher;

public abstract class SimpleGitCommandAction extends GitAction
{

	@Override
	public void run()
	{
		File workingDir = getWorkingDir();
		String working = null;
		if (workingDir != null)
			working = workingDir.toString();
		final String finWorking = working;
		final String[] command = getCommand();
		StringBuilder jobName = new StringBuilder("git"); //$NON-NLS-1$
		for (String string : command)
		{
			jobName.append(" ").append(string); //$NON-NLS-1$
		}
		Job job = new Job(jobName.toString())
		{
			@Override
			protected IStatus run(IProgressMonitor monitor)
			{
				ILaunch launch = Launcher.launch(GitExecutable.instance().path(), finWorking, command);
				while (!launch.isTerminated())
				{
					Thread.yield();
					if (monitor.isCanceled())
						return Status.CANCEL_STATUS;
				}
				postLaunch();
				return Status.OK_STATUS;
			}
		};
		job.setUser(true);
		job.setPriority(Job.LONG);
		job.schedule();
	}

	protected abstract String[] getCommand();

	/**
	 * Hook for running code after the launch has terminated.
	 */
	protected abstract void postLaunch();

	private File getWorkingDir()
	{
		IResource[] resources = getSelectedResources();
		if (resources == null || resources.length == 0)
			return null;
		IProject project = resources[0].getProject();
		GitRepository repo = GitRepository.getAttached(project);
		if (repo == null)
			return null;
		return new File(repo.workingDirectory());
	}
	
	protected void refreshRepoIndex()
	{
		IResource[] resources = getSelectedResources();
		if (resources == null || resources.length == 0)
			return;
		IProject project = resources[0].getProject();
		GitRepository repo = GitRepository.getAttached(project);
		if (repo != null)
			repo.index().refresh();
		// FIXME All the staged files that got committed don't seem to be getting their decorations updated!
	}
}
