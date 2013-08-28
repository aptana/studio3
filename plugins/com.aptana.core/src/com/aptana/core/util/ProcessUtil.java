/**
' * Aptana Studio
 * Copyright (c) 2005-2013 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.core.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
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
	protected static boolean isJava7 = VersionUtil.compareVersions(System.getProperty("java.version"), "1.7") >= 0; //$NON-NLS-1$ //$NON-NLS-2$
	private static final String MASK = StringUtil.repeat('*', 10);

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

	private static IStatus processData(Process process, File outputFile, File errorFile)
	{
		FileInputStream inputStream = null, errorStream = null;
		try
		{
			// Wait until the process exits to start reading from the redirected output file.
			inputStream = new FileInputStream(outputFile);
			errorStream = new FileInputStream(errorFile);
			return processData(inputStream, errorStream, null, null, process, true);
		}
		catch (FileNotFoundException e)
		{
			IdeLog.logError(CorePlugin.getDefault(), e);
		}
		finally
		{
			try
			{
				if (inputStream != null)
				{
					inputStream.close();
				}
			}
			catch (IOException e)
			{
			}
			try
			{
				if (errorStream != null)
				{
					errorStream.close();
				}
			}
			catch (IOException e)
			{
			}
		}
		return null;
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
	 * @param redirect
	 *            Whether the output is redirected to a temporary file and read from that.
	 * @param arguments
	 *            A List of String arguments to the command.
	 * @return
	 */
	public static IStatus runInBackground(String command, IPath workingDirectory, String input,
			Map<String, String> environment, boolean redirect, String... arguments)
	{
		File outFile = null, errFile = null;
		try
		{
			if (redirect)
			{
				outFile = File.createTempFile("studio", ".out"); //$NON-NLS-1$ //$NON-NLS-2$
				errFile = File.createTempFile("studio", ".err"); //$NON-NLS-1$ //$NON-NLS-2$
				List<String> argsList = CollectionsUtil.newList(arguments);
				CollectionsUtil.addToList(argsList, ">", outFile.getAbsolutePath(), "2>", errFile.getAbsolutePath()); //$NON-NLS-1$ //$NON-NLS-2$
				Process p;
				if (isJava7)
				{
					List<String> commands = new ArrayList<String>(Arrays.asList(arguments));
					commands.add(0, command);
					p = ProcessUtil.instance().doRun(commands, workingDirectory, environment, true, outFile, errFile);
				}
				else
				{
					p = run(command, workingDirectory, environment, argsList.toArray(new String[argsList.size()]));
				}
				return processData(p, outFile, errFile);
			}
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
		finally
		{
			if (outFile != null)
			{
				outFile.delete();
			}
			if (errFile != null)
			{
				errFile.delete();
			}
		}
	}

	/**
	 * Launches the process, pipes input to STDIN and returns an IStatus representing the result of execution. The
	 * output of the process is displayed onto progress monitor for each line. Exit code of the process is stored in the
	 * IStatuse.getCode(). Output is stored in IStatus.getMessage(). A non-zero exit code makes it an IStatus with ERROR
	 * severity. Otherwise it uses OK severity.
	 * 
	 * @param command
	 *            The executable/script to run
	 * @param input
	 *            String input to pipe to STDIN after launching the process.
	 * @param workingDirectory
	 *            The working directory to use for the process.
	 * @param environment
	 *            Environment variable map to use for the process.
	 * @param monitor
	 *            Progress monitor to display the output of the progress
	 * @param arguments
	 *            A List of String arguments to the command.
	 * @return
	 */
	public static IStatus run(String command, IPath workingDirectory, char[] input, Map<String, String> environment,
			IProgressMonitor monitor, String... args)
	{
		try
		{
			Process p = run(command, workingDirectory, environment, args);
			ProcessRunnable runnable;
			if (PlatformUtil.isWindows())
			{
				runnable = new ProcessRunnable(p, monitor, true);
			}
			else
			{
				runnable = new SudoCommandProcessRunnable(p, monitor, true, input);
			}
			Thread t = new Thread(runnable, "Runnable for " + command); //$NON-NLS-1$
			t.start();
			t.join();
			return runnable.getResult();
		}
		catch (CoreException ce)
		{
			return ce.getStatus();
		}
		catch (Exception e)
		{
			return new Status(Status.ERROR, CorePlugin.PLUGIN_ID, e.getMessage());
		}
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
		return runInBackground(command, workingDirectory, input, environment, false, arguments);
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
		return doRun(command, workingDirectory, environment, false, null, null);
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
	protected Process doRun(List<String> command, IPath workingDirectory, Map<String, String> environment,
			boolean redirect, File outFile, File errFile) throws IOException, CoreException
	{
		ProcessBuilder processBuilder = createProcessBuilder(command);
		if (redirect)
		{
			// Another Windows HACK : redirection operators does not work on Java7. So, using reflection to invoke new
			// Java 7 APIs to redirect output and error streams.
			try
			{
				Method redirectOutputMethod = processBuilder.getClass().getMethod("redirectOutput", File.class); //$NON-NLS-1$
				redirectOutputMethod.invoke(processBuilder, outFile);

				Method redirectErrorMethod = processBuilder.getClass().getMethod("redirectError", File.class); //$NON-NLS-1$
				redirectErrorMethod.invoke(processBuilder, errFile);
			}
			catch (Exception e)
			{
				IdeLog.logError(CorePlugin.getDefault(), e);
			}
		}
		if (workingDirectory != null)
		{
			processBuilder.directory(workingDirectory.toFile());
		}
		// Make sure we don't keep the text in the env map!
		String textToObfuscate = (environment == null) ? null : environment.remove(TEXT_TO_OBFUSCATE);
		TreeMap<String, String> map = null;
		if (environment != null && !environment.isEmpty())
		{
			map = new TreeMap<String, String>(environment);
			processBuilder.environment().putAll(environment);
		}
		if (isInfoLoggingEnabled())
		{
			String path = null;
			if (processBuilder.directory() != null)
			{
				path = processBuilder.directory().getAbsolutePath();
			}

			if (isInfoLoggingEnabled())
			{
				logInfo(MessageFormat.format(Messages.ProcessUtil_RunningProcess,
						getObfuscatedCommandString(command, textToObfuscate), path, map));
			}
		}
		if (environment != null && environment.containsKey(REDIRECT_ERROR_STREAM))
		{
			processBuilder.redirectErrorStream(true);
		}
		return startProcess(processBuilder);
	}

	/**
	 * Returns a command string after obfuscating the given text to obfuscate.
	 * 
	 * @param command
	 * @param textToObfuscate
	 * @return The obfuscated command string, wrapped in double quotes.
	 */
	protected static String getObfuscatedCommandString(List<String> command, String textToObfuscate)
	{
		String message;
		if (!StringUtil.isEmpty(textToObfuscate))
		{
			// @formatter:off
			// password patterns:
			// :(password)@ 		// URLs
			// password 			// arg value
			// key=password 		// key pair value
			// @formatter:on
			String quoted = RegexUtil.quote(textToObfuscate);
			Pattern hideMePattern = Pattern.compile("[^:]+:" + quoted + "@|^" + quoted + "$|.*?=" + quoted); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			List<String> commandMessage = new ArrayList<String>(command.size());
			for (String arg : command)
			{
				if (!StringUtil.isEmpty(arg))
				{
					StringBuffer sb = new StringBuffer();

					Matcher m = hideMePattern.matcher(arg);
					while (m.find())
					{
						String found = m.group();
						String replacement = MASK;
						if (found.charAt(found.length() - 1) == '@')
						{
							replacement = found.substring(0, found.length() - (textToObfuscate.length() + 2)) + ':'
									+ MASK + '@';
						}
						else if (found.endsWith("=" + textToObfuscate)) //$NON-NLS-1$
						{
							replacement = found.substring(0, (found.length() - textToObfuscate.length())) + MASK;
						}
						m.appendReplacement(sb, replacement);
					}
					m.appendTail(sb);
					arg = sb.toString();
				}
				commandMessage.add(arg);
			}
			message = StringUtil.join("\" \"", commandMessage); //$NON-NLS-1$
		}
		else
		{
			message = StringUtil.join("\" \"", command); //$NON-NLS-1$
		}
		return MessageFormat.format("\"{0}\"", message); //$NON-NLS-1$
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
