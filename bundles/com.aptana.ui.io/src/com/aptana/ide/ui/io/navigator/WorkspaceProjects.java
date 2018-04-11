/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
// $codepro.audit.disable staticFieldNamingConvention

package com.aptana.ide.ui.io.navigator;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE.SharedImages;
import org.eclipse.ui.model.IWorkbenchAdapter;

public class WorkspaceProjects implements IWorkbenchAdapter {

    private static WorkspaceProjects instance;

    private WorkspaceProjects() {
    }

    public static WorkspaceProjects getInstance() {
        if (instance == null) {
            instance = new WorkspaceProjects();
        }
        return instance;
    }

    public Object[] getChildren(Object o) {
        return ResourcesPlugin.getWorkspace().getRoot().getProjects();
    }

    public ImageDescriptor getImageDescriptor(Object object) {
        return PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(
                SharedImages.IMG_OBJ_PROJECT);
    }

    public String getLabel(Object o) {
        return Messages.WorkspaceProjects_LBL;
    }

    public Object getParent(Object o) {
        return null;
    }

    @Override
    public String toString() {
        return Messages.WorkspaceProjects_LBL;
    }
}
