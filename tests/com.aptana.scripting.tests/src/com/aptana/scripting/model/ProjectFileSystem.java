/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
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
