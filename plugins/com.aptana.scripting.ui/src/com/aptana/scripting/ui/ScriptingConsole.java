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
	private static MessageConsole console;
	private static MessageConsoleStream outputConsoleStream;
	private static MessageConsoleStream errorConsoleStream;
	private static MessageConsoleStream infoConsoleStream;
	private static MessageConsoleStream warningConsoleStream;
	private static MessageConsoleStream traceConsoleStream;

	/**
	 * Return the singleton instance of ScriptConsole. Should be called on UI thread.
	 * <p>
	 * Throws IllegalStateException if unable to get the Display which most likely happens when called on non-UI thread.
	 * 
	 * @return
	 */
	public static ScriptingConsole getDefault()
	{
		if (INSTANCE == null)
		{
			INSTANCE = new ScriptingConsole();
		}
		
		return INSTANCE;
	}

	/**
	 * print
	 * 
	 * @param stream
	 * @param output
	 */
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

	/**
	 * ScriptingConsole
	 */
	private ScriptingConsole()
	{
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
				console = new MessageConsole(Messages.EarlyStartup_SCRIPTING_CONSOLE_NAME, ScriptingUIPlugin.getImageDescriptor(CONSOLE_ICON_PATH));

				// register our console with Eclipse
				ConsolePlugin.getDefault().getConsoleManager().addConsoles(new IConsole[] { console });

				// create message streams
				outputConsoleStream = console.newMessageStream();
				errorConsoleStream = console.newMessageStream();
				infoConsoleStream = console.newMessageStream();
				warningConsoleStream = console.newMessageStream();
				traceConsoleStream = console.newMessageStream();

				// set stream colors
				outputConsoleStream.setColor(display.getSystemColor(SWT.COLOR_WIDGET_FOREGROUND));
				errorConsoleStream.setColor(display.getSystemColor(SWT.COLOR_DARK_RED));
				infoConsoleStream.setColor(display.getSystemColor(SWT.COLOR_DARK_BLUE));
				warningConsoleStream.setColor(display.getSystemColor(SWT.COLOR_DARK_YELLOW));
				traceConsoleStream.setColor(display.getSystemColor(SWT.COLOR_DARK_GREEN));
			}
		});
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
	 * print
	 * 
	 * @param output
	 */
	public void print(String output)
	{
		print(getOutputConsoleStream(), output);
		showConsole();
	}

	/**
	 * printErr
	 * 
	 * @param output
	 */
	public void printErr(String output)
	{
		print(getErrorConsoleStream(), output);
		showConsole();
	}

	/**
	 * showConsole
	 */
	private void showConsole()
	{
		if (console != null)
		{
			console.activate();
		}
	}

}
