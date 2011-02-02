/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.ide.ui.io.navigator.actions;

import org.eclipse.jface.viewers.AbstractTreeViewer;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.actions.BaseSelectionListenerAction;

/**
 * @author Michael Xia (mxia@aptana.com)
 */
public class BaseDoubleClickAction extends BaseSelectionListenerAction {

    private TreeViewer fTreeViewer;

    public BaseDoubleClickAction(TreeViewer treeViewer) {
        super("Double click"); //$NON-NLS-1$
        fTreeViewer = treeViewer;
    }

    public void run() {
        if (selectionHasChildren()) {
            // performs the usual double-click action
            IStructuredSelection selection = (IStructuredSelection) fTreeViewer.getSelection();
            TreeItem item = fTreeViewer.getTree().getSelection()[0];
            if (item.getExpanded()) {
                fTreeViewer.collapseToLevel(selection.getFirstElement(), AbstractTreeViewer.ALL_LEVELS);
            } else {
                fTreeViewer.expandToLevel(selection.getFirstElement(), 1);
            }
        }
    }

    protected boolean selectionHasChildren() {
        TreeItem[] items = fTreeViewer.getTree().getSelection();
        return items.length > 0 && items[0].getItemCount() > 0;
    }
}
