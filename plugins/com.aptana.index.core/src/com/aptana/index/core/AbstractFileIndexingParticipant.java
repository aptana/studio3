/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the Eclipse Public License (EPL).
 * Please see the license-epl.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.index.core;

import java.io.File;
import java.net.URI;
import java.text.MessageFormat;
import java.util.Set;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;

public abstract class AbstractFileIndexingParticipant implements IFileStoreIndexingParticipant
{
	private static final String EXTERNAL_URI = "uri"; //$NON-NLS-1$

	private int priority;

	/**
	 * addIndex
	 * 
	 * @param index
	 * @param file
	 * @param category
	 * @param word
	 */
	protected void addIndex(Index index, IFileStore file, String category, String word)
	{
		index.addEntry(category, word, file.toURI());
	}

	/**
	 * createTask
	 * 
	 * @param store
	 * @param message
	 * @param priority
	 * @param line
	 * @param start
	 * @param end
	 */
	protected void createTask(IFileStore store, String message, int priority, int line, int start, int end)
	{
		try
		{
			IResource resource = getResource(store);
			IMarker marker = resource.createMarker(IMarker.TASK);
			if (resource.equals(ResourcesPlugin.getWorkspace().getRoot()))
			{
				marker.setAttribute(EXTERNAL_URI, store.toURI().toString());
			}
			marker.setAttribute(IMarker.MESSAGE, message);
			marker.setAttribute(IMarker.PRIORITY, priority);
			marker.setAttribute(IMarker.LINE_NUMBER, line);
			marker.setAttribute(IMarker.CHAR_START, start);
			marker.setAttribute(IMarker.CHAR_END, end);
		}
		catch (CoreException e)
		{
			IndexPlugin.logError(e);
		}
	}

	/**
	 * Returns a display string for use when indexing files
	 * 
	 * @param index
	 * @param file
	 * @return
	 */
	protected String getIndexingMessage(Index index, IFileStore file)
	{
		return MessageFormat.format("Indexing {0}", index.getRelativeDocumentPath(file.toURI()).toString()); //$NON-NLS-1$
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.index.core.IFileStoreIndexingParticipant#getPriority()
	 */
	@Override
	public int getPriority()
	{
		return priority;
	}

	/**
	 * getResource
	 * 
	 * @param store
	 * @return
	 */
	private IResource getResource(IFileStore store)
	{
		URI uri = store.toURI();
		if (uri.getScheme().equals(EFS.SCHEME_FILE))
		{
			File file = new File(uri);
			IFile iFile = ResourcesPlugin.getWorkspace().getRoot()
					.getFileForLocation(Path.fromOSString(file.getAbsolutePath()));
			if (iFile != null)
			{
				return iFile;
			}
		}
		return ResourcesPlugin.getWorkspace().getRoot();
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.index.core.IFileStoreIndexingParticipant#index(java.util.Set, com.aptana.index.core.Index,
	 * org.eclipse.core.runtime.IProgressMonitor)
	 */
	public void index(Set<IFileStore> files, Index index, IProgressMonitor monitor) throws CoreException
	{
		SubMonitor sub = SubMonitor.convert(monitor, files.size() * 100);

		for (IFileStore file : files)
		{
			if (sub.isCanceled())
			{
				throw new CoreException(Status.CANCEL_STATUS);
			}

			Thread.yield(); // be nice to other threads, let them get in before each file...

			indexFileStore(index, file, sub.newChild(100));
		}

		sub.done();
	}

	/**
	 * indexFileStore
	 * 
	 * @param index
	 * @param store
	 * @param monitor
	 */
	protected abstract void indexFileStore(final Index index, IFileStore store, IProgressMonitor monitor);

	/**
	 * removeTasks
	 * 
	 * @param store
	 * @param monitor
	 */
	protected void removeTasks(IFileStore store, IProgressMonitor monitor)
	{
		URI uri = store.toURI();
		String uriString = uri.toString();

		try
		{
			IResource resource = getResource(store);
			if (resource.equals(ResourcesPlugin.getWorkspace().getRoot()))
			{
				// Iterate over markers on the root and remove any with matching "uri"
				IMarker[] tasks = resource.findMarkers(IMarker.TASK, true, IResource.DEPTH_ZERO);
				for (IMarker task : tasks)
				{
					if (task == null)
					{
						continue;
					}
					try
					{
						String markerURI = (String) task.getAttribute(EXTERNAL_URI);
						if (markerURI == null)
						{
							continue;
						}
						if (markerURI.equals(uriString))
						{
							task.delete();
						}
					}
					catch (CoreException e)
					{
						IndexPlugin.logError(e);
					}
				}
			}
			else
			{
				resource.deleteMarkers(IMarker.TASK, true, IResource.DEPTH_ZERO);
			}
		}
		catch (CoreException e)
		{
			IndexPlugin.logError(e);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.index.core.IFileStoreIndexingParticipant#setPriority(int)
	 */
	@Override
	public void setPriority(int priority)
	{
		this.priority = priority;
	}
}
