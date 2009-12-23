package com.aptana.scripting;

import java.util.ArrayList;
import java.util.List;

public class ScriptLogger
{
	private static ScriptLogger INSTANCE;
	
	private List<ScriptLogListener> _logListeners;
	private LogLevel _logLevel;
	
	/**
	 * logError
	 * 
	 * @param message
	 */
	public static void logError(String message)
	{
		getInstance().fireLogErrorEvent(message);
	}

	/**
	 * logInfo
	 * 
	 * @param message
	 */
	public static void logInfo(String message)
	{
		getInstance().fireLogInfoEvent(message);
	}

	/**
	 * logWarning
	 * 
	 * @param message
	 */
	public static void logWarning(String message)
	{
		getInstance().fireLogWarningEvent(message);
	}

	/**
	 * trace
	 * 
	 * @param message
	 */
	public static void trace(String message)
	{
		getInstance().fireTraceEvent(message);
	}
	
	/**
	 * getInstance
	 * 
	 * @return
	 */
	public static ScriptLogger getInstance()
	{
		if (INSTANCE == null)
		{
			INSTANCE = new ScriptLogger();
		}
		
		return INSTANCE;
	}
	
	/**
	 * ScriptLogger
	 */
	private ScriptLogger()
	{
		this._logLevel = LogLevel.ERROR;
	}
	
	/**
	 * addLogListener
	 * 
	 * @param listener
	 */
	public void addLogListener(ScriptLogListener listener)
	{
		if (listener != null)
		{
			if (this._logListeners == null)
			{
				this._logListeners = new ArrayList<ScriptLogListener>();
			}
			
			this._logListeners.add(listener);
		}
	}
	
	/**
	 * fireLogErrorEvent
	 * 
	 * @param error
	 */
	public void fireLogErrorEvent(String error)
	{
		if (this._logListeners != null && this._logLevel.getIndex() <= LogLevel.ERROR.getIndex())
		{
			for (ScriptLogListener listener : this._logListeners)
			{
				listener.logError(error);
			}
		}
	}
	
	/**
	 * fireLogInfoEvent
	 * 
	 * @param error
	 */
	public void fireLogInfoEvent(String info)
	{
		if (this._logListeners != null && this._logLevel.getIndex() <= LogLevel.INFO.getIndex())
		{
			for (ScriptLogListener listener : this._logListeners)
			{
				listener.logInfo(info);
			}
		}
	}
	
	/**
	 * fireLogWarningEvent
	 * 
	 * @param error
	 */
	public void fireLogWarningEvent(String warning)
	{
		if (this._logListeners != null && this._logLevel.getIndex() <= LogLevel.WARNING.getIndex())
		{
			for (ScriptLogListener listener : this._logListeners)
			{
				listener.logWarning(warning);
			}
		}
	}
	
	/**
	 * fireTraceEvent
	 * 
	 * @param error
	 */
	public void fireTraceEvent(String message)
	{
		if (this._logListeners != null && this._logLevel.getIndex() <= LogLevel.TRACE.getIndex())
		{
			for (ScriptLogListener listener : this._logListeners)
			{
				listener.trace(message);
			}
		}
	}
	
	/**
	 * getLogLevel
	 * 
	 * @return
	 */
	public LogLevel getLogLevel()
	{
		return this._logLevel;
	}
	
	/**
	 * removeLogListener
	 * 
	 * @param listener
	 */
	public void removeLogListener(ScriptLogListener listener)
	{
		if (this._logListeners != null)
		{
			this._logListeners.remove(listener);
		}
	}
	
	/**
	 * setLogLevel
	 * 
	 * @param level
	 */
	public void setLogLevel(LogLevel level)
	{
		this._logLevel = level;
	}
	
	/**
	 * setLogLevel
	 * 
	 * @param level
	 */
	public void setLogLevel(String level)
	{
		this._logLevel = LogLevel.get(level);
	}
}
