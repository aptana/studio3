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
 * Contains a list of available sites that have the specific project as the
 * source.
 * 
 * @author Michael Xia (mxia@aptana.com)
 */
public class ProjectSiteConnections extends PlatformObject implements IWorkbenchAdapter {

    private static ImageDescriptor IMAGE_DESCRIPTOR = SyncingUIPlugin
            .getImageDescriptor("icons/full/obj16/connection.png"); //$NON-NLS-1$

    private IProject fProject;

    public ProjectSiteConnections(IProject project) {
        fProject = project;
    }

    public Object[] getChildren(Object o) {
        ISiteConnection[] sites = SiteConnectionUtils.findSitesForSource(fProject, true, true);
        List<ProjectSiteConnection> targets = new ArrayList<ProjectSiteConnection>();
        for (ISiteConnection site : sites) {
        	targets.add(new ProjectSiteConnection(fProject, site));
        }
        return targets.toArray(new ProjectSiteConnection[targets.size()]);
    }

    public ImageDescriptor getImageDescriptor(Object object) {
        return IMAGE_DESCRIPTOR;
    }

    public String getLabel(Object o) {
        return Messages.ProjectSiteConnections_Name;
    }

    public Object getParent(Object o) {
        return null;
    }

    @SuppressWarnings("rawtypes")
    public Object getAdapter(Class adapter) {
        if (adapter == IProject.class || adapter == IContainer.class) {
            return fProject;
        }
        return super.getAdapter(adapter);
    }

    @Override
    public String toString() {
        return Messages.ProjectSiteConnections_Name;
    }
}
