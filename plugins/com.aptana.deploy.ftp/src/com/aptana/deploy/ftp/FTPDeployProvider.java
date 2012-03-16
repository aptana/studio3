/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.deploy.ftp;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.viewers.StructuredSelection;

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

	public void deploy(IContainer selectedContainer, IProgressMonitor monitor)
	{
		SynchronizeProjectAction action = new SynchronizeProjectAction();
		action.setActivePart(null, UIUtils.getActivePart());
		action.setSelection(new StructuredSelection(selectedContainer));
		ISiteConnection[] sites = SiteConnectionUtils.findSitesForSource(selectedContainer, true);
		if (sites.length > 1)
		{
			String lastConnection = ResourceSynchronizationUtils.getLastSyncConnection(selectedContainer);
			if (lastConnection == null)
			{
				lastConnection = DeployPreferenceUtil.getDeployEndpoint(selectedContainer);
			}
			if (lastConnection != null)
			{
				action.setSelectedSite(SiteConnectionUtils.getSiteWithDestination(lastConnection, sites));
			}
		}
		action.run(null);
	}

	public boolean handles(IContainer selectedContainer)
	{
		ISiteConnection[] siteConnections = SiteConnectionUtils.findSitesForSource(selectedContainer, false);
		return siteConnections.length > 0;
	}

	public String getDeployMenuName()
	{
		return Messages.FTPDeployProvider_DeployMenuName;
	}
}
