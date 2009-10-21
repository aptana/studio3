package com.aptana.ide.red.git;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.ui.IDebugUIConstants;
import org.eclipse.debug.ui.RefreshTab;
import org.eclipse.ui.externaltools.internal.model.IExternalToolConstants;

public class ProcessUtil
{

	public static String outputForCommand(String command, String workingDir, String... args)
	{
		Map<Integer, String> result = runInBackground(command, workingDir, args);
		if (result == null || result.isEmpty())
			return null;
		return result.values().iterator().next();
	}

	public static ILaunch run(String command, String workingDir, String... args)
	{
		try
		{
			ILaunchConfigurationWorkingCopy config = createLaunchConfig(command, workingDir, args);
			config.setAttribute(IExternalToolConstants.ATTR_SHOW_CONSOLE, true);
			config.setAttribute(IDebugUIConstants.ATTR_LAUNCH_IN_BACKGROUND, false);
			config.setAttribute(RefreshTab.ATTR_REFRESH_SCOPE, "${project}"); // FIXME Determine if we need to
			// refresh
			// this project, or the workspace based
			// on what projects are attached to same
			// repo!
			config.setAttribute(RefreshTab.ATTR_REFRESH_RECURSIVE, true);
			return config.launch(ILaunchManager.RUN_MODE, new NullProgressMonitor());
		}
		catch (CoreException e1)
		{
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return null;
	}

	private static ILaunchConfigurationWorkingCopy createLaunchConfig(String command, String workingDir, String... args)
			throws CoreException
	{
		String toolArgs = join(args, " ");
		ILaunchManager manager = DebugPlugin.getDefault().getLaunchManager();
		ILaunchConfigurationType configType = manager
				.getLaunchConfigurationType(IExternalToolConstants.ID_PROGRAM_BUILDER_LAUNCH_CONFIGURATION_TYPE);
		ILaunchConfigurationWorkingCopy config = configType.newInstance(null, getLastPortion(command) + " " + toolArgs);
		config.setAttribute(IExternalToolConstants.ATTR_LOCATION, command);
		config.setAttribute(IExternalToolConstants.ATTR_TOOL_ARGUMENTS, toolArgs);
		config.setAttribute(IExternalToolConstants.ATTR_WORKING_DIRECTORY, workingDir);
		config.setAttribute(DebugPlugin.ATTR_CAPTURE_OUTPUT, true);
		config.setAttribute(IExternalToolConstants.ATTR_PROMPT_FOR_ARGUMENTS, false);
		return config;
	}

	private static String getLastPortion(String command)
	{
		IPath path = new Path(command);
		return path.lastSegment();
	}

	private static String read(File tmpFile)
	{
		try
		{
			return read(new BufferedInputStream(new FileInputStream(tmpFile)));
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	private static String read(InputStream stream)
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
		catch (Exception e)
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

	private static String join(String[] commands, String delimiter)
	{
		StringBuilder builder = new StringBuilder();
		for (String command : commands)
		{
			builder.append(command).append(delimiter);
		}
		builder.delete(builder.length() - delimiter.length(), builder.length());
		return builder.toString();
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

	public static Map<Integer, String> runInBackground(String command, String workingDir, String input,
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
