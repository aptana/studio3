/**
 * Aptana Studio
 * Copyright (c) 2014 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.ide.core.io.downloader;

import java.io.File;
import java.net.URI;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.filesystem.IFileSystem;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Status;

/**
 * Special subclass intended to shortcut when the URI we're pointing at uses a file: URI. We copy the contents to the
 * intended saveLocation. We could just return the original path, but much of our code assumes that we can unzip and
 * delete the "downloaded" file. If the user pointed at an existing file we don't want to do that, so for safety we just
 * do a quick copy to the temp dir.
 * 
 * @author Chris Williams <cwilliams@appcelerator.com>
 */
class FileDownloadRequest extends ContentDownloadRequest
{

	public FileDownloadRequest(URI uri) throws CoreException
	{
		super(uri);
	}

	public FileDownloadRequest(URI uri, File saveTo) throws CoreException
	{
		super(uri, saveTo);
	}

	@Override
	public void execute(IProgressMonitor monitor)
	{
		// We copy from the original URI to the new location (typically temp dir)
		// TODO Use IOUtil.copyFile(source, destination) ? It may be faster

		IFileSystem fs = EFS.getLocalFileSystem();
		IFileStore src = fs.getStore(uri);
		IFileStore destination = fs.getStore(getDownloadLocation());
		try
		{
			src.copy(destination, EFS.OVERWRITE, monitor);
			setResult(Status.OK_STATUS);
		}
		catch (CoreException e)
		{
			setResult(e.getStatus());
		}
	}
}
