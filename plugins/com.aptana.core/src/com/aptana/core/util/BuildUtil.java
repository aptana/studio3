/**
 * Aptana Studio
 * Copyright (c) 2005-2013 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.core.util;

import java.util.Map;

import org.eclipse.core.internal.events.BuildManager;
import org.eclipse.core.internal.resources.BuildConfiguration;
import org.eclipse.core.internal.resources.Workspace;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import com.aptana.core.CorePlugin;

/**
 * Build utilities class. <br>
 * The class allows synchronous and asynchronous calls for specific builders.
 * 
 * @author Shalom Gibly <sgibly@appcelerator.com>
 */
@SuppressWarnings({ "rawtypes", "restriction", "unchecked" })
public class BuildUtil
{

	/**
	 * Synchronous run of a builder with the given name on the given project.
	 * 
	 * @param project
	 * @param kind
	 *            See {@link IncrementalProjectBuilder} for the available types.
	 * @param builderName
	 * @param args
	 * @param monitor
	 * @return A status indicating if the build succeeded or failed
	 */
	public static IStatus syncBuild(IProject project, int kind, String builderName, Map args, IProgressMonitor monitor)
	{
		try
		{
			IWorkspace workspace = ResourcesPlugin.getWorkspace();
			BuildManager buildManager = ((Workspace) workspace).getBuildManager();
			return buildManager.build(new BuildConfiguration(project), kind, builderName, args, monitor);
		}
		catch (IllegalStateException e)
		{
			return new Status(IStatus.ERROR, CorePlugin.PLUGIN_ID, "Error while getting the Workspace", e); //$NON-NLS-1$
		}
	}
}
