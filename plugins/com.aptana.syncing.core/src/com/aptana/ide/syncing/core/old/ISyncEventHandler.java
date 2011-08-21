/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.ide.syncing.core.old;

import org.eclipse.core.runtime.IProgressMonitor;

/**
 * @author Kevin Lindsey
 */
public interface ISyncEventHandler extends IConnectionPointEventHandler
{
	/**
	 * syncContinue
	 * 
	 * @param monitor
	 * @return boolean
	 */
	boolean syncContinue(IProgressMonitor monitor);

	/**
	 * syncEvent
	 * 
	 * @param item
	 * @param index
	 * @param totalItems
	 * @param monitor
	 * @return boolean
	 */
	boolean syncEvent(VirtualFileSyncPair item, int index, int totalItems, IProgressMonitor monitor);

	/**
	 * syncErrorEvent
	 * 
	 * @param item
	 * @param e
	 * @param monitor
	 * @return boolean
	 */
	boolean syncErrorEvent(VirtualFileSyncPair item, Exception e, IProgressMonitor monitor);

	/**
	 * Indicates how many bytes have been transferred for a specific item.
	 * 
	 * @param item
	 *            the item being synced
	 * @param bytes
	 *            the number of bytes transferred
	 * @param monitor
	 */
	void syncTransferring(VirtualFileSyncPair item, long bytes, IProgressMonitor monitor);

	/**
	 * Sync done callback
	 * 
	 * @param item
	 * @param monitor
	 */
	void syncDone(VirtualFileSyncPair item, IProgressMonitor monitor);

}
