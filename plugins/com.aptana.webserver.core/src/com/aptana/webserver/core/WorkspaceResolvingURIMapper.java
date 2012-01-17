/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.webserver.core;

import java.io.File;
import java.net.URI;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;

import com.aptana.core.IURIMapper;
import com.aptana.core.io.efs.EFSUtils;
import com.aptana.core.logging.IdeLog;

/**
 * @author Max Stepanov
 */
public class WorkspaceResolvingURIMapper implements IURIMapper
{

	private IURIMapper baseMapper;

	/**
	 * 
	 */
	public WorkspaceResolvingURIMapper(IURIMapper baseMapper)
	{
		this.baseMapper = baseMapper;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.webserver.core.IURLMapper#resolve(org.eclipse.core.filesystem.IFileStore)
	 */
	public URI resolve(IFileStore fileStore)
	{
		URI uri = baseMapper.resolve(fileStore);
		if (uri == null)
		{
			try
			{
				File file = fileStore.toLocalFile(EFS.NONE, new NullProgressMonitor());
				if (file != null)
				{
					uri = baseMapper.resolve(EFSUtils.getLocalFileStore(file));
				}
			}
			catch (CoreException e)
			{
				IdeLog.logError(WebServerCorePlugin.getDefault(), e);
			}
		}
		return uri;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.webserver.core.IURLMapper#resolve(java.net.URL)
	 */
	public IFileStore resolve(URI uri)
	{
		IFileStore fileStore = baseMapper.resolve(uri);
		if (fileStore != null && fileStore.getFileSystem() == EFS.getLocalFileSystem()) // $codepro.audit.disable
																						// useEquals
		{
			try
			{
				fileStore = EFSUtils.fromLocalFile(fileStore.toLocalFile(EFS.NONE, new NullProgressMonitor()));
			}
			catch (CoreException e)
			{
				IdeLog.logError(WebServerCorePlugin.getDefault(), e);
			}
		}
		return fileStore;
	}
}
