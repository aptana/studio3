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
package com.aptana.scripting.model;

import java.io.ByteArrayInputStream;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;

public class ProjectFileSystem implements IBundleFileSystem
{
	/*
	 * (non-Javadoc)
	 * @see com.aptana.scripting.model.IBundleFileSystem#createDirectory(java.lang.Object, java.lang.String)
	 */
	public Object createDirectory(Object folder, String name) throws Exception
	{
		IPath path = new Path(name);
		IFolder result = ((IContainer) folder).getFolder(path);

		if (result.exists() == false)
		{
			result.create(true, true, new NullProgressMonitor());
		}

		return result;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.scripting.model.IBundleFileSystem#createFile(java.lang.Object, java.lang.String,
	 * java.lang.String)
	 */
	public Object createFile(Object folder, String name, String content) throws Exception
	{
		ByteArrayInputStream source = new ByteArrayInputStream(content.getBytes());
		IFile result = ((IFolder) folder).getFile(name);

		result.create(source, true, new NullProgressMonitor());

		return result;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.scripting.model.IBundleFileSystem#createProject(java.lang.String)
	 */
	public Object createProject(String name) throws Exception
	{
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IProject project = workspace.getRoot().getProject(name);

		if (project.exists() == false)
		{
			project.create(new NullProgressMonitor());
		}
		if (project.isOpen() == false)
		{
			project.open(new NullProgressMonitor());
		}

		return project;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.scripting.model.IBundleFileSystem#deleteDirectory(java.lang.Object)
	 */
	public void deleteDirectory(Object directory) throws Exception
	{
		((IFolder) directory).delete(true, new NullProgressMonitor());
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.scripting.model.IBundleFileSystem#deleteFile(java.lang.Object)
	 */
	public void deleteFile(Object file) throws Exception
	{
		((IFile) file).delete(true, new NullProgressMonitor());
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.scripting.model.IBundleFileSystem#deleteProject()
	 */
	public void deleteProject(Object project) throws Exception
	{
		((IProject) project).delete(true, new NullProgressMonitor());
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.scripting.model.IBundleFileSystem#exists(java.lang.Object)
	 */
	public boolean exists(Object file)
	{
		return ((IResource) file).exists();
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.scripting.model.IBundleFileSystem#getFile(java.lang.Object, java.lang.String)
	 */
	public Object getFile(Object directory, String name) throws Exception
	{
		return ((IFolder) directory).getFile(new Path(name));
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.scripting.model.IBundleFileSystem#moveDirectory(java.lang.Object, java.lang.String)
	 */
	public void moveDirectory(Object directory, String newName) throws Exception
	{
		IFolder current = (IFolder) directory;
		IPath newPath = current.getParent().getFullPath().append(newName);
		
		current.move(newPath, true, new NullProgressMonitor());
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.scripting.model.IBundleFileSystem#moveFile(java.lang.Object, java.lang.String)
	 */
	public void moveFile(Object file, String newName) throws Exception
	{
		IFile current = (IFile) file;
		IPath newPath = current.getParent().getFullPath().append(newName);

		current.move(newPath, true, new NullProgressMonitor());
	}
}
