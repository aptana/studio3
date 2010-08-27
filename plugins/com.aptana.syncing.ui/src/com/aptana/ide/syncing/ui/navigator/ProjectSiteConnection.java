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

import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.PlatformObject;

import com.aptana.ide.core.io.IConnectionPoint;
import com.aptana.ide.syncing.core.ISiteConnection;

/**
 * @author Michael Xia (mxia@aptana.com)
 */
public final class ProjectSiteConnection extends PlatformObject {

    private final IProject project;
    private final ISiteConnection siteConnection;

    private int hashCode;

    public ProjectSiteConnection(IProject project, ISiteConnection siteConnection) {
        this.project = project;
        this.siteConnection = siteConnection;
    }

    public IProject getProject() {
        return project;
    }

    public ISiteConnection getSiteConnection() {
        return siteConnection;
    }

    public boolean canDisconnect() {
    	IConnectionPoint connectionPoint = siteConnection.getDestination();
    	return connectionPoint == null ? false : connectionPoint.canDisconnect();
    }

    @SuppressWarnings("rawtypes")
    public Object getAdapter(Class adapter) {
        if (adapter == IProject.class) {
            return project;
        } else if (adapter == ISiteConnection.class) {
            return siteConnection;
        } else if (adapter == IConnectionPoint.class) {
            return siteConnection.getDestination();
        } else if (adapter == IFileStore.class) {
            IConnectionPoint destination = siteConnection.getDestination();
            try {
                return destination == null ? null : destination.getRoot();
            } catch (CoreException e) {
                // falls through on error
            }
        }
        return super.getAdapter(adapter);
    }

    @Override
    public int hashCode() {
        if (hashCode == 0) {
            hashCode = 7;
            hashCode = 31 * hashCode + project.hashCode();
            hashCode = 31 * hashCode + siteConnection.hashCode();
        }
        return hashCode;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ProjectSiteConnection)) {
            return false;
        }
        ProjectSiteConnection connection = (ProjectSiteConnection) o;
        return project == connection.project && siteConnection == connection.siteConnection;
    }

    @Override
    public String toString() {
        return getSiteConnection().toString();
    }
}
