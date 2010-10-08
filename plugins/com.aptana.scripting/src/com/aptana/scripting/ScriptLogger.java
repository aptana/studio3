/**
 * This file Copyright (c) 2005-2010 Aptana, Inc. This program is
 * dual-licensed under both the Aptana Public License and the GNU General
 * Public license. You may elect to use one or the other of these licenses.
 * 
 * This program is distributed in the hope that it will be useful, but
 * AS-IS and WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE, TITLE, or
 * NONINFRINGEMENT. Redistribution, except as permitted by whichever of
 * the GPL or APL you select, is prohibited.
 *
 * 1. For the GPL license (GPL), you can redistribute and/or modify this
 * program under the terms of the GNU General Public License,
 * Version 3, as published by the Free Software Foundation.  You should
 * have received a copy of the GNU General Public License, Version 3 along
 * with this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 * 
 * Aptana provides a special exception to allow redistribution of this file
 * with certain other free and open source software ("FOSS") code and certain additional terms
 * pursuant to Section 7 of the GPL. You may view the exception and these
 * terms on the web at http://www.aptana.com/legal/gpl/.
 * 
 * 2. For the Aptana Public License (APL), this program and the
 * accompanying materials are made available under the terms of the APL
 * v1.0 which accompanies this distribution, and is available at
 * http://www.aptana.com/legal/apl/.
 * 
 * You may view the GPL, Aptana's exception and additional terms, and the
 * APL in the file titled license.html at the root of the corresponding
 * plugin containing this source file.
 * 
 * Any modifications to this file must keep this entire header intact.
 */
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
	 * Remove this once either CommandExecutionUtils or Theming has been pulled out of editor.common
	 * 
	 * @deprecated
	 * @param message
	 */
	public static void print(String message)
	{
		getInstance().firePrintEvent(message);
	}
	
	/**
	 * Remove this once either CommandExecutionUtils or Theming has been pulled out of editor.common
	 * 
	 * @deprecated
	 * @param message
	 */
	public static void printError(String message)
	{
		getInstance().firePrintErrorEvent(message);
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
	 * firePrintEvent
	 * 
	 * @param message
	 */
	@SuppressWarnings("deprecation")
	public void firePrintEvent(String message)
	{
		if (this._logListeners != null)
		{
			for (ScriptLogListener listener : this._logListeners)
			{
				listener.print(message);
			}
		}
	}
	
	/**
	 * firePrintErrorEvent
	 * 
	 * @param message
	 */
	@SuppressWarnings("deprecation")
	public void firePrintErrorEvent(String message)
	{
		if (this._logListeners != null)
		{
			for (ScriptLogListener listener : this._logListeners)
			{
				listener.printError(message);
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
