package com.aptana.testing.utils;

import java.io.File;
import java.io.IOException;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;

abstract public class ProjectCreator
{

	public static IProject createAndOpen(String projectName) throws CoreException, IOException
	{
		IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
		if (!project.exists())
		{
			// Create in a new directory inside the temp dir, otherwise on unit test machine we may get messed up
			// because we're already under a git repo!
			IProjectDescription desc = ResourcesPlugin.getWorkspace().newProjectDescription(projectName);
			File tmpfile = File.createTempFile(projectName, null);
			File projectDir = new File(tmpfile.getParentFile(), projectName);
			desc.setLocation(new Path(projectDir.getAbsolutePath()));
			project.create(desc, new NullProgressMonitor());
		}
		if (!project.isOpen())
			project.open(new NullProgressMonitor());
		return project;
	}
}
