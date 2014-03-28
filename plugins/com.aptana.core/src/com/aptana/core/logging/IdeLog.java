/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.core.logging;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;

import com.aptana.core.CorePlugin;
import com.aptana.core.ICorePreferenceConstants;
import com.aptana.core.IDebugScopes;
import com.aptana.core.util.EclipseUtil;
import com.aptana.core.util.StringUtil;

/**
 * Utility for logging IDE messages.
 * 
 * @author Spike Washburn
 */
public final class IdeLog
{

	private static Map<Plugin, List<IStatus>> earlyMessageCache = new HashMap<Plugin, List<IStatus>>();
	private static boolean caching = true;
	private static StatusLevel level = StatusLevel.ERROR;

	public static enum StatusLevel
	{
		ERROR, WARNING, INFO, OFF
	};

	/**
	 * Flushes any cached logging messages
	 */
	public static void flushCache()
	{
		caching = false;

		for (Map.Entry<Plugin, List<IStatus>> entry : earlyMessageCache.entrySet())
		{
			for (IStatus status : entry.getValue())
			{
				StatusLevel severity = getStatusLevel(status.getSeverity());
				if (status.getSeverity() == IStatus.ERROR || isOutputEnabled(entry.getKey(), severity, null))
				{
					log(entry.getKey(), status.getSeverity(), status.getMessage(), null, status.getException());
				}
			}
		}

		earlyMessageCache.clear();
	}

	/**
	 * Private constructor not to be used.
	 */
	private IdeLog()
	{
	}

	/**
	 * Get whether Studio is in debug mode
	 * 
	 * @return - true if debugging
	 */
	private static boolean isSeverityEnabled(StatusLevel debugLevel)
	{
		if (caching)
		{
			return true;
		}
		else
		{
			if (level.equals(StatusLevel.OFF))
			{
				return false;
			}

			return level.compareTo(debugLevel) >= 0;
		}
	}

	/**
	 * Returns the current severity preference
	 * 
	 * @return
	 */
	public static StatusLevel getCurrentSeverity()
	{
		return level;
	}

	/**
	 * Sets the current severity preference
	 */
	public static void setCurrentSeverity(StatusLevel severity)
	{
		level = severity;
	}

	/**
	 * Returns the current severity preference
	 * 
	 * @return
	 */
	public static StatusLevel getSeverityPreference()
	{
		try
		{
			return Enum.valueOf(
					StatusLevel.class,
					Platform.getPreferencesService().getString(CorePlugin.PLUGIN_ID,
							ICorePreferenceConstants.PREF_DEBUG_LEVEL, StatusLevel.ERROR.toString(), null));
		}
		catch (IllegalArgumentException ex)
		{
			return StatusLevel.ERROR;
		}
	}

	/**
	 * Is the particular scope in question enabled
	 * 
	 * @param plugin
	 * @param scope
	 * @return
	 */
	private static boolean isScopeEnabled(String scope)
	{
		if (scope != null)
		{
			return EclipseUtil.isDebugOptionEnabled(scope);
		}
		return true;
	}

	/**
	 * Are we currently outputting items of INFO severity and this scope? Use this method if you want to check before
	 * actually composing a message.
	 * 
	 * @return
	 */
	public static boolean isErrorEnabled(Plugin plugin, String scope)
	{
		return isOutputEnabled(plugin, StatusLevel.ERROR, scope);
	}

	/**
	 * Are we currently outputting items of INFO severity and this scope? Use this method if you want to check before
	 * actually composing a message.
	 * 
	 * @return
	 */
	public static boolean isWarningEnabled(Plugin plugin, String scope)
	{
		return isOutputEnabled(plugin, StatusLevel.WARNING, scope);
	}

	/**
	 * Are we currently outputting items of INFO severity and this scope? Use this method if you want to check before
	 * actually composing a message.
	 * 
	 * @return
	 */
	public static boolean isInfoEnabled(Plugin plugin, String scope)
	{
		return isOutputEnabled(plugin, StatusLevel.INFO, scope);
	}

	/**
	 * Are we currently outputting items of TRACE severity and this scope? Use this method if you want to check before
	 * actually composing a message.
	 * 
	 * @return
	 */
	public static boolean isTraceEnabled(Plugin plugin, String scope)
	{
		return isSeverityEnabled(StatusLevel.INFO) && Platform.inDebugMode() && isScopeEnabled(scope);
	}

	/**
	 * Are we currently outputting items of this severity and this scope? Use this method if you want to check before
	 * actually composing an error message.
	 * 
	 * @return
	 */
	private static boolean isOutputEnabled(Plugin plugin, StatusLevel severity, String scope)
	{
		if (!isSeverityEnabled(severity))
		{
			return false;
		}
		try
		{
			if (!Platform.inDebugMode())
			{
				return true;
			}
		}
		catch (Exception e)
		{
			// ignore. May happen if we're running unit tests outside IDE
		}

		return isScopeEnabled(scope);
	}

	/**
	 * Converts the IStatus level into something we get.
	 * 
	 * @param status
	 * @return
	 */
	private static StatusLevel getStatusLevel(int status)
	{
		switch (status)
		{
			case IStatus.INFO:
			{
				return StatusLevel.INFO;
			}
			case IStatus.WARNING:
			{
				return StatusLevel.WARNING;
			}
			case IStatus.ERROR:
			{
				return StatusLevel.ERROR;
			}
			default:
			{
				return StatusLevel.OFF;
			}
		}
	}

	/**
	 * Logs an error
	 * 
	 * @param plugin
	 * @param message
	 *            The message to log
	 */
	public static void logError(Plugin plugin, String message)
	{
		logError(plugin, message, (Throwable) null);
	}

	public static void logError(Plugin plugin, Throwable th)
	{
		logError(plugin, th, null);
	}

	public static void logError(Plugin plugin, Throwable th, String scope)
	{
		logError(plugin, th.getMessage(), th, scope);
	}

	/**
	 * Logs an error
	 * 
	 * @param plugin
	 * @param message
	 *            The message to log
	 */
	public static void logError(Plugin plugin, String message, Throwable th)
	{
		logError(plugin, message, th, null);
	}

	/**
	 * Logs an error
	 * 
	 * @param plugin
	 * @param message
	 *            The message to log
	 */
	public static void logError(Plugin plugin, String message, String scope)
	{
		logError(plugin, message, null, scope);
	}

	/**
	 * Logs an error
	 * 
	 * @param plugin
	 * @param message
	 * @param th
	 */
	public static void logError(Plugin plugin, String message, Throwable th, String scope)
	{
		if (isErrorEnabled(plugin, scope))
		{
			log(plugin, IStatus.ERROR, message, scope, th);
		}
		else
		{
			logTrace(plugin, IStatus.ERROR, message, scope, th);
		}
	}

	/**
	 * Logs a warning
	 * 
	 * @param plugin
	 * @param message
	 *            The message to log
	 */
	public static void logWarning(Plugin plugin, String message)
	{
		logWarning(plugin, message, (Throwable) null);
	}

	/**
	 * Logs a warning
	 * 
	 * @param plugin
	 * @param message
	 *            The message to log
	 */
	public static void logWarning(Plugin plugin, String message, String scope)
	{
		logWarning(plugin, message, null, scope);
	}

	public static void logWarning(Plugin plugin, Throwable th)
	{
		logWarning(plugin, th.getMessage(), th);
	}

	public static void logWarning(Plugin plugin, String message, Throwable th)
	{
		logWarning(plugin, message, th, null);
	}

	/**
	 * Logs a warning
	 * 
	 * @param plugin
	 * @param message
	 * @param th
	 */
	public static void logWarning(Plugin plugin, String message, Throwable th, String scope)
	{
		if (isWarningEnabled(plugin, scope))
		{
			log(plugin, IStatus.WARNING, message, scope, th);
		}
		else
		{
			logTrace(plugin, IStatus.INFO, message, scope, th);
		}
	}

	/**
	 * Logs an informational message
	 * 
	 * @param plugin
	 * @param message
	 */
	public static void logInfo(Plugin plugin, String message)
	{
		logInfo(plugin, message, null);
	}

	/**
	 * Logs an informational message
	 * 
	 * @param plugin
	 * @param message
	 */
	public static void logInfo(Plugin plugin, String message, String scope)
	{
		logInfo(plugin, message, null, scope);
	}

	/**
	 * Logs an informational message
	 * 
	 * @param plugin
	 * @param message
	 * @param th
	 */
	public static void logInfo(Plugin plugin, String message, Throwable th, String scope)
	{
		if (isInfoEnabled(plugin, scope))
		{
			log(plugin, IStatus.INFO, message, scope, th);
		}
		else
		{
			logTrace(plugin, IStatus.INFO, message, scope, th);
		}
	}

	/**
	 * Logs a trace message.
	 * 
	 * @param plugin
	 *            the plugin that generated this message
	 * @param message
	 *            the message to log
	 */
	public static void logTrace(Plugin plugin, String message)
	{
		logTrace(plugin, message, null);
	}

	/**
	 * Logs an trace message with a specific scope.
	 * 
	 * @param plugin
	 *            the plugin that generated this message
	 * @param message
	 *            the message to log
	 * @param scope
	 *            the scope on when the message should be logged
	 */
	public static void logTrace(Plugin plugin, String message, String scope)
	{
		logTrace(plugin, message, null, scope);
	}

	/**
	 * Logs a trace message with a possible exception and specific scope.
	 * 
	 * @param plugin
	 *            the plugin that generated this message
	 * @param message
	 *            the message to log
	 * @param th
	 *            the exception, or null if there isn't one
	 * @param scope
	 *            the scope on when the message should be logged
	 */
	public static void logTrace(Plugin plugin, String message, Throwable th, String scope)
	{
		if (isTraceEnabled(plugin, scope))
		{
			log(plugin, IStatus.INFO, message, scope, th);
		}
		else
		{
			logTrace(plugin, IStatus.INFO, message, scope, th);
		}
	}

	/**
	 * Trace this message out anyway if we are in debug mode
	 * 
	 * @param plugin
	 * @param message
	 * @param scope
	 * @param th
	 */
	private static void logTrace(Plugin plugin, int severity, String message, String scope, Throwable th)
	{
		if (isScopeEnabled(IDebugScopes.LOGGER))
		{
			StatusLevel newSeverity = getStatusLevel(severity);
			boolean inSeverity = isSeverityEnabled(newSeverity);

			String cause = StringUtil.EMPTY;
			if (!inSeverity)
			{
				cause = "Not logging items of current severity."; //$NON-NLS-1$
			}
			else if (!isScopeEnabled(scope))
			{
				cause = "Scope not enabled."; //$NON-NLS-1$
			}

			String traceMessage = MessageFormat.format(
					"(Build {0}) Skipping log of {1} {2} {3}. Cause: {4}", EclipseUtil.getPluginVersion(plugin), //$NON-NLS-1$
					getLabel(severity), scope, message, cause);

			if (plugin != null)
			{
				Status logStatus = new Status(severity, plugin.getBundle().getSymbolicName(), IStatus.OK, traceMessage,
						th);
				plugin.getLog().log(logStatus);
			}
			else
			{
				System.err.println(traceMessage); // CHECKSTYLE:ON
			}
		}
	}

	/**
	 * Logs an item to the current plugin's log
	 * 
	 * @param plugin
	 * @param status
	 */
	public static void log(Plugin plugin, IStatus status)
	{
		log(plugin, status, null);
	}

	/**
	 * Logs an item to the current plugin's log
	 * 
	 * @param plugin
	 * @param status
	 * @param scope
	 */
	public static void log(Plugin plugin, IStatus status, String scope)
	{
		Throwable th = status.getException();
		if (plugin == null)
		{
			// CHECKSTYLE:OFF
			System.err.println(status.getMessage()); // CHECKSTYLE:ON
			if (th != null)
			{
				// CHECKSTYLE:OFF
				th.printStackTrace(); // CHECKSTYLE:ON
			}
			return;
		}

		if (!EclipseUtil.isPluginLoaded(plugin))
		{
			// CHECKSTYLE:OFF
			System.err.println(status.getMessage()); // CHECKSTYLE:ON
			return;
		}

		if (caching)
		{
			List<IStatus> statusMessages = null;
			if (earlyMessageCache.containsKey(plugin))
			{
				statusMessages = earlyMessageCache.get(plugin);
			}
			else
			{
				statusMessages = new ArrayList<IStatus>();
				earlyMessageCache.put(plugin, statusMessages);
			}
			statusMessages.add(status);
		}
		else
		{
			plugin.getLog().log(status);
		}
		if (status.getSeverity() == IStatus.ERROR && isOutputEnabled(plugin, StatusLevel.ERROR, scope))
		{
			// dump the error to stderr so the devteam knows it happened
			// TODO: we should create a debug-mode flag that sets a custom
			// logger for all plugins.
			// CHECKSTYLE:OFF
			System.err.println(status.getMessage()); // CHECKSTYLE:ON

			if (th != null)
			{
				// CHECKSTYLE:OFF
				th.printStackTrace(); // CHECKSTYLE:ON
			}
		}
	}

	/**
	 * Logs an item to the current plugin's log
	 * 
	 * @param plugin
	 * @param status
	 * @param message
	 * @param th
	 */
	private static void log(Plugin plugin, int severity, String message, String scope, Throwable th)
	{
		String tempMessage = buildMessage(plugin, severity, message, scope, th);
		String symbolicName = CorePlugin.PLUGIN_ID;
		if (plugin != null && plugin.getBundle() != null)
		{
			symbolicName = plugin.getBundle().getSymbolicName();
		}
		Status logStatus = new Status(severity, symbolicName, IStatus.OK, tempMessage, th);
		log(plugin, logStatus, scope);
	}

	/**
	 * Constructs the message to log
	 * 
	 * @param plugin
	 * @param severity
	 * @param message
	 * @param scope
	 * @param th
	 * @return
	 */
	private static String buildMessage(Plugin plugin, int severity, String message, String scope, Throwable th)
	{
		if (scope == null)
		{
			scope = StringUtil.EMPTY;
		}

		String version;
		if (EclipseUtil.isStandalone())
		{
			version = EclipseUtil.getProductVersion();
		}
		else
		{
			version = EclipseUtil.getStudioVersion();
		}
		return MessageFormat.format("(Build {0}) {1} {2} {3}", //$NON-NLS-1$
				version, getLabel(severity), scope, message);
	}

	/**
	 * @param status
	 * @return
	 */
	private static String getLabel(int status)
	{
		switch (status)
		{
			case IStatus.INFO:
			{
				return Messages.IdeLog_INFO;
			}
			case IStatus.WARNING:
			{
				return Messages.IdeLog_WARNING;
			}
			case IStatus.ERROR:
			{
				return Messages.IdeLog_ERROR;
			}
			default:
			{
				return Messages.IdeLog_UNKNOWN;
			}
		}
	}
}
