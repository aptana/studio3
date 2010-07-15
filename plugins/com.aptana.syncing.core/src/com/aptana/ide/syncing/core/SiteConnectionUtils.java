/**
 * This file Copyright (c) 2005-2010 Aptana, Inc. This program is
 * dual-licensed under both the Aptana Public License and the GNU General
 * Public license. You may elect to use one or the other of these licenses.
 * 
 * This program is distributed in the hope that it will be useful, but
 * AS-IS and WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE, TITLE, or
 * NONINFRINGEMENT. Redistribution, except as permitted by whichever of
 * the GPL or APL you select, is prohibited.
 *
 * 1. For the GPL license (GPL), you can redistribute and/or modify this
 * program under the terms of the GNU General Public License,
 * Version 3, as published by the Free Software Foundation.  You should
 * have received a copy of the GNU General Public License, Version 3 along
 * with this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 * 
 * Aptana provides a special exception to allow redistribution of this file
 * with certain other free and open source software ("FOSS") code and certain additional terms
 * pursuant to Section 7 of the GPL. You may view the exception and these
 * terms on the web at http://www.aptana.com/legal/gpl/.
 * 
 * 2. For the Aptana Public License (APL), this program and the
 * accompanying materials are made available under the terms of the APL
 * v1.0 which accompanies this distribution, and is available at
 * http://www.aptana.com/legal/apl/.
 * 
 * You may view the GPL, Aptana's exception and additional terms, and the
 * APL in the file titled license.html at the root of the corresponding
 * plugin containing this source file.
 * 
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

import com.aptana.ide.core.io.IConnectionPoint;

/**
 * @author Max Stepanov
 * @author Michael Xia
 *
 */
public final class SiteConnectionUtils {

	/**
	 * 
	 */
	private SiteConnectionUtils() {
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
    public static ISiteConnection createSite(String name, IConnectionPoint source,
            IConnectionPoint destination) {
        ISiteConnection site = SiteConnectionManager.getInstance().createSiteConnection();
        site.setName(name);
        site.setSource(source);
        site.setDestination(destination);

        return site;
    }

    /**
     * Retrieves a list of all available sites that have the object as the
     * source (i.e. an IContainer or FilesystemObject).
     * 
     * @param object
     *            the source object
     * @return the list as an array
     */
	public static ISiteConnection[] findSitesForSource(IAdaptable object) {
		return findSitesForSource(object, false);
	}

    /**
     * Retrieves a list of all available sites that have the object as the
     * source (i.e. an IContainer or FilesystemObject).
     * 
     * @param object
     *            the source object
     * @param strict
     *            true if only to get the exact matches, false if the parent
     *            folder is allowed
     * @return the list as an array
     */
	public static ISiteConnection[] findSitesForSource(IAdaptable object, boolean strict) {
		List<ISiteConnection> list = new ArrayList<ISiteConnection>();
		ISiteConnection[] allsites = SyncingPlugin.getSiteConnectionManager().getSiteConnections();
		if (object instanceof IConnectionPoint) {
			for (ISiteConnection i : allsites) {
				if (object.equals(i.getSource())) {
					list.add(i);
				}
			}
		} else if (object instanceof IResource) {
			IResource resource = (IResource) object;
			for (ISiteConnection i : allsites) {
				IConnectionPoint sourceConnectionPoint = i.getSource();
				if (sourceConnectionPoint != null) {
					IContainer connectionRoot = (IContainer) sourceConnectionPoint.getAdapter(IResource.class);
					if (connectionRoot != null) {
						if (connectionRoot.equals(resource) || (!strict && contains(connectionRoot, resource))) {
							list.add(i);
						}
					}
				}
			}
		} else {
			IFileStore fileStore = (IFileStore) object.getAdapter(IFileStore.class);
			if (fileStore != null) {
				for (ISiteConnection i : allsites) {
					IConnectionPoint sourceConnectionPoint = i.getSource();
					if (sourceConnectionPoint != null) {
						try {
							IFileStore root = sourceConnectionPoint.getRoot();
							if (root != null) {
								if (root.equals(fileStore) || (!strict && root.isParentOf(fileStore))) {
									list.add(i);
								}
							}
						} catch (CoreException ignore) {
						}
					}
				}
			}
		}
		return list.toArray(new ISiteConnection[list.size()]);
	}

    /**
     * Retrieves a list of all available sites that have the connection point as
     * the destination.
     * 
     * @param destination
     *            the connection point
     * @return the list as an array
     */
    public static ISiteConnection[] findSitesWithDestination(IConnectionPoint destination) {
        List<ISiteConnection> list = new ArrayList<ISiteConnection>();
        ISiteConnection[] allsites = SyncingPlugin.getSiteConnectionManager().getSiteConnections();
        for (ISiteConnection i : allsites) {
            if (destination.equals(i.getDestination())) {
                list.add(i);
            }
        }
        return list.toArray(new ISiteConnection[list.size()]);
    }

    /**
     * Retrieves a list of all available sites that have the specific source and
     * destination.
     * 
     * @param source
     *            the source object
     * @param destination
     *            the connection point as destination
     * @return the list as an array
     */
    public static ISiteConnection[] findSites(IAdaptable source, IConnectionPoint destination) {
        List<ISiteConnection> list = new ArrayList<ISiteConnection>();
        ISiteConnection[] sites = findSitesForSource(source, true);
        for (ISiteConnection site : sites) {
            if (site.getDestination() == destination) {
                list.add(site);
            }
        }
        return list.toArray(new ISiteConnection[list.size()]);
    }

	public static ISiteConnection getSiteWithDestination(String destinationName, ISiteConnection[] sites)
	{
		for (ISiteConnection site : sites)
		{
			if (site.getDestination().getName().equals(destinationName))
			{
				return site;
			}
		}
		return null;
	}

	private static boolean contains(IContainer container, IResource resource) {
		return container.getFullPath().isPrefixOf(resource.getFullPath());
	}
}
