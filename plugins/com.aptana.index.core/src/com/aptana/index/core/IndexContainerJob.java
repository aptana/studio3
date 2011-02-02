/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the Eclipse Public License (EPL).
 * Please see the license-epl.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.index.core;

import java.io.IOException;
import java.net.URI;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileInfo;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;

public class IndexContainerJob extends IndexRequestJob
{

	private ArrayList<String> fileURIs;

	protected IndexContainerJob(URI containerURI)
	{
		super(containerURI);
	}

	protected IndexContainerJob(String name, URI containerURI)
	{
		super(name, containerURI);
	}

	@Override
	public IStatus run(IProgressMonitor monitor)
	{
		SubMonitor sub = SubMonitor.convert(monitor, 1000);
		if (sub.isCanceled())
		{
			return Status.CANCEL_STATUS;
		}

		Index index = getIndex();
		if (index == null)
		{
			IndexPlugin.logError(MessageFormat.format("Index is null for container: {0}", getContainerURI()), null); //$NON-NLS-1$
			return Status.CANCEL_STATUS;
		}
		try
		{
			// Collect the full set of files in the project...
			Set<IFileStore> files = addFiles(EFS.getStore(getContainerURI()), sub.newChild(100));
			if (sub.isCanceled())
			{
				return Status.CANCEL_STATUS;
			}

			// Checks what's in the index, and if any of the files in there no longer exist, we now remove them...
			Set<String> documents = index.queryDocumentNames(null);
			removeDeletedFiles(index, documents, files);
			sub.worked(100);

			// Ok, we removed files, and now if there's none left in project we can just end here.
			if (files == null || files.isEmpty())
			{
				return Status.OK_STATUS;
			}
			if (sub.isCanceled())
			{
				return Status.CANCEL_STATUS;
			}

			// Should check timestamp of index versus timestamps of files, only index files that are out of date
			// (for Ruby)!
			long timestamp = 0L;
			if (documents != null && documents.size() > 0)
			{
				// If there's nothing in the index, index everything; otherwise use last modified time of index to
				// filter...
				timestamp = index.getIndexFile().lastModified();
			}
			files = filterFiles(timestamp, files);
			sub.worked(50);

			if (files != null && !files.isEmpty())
			{
				indexFileStores(index, files, sub.newChild(750));
			}
		}
		catch (CoreException e)
		{
			return e.getStatus();
		}
		catch (IOException e)
		{
			IndexPlugin.logError(e.getMessage(), e);
		}
		finally
		{
			try
			{
				index.save();
			}
			catch (IOException e)
			{
				IndexPlugin.logError("An error occurred while saving an index", e); //$NON-NLS-1$
			}
			sub.done();
		}
		return Status.OK_STATUS;
	}

	private Set<IFileStore> addFiles(IFileStore file, IProgressMonitor monitor)
	{
		SubMonitor sub = SubMonitor.convert(monitor, 10);
		Set<IFileStore> files = new HashSet<IFileStore>();
		try
		{
			if (file == null)
				return files;
			IFileInfo info = file.fetchInfo(EFS.NONE, sub.newChild(1));
			if (!info.exists())
			{
				return files;
			}
			if (info.isDirectory())
			{
				try
				{
					IFileStore[] fileList = file.childStores(EFS.NONE, sub.newChild(2));
					if (fileList == null || fileList.length == 0)
						return files;
					for (IFileStore child : fileList)
					{
						files.addAll(addFiles(child, sub.newChild(7)));
					}
				}
				catch (CoreException e)
				{
					IndexPlugin.logError(e);
				}
			}
			else
			{
				if (info.exists())
				{
					files.add(file);
				}
			}
		}
		catch (CoreException e)
		{
			IndexPlugin.logError(e);
		}
		finally
		{
			sub.done();
		}
		return files;
	}

	private void removeDeletedFiles(Index index, Set<String> documents, Set<IFileStore> files)
	{
		for (String docName : documents)
		{
			if (!fileExists(files, docName))
			{
				index.remove(URI.create(docName));
			}
		}
	}

	protected Set<IFileStore> filterFiles(long indexLastModified, Set<IFileStore> files)
	{
		Set<IFileStore> filtered = new HashSet<IFileStore>();
		for (IFileStore file : files)
		{
			if (file.fetchInfo().getLastModified() >= indexLastModified)
			{
				filtered.add(file);
			}
		}
		return filtered;
	}

	private boolean fileExists(Set<IFileStore> files, String lastString)
	{
		return Collections.binarySearch(getFileURIs(files), lastString) >= 0;
	}

	private List<? extends String> getFileURIs(Set<IFileStore> files)
	{
		if (fileURIs == null)
		{
			fileURIs = new ArrayList<String>();
			for (IFileStore store : files)
			{
				fileURIs.add(store.toURI().toString());
			}
			Collections.sort(fileURIs);
		}
		return fileURIs;
	}

}