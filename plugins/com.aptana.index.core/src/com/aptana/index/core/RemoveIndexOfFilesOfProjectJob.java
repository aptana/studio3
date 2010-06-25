package com.aptana.index.core;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;

class RemoveIndexOfFilesOfProjectJob extends IndexRequestJob
{

	private final IProject project;
	private final Set<IFile> files;

	public RemoveIndexOfFilesOfProjectJob(IProject project, Set<IFile> files)
	{
		super(MessageFormat.format("Removing entries for files in index of project {0}", project.getName()), project
				.getLocationURI());
		this.project = project;
		this.files = files;
	}

	@Override
	public IStatus run(IProgressMonitor monitor)
	{
		SubMonitor sub = SubMonitor.convert(monitor, files.size());
		if (sub.isCanceled())
		{
			return Status.CANCEL_STATUS;
		}
		if (!project.isAccessible())
		{
			return Status.CANCEL_STATUS;
		}

		Index index = getIndex();
		try
		{
			// Cleanup indices for files
			for (IFile file : files)
			{
				if (monitor.isCanceled())
				{
					return Status.CANCEL_STATUS;
				}
				index.remove(file.getLocationURI());
				sub.worked(1);
			}
		}
		finally
		{
			try
			{
				index.save();
			}
			catch (IOException e)
			{
				IndexActivator.logError(e.getMessage(), e);
			}
			sub.done();
		}
		return Status.OK_STATUS;
	}

}