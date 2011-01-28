/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.deploy.internal.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.ui.PlatformUI;

import com.aptana.deploy.dialogs.FTPDeployPropertyDialog;
import com.aptana.deploy.preferences.DeployPreferenceUtil;
import com.aptana.ide.core.io.IConnectionPoint;
import com.aptana.ide.syncing.core.ISiteConnection;
import com.aptana.ide.syncing.core.ResourceSynchronizationUtils;
import com.aptana.ide.syncing.core.SiteConnectionUtils;
import com.aptana.ide.syncing.ui.dialogs.ChooseSiteConnectionDialog;
import com.aptana.ui.util.UIUtils;

public class DeploySettingsHandler extends AbstractHandler
{

	public Object execute(ExecutionEvent event) throws ExecutionException
	{
		FTPDeployPropertyDialog settingsDialog = new FTPDeployPropertyDialog(PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getShell());
		IProject selectedProject = UIUtils.getSelectedProject();
		settingsDialog.setProject(selectedProject);

		ISiteConnection[] siteConnections = SiteConnectionUtils.findSitesForSource(selectedProject);
		ISiteConnection lastSiteConnection = null;
		if (siteConnections.length > 1)
		{
			// try for last remembered site first
			String lastConnection = ResourceSynchronizationUtils.getLastSyncConnection(selectedProject);
			if (lastConnection == null)
			{
				lastConnection = DeployPreferenceUtil.getDeployEndpoint(selectedProject);
			}
			if (lastConnection != null)
			{
				lastSiteConnection = SiteConnectionUtils.getSiteWithDestination(lastConnection, siteConnections);
			}
		}
		else if (siteConnections.length == 1)
		{
			lastSiteConnection = siteConnections[0];
		}

		if (lastSiteConnection != null)
		{
			settingsDialog.setPropertySource(lastSiteConnection.getDestination());
		}
		else if (siteConnections.length > 1)
		{
			ChooseSiteConnectionDialog dialog = new ChooseSiteConnectionDialog(PlatformUI.getWorkbench()
					.getActiveWorkbenchWindow().getShell(), siteConnections);
			dialog.setShowRememberMyDecision(true);
			dialog.open();

			IConnectionPoint destination = dialog.getSelectedSite().getDestination();
			if (destination != null)
			{
				Boolean rememberMyDecision = dialog.isRememberMyDecision();
				if (rememberMyDecision)
				{
					ResourceSynchronizationUtils.setRememberDecision(selectedProject, rememberMyDecision);
				}
				// remembers the last sync connection
				ResourceSynchronizationUtils.setLastSyncConnection(selectedProject, destination.getName());
			}
			settingsDialog.setPropertySource(destination);
		}
		settingsDialog.open();

		return null;
	}
}
