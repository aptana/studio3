/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.ide.syncing.ui.navigator;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IProject;

import com.aptana.ide.core.io.CoreIOPlugin;
import com.aptana.ide.core.io.IConnectionPoint;
import com.aptana.ide.core.io.events.ConnectionPointEvent;
import com.aptana.ide.core.io.events.IConnectionPointListener;
import com.aptana.ide.ui.io.IOUIPlugin;

/**
 * @author Michael Xia (mxia@aptana.com)
 */
public class ProjectSitesManager
{

	private static ProjectSitesManager fInstance;

	private Map<IProject, ProjectSiteConnections> fProjects;

	private IConnectionPointListener fListener = new IConnectionPointListener()
	{

		public void connectionPointChanged(ConnectionPointEvent event)
		{
			IConnectionPoint destConnection = event.getConnectionPoint();

			switch (event.getKind())
			{
				case ConnectionPointEvent.POST_CHANGE:
					// refreshes the project connection node that contains the
					// connection point that was modified
					Collection<ProjectSiteConnections> projectConnections = fProjects.values();
					ProjectSiteConnection[] connections;
					IConnectionPoint connectionPoint;
					for (ProjectSiteConnections sites : projectConnections)
					{
						connections = (ProjectSiteConnection[]) sites.getChildren(null);
						for (ProjectSiteConnection projectConnection : connections)
						{
							connectionPoint = (IConnectionPoint) projectConnection.getAdapter(IConnectionPoint.class);
							if (connectionPoint == destConnection)
							{
								IOUIPlugin.refreshNavigatorView(projectConnection);
							}
						}
					}
			}
		}
	};

	public static ProjectSitesManager getInstance()
	{
		if (fInstance == null)
		{
			fInstance = new ProjectSitesManager();
		}
		return fInstance;
	}

	public ProjectSiteConnections getProjectSites(IProject project)
	{
		ProjectSiteConnections sites = fProjects.get(project);
		if (sites == null)
		{
			sites = new ProjectSiteConnections(project);
			fProjects.put(project, sites);
		}
		return sites;
	}

	private ProjectSitesManager()
	{
		fProjects = new HashMap<IProject, ProjectSiteConnections>();
		CoreIOPlugin.getConnectionPointManager().addConnectionPointListener(fListener);
	}
}
