/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.deploy.ftp;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;

import com.aptana.deploy.IDeployProvider;
import com.aptana.deploy.preferences.DeployPreferenceUtil;
import com.aptana.ide.syncing.core.ISiteConnection;
import com.aptana.ide.syncing.core.ResourceSynchronizationUtils;
import com.aptana.ide.syncing.core.SiteConnectionUtils;
import com.aptana.ide.syncing.ui.actions.SynchronizeProjectAction;
import com.aptana.ui.util.UIUtils;

public class FTPDeployProvider implements IDeployProvider
{

	public static final String ID = "com.aptana.deploy.ftp.provider"; //$NON-NLS-1$

	public void deploy(IProject selectedProject, IProgressMonitor monitor)
	{
		SynchronizeProjectAction action = new SynchronizeProjectAction();
		action.setActivePart(null, UIUtils.getActivePart());
		action.setSelection(UIUtils.getActiveWorkbenchWindow().getSelectionService().getSelection());
		ISiteConnection[] sites = SiteConnectionUtils.findSitesForSource(selectedProject, true);
		if (sites.length > 1)
		{
			String lastConnection = ResourceSynchronizationUtils.getLastSyncConnection(selectedProject);
			if (lastConnection == null)
			{
				lastConnection = DeployPreferenceUtil.getDeployEndpoint(selectedProject);
			}
			if (lastConnection != null)
			{
				action.setSelectedSite(SiteConnectionUtils.getSiteWithDestination(lastConnection, sites));
			}
		}
		action.run(null);
	}

	public boolean handles(IProject selectedProject)
	{
		ISiteConnection[] siteConnections = SiteConnectionUtils.findSitesForSource(selectedProject, true);
		return siteConnections.length > 0;
	}

}
