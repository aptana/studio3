/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.ide.core.io.downloader;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;

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
	private List<String> completedDownloadsPaths;

	/**
	 * Constructs a new DownloadManager
	 */
	public DownloadManager()
	{
		downloads = new ArrayList<ContentDownloadRequest>();
		completedDownloadsPaths = new ArrayList<String>();
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
			this.downloads.add(new ContentDownloadRequest(url));
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
			this.downloads.add(new ContentDownloadRequest(url, saveTo));
		}
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
		if (urls != null)
		{
			for (URL url : urls)
			{
				if (url != null)
				{
					this.downloads.add(new ContentDownloadRequest(url));
				}
			}
		}
	}

	/**
	 * Starts the downloads. Returns a status for the overall operation.
	 */
	public IStatus start(IProgressMonitor monitor)
	{
		SubMonitor subMonitor = SubMonitor.convert(monitor, Messages.DownloadManager_downloadngContent, 1000);
		try
		{
			if (downloads.isEmpty())
			{
				return Status.OK_STATUS;
			}
			download(subMonitor);
			return getStatus(monitor);
		}
		finally
		{
			subMonitor.done();
		}
	}

	/**
	 * Download the remote content.
	 * 
	 * @param monitor
	 */
	protected void download(SubMonitor monitor)
	{
		int workUnits = 1000 / downloads.size();
		for (Iterator<ContentDownloadRequest> iterator = downloads.iterator(); iterator.hasNext();)
		{
			ContentDownloadRequest request = iterator.next();
			request.execute(monitor.newChild(workUnits));
			if (request.getResult() != null && request.getResult().isOK())
			{
				completedDownloadsPaths.add(request.getDownloadLocation());
				iterator.remove();
			}
			monitor.setWorkRemaining(downloads.size());
		}
	}

	/**
	 * Returns the location paths where the requested content was downloaded to.
	 * 
	 * @return A string array containing the paths for the file that were downloaded.
	 */
	public String[] getContentsLocations()
	{
		return completedDownloadsPaths.toArray(new String[completedDownloadsPaths.size()]);
	}

	private IStatus getStatus(IProgressMonitor monitor)
	{
		if (monitor != null && monitor.isCanceled())
		{
			return Status.CANCEL_STATUS;
		}
		if (downloads.isEmpty())
		{
			return Status.OK_STATUS;
		}

		MultiStatus result = new MultiStatus(CoreIOPlugin.PLUGIN_ID, IStatus.OK, null, null);
		for (ContentDownloadRequest request : downloads)
		{
			IStatus failed = request.getResult();
			if (failed != null && !failed.isOK())
				result.add(failed);
		}
		return result;
	}
}
