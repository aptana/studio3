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
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.osgi.service.prefs.BackingStoreException;

import com.aptana.deploy.DeployPlugin;
import com.aptana.deploy.preferences.IPreferenceConstants.DeployType;

public class DeployPreferenceUtil
{

	/**
	 * @deprecated Please only use for compatibility layer internal to this plugin!
	 * @param project
	 * @return
	 */
	public static DeployType getDeployType(IProject project)
	{
		String type = Platform.getPreferencesService().getString(DeployPlugin.getPluginIdentifier(),
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
			if (type.equals(DeployType.RED_HAT.toString()))
			{
				return DeployType.RED_HAT;
			}
		}
		return DeployType.NONE;
	}

	public static String getDeployEndpoint(IProject project)
	{
		return Platform.getPreferencesService().getString(DeployPlugin.getPluginIdentifier(),
				MessageFormat.format("{0}:{1}", //$NON-NLS-1$
						com.aptana.deploy.preferences.IPreferenceConstants.PROJECT_DEPLOY_ENDPOINT, project.getName()),
				null, null);
	}

	public static String getDeployProviderId(IProject project)
	{
		String id = null;
		try
		{
			id = project.getPersistentProperty(new QualifiedName(DeployPlugin.getPluginIdentifier(), "provider")); //$NON-NLS-1$
			if (id == null)
			{
				// Add a compatibility layer with old stuff here
				id = mapTypeToId(getDeployType(project));
			}
		}
		catch (CoreException e)
		{
			DeployPlugin.logError(e);
		}
		return id;
	}

	/**
	 * Compatibility layer to help migrate projects set up old way.
	 * 
	 * @param type
	 * @return
	 */
	private static String mapTypeToId(DeployType type)
	{
		if (type == null)
		{
			return null;
		}
		switch (type)
		{
			case RED_HAT:
				return "com.aptana.deploy.redhat.provider"; //$NON-NLS-1$
			case HEROKU:
				return "com.aptana.deploy.heroku.provider"; //$NON-NLS-1$
			case ENGINEYARD:
				return "com.aptana.deploy.engineyard.provider"; //$NON-NLS-1$
			case FTP:
				return "com.aptana.deploy.ftp.provider"; //$NON-NLS-1$
			case CAPISTRANO:
				return "com.aptana.deploy.capistrano.provider"; //$NON-NLS-1$
			default:
				break;
		}
		return null;
	}

	public static void setDeployType(IProject project, String providerId)
	{
		try
		{
			project.setPersistentProperty(new QualifiedName(DeployPlugin.getPluginIdentifier(), "provider"), providerId); //$NON-NLS-1$
		}
		catch (CoreException e1)
		{
			DeployPlugin.logError(e1);
		}
	}

	public static void setDeployEndpoint(IProject project, String endpoint)
	{
		IEclipsePreferences prefs = (new InstanceScope()).getNode(DeployPlugin.getPluginIdentifier());
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
