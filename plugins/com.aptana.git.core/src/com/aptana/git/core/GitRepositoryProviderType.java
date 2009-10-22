package com.aptana.git.core;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.team.core.RepositoryProvider;
import org.eclipse.team.core.RepositoryProviderType;
import org.eclipse.team.core.TeamException;

import com.aptana.git.core.model.GitRepository;

public class GitRepositoryProviderType extends RepositoryProviderType
{

	@Override
	public void metaFilesDetected(IProject project, IContainer[] containers)
	{
		// FIXME What if the container isn't the project root!
		if (GitRepository.instance(project) != null)
			return;

		final IProject toConnect = project;
		Job job = new Job("Auto-share")
		{

			@Override
			protected IStatus run(IProgressMonitor monitor)
			{
				if (monitor == null)
					monitor = new NullProgressMonitor();
				try
				{
					GitRepository repo = GitRepository.create(toConnect.getLocationURI());
					monitor.worked(40);
					if (repo != null)
					{
						RepositoryProvider.map(toConnect, GitRepositoryProvider.ID);
						monitor.worked(10);
						toConnect.refreshLocal(IResource.DEPTH_INFINITE, new SubProgressMonitor(monitor, 50));
						// TODO Need to force the labels to be drawn!
					}
					monitor.done();
				}
				catch (TeamException e)
				{
					return new Status(IStatus.ERROR, GitPlugin.getPluginId(), e.getMessage(), e);
				}
				catch (CoreException e)
				{
					return e.getStatus();
				}
				return Status.OK_STATUS;
			}
		};
		job.setSystem(true);
		job.schedule();
	}
}
