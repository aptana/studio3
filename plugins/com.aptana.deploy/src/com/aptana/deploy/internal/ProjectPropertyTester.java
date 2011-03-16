/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.deploy.internal;

import org.eclipse.core.expressions.PropertyTester;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.Platform;

import com.aptana.deploy.preferences.DeployPreferenceUtil;
import com.aptana.deploy.preferences.IPreferenceConstants.DeployType;
import com.aptana.git.core.GitPlugin;
import com.aptana.git.core.model.GitRepository;
import com.aptana.ide.core.io.IBaseRemoteConnectionPoint;
import com.aptana.ide.syncing.core.ISiteConnection;
import com.aptana.ide.syncing.core.SiteConnectionUtils;

public class ProjectPropertyTester extends PropertyTester
{

	private static final String DEPLOY_TYPE = "isDeployType"; //$NON-NLS-1$
	private static final String TYPE_CAP = "cap"; //$NON-NLS-1$
	private static final String TYPE_HEROKU = "heroku"; //$NON-NLS-1$
	private static final String TYPE_FTP = "ftp"; //$NON-NLS-1$
	private static final String TYPE_ENGINEYARD = "engineyard"; //$NON-NLS-1$

	public boolean test(Object receiver, String property, Object[] args, Object expectedValue)
	{
		if (receiver instanceof IResource)
		{
			IProject project = ((IResource) receiver).getProject();
			if (DEPLOY_TYPE.equals(property))
			{
				if (TYPE_CAP.equals(expectedValue))
				{
					return isCapistranoProject(project);
				}
				if (TYPE_HEROKU.equals(expectedValue))
				{
					return isHerokuProject(project);
				}
				if (TYPE_FTP.equals(expectedValue))
				{
					return isFTPProject(project);
				}
				if (TYPE_ENGINEYARD.equals(expectedValue))
				{
					return isEngineYardProject(project);
				}
			}
		}
		return false;
	}

	private static boolean isCapistranoProject(IProject project)
	{
		return project.getFile("Capfile").exists(); //$NON-NLS-1$
	}

	private static boolean isHerokuProject(IProject project)
	{
		GitRepository repo = GitPlugin.getDefault().getGitRepositoryManager().getAttached(project);
		if (repo != null)
		{
			for (String remote : repo.remotes())
			{
				if (remote.indexOf("heroku") != -1) //$NON-NLS-1$
				{
					return true;
				}
			}
			for (String remoteURL : repo.remoteURLs())
			{
				if (remoteURL.indexOf("heroku.com") != -1) //$NON-NLS-1$
				{
					return true;
				}
			}
		}
		return false;
	}

	private static boolean isFTPProject(IProject project)
	{
		ISiteConnection[] siteConnections = SiteConnectionUtils.findSitesForSource(project);
		for (ISiteConnection site : siteConnections)
		{
			if (site.getDestination() instanceof IBaseRemoteConnectionPoint)
			{
				return true;
			}
		}
		return false;
	}

	private static boolean isEngineYardProject(IProject project)
	{
		// Engine Yard gem does not work in Windows
		if (!Platform.OS_WIN32.equals(Platform.getOS()))
		{
			DeployType type = DeployPreferenceUtil.getDeployType(project);
			return DeployType.ENGINEYARD.equals(type);
		}
		return false;
	}
}
