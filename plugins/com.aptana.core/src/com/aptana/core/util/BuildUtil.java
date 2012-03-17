/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.core.util;

import java.lang.reflect.Method;
import java.util.Map;

import org.eclipse.core.internal.events.BuildManager;
import org.eclipse.core.internal.resources.Workspace;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.osgi.framework.Bundle;

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
	private static final String BUILD_METHOD_NAME = "build"; //$NON-NLS-1$
	private static final String BUILD_CONFIGURATION_37_CLASS_NAME = "org.eclipse.core.internal.resources.BuildConfiguration"; //$NON-NLS-1$
	private static Class buildConfigurationClass;
	static
	{
		try
		{
			Bundle b = ResourcesPlugin.getPlugin().getBundle();
			buildConfigurationClass = b.loadClass(BUILD_CONFIGURATION_37_CLASS_NAME);
		}
		catch (Throwable t)
		{
			// ignore
		}
	}

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
			if (buildConfigurationClass == null)
			{
				return syncBuild36(project, kind, builderName, args, monitor);
			}
			else
			{
				return syncBuild37(project, kind, builderName, args, monitor);
			}
		}
		catch (Exception e)
		{
			return new Status(IStatus.ERROR, CorePlugin.PLUGIN_ID, "Error while invoking a synchronous builder", e); //$NON-NLS-1$
		}
	}

	/**
	 * Sync build on an Eclipse 3.6
	 * 
	 * @param project
	 * @param kind
	 * @param builderName
	 * @param args
	 * @param monitor
	 * @throws Exception
	 */
	private static IStatus syncBuild36(IProject project, int kind, String builderName, Map args,
			IProgressMonitor monitor) throws Exception
	{
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		BuildManager buildManager = ((Workspace) workspace).getBuildManager();
		Method buildMethod = buildManager.getClass().getMethod(BUILD_METHOD_NAME, IProject.class, Integer.TYPE,
				String.class, Map.class, IProgressMonitor.class);
		return (IStatus) buildMethod.invoke(buildManager, project, kind, builderName, args, monitor);
	}

	/**
	 * Sync build on an Eclipse 3.7
	 * 
	 * @param project
	 * @param kind
	 * @param builderName
	 * @param args
	 * @param monitor
	 * @throws Exception
	 */
	private static IStatus syncBuild37(IProject project, int kind, String builderName, Map args,
			IProgressMonitor monitor) throws Exception
	{
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		BuildManager buildManager = ((Workspace) workspace).getBuildManager();
		Method buildMethod = buildManager.getClass().getMethod(BUILD_METHOD_NAME, buildConfigurationClass,
				Integer.TYPE, String.class, Map.class, IProgressMonitor.class);
		Object buildConfigurationInstance = buildConfigurationClass.getConstructor(IProject.class).newInstance(project);
		return (IStatus) buildMethod.invoke(buildManager, buildConfigurationInstance, kind, builderName, args, monitor);
	}

	/**
	 * Asynchronous run of a builder with the given name on the given project.
	 * 
	 * @param project
	 * @param kind
	 *            See {@link IncrementalProjectBuilder} for the available types.
	 * @param builderName
	 * @param args
	 * @param monitor
	 * @throws CoreException
	 */
	public static void asyncBuild(IProject project, int kind, String builderName, Map args, IProgressMonitor monitor)
			throws CoreException
	{
		project.build(kind, builderName, args, monitor);
	}
}
