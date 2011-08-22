/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.ide.syncing.core.old.handlers;

import org.eclipse.core.runtime.IProgressMonitor;

import com.aptana.ide.core.io.IConnectionPoint;
import com.aptana.ide.syncing.core.old.ISyncEventHandler;
import com.aptana.ide.syncing.core.old.VirtualFileSyncPair;

/**
 * @author Kevin Sawicki (ksawicki@aptana.com)
 */
public abstract class SyncEventHandlerAdapter implements ISyncEventHandler
{

	/**
	 * @see com.aptana.ide.syncing.core.events.sync.ISyncEventHandler#syncContinue(IProgressMonitor)
	 */
	public boolean syncContinue(IProgressMonitor monitor)
	{
		// Does nothing by default, subclasses should override
		return true;
	}

	/**
	 * @see com.aptana.ide.syncing.core.events.sync.ISyncEventHandler#syncDone(com.aptana.ide.core.io.syncing.sync.VirtualFileSyncPair,
	 *      IProgressMonitor)
	 */
	public void syncDone(VirtualFileSyncPair item, IProgressMonitor monitor)
	{
		// Does nothing by default, subclasses should override
	}

	/**
	 * @see com.aptana.ide.syncing.core.events.sync.ISyncEventHandler#syncErrorEvent(com.aptana.ide.core.io.syncing.sync.VirtualFileSyncPair,
	 *      java.lang.Exception, IProgressMonitor)
	 */
	public boolean syncErrorEvent(VirtualFileSyncPair item, Exception e, IProgressMonitor monitor)
	{
		// Does nothing by default, subclasses should override
		return true;
	}

	/**
	 * @see com.aptana.ide.syncing.core.events.sync.ISyncEventHandler#syncEvent(com.aptana.ide.core.io.syncing.sync.VirtualFileSyncPair,
	 *      int, int, IProgressMonitor)
	 */
	public boolean syncEvent(VirtualFileSyncPair item, int index, int totalItems, IProgressMonitor monitor)
	{
		// Does nothing by default, subclasses should override
		return true;
	}

	/**
	 * @see com.aptana.ide.syncing.core.events.sync.ISyncEventHandler#syncTransferring(com.aptana.ide.core.io.syncing.sync.VirtualFileSyncPair,
	 *      long, IProgressMonitor)
	 */
	public void syncTransferring(VirtualFileSyncPair item, long bytes, IProgressMonitor monitor)
	{
		// Does nothing by default, subclasses should override
	}

	/**
	 * @see com.aptana.ide.core.io.IConnectionPointEventHandler#getFilesEvent(com.aptana.ide.core.io.IVirtualFileManager,
	 *      java.lang.String)
	 */
	public boolean getFilesEvent(IConnectionPoint manager, String path)
	{
		// Does nothing by default, subclasses should override
		return true;
	}

}
