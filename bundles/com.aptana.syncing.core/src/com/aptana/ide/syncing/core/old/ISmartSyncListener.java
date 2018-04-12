/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.ide.syncing.core.old;

/**
 * Smart sync listener
 * 
 * @author Kevin Sawicki (ksawicki@aptana.com)
 */
public interface ISmartSyncListener
{

	/**
	 * A smart sync has been completed for the virtual file managers available in the smart sync event
	 * 
	 * @param event
	 */
	public void smartSyncComplete(SmartSyncEvent event);

}
