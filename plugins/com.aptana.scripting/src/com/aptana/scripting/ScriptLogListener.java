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
}
