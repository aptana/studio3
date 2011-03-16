/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.ide.syncing.core.old;

/**
 * @author Kevin Sawicki (ksawicki@aptana.com)
 */
public interface ILogger
{

	/**
	 * Logs an error message with a throwable object
	 * 
	 * @param message
	 * @param th
	 */
	void logError(String message, Throwable th);

	/**
	 * Logs an info message with a throwable object
	 * 
	 * @param message
	 * @param th
	 */
	void logInfo(String message, Throwable th);

	/**
	 * Logs a warning message with a throwable object
	 * 
	 * @param message
	 * @param th
	 */
	void logWarning(String message, Throwable th);
}
