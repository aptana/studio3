package com.aptana.scripting.ui;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTError;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.console.MessageConsoleStream;

public class ScriptingConsole
{
	private static final String CONSOLE_ICON_PATH = "icons/console.png"; //$NON-NLS-1$

	private static ScriptingConsole INSTANCE;

	private static MessageConsole _console;
	private static MessageConsoleStream _outputConsoleStream;
	private static MessageConsoleStream _errorConsoleStream;
	private static MessageConsoleStream _infoConsoleStream;
	private static MessageConsoleStream _warningConsoleStream;
	private static MessageConsoleStream _traceConsoleStream;

	/**
	 * Return the singleton instance of ScriptConsole. Should be called
	 * on UI thread.
	 * <p>
	 * Throws IllegalStateException if unable to get the Display
	 * which most likely happens when called on non-UI thread.
	 *
	 * @return
	 */
	public static ScriptingConsole getDefault()
	{
		if (INSTANCE == null) {
			INSTANCE = new ScriptingConsole();
		}
		return INSTANCE;
	}

	private ScriptingConsole() {
		IWorkbench workbench = PlatformUI.getWorkbench();
		final Display display = workbench.getDisplay();
		if (display == null)
		{
			throw new IllegalStateException(new SWTError(SWT.ERROR_THREAD_INVALID_ACCESS));
		}
		display.syncExec(new Runnable()
		{
			public void run()
			{
				// create console
				_console = new MessageConsole(
					Messages.EarlyStartup_SCRIPTING_CONSOLE_NAME,
					ScriptingUIPlugin.getImageDescriptor(CONSOLE_ICON_PATH)
				);

				// register our console with Eclipse
				ConsolePlugin.getDefault().getConsoleManager().addConsoles(new IConsole[] { _console });

				// create message streams
				_outputConsoleStream = _console.newMessageStream();
				_errorConsoleStream = _console.newMessageStream();
				_infoConsoleStream = _console.newMessageStream();
				_warningConsoleStream = _console.newMessageStream();
				_traceConsoleStream = _console.newMessageStream();

				// set stream colors
				_outputConsoleStream.setColor(display.getSystemColor(SWT.COLOR_WIDGET_FOREGROUND));
				_errorConsoleStream.setColor(display.getSystemColor(SWT.COLOR_DARK_RED));
				_infoConsoleStream.setColor(display.getSystemColor(SWT.COLOR_DARK_BLUE));
				_warningConsoleStream.setColor(display.getSystemColor(SWT.COLOR_DARK_YELLOW));
				_traceConsoleStream.setColor(display.getSystemColor(SWT.COLOR_DARK_GREEN));
			}
		});
	}

	public void print(String output)
	{
		print(getOutputConsoleStream(), output);
		showConsole();
	}

	public void printErr(String output)
	{
		print(getErrorConsoleStream(), output);
		showConsole();
	}

	private void showConsole()
	{
		if (_console != null)
		{
			_console.activate();
		}
	}

	private static void print(final MessageConsoleStream stream, final String output)
	{
		Job job = new Job("Writing to console") //$NON-NLS-1$
		{
			@Override
			protected IStatus run(IProgressMonitor monitor)
			{
				stream.print(output);
				return Status.OK_STATUS;
			}
		};
		job.setSystem(true);
		job.setPriority(Job.SHORT);
		job.schedule();
	}

	MessageConsoleStream getOutputConsoleStream()
	{
		return _outputConsoleStream;
	}

	MessageConsoleStream getErrorConsoleStream()
	{
		return _errorConsoleStream;
	}

	MessageConsoleStream getInfoConsoleStream()
	{
		return _infoConsoleStream;
	}

	MessageConsoleStream getWarningConsoleStream()
	{
		return _warningConsoleStream;
	}

	MessageConsoleStream getTraceConsoleStream()
	{
		return _traceConsoleStream;
	}

}
