/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.deploy.preferences;

import java.text.MessageFormat;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.osgi.service.prefs.BackingStoreException;

import com.aptana.deploy.Activator;
import com.aptana.deploy.preferences.IPreferenceConstants.DeployType;

public class DeployPreferenceUtil
{

	public static DeployType getDeployType(IProject project)
	{
		String type = Platform.getPreferencesService().getString(Activator.getPluginIdentifier(),
				MessageFormat.format("{0}:{1}", IPreferenceConstants.PROJECT_DEPLOY_TYPE, project.getName()), null, //$NON-NLS-1$
				null);
		if (type != null)
		{
			if (type.equals(DeployType.HEROKU.toString()))
			{
				return DeployType.HEROKU;
			}
			if (type.equals(DeployType.FTP.toString()))
			{
				return DeployType.FTP;
			}
			if (type.equals(DeployType.CAPISTRANO.toString()))
			{
				return DeployType.CAPISTRANO;
			}
			if (type.equals(DeployType.ENGINEYARD.toString()))
			{
				return DeployType.ENGINEYARD;
			}
		}
		return null;
	}

	public static String getDeployEndpoint(IProject project)
	{
		return Platform.getPreferencesService().getString(Activator.getPluginIdentifier(),
				MessageFormat.format("{0}:{1}", //$NON-NLS-1$
						com.aptana.deploy.preferences.IPreferenceConstants.PROJECT_DEPLOY_ENDPOINT, project.getName()),
				null, null);
	}

	public static void setDeployType(IProject project, DeployType type)
	{
		IEclipsePreferences prefs = (new InstanceScope()).getNode(Activator.getPluginIdentifier());
		prefs.put(MessageFormat.format("{0}:{1}", IPreferenceConstants.PROJECT_DEPLOY_TYPE, project.getName()), //$NON-NLS-1$
				type.toString());
		try
		{
			prefs.flush();
		}
		catch (BackingStoreException e)
		{
		}
	}

	public static void setDeployEndpoint(IProject project, String endpoint)
	{
		IEclipsePreferences prefs = (new InstanceScope()).getNode(Activator.getPluginIdentifier());
		prefs.put(MessageFormat.format("{0}:{1}", IPreferenceConstants.PROJECT_DEPLOY_ENDPOINT, project.getName()), //$NON-NLS-1$
				endpoint);
		try
		{
			prefs.flush();
		}
		catch (BackingStoreException e)
		{
		}
	}
}
