package com.aptana.index.core;

import java.net.URI;
import java.text.MessageFormat;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.core.runtime.jobs.IJobManager;
import org.eclipse.core.runtime.jobs.Job;

class RemoveIndexOfProjectJob extends IndexRequestJob
{

	public RemoveIndexOfProjectJob(IProject project)
	{
		super(MessageFormat.format("Removing index for project {0}", project.getName()), getURI(project));
	}

	private static URI getURI(IProject project)
	{
		URI uri = project.getLocationURI();
		if (uri != null)
		{
			return uri;
		}
		IndexActivator.logError(
				MessageFormat.format("Project's location URI is null. raw location: {0}, path: {1}",
						project.getRawLocationURI(), project.getFullPath()), null);
		uri = project.getRawLocationURI();
		return uri;
	}

	@Override
	public boolean shouldRun()
	{
		return shouldSchedule();
	}

	@Override
	public boolean shouldSchedule()
	{
		return getContainerURI() != null;
	}

	@Override
	public IStatus run(IProgressMonitor monitor)
	{
		SubMonitor sub = SubMonitor.convert(monitor, 2);
		try
		{
			if (sub.isCanceled())
			{
				return Status.CANCEL_STATUS;
			}

			IndexManager.getInstance().removeIndex(getContainerURI());
			sub.worked(1);

			// Remove any pending jobs in the family
			IJobManager jobManager = Job.getJobManager();
			jobManager.cancel(getContainerURI());
		}
		finally
		{
			sub.done();
		}

		return Status.OK_STATUS;
	}

}