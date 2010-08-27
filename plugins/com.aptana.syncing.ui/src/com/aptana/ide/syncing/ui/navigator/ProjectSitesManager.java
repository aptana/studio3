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
public class ProjectSitesManager {

    private static ProjectSitesManager fInstance;

    private Map<IProject, ProjectSiteConnections> fProjects;

    private IConnectionPointListener fListener = new IConnectionPointListener() {

        public void connectionPointChanged(ConnectionPointEvent event) {
            IConnectionPoint destConnection = event.getConnectionPoint();

            switch (event.getKind()) {
            case ConnectionPointEvent.POST_CHANGE:
                // refreshes the project connection node that contains the
                // connection point that was modified
                Collection<ProjectSiteConnections> projectConnections = fProjects.values();
                ProjectSiteConnection[] connections;
                IConnectionPoint connectionPoint;
                for (ProjectSiteConnections sites : projectConnections) {
                    connections = (ProjectSiteConnection[]) sites.getChildren(null);
                    for (ProjectSiteConnection projectConnection : connections) {
                        connectionPoint = (IConnectionPoint) projectConnection
                                .getAdapter(IConnectionPoint.class);
                        if (connectionPoint == destConnection) {
                            IOUIPlugin.refreshNavigatorView(projectConnection);
                        }
                    }
                }
            }
        }
    };

    public static ProjectSitesManager getInstance() {
        if (fInstance == null) {
            fInstance = new ProjectSitesManager();
        }
        return fInstance;
    }

    public ProjectSiteConnections getProjectSites(IProject project) {
        ProjectSiteConnections sites = fProjects.get(project);
        if (sites == null) {
            sites = new ProjectSiteConnections(project);
            fProjects.put(project, sites);
        }
        return sites;
    }

    private ProjectSitesManager() {
        fProjects = new HashMap<IProject, ProjectSiteConnections>();
        CoreIOPlugin.getConnectionPointManager().addConnectionPointListener(fListener);
    }
}
