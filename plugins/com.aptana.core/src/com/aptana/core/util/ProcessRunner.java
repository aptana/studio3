/**
 * Aptana Studio
 * Copyright (c) 2014 by Appcelerator, Inc. All Rights Reserved.
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

public class ProcessRunner implements IProcessRunner
{

	private static boolean isJava7 = VersionUtil.compareVersions(System.getProperty("java.version"), "1.7") >= 0; //$NON-NLS-1$ //$NON-NLS-2$
	private static final String MASK = StringUtil.repeat('*', 10);

	public IStatus run(IPath workingDirectory, Map<String, String> environment, char[] input, List<String> args,
			IProgressMonitor monitor)
	{
		try
		{
			// FIXME If the monitor is cancelled we should kill the process if it is blocked! Since it's in another
			// thread we should be able to do so!
			Process p = doRun(args, workingDirectory, environment, false, null, null);
			ProcessRunnable runnable = new SudoCommandProcessRunnable(p, monitor, true, input);
			Thread t = new Thread(runnable, "Runnable for " + args.get(0)); //$NON-NLS-1$
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

	public Process run(String... args) throws IOException, CoreException
	{
		return run((IPath) null, args);
	}

	public Process run(IPath workingDirectory, String... arguments) throws IOException, CoreException
	{
		return run(workingDirectory, null, arguments);
	}

	public Process run(Map<String, String> environment, String... arguments) throws IOException, CoreException
	{
		return run(null, environment, arguments);
	}

	public Process run(IPath workingDirectory, Map<String, String> environment, String... arguments)
			throws IOException, CoreException
	{
		List<String> commands = new ArrayList<String>(Arrays.asList(arguments));
		return doRun(commands, workingDirectory, environment, false, null, null);
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
	private Process doRun(List<String> command, IPath workingDirectory, Map<String, String> environment,
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
		if (isInfoLoggingEnabled(IDebugScopes.SHELL))
		{
			String path = null;
			if (processBuilder.directory() != null)
			{
				path = processBuilder.directory().getAbsolutePath();
			}
			logInfo(MessageFormat.format(Messages.ProcessUtil_RunningProcess,
					getObfuscatedCommandString(command, textToObfuscate), path, map), IDebugScopes.SHELL);
		}
		if (environment != null && environment.containsKey(REDIRECT_ERROR_STREAM))
		{
			processBuilder.redirectErrorStream(true);
		}
		return startProcess(processBuilder);
	}

	/**
	 * @param command
	 * @return
	 */
	protected ProcessBuilder createProcessBuilder(List<String> command)
	{
		return new ProcessBuilder(command);
	}

	protected boolean isInfoLoggingEnabled(String scope)
	{
		return IdeLog.isInfoEnabled(CorePlugin.getDefault(), scope);
	}

	protected Process startProcess(ProcessBuilder processBuilder) throws IOException
	{
		return processBuilder.start();
	}

	/**
	 * Returns a command string after obfuscating the given text to obfuscate.
	 * 
	 * @param command
	 * @param textToObfuscate
	 * @return The obfuscated command string, wrapped in double quotes.
	 */
	String getObfuscatedCommandString(List<String> command, String textToObfuscate)
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
			String urlPattern = "[^:]+:" + quoted + "@"; //$NON-NLS-1$ //$NON-NLS-2$
			Pattern hideMePattern = Pattern.compile(urlPattern + "|^" + quoted + "$|.*?=" + quoted); //$NON-NLS-1$ //$NON-NLS-2$
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
						if (found.matches(urlPattern))
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

	protected void logInfo(String msg, String scope)
	{
		IdeLog.logInfo(CorePlugin.getDefault(), msg, scope);
	}

	public IStatus runInBackground(String... args)
	{
		return runInBackground((IPath) null, args);
	}

	public IStatus runInBackground(IPath workingDir, String... args)
	{
		return runInBackground(workingDir, null, args);
	}

	public IStatus runInBackground(Map<String, String> environment, String... args)
	{
		return runInBackground(null, environment, args);
	}

	public IStatus runInBackground(IPath workingDir, Map<String, String> environment, String... args)
	{
		return runInBackground(workingDir, environment, null, CollectionsUtil.newList(args));
	}

	public IStatus runInBackground(IPath workingDirectory, Map<String, String> environment, String input,
			List<String> args)
	{
		return runInBackground(workingDirectory, environment, input, false, args);
	}

	public IStatus runInBackground(IPath workingDirectory, Map<String, String> environment, String input,
			boolean redirect, List<String> arguments)
	{
		File outFile = null, errFile = null;
		try
		{
			if (redirect)
			{
				outFile = File.createTempFile("studio", ".out"); //$NON-NLS-1$ //$NON-NLS-2$
				errFile = File.createTempFile("studio", ".err"); //$NON-NLS-1$ //$NON-NLS-2$
				Process p;
				if (isJava7)
				{
					p = doRun(arguments, workingDirectory, environment, redirect, outFile, errFile);
				}
				else
				{
					CollectionsUtil.addToList(arguments,
							">", outFile.getAbsolutePath(), "2>", errFile.getAbsolutePath()); //$NON-NLS-1$ //$NON-NLS-2$
					p = run(workingDirectory, environment, arguments.toArray(new String[arguments.size()]));
				}
				return processData(p, outFile, errFile);
			}
			Process p = run(workingDirectory, environment, arguments.toArray(new String[arguments.size()]));
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

	private IStatus processData(Process process, File outputFile, File errorFile)
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

	private IStatus processData(Process process, String input)
	{
		return processData(process.getInputStream(), process.getErrorStream(), process.getOutputStream(), input,
				process, false);
	}

	private IStatus processData(InputStream inputStream, InputStream errorStream, OutputStream outputStream,
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
			logProcessOutput(stdout, stderr);

			return new ProcessStatus(exitValue, stdout, stderr);
		}
		catch (InterruptedException e)
		{
			IdeLog.logError(CorePlugin.getDefault(), e);
		}
		return null;
	}

	private void logProcessOutput(String stdout, String stderr)
	{
		if (isInfoLoggingEnabled(IDebugScopes.SHELL_OUTPUT))
		{
			// We can try to always log the error stream prior to standard output for better visibility of the
			// issues with process.
			StringBuilder sb = new StringBuilder();
			if (!StringUtil.isEmpty(stderr))
			{
				sb.append("Process Error Output:"); //$NON-NLS-1$
				sb.append(FileUtil.NEW_LINE);
				sb.append(stderr);
				sb.append(FileUtil.NEW_LINE);
			}
			if (!StringUtil.isEmpty(stdout))
			{
				sb.append("Process Output:"); //$NON-NLS-1$
				sb.append(FileUtil.NEW_LINE);
				sb.append(stdout);
			}
			logInfo(sb.toString(), IDebugScopes.SHELL_OUTPUT);
		}
	}

	public IStatus processResult(Process p)
	{
		return processData(p, null);
	}

	public String outputForProcess(Process process)
	{
		IStatus result = processData(process, null);
		if (result == null)
		{
			return null;
		}
		return result.getMessage();
	}
}
