/**
 * This file Copyright (c) 2005-2010 Aptana, Inc. This program is
 * dual-licensed under both the Aptana Public License and the GNU General
 * Public license. You may elect to use one or the other of these licenses.
 * 
 * This program is distributed in the hope that it will be useful, but
 * AS-IS and WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE, TITLE, or
 * NONINFRINGEMENT. Redistribution, except as permitted by whichever of
 * the GPL or APL you select, is prohibited.
 *
 * 1. For the GPL license (GPL), you can redistribute and/or modify this
 * program under the terms of the GNU General Public License,
 * Version 3, as published by the Free Software Foundation.  You should
 * have received a copy of the GNU General Public License, Version 3 along
 * with this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 * 
 * Aptana provides a special exception to allow redistribution of this file
 * with certain other free and open source software ("FOSS") code and certain additional terms
 * pursuant to Section 7 of the GPL. You may view the exception and these
 * terms on the web at http://www.aptana.com/legal/gpl/.
 * 
 * 2. For the Aptana Public License (APL), this program and the
 * accompanying materials are made available under the terms of the APL
 * v1.0 which accompanies this distribution, and is available at
 * http://www.aptana.com/legal/apl/.
 * 
 * You may view the GPL, Aptana's exception and additional terms, and the
 * APL in the file titled license.html at the root of the corresponding
 * plugin containing this source file.
 * 
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
	{
		try
		{
			IFileStore localFileStore = toLocalFileStore(fileStore, monitor);
			IFileInfo remoteFileInfo = fileStore.fetchInfo(EFS.NONE, monitor);
			return new UniformFileStoreEditorInput(localFileStore, fileStore, remoteFileInfo);
		}
		catch (CoreException e)
		{
		}
		return null;
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
	private static IFileStore toLocalFileStore(IFileStore fileStore, IProgressMonitor monitor) throws CoreException
	{
		File file = fileStore.toLocalFile(EFS.NONE, monitor);
		if (file != null)
		{
			// the file is already local
			return fileStore;
		}
		try
		{
			String prefix = fileStore.getFileSystem().getScheme();
			while (prefix.length() < 3)
			{
				prefix += "_"; //$NON-NLS-1$
			}
			file = File.createTempFile(prefix, fileStore.getName());
		}
		catch (IOException e)
		{
			return fileStore;
		}
		IFileStore localFileStore = EFS.getLocalFileSystem().fromLocalFile(file);
		fileStore.copy(localFileStore, EFS.OVERWRITE, monitor);
		file.deleteOnExit();

		return localFileStore;
	}
}
