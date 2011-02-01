/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.scripting;

public interface ScriptLogListener
{
	/**
	 * logError
	 * 
	 * @param error
	 */
	void logError(String error);
	
	/**
	 * logInfo
	 * 
	 * @param info
	 */
	void logInfo(String info);
	
	
	/**
	 * logWarning
	 * 
	 * @param warning
	 */
	void logWarning(String warning);
	
	/**
	 * trace
	 * 
	 * @param message
	 */
	void trace(String message);
	
	/**
	 * Remove this once either CommandExecutionUtils or Theming has been pulled out of editor.common
	 * 
	 * @deprecated
	 * @param message
	 */
	void print(String message);
	
	/**
	 * Remove this once either CommandExecutionUtils or Theming has been pulled out of editor.common
	 * 
	 * @deprecated
	 * @param message
	 */
	void printError(String message);
}
