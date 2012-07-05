/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.projects.util;

import java.io.File;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.ICommand;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Platform;

import com.aptana.core.logging.IdeLog;
import com.aptana.core.util.ArrayUtil;
import com.aptana.core.util.CollectionsUtil;
import com.aptana.core.util.StringUtil;
import com.aptana.projects.ProjectsPlugin;

public class ProjectUtil
{

	/**
	 * @param projectPath
	 *            the project location
	 * @param natureIds
	 *            the list of required natures
	 * @param builderIds
	 *            the list of required builders
	 * @return a project description for the project that includes the list of required natures and builders
	 */
	public static IProjectDescription getProjectDescription(IPath projectPath, String[] natureIds, String[] builderIds)
	{
		if (projectPath == null)
		{
			return null;
		}

		IProjectDescription description = null;
		IPath dotProjectPath = projectPath.append(IProjectDescription.DESCRIPTION_FILE_NAME);
		File dotProjectFile = dotProjectPath.toFile();
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		if (dotProjectFile.exists())
		{
			// loads description from the existing .project file
			try
			{
				description = workspace.loadProjectDescription(dotProjectPath);
				if (Platform.getLocation().isPrefixOf(projectPath))
				{
					description.setLocation(null);
				}
			}
			catch (CoreException e)
			{
				IdeLog.logWarning(ProjectsPlugin.getDefault(), "Failed to load the existing .project file.", e); //$NON-NLS-1$
			}
		}
		if (description == null)
		{
			// creates a new project description
			description = workspace.newProjectDescription(projectPath.lastSegment());
			if (Platform.getLocation().isPrefixOf(projectPath))
			{
				description.setLocation(null);
			}
			else
			{
				description.setLocation(projectPath);
			}
		}

		// adds the required natures to the project description
		if (!ArrayUtil.isEmpty(natureIds))
		{
			Set<String> natures = CollectionsUtil.newInOrderSet(natureIds);
			CollectionsUtil.addToSet(natures, description.getNatureIds());
			description.setNatureIds(natures.toArray(new String[natures.size()]));
		}

		// adds the required builders to the project description
		if (!ArrayUtil.isEmpty(builderIds))
		{
			ICommand[] existingBuilders = description.getBuildSpec();
			List<ICommand> builders = CollectionsUtil.newList(existingBuilders);
			for (String builderId : builderIds)
			{
				if (!hasBuilder(builderId, existingBuilders))
				{
					ICommand newBuilder = description.newCommand();
					newBuilder.setBuilderName(builderId);
					builders.add(newBuilder);
				}
			}
			description.setBuildSpec(builders.toArray(new ICommand[builders.size()]));
		}

		return description;
	}

	private static boolean hasBuilder(String builderName, ICommand[] builders)
	{
		if (StringUtil.isEmpty(builderName))
		{
			return false;
		}
		for (ICommand builder : builders)
		{
			if (builderName.equals(builder.getBuilderName()))
			{
				return true;
			}
		}
		return false;
	}

	/**
	 * Returns <code>true</code> if the given project is not <code>null</code> and is accessible.
	 * 
	 * @param project
	 * @return <code>true</code> if accessible; <code>false</code> otherwise.
	 * @see IProject#isAccessible()
	 */
	public static boolean isAccessible(IProject project)
	{
		return project != null && project.isAccessible();
	}
}
