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
