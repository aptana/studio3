/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.ide.ui.io.navigator.actions;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IWorkbenchCommandConstants;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.navigator.CommonActionProvider;
import org.eclipse.ui.navigator.ICommonActionExtensionSite;
import org.eclipse.ui.navigator.ICommonMenuConstants;

public class FileSystemMgmtActionProvider extends CommonActionProvider {

    private FileSystemRefreshAction fRefreshAction;

    public FileSystemMgmtActionProvider() {
    }

    public void init(ICommonActionExtensionSite aSite) {
        super.init(aSite);

        fRefreshAction = new FileSystemRefreshAction();
        fRefreshAction
                .setActionDefinitionId(IWorkbenchCommandConstants.FILE_REFRESH);
    }

    public void fillContextMenu(IMenuManager menu) {
        IStructuredSelection selection = getSelection();
        fRefreshAction.selectionChanged(selection);
        menu.appendToGroup(ICommonMenuConstants.GROUP_BUILD, fRefreshAction);
    }

    public void fillActionBars(IActionBars actionBars) {
        actionBars.setGlobalActionHandler(ActionFactory.REFRESH.getId(),
                fRefreshAction);
        updateActionBars();
    }

    public void updateActionBars() {
        IStructuredSelection selection = getSelection();
        fRefreshAction.selectionChanged(selection);
    }

    private IStructuredSelection getSelection() {
        return (IStructuredSelection) getContext().getSelection();
    }
}
