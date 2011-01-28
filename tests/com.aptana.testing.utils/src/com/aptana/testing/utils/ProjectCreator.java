/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
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

	public static IProject createAndOpen(String projectName) throws CoreException
	{
		IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
		if (!project.exists())
		{
			// Create in a new directory inside the temp dir, otherwise on unit test machine we may get messed up
			// because we're already under a git repo!
			IProjectDescription desc = ResourcesPlugin.getWorkspace().newProjectDescription(projectName);
			
			File tmpDir;			
			try
			{
				File tmpfile = File.createTempFile(projectName, null);
				tmpDir = tmpfile.getParentFile();
			}
			catch (IOException e)
			{
				String tmpDirString = System.getProperty("java.io.tmpdir");
				if (tmpDirString == null || tmpDirString.trim().length() == 0)
				{
					tmpDirString = "/tmp";
				}
				tmpDir = new File(tmpDirString);
			}
			File projectDir = new File(tmpDir, projectName);
			desc.setLocation(new Path(projectDir.getAbsolutePath()));
			project.create(desc, new NullProgressMonitor());
		}
		if (!project.isOpen())
			project.open(new NullProgressMonitor());
		return project;
	}
}
