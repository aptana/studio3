/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.sax;

/**
 * IValidatingReaderLogger
 */
public interface IValidatingReaderLogger
{
	/**
	 * log an error
	 * 
	 * @param message
	 */
	void logError(String message, int line, int column);

	/**
	 * log info
	 * 
	 * @param message
	 */
	void logInfo(String message, int line, int column);

	/**
	 * log a warning
	 * 
	 * @param message
	 */
	void logWarning(String message, int line, int column);
}
