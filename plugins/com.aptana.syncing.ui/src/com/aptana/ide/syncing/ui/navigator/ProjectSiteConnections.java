/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.ide.syncing.ui.navigator;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.PlatformObject;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.model.IWorkbenchAdapter;

import com.aptana.ide.syncing.core.ISiteConnection;
import com.aptana.ide.syncing.core.SiteConnectionUtils;
import com.aptana.ide.syncing.ui.SyncingUIPlugin;

/**
 * Contains a list of available sites that have the specific project as the source.
 * 
 * @author Michael Xia (mxia@aptana.com)
 */
public class ProjectSiteConnections extends PlatformObject implements IWorkbenchAdapter
{

	private static ImageDescriptor IMAGE_DESCRIPTOR = SyncingUIPlugin
			.getImageDescriptor("icons/full/obj16/connection.png"); //$NON-NLS-1$

	private IProject fProject;

	public ProjectSiteConnections(IProject project)
	{
		fProject = project;
	}

	public Object[] getChildren(Object o)
	{
		ISiteConnection[] sites = SiteConnectionUtils.findSitesForSource(fProject, true, true);
		List<ProjectSiteConnection> targets = new ArrayList<ProjectSiteConnection>();
		for (ISiteConnection site : sites)
		{
			targets.add(new ProjectSiteConnection(fProject, site));
		}
		return targets.toArray(new ProjectSiteConnection[targets.size()]);
	}

	public ImageDescriptor getImageDescriptor(Object object)
	{
		return IMAGE_DESCRIPTOR;
	}

	public String getLabel(Object o)
	{
		return Messages.ProjectSiteConnections_Name;
	}

	public Object getParent(Object o)
	{
		return null;
	}

	@SuppressWarnings("rawtypes")
	public Object getAdapter(Class adapter)
	{
		if (adapter == IProject.class || adapter == IContainer.class)
		{
			return fProject;
		}
		return super.getAdapter(adapter);
	}

	@Override
	public String toString()
	{
		return Messages.ProjectSiteConnections_Name;
	}
}
