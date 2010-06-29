package com.aptana.index.core;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
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
			filterFiles(timestamp, files);
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
			IndexActivator.logError(e.getMessage(), e);
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
					IndexActivator.logError(e);
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
			IndexActivator.logError(e);
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

	private void filterFiles(long indexLastModified, Set<IFileStore> files)
	{
		Iterator<IFileStore> iter = files.iterator();
		while (iter.hasNext())
		{
			IFileStore file = iter.next();
			if (file.fetchInfo().getLastModified() < indexLastModified)
			{
				iter.remove();
			}
		}
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