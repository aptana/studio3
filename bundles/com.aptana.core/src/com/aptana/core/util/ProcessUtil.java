/**
' * Aptana Studio
 * Copyright (c) 2005-2013 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.core.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;

/**
 * A Utility for launching process synch and async via ProcessBuilder. Does not go through the Eclipse launching
 * infrastructure or our terminal!
 * 
 * @deprecated Use {@link IProcessRunner} when possible!
 * @author cwilliams
 */
public class ProcessUtil
{

	/**
	 * @deprecated Use {@link IProcessRunner#TEXT_TO_OBFUSCATE}
	 */
	public static final String TEXT_TO_OBFUSCATE = IProcessRunner.TEXT_TO_OBFUSCATE;

	private ProcessUtil()
	{
		// added so tests can subclass.
	}

	public static String outputForCommand(String command, IPath workingDir, String... args)
	{
		return outputForCommand(command, workingDir, null, args);
	}

	public static String outputForCommand(String command, IPath workingDir, Map<String, String> env, String... args)
	{
		IStatus result = runInBackground(command, workingDir, env, args);
		if (result == null)
		{
			return null;
		}
		return result.getMessage();
	}

	/**
	 * Runs a command in the workingDir with the passed in arguments. Returns an IStatus. Exit code of the process is
	 * stored in the IStatuse.getCode(). Output is stored in IStatus.getMessage(). A non-zero exit code makes it an
	 * IStatus with ERROR severity. Otherwise it uses OK severity.
	 * 
	 * @param command
	 *            The executable/script to run
	 * @param workingDir
	 *            The working directory to use for the process.
	 * @param args
	 *            A List of String arguments to the command.
	 * @return
	 * @deprecated Use {@link IProcessRunner#runInBackground(IPath, String...)}
	 */
	public static IStatus runInBackground(String command, IPath workingDir, String... args)
	{
		List<String> arguments = new ArrayList<String>(Arrays.asList(args));
		arguments.add(0, command);
		return new ProcessRunner().runInBackground(workingDir, arguments.toArray(new String[arguments.size()]));
	}

	/**
	 * Runs a command in the workingDir with the passed in arguments. Returns an IStatus. Exit code of the process is
	 * stored in the IStatuse.getCode(). Output is stored in IStatus.getMessage(). A non-zero exit code makes it an
	 * IStatus with ERROR severity. Otherwise it uses OK severity.
	 * 
	 * @param command
	 *            The executable/script to run
	 * @param workingDir
	 *            The working directory to use for the process.
	 * @param env
	 *            Environment variable map to use for the process.
	 * @param args
	 *            A List of String arguments to the command.
	 * @return
	 * @deprecated Use {@link IProcessRunner#runInBackground(IPath, Map, String...)}
	 */
	public static IStatus runInBackground(String command, IPath workingDir, Map<String, String> env, String... args)
	{
		List<String> arguments = new ArrayList<String>(Arrays.asList(args));
		arguments.add(0, command);
		return new ProcessRunner().runInBackground(workingDir, env, arguments.toArray(new String[arguments.size()]));
	}

	/**
	 * @deprecated Use {@link IProcessRunner#outputForProcess(Process)}
	 * @param process
	 * @return
	 */
	public static String outputForProcess(Process process)
	{
		return new ProcessRunner().outputForProcess(process);
	}

	/**
	 * reads the stdout and stderr from process, returns an IStatus with the exit code, and results. Cast to
	 * ProcessStatus to get at each stream's output separately.
	 * 
	 * @param process
	 * @return
	 * @deprecated Use {@link IProcessRunner#processResult(Process)}
	 */
	public static IStatus processResult(Process process)
	{
		return new ProcessRunner().processResult(process);
	}

	/**
	 * Launches the process and returns a handle to the active Process.
	 * 
	 * @param command
	 *            The executable/script to run
	 * @param workingDirectory
	 *            The working directory to use for the process.
	 * @param environment
	 *            Environment variable map to use for the process.
	 * @param arguments
	 *            A List of String arguments to the command.
	 * @return
	 * @throws IOException
	 * @throws CoreException
	 * @deprecated Use {@link IProcessRunner#run(String, IPath, Map, String...)}
	 */
	public static Process run(String command, IPath workingDirectory, Map<String, String> environment,
			String... arguments) throws IOException, CoreException
	{
		List<String> args = new ArrayList<String>(Arrays.asList(arguments));
		args.add(0, command);
		return new ProcessRunner().run(workingDirectory, environment, args.toArray(new String[args.size()]));
	}

	/**
	 * Returns the exit code of the process. If timeout passes, we return early. If forceKill is specified and timeout
	 * elapses, we will call destroy on the process before returning. Returns -1 if we were uanble to get the real exiit
	 * code.
	 * 
	 * @param process
	 * @param timeout
	 * @param forceKillAfterTimeout
	 * @return
	 */
	public static int waitForProcess(Process process, final long timeout, boolean forceKillAfterTimeout)
	{
		final Thread waitingThread = Thread.currentThread();
		Thread timeoutThread = new Thread()
		{
			public void run()
			{
				try
				{
					Thread.sleep(timeout);
					waitingThread.interrupt();
				}
				catch (InterruptedException ignore)
				{
				}
			}
		};

		int exitcode = -1;
		if (timeout > 0)
		{
			try
			{
				timeoutThread.start();
				exitcode = process.waitFor();
			}
			catch (InterruptedException e)
			{
				Thread.interrupted();
			}
			finally
			{
				timeoutThread.interrupt();
			}
			if (forceKillAfterTimeout)
			{
				process.destroy();
			}
		}
		try
		{
			exitcode = process.waitFor();
		}
		catch (InterruptedException e)
		{
		}
		return exitcode;
	}

	/**
	 * reads the stdout and stderr from process, returns an IStatus with the exit code, and results. Cast to
	 * ProcessStatus to get at each stream's output separately.
	 * 
	 * @param process
	 * @return status
	 * @deprecated Use {@link IProcessRunner#processResult(Process)}
	 */
	public static IStatus processResultWithTimeout(Process process, long timeOut)
	{
		// Supply timeOut in seconds
		return new ProcessRunner().processResultWithTimeout(process, timeOut);
	}
}
