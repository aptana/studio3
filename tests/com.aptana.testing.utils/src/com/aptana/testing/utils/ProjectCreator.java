/**
 * This file Copyright (c) 2005-2010 Aptana, Inc. This program is
 * dual-licensed under both the Aptana Public License and the GNU General
 * Public license. You may elect to use one or the other of these licenses.
 * 
 * This program is distributed in the hope that it will be useful, but
 * AS-IS and WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE, TITLE, or
 * NONINFRINGEMENT. Redistribution, except as permitted by whichever of
 * the GPL or APL you select, is prohibited.
 *
 * 1. For the GPL license (GPL), you can redistribute and/or modify this
 * program under the terms of the GNU General Public License,
 * Version 3, as published by the Free Software Foundation.  You should
 * have received a copy of the GNU General Public License, Version 3 along
 * with this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 * 
 * Aptana provides a special exception to allow redistribution of this file
 * with certain other free and open source software ("FOSS") code and certain additional terms
 * pursuant to Section 7 of the GPL. You may view the exception and these
 * terms on the web at http://www.aptana.com/legal/gpl/.
 * 
 * 2. For the Aptana Public License (APL), this program and the
 * accompanying materials are made available under the terms of the APL
 * v1.0 which accompanies this distribution, and is available at
 * http://www.aptana.com/legal/apl/.
 * 
 * You may view the GPL, Aptana's exception and additional terms, and the
 * APL in the file titled license.html at the root of the corresponding
 * plugin containing this source file.
 * 
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
