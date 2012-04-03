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
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;

import com.aptana.core.logging.IdeLog;

// TODO Should we just remove this entirely? It's only used in one place!
public class RemoveIndexOfFilesOfProjectJob extends IndexRequestJob
{

	private final IProject project;
	private final Set<IFile> files;

	public RemoveIndexOfFilesOfProjectJob(IProject project, Set<IFile> files)
	{
		super(MessageFormat.format(Messages.RemoveIndexOfFilesOfProjectJob_Name, project.getName()), project
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
		if (!project.isAccessible() || getContainerURI() == null)
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
				IdeLog.logError(IndexPlugin.getDefault(), e);
			}
			sub.done();
		}
		return Status.OK_STATUS;
	}
}
