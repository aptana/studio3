/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.deploy.ftp.internal.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.expressions.IEvaluationContext;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IResource;

import com.aptana.deploy.ftp.ui.wizard.FTPDeployPropertyDialog;
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
		IResource selectedResource = UIUtils.getSelectedResource((IEvaluationContext) event.getApplicationContext());
		if (selectedResource == null)
		{
			return null;
		}
		IContainer container;
		if (selectedResource instanceof IContainer)
		{
			container = (IContainer) selectedResource;
		}
		else
		{
			container = selectedResource.getParent();
		}

		FTPDeployPropertyDialog settingsDialog = new FTPDeployPropertyDialog(UIUtils.getActiveShell());
		settingsDialog.setProject(container.getProject());

		ISiteConnection[] siteConnections = SiteConnectionUtils.findSitesForSource(container);
		ISiteConnection lastSiteConnection = null;
		if (siteConnections.length > 1)
		{
			String lastConnection = null;
			// try for last remembered site first
			if (ResourceSynchronizationUtils.isRememberDecision(container))
			{
				lastConnection = ResourceSynchronizationUtils.getLastSyncConnection(container);
			}
			if (lastConnection == null)
			{
				lastConnection = DeployPreferenceUtil.getDeployEndpoint(container);
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
			ChooseSiteConnectionDialog dialog = new ChooseSiteConnectionDialog(UIUtils.getActiveShell(),
					siteConnections);
			dialog.setShowRememberMyDecision(true);
			dialog.open();

			IConnectionPoint destination = null;
			ISiteConnection site = dialog.getSelectedSite();
			if (site != null)
			{
				ResourceSynchronizationUtils.setRememberDecision(container, dialog.isRememberMyDecision());

				destination = site.getDestination();
				if (destination != null)
				{
					// remembers the last sync connection
					ResourceSynchronizationUtils.setLastSyncConnection(container, destination.getName());
				}
			}
			settingsDialog.setPropertySource(destination);
		}
		settingsDialog.open();

		return null;
	}
}
