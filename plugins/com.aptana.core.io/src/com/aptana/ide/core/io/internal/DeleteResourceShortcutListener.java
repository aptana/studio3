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
package com.aptana.ide.core.io.internal;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;

import com.aptana.ide.core.io.CoreIOPlugin;
import com.aptana.ide.core.io.IConnectionPoint;
import com.aptana.ide.core.io.IConnectionPointManager;
import com.aptana.ide.core.io.WorkspaceConnectionPoint;

/**
 * Deletes its corresponding project shortcuts when a resource is deleted.
 * 
 * @author Michael Xia (mxia@aptana.com)
 */
public class DeleteResourceShortcutListener implements IResourceChangeListener {

    private static final String CATEGORY_ID = "com.aptana.ide.core.io.projectShortcuts"; //$NON-NLS-1$

    /**
     * @see org.eclipse.core.resources.IResourceChangeListener#resourceChanged(org.eclipse.core.resources.IResourceChangeEvent)
     */
    public void resourceChanged(IResourceChangeEvent event) {
        IResourceDelta delta = event.getDelta();

        switch (delta.getKind()) {
        case IResourceDelta.REMOVED:
            break;
        case IResourceDelta.CHANGED:
            List<IConnectionPoint> deleted = new ArrayList<IConnectionPoint>();
            IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();

            IConnectionPointManager manager = CoreIOPlugin.getConnectionPointManager();
            IConnectionPoint[] projectShortcuts = manager.getConnectionPointCategory(CATEGORY_ID)
                    .getConnectionPoints();
            WorkspaceConnectionPoint connectionPoint;
            for (IConnectionPoint shortcut : projectShortcuts) {
                connectionPoint = (WorkspaceConnectionPoint) shortcut;

                // see if the relative path matches the changed item
                IResourceDelta d = delta.findMember(connectionPoint.getPath());
                if (d != null && d.getKind() == IResourceDelta.REMOVED) {
                    if (d.getMovedToPath() == null) {
                    	IResource resource = d.getResource();
                    	if (!(resource instanceof IProject) && resource.getProject().exists() && !resource.getProject().isOpen())
                    	{
                    		continue;
                    	}
                    	// the original container was deleted
                    	deleted.add(shortcut);
                    } else {
                        // the original container was moved
                        IPath newPath = d.getMovedToPath();
                        IContainer newContainer;
                        if (d.getResource() instanceof IProject) {
                            newContainer = root.getProject(newPath.toString());
                        } else {
                            newContainer = root.getFolder(newPath);
                        }
                        connectionPoint.setResource(newContainer);
                        connectionPoint.setName(newContainer.getName());
                    }
                }
            }

            // if the item was deleted, remove it
            for (IConnectionPoint projectShortcut : deleted) {
                manager.removeConnectionPoint(projectShortcut);
            }
            break;
        }
    }
}
