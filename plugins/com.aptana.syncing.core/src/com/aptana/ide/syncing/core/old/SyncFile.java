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
public class SyncFile implements ISyncResource
{

	private VirtualFileSyncPair pair;
	private SyncFolder parent;
	private IPath path;
	private boolean skipped = false;
	private int transferState = -1;
	private long transferredBytes;

	/**
	 * Creates a new sync file object
	 * 
	 * @param path
	 * @param pair
	 * @param parent
	 */
	public SyncFile(IPath path, VirtualFileSyncPair pair, SyncFolder parent)
	{
		this.pair = pair;
		this.path = path;
		this.parent = parent;
	}

	/**
	 * @see com.aptana.ide.syncing.ui.views.views.ISyncResource#getPair()
	 */
	public VirtualFileSyncPair getPair()
	{
		return pair;
	}

	/**
	 * @see com.aptana.ide.syncing.ui.views.views.ISyncResource#getParent()
	 */
	public SyncFolder getParent()
	{
		return parent;
	}

	/**
	 * @see com.aptana.ide.syncing.ui.views.views.ISyncResource#getName()
	 */
	public String getName()
	{
		return path.lastSegment();
	}

	/**
	 * @see com.aptana.ide.syncing.ui.views.views.ISyncResource#getPath()
	 */
	public IPath getPath()
	{
		return path;
	}

	/**
	 * @see com.aptana.ide.syncing.ui.views.views.ISyncResource#isSkipped()
	 */
	public boolean isSkipped()
	{
		return skipped;
	}

	/**
	 * @see com.aptana.ide.syncing.ui.views.views.ISyncResource#setSkipped(boolean)
	 */
	public void setSkipped(boolean skipped)
	{
		this.skipped = skipped;
	}

	/**
	 * @see com.aptana.ide.syncing.ui.views.views.ISyncResource#getSyncState()
	 */
	public int getSyncState()
	{
		if (pair == null)
		{
			return -1;
		}
		return pair.getSyncState();
	}

	/**
	 * @see com.aptana.ide.syncing.ui.views.views.ISyncResource#getTransferState()
	 */
	public int getTransferState()
	{
		return transferState;
	}

	/**
	 * @see com.aptana.ide.syncing.ui.views.views.ISyncResource#setTransferState(int)
	 */
	public void setTransferState(int state)
	{
		this.transferState = state;
	}

	/**
	 * @see com.aptana.ide.syncing.ui.views.views.ISyncResource#getTransferredBytes()
	 */
	public long getTransferredBytes()
	{
		return transferredBytes;
	}

	/**
	 * @see com.aptana.ide.syncing.ui.views.views.ISyncResource#setTransferredBytes(long)
	 */
	public void setTransferredBytes(long bytes)
	{
		this.transferredBytes = bytes;
	}

}
