/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.ide.filesystem.s3;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileInfo;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.filesystem.provider.FileInfo;
import org.eclipse.core.filesystem.provider.FileStore;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.SubMonitor;

import com.amazon.s3.AWSAuthConnection;
import com.amazon.s3.Bucket;
import com.amazon.s3.CallingFormat;
import com.amazon.s3.ListAllMyBucketsResponse;
import com.amazon.s3.ListBucketResponse;
import com.amazon.s3.ListEntry;
import com.amazon.s3.Response;
import com.aptana.core.util.FileUtil;
import com.aptana.core.util.IOUtil;
import com.aptana.ide.core.io.CoreIOPlugin;

class S3FileStore extends FileStore
{

	private static final String DATE_FORMAT = "EEE, d MMM yyyy HH:mm:ss z"; //$NON-NLS-1$
	private static final String LAST_MODIFIED = "Last-Modified"; //$NON-NLS-1$
	private static final String CONTENT_LENGTH = "Content-Length"; //$NON-NLS-1$
	private static final String SEPARATOR = "/"; //$NON-NLS-1$
	private static final String FOLDER_SUFFIX = "_$folder$"; //$NON-NLS-1$

	private URI uri;
	private Path path;
	private String accessKey;

	protected S3FileStore(URI uri)
	{
		this.uri = uri;
		this.path = new Path(uri.getPath().replaceAll("%2F", SEPARATOR)); //$NON-NLS-1$
	}

	@Override
	public String[] childNames(int options, IProgressMonitor monitor) throws CoreException // NO_UCD
	{
		return childNames(options, false, monitor);
	}

	private String[] childNames(int options, boolean includeHackFolderFiles, IProgressMonitor monitor)
			throws CoreException
	{
		try
		{
			if (isRoot())
			{
				return getBuckets();
			}
			// Inside a bucket
			String prefix = getPrefix();
			List<ListEntry> entries = listEntries();
			List<String> keys = new ArrayList<String>();
			if (entries == null)
				return keys.toArray(new String[0]);
			for (ListEntry entry : entries)
			{
				if (prefix.length() >= entry.key.length())
					continue;
				try
				{
					String relative = entry.key.substring(prefix.length());
					if (prefix.length() == 0 || relative.startsWith(SEPARATOR))
					{ // actual children
						if (prefix.length() > 0)
							relative = relative.substring(1);
						// only add direct children (so take up to next path separator)
						int index = relative.indexOf(SEPARATOR);
						if (index != -1)
						{
							relative = relative.substring(0, index);
						}
						else if (relative.endsWith(FOLDER_SUFFIX))
							relative = relative.substring(0, relative.length() - FOLDER_SUFFIX.length());
					}
					else
					{
						// file at same level (peer, not child), check just for the _$folder$ hack
						if (relative.equals(FOLDER_SUFFIX))
						{
							if (!includeHackFolderFiles)
								continue;
						}
						else
							continue;
					}
					if (relative.length() == 0)
						continue;
					if (!keys.contains(relative))
						keys.add(relative);
				}
				catch (Exception e)
				{
					S3FileSystemPlugin.log(e);
				}
			}
			return keys.toArray(new String[keys.size()]);
		}
		catch (MalformedURLException e)
		{
			throw S3FileSystemPlugin.coreException(e);
		}
		catch (IOException e)
		{
			throw S3FileSystemPlugin.coreException(e);
		}
	}

	private @SuppressWarnings("unchecked")
	String[] getBuckets() throws MalformedURLException, IOException
	{
		// We're outside any buckets. List the buckets!
		List<String> keys = new ArrayList<String>();
		ListAllMyBucketsResponse resp = getAWSConnection().listAllMyBuckets(null);
		if (resp == null || resp.entries == null)
			return keys.toArray(new String[0]);
		List<Bucket> buckets = resp.entries;
		for (Bucket bucket : buckets)
		{
			keys.add(bucket.name);
		}
		return keys.toArray(new String[keys.size()]);
	}

	private boolean isRoot()
	{
		return getBucket() == null;
	}

	private String getPrefix()
	{
		String prefix = path.removeFirstSegments(1).toPortableString();
		if (prefix.startsWith(SEPARATOR))
		{
			prefix = prefix.substring(1);
		}
		return prefix;
	}

	@Override
	public IFileInfo fetchInfo(int options, IProgressMonitor monitor) throws CoreException
	{
		FileInfo info = new FileInfo(getName());
		if (path.isRoot())
		{
			info.setExists(true);
			info.setDirectory(true);
			info.setAttribute(EFS.ATTRIBUTE_OWNER_EXECUTE, true);
			info.setAttribute(EFS.ATTRIBUTE_GROUP_EXECUTE, true);
		}
		else if (isBucket())
		{
			// we're a bucket
			try
			{
				boolean exists = getAWSConnection().checkBucketExists(getBucket());
				info.setExists(exists);
				info.setDirectory(true);
				info.setAttribute(EFS.ATTRIBUTE_OWNER_EXECUTE, true);
			}
			catch (IOException e)
			{
				throw S3FileSystemPlugin.coreException(e);
			}
		}
		else
		{
			try
			{
				HttpURLConnection connection = getAWSConnection().head(getBucket(), getKey(), null);
				if (connection.getResponseCode() < 400)
				{
					info.setExists(true);
					info.setDirectory(false);
					String length = connection.getHeaderField(CONTENT_LENGTH);
					if (length != null)
						info.setLength(Long.parseLong(length));
					try
					{
						String lastModified = connection.getHeaderField(LAST_MODIFIED);
						if (lastModified != null)
						{
							Date date = new SimpleDateFormat(DATE_FORMAT).parse(lastModified);
							info.setLastModified(date.getTime());
						}
					}
					catch (ParseException e)
					{
						// ignore
					}
				}
				else
				{
					// Only "exists" if there's any children! There are no "directories" in S3. Make sure not to filter
					// out
					// the _$folder$ hacks for this
					String[] children = childNames(options, true, monitor);
					if (children != null && children.length > 0)
					{
						info.setDirectory(true);
						info.setExists(true);
						info.setLastModified(System.currentTimeMillis());
						info.setLength(EFS.NONE);
						info.setAttribute(EFS.ATTRIBUTE_OWNER_EXECUTE, true);
					}
				}
			}
			catch (MalformedURLException e)
			{
				throw S3FileSystemPlugin.coreException(e);
			}
			catch (IOException e)
			{
				throw S3FileSystemPlugin.coreException(e);
			}
		}
		return info;
	}

	private boolean isBucket()
	{
		return getKey() == null || getKey().length() == 0;
	}

	@Override
	public IFileStore getChild(String name)
	{
		try
		{
			IPath childPath = path.append(name);
			if (!childPath.isAbsolute())
				childPath = childPath.makeAbsolute();
			return new S3FileStore(getURI(childPath));
		}
		catch (URISyntaxException e)
		{
			S3FileSystemPlugin.log(e);
		}
		return null;
	}

	private URI getURI(IPath childPath) throws URISyntaxException
	{
		return new URI(uri.getScheme(), uri.getUserInfo(), uri.getHost(), uri.getPort(), childPath.toPortableString(),
				uri.getQuery(), uri.getFragment());
	}

	@Override
	public String getName()
	{
		return path.segmentCount() == 0 ? path.toPortableString() : path.lastSegment();
	}

	@Override
	public IFileStore getParent()
	{
		if (path.segmentCount() == 0)
			return null;
		try
		{
			IPath parentPath = path.removeLastSegments(1);
			return new S3FileStore(getURI(parentPath));
		}
		catch (URISyntaxException e)
		{
			S3FileSystemPlugin.log(e);
		}
		return null;
	}

	@Override
	public InputStream openInputStream(int options, IProgressMonitor monitor) throws CoreException
	{
		try
		{
			HttpURLConnection connection = getAWSConnection().getRaw(getBucket(), getKey(), null);
			int responseCode = connection.getResponseCode();
			// Throw a CoreException wrapping a FileNotFoundException when we're trying to read an S3Object that doesn't
			// exist
			if (responseCode == 404)
			{
				// tests expect message to be the filepath
				throw S3FileSystemPlugin.coreException(EFS.ERROR_NOT_EXISTS,
						new FileNotFoundException(path.toPortableString()));
			}
			if (responseCode < 400)
			{
				return connection.getInputStream();
			}
			throw S3FileSystemPlugin.coreException(EFS.ERROR_INTERNAL,
					new Exception(errorMessage(responseCode, connection)));
		}
		catch (MalformedURLException e)
		{
			throw S3FileSystemPlugin.coreException(e);
		}
		catch (IOException e)
		{
			throw S3FileSystemPlugin.coreException(e);
		}
	}

	AWSAuthConnection getAWSConnection()
	{
		boolean secure = true;
		if (getBucket() != null && getBucket().indexOf(".") != -1) //$NON-NLS-1$
		{
			secure = false; // Work around weird bug? Do we need subdomain calling format?
		}
		return new AWSAuthConnection(getAccessKey(), getSecretAccessKey(), secure, uri.getHost(),
				CallingFormat.getPathCallingFormat());
	}

	private char[] promptPassword(String title, String message)
	{
		char[] password = CoreIOPlugin.getAuthenticationManager().promptPassword(getAuthId(), getAccessKey(), title,
				message);
		if (password == null)
		{
			password = new char[0];
			throw new OperationCanceledException();
		}
		return password;
	}

	private char[] getOrPromptPassword(String title, String message)
	{
		char[] password = CoreIOPlugin.getAuthenticationManager().getPassword(getAuthId());
		if (password == null)
		{
			password = new char[0];
			promptPassword(title, message);
		}
		return password;
	}

	private String getAuthId()
	{
		return Policy.generateAuthId(S3ConnectionPoint.TYPE, getAccessKey(), uri.getHost());
	}

	private String getSecretAccessKey()
	{
		String userInfo = uri.getUserInfo();
		if (userInfo.contains(":")) //$NON-NLS-1$
		{
			return userInfo.split(":")[1]; //$NON-NLS-1$
		}
		return new String(getOrPromptPassword(
				MessageFormat.format(Messages.S3FileStore_Authentication, getAccessKey()),
				Messages.S3FileStore_EnterAccessKey));
	}

	private synchronized String getAccessKey()
	{
		if (accessKey == null)
		{
			String userInfo = uri.getUserInfo();
			if (userInfo.contains(":")) //$NON-NLS-1$
			{
				accessKey = userInfo.split(":")[0]; //$NON-NLS-1$
			}
			else
			{
				accessKey = userInfo;
			}
		}
		return accessKey;
	}

	String getKey()
	{
		String key = path.removeFirstSegments(1).toPortableString();
		if (key.startsWith(SEPARATOR) && key.length() > 1)
			return key.substring(1);
		return key;
	}

	private String getBucket()
	{
		if (path.segmentCount() == 0)
			return null;
		return path.segment(0);
	}

	@Override
	public URI toURI()
	{
		return uri;
	}

	@Override
	public void delete(int options, IProgressMonitor monitor) throws CoreException
	{
		try
		{
			// TODO There's got to be a faster way to delete the subdirectory structure using listEntries and
			// filtering
			// down to just children (not peers starting with same prefix)
			// Delete depth first
			IFileStore[] children = childStores(options, monitor);
			for (IFileStore child : children)
			{
				child.delete(options, monitor);
			}

			int responseCode = 0;
			if (isBucket())
			{
				// Deleting a bucket!
				Response resp = getAWSConnection().deleteBucket(getBucket(), null);
				responseCode = resp.connection.getResponseCode(); // force connection to finish
			}
			else
			{

				String key = getKey();
				Response resp = getAWSConnection().delete(getBucket(), key, null);
				responseCode = resp.connection.getResponseCode(); // force connection to finish

				// Handle if we're faking a folder. try to delete the fake folder suffix file.
				resp = getAWSConnection().delete(getBucket(), key + FOLDER_SUFFIX, null);
				resp.connection.getResponseCode(); // force connection to finish
			}
			if (responseCode < 400)
			{
				return;
			}
			throw S3FileSystemPlugin.coreException(EFS.ERROR_DELETE, new Exception(path.toPortableString()));
		}
		catch (MalformedURLException e)
		{
			throw S3FileSystemPlugin.coreException(e);
		}
		catch (IOException e)
		{
			throw S3FileSystemPlugin.coreException(e);
		}
	}

	@Override
	public OutputStream openOutputStream(int options, IProgressMonitor monitor) throws CoreException
	{
		try
		{
			// If we know this is a bucket, just fail right away because you can't write to the bucket itself!
			if (isBucket())
			{
				throw S3FileSystemPlugin.coreException(EFS.ERROR_READ_ONLY, new Exception("Can't write to a bucket!")); //$NON-NLS-1$
			}
			// if "parent" doesn't exist, need to fail
			IFileStore parent = getParent();
			IFileInfo info = parent.fetchInfo();
			if (!info.exists())
			{
				throw S3FileSystemPlugin.coreException(EFS.ERROR_WRITE,
						new FileNotFoundException(path.toPortableString()));
			}
			HttpURLConnection connection = getAWSConnection().putRaw(getBucket(), getKey(), null);
			return new HttpForcingOutputStream(connection.getOutputStream(), connection);
		}
		catch (MalformedURLException e)
		{
			throw S3FileSystemPlugin.coreException(e);
		}
		catch (IOException e)
		{
			throw S3FileSystemPlugin.coreException(e);
		}
	}

	@Override
	public IFileStore mkdir(int options, IProgressMonitor monitor) throws CoreException
	{
		try
		{
			HttpURLConnection connection = null;
			if (isBucket())
			{
				// Empty key means we're actually creating a bucket!
				Response resp = getAWSConnection().createBucket(getBucket(), null, null);
				connection = resp.connection;
			}
			else
			{
				// If the options are SHALLOW, we must not create the object unless the "parents" exist!
				if ((options & EFS.SHALLOW) != 0)
				{
					IFileStore parent = getParent();
					IFileInfo info = parent.fetchInfo();
					// Tests expect that we return FileNotFound for current path when parent doesn't exist or is not a
					// directory!
					if (!info.exists())
					{
						throw S3FileSystemPlugin.coreException(EFS.ERROR_INTERNAL, Messages.S3FileStore_ParentNotExist,
								new FileNotFoundException(path.toPortableString()));
					}
					if (!info.isDirectory())
					{
						throw S3FileSystemPlugin.coreException(EFS.ERROR_INTERNAL,
								Messages.S3FileStore_ParentNotADirectory,
								new FileNotFoundException(path.toPortableString()));
					}
				}
				connection = getAWSConnection().putRaw(getBucket(), getKey() + FOLDER_SUFFIX, null);
				connection.getOutputStream().write(new byte[] {});
			}
			int responseCode = connection.getResponseCode();
			if (responseCode >= 400)
			{
				throw S3FileSystemPlugin.coreException(EFS.ERROR_INTERNAL,
						new Exception(errorMessage(responseCode, connection)));
			}
		}
		catch (MalformedURLException e)
		{
			throw S3FileSystemPlugin.coreException(e);
		}
		catch (IOException e)
		{
			throw S3FileSystemPlugin.coreException(e);
		}
		return this;
	}

	private String errorMessage(int responseCode, HttpURLConnection connection) throws CoreException
	{
		String msg = ""; //$NON-NLS-1$
		try
		{
			msg = IOUtil.read(connection.getErrorStream());
			int index = msg.indexOf("<Message>"); //$NON-NLS-1$
			if (index != -1)
			{
				msg = msg.substring(index + 9);
			}
			index = msg.indexOf("</Message>"); //$NON-NLS-1$
			if (index != -1)
			{
				msg = msg.substring(0, index);
			}
		}
		catch (Exception e)
		{
			// ignore
		}
		return MessageFormat.format("({0}) {1}", responseCode, msg); //$NON-NLS-1$
	}

	@Override
	public void putInfo(IFileInfo info, int options, IProgressMonitor monitor) throws CoreException
	{
		if ((options & EFS.SET_LAST_MODIFIED) != 0)
		{
			// TODO Is there any way to set this on S3 objects?
		}
		if ((options & EFS.SET_ATTRIBUTES) != 0)
		{
			// TODO Set ACL permissions on file?
		}
	}

	@Override
	public File toLocalFile(int options, IProgressMonitor monitor) throws CoreException
	{
		SubMonitor sub = SubMonitor.convert(monitor, 100);
		if (options == EFS.CACHE)
		{
			try
			{
				IFileInfo myInfo = fetchInfo(EFS.NONE, sub.newChild(25));
				File result;
				if (!myInfo.exists())
					result = File.createTempFile("Non-Existent-", Long.toString(System.currentTimeMillis())); //$NON-NLS-1$
				else
				{
					if (myInfo.isDirectory())
					{
						File tmpDir = FileUtil.getTempDirectory().toFile();
						result = getUniqueDirectory(tmpDir);
					}
					else
					{
						result = File.createTempFile("s3file", "efs"); //$NON-NLS-1$ //$NON-NLS-2$
					}
					sub.worked(25);
					IFileStore resultStore = EFS.getLocalFileSystem().fromLocalFile(result);
					copy(resultStore, EFS.OVERWRITE, sub.newChild(25));
				}
				result.deleteOnExit();
				return result;
			}
			catch (IOException e)
			{
				throw S3FileSystemPlugin.coreException(EFS.ERROR_WRITE, e);
			}
		}
		return super.toLocalFile(options, monitor);
	}

	private File getUniqueDirectory(File parent)
	{
		File dir;
		long i = 0;
		// find an unused directory name
		do
		{
			dir = new File(parent, Long.toString(System.currentTimeMillis() + i++));
		}
		while (dir.exists());
		return dir;
	}

	@Override
	protected void copyFile(IFileInfo sourceInfo, IFileStore destination, int options, IProgressMonitor monitor)
			throws CoreException
	{
		// if we're copying from S3 to S3 we can do so using S3's special copy API shortcut!
		if (destination instanceof S3FileStore)
		{
			S3FileStore s3Dest = (S3FileStore) destination;

			if ((options & EFS.OVERWRITE) == 0)
			{
				IFileInfo destInfo = destination.fetchInfo();
				if (destInfo.exists())
				{
					throw S3FileSystemPlugin.coreException(EFS.ERROR_INTERNAL, Messages.S3FileStore_DestinationExists,
							new FileNotFoundException(s3Dest.path.toPortableString()));
				}
			}

			// We must not create the object unless the "parent" exists!
			IFileStore parent = destination.getParent();
			IFileInfo info = parent.fetchInfo();
			// Tests expect that we return FileNotFound for path when parent doesn't exist or is not a
			// directory!
			if (!info.exists())
			{
				throw S3FileSystemPlugin.coreException(EFS.ERROR_INTERNAL, Messages.S3FileStore_ParentNotExist,
						new FileNotFoundException(s3Dest.path.toPortableString()));
			}
			if (!info.isDirectory())
			{
				throw S3FileSystemPlugin.coreException(EFS.ERROR_INTERNAL, Messages.S3FileStore_ParentNotADirectory,
						new FileNotFoundException(s3Dest.path.toPortableString()));
			}

			try
			{
				getAWSConnection().copy(getBucket(), getKey(), s3Dest.getBucket(), s3Dest.getKey(), null);
			}
			catch (MalformedURLException e)
			{
				throw S3FileSystemPlugin.coreException(EFS.ERROR_INTERNAL, e);
			}
			catch (IOException e)
			{
				throw S3FileSystemPlugin.coreException(EFS.ERROR_INTERNAL, e);
			}
		}
		else
		{
			super.copyFile(sourceInfo, destination, options, monitor);
		}
	}

	@SuppressWarnings("unchecked")
	List<ListEntry> listEntries() throws MalformedURLException, IOException
	{
		String prefix = getPrefix();
		if (prefix != null && prefix.trim().length() == 0)
			prefix = null;
		// FIXME If the list is truncated we need to grab the last entry as a marker and continually iterate and combine
		// responses!
		ListBucketResponse resp = getAWSConnection().listBucket(getBucket(), prefix, null, null, null);
		return resp.entries;
	}

	@Override
	public void move(IFileStore destination, int options, IProgressMonitor monitor) throws CoreException
	{
		SubMonitor sub = SubMonitor.convert(monitor, 100);
		try
		{
			copy(destination, options, sub.newChild(70));
			delete(EFS.NONE, sub.newChild(30));
		}
		finally
		{
			sub.done();
		}
	}

	@Override
	protected void copyDirectory(IFileInfo sourceInfo, IFileStore destination, int options, IProgressMonitor monitor)
			throws CoreException
	{
		if ((options & EFS.OVERWRITE) == 0)
		{
			IFileInfo destInfo = destination.fetchInfo();
			if (destInfo.exists())
			{
				throw S3FileSystemPlugin.coreException(EFS.ERROR_EXISTS, new FileNotFoundException(destination.toURI()
						.getPath()));
			}
		}
		IFileStore destParent = destination.getParent();
		IFileInfo fi = destParent.fetchInfo();
		if (!fi.exists())
		{
			throw S3FileSystemPlugin.coreException(EFS.ERROR_WRITE, new FileNotFoundException(destination.toURI()
					.getPath()));
		}
		super.copyDirectory(sourceInfo, destination, options, monitor);
	}

	private static class HttpForcingOutputStream extends OutputStream
	{

		private OutputStream out;
		private HttpURLConnection connection;

		HttpForcingOutputStream(OutputStream out, HttpURLConnection connection)
		{
			this.out = out;
			this.connection = connection;
		}

		@Override
		public void write(int b) throws IOException
		{
			out.write(b);
		}

		@Override
		public void flush() throws IOException
		{
			out.flush();
		}

		@Override
		public void close() throws IOException
		{
			out.close();
			connection.getResponseCode(); // force the connection to finish!
		}

		@Override
		public void write(byte[] b, int off, int len) throws IOException
		{
			out.write(b, off, len);
		}

		@Override
		public void write(byte[] b) throws IOException
		{
			out.write(b);
		}
	}
}
