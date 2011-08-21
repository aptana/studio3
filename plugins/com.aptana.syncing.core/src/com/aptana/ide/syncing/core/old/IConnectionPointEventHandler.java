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
 * @author Kevin Lindsey
 */
public interface IConnectionPointEventHandler
{
	/**
	 * getFilesEvent
	 * 
	 * @param manager
	 * @param path
	 * @return Returns true if the file listing should continue. A value of false will abort the current file listing.
	 */
	boolean getFilesEvent(IConnectionPoint manager, String path);
}
