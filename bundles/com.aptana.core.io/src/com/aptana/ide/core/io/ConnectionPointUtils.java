/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */

package com.aptana.ide.core.io;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;

import com.aptana.core.io.efs.EFSUtils;

/**
 * @author Max Stepanov
 */
public final class ConnectionPointUtils
{

	/**
	 * 
	 */
	private ConnectionPointUtils()
	{
	}

	public static IConnectionPoint findConnectionPoint(URI uri)
	{
		for (IConnectionPoint i : CoreIOPlugin.getConnectionPointManager().getConnectionPoints())
		{
			if (uri.equals(i.getRootURI()))
			{
				return i;
			}
		}
		return null;
	}

	public static IConnectionPoint[] getRemoteConnectionPoints()
	{
		List<IConnectionPoint> list = new ArrayList<IConnectionPoint>();
		for (IConnectionPoint i : CoreIOPlugin.getConnectionPointManager().getConnectionPoints())
		{
			if (isRemote(i))
			{
				list.add(i);
			}
		}
		return list.toArray(new IConnectionPoint[list.size()]);
	}

	public static boolean isLocal(IConnectionPoint connectionPoint)
	{
		return connectionPoint instanceof LocalConnectionPoint;
	}

	public static boolean isWorkspace(IConnectionPoint connectionPoint)
	{
		return connectionPoint instanceof WorkspaceConnectionPoint;
	}

	public static boolean isRemote(IConnectionPoint connectionPoint)
	{
		return connectionPoint instanceof IBaseRemoteConnectionPoint;
	}

	private static IConnectionPoint createLocalConnectionPoint(IPath path)
	{
		LocalConnectionPoint connectionPoint = new LocalConnectionPoint(path);
		connectionPoint.setName(path.toPortableString());
		return connectionPoint;
	}

	private static IConnectionPoint createWorkspaceConnectionPoint(IContainer container)
	{
		WorkspaceConnectionPoint connectionPoint = new WorkspaceConnectionPoint(container);
		connectionPoint.setName((container instanceof IProject) ? container.getName() : container.getFullPath()
				.toPortableString());
		return connectionPoint;
	}

	public static IConnectionPoint findOrCreateLocalConnectionPoint(IPath path)
	{
		IConnectionPoint connectionPoint = findConnectionPoint(EFSUtils.getLocalFileStore(path.toFile()).toURI());
		if (connectionPoint == null)
		{
			connectionPoint = ConnectionPointUtils.createLocalConnectionPoint(path);
		}
		return connectionPoint;
	}

	public static IConnectionPoint findOrCreateWorkspaceConnectionPoint(IContainer container)
	{
		IConnectionPoint connectionPoint = findConnectionPoint(EFSUtils.getFileStore(container).toURI());
		if (connectionPoint == null)
		{
			connectionPoint = ConnectionPointUtils.createWorkspaceConnectionPoint(container);
		}
		return connectionPoint;
	}

	/**
	 * Returns whether a connection point with the same name already exists
	 * 
	 * @param pointName
	 * @return whether a connection point with the same name already exists
	 */
	public static boolean isConnectionPointNameUnique(String pointName)
	{
		IConnectionPoint[] connectionPoints = CoreIOPlugin.getConnectionPointManager().getConnectionPoints();
		for (IConnectionPoint point : connectionPoints)
		{
			if (point.getName().equalsIgnoreCase(pointName))
			{
				return false;
			}
		}

		return true;
	}
}
