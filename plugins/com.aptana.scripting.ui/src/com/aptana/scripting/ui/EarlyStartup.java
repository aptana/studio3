package com.aptana.scripting.ui;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.eclipse.ui.IStartup;

import com.aptana.scripting.ScriptLogListener;
import com.aptana.scripting.ScriptLogger;

public class EarlyStartup implements IStartup
{
	public void earlyStartup()
	{
		// create our scripting log listener and register it
		ScriptLogger.getInstance().addLogListener(new ScriptLogListener()
		{
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); //$NON-NLS-1$

			private String getDateTimeStamp()
			{
				return format.format(new Date());
			}

			private String formatMessage(String message)
			{
				return "[" + this.getDateTimeStamp() + "] " + message; //$NON-NLS-1$ //$NON-NLS-2$
			}

			public void logError(String error)
			{
				ScriptingConsole.getDefault().getErrorConsoleStream().println(this.formatMessage(error));
			}

			public void logInfo(String info)
			{
				ScriptingConsole.getDefault().getInfoConsoleStream().println(this.formatMessage(info));
			}

			public void logWarning(String warning)
			{
				ScriptingConsole.getDefault().getWarningConsoleStream().println(this.formatMessage(warning));
			}

			public void trace(String message)
			{
				ScriptingConsole.getDefault().getTraceConsoleStream().println(this.formatMessage(message));
			}
		});
	}
}
