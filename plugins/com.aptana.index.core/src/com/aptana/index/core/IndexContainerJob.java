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

import com.aptana.core.IFilter;
import com.aptana.core.IMap;
import com.aptana.core.logging.IdeLog;
import com.aptana.core.util.ArrayUtil;
import com.aptana.core.util.CollectionsUtil;

public class IndexContainerJob extends IndexRequestJob
{

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
			IdeLog.logError(IndexPlugin.getDefault(),
					MessageFormat.format("Index is null for container: {0}", getContainerURI())); //$NON-NLS-1$
			return Status.CANCEL_STATUS;
		}

		try
		{
			// Collect the full set of files in the project...
			Set<IFileStore> files = addFiles(getContainerFileStore(), sub.newChild(100));
			if (sub.isCanceled())
			{
				return Status.CANCEL_STATUS;
			}

			// Collect any "special" files contributed for the container URI. Mostly this is to allow files associated
			// with IProjects to be included in indexing
			files.addAll(getContributedFiles(getContainerURI()));

			// Checks what's in the index, and if any of the files in there no longer exist, we now remove them...
			Set<String> documents = index.queryDocumentNames(null);
			sub.worked(25);
			if (sub.isCanceled())
			{
				return Status.CANCEL_STATUS;
			}
			removeDeletedFiles(index, documents, files, sub.newChild(75));

			// Ok, we removed files, and now if there's none left in project we can just end here.
			if (CollectionsUtil.isEmpty(files))
			{
				return Status.OK_STATUS;
			}

			// Should check timestamp of index versus timestamps of files, only index files that are out of date
			// (for Ruby)!
			long timestamp = 0L;
			if (!CollectionsUtil.isEmpty(documents))
			{
				// If there's nothing in the index, index everything; otherwise use last modified time of index to
				// filter...
				timestamp = index.getIndexFile().lastModified();
			}

			if (sub.isCanceled())
			{
				return Status.CANCEL_STATUS;
			}
			files = filterFilesByTimestamp(timestamp, files);
			sub.worked(50);

			if (!CollectionsUtil.isEmpty(files))
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
			IdeLog.logError(IndexPlugin.getDefault(), e);
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

	protected IFileStore getContainerFileStore() throws CoreException
	{
		return EFS.getStore(getContainerURI());
	}

	/**
	 * Given an {@link IFileStore}, we traverse to add all files underneath it. This method is recursive, traversing
	 * into sub-directories. TODO Combine with logic from EFSUtils in core.io!
	 * 
	 * @param file
	 * @param monitor
	 * @return
	 */
	private Set<IFileStore> addFiles(IFileStore file, IProgressMonitor monitor)
	{
		// TODO We should likely call IFileSystem.fetchTree and use that if it doesn't return null (because that is more
		// efficient in some schemes)!
		SubMonitor sub = SubMonitor.convert(monitor, 10);
		Set<IFileStore> files = new HashSet<IFileStore>();
		try
		{
			if (file == null)
			{
				return files;
			}
			IFileInfo info = file.fetchInfo(EFS.NONE, sub.newChild(1));
			if (!info.exists())
			{
				return files;
			}
			// We know it exists...
			if (info.isDirectory())
			{
				try
				{
					// Now try to dive into directory and add all children recursively
					IFileStore[] fileList = file.childStores(EFS.NONE, sub.newChild(2));
					if (ArrayUtil.isEmpty(fileList))
					{
						return files;
					}
					for (IFileStore child : fileList)
					{
						files.addAll(addFiles(child, sub.newChild(7)));
					}
				}
				catch (CoreException e)
				{
					IdeLog.logError(IndexPlugin.getDefault(), e);
				}
			}
			else
			{
				// it's a file that exists, base case, add it.
				files.add(file);
			}
		}
		catch (CoreException e)
		{
			IdeLog.logError(IndexPlugin.getDefault(), e);
		}
		finally
		{
			sub.done();
		}
		return files;
	}

	// TODO Combine this with RemoveFilesOfIndexJob logic?
	private void removeDeletedFiles(Index index, Set<String> documents, Set<IFileStore> files, IProgressMonitor monitor)
	{
		if (CollectionsUtil.isEmpty(documents))
		{
			return;
		}

		SubMonitor sub = SubMonitor.convert(monitor, files.size() + documents.size());

		// Turn list of file stores into set of unique URI strings
		List<String> fileStoreURIs = CollectionsUtil.map(files, new IMap<IFileStore, String>()
		{
			public String map(IFileStore item)
			{
				return item.toURI().toString();
			}
		});
		Set<String> uris = new HashSet<String>(fileStoreURIs);
		sub.worked(files.size());

		// Now remove any document from index that isn't in the file listing.
		for (String docName : documents)
		{
			if (!uris.contains(docName))
			{
				index.remove(URI.create(docName));
			}
			sub.worked(1);
		}
		sub.done();
	}

	/**
	 * Filters the set of {@link IFileStore}s to those whose lastMod is at or after the passed in mod timestamp.
	 * 
	 * @param indexLastModified
	 * @param files
	 * @return
	 */
	protected Set<IFileStore> filterFilesByTimestamp(final long indexLastModified, Set<IFileStore> files)
	{
		Set<IFileStore> filtered = new HashSet<IFileStore>(files.size());
		CollectionsUtil.filter(files, filtered, new IFilter<IFileStore>()
		{
			public boolean include(IFileStore item)
			{
				return item.fetchInfo().getLastModified() >= indexLastModified;
			}
		});
		return filtered;
	}

}