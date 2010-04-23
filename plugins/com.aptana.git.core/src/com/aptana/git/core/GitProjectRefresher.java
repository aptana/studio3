package com.aptana.git.core;

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
import org.eclipse.core.runtime.jobs.Job;

import com.aptana.git.core.model.AbstractGitRepositoryListener;
import com.aptana.git.core.model.BranchChangedEvent;
import com.aptana.git.core.model.GitRepository;
import com.aptana.git.core.model.IGitRepositoriesListener;
import com.aptana.git.core.model.IGitRepositoryManager;
import com.aptana.git.core.model.IndexChangedEvent;
import com.aptana.git.core.model.PullEvent;
import com.aptana.git.core.model.RepositoryAddedEvent;
import com.aptana.git.core.model.RepositoryRemovedEvent;

/**
 * Listens to repository changes and forces the relevant resources in the workspace to refresh.
 * 
 * @author cwilliams
 */
class GitProjectRefresher extends AbstractGitRepositoryListener implements IGitRepositoriesListener
{

	public void branchChanged(BranchChangedEvent e)
	{
		refreshAffectedProjects(e.getRepository());
	}

	@Override
	public void pulled(PullEvent e)
	{
		refreshAffectedProjects(e.getRepository());
	}

	public void indexChanged(IndexChangedEvent e)
	{
		// We get a list of the files whose status just changed. We need to refresh those and any
		// parents/ancestors of those.
		refreshResources(e.getFilesWithChanges(), IResource.DEPTH_ZERO);
	}

	protected void refreshAffectedProjects(GitRepository repo)
	{
		final Set<IProject> affectedProjects = new HashSet<IProject>();
		for (IProject project : ResourcesPlugin.getWorkspace().getRoot().getProjects())
		{
			GitRepository other = getRepositoryManager().getAttached(project);
			if (other != null && other.equals(repo))
				affectedProjects.add(project);
		}

		refreshResources(affectedProjects, IResource.DEPTH_INFINITE);
	}

	protected IGitRepositoryManager getRepositoryManager()
	{
		return GitPlugin.getDefault().getGitRepositoryManager();
	}

	private void refreshResources(final Collection<? extends IResource> resources, final int depth)
	{
		if (resources == null || resources.isEmpty())
			return;

		WorkspaceJob job = new WorkspaceJob("Refresh projects") //$NON-NLS-1$
		{
			@Override
			public IStatus runInWorkspace(IProgressMonitor monitor) throws CoreException
			{
				int work = 100 * resources.size();
				SubMonitor sub = SubMonitor.convert(monitor, work);
				for (IResource resource : resources)
				{
					if (sub.isCanceled())
						return Status.CANCEL_STATUS;
					resource.refreshLocal(depth, sub.newChild(100));
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

	@Override
	public void repositoryAdded(RepositoryAddedEvent e)
	{
		e.getRepository().addListener(this);
	}

	@Override
	public void repositoryRemoved(RepositoryRemovedEvent e)
	{
		e.getRepository().removeListener(this);
	}

}
