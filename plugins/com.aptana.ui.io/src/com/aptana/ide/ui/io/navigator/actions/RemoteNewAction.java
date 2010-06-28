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
package com.aptana.ide.ui.io.navigator.actions;

import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IMenuCreator;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.actions.BaseSelectionListenerAction;

/**
 * @author Michael Xia (mxia@aptana.com)
 */
public class RemoteNewAction extends BaseSelectionListenerAction {

    private class MenuCreator implements IMenuCreator {

        private MenuManager dropDownMenuMgr;
        private NewFolderAction fNewFolderAction;
        private NewFileAction fNewFileAction;

        public MenuCreator() {
            fNewFolderAction = new NewFolderAction(fWindow);
            fNewFileAction = new NewFileAction(fWindow);
        }

        public void dispose() {
            if (dropDownMenuMgr != null) {
                dropDownMenuMgr.dispose();
                dropDownMenuMgr = null;
            }
        }

        public Menu getMenu(Control parent) {
            createDropDownMenuMgr();
            return dropDownMenuMgr.createContextMenu(parent);
        }

        public Menu getMenu(Menu parent) {
            createDropDownMenuMgr();

            Menu menu = new Menu(parent);
            IContributionItem[] items = dropDownMenuMgr.getItems();
            for (IContributionItem item : items) {
                if (item instanceof ActionContributionItem) {
                    item = new ActionContributionItem(((ActionContributionItem) item).getAction());
                }
                item.fill(menu, -1);
            }
            return menu;
        }

        public void selectionChanged(IStructuredSelection selection) {
            fNewFolderAction.selectionChanged(selection);
            fNewFileAction.selectionChanged(selection);
        }

        private void createDropDownMenuMgr() {
            if (dropDownMenuMgr == null) {
                dropDownMenuMgr = new MenuManager();
                dropDownMenuMgr.add(fNewFolderAction);
                dropDownMenuMgr.add(fNewFileAction);
            }
        }

    };

    private IWorkbenchWindow fWindow;
    private MenuCreator fMenuCreator;

    public RemoteNewAction(IWorkbenchWindow window) {
        super(Messages.FileSystemNewAction_Text);
        fWindow = window;
        setMenuCreator(fMenuCreator = new MenuCreator());
    }

    protected boolean updateSelection(IStructuredSelection selection) {
        fMenuCreator.selectionChanged(selection);

        return super.updateSelection(selection);
    }
}
