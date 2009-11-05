package com.aptana.git.ui.internal;

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
import org.eclipse.core.runtime.jobs.Job;

import com.aptana.git.core.model.BranchChangedEvent;
import com.aptana.git.core.model.GitRepository;
import com.aptana.git.core.model.IGitRepositoryListener;
import com.aptana.git.core.model.IndexChangedEvent;
import com.aptana.git.core.model.RepositoryAddedEvent;

public class GitProjectRefresher implements IGitRepositoryListener
{

	public void branchChanged(BranchChangedEvent e)
	{
		refreshAffectedProjects(e.getRepository());
	}

	public void indexChanged(IndexChangedEvent e)
	{
		// TODO Auto-generated method stub

	}

	public void repositoryAdded(RepositoryAddedEvent e)
	{
		// TODO Auto-generated method stub

	}

	protected void refreshAffectedProjects(GitRepository repo)
	{
		final Set<IProject> affectedProjects = new HashSet<IProject>();
		for (IProject project : ResourcesPlugin.getWorkspace().getRoot().getProjects())
		{
			GitRepository other = GitRepository.getAttached(project);
			if (other != null && other.equals(repo))
				affectedProjects.add(project);
		}

		WorkspaceJob job = new WorkspaceJob("Refresh projects")
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
		job.setUser(false);
		job.setPriority(Job.LONG);
		job.schedule();
	}

}
