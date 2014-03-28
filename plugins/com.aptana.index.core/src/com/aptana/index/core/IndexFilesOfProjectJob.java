/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the Eclipse Public License (EPL).
 * Please see the license-epl.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
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

import com.aptana.core.logging.IdeLog;

/**
 * This job updates/re-indexes the Set of IFiles passed in for a given IProject. This does _not_ filter the file set in
 * any way based on timestamps, it will force a re-index of each file!
 * 
 * @author cwilliams
 */
// TODO Remove this entirely! It's only used in one place!
public class IndexFilesOfProjectJob extends IndexRequestJob
{

	private final IProject project;
	private final Set<IFile> files;

	public IndexFilesOfProjectJob(IProject project, Set<IFile> files)
	{
		super(MessageFormat.format(Messages.IndexFilesOfProjectJob_Name, project.getName()), project.getLocationURI());
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
			IdeLog.logError(IndexPlugin.getDefault(),
					MessageFormat.format("Index is null for container: {0}", getContainerURI())); //$NON-NLS-1$
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
				IdeLog.logError(IndexPlugin.getDefault(), "An error occurred while saving an index", e); //$NON-NLS-1$
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
				{
					continue;
				}
				fileStores.add(store);
			}
			catch (CoreException e)
			{
				IdeLog.logError(IndexPlugin.getDefault(), e);
			}
			finally
			{
				sub.worked(1);
			}
		}
		return fileStores;
	}

}