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
