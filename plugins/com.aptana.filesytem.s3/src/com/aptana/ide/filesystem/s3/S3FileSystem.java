/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.ide.filesystem.s3;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;

import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.filesystem.IFileTree;
import org.eclipse.core.filesystem.provider.FileSystem;
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
	public IFileTree fetchFileTree(IFileStore root, IProgressMonitor monitor)
	{
		if (!(root instanceof S3FileStore))
		{
			return null;
		}
		try
		{
			S3FileStore s3Store = (S3FileStore) root;
			if (monitor != null && monitor.isCanceled())
			{
				return null;
			}
			// FIXME What about when s3Store is the absolute root (not in a bucket)?!
			return new S3FileTree(root, s3Store.listEntries());
		}
		catch (MalformedURLException e)
		{
			S3FileSystemPlugin.log(e);
		}
		catch (IOException e)
		{
			S3FileSystemPlugin.log(e);
		}
		return null;
	}
}
