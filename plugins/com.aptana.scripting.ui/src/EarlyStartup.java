import org.eclipse.ui.IStartup;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.console.MessageConsoleStream;

import com.aptana.scripting.ScriptLogListener;
import com.aptana.scripting.ScriptLogger;
import com.aptana.scripting.ui.ScriptingUIPlugin;

public class EarlyStartup implements IStartup
{
	private MessageConsole _console;
	private MessageConsoleStream _consoleStream;
	private ScriptLogListener _logListener;
	
	public void earlyStartup()
	{
		// create console
		this._console = new MessageConsole(
			Messages.EarlyStartup_SCRIPTING_CONSOLE_NAME,
			ScriptingUIPlugin.getImageDescriptor("icons/console.png") //$NON-NLS-1$
		);
		
		// grab message stream
		this._consoleStream = this._console.newMessageStream();
		
		// register our console with Eclipse
		ConsolePlugin.getDefault().getConsoleManager().addConsoles(new IConsole[] { this._console });
		
		// create our scripting log listener
		this._logListener = new ScriptLogListener()
		{
			public void logError(String error)
			{
				_consoleStream.println(Messages.EarlyStartup_ERROR_PREFIX + error);
			}

			public void logInfo(String info)
			{
				_consoleStream.println(Messages.EarlyStartup_INFO_PREFIX + info);
			}

			public void logWarning(String warning)
			{
				_consoleStream.println(Messages.EarlyStartup_WARNING_PREFIX + warning);
			}

			public void trace(String message)
			{
				_consoleStream.println(Messages.EarlyStartup_TRACE_PREFIX + message);
			}
		};
		
		// and register it
		ScriptLogger.getInstance().addLogListener(this._logListener);
	}
}
