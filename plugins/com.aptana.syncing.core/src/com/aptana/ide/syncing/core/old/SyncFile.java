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
