package com.aptana.scripting.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IStartup;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.console.MessageConsoleStream;

import com.aptana.scripting.ScriptLogListener;
import com.aptana.scripting.ScriptLogger;

public class EarlyStartup implements IStartup
{
	private MessageConsole _console;
	private MessageConsoleStream _errorConsoleStream;
	private MessageConsoleStream _infoConsoleStream;
	private MessageConsoleStream _warningConsoleStream;
	private MessageConsoleStream _traceConsoleStream;
	private ScriptLogListener _logListener;
	
	public void earlyStartup()
	{
		// create console
		this._console = new MessageConsole(
			Messages.EarlyStartup_SCRIPTING_CONSOLE_NAME,
			ScriptingUIPlugin.getImageDescriptor("icons/console.png") //$NON-NLS-1$
		);
		
		// create message streams
		this._errorConsoleStream = this._console.newMessageStream();
		this._infoConsoleStream = this._console.newMessageStream();
		this._warningConsoleStream = this._console.newMessageStream();
		this._traceConsoleStream = this._console.newMessageStream();
		
		// set stream colors
		final Display display = PlatformUI.getWorkbench().getDisplay();
		
		display.syncExec(new Runnable()
		{
			public void run()
			{
				_errorConsoleStream.setColor(display.getSystemColor(SWT.COLOR_DARK_RED));
				_infoConsoleStream.setColor(display.getSystemColor(SWT.COLOR_DARK_BLUE));
				_warningConsoleStream.setColor(display.getSystemColor(SWT.COLOR_DARK_YELLOW));
				_traceConsoleStream.setColor(display.getSystemColor(SWT.COLOR_DARK_GREEN));
			}
		});
		
		// register our console with Eclipse
		ConsolePlugin.getDefault().getConsoleManager().addConsoles(new IConsole[] { this._console });
		
		// create our scripting log listener
		this._logListener = new ScriptLogListener()
		{
			public void logError(String error)
			{
				_errorConsoleStream.println(Messages.EarlyStartup_ERROR_PREFIX + error);
			}

			public void logInfo(String info)
			{
				_infoConsoleStream.println(Messages.EarlyStartup_INFO_PREFIX + info);
			}

			public void logWarning(String warning)
			{
				_warningConsoleStream.println(Messages.EarlyStartup_WARNING_PREFIX + warning);
			}

			public void trace(String message)
			{
				_traceConsoleStream.println(Messages.EarlyStartup_TRACE_PREFIX + message);
			}
		};
		
		// and register it
		ScriptLogger.getInstance().addLogListener(this._logListener);
	}
}
