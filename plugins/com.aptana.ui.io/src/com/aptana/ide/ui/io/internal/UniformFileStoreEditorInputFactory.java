/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.ide.ui.io.internal;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileInfo;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IElementFactory;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.ide.FileStoreEditorInput;

import com.aptana.core.io.efs.SyncUtils;
import com.aptana.core.util.FileUtil;

public class UniformFileStoreEditorInputFactory implements IElementFactory
{

	static final String ID = "com.aptana.ui.io.UniformFileStoreEditorInputFactory"; //$NON-NLS-1$
	private static final String URI = "uri"; //$NON-NLS-1$

	public IAdaptable createElement(IMemento memento)
	{
		String uriString = memento.getString(URI);
		if (uriString == null)
		{
			return null;
		}
		URI uri;
		try
		{
			uri = new URI(uriString);
		}
		catch (URISyntaxException e)
		{
			return null;
		}

		try
		{
			return getUniformEditorInput(EFS.getStore(uri), new NullProgressMonitor());
		}
		catch (CoreException e)
		{
		}
		return null;
	}

	public static IEditorInput getUniformEditorInput(IFileStore fileStore, IProgressMonitor monitor)
			throws CoreException
	{
		if (fileStore.getFileSystem() == EFS.getLocalFileSystem())
		{ // $codepro.audit.disable useEquals
			return new FileStoreEditorInput(fileStore);
		}
		IFileInfo remoteFileInfo = fileStore.fetchInfo(EFS.NONE, monitor);
		IFileStore localFileStore = toLocalFileStore(fileStore, remoteFileInfo, monitor);
		return new UniformFileStoreEditorInput(localFileStore, fileStore, remoteFileInfo);
	}

	static void saveState(IMemento memento, UniformFileStoreEditorInput input)
	{
		// stores the remote URI
		memento.putString(URI, input.getFileStore().toURI().toString());
	}

	/**
	 * Returns a file in the local file system with the same state as the remote file.
	 * 
	 * @param fileStore
	 *            the remote file store
	 * @param monitor
	 *            the progress monitor (could be null)
	 * @return File the local file store
	 */
	private static IFileStore toLocalFileStore(IFileStore fileStore, IFileInfo fileInfo, IProgressMonitor monitor)
			throws CoreException
	{
		File file = fileStore.toLocalFile(EFS.NONE, monitor);
		if (file != null)
		{
			// the file is already local
			return fileStore;
		}
		try
		{
			StringBuilder prefix = new StringBuilder(fileStore.getFileSystem().getScheme());
			while (prefix.length() < 3)
			{
				prefix.append('_');
			}
			String prefixStr = prefix.toString();
			File destDir = new File(FileUtil.getTempDirectory().toOSString(), prefixStr);
			destDir.mkdirs();
			file = File.createTempFile(prefixStr, fileStore.getName(), destDir);
		}
		catch (IOException e)
		{
			return fileStore;
		}
		IFileStore localFileStore = EFS.getLocalFileSystem().fromLocalFile(file);
		SyncUtils.copy(fileStore, fileInfo, localFileStore, EFS.NONE, monitor);
		file.deleteOnExit();

		return localFileStore;
	}
}
