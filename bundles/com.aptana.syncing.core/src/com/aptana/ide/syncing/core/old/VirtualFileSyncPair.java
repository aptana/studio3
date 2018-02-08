/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
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

import com.aptana.core.io.vfs.IExtendedFileStore;
import com.aptana.core.logging.IdeLog;
import com.aptana.ide.syncing.core.SyncingPlugin;

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
			IdeLog.logError(SyncingPlugin.getDefault(), Messages.VirtualFileSyncPair_SourceFileInfoError, e);
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
	public InputStream getSourceInputStream() throws CoreException
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
			IdeLog.logError(SyncingPlugin.getDefault(), Messages.VirtualFileSyncPair_DestFileInfoErrror, e);
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
	public InputStream getDestinationInputStream() throws CoreException
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
		if (getSourceFile() != null && getSourceFile().fetchInfo().isDirectory())
		{
			return true;
		}
		return getDestinationFile() != null && getDestinationFile().fetchInfo().isDirectory();
	}

	/**
	 * Returns a nicely formatted version of the file pair
	 */
	public String toString()
	{
		String text = ""; //$NON-NLS-1$
		text += (this._sourceFile != null) ? this._sourceFile.toString() : "null"; //$NON-NLS-1$
		text += " <-> "; //$NON-NLS-1$
		text += (this._destinationFile != null) ? this._destinationFile.toString() : "null"; //$NON-NLS-1$

		return text;
	}
}