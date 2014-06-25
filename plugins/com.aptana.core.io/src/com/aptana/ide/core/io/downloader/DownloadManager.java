/**
 * Aptana Studio
 * Copyright (c) 2005-2014 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.ide.core.io.downloader;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.filesystem.EFS;
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
	 * @deprecated Use {@link #addURI(URI)}
	 */
	public void addURL(URL url) throws CoreException
	{
		if (url != null)
		{
			try
			{
				addURI(url.toURI());
			}
			catch (URISyntaxException e)
			{
				throw new CoreException(new Status(IStatus.ERROR, CoreIOPlugin.PLUGIN_ID, e.getMessage(), e));
			}
		}
	}

	/**
	 * Adds a URI for the pending downloads list.<br>
	 * Note that this method should be called <b>before</b> the {@link #start(IProgressMonitor)} is called.
	 * 
	 * @param uri
	 *            A URI with a file-name to be downloaded.
	 * @throws CoreException
	 *             In case the URI file name cannot be extracted from the URI.
	 */
	public void addURI(URI uri) throws CoreException
	{
		if (uri != null)
		{
			if (EFS.SCHEME_FILE.equals(uri.getScheme()))
			{
				addDownload(new FileDownloadRequest(uri));
			}
			else
			{
				addDownload(new ContentDownloadRequest(uri));
			}
		}
	}

	/**
	 * Adds a URI for the pending downloads list.<br>
	 * This method also accepts a {@link File} that the download process will write to.<br>
	 * Note that this method should be called <b>before</b> the {@link #start(IProgressMonitor)} is called.
	 * 
	 * @param uri
	 *            A URI with a file-name to be downloaded.
	 * @param saveTo
	 *            The file to write to.
	 * @throws CoreException
	 *             In case the URI file name cannot be extracted from the URI.
	 */
	public void addURI(URI uri, File saveTo) throws CoreException
	{
		if (uri != null)
		{
			if (EFS.SCHEME_FILE.equals(uri.getScheme()))
			{
				addDownload(new FileDownloadRequest(uri, saveTo));
			}
			else
			{
				addDownload(new ContentDownloadRequest(uri, saveTo));
			}
		}
	}

	protected synchronized void addDownload(ContentDownloadRequest request)
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
	public void addURIs(List<URI> uris) throws CoreException
	{
		if (!CollectionsUtil.isEmpty(uris))
		{
			for (URI uri : uris)
			{
				addURI(uri);
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
	protected IStatus download(IProgressMonitor monitor)
	{
		SubMonitor subMonitor = SubMonitor.convert(monitor, Messages.DownloadManager_downloadingContent,
				downloads.size());
		MultiStatus multi = new MultiStatus(CoreIOPlugin.PLUGIN_ID, IStatus.OK, null, null);
		completedDownloadsPaths = new ArrayList<IPath>(downloads.size());
		for (Iterator<ContentDownloadRequest> iterator = downloads.iterator(); iterator.hasNext();)
		{
			if (subMonitor.isCanceled())
			{
				multi.add(Status.CANCEL_STATUS);
				// TODO Add cancel status for all the rest of the downloads?
				return multi;
			}

			// FIXME If the request if for a file URI, we should cheat and not "download" anything. We should just
			// basically do a no-op and return the original path (or copy to intended saveLocation)

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
