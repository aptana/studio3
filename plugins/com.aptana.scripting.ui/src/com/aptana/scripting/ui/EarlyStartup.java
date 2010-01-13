package com.aptana.scripting.ui;

import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.eclipse.ui.IStartup;
import org.jruby.Ruby;
import org.jruby.RubyIO;

import com.aptana.scripting.ScriptLogListener;
import com.aptana.scripting.ScriptLogger;
import com.aptana.scripting.ScriptingEngine;

public class EarlyStartup implements IStartup
{
	private static final String CONSOLE_CONSTANT = "CONSOLE";
	private static final String CONSOLE_VARIABLE = "$console";

	public void earlyStartup()
	{
		final ScriptingConsole console = ScriptingConsole.getDefault();
		
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
				console.getErrorConsoleStream().println(this.formatMessage(error));
			}

			public void logInfo(String info)
			{
				console.getInfoConsoleStream().println(this.formatMessage(info));
			}

			public void logWarning(String warning)
			{
				console.getWarningConsoleStream().println(this.formatMessage(warning));
			}

			public void trace(String message)
			{
				console.getTraceConsoleStream().println(this.formatMessage(message));
			}
		});

		// create CONSOLE and $console streams
		Ruby runtime = ScriptingEngine.getInstance().getScriptingContainer().getRuntime();
		RubyIO rubyStream = new RubyIO(runtime, console.getOutputConsoleStream());
		
		// store as a global and a constant
		runtime.getGlobalVariables().set(CONSOLE_VARIABLE, rubyStream);
		runtime.getObject().setConstant(CONSOLE_CONSTANT, rubyStream);

		// use console for STDERR
		// NOTE: The whole isVerbose thing is a bit hacky. As a side-effect, if the verbose
		// setting is nil, then warnings are turned off. We turn them off so we don't get the
		// warning about redefining the STDERR constant. I would expect setErrorWriter to
		// prevent that, but it doesn't. Obviously, this workaround is probably not future-
		// proof and there may be a correct way of redefining STDERR which doesn't throw this
		// warning
		OutputStreamWriter writer = new OutputStreamWriter(console.getErrorConsoleStream());
		boolean isVerbose = runtime.isVerbose();
		runtime.setVerbose(runtime.getNil());
		ScriptingEngine.getInstance().getScriptingContainer().setErrorWriter(writer);
		runtime.setVerbose((isVerbose) ? runtime.getTrue() : runtime.getFalse());
	}
}
