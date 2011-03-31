/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.ide.ui.io.navigator.actions;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.navigator.CommonActionProvider;
import org.eclipse.ui.navigator.ICommonActionExtensionSite;
import org.eclipse.ui.navigator.ICommonMenuConstants;
import org.eclipse.ui.navigator.ICommonViewerWorkbenchSite;

import com.aptana.ide.ui.io.Utils;

/**
 * @author Michael Xia (mxia@aptana.com)
 */
public class FileSystemNewActionProvider extends CommonActionProvider {

    private FileSystemNewAction fNewAction;
    private boolean fContribute;

    public FileSystemNewActionProvider() {
    }

    public void init(ICommonActionExtensionSite aSite) {
        super.init(aSite);

        if (aSite.getViewSite() instanceof ICommonViewerWorkbenchSite) {
            ICommonViewerWorkbenchSite viewSite = (ICommonViewerWorkbenchSite) aSite.getViewSite();
            fNewAction = new FileSystemNewAction(viewSite.getWorkbenchWindow());
            fContribute = true;
        }
    }

    public void fillContextMenu(IMenuManager menu) {
        if (fContribute) {
            updateSelection();

            if (isLocalFile()) {
                menu.insertAfter(ICommonMenuConstants.GROUP_NEW, fNewAction);
            }
        }
    }

    private boolean isLocalFile() {
        IStructuredSelection selection = getSelection();
        if (selection == null || selection.isEmpty()) {
            return false;
        }

        Object object = selection.getFirstElement();
        if (object instanceof IAdaptable) {
            IFileStore fileStore = Utils.getFileStore((IAdaptable) object);
            if (fileStore == null) {
                return false;
            }

            try {
                return fileStore.toLocalFile(EFS.NONE, null) != null;
            } catch (CoreException e) {
                // ignores the exception
            }
        }
        return false;
    }

    private IStructuredSelection getSelection() {
        return (IStructuredSelection) getContext().getSelection();
    }

    private void updateSelection() {
        fNewAction.selectionChanged(getSelection());
    }
}
