/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.ide.syncing.core.old;

import org.eclipse.core.runtime.CoreException;

import com.aptana.core.util.StringUtil;
import com.aptana.ide.core.io.IConnectionPoint;

/**
 * @author Kevin Lindsey
 */
public class ConnectionPointSyncPair
{
	/**
	 * Upload the items
	 */
	public static final int Upload = 0;

	/**
	 * Download the items
	 */
	public static final int Download = 1;

	/**
	 * Upload and Download
	 */
	public static final int Both = 2;

	/*
	 * Fields
	 */
	private String _nickName = StringUtil.EMPTY;
	private IConnectionPoint _sourceFileManager = null;
	private IConnectionPoint _destinationFileManager = null;
	private int _syncOption = 0;
	private boolean _useCRC = false;
	private boolean _deleteRemoteFiles = false;

	/*
	 * Constructors
	 */

	/**
	 * SyncItem
	 */
	public ConnectionPointSyncPair()
	{
	}

	/**
	 * SyncItem
	 */
	public ConnectionPointSyncPair(IConnectionPoint source, IConnectionPoint dest)
	{
		_sourceFileManager = source;
		_destinationFileManager = dest;
	}

	/*
	 * Methods
	 */

	/**
	 * getClientFile
	 * 
	 * @return IVirtualFileManager
	 */
	public IConnectionPoint getSourceFileManager()
	{
		return this._sourceFileManager;
	}

	/**
	 * setClientFileManager
	 * 
	 * @param sourceFileManager
	 */
	public void setSourceFileManager(IConnectionPoint sourceFileManager)
	{
		this._sourceFileManager = sourceFileManager;
	}

	/**
	 * getServerFileManager
	 * 
	 * @return IConnectionPoint
	 */
	public IConnectionPoint getDestinationFileManager()
	{
		return this._destinationFileManager;
	}

	/**
	 * setServerFileManager
	 * 
	 * @param destinationFileManager
	 */
	public void setDestinationFileManager(IConnectionPoint destinationFileManager)
	{
		this._destinationFileManager = destinationFileManager;
	}

	/**
	 * getNickName
	 * 
	 * @return String
	 */
	public String getNickName()
	{
		return this._nickName;
	}

	/**
	 * setNickName
	 * 
	 * @param nickName
	 */
	public void setNickName(String nickName)
	{
		this._nickName = nickName;
	}

	/**
	 * getSyncOption
	 * 
	 * @return int
	 */
	public int getSyncState()
	{
		return this._syncOption;
	}

	/**
	 * setSyncOption
	 * 
	 * @param syncOption
	 */
	public void setSyncState(int syncOption)
	{
		this._syncOption = syncOption;
	}

	/**
	 * @return Returns the _deleteRemoteFiles.
	 */
	public boolean isDeleteRemoteFiles()
	{
		return _deleteRemoteFiles;
	}

	/**
	 * @param remoteFiles
	 *            The deleteRemoteFiles to set.
	 */
	public void setDeleteRemoteFiles(boolean remoteFiles)
	{
		_deleteRemoteFiles = remoteFiles;
	}

	/**
	 * @return Returns the useCRC.
	 */
	public boolean isUseCRC()
	{
		return _useCRC;
	}

	/**
	 * @return Returns true if this connection is valid
	 */
	public boolean isValid()
	{
		try
		{
			return getDestinationFileManager() != null && getDestinationFileManager().getRoot() != null
					&& getSourceFileManager() != null && getSourceFileManager().getRoot() != null;
		}
		catch (CoreException e)
		{
			return false;
		}
	}

	/**
	 * @param usecrc
	 *            The useCRC to set.
	 */
	public void setUseCRC(boolean usecrc)
	{
		_useCRC = usecrc;
	}

}
