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
