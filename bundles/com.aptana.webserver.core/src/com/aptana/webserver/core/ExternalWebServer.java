/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.webserver.core;

import java.io.File;
import java.text.MessageFormat;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.core.ILaunchesListener2;
import org.eclipse.debug.core.Launch;
import org.eclipse.debug.core.model.IProcess;

import com.aptana.core.epl.IMemento;
import com.aptana.core.util.CollectionsUtil;
import com.aptana.core.util.ProcessUtil;
import com.aptana.core.util.StringUtil;

/**
 * A simple web server configuration. This assumes a server mapping to a local document root, and allows the user to
 * enter in custom commands to start and stop the server. This can be handy to hook up your own install of something
 * like NodeJS, apache, etc.
 * 
 * @author Max Stepanov
 * @author cwilliams
 */
public class ExternalWebServer extends SimpleWebServer
{

	/**
	 * Listens for when launch terminates, and auto-updates server state when it does (in case it terminated in a way
	 * that user didn't go through {@link IServer#stop(boolean, IProgressMonitor)}.
	 * 
	 * @author cwilliams
	 */
	private final class LaunchListener implements ILaunchesListener2
	{
		public void launchesRemoved(ILaunch[] launches)
		{
			// do nothing
		}

		public void launchesChanged(ILaunch[] launches)
		{
			// do nothing
		}

		public void launchesAdded(ILaunch[] launches)
		{
			// do nothing
		}

		public void launchesTerminated(ILaunch[] launches)
		{
			for (ILaunch launch : launches)
			{
				if (launch.equals(getLaunch()))
				{
					updateState(State.STOPPED);
					DebugPlugin.getDefault().getLaunchManager().removeLaunchListener(fLaunchListener);
					break;
				}
			}
		}
	}

	private static final String START_SERVER_COMMAND = "startCommand"; //$NON-NLS-1$
	private static final String STOP_SERVER_COMMAND = "stopCommand"; //$NON-NLS-1$

	private String startCommand;
	private String stopCommand;
	private Launch fLaunch;
	private LaunchListener fLaunchListener;

	public ExternalWebServer()
	{
	}

	public void saveState(IMemento memento)
	{
		super.saveState(memento);
		if (startCommand != null)
		{
			memento.createChild(START_SERVER_COMMAND).putTextData(startCommand);
		}
		if (stopCommand != null)
		{
			memento.createChild(STOP_SERVER_COMMAND).putTextData(stopCommand);
		}
	}

	public void loadState(IMemento memento)
	{
		super.loadState(memento);

		IMemento child = memento.getChild(START_SERVER_COMMAND);
		if (child != null)
		{
			startCommand = child.getTextData();
		}
		child = memento.getChild(STOP_SERVER_COMMAND);
		if (child != null)
		{
			stopCommand = child.getTextData();
		}
	}

	private IStatus doLaunch(String arg) throws CoreException
	{
		exec(DebugPlugin.parseArguments(arg), getDocumentRootPath());
		return Status.OK_STATUS;
	}

	/**
	 * @param program
	 * @param arguments
	 * @param workingDirectory
	 * @return created process
	 * @throws CoreException
	 */
	private IProcess exec(String[] arguments, IPath workingDirectory) throws CoreException
	{

		File workingDir = null;
		if (workingDirectory != null)
		{
			workingDir = workingDirectory.toFile();
			if (!workingDir.isDirectory())
			{
				workingDir = null;
			}
		}

		// FIXME Make a launch configuration? Doing this low-level stuff by hand is error-prone...
		Process p = DebugPlugin.exec(arguments, workingDir);
		IProcess process = null;
		if (p != null)
		{
			// Do a quick check to see if the execution immediately failed, meaning we probably have a busted command.
			try
			{
				int exitValue = p.exitValue();
				if (exitValue != 0)
				{
					throw new CoreException(ProcessUtil.processResult(p));
				}
			}
			catch (IllegalThreadStateException e)
			{
				// ignore
			}
			fLaunch = new Launch(null, ILaunchManager.RUN_MODE, null);
			fLaunch.setAttribute(DebugPlugin.ATTR_LAUNCH_TIMESTAMP, Long.toString(System.currentTimeMillis()));
			getLaunchManager().addLaunch(fLaunch);
			process = DebugPlugin.newProcess(fLaunch, p,
					MessageFormat.format("{0} - {1}", getType().getName(), getName())); //$NON-NLS-1$
			process.setAttribute(IProcess.ATTR_CMDLINE, renderCommandLine(arguments));

			if (fLaunchListener == null)
			{
				fLaunchListener = new LaunchListener();
			}
			getLaunchManager().addLaunchListener(fLaunchListener);
		}

		return process;
	}

	protected ILaunchManager getLaunchManager()
	{
		return DebugPlugin.getDefault().getLaunchManager();
	}

	/**
	 * @param commandLine
	 * @return rendered command line
	 */
	private String renderCommandLine(String[] commandLine)
	{
		if (commandLine.length < 1)
		{
			return StringUtil.EMPTY;
		}

		return StringUtil.join(" ", commandLine); //$NON-NLS-1$
	}

	public IStatus stop(boolean force, IProgressMonitor monitor)
	{
		updateState(State.STOPPING);
		try
		{
			IStatus status = doLaunch(startCommand);
			updateState(State.STOPPED);
			return status;
		}
		catch (CoreException e)
		{
			updateState(State.UNKNOWN);
			return e.getStatus();
		}
	}

	public IStatus start(String mode, IProgressMonitor monitor)
	{
		// TODO Verify mode is run? We only support running...
		updateState(State.STARTING);
		try
		{
			IStatus status = doLaunch(startCommand);
			updateState(State.STARTED);
			return status;
		}
		catch (CoreException e)
		{
			updateState(State.STOPPED);
			return e.getStatus();
		}
	}

	public String getMode()
	{
		return ILaunchManager.RUN_MODE;
	}

	public ILaunch getLaunch()
	{
		return fLaunch;
	}

	public IProcess[] getProcesses()
	{
		return getLaunch().getProcesses();
	}

	public String getStartCommand()
	{
		return this.startCommand;
	}

	public String getStopCommand()
	{
		return this.stopCommand;
	}

	public void setStartCommand(String text)
	{
		this.startCommand = text;
	}

	public void setStopCommand(String text)
	{
		this.stopCommand = text;
	}

	// Overrides of Simple web server, since we can actually start/stop

	public State getState()
	{
		return this.fState;
	}

	public Set<String> getAvailableModes()
	{
		return CollectionsUtil.newSet(ILaunchManager.RUN_MODE);
	}
}
