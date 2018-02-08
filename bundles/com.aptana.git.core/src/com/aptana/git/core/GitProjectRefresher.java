/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.git.core;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
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
		// Do a smarter diff and only refresh files that have changed between the two:
		// git diff --name-only e.getOldBranchName() e.getNewBranchName()
		IStatus result = e.getRepository().execute(GitRepository.ReadWrite.READ, "diff", "--name-only", e.getOldBranchName(), e.getNewBranchName()); //$NON-NLS-1$ //$NON-NLS-2$
		if (result != null && result.isOK())
		{
			Collection<IResource> files = new ArrayList<IResource>();
			String output = result.getMessage();
			String[] lines = output.split("\r\n?|\n"); //$NON-NLS-1$ // $codepro.audit.disable platformSpecificLineSeparator
			for (String line : lines)
			{
				if (line == null || line.trim().length() == 0)
				{
					continue;
				}
				IFile file = ResourcesPlugin.getWorkspace().getRoot()
						.getFileForLocation(e.getRepository().workingDirectory().append(line));
				files.add(file);
			}
			refreshResources(files, IResource.DEPTH_ZERO);
		}
		else
		{
			refreshAffectedProjects(e.getRepository());
		}
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

	private void refreshAffectedProjects(GitRepository repo)
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

	private IGitRepositoryManager getRepositoryManager()
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
					{
						return Status.CANCEL_STATUS;
					}
					if (resource == null)
					{
						continue;
					}
					if (resource.getType() == IResource.PROJECT)
					{
						// Check to see if this project exists in the new branch! If not, auto-close the project, or
						// just not refresh it?
						File dir = resource.getLocation().toFile();
						if (!dir.exists())
						{
							// Close the project, this actually causes the .project file to get generated, though!
							try
							{
								resource.getProject().close(sub.newChild(100));
							}
							catch (CoreException e)
							{
								if (e.getStatus().getSeverity() > IStatus.WARNING)
								{
									throw e;
								}
							}
							File dotProject = new File(dir, IProjectDescription.DESCRIPTION_FILE_NAME);
							if (dotProject.delete())
							{
								dir.delete();
							}
							continue;
						}
					}
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

	public void repositoryAdded(RepositoryAddedEvent e)
	{
		e.getRepository().addListener(this);
	}

	public void repositoryRemoved(RepositoryRemovedEvent e)
	{
		e.getRepository().removeListener(this);
	}

}
