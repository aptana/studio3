/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.git.ui.internal;

import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.ui.IDebugUIConstants;

import com.aptana.console.process.ConsoleProcessFactory;
import com.aptana.git.core.IGitLaunchConfigurationConstants;
import com.aptana.git.core.model.GitExecutable;
import com.aptana.git.core.model.GitRepository;

/**
 * Launches a process through Eclipse's launching infrastructure, launching it into the console.
 * 
 * @author cwilliams
 */
public abstract class Launcher
{

	/**
	 * Launches a git process against the repository.
	 * 
	 * @param repo
	 * @param monitor
	 * @param args
	 * @return
	 */
	public static ILaunch launch(GitRepository repo, IProgressMonitor monitor, String... args) throws CoreException
	{
		IPath workingDir = null;
		if (repo != null)
		{
			workingDir = repo.workingDirectory();
		}
		ILaunchConfigurationWorkingCopy config = createLaunchConfig(GitExecutable.instance().path().toOSString(),
				workingDir, args);
		return config.launch(ILaunchManager.RUN_MODE, monitor);
	}

	private static ILaunchConfigurationWorkingCopy createLaunchConfig(String command, IPath workingDir, String... args)
			throws CoreException
	{
		ILaunchManager manager = DebugPlugin.getDefault().getLaunchManager();
		ILaunchConfigurationType configType = manager
				.getLaunchConfigurationType(IGitLaunchConfigurationConstants.ID_GIT_LAUNCH_CONFIGURATION_TYPE);
		String name = getLastPortion(command) + " " + join(args, " "); //$NON-NLS-1$ //$NON-NLS-2$
		name = manager.generateLaunchConfigurationName(name);
		String toolArgs = '"' + join(args, "\" \"") + '"'; //$NON-NLS-1$
		ILaunchConfigurationWorkingCopy config = configType.newInstance(null, name);
		config.setAttribute(IGitLaunchConfigurationConstants.ATTR_LOCATION, command);
		config.setAttribute(IGitLaunchConfigurationConstants.ATTR_ARGUMENTS, toolArgs);
		if (workingDir != null)
		{
			config.setAttribute(IGitLaunchConfigurationConstants.ATTR_WORKING_DIRECTORY, workingDir.toOSString());
		}
		config.setAttribute(DebugPlugin.ATTR_CAPTURE_OUTPUT, true);
		config.setAttribute(IGitLaunchConfigurationConstants.ATTR_SHOW_CONSOLE, true);
		config.setAttribute(IDebugUIConstants.ATTR_LAUNCH_IN_BACKGROUND, false);
		config.setAttribute(DebugPlugin.ATTR_PROCESS_FACTORY_ID, ConsoleProcessFactory.ID);
		Map<String, String> env = GitExecutable.getEnvironment();
		if (!env.isEmpty())
		{
			config.setAttribute(ILaunchManager.ATTR_ENVIRONMENT_VARIABLES, env);
			config.setAttribute(ILaunchManager.ATTR_APPEND_ENVIRONMENT_VARIABLES, true);
		}
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
