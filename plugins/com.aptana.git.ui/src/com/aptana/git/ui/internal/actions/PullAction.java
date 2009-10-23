package com.aptana.git.ui.internal.actions;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.jface.action.IAction;

import com.aptana.git.core.model.GitRepository;

public class PullAction extends GitAction
{

	@Override
	protected String getCommand()
	{
		return "pull";
	}

	protected void execute(IAction action) throws InvocationTargetException, InterruptedException
	{
		super.execute(action);

		final Set<IProject> affectedProjects = new HashSet<IProject>();
		for (IResource resource : getSelectedResources())
		{
			if (resource == null)
				continue;
			affectedProjects.add(resource.getProject());
			GitRepository repo = GitRepository.getAttached(resource.getProject());
			if (repo != null)
			{
				affectedProjects.addAll(getAssociatedProjects(repo));
			}
		}

		WorkspaceJob job = new WorkspaceJob("Refresh resources")
		{
			@Override
			public IStatus runInWorkspace(IProgressMonitor monitor) throws CoreException
			{
				int work = 100 * affectedProjects.size();
				SubMonitor sub = SubMonitor.convert(monitor, work);
				for (IProject resource : affectedProjects)
				{
					if (sub.isCanceled())
						return Status.CANCEL_STATUS;
					resource.refreshLocal(IResource.DEPTH_INFINITE, sub.newChild(100));
				}
				sub.done();
				return Status.OK_STATUS;
			}
		};
		job.setRule(ResourcesPlugin.getWorkspace().getRoot());
		job.setUser(true);
		job.schedule();
	}

	private Collection<? extends IProject> getAssociatedProjects(GitRepository repo)
	{
		Set<IProject> projects = new HashSet<IProject>();
		for (IProject project : ResourcesPlugin.getWorkspace().getRoot().getProjects())
		{
			GitRepository other = GitRepository.getAttached(project);
			if (other != null && other.equals(repo))
			{
				projects.add(project);
			}
		}
		return projects;
	}
}
