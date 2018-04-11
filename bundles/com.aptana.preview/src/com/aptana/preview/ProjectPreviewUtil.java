/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.preview;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;

import com.aptana.webserver.core.IServer;
import com.aptana.webserver.core.WebServerCorePlugin;

public class ProjectPreviewUtil
{

	public static IServer getServerConfiguration(IProject project)
	{
		if (project != null)
		{
			try
			{
				String name = project.getPersistentProperty(IPreviewConstants.PROJECT_PREVIEW_SERVER);
				if (name != null)
				{
					return WebServerCorePlugin.getDefault().getServerManager().findServerByName(name);
				}
			}
			catch (CoreException e)
			{
			}
		}
		return null;
	}

	public static void setServerConfiguration(IProject project, IServer serverConfig)
	{
		if (project != null)
		{
			try
			{
				if (serverConfig == null)
				{
					project.setPersistentProperty(IPreviewConstants.PROJECT_PREVIEW_SERVER, null);
				}
				else
				{
					project.setPersistentProperty(IPreviewConstants.PROJECT_PREVIEW_SERVER, serverConfig.getName());
				}
			}
			catch (CoreException e)
			{
			}
		}
	}
}
