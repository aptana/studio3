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
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IWorkbenchCommandConstants;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.actions.ActionGroup;
import org.eclipse.ui.navigator.ICommonMenuConstants;

public class FileSystemRefactorActionGroup extends ActionGroup {

    private FileSystemRenameAction fRenameAction;

    private Shell fShell;
    private Tree fTree;

    public FileSystemRefactorActionGroup(Shell shell, Tree tree) {
        fShell = shell;
        fTree = tree;
        makeActions();
    }

    public void fillContextMenu(IMenuManager menu) {
        IStructuredSelection selection = getSelection();

        if (selection != null && !selection.isEmpty()) {
            fRenameAction.selectionChanged(selection);
            menu.appendToGroup(ICommonMenuConstants.GROUP_REORGANIZE,
                    fRenameAction);
        }
    }

    public void fillActionBars(IActionBars actionBars) {
        updateActionBars();

        actionBars.setGlobalActionHandler(ActionFactory.RENAME.getId(),
                fRenameAction);
    }

    public void updateActionBars() {
        IStructuredSelection selection = getSelection();
        fRenameAction.selectionChanged(selection);
    }

    protected void makeActions() {
        fRenameAction = new FileSystemRenameAction(fShell, fTree);
        fRenameAction
                .setActionDefinitionId(IWorkbenchCommandConstants.FILE_RENAME);
    }

    private IStructuredSelection getSelection() {
        return (IStructuredSelection) getContext().getSelection();
    }
}
