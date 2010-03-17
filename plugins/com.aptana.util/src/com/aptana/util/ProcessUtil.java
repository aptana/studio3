package com.aptana.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
		try
		{
			Process p = builder.start();
			if (input != null)
			{
				write(input, p.getOutputStream());
			}
			String read = read(p.getInputStream());
			if (read.endsWith("\n")) //$NON-NLS-1$
				read = read.substring(0, read.length() - 1);
			int exitValue = p.waitFor();
			if (exitValue != 0 && (read == null || read.trim().length() == 0))
			{
				read = read(p.getErrorStream());
			}
			Map<Integer, String> result = new HashMap<Integer, String>();
			result.put(exitValue, read);
			return result;
		}
		catch (IOException e)
		{
			UtilPlugin.logError(e.getMessage(), e);
		}
		catch (InterruptedException e)
		{
			UtilPlugin.logError(e.getMessage(), e);
		}
		return null;
	}

	private static void write(String input, OutputStream out)
	{
		if (out == null)
			return;
		try
		{
			out.write(input.getBytes());
			out.flush();
		}
		catch (IOException e)
		{
			UtilPlugin.logError(e.getMessage(), e);
		}
		finally
		{
			try
			{
				out.close();
			}
			catch (IOException e)
			{
				// ignore
			}
		}
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
