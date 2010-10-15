package com.aptana.ide.filesystem.s3;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;

import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.filesystem.IFileTree;
import org.eclipse.core.filesystem.provider.FileSystem;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

public class S3FileSystem extends FileSystem
{

	@Override
	public IFileStore getStore(URI uri)
	{
		return new S3FileStore(uri);
	}

	@Override
	public boolean canDelete()
	{
		return true;
	}

	@Override
	public boolean canWrite()
	{
		return true;
	}

	@Override
	public IFileTree fetchFileTree(IFileStore root, IProgressMonitor monitor) throws CoreException
	{
		if (!(root instanceof S3FileStore))
			return null;
		try
		{
			S3FileStore s3Store = (S3FileStore) root;
			if (monitor != null && monitor.isCanceled())
				return null;
			// FIXME What about when s3Store is the absolute root (not in a bucket)?!
			return new S3FileTree(root, s3Store.listEntries());
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
}
