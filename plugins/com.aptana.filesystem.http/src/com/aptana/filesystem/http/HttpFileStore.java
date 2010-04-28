package com.aptana.filesystem.http;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileInfo;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.filesystem.provider.FileInfo;
import org.eclipse.core.filesystem.provider.FileStore;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.SubMonitor;

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
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
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
			URI parentURI = new URI(uri.getScheme(), uri.getUserInfo(), uri.getHost(), uri.getPort(), aPath
					.toPortableString(), uri.getQuery(), uri.getFragment());
			return new HttpFileStore(parentURI);
		}
		catch (URISyntaxException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
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
		SubMonitor sub = SubMonitor.convert(monitor);

		// Try to grab copied cache file based on URL!
		File tmpDir = new File(System.getProperty("java.io.tmpdir")); //$NON-NLS-1$
		File cached = new File(tmpDir, getPath());
		if (cached.exists())
		{
			IFileStore localFile = EFS.getLocalFileSystem().fromLocalFile(cached);
			long lastModified = localFile.fetchInfo(EFS.NONE, sub.newChild(5)).getLastModified();
			long remoteLastModified = fetchInfo(EFS.NONE, sub.newChild(20)).getLastModified();
			// TODO What if one or both return EFS.NONE?
			if (lastModified >= remoteLastModified)
			{
				return cached;
			}
		}
		sub.worked(25);
		// make directory structure for our copy
		cached.getParentFile().mkdirs();

		// Download to some filename we can associate and pull back up based on URL! (used above)
		IFileStore resultStore = EFS.getLocalFileSystem().fromLocalFile(cached);
		copy(resultStore, EFS.OVERWRITE, sub.newChild(75));
		cached.deleteOnExit();
		return cached;
	}

	private String getPath()
	{
		StringBuilder builder = new StringBuilder();
		builder.append(uri.getScheme());
		builder.append(File.separator);
		builder.append(uri.getHost());
		builder.append(File.separator);
		int port = uri.getPort();
		if (port != -1 && port != 80) // TODO check for default port for scheme (this assumes always http, not https)!
		{
			builder.append(uri.getPort());
			builder.append(File.separator);
		}
		builder.append(new Path(uri.getPath()).toOSString());
		return builder.toString();
	}

	@Override
	public InputStream openInputStream(int options, IProgressMonitor monitor) throws CoreException
	{
		try
		{
			return uri.toURL().openStream();
		}
		catch (MalformedURLException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}
