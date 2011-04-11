package com.aptana.deploy.ftp;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.PlatformUI;

import com.aptana.deploy.IDeployProvider;
import com.aptana.deploy.preferences.DeployPreferenceUtil;
import com.aptana.ide.syncing.core.ISiteConnection;
import com.aptana.ide.syncing.core.ResourceSynchronizationUtils;
import com.aptana.ide.syncing.core.SiteConnectionUtils;
import com.aptana.ide.syncing.ui.actions.SynchronizeProjectAction;

public class FTPDeployProvider implements IDeployProvider
{

	public static final String ID = "com.aptana.deploy.ftp.provider"; //$NON-NLS-1$

	public void deploy(IProject selectedProject, IProgressMonitor monitor)
	{
		SynchronizeProjectAction action = new SynchronizeProjectAction();
		action.setActivePart(null, PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActivePart());
		action.setSelection(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getSelectionService().getSelection());
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
