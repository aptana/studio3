/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.projects;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.runtime.CoreException;

import com.aptana.core.build.UnifiedBuilder;
import com.aptana.core.util.ResourceUtil;

public class WebProjectNature implements IProjectNature
{

	public static final String ID = ProjectsPlugin.PLUGIN_ID + ".webnature"; //$NON-NLS-1$

	private IProject project;

	public void configure() throws CoreException
	{
		ResourceUtil.addBuilder(getProject(), UnifiedBuilder.ID);
	}

	public void deconfigure() throws CoreException
	{
		ResourceUtil.removeBuilderIfOrphaned(getProject(), UnifiedBuilder.ID);
	}

	public IProject getProject()
	{
		return project;
	}

	public void setProject(IProject project)
	{
		this.project = project;
	}
}
