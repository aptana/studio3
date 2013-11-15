/**
 * Aptana Studio
 * Copyright (c) 2005-2013 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.samples.model;

import java.io.IOException;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;

import com.aptana.projects.ProjectData;
import com.aptana.samples.handlers.ISampleProjectHandler;

public interface IProjectSample
{

	/**
	 * @return true if the sample is from a git repo, false if it resides locally
	 */
	public boolean isRemote();

	/**
	 * @return the name of the sample
	 */
	public String getName();

	/**
	 * @return the remote git url or the local zip path
	 */
	public String getLocation();

	/**
	 * @return the array of nature ids that should apply to the project
	 */
	public String[] getNatures();

	/**
	 * @return a class that does any necessary post-processing after the project is created; could be null
	 */
	public ISampleProjectHandler getProjectHandler();

	/**
	 * Indicates destination path where the sample project data has to be copied or extracted.
	 * 
	 * @return
	 */
	public IPath getDestination();

	/**
	 * @return Category of the sample.
	 */
	public SampleCategory getCategory();

	/**
	 * @return unique id of the sample.
	 */
	public String getId();

	/**
	 * @return description of the sample.
	 */
	public String getDescription();

	/**
	 * Extract the contents of sample into newly created project.
	 * 
	 * @param project
	 * @param projectData
	 * @param monitor
	 * @return
	 * @throws IOException
	 */
	public IStatus createNewProject(IProject project, ProjectData projectData, IProgressMonitor monitor)
			throws CoreException;
}
