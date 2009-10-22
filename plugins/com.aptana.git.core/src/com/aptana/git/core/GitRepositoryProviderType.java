package com.aptana.git.core;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.team.core.RepositoryProviderType;

import com.aptana.git.core.model.GitRepository;

public class GitRepositoryProviderType extends RepositoryProviderType
{

	@Override
	public void metaFilesDetected(IProject project, IContainer[] containers)
	{
		// FIXME What if the container isn't the project root!
		if (GitRepository.getAttached(project) != null)
			return;

		final IProject toConnect = project;
		Job job = new Job("Auto-share")
		{

			@Override
			protected IStatus run(IProgressMonitor monitor)
			{
				if (monitor == null)
					monitor = new NullProgressMonitor();
				monitor.beginTask("Attaching project " + toConnect.getName(), 100);
				try
				{
					GitRepository.attachExisting(toConnect, new SubProgressMonitor(monitor, 100));
					monitor.done();
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
