/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
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
