/**
 * Aptana Studio
 * Copyright (c) 2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the Eclipse Public License (EPL).
 * Please see the license-epl.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.index.core;

import java.io.IOException;
import java.net.URI;
import java.text.MessageFormat;
import java.util.Set;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;

import com.aptana.core.logging.IdeLog;
import com.aptana.core.util.CollectionsUtil;

/**
 * A special job which re-indexes a single file. The file is treated as it's own "container". The file is only
 * re-indexed if the file's timestamp is later than the index file we wrote to for it.
 * 
 * @author cwilliams
 */
public class IndexFileJob extends IndexRequestJob
{

	public IndexFileJob(String name, URI containerURI)
	{
		super(name, containerURI);
	}

	@Override
	public IStatus run(IProgressMonitor monitor)
	{
		SubMonitor sub = SubMonitor.convert(monitor, 100);
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
			IFileStore file = EFS.getStore(getContainerURI());
			sub.worked(2);

			// Checks what's in the index, and if any of the files in there no longer exist, we now remove them...
			Set<String> documents = index.queryDocumentNames(null);
			sub.worked(3);

			// Should check timestamp of index versus timestamps of files, only index files that are out of date
			// (for Ruby)!
			long timestamp = 0L;
			if (!CollectionsUtil.isEmpty(documents))
			{
				// If there's nothing in the index, index everything; otherwise use last modified time of index to
				// filter...
				timestamp = index.getIndexFile().lastModified();
			}
			sub.worked(2);

			if (sub.isCanceled())
			{
				return Status.CANCEL_STATUS;
			}

			if (file.fetchInfo(EFS.NONE, sub.newChild(3)).getLastModified() >= timestamp)
			{
				indexFileStores(index, CollectionsUtil.newSet(file), sub.newChild(90));
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

}
