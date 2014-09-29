/**
 * Aptana Studio
 * Copyright (c) 2005-2013 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.projects;

import java.util.List;
import java.util.UUID;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

/**
 * A class that holds the data for the creation of the project.
 */
public class ProjectData
{
	public String projectName;
	public String directory;
	public boolean isDefaultLocation;
	public String appId;
	public String appGUID;
	public String appURL;
	public List<String> selectedItems;
	public String sdkVersion;
	public boolean cloneFromGit;
	public IProject project;

	public static final String DEFAULT_PUBLISHER_URL = "http://"; //$NON-NLS-1$

	public ProjectData()
	{
		this(null, null, null, false, null, DEFAULT_PUBLISHER_URL, null, null, false);
	}

	public ProjectData(IProject project, String projectName, String directory, boolean isDefaultLocation, String appId,
			String appURL, String sdkVersion, List<String> selectedItems, boolean cloneFromGit)
	{
		this.project = project;

		this.projectName = projectName;
		this.directory = directory;
		this.isDefaultLocation = isDefaultLocation;
		this.appId = appId;
		this.appURL = appURL;
		this.sdkVersion = sdkVersion;
		this.selectedItems = selectedItems;
		this.appGUID = UUID.randomUUID().toString(); // Random generation
		this.cloneFromGit = cloneFromGit;
	}

	public IPath getProjectLocation()
	{
		if (project != null && project.getLocation() != null)
		{
			return project.getLocation();
		}
		IPath path = Path.fromOSString(directory);
		// when default is used, getLocationPath() only returns the workspace root, so needs to append the project
		// name to the path
		if (isDefaultLocation)
		{
			path = path.append(projectName);
		}
		return path;
	}
}
