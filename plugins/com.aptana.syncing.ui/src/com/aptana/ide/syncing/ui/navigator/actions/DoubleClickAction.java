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
package com.aptana.ide.syncing.ui.navigator.actions;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Shell;

import com.aptana.ide.syncing.core.ISiteConnection;
import com.aptana.ide.syncing.ui.dialogs.SiteConnectionsEditorDialog;
import com.aptana.ide.syncing.ui.editors.EditorUtils;
import com.aptana.ide.syncing.ui.navigator.ProjectSiteConnection;
import com.aptana.ide.ui.io.navigator.actions.BaseDoubleClickAction;

/**
 * @author Michael Xia (mxia@aptana.com)
 */
public class DoubleClickAction extends BaseDoubleClickAction {

    private Shell fShell;
    private TreeViewer fTreeViewer;

    public DoubleClickAction(Shell shell, TreeViewer treeViewer) {
        super(treeViewer);
        fShell = shell;
        fTreeViewer = treeViewer;
    }

    public void run() {
        IStructuredSelection selection = (IStructuredSelection) fTreeViewer.getSelection();
        Object element = selection.getFirstElement();
        if (element instanceof ISiteConnection) {
            // double-clicked on a site; opens it in the connection editor
            EditorUtils.openConnectionEditor((ISiteConnection) element);
        } else if (element instanceof ProjectSiteConnection) {
            // double-clicked on a site inside a project; both expands the node
            // and opens the connection editor
            super.run();
            EditorUtils.openConnectionEditor(((ProjectSiteConnection) element).getSiteConnection());
        } else {
            if (selectionHasChildren()) {
                super.run();
            } else {
                // no connection point has been defined against the project;
                // opens the new site connection dialog
                IAdaptable source = null;
                if (element instanceof IAdaptable) {
                    source = (IAdaptable) element;
                }
            	openNewSiteConnectionDialog(source);
            }
        }
    }

    private void openNewSiteConnectionDialog(IAdaptable source) {
        SiteConnectionsEditorDialog dlg = new SiteConnectionsEditorDialog(fShell);
        dlg.setCreateNew(Messages.DoubleClickAction_NewConnection, source, null);
        dlg.open();
    }
}
