/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
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
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;

import com.aptana.core.IFilter;
import com.aptana.core.IMap;
import com.aptana.core.logging.IdeLog;
import com.aptana.core.util.CollectionsUtil;

/**
 * This class takes the URI of a container. It collects all the files underneath the container recursively, and then
 * attempts to index the diff since our last index. this involves wiping entries for files/documents that no longer
 * exist, and re-indexing files that have been modified since the last time we modified our index file.
 * 
 * @author cwilliams
 */
public class IndexContainerJob extends IndexRequestJob
{

	public IndexContainerJob(URI containerURI)
	{
		super(containerURI);
	}

	public IndexContainerJob(String name, URI containerURI)
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
			Set<IFileStore> files = IndexUtil.getAllFiles(getContainerFileStore(), sub.newChild(100));
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

	private IFileStore getContainerFileStore() throws CoreException
	{
		return EFS.getStore(getContainerURI());
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