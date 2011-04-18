/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
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
import org.eclipse.core.runtime.Status;

import com.aptana.core.CorePlugin;

/**
 * A Utility for launching process synch and async via ProcessBuilder. Does not go through the Eclipse launching
 * infrastructure or our terminal!
 * 
 * @author cwilliams
 */
public abstract class ProcessUtil
{

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
	 */
	public static IStatus runInBackground(String command, IPath workingDir, String... args)
	{
		return runInBackground(command, workingDir, null, args);
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
	 */
	public static IStatus runInBackground(String command, IPath workingDir, Map<String, String> env, String... args)
	{
		return runInBackground(command, workingDir, null, env, args);
	}

	public static String outputForProcess(Process process)
	{
		IStatus result = processData(process, null);
		if (result == null)
		{
			return null;
		}
		return result.getMessage();
	}

	private static IStatus processData(Process process, String input)
	{
		String lineSeparator = ResourceUtil.getLineSeparatorValue(null);
		try
		{
			// Read and write in threads to avoid from choking the process streams
			OutputStreamThread writerThread = null;
			if (input != null)
			{
				// TODO - Use EditorUtils.getEncoding once we have an IFile reference.
				// Using the UTF-8 will not work for all cases.
				writerThread = new OutputStreamThread(process.getOutputStream(), input, "UTF-8"); //$NON-NLS-1$
			}
			InputStreamGobbler readerGobbler = new InputStreamGobbler(process.getInputStream(), lineSeparator, "UTF-8"); //$NON-NLS-1$
			InputStreamGobbler errorGobbler = new InputStreamGobbler(process.getErrorStream(), lineSeparator, null);

			// Start the threads
			if (writerThread != null)
			{
				writerThread.start();
			}
			readerGobbler.start();
			errorGobbler.start();
			// This will wait till the process is done.
			int exitValue = process.waitFor();
			if (writerThread != null)
			{
				writerThread.interrupt();
				writerThread.join();
			}
			readerGobbler.interrupt();
			errorGobbler.interrupt();
			readerGobbler.join();
			errorGobbler.join();

			String stdout = readerGobbler.getResult();
			String stderr = errorGobbler.getResult();
			return new ProcessStatus(exitValue, stdout, stderr);
		}
		catch (InterruptedException e)
		{
			CorePlugin.logError(e.getMessage(), e);
		}
		return null;
	}

	/**
	 * Launches the process, pipes input to STDIN and returns an IStatus representing the result of execution. Exit code
	 * of the process is stored in the IStatuse.getCode(). Output is stored in IStatus.getMessage(). A non-zero exit
	 * code makes it an IStatus with ERROR severity. Otherwise it uses OK severity.
	 * 
	 * @param command
	 *            The executable/script to run
	 * @param input
	 *            String input to pipe to STDIN after launching the process.
	 * @param workingDirectory
	 *            The working directory to use for the process.
	 * @param environment
	 *            Environment variable map to use for the process.
	 * @param arguments
	 *            A List of String arguments to the command.
	 * @return
	 */
	public static IStatus runInBackground(String command, IPath workingDirectory, String input,
			Map<String, String> environment, String... arguments)
	{
		try
		{
			Process p = run(command, workingDirectory, environment, arguments);
			return processData(p, input);
		}
		catch (IOException e)
		{
			return new Status(IStatus.ERROR, CorePlugin.PLUGIN_ID, e.getMessage(), e);
		}
		catch (CoreException e)
		{
			return e.getStatus();
		}
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
	 */
	public static Process run(String command, IPath workingDirectory, Map<String, String> environment,
			String... arguments) throws IOException, CoreException
	{
		List<String> commands = new ArrayList<String>(Arrays.asList(arguments));
		commands.add(0, command);
		return run(commands, workingDirectory, environment);
	}

	/**
	 * Launches the process and returns a handle to the active Process.
	 * 
	 * @param command
	 *            The executable/script to run
	 * @param workingDirectory
	 *            The working directory to use for the process.
	 * @param arguments
	 *            A List of String arguments to the command.
	 * @return
	 * @throws IOException
	 * @throws CoreException
	 */
	public static Process run(String command, IPath workingDirectory, String... arguments) throws IOException,
			CoreException
	{
		return run(command, workingDirectory, null, arguments);
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
	 * @return
	 * @throws IOException
	 * @throws CoreException
	 */
	public static Process run(List<String> command, IPath workingDirectory, Map<String, String> environment)
			throws IOException, CoreException
	{
		ProcessBuilder processBuilder = new ProcessBuilder(command);
		if (workingDirectory != null)
		{
			processBuilder.directory(workingDirectory.toFile());
		}
		if (environment != null && !environment.isEmpty())
		{
			processBuilder.environment().putAll(environment);
		}
		return processBuilder.start();
	}

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

		int exitcode = 0;
		if (timeout != -1)
		{
			try
			{
				timeoutThread.start();
				exitcode = process.waitFor();
				waitingThread.interrupt();
			}
			catch (InterruptedException e)
			{
				Thread.interrupted();
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

}
