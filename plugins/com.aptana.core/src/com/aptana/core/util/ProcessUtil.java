package com.aptana.core.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.aptana.core.CorePlugin;
import com.aptana.core.internal.InputStreamGobbler;
import com.aptana.core.internal.OutputStreamThread;

/**
 * A Utility for launching process synch and async via ProcessBuilder. Does not go through the Eclipse launching
 * infrastructure or our terminal!
 * 
 * @author cwilliams
 */
public abstract class ProcessUtil
{

	public static String outputForCommand(String command, String workingDir, String... args)
	{
		return outputForCommand(command, workingDir, null, args);
	}

	public static String outputForCommand(String command, String workingDir, Map<String, String> env, String... args)
	{
		Map<Integer, String> result = runInBackground(command, workingDir, env, args);
		if (result == null || result.isEmpty())
			return null;
		return result.values().iterator().next();
	}

	public static String read(InputStream stream)
	{
		return IOUtil.read(stream, "UTF-8"); //$NON-NLS-1$
	}

	public static Map<Integer, String> runInBackground(String command, String workingDir, String[] args)
	{
		return runInBackground(command, workingDir, null, args);
	}

	public static Map<Integer, String> runInBackground(String command, String workingDir, Map<String, String> env,
			String[] args)
	{
		return runInBackground(command, workingDir, null, env, args);
	}

	/**
	 * Launches the process and returns a map from the exit value to the stdout output read in.
	 * 
	 * @param command
	 * @param workingDir
	 * @param input
	 * @param env
	 * @param args
	 * @return
	 */
	public static Map<Integer, String> runInBackground(String command, String workingDir, String input,
			Map<String, String> env, String[] args)
	{
		List<String> commands = new ArrayList<String>();
		commands.add(command);
		for (String arg : args)
			commands.add(arg);

		ProcessBuilder builder = new ProcessBuilder(commands);
		if (workingDir != null && workingDir.trim().length() > 0)
			builder.directory(new File(workingDir));

		if (env != null && !env.isEmpty())
		{
			builder.environment().putAll(env);
		}
		String lineSeparator = ResourceUtil.getLineSeparatorValue(null);
		try
		{
			Process p = builder.start();
			// Read and write in threads to avoid from choking the process streams
			OutputStreamThread writerThread = null;
			if (input != null)
			{
				// TODO - Use EditorUtils.getEncoding once we have an IFile reference.
				// Using the UTF-8 will not work for all cases.
				writerThread = new OutputStreamThread(p.getOutputStream(), input, "UTF-8"); //$NON-NLS-1$
			}
			InputStreamGobbler readerGobbler = new InputStreamGobbler(p.getInputStream(), lineSeparator, "UTF-8"); //$NON-NLS-1$
			InputStreamGobbler errorGobbler = new InputStreamGobbler(p.getErrorStream(), lineSeparator, null);

			// Start the threads
			if (writerThread != null)
			{
				writerThread.start();
			}
			readerGobbler.start();
			errorGobbler.start();
			// This will wait till the process is done.
			int exitValue = p.waitFor();
			if (writerThread != null)
			{
				writerThread.interrupt();
				writerThread.join();
			}
			readerGobbler.interrupt();
			errorGobbler.interrupt();
			readerGobbler.join();
			errorGobbler.join();

			String read = readerGobbler.getResult();
			if (exitValue != 0 && (read == null || read.trim().length() == 0))
			{
				read = errorGobbler.getResult();
			}
			else
			{
				if (read != null && read.endsWith("\n")) //$NON-NLS-1$
				{
					read = read.substring(0, read.length() - 1);
				}
			}
			Map<Integer, String> result = new HashMap<Integer, String>();
			result.put(exitValue, read);
			return result;
		}
		catch (IOException e)
		{
			CorePlugin.logError(e.getMessage(), e);
		}
		catch (InterruptedException e)
		{
			CorePlugin.logError(e.getMessage(), e);
		}
		return null;
	}

	/**
	 * Launches the process and returns a handle to the active Process.
	 * 
	 * @param command
	 * @param workingDir
	 * @param args
	 * @return
	 * @throws IOException
	 */
	public static Process run(String command, String workingDir, String... args) throws IOException
	{
		List<String> commands = new ArrayList<String>();
		commands.add(command);
		for (String arg : args)
			commands.add(arg);

		ProcessBuilder builder = new ProcessBuilder(commands);
		if (workingDir != null)
			builder.directory(new File(workingDir));

		return builder.start();
	}

}
