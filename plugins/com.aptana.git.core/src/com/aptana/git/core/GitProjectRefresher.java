package com.aptana.git.core;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.core.runtime.jobs.Job;

import com.aptana.git.core.model.BranchChangedEvent;
import com.aptana.git.core.model.ChangedFile;
import com.aptana.git.core.model.GitRepository;
import com.aptana.git.core.model.IGitRepositoryListener;
import com.aptana.git.core.model.IndexChangedEvent;
import com.aptana.git.core.model.RepositoryAddedEvent;

/**
 * Listens to repository changes and forces the relevant resources in the workspace to refresh.
 * 
 * @author cwilliams
 */
class GitProjectRefresher implements IGitRepositoryListener
{

	public void branchChanged(BranchChangedEvent e)
	{
		refreshAffectedProjects(e.getRepository());
	}

	public void indexChanged(IndexChangedEvent e)
	{
		// We get a list of the files whose status just changed. We need to refresh those and any
		// parents/ancestors of those.
		GitRepository repo = e.getRepository();
		String workingDirectory = repo.workingDirectory();
		Collection<ChangedFile> changedFiles = e.changedFiles();
		List<IResource> files = new ArrayList<IResource>();
		for (ChangedFile changedFile : changedFiles)
		{
			String path = workingDirectory + File.separator + changedFile.getPath();
			IFile file = ResourcesPlugin.getWorkspace().getRoot().getFileForLocation(new Path(path));
			if (file == null)
				continue;
			files.add(file);
			// Need to add all parents up to project!
			IContainer parent = null;
			IResource child = file;
			while ((parent = child.getParent()) != null)
			{
				files.add(parent);
				child = parent;
			}
		}
		refreshResources(files);
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

		refreshResources(affectedProjects);
	}

	private void refreshResources(final Collection<? extends IResource> resources)
	{
		WorkspaceJob job = new WorkspaceJob("Refresh projects")
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
