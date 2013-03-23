/**
 * Aptana Studio
 * Copyright (c) 2013 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.js.debug.core.model;

/**
 * A JavaScript debug connection handler interface.
 */
public interface IJSDebugConnectionHandler
{
	/**
	 * Handle a connection message
	 * 
	 * @param message
	 */
	void handleMessage(String message);

	/**
	 * Handle connection shutdown
	 */
	void handleShutdown();
}