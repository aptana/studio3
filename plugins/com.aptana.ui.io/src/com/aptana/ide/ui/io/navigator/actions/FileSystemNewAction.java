/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.ide.ui.io.navigator.actions;

import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IMenuCreator;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.actions.BaseSelectionListenerAction;

/**
 * @author Michael Xia (mxia@aptana.com)
 */
public class FileSystemNewAction extends BaseSelectionListenerAction {

    private class MenuCreator implements IMenuCreator {

        private MenuManager dropDownMenuMgr;
        private NewFolderAction fNewFolderAction;
        private NewFileAction fNewFileAction;

        protected MenuCreator() {
            fNewFolderAction = new NewFolderAction(fWindow);
            fNewFileAction = new NewFileAction();
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
                dropDownMenuMgr.add(new Separator());
                // adds the "Other..." action
                dropDownMenuMgr.add(ActionFactory.NEW.create(fWindow));
            }
        }
    };

    private IWorkbenchWindow fWindow;
    private MenuCreator fMenuCreator;

    public FileSystemNewAction(IWorkbenchWindow window) {
        super(Messages.FileSystemNewAction_Text);
        fWindow = window;
        setMenuCreator(fMenuCreator = new MenuCreator());
    }

    protected boolean updateSelection(IStructuredSelection selection) {
        fMenuCreator.selectionChanged(selection);

        return super.updateSelection(selection);
    }
}
