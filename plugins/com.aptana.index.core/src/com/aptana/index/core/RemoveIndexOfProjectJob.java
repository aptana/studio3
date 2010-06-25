package com.aptana.index.core;

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
		super(MessageFormat.format("Removing index for project {0}", project.getName()), project.getLocationURI());
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