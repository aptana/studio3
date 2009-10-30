package com.aptana.git.ui.internal.actions;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.ui.IDebugUIConstants;
import org.eclipse.jface.action.IAction;
import org.eclipse.team.internal.ui.actions.TeamAction;
import org.eclipse.ui.externaltools.internal.model.IExternalToolConstants;

import com.aptana.git.core.model.GitExecutable;
import com.aptana.git.core.model.GitRepository;

/**
 * Base class for actions that simple call out to actions/commands on the Git executable to be run inside the Eclipse
 * console. Used for global actions like Push, Pull, Status.
 * 
 * @author cwilliams
 */
public abstract class GitAction extends TeamAction
{

	@Override
	protected void execute(IAction action) throws InvocationTargetException, InterruptedException
	{
		File workingDir = getWorkingDir();
		String working = null;
		if (workingDir != null)
			working = workingDir.toString();
		launch(GitExecutable.instance().path(), working, getCommand());
	}

	protected abstract String[] getCommand();

	private File getWorkingDir()
	{
		IResource[] resources = getSelectedResources();
		if (resources == null || resources.length == 0)
			return null;
		IProject project = resources[0].getProject();
		GitRepository repo = GitRepository.getAttached(project);
		if (repo == null)
			return null;
		return new File(repo.workingDirectory());
	}

	@Override
	public boolean isEnabled()
	{
		IResource[] resources = getSelectedResources();
		if (resources == null || resources.length != 1)
			return false;
		IProject project = resources[0].getProject();
		GitRepository repo = GitRepository.getAttached(project);
		if (repo == null)
			return false;
		return true;
	}

	/**
	 * @param command
	 * @param workingDir
	 * @param args
	 * @return
	 */
	private static ILaunch launch(String command, String workingDir, String... args)
	{
		try
		{
			ILaunchConfigurationWorkingCopy config = createLaunchConfig(command, workingDir, args);
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
		String toolArgs = join(args, " "); //$NON-NLS-1$
		ILaunchManager manager = DebugPlugin.getDefault().getLaunchManager();
		ILaunchConfigurationType configType = manager
				.getLaunchConfigurationType(IExternalToolConstants.ID_PROGRAM_BUILDER_LAUNCH_CONFIGURATION_TYPE);
		ILaunchConfigurationWorkingCopy config = configType.newInstance(null, getLastPortion(command) + " " + toolArgs); //$NON-NLS-1$
		config.setAttribute(IExternalToolConstants.ATTR_LOCATION, command);
		config.setAttribute(IExternalToolConstants.ATTR_TOOL_ARGUMENTS, toolArgs);
		config.setAttribute(IExternalToolConstants.ATTR_WORKING_DIRECTORY, workingDir);
		config.setAttribute(DebugPlugin.ATTR_CAPTURE_OUTPUT, true);
		config.setAttribute(IExternalToolConstants.ATTR_PROMPT_FOR_ARGUMENTS, false);
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
	
	protected void refreshAffectedProjects()
	{
		final Set<IProject> affectedProjects = new HashSet<IProject>();
		for (IResource resource : getSelectedResources())
		{
			if (resource == null)
				continue;
			affectedProjects.add(resource.getProject());
			GitRepository repo = GitRepository.getAttached(resource.getProject());
			if (repo != null)
			{
				affectedProjects.addAll(getAssociatedProjects(repo));
			}
		}

		WorkspaceJob job = new WorkspaceJob(Messages.PullAction_RefreshJob_Title)
		{
			@Override
			public IStatus runInWorkspace(IProgressMonitor monitor) throws CoreException
			{
				int work = 100 * affectedProjects.size();
				SubMonitor sub = SubMonitor.convert(monitor, work);
				for (IProject resource : affectedProjects)
				{
					if (sub.isCanceled())
						return Status.CANCEL_STATUS;
					resource.refreshLocal(IResource.DEPTH_INFINITE, sub.newChild(100));
				}
				sub.done();
				return Status.OK_STATUS;
			}
		};
		job.setRule(ResourcesPlugin.getWorkspace().getRoot());
		job.setUser(true);
		job.schedule();
	}

	private Collection<? extends IProject> getAssociatedProjects(GitRepository repo)
	{
		Set<IProject> projects = new HashSet<IProject>();
		for (IProject project : ResourcesPlugin.getWorkspace().getRoot().getProjects())
		{
			GitRepository other = GitRepository.getAttached(project);
			if (other != null && other.equals(repo))
			{
				projects.add(project);
			}
		}
		return projects;
	}
}
