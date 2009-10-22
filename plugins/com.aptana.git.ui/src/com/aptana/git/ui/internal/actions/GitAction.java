package com.aptana.git.ui.internal.actions;

import java.io.File;
import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.action.IAction;
import org.eclipse.team.internal.ui.actions.TeamAction;

import com.aptana.git.Activator;
import com.aptana.git.model.GitRepository;

abstract class GitAction extends TeamAction
{

	@Override
	protected void execute(IAction action) throws InvocationTargetException, InterruptedException
	{
		File workingDir = getWorkingDir();
		String working = null;
		if (workingDir != null)
			working = workingDir.toString();
		Activator.getDefault().getExecutable().run(working, getCommand());
	}

	protected abstract String getCommand();

	private File getWorkingDir()
	{
		IProject[] selected = getSelectedProjects();
		if (selected != null)
		{
			if (selected[0] != null)
			{
				IProject project = selected[0];
				IPath path = project.getLocation();
				return path.toFile();
			}
		}
		return null;
	}

	@Override
	public boolean isEnabled()
	{
		IResource[] resources = getSelectedResources();
		if (resources == null || resources.length != 1)
			return false;
		IProject project = resources[0].getProject();
		GitRepository repo = GitRepository.instance(project);
		if (repo == null)
			return false;
		return true;
	}
}
