/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.ide.syncing.core.old;

import org.eclipse.core.runtime.IPath;

/**
 * @author Kevin Sawicki (ksawicki@aptana.com)
 */
public interface ISyncResource
{

	/**
	 * SYNCED transfer state
	 */
	static final int SYNCED = 0;

	/**
	 * ERROR transfer state
	 */
	static final int ERROR = 1;

	/**
	 * SYNCING transfer state
	 */
	static final int SYNCING = 2;

	/**
	 * Gets the transfer state
	 * 
	 * @return - int state
	 */
	int getTransferState();

	/**
	 * Sets the transfer state
	 * 
	 * @param state
	 */
	void setTransferState(int state);

	/**
	 * Gets the sync state
	 * 
	 * @return - SyncState
	 */
	int getSyncState();

	/**
	 * True if skipped
	 * 
	 * @return - true if skipped
	 */
	boolean isSkipped();

	/**
	 * Sets the resource as skipped
	 * 
	 * @param skipped
	 */
	void setSkipped(boolean skipped);

	/**
	 * Gets the parent of this resource
	 * 
	 * @return - parent
	 */
	SyncFolder getParent();

	/**
	 * Gets the name of this resource
	 * 
	 * @return - name
	 */
	String getName();

	/**
	 * Gets the path of this resource
	 * 
	 * @return - path
	 */
	IPath getPath();

	/**
	 * Gets the sync pair object for this resource
	 * 
	 * @return - sync pair
	 */
	VirtualFileSyncPair getPair();

	/**
	 * Gets the number of bytes transferred for this resource
	 * 
	 * @return - the number of bytes transferred
	 */
	long getTransferredBytes();

	/**
	 * Sets the number of bytes transferred for this resource
	 * 
	 * @param bytes
	 *            the number of bytes transferred
	 */
	void setTransferredBytes(long bytes);

}
