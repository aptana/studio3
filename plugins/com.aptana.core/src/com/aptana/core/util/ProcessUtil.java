/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
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
import java.util.TreeMap;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import com.aptana.core.CorePlugin;
import com.aptana.core.IDebugScopes;
import com.aptana.core.logging.IdeLog;

/**
 * A Utility for launching process synch and async via ProcessBuilder. Does not go through the Eclipse launching
 * infrastructure or our terminal!
 * 
 * @author cwilliams
 */
public class ProcessUtil
{

	public static final String TEXT_TO_OBFUSCATE = "textToObfuscate"; //$NON-NLS-1$

	private static ProcessUtil fgInstance;
	/*
	 * When this flag is set in the environment for the process, it hints to redirect the error stream to redirect to
	 * output stream itself.
	 */
	public static String REDIRECT_ERROR_STREAM = "redirectErrorStream"; //$NON-NLS-1$

	protected ProcessUtil()
	{
		// added so tests can subclass.
	}

	private synchronized static ProcessUtil instance()
	{
		if (fgInstance == null)
		{
			fgInstance = new ProcessUtil();
		}
		return fgInstance;
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
		String lineSeparator = ResourceUtil.getLineSeparatorValue(null);
		try
		{
			// Read and write in threads to avoid from choking the process streams
			OutputStreamThread writerThread = null;
			if (input != null)
			{
				// TODO - Use EditorUtils.getEncoding once we have an IFile reference.
				// Using the UTF-8 will not work for all cases.
				writerThread = new OutputStreamThread(process.getOutputStream(), input, IOUtil.UTF_8);
			}
			InputStreamGobbler readerGobbler = new InputStreamGobbler(process.getInputStream(), lineSeparator,
					IOUtil.UTF_8);
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
			IdeLog.logError(CorePlugin.getDefault(), e);
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
		return ProcessUtil.instance().doRun(command, workingDirectory, environment);
	}

	/**
	 * Instance method so that we can test this class! Not meant to be called outside tests
	 * 
	 * @param command
	 * @param workingDirectory
	 * @param environment
	 * @param password
	 * @return
	 * @throws IOException
	 * @throws CoreException
	 */
	protected Process doRun(List<String> command, IPath workingDirectory, Map<String, String> environment)
			throws IOException, CoreException
	{
		ProcessBuilder processBuilder = createProcessBuilder(command);
		if (workingDirectory != null)
		{
			processBuilder.directory(workingDirectory.toFile());
		}

		TreeMap<String, String> map = null;
		if (environment != null && !environment.isEmpty())
		{
			map = new TreeMap<String, String>(environment);
			map.remove(TEXT_TO_OBFUSCATE);
			processBuilder.environment().putAll(environment);
		}
		if (isInfoLoggingEnabled())
		{
			String path = null;
			if (processBuilder.directory() != null)
			{
				path = processBuilder.directory().getAbsolutePath();
			}
			String message = StringUtil.join("\" \"", command); //$NON-NLS-1$
			String textToObfuscate = (environment == null) ? null : environment.get(TEXT_TO_OBFUSCATE);
			if (!StringUtil.isEmpty(textToObfuscate))
			{
				message = message.replace(textToObfuscate, StringUtil.repeat('*', textToObfuscate.length()));
			}
			logInfo(StringUtil.format(Messages.ProcessUtil_RunningProcess, new Object[] { message, path, map }));
		}
		if (environment != null && environment.containsKey(REDIRECT_ERROR_STREAM))
		{
			processBuilder.redirectErrorStream(true);
		}
		return startProcess(processBuilder);
	}

	protected Process startProcess(ProcessBuilder processBuilder) throws IOException
	{
		return processBuilder.start();
	}

	protected void logInfo(String msg)
	{
		IdeLog.logInfo(CorePlugin.getDefault(), msg, IDebugScopes.SHELL);
	}

	protected boolean isInfoLoggingEnabled()
	{
		return IdeLog.isInfoEnabled(CorePlugin.getDefault(), IDebugScopes.SHELL);
	}

	/**
	 * @param command
	 * @return
	 */
	protected ProcessBuilder createProcessBuilder(List<String> command)
	{
		return new ProcessBuilder(command);
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
