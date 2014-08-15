/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.deploy.preferences;

import java.text.MessageFormat;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.osgi.service.prefs.BackingStoreException;

import com.aptana.core.logging.IdeLog;
import com.aptana.deploy.DeployPlugin;
import com.aptana.deploy.internal.ProjectPropertyTester;
import com.aptana.deploy.preferences.IPreferenceConstants.DeployType;

public class DeployPreferenceUtil
{

	// Note: extracting these constants made a huge difference when just switching editors.
	// (around 98% of the time it was in com.aptana.deploy.preferences.DeployPreferenceUtil.getDeployProviderId
	// as it's called a bazzilion times from com.aptana.deploy.internal.ProjectPropertyTester.test)
	private static final String DEPLOY_PLUGIN_IDENTIFIER = DeployPlugin.getPluginIdentifier();
	private static final QualifiedName DEPLOY_TYPE_QUALIFIED_KEY = new QualifiedName(DEPLOY_PLUGIN_IDENTIFIER,
			"provider"); //$NON-NLS-1$
	private static final String RED_HAT_STRING = DeployType.RED_HAT.toString();
	private static final String ENGINEYARD_STRING = DeployType.ENGINEYARD.toString();
	private static final String CAPISTRANO_STRING = DeployType.CAPISTRANO.toString();
	private static final String FTP_STRING = DeployType.FTP.toString();
	private static final String HEROKU_STRING = DeployType.HEROKU.toString();

	private static IPreferencesService preferencesService = Platform.getPreferencesService();

	/**
	 * Should only use for compatibility.
	 * 
	 * @param project
	 * @return
	 */
	private static DeployType getDeployType(IProject project)
	{
		if (project == null)
		{
			return DeployType.NONE;
		}
		String projectName = project.getName();
		String key = new StringBuilder(IPreferenceConstants.PROJECT_DEPLOY_TYPE.length() + projectName.length() + 2)
				.append(IPreferenceConstants.PROJECT_DEPLOY_TYPE).append(':').append(projectName).toString();
		String type = preferencesService.getString(DEPLOY_PLUGIN_IDENTIFIER, key, null, null);
		if (type != null)
		{
			if (type.equals(HEROKU_STRING))
			{
				return DeployType.HEROKU;
			}
			if (type.equals(FTP_STRING))
			{
				return DeployType.FTP;
			}
			if (type.equals(CAPISTRANO_STRING))
			{
				return DeployType.CAPISTRANO;
			}
			if (type.equals(ENGINEYARD_STRING))
			{
				return DeployType.ENGINEYARD;
			}
			if (type.equals(RED_HAT_STRING))
			{
				return DeployType.RED_HAT;
			}
		}
		return DeployType.NONE;
	}

	public static String getDeployEndpoint(IContainer container)
	{
		if (container == null)
		{
			return null;
		}
		return Platform.getPreferencesService().getString(DEPLOY_PLUGIN_IDENTIFIER, MessageFormat.format("{0}:{1}", //$NON-NLS-1$
				com.aptana.deploy.preferences.IPreferenceConstants.PROJECT_DEPLOY_ENDPOINT, container.getFullPath()),
				null, null);
	}

	/**
	 * Note: this method MUST be fast (it's called over and over again when switching an editor).
	 */
	public static String getDeployProviderId(IContainer container)
	{
		if (container == null)
		{
			return null;
		}
		String id = null;
		try
		{
			id = container.getProject().getPersistentProperty(DEPLOY_TYPE_QUALIFIED_KEY);
			if (id == null)
			{
				// Add a compatibility layer with old stuff here
				id = mapTypeToId(getDeployType(container.getProject()));
			}
		}
		catch (Exception e)
		{
			IdeLog.logWarning(DeployPlugin.getDefault(), e);
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

	public static void setDeployType(IContainer container, String providerId)
	{
		try
		{
			container.getProject().setPersistentProperty(DEPLOY_TYPE_QUALIFIED_KEY, providerId);
			ProjectPropertyTester.resetCache(container);
		}
		catch (CoreException e)
		{
			IdeLog.logError(DeployPlugin.getDefault(), e);
		}
	}

	public static void setDeployEndpoint(IContainer container, String endpoint)
	{
		IEclipsePreferences prefs = InstanceScope.INSTANCE.getNode(DEPLOY_PLUGIN_IDENTIFIER);
		prefs.put(
				MessageFormat.format("{0}:{1}", IPreferenceConstants.PROJECT_DEPLOY_ENDPOINT, container.getFullPath()), //$NON-NLS-1$
				endpoint);
		try
		{
			prefs.flush();
		}
		catch (BackingStoreException e)
		{
			IdeLog.logError(DeployPlugin.getDefault(), e);
		}
	}
}
