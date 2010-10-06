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
package com.aptana.scripting.ui;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.console.MessageConsoleStream;

import com.aptana.scripting.ScriptLogListener;
import com.aptana.scripting.ScriptLogger;
import com.aptana.theme.ConsoleThemer;
import com.aptana.theme.extensions.ConsoleThemePageParticipant;

/**
 * Singleton for the scripting console.
 */
public class ScriptingConsole
{
	private static final String CONSOLE_ICON_PATH = "icons/console.png"; //$NON-NLS-1$

	private static ScriptingConsole INSTANCE;
	private MessageConsole console;
	private MessageConsoleStream outputConsoleStream;
	private MessageConsoleStream errorConsoleStream;
	private MessageConsoleStream infoConsoleStream;
	private MessageConsoleStream warningConsoleStream;
	private MessageConsoleStream traceConsoleStream;

	/**
	 * Singleton: private constructor
	 */
	private ScriptingConsole()
	{
		if (console == null)
		{
			console = new MessageConsole(Messages.EarlyStartup_SCRIPTING_CONSOLE_NAME, ScriptingUIPlugin
					.getImageDescriptor(CONSOLE_ICON_PATH));

			// create message streams
			outputConsoleStream = console.newMessageStream();
			errorConsoleStream = console.newMessageStream();
			infoConsoleStream = console.newMessageStream();
			warningConsoleStream = console.newMessageStream();
			traceConsoleStream = console.newMessageStream();

			// bring console into view when errors occur
			errorConsoleStream.setActivateOnWrite(true);

			HashMap<MessageConsoleStream, String> themeConsoleStreamToColor = new HashMap<MessageConsoleStream, String>();
			themeConsoleStreamToColor.put(outputConsoleStream, ConsoleThemer.CONSOLE_OUTPUT);
			themeConsoleStreamToColor.put(errorConsoleStream, ConsoleThemer.CONSOLE_ERROR);
			themeConsoleStreamToColor.put(infoConsoleStream, ConsoleThemer.CONSOLE_INFO);
			themeConsoleStreamToColor.put(warningConsoleStream, ConsoleThemer.CONSOLE_WARNING);
			themeConsoleStreamToColor.put(traceConsoleStream, ConsoleThemer.CONSOLE_TRACE);

			// Will be used later on by the ConsoleThemePageParticipant to properly set the colors
			// following the theme.
			console.setAttribute(ConsoleThemePageParticipant.THEME_CONSOLE_STREAM_TO_COLOR_ATTRIBUTE,
					themeConsoleStreamToColor);

			// register our console with Eclipse
			ConsolePlugin.getDefault().getConsoleManager().addConsoles(new IConsole[] { console });
		}
	}

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

	/**
	 * addListeners
	 */
	private void addListeners()
	{
		this.listenForLoggingEvents();
	}

	/**
	 * getErrorConsoleStream
	 * 
	 * @return
	 */
	MessageConsoleStream getErrorConsoleStream()
	{
		return errorConsoleStream;
	}

	/**
	 * getInfoConsoleStream
	 * 
	 * @return
	 */
	MessageConsoleStream getInfoConsoleStream()
	{
		return infoConsoleStream;
	}

	/**
	 * getOutputConsoleStream
	 * 
	 * @return
	 */
	MessageConsoleStream getOutputConsoleStream()
	{
		return outputConsoleStream;
	}

	/**
	 * getTraceConsoleStream
	 * 
	 * @return
	 */
	MessageConsoleStream getTraceConsoleStream()
	{
		return traceConsoleStream;
	}

	/**
	 * getWarningConsoleStream
	 * 
	 * @return
	 */
	MessageConsoleStream getWarningConsoleStream()
	{
		return warningConsoleStream;
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
