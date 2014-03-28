/**
 * Aptana Studio
 * Copyright (c) 2005-2013 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.ide.core.io.downloader;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;

import com.aptana.core.util.CollectionsUtil;
import com.aptana.ide.core.io.CoreIOPlugin;

/**
 * A file download manager.<br>
 * This manager can accept multiple files URLs to download. It then connects and retrieve the content while providing
 * progress information with time estimations.
 * 
 * @author Shalom Gibly <sgibly@aptana.com>
 */
public class DownloadManager
{
	private List<ContentDownloadRequest> downloads;
	private List<IPath> completedDownloadsPaths;

	/**
	 * Constructs a new DownloadManager
	 */
	public DownloadManager()
	{
	}

	/**
	 * Adds a URL for the pending downloads list.<br>
	 * Note that this method should be called <b>before</b> the {@link #start(IProgressMonitor)} is called.
	 * 
	 * @param url
	 *            A URL with a file-name to be downloaded.
	 * @throws CoreException
	 *             In case the URL file name cannot be extracted from the URL.
	 */
	public void addURL(URL url) throws CoreException
	{
		if (url != null)
		{
			addDownload(new ContentDownloadRequest(url));
		}
	}

	/**
	 * Adds a URL for the pending downloads list.<br>
	 * This method also accepts a {@link File} that the download process will write to.<br>
	 * Note that this method should be called <b>before</b> the {@link #start(IProgressMonitor)} is called.
	 * 
	 * @param url
	 *            A URL with a file-name to be downloaded.
	 * @param saveTo
	 *            The file to write to.
	 * @throws CoreException
	 *             In case the URL file name cannot be extracted from the URL.
	 */
	public void addURL(URL url, File saveTo) throws CoreException
	{
		if (url != null)
		{
			addDownload(new ContentDownloadRequest(url, saveTo));
		}
	}

	private synchronized void addDownload(ContentDownloadRequest request)
	{
		if (downloads == null)
		{
			downloads = new ArrayList<ContentDownloadRequest>(2);
		}
		downloads.add(request);
	}

	/**
	 * Adds a URL list for the pending downloads list.<br>
	 * Note that this method should be called <b>before</b> the {@link #start(IProgressMonitor)} is called.
	 * 
	 * @param urls
	 *            A URLs list, each URL should hold a file-name to be downloaded.
	 * @throws CoreException
	 *             In case one or more of the given URLs do not contain a file name.
	 */
	public void addURLs(List<URL> urls) throws CoreException
	{
		if (!CollectionsUtil.isEmpty(urls))
		{
			for (URL url : urls)
			{
				addURL(url);
			}
		}
	}

	/**
	 * Starts the downloads. Returns a status for the overall operation.
	 */
	public IStatus start(IProgressMonitor monitor)
	{
		if (CollectionsUtil.isEmpty(downloads))
		{
			return Status.OK_STATUS;
		}

		try
		{
			return download(monitor);
		}
		finally
		{
			if (monitor != null)
			{
				monitor.done();
			}
		}
	}

	/**
	 * Download the remote content.
	 * 
	 * @param monitor
	 */
	private IStatus download(IProgressMonitor monitor)
	{
		SubMonitor subMonitor = SubMonitor.convert(monitor, Messages.DownloadManager_downloadingContent,
				downloads.size());
		MultiStatus multi = new MultiStatus(CoreIOPlugin.PLUGIN_ID, IStatus.OK, null, null);
		completedDownloadsPaths = new ArrayList<IPath>(downloads.size());
		for (Iterator<ContentDownloadRequest> iterator = downloads.iterator(); iterator.hasNext();)
		{
			if (subMonitor.isCanceled())
			{
				// TODO Append to multi status and return that?
				return Status.CANCEL_STATUS;
			}

			ContentDownloadRequest request = iterator.next();
			request.execute(subMonitor.newChild(1));
			IStatus result = request.getResult();
			if (result != null)
			{
				if (result.isOK())
				{
					completedDownloadsPaths.add(request.getDownloadLocation());
					iterator.remove();
				}
				multi.add(result);
			}
			subMonitor.setWorkRemaining(downloads.size());
		}

		return multi;
	}

	/**
	 * Returns the location paths where the requested content was downloaded to.
	 * 
	 * @return A list of IPath containing the paths for the file that were downloaded.
	 */
	public List<IPath> getContentsLocations()
	{
		return Collections.unmodifiableList(CollectionsUtil.getListValue(completedDownloadsPaths));
	}
}
