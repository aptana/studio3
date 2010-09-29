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
package com.aptana.ide.syncing.core.old;

import java.io.IOException;
import java.io.InputStream;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileInfo;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

import com.aptana.ide.core.io.vfs.IExtendedFileStore;

/**
 * @author Kevin Lindsey
 */
public class VirtualFileSyncPair
{
	public static final int Direction_None = 0;
	public static final int Direction_ClientToServer = 1;
	public static final int Direction_ServerToClient = 2;

	private String _relativePath;
	private IFileStore _sourceFile;
	private IFileInfo _sourceFileInfo = null;
	private IFileStore _destinationFile;
	private IFileInfo _destinationFileInfo = null;
	private int _syncState;
	private int _syncDirection = Direction_None;

	/**
	 * SyncItem
	 * 
	 * @param sourceFile
	 * @param destinationFile
	 * @param relativePath
	 * @param syncState
	 */
	public VirtualFileSyncPair(IFileStore sourceFile, IFileStore destinationFile, String relativePath, int syncState)
	{
		this._sourceFile = sourceFile;
		this._destinationFile = destinationFile;
		this._relativePath = relativePath;
		this._syncState = syncState;
	}

	/**
	 * getClientFile
	 * 
	 * @return IVirtualFile
	 */
	public IFileStore getSourceFile()
	{
		return this._sourceFile;
	}

	/**
	 * getSourceFileInfo
	 * 
	 * @return
	 */
	public IFileInfo getSourceFileInfo()
	{
		try
		{
			return getSourceFileInfo(null);
		}
		catch (CoreException e)
		{
			return null;
		}
	}

	/**
	 * getSourceFileInfo
	 * 
	 * @return IVirtualFile
	 * @throws CoreException
	 */
	public IFileInfo getSourceFileInfo(IProgressMonitor monitor) throws CoreException
	{
		if (this._sourceFile == null)
		{
			return null;
		}
		if (this._sourceFileInfo == null)
		{
			this._sourceFileInfo = _sourceFile.fetchInfo(IExtendedFileStore.DETAILED, monitor);
		}
		return this._sourceFileInfo;
	}

	/**
	 * getClientInputStream
	 * 
	 * @return InputStream
	 * @throws ConnectionException
	 * @throws VirtualFileManagerException
	 * @throws IOException
	 * @throws CoreException
	 */
	public InputStream getSourceInputStream() throws IOException, CoreException
	{
		InputStream result = null;

		if (this._sourceFile != null && !this._sourceFile.fetchInfo().isDirectory())
		{
			result = this._sourceFile.openInputStream(EFS.NONE, null);
		}

		return result;
	}

	/**
	 * setClientFile
	 * 
	 * @param sourceFile
	 */
	public void setSourceFile(IFileStore sourceFile)
	{
		this._sourceFile = sourceFile;
		this._sourceFileInfo = null;
	}

	/**
	 * getServerFile
	 * 
	 * @return IVirtualFile
	 */
	public IFileStore getDestinationFile()
	{
		return this._destinationFile;
	}

	/**
	 * getDestinationFileInfo
	 * 
	 * @return
	 */
	public IFileInfo getDestinationFileInfo()
	{
		try
		{
			return getDestinationFileInfo(null);
		}
		catch (CoreException e)
		{
			return null;
		}
	}

	/**
	 * getDestinationFileInfo
	 * 
	 * @return IVirtualFile
	 * @throws CoreException
	 */
	public IFileInfo getDestinationFileInfo(IProgressMonitor monitor) throws CoreException
	{
		if (this._destinationFile == null)
		{
			return null;
		}
		if (this._destinationFileInfo == null)
		{
			this._destinationFileInfo = _destinationFile.fetchInfo(IExtendedFileStore.DETAILED, monitor);
		}
		return this._destinationFileInfo;
	}

	/**
	 * getServerInputStream
	 * 
	 * @return InputStream
	 * @throws ConnectionException
	 * @throws VirtualFileManagerException
	 * @throws IOException
	 * @throws CoreException
	 */
	public InputStream getDestinationInputStream() throws IOException, CoreException
	{
		InputStream result = null;

		if (this._destinationFile != null && !this._destinationFile.fetchInfo().isDirectory())
		{
			result = this._destinationFile.openInputStream(EFS.NONE, null);
		}

		return result;
	}

	/**
	 * setServerFile
	 * 
	 * @param destinationFile
	 */
	public void setDestinationFile(IFileStore destinationFile)
	{
		this._destinationFile = destinationFile;
		this._destinationFileInfo = null;
	}

	/**
	 * getRelativePath
	 * 
	 * @return String
	 */
	public String getRelativePath()
	{
		return this._relativePath;
	}

	/**
	 * getSyncState
	 * 
	 * @return int
	 */
	public int getSyncState()
	{
		return this._syncState;
	}

	/**
	 * setSyncState
	 * 
	 * @param syncState
	 */
	public void setSyncState(int syncState)
	{
		this._syncState = syncState;
	}

	/**
	 * getSyncDirection
	 * 
	 * @return int
	 */
	public int getSyncDirection()
	{
		return _syncDirection;
	}

	/**
	 * @param direction
	 */
	public void setSyncDirection(int direction)
	{
		this._syncDirection = direction;
	}

	/**
	 * Am I a folder?
	 * 
	 * @return boolean
	 */
	public boolean isDirectory()
	{
		if (getSyncState() == SyncState.IncompatibleFileTypes)
		{
			return false;
		}
		else
		{
			if (getSourceFile() != null && getSourceFile().fetchInfo().isDirectory())
			{
				return true;
			}
			else
			{
				return (getDestinationFile() != null && getDestinationFile().fetchInfo().isDirectory());
			}
		}
	}
}