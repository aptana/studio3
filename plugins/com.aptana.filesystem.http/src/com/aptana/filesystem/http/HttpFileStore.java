/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.filesystem.http;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.MessageFormat;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileInfo;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.filesystem.provider.FileInfo;
import org.eclipse.core.filesystem.provider.FileStore;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;

import com.aptana.core.logging.IdeLog;
import com.aptana.core.util.FileUtil;

public class HttpFileStore extends FileStore
{

	private URI uri;

	HttpFileStore(URI uri)
	{
		this.uri = uri;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.core.filesystem.IFileStore#childNames(int, org.eclipse.core.runtime.IProgressMonitor)
	 */
	public String[] childNames(int options, IProgressMonitor monitor) throws CoreException
	{
		// TODO Can we really tell what the child names are over http? I don't think we can
		return new String[0];
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.core.filesystem.IFileStore#fetchInfo(int, org.eclipse.core.runtime.IProgressMonitor)
	 */
	public IFileInfo fetchInfo(int options, IProgressMonitor monitor) throws CoreException
	{
		FileInfo result = new FileInfo(getName());
		try
		{
			HttpURLConnection connection = (HttpURLConnection) uri.toURL().openConnection();
			connection.setUseCaches(false);
			connection.setAllowUserInteraction(false);
			connection.setDoOutput(false);
			connection.setRequestMethod("HEAD"); //$NON-NLS-1$
			int length = connection.getContentLength();
			long lastModified = connection.getLastModified();

			result.setExists(true);
			result.setLastModified(lastModified);
			if (length == -1)
			{
				result.setLength(EFS.NONE);
			}
			else
			{
				result.setLength(length);
			}
		}
		catch (IOException e)
		{
			// throw new CoreException(new Status(IStatus.ERROR, HttpFilesystemPlugin.PLUGIN_ID, EFS.ERROR_READ,
			// e.getMessage(), e));
			HttpFilesystemPlugin.log(e);
			result.setExists(false);
		}
		return result;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.core.filesystem.IFileStore#getChild(java.lang.String)
	 */
	public IFileStore getChild(String name)
	{
		return new HttpFileStore(uri.resolve(name));
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.core.filesystem.IFileStore#getName()
	 */
	public String getName()
	{
		return uri.toString();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.core.filesystem.IFileStore#getParent()
	 */
	public IFileStore getParent()
	{
		// We need to take the uri.getPath and lop off the last segment!
		try
		{
			String path = uri.getPath();
			IPath aPath = new Path(path);
			aPath = aPath.removeLastSegments(1);
			// FIXME What about fragments/query params?
			URI parentURI = new URI(uri.getScheme(), uri.getUserInfo(), uri.getHost(), uri.getPort(), aPath
					.toPortableString(), uri.getQuery(), uri.getFragment());
			return new HttpFileStore(parentURI);
		}
		catch (URISyntaxException e)
		{
			HttpFilesystemPlugin.log(e);
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.core.filesystem.IFileStore#toURI()
	 */
	public URI toURI()
	{
		return uri;
	}

	@Override
	public synchronized File toLocalFile(int options, IProgressMonitor monitor) throws CoreException
	{
		if ((options & EFS.CACHE) == 0)
			return null;

		SubMonitor sub = SubMonitor.convert(monitor);

		// Try to grab copied cache file based on URL!
		File tmpDir = FileUtil.getTempDirectory().toFile();
		File cached = new File(tmpDir, getPath(uri));
		if (cached.exists())
		{
			IFileStore localFile = EFS.getLocalFileSystem().fromLocalFile(cached);
			long lastModified = localFile.fetchInfo(EFS.NONE, sub.newChild(5)).getLastModified();
			long remoteLastModified = fetchInfo(EFS.NONE, sub.newChild(20)).getLastModified();

			// TODO What if one or both return EFS.NONE?
			if (lastModified >= remoteLastModified)
			{
				if (IdeLog.isTraceEnabled(HttpFilesystemPlugin.getDefault(), IDebugScopes.FILESYSTEM))
				{
					IdeLog.logTrace(HttpFilesystemPlugin.getDefault(),
							MessageFormat.format(
							"Returning locally cached URI {0} version for remote URI {0}", localFile.toURI(), uri)); //$NON-NLS-1$
				}
				return cached;
			}
		}
		sub.worked(25);
		// make directory structure for our copy
		if (!cached.getParentFile().exists() && !cached.getParentFile().mkdirs())
		{
			throw new CoreException(new Status(IStatus.ERROR, HttpFilesystemPlugin.PLUGIN_ID, EFS.ERROR_INTERNAL,
							MessageFormat
									.format("Unable to create directory structure {0} for locally cached copy", cached.getParentFile()), null)); //$NON-NLS-1$
		}

		// Download to some filename we can associate and pull back up based on URL! (used above)
		IFileStore resultStore = EFS.getLocalFileSystem().fromLocalFile(cached);
		copy(resultStore, EFS.OVERWRITE, sub.newChild(75));
		cached.deleteOnExit();

		if (IdeLog.isTraceEnabled(HttpFilesystemPlugin.getDefault(), IDebugScopes.FILESYSTEM))
		{
			IdeLog.logTrace(HttpFilesystemPlugin.getDefault(),
					MessageFormat.format("Caching remote URI {0} to local URI {1}", uri, resultStore.toURI())); //$NON-NLS-1$
		}

		return cached;
	}

	/**
	 * Given a URL, returns a filesystem-sanctioned-path
	 * 
	 * @param uri
	 * @return
	 */
	public static String getPath(URI uri)
	{
		char separator = '_';

		// FIXME What about fragments/query params?
		StringBuilder builder = new StringBuilder();
		builder.append(uri.getScheme());
		builder.append(separator);
		builder.append(uri.getHost());
		builder.append(separator);
		int port = uri.getPort();
		int defaultPort = 80;
		if ("https".equalsIgnoreCase(uri.getScheme())) //$NON-NLS-1$
		{
			defaultPort = 443;
		}
		if (port != -1 && port != defaultPort)
		{
			builder.append(uri.getPort());
			builder.append(separator);
		}
		builder.append(separator);
		builder.append(builder.hashCode());
		builder.append(new Path(uri.getPath().replace('/', separator)).toOSString());
		return builder.toString();
	}

	@Override
	public InputStream openInputStream(int options, IProgressMonitor monitor) throws CoreException
	{
		monitor = SubMonitor.convert(monitor, 2);
		try
		{
			monitor.beginTask("", 1); //$NON-NLS-1$
			return uri.toURL().openStream();
		}
		catch (MalformedURLException e)
		{
			HttpFilesystemPlugin.log(e);
			throw new CoreException(new Status(IStatus.ERROR, HttpFilesystemPlugin.PLUGIN_ID, EFS.ERROR_NO_LOCATION, e
					.getMessage(), e));
		}
		catch (IOException e)
		{
			HttpFilesystemPlugin.log(e);
			throw new CoreException(new Status(IStatus.ERROR, HttpFilesystemPlugin.PLUGIN_ID, EFS.ERROR_READ, e
					.getMessage(), e));
		}
		finally
		{
			monitor.done();
		}
	}
}
