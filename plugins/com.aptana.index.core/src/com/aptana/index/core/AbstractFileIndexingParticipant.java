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
import java.util.regex.Matcher;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;

import com.aptana.core.logging.IdeLog;
import com.aptana.core.util.StringUtil;

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
	 * @deprecated
	 */
	protected void addIndex(Index index, IFileStore file, String category, String word)
	{
		addIndex(index, file.toURI(), category, word);
	}

	protected void addIndex(Index index, URI uri, String category, String word)
	{
		index.addEntry(category, word, uri);
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
			IdeLog.logError(IndexPlugin.getDefault(), e);
		}
	}

	/**
	 * If the underlying resource is a local file in the workspace, we try to grab the encoding. Otherwise return null,
	 * so that we attempt to sniff the charset in IOUtil.read.
	 * 
	 * @param fileStore
	 * @return
	 */
	protected String getCharset(IFileStore fileStore)
	{
		URI uri = fileStore.toURI();
		if ("file".equals(uri.getScheme())) //$NON-NLS-1$
		{
			IFile theFile = ResourcesPlugin.getWorkspace().getRoot()
					.getFileForLocation(Path.fromOSString(new File(uri).getAbsolutePath()));
			if (theFile != null)
			{
				try
				{
					return theFile.getCharset();
				}
				catch (CoreException e)
				{
					IdeLog.logError(IndexPlugin.getDefault(), e);
					// It's in the workspace, but we couldn't grab the encoding, fall back to workspace encoding.
					return ResourcesPlugin.getEncoding();
				}
			}
		}
		return null;
	}

	/**
	 * Returns a display string for use when indexing files
	 * 
	 * @param index
	 * @param file
	 * @return
	 * @deprecated
	 */
	protected String getIndexingMessage(Index index, IFileStore file)
	{
		return getIndexingMessage(index, file.toURI());
	}

	protected String getIndexingMessage(Index index, URI uri)
	{
		String relativePath = null;
		if (index != null)
		{
			relativePath = index.getRelativeDocumentPath(uri).toString();
		}
		else
		{
			relativePath = uri.toString();
		}

		return MessageFormat.format("Indexing {0}", relativePath); //$NON-NLS-1$
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.index.core.IFileStoreIndexingParticipant#getPriority()
	 */
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
						IdeLog.logError(IndexPlugin.getDefault(), e);
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
			IdeLog.logError(IndexPlugin.getDefault(), e);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.index.core.IFileStoreIndexingParticipant#setPriority(int)
	 */
	public void setPriority(int priority)
	{
		this.priority = priority;
	}

	protected int getLineNumber(int start, String source)
	{
		if (start < 0 || start >= source.length())
		{
			return -1;
		}
		if (start == 0)
		{
			return 1;
		}

		Matcher m = StringUtil.LINE_SPLITTER.matcher(source.substring(0, start));
		int line = 1;
		while (m.find())
		{
			int offset = m.start();
			if (offset > start)
			{
				break;
			}
			line++;
		}
		return line;
	}
}
