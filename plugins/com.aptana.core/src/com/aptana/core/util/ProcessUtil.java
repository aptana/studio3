/**
' * Aptana Studio
 * Copyright (c) 2005-2013 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.core.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;

import com.aptana.core.CorePlugin;
import com.aptana.core.logging.IdeLog;

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

	public static String outputForProcess(Process process)
	{
		IStatus result = processData(process, null);
		if (result == null)
		{
			return null;
		}
		return result.getMessage();
	}

	/**
	 * reads the stdout and stderr from process, returns an IStatus with the exit code, and results. Cast to
	 * ProcessStatus to get at each stream's output separately.
	 * 
	 * @param process
	 * @return
	 */
	public static IStatus processResult(Process process)
	{
		return processData(process, null);
	}

	private static IStatus processData(Process process, String input)
	{
		return processData(process.getInputStream(), process.getErrorStream(), process.getOutputStream(), input,
				process, false);
	}

	private static IStatus processData(InputStream inputStream, InputStream errorStream, OutputStream outputStream,
			String input, Process process, boolean earlyWait)
	{
		String lineSeparator = ResourceUtil.getLineSeparatorValue(null);
		try
		{
			int exitValue = 0;
			if (earlyWait)
			{
				exitValue = process.waitFor();
			}
			// Read and write in threads to avoid from choking the process streams
			OutputStreamThread writerThread = null;
			if (!StringUtil.isEmpty(input))
			{
				// TODO - Use EditorUtils.getEncoding once we have an IFile reference.
				// Using the UTF-8 will not work for all cases.
				writerThread = new OutputStreamThread(outputStream, input, IOUtil.UTF_8);
			}
			InputStreamGobbler readerGobbler = new InputStreamGobbler(inputStream, lineSeparator, IOUtil.UTF_8);
			InputStreamGobbler errorGobbler = new InputStreamGobbler(errorStream, lineSeparator, null);

			// Start the threads
			if (writerThread != null)
			{
				writerThread.start();
			}
			readerGobbler.start();
			errorGobbler.start();
			if (!earlyWait)
			{
				// This will wait till the process is done.
				exitValue = process.waitFor();
			}
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
			IdeLog.logError(CorePlugin.getDefault(), e);
		}
		return null;
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
}
