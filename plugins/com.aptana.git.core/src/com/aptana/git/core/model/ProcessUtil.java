package com.aptana.git.core.model;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

abstract class ProcessUtil
{

	static String outputForCommand(String command, String workingDir, String... args)
	{
		Map<Integer, String> result = runInBackground(command, workingDir, args);
		if (result == null || result.isEmpty())
			return null;
		return result.values().iterator().next();
	}

	static String read(InputStream stream)
	{
		StringBuilder builder = new StringBuilder();
		try
		{
			int read;
			while ((read = stream.read()) != -1)
			{
				builder.append((char) read);
			}
			return builder.toString();
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally
		{
			try
			{
				stream.close();
			}
			catch (IOException e)
			{
				// ignore
			}
		}

		return null;
	}

	static Map<Integer, String> runInBackground(String command, String workingDir, String[] args)
	{
		return runInBackground(command, workingDir, null, args);
	}

	static Map<Integer, String> runInBackground(String command, String workingDir, Map<String, String> env,
			String[] args)
	{
		return runInBackground(command, workingDir, null, env, args);
	}

	static Map<Integer, String> runInBackground(String command, String workingDir, String input,
			Map<String, String> env, String[] args)
	{
		List<String> commands = new ArrayList<String>();
		commands.add(command);
		for (String arg : args)
			commands.add(arg);

		ProcessBuilder builder = new ProcessBuilder(commands);
		if (workingDir != null)
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
			String read = read(new BufferedInputStream(p.getInputStream()));
			if (read.endsWith("\n"))
				read = read.substring(0, read.length() - 1);
			int exitValue = p.waitFor();
			Map<Integer, String> result = new HashMap<Integer, String>();
			result.put(exitValue, read);
			return result;
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (InterruptedException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
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
			// TODO Auto-generated catch block
			e.printStackTrace();
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
}
