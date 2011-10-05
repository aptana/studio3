/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.git.internal.core.launching;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.debug.core.model.LaunchConfigurationDelegate;

import com.aptana.git.core.GitPlugin;
import com.aptana.git.core.IGitLaunchConfigurationConstants;
import com.aptana.git.core.model.GitExecutable;
import com.aptana.git.core.model.GitRepository;
import com.aptana.git.core.model.IGitRepositoryManager;

public class GitLaunchDelegate extends LaunchConfigurationDelegate
{

	public void launch(ILaunchConfiguration configuration, String mode, ILaunch launch, IProgressMonitor monitor)
			throws CoreException
	{
		List<String> commandList = new ArrayList<String>();

		// Git binary
		IPath gitExecutablePath = gitExecutable(configuration);
		commandList.add(gitExecutablePath.toOSString());
		// Arguments to git
		commandList.addAll(arguments(configuration));
		// Working dir
		IPath workingDir = getWorkingDirectory(configuration);
		// ENV
		String[] env = getEnvironment(configuration);

		// Now actually launch the process!
		Process process = DebugPlugin.exec(commandList.toArray(new String[commandList.size()]),
				(workingDir == null) ? null : workingDir.toFile(), env);
		// FIXME Build a label from args?
		String label = commandList.get(0);
		// Set process type to "git" so our linetracker hyperlink stuff works
		Map<String, String> map = new HashMap<String, String>();
		map.put(IProcess.ATTR_PROCESS_TYPE, IGitLaunchConfigurationConstants.PROCESS_TYPE);
		DebugPlugin.newProcess(launch, process, label, map);
	}

	private Collection<? extends String> arguments(ILaunchConfiguration configuration) throws CoreException
	{
		String interpreterArgs = configuration.getAttribute(IGitLaunchConfigurationConstants.ATTR_ARGUMENTS,
				(String) null);
		return Arrays.asList(DebugPlugin.parseArguments(interpreterArgs));
	}

	private String[] getEnvironment(ILaunchConfiguration configuration) throws CoreException
	{
		return DebugPlugin.getDefault().getLaunchManager().getEnvironment(configuration);
	}

	/**
	 * Return a File pointing at the working directory for the launch. Throws a CoreException if no value specified, or
	 * specified location does not exist or is not a directory.
	 * 
	 * @param configuration
	 * @return
	 * @throws CoreException
	 */
	protected IPath getWorkingDirectory(ILaunchConfiguration configuration) throws CoreException
	{
		String workingDirVal = configuration.getAttribute(IGitLaunchConfigurationConstants.ATTR_WORKING_DIRECTORY,
				(String) null);
		if (workingDirVal == null)
		{
			return null;
		}
		IPath workingDirectory = Path.fromOSString(workingDirVal);
		if (!workingDirectory.toFile().isDirectory())
		{
			abort(MessageFormat.format(Messages.GitLaunchDelegate_InvalidWorkingDir, workingDirVal), null);
		}
		return workingDirectory;
	}

	protected IPath gitExecutable(ILaunchConfiguration configuration) throws CoreException
	{
		IPath path = null;
		String location = configuration.getAttribute(IGitLaunchConfigurationConstants.ATTR_LOCATION, (String) null);
		if (location != null)
		{
			path = Path.fromOSString(location);
		}
		if (path == null)
		{
			// Fall back to global instance path
			GitExecutable executable = GitExecutable.instance();
			path = executable.path();
		}
		if (path == null)
		{
			abort(Messages.GitLaunchDelegate_NoGitExecutableSpecified, null);
		}
		if (!path.toFile().exists())
		{
			abort(MessageFormat.format(Messages.GitLaunchDelegate_GitExecutableDoesntExist, path), null);
		}
		return path;
	}

	/**
	 * Determines the affected projects by looping through them to see which ones are attached to the relevant repo.
	 * This way we only prompt for unsaved files when they live inside the same repo!
	 */
	@Override
	protected IProject[] getBuildOrder(ILaunchConfiguration configuration, String mode) throws CoreException
	{
		IPath wd = getWorkingDirectory(configuration);
		if (wd != null)
		{
			GitRepository repo = getRepositoryManager().getUnattachedExisting(wd.toFile().toURI());
			final Set<IProject> affectedProjects = new HashSet<IProject>();
			for (IProject project : ResourcesPlugin.getWorkspace().getRoot().getProjects())
			{
				GitRepository other = getRepositoryManager().getAttached(project);
				if (other != null && other.equals(repo))
				{
					affectedProjects.add(project);
				}
			}
			return computeBuildOrder(affectedProjects.toArray(new IProject[affectedProjects.size()]));
		}
		return super.getBuildOrder(configuration, mode);
	}

	protected IGitRepositoryManager getRepositoryManager()
	{
		return GitPlugin.getDefault().getGitRepositoryManager();
	}

	/**
	 * Throws an exception with a new status containing the given message and optional exception.
	 * 
	 * @param message
	 *            error message
	 * @param e
	 *            underlying exception
	 * @throws CoreException
	 */
	private void abort(String message, Throwable e) throws CoreException
	{
		throw new CoreException(new Status(IStatus.ERROR, GitPlugin.PLUGIN_ID, 0, message, e));
	}
}
