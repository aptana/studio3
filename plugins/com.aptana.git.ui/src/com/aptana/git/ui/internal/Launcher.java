package com.aptana.git.ui.internal;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.ui.IDebugUIConstants;
import org.eclipse.ui.externaltools.internal.model.IExternalToolConstants;

import com.aptana.git.ui.GitUIPlugin;

/**
 * Launches a process through Eclipse's launching infrastructure, launching it into the console.
 * 
 * @author cwilliams
 */
@SuppressWarnings("restriction")
public abstract class Launcher
{

	/**
	 * @param command
	 * @param workingDir
	 * @param args
	 * @return
	 */
	public static ILaunch launch(String command, String workingDir, String... args)
	{
		try
		{
			ILaunchConfigurationWorkingCopy config = createLaunchConfig(command, workingDir, args);
			return config.launch(ILaunchManager.RUN_MODE, new NullProgressMonitor());
		}
		catch (CoreException e)
		{
			GitUIPlugin.logError(e);
		}
		return null;
	}

	// TODO 3.6+ Can't properly point to undeprecated constants until 3.6 is our base version where they moved these out to a core plugin
	// @SuppressWarnings("deprecation")
	private static ILaunchConfigurationWorkingCopy createLaunchConfig(String command, String workingDir, String... args)
			throws CoreException
	{
		String toolArgs = join(args, " "); //$NON-NLS-1$
		ILaunchManager manager = DebugPlugin.getDefault().getLaunchManager();
		ILaunchConfigurationType configType = manager
				.getLaunchConfigurationType(IExternalToolConstants.ID_PROGRAM_BUILDER_LAUNCH_CONFIGURATION_TYPE);
		ILaunchConfigurationWorkingCopy config = configType.newInstance(null, getLastPortion(command) + " " + toolArgs); //$NON-NLS-1$
		config.setAttribute(IExternalToolConstants.ATTR_LOCATION, command);
		config.setAttribute(IExternalToolConstants.ATTR_TOOL_ARGUMENTS, toolArgs);
		config.setAttribute(IExternalToolConstants.ATTR_WORKING_DIRECTORY, workingDir);
		config.setAttribute(DebugPlugin.ATTR_CAPTURE_OUTPUT, true);
		config.setAttribute(IExternalToolConstants.ATTR_SHOW_CONSOLE, true);
		config.setAttribute(IDebugUIConstants.ATTR_LAUNCH_IN_BACKGROUND, false);
		return config;
	}

	private static String getLastPortion(String command)
	{
		return new Path(command).lastSegment();
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
}
