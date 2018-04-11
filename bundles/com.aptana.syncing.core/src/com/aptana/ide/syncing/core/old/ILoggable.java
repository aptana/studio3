/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.ide.syncing.core.old;

/**
 * Interface for an object that logs to an ILogger
 * 
 * @author Kevin Sawicki (ksawicki@aptana.com)
 */
public interface ILoggable
{

	/**
	 * Sets the logger to use for this object
	 * 
	 * @param logger
	 */
	void setLogger(ILogger logger);

	/**
	 * Gets the logger this object is currently loggin to
	 * 
	 * @return - logger
	 */
	ILogger getLogger();
}
