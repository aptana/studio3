/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.ide.syncing.core.old;

import com.aptana.ide.core.io.IConnectionPoint;

/**
 * @author Kevin Sawicki (ksawicki@aptana.com)
 */
public class SmartSyncEvent
{

	/**
	 * Destination file manager
	 */
	public IConnectionPoint destinationManager;

	/**
	 * Source file manager
	 */
	public IConnectionPoint sourceManager;

	/**
	 * All the pairs that completed successfully
	 */
	public VirtualFileSyncPair[] completedPairs;

	/**
	 * Comment about the sync.
	 */
	public String comment;

}
