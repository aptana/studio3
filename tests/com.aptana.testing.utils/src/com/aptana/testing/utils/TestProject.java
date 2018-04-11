/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */

package com.aptana.testing.utils;

import java.io.ByteArrayInputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.ICommand;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;

public class TestProject
{

	private static IProject project;

	/**
	 * Creates a new project with the specified prefix name in the root or the workspace
	 * 
	 * @param projectNamePrefix
	 * @throws CoreException
	 */
	public TestProject(String projectNamePrefix, String[] projectNatures) throws CoreException
	{
		createProject(projectNamePrefix, projectNatures, new String[0], new NullProgressMonitor());
	}

	/**
	 * Creates a new project with the specified prefix name in the root or the workspace
	 * 
	 * @param projectNamePrefix
	 * @throws CoreException
	 */
	public TestProject(String projectNamePrefix, String[] projectNatures, String[] buildSpecs) throws CoreException
	{
		createProject(projectNamePrefix, projectNatures, buildSpecs, new NullProgressMonitor());
	}

	/**
	 * Creates a new project with the specified prefix name in the root or the workspace
	 * 
	 * @param projectNamePrefix
	 * @throws CoreException
	 */
	public TestProject(String projectNamePrefix, String[] projectNatures, IProgressMonitor monitor)
			throws CoreException
	{
		createProject(projectNamePrefix, projectNatures, new String[0], monitor);
	}

	/**
	 * Creates a new project with the specified prefix name in the root or the workspace
	 * 
	 * @param projectNamePrefix
	 * @throws CoreException
	 */
	public TestProject(String projectNamePrefix, String[] projectNatures, String[] buildSpecs, IProgressMonitor monitor)
			throws CoreException
	{
		createProject(projectNamePrefix, projectNatures, buildSpecs, monitor);
	}

	/**
	 * Creates a folder inside a project
	 * 
	 * @param project
	 * @param folderName
	 * @return
	 * @throws CoreException
	 */
	public IFolder createFolder(String folderName) throws CoreException
	{
		return createFolder(folderName, new NullProgressMonitor());
	}

	/**
	 * Creates a folder inside a project
	 * 
	 * @param project
	 * @param folderName
	 * @param monitor
	 * @return
	 * @throws CoreException
	 */
	public IFolder createFolder(String folderName, IProgressMonitor monitor) throws CoreException
	{
		SubMonitor subMonitor = SubMonitor.convert(monitor);
		IFolder folder = project.getFolder(folderName);
		folder.create(true, true, subMonitor);
		return folder;
	}

	/**
	 * Creates a file inside a project
	 * 
	 * @param project
	 * @param fileName
	 * @param contents
	 * @param monitor
	 * @return
	 * @throws CoreException
	 */
	public IFile createFile(String fileName, String contents, IProgressMonitor monitor) throws CoreException
	{
		SubMonitor subMonitor = SubMonitor.convert(monitor);
		IFile file = project.getFile(fileName);
		ByteArrayInputStream source = new ByteArrayInputStream(contents.getBytes());
		file.create(source, true, subMonitor);
		return file;
	}

	/**
	 * Creates a file inside a project
	 * 
	 * @param project
	 * @param fileName
	 * @param contents
	 * @param monitor
	 * @return
	 * @throws CoreException
	 */
	public IFile createFile(String fileName, String contents) throws CoreException
	{
		return createFile(fileName, contents, new NullProgressMonitor());
	}

	/**
	 * Creates a new project with the given name
	 * 
	 * @param projectNamePrefix
	 * @param projectNatures
	 * @param monitor
	 * @return
	 * @throws CoreException
	 */
	protected void createProject(String projectNamePrefix, String[] projectNatures, String[] buildSpecs,
			IProgressMonitor monitor) throws CoreException
	{
		// we pass in a prefix in order to make sure (and ensure) that we have no collisions in project names
		SubMonitor subMonitor = SubMonitor.convert(monitor);
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		project = workspace.getRoot().getProject(projectNamePrefix + "_" + System.currentTimeMillis());
		IProjectDescription description = workspace.newProjectDescription(project.getName());
		description.setNatureIds(projectNatures);

		if (buildSpecs != null && buildSpecs.length > 0)
		{
			List<ICommand> builderCommands = new ArrayList<ICommand>(buildSpecs.length);
			for (String builder : buildSpecs)
			{
				ICommand command = description.newCommand();
				command.setBuilderName(builder);
				builderCommands.add(command);
			}
			description.setBuildSpec(builderCommands.toArray(new ICommand[builderCommands.size()]));
		}

		if (!project.exists())
		{
			project.create(description, subMonitor);
		}
		if (!project.isOpen())
		{
			project.open(subMonitor);
		}
	}

	/**
	 * Deletes a project
	 * 
	 * @param project
	 * @return
	 * @throws CoreException
	 */
	public void delete() throws CoreException
	{
		delete(new NullProgressMonitor());
	}

	/**
	 * Deletes a project
	 * 
	 * @param project
	 * @param monitor
	 * @return
	 * @throws CoreException
	 */
	public void delete(IProgressMonitor monitor) throws CoreException
	{
		SubMonitor subMonitor = SubMonitor.convert(monitor);
		// Refresh before deleting
		project.refreshLocal(IResource.DEPTH_INFINITE, subMonitor);
		// Delete the generated project (with any files we have in it)
		project.delete(true, monitor);
	}

	/**
	 * Returns the actual IProject object
	 * 
	 * @return
	 */
	public IProject getInnerProject()
	{
		return project;
	}

	/**
	 * Finds the member at the given path
	 * 
	 * @param path
	 * @return
	 */
	public IResource findMember(String path)
	{
		return project.findMember(path);
	}

	/**
	 * Returns the actual URI object
	 * 
	 * @return
	 */
	public URI getURI()
	{
		return project.getLocationURI();
	}

}
