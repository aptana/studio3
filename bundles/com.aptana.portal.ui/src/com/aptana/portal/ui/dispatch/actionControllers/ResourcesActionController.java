/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.portal.ui.dispatch.actionControllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;

import com.aptana.configurations.processor.ConfigurationStatus;
import com.aptana.jetty.util.epl.ajax.JSON;

/**
 * A action controller for resource related actions.
 * 
 * @author Shalom Gibly <sgibly@appcelerator.com>
 */
public class ResourcesActionController extends AbstractActionController
{
	// ############## Actions ###############
	/**
	 * Returns an array representation of the accessible projects names in the workspace.
	 * 
	 * @return A JSON array representation for the project names.
	 */
	@ControllerAction
	public Object getProjects()
	{
		IProject[] projects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
		List<String> names = new ArrayList<String>(projects.length);
		for (IProject project : projects)
		{
			if (project.isAccessible())
			{
				names.add(project.getName());
			}
		}
		return JSON.toString(names.toArray(new String[names.size()]));
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.configurations.processor.IConfigurationProcessorListener#configurationStateChanged(com.aptana.
	 * configurations.processor.ConfigurationStatus, java.util.Set)
	 */
	public void configurationStateChanged(ConfigurationStatus status, Set<String> attributesChanged)
	{
		// Nothing to do here
	}
}
