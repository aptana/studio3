/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */

package com.aptana.ide.syncing.core;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;

import com.aptana.ide.core.io.ConnectionPointUtils;
import com.aptana.ide.core.io.IConnectionPoint;

/**
 * @author Max Stepanov
 * @author Michael Xia
 */
public final class SiteConnectionUtils
{

	/**
	 * 
	 */
	private SiteConnectionUtils()
	{
	}

	/**
	 * Creates a new site connection with specific source and destination.
	 * 
	 * @param name
	 *            the name of the site connection
	 * @param source
	 *            the source connection point
	 * @param destination
	 *            the destination connection point
	 * @return the site connection
	 */
	public static ISiteConnection createSite(String name, IConnectionPoint source, IConnectionPoint destination)
	{
		ISiteConnection site = SiteConnectionManager.getInstance().createSiteConnection();
		site.setName(name);
		site.setSource(source);
		site.setDestination(destination);

		return site;
	}

	/**
	 * Retrieves a list of all available sites that have the object as the source (i.e. an IContainer or
	 * FilesystemObject).
	 * 
	 * @param object
	 *            the source object
	 * @return the list as an array
	 */
	public static ISiteConnection[] findSitesForSource(IAdaptable object)
	{
		return findSitesForSource(object, false);
	}

	/**
	 * Retrieves a list of all available sites that have the object as the source (i.e. an IContainer or
	 * FilesystemObject).
	 * 
	 * @param object
	 *            the source object
	 * @param strict
	 *            true if only to get the exact matches, false if the parent folder is allowed
	 * @return the list as an array
	 */
	public static ISiteConnection[] findSitesForSource(IAdaptable object, boolean strict)
	{
		return findSitesForSource(object, strict, false);
	}

	/**
	 * Retrieves a list of all available sites that have the object as the source (i.e. an IContainer or
	 * FilesystemObject).
	 * 
	 * @param object
	 *            the source object
	 * @param strict
	 *            true if only to get the exact matches, false if the parent folder is allowed
	 * @param includeChildren
	 *            true if the child elements which are the sources of any site connection should be included, false
	 *            otherwise
	 * @return the list as an array
	 */
	public static ISiteConnection[] findSitesForSource(IAdaptable object, boolean strict, boolean includeChildren)
	{
		List<ISiteConnection> list = new ArrayList<ISiteConnection>();
		ISiteConnection[] allsites = SyncingPlugin.getSiteConnectionManager().getSiteConnections();

		IConnectionPoint connectionPoint = (IConnectionPoint) object.getAdapter(IConnectionPoint.class);
		if (connectionPoint != null)
		{
			for (ISiteConnection i : allsites)
			{
				if (connectionPoint.equals(i.getSource()))
				{
					list.add(i);
				}
			}
		}
		else
		{
			IResource resource = (IResource) object.getAdapter(IResource.class);
			if (resource != null)
			{
				for (ISiteConnection i : allsites)
				{
					IConnectionPoint sourceConnectionPoint = i.getSource();
					if (sourceConnectionPoint != null)
					{
						IContainer connectionRoot = (IContainer) sourceConnectionPoint.getAdapter(IResource.class);
						if (connectionRoot != null)
						{
							if (connectionRoot.equals(resource) || (!strict && contains(connectionRoot, resource))
									|| (includeChildren && contains(resource, connectionRoot)))
							{
								IConnectionPoint destination = i.getDestination();
								if (destination != null
										&& ConnectionPointUtils.findConnectionPoint(destination.getRootURI()) != null)
								{
									list.add(i);
								}
							}
						}
					}
				}
			}
			else
			{
				IFileStore fileStore = (IFileStore) object.getAdapter(IFileStore.class);
				if (fileStore != null)
				{
					for (ISiteConnection i : allsites)
					{
						IConnectionPoint sourceConnectionPoint = i.getSource();
						if (sourceConnectionPoint != null)
						{
							try
							{
								IFileStore root = sourceConnectionPoint.getRoot();
								if (root != null)
								{
									if (root.equals(fileStore) || (!strict && root.isParentOf(fileStore))
											|| (includeChildren && fileStore.isParentOf(root)))
									{
										IConnectionPoint destination = i.getDestination();
										if (destination != null
												&& ConnectionPointUtils.findConnectionPoint(destination.getRootURI()) != null)
										{
											list.add(i);
										}
									}
								}
							}
							catch (CoreException ignore)
							{
							}
						}
					}
				}
			}
		}
		return list.toArray(new ISiteConnection[list.size()]);
	}

	/**
	 * Retrieves a list of all available sites that have the connection point as the destination.
	 * 
	 * @param destination
	 *            the connection point
	 * @return the list as an array
	 */
	public static ISiteConnection[] findSitesWithDestination(IAdaptable object)
	{
		return findSitesWithDestination(object, false);
	}

	/**
	 * Retrieves a list of all available sites that have the object as the destination.
	 * 
	 * @param object
	 *            the source object
	 * @param strict
	 *            true if only to get the exact matches, false if the parent folder is allowed
	 * @return the list as an array
	 */
	public static ISiteConnection[] findSitesWithDestination(IAdaptable object, boolean strict)
	{
		List<ISiteConnection> list = new ArrayList<ISiteConnection>();
		ISiteConnection[] allsites = SyncingPlugin.getSiteConnectionManager().getSiteConnections();

		IConnectionPoint connectionPoint = (IConnectionPoint) object.getAdapter(IConnectionPoint.class);
		if (connectionPoint != null)
		{
			for (ISiteConnection i : allsites)
			{
				if (connectionPoint.equals(i.getDestination()))
				{
					list.add(i);
				}
			}
		}
		else
		{
			IResource resource = (IResource) object.getAdapter(IResource.class);
			if (resource != null)
			{
				for (ISiteConnection i : allsites)
				{
					IConnectionPoint destinationConnectionPoint = i.getDestination();
					if (destinationConnectionPoint != null)
					{
						IContainer connectionRoot = (IContainer) destinationConnectionPoint.getAdapter(IResource.class);
						if (connectionRoot != null)
						{
							if (connectionRoot.equals(resource) || (!strict && contains(connectionRoot, resource)))
							{
								list.add(i);
							}
						}
					}
				}
			}
			else
			{
				IFileStore fileStore = (IFileStore) object.getAdapter(IFileStore.class);
				if (fileStore != null)
				{
					for (ISiteConnection i : allsites)
					{
						IConnectionPoint destinationConnectionPoint = i.getDestination();
						if (destinationConnectionPoint != null)
						{
							try
							{
								IFileStore root = destinationConnectionPoint.getRoot();
								if (root != null)
								{
									if (root.equals(fileStore) || (!strict && root.isParentOf(fileStore)))
									{
										list.add(i);
									}
								}
							}
							catch (CoreException ignore)
							{
							}
						}
					}
				}
			}
		}
		return list.toArray(new ISiteConnection[list.size()]);
	}

	/**
	 * Retrieves a list of all available sites that have the specific source and destination.
	 * 
	 * @param source
	 *            the source object
	 * @param destination
	 *            the connection point as destination
	 * @return the list as an array
	 */
	public static ISiteConnection[] findSites(IAdaptable source, IConnectionPoint destination)
	{
		List<ISiteConnection> list = new ArrayList<ISiteConnection>();
		ISiteConnection[] sites = findSitesForSource(source, true);
		for (ISiteConnection site : sites)
		{
			if (site.getDestination() == destination)
			{
				list.add(site);
			}
		}
		return list.toArray(new ISiteConnection[list.size()]);
	}

	public static ISiteConnection getSiteWithDestination(String destinationName, ISiteConnection[] sites)
	{
		IConnectionPoint destination;
		for (ISiteConnection site : sites)
		{
			destination = site.getDestination();
			if (destination != null && destination.getName().equals(destinationName))
			{
				return site;
			}
		}
		return null;
	}

	private static boolean contains(IResource container, IResource resource)
	{
		return container.getFullPath().isPrefixOf(resource.getFullPath());
	}

	/**
	 * Returns the uniqueness of the site name
	 * 
	 * @param siteName
	 * @return whether or not the name is unique among current sites
	 */
	public static boolean isSiteNameUnique(String siteName)
	{
		ISiteConnection[] siteConnections = SyncingPlugin.getSiteConnectionManager().getSiteConnections();
		for (ISiteConnection connection : siteConnections)
		{
			if (connection.getName().equalsIgnoreCase(siteName))
			{
				return false;
			}
		}
		return true;
	}
}
