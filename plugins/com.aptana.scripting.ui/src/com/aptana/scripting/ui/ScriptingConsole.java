/**
 * Aptana Studio
 * Copyright (c) 2005-2013 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.scripting.ui;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.console.MessageConsoleStream;

import com.aptana.scripting.ScriptLogListener;
import com.aptana.scripting.ScriptLogger;
import com.aptana.theme.ConsoleThemer;

/**
 * Singleton for the scripting console.
 */
public class ScriptingConsole
{
	private static final String CONSOLE_ICON_PATH = "icons/console.png"; //$NON-NLS-1$
	private static ScriptingConsole INSTANCE;

	/**
	 * Return the singleton instance of ScriptConsole. Should be called on UI thread.
	 * 
	 * @return
	 */
	public static synchronized ScriptingConsole getInstance()
	{
		if (INSTANCE == null)
		{
			INSTANCE = new ScriptingConsole();

			INSTANCE.addListeners();
		}

		return INSTANCE;
	}

	private MessageConsole console;
	private MessageConsoleStream outputConsoleStream;
	private MessageConsoleStream errorConsoleStream;
	private MessageConsoleStream infoConsoleStream;
	private MessageConsoleStream warningConsoleStream;
	private MessageConsoleStream traceConsoleStream;
	private Map<MessageConsoleStream, String> streamColorMap;

	/**
	 * Singleton: private constructor
	 */
	private ScriptingConsole()
	{
		if (console == null)
		{
			// create our scripting console so the getters can create streams
			// from it
			console = new MessageConsole(Messages.ScriptingConsole_SCRIPTING_CONSOLE_NAME,
					ScriptingUIPlugin.getImageDescriptor(CONSOLE_ICON_PATH));

			// create the message stream color map so the getters can populate
			// it
			streamColorMap = new HashMap<MessageConsoleStream, String>();

			// make sure message streams exist so we can apply themes to them.
			getOutputConsoleStream();
			getErrorConsoleStream();
			getInfoConsoleStream();
			getWarningConsoleStream();
			getTraceConsoleStream();

			// register our console with Eclipse
			ConsolePlugin.getDefault().getConsoleManager().addConsoles(new IConsole[] { console });
		}
	}

	/**
	 * addListeners
	 */
	private void addListeners()
	{
		this.listenForLoggingEvents();
	}

	/**
	 * getConsoleStream
	 * 
	 * @param currentStream
	 * @param colorKey
	 * @return
	 */
	MessageConsoleStream getConsoleStream(final MessageConsoleStream currentStream, String colorKey)
	{
		// NOTE: We use this odd logic with the getters to cover a case that
		// occurs when a console stream gets wrapped in a RubyIO object for
		// scripting purposes. When the RubyIO object gets GC'ed, the stream
		// is closed. When we get a stream, we detect if it is closed and
		// create a new one, transfer the old color and font settings, and
		// update the stream color map for later themeing
		if (currentStream == null || currentStream.isClosed())
		{
			// remove obsolete reference
			if (currentStream != null)
			{
				streamColorMap.remove(currentStream);
			}

			// create a new stream to take the place of the old one
			final MessageConsoleStream newStream = console.newMessageStream();

			// add in new reference
			streamColorMap.put(newStream, colorKey);

			// transfer current font and color settings, if possible
			if (currentStream != null)
			{
				Display.getDefault().asyncExec(new Runnable()
				{

					public void run()
					{
						newStream.setColor(currentStream.getColor());
						newStream.setFontStyle(currentStream.getFontStyle());
					}
				});
			}

			// return the new console
			return newStream;
		}

		return currentStream;
	}

	/**
	 * getErrorConsoleStream
	 * 
	 * @return
	 */
	MessageConsoleStream getErrorConsoleStream()
	{
		return errorConsoleStream = getConsoleStream(errorConsoleStream, ConsoleThemer.CONSOLE_ERROR);
	}

	/**
	 * getInfoConsoleStream
	 * 
	 * @return
	 */
	MessageConsoleStream getInfoConsoleStream()
	{
		return infoConsoleStream = getConsoleStream(infoConsoleStream, ConsoleThemer.CONSOLE_INFO);
	}

	/**
	 * getOutputConsoleStream
	 * 
	 * @return
	 */
	MessageConsoleStream getOutputConsoleStream()
	{
		return outputConsoleStream = getConsoleStream(outputConsoleStream, ConsoleThemer.CONSOLE_OUTPUT);
	}

	/**
	 * getTraceConsoleStream
	 * 
	 * @return
	 */
	MessageConsoleStream getTraceConsoleStream()
	{
		return traceConsoleStream = getConsoleStream(traceConsoleStream, ConsoleThemer.CONSOLE_TRACE);
	}

	/**
	 * getWarningConsoleStream
	 * 
	 * @return
	 */
	MessageConsoleStream getWarningConsoleStream()
	{
		return warningConsoleStream = getConsoleStream(warningConsoleStream, ConsoleThemer.CONSOLE_WARNING);
	}

	/**
	 * listenForLoggingEvents
	 */
	private void listenForLoggingEvents()
	{
		// create our scripting log listener and register it
		ScriptLogger.getInstance().addLogListener(new ScriptLogListener()
		{
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); //$NON-NLS-1$

			private String formatMessage(String message)
			{
				return "[" + this.getDateTimeStamp() + "] " + message; //$NON-NLS-1$ //$NON-NLS-2$
			}

			private String getDateTimeStamp()
			{
				return format.format(new Date());
			}

			public void logError(String error)
			{
				getErrorConsoleStream().println(this.formatMessage(error));
			}

			public void logInfo(String info)
			{
				getInfoConsoleStream().println(this.formatMessage(info));
			}

			public void logWarning(String warning)
			{
				getWarningConsoleStream().println(this.formatMessage(warning));
			}

			public void print(String message)
			{
				getOutputConsoleStream().println(message);
			}

			public void printError(String message)
			{
				getErrorConsoleStream().println(message);
			}

			public void trace(String message)
			{
				getTraceConsoleStream().println(this.formatMessage(message));
			}
		});
	}
}
