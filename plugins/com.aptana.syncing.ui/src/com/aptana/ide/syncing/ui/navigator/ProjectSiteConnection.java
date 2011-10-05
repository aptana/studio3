/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.ide.syncing.ui.navigator;

import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.PlatformObject;

import com.aptana.ide.core.io.IConnectionPoint;
import com.aptana.ide.syncing.core.ISiteConnection;

/**
 * @author Michael Xia (mxia@aptana.com)
 */
public final class ProjectSiteConnection extends PlatformObject
{

	private final IProject project;
	private final ISiteConnection siteConnection;

	private int hashCode;

	public ProjectSiteConnection(IProject project, ISiteConnection siteConnection)
	{
		this.project = project;
		this.siteConnection = siteConnection;
	}

	public IProject getProject()
	{
		return project;
	}

	public ISiteConnection getSiteConnection()
	{
		return siteConnection;
	}

	public boolean canDisconnect()
	{
		IConnectionPoint connectionPoint = siteConnection.getDestination();
		return (connectionPoint == null) ? false : connectionPoint.canDisconnect();
	}

	@SuppressWarnings("rawtypes")
	public Object getAdapter(Class adapter)
	{
		if (adapter == IProject.class)
		{
			return project;
		}
		else if (adapter == ISiteConnection.class)
		{
			return siteConnection;
		}
		else if (adapter == IConnectionPoint.class)
		{
			return siteConnection.getDestination();
		}
		else if (adapter == IFileStore.class)
		{
			IConnectionPoint destination = siteConnection.getDestination();
			try
			{
				return (destination == null) ? null : destination.getRoot();
			}
			catch (CoreException e)
			{
				// falls through on error
			}
		}
		return super.getAdapter(adapter);
	}

	@Override
	public int hashCode()
	{
		if (hashCode == 0)
		{
			hashCode = 7;
			hashCode = 31 * hashCode + project.hashCode();
			hashCode = 31 * hashCode + siteConnection.hashCode();
		}
		return hashCode;
	}

	@Override
	public boolean equals(Object o)
	{
		if (!(o instanceof ProjectSiteConnection))
		{
			return false;
		}
		ProjectSiteConnection connection = (ProjectSiteConnection) o;
		return project == connection.project && siteConnection == connection.siteConnection;
	}

	@Override
	public String toString()
	{
		return getSiteConnection().toString();
	}
}
