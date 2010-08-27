package com.aptana.index.core;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;

/**
 * This job updates/re-indexes the Set of IFiles passed in for a given IProject. This does _not_ filter the file set in
 * any way based on timestamps, it will force a re-index of each file!
 * 
 * @author cwilliams
 */
class IndexFilesOfProjectJob extends IndexRequestJob
{

	private final IProject project;
	private final Set<IFile> files;

	public IndexFilesOfProjectJob(IProject project, Set<IFile> files)
	{
		super(MessageFormat.format("Indexing files in project {0}", project.getName()), project.getLocationURI());
		this.project = project;
		this.files = files;
	}

	@Override
	public IStatus run(IProgressMonitor monitor)
	{
		SubMonitor sub = SubMonitor.convert(monitor, 10 * files.size());
		if (sub.isCanceled())
		{
			return Status.CANCEL_STATUS;
		}

		if (!project.isAccessible())
		{
			return Status.CANCEL_STATUS;
		}

		Index index = getIndex();
		if (index == null)
		{
			IndexActivator.logError(MessageFormat.format("Index is null for container: {0}", getContainerURI()), null); //$NON-NLS-1$
			return Status.CANCEL_STATUS;
		}
		try
		{
			Set<IFileStore> fileStores = toFileStores(sub.newChild(files.size()));
			if (sub.isCanceled())
			{
				return Status.CANCEL_STATUS;
			}
			indexFileStores(index, fileStores, sub.newChild(9 * files.size()));
		}
		catch (CoreException e)
		{
			return e.getStatus();
		}
		finally
		{
			try
			{
				index.save();
			}
			catch (IOException e)
			{
				IndexActivator.logError("An error occurred while saving an index", e);
			}
			sub.done();
		}
		return Status.OK_STATUS;
	}

	private Set<IFileStore> toFileStores(IProgressMonitor monitor)
	{
		SubMonitor sub = SubMonitor.convert(monitor, files.size());
		Set<IFileStore> fileStores = new HashSet<IFileStore>(files.size());
		for (IFile file : files)
		{
			try
			{
				IFileStore store = EFS.getStore(file.getLocationURI());
				if (store == null)
					continue;
				fileStores.add(store);
			}
			catch (CoreException e)
			{
				IndexActivator.logError(e);
			}
			finally
			{
				sub.worked(1);
			}
		}
		return fileStores;
	}

}