/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.ide.ui.io.navigator.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.actions.NewProjectAction;
import org.eclipse.ui.internal.navigator.resources.plugin.WorkbenchNavigatorMessages;
import org.eclipse.ui.navigator.CommonActionProvider;
import org.eclipse.ui.navigator.ICommonActionExtensionSite;
import org.eclipse.ui.navigator.ICommonMenuConstants;
import org.eclipse.ui.navigator.ICommonViewerWorkbenchSite;

@SuppressWarnings("restriction")
public class NewProjectActionProvider extends CommonActionProvider {

    private static final String NEW_MENU_NAME = "common.new.menu";//$NON-NLS-1$

    private IAction newProjectAction;
    private boolean fContribute;

    public NewProjectActionProvider() {
    }

    public void init(ICommonActionExtensionSite aSite) {
        super.init(aSite);

        if (aSite.getViewSite() instanceof ICommonViewerWorkbenchSite) {
            IWorkbenchWindow window = ((ICommonViewerWorkbenchSite) aSite
                    .getViewSite()).getWorkbenchWindow();
            newProjectAction = new NewProjectAction(window);
            fContribute = true;
        }
    }

    public void fillContextMenu(IMenuManager menu) {
        if (fContribute) {
            IMenuManager submenu = new MenuManager(
                    WorkbenchNavigatorMessages.NewActionProvider_NewMenu_label,
                    NEW_MENU_NAME);
            submenu.add(newProjectAction);

            menu.insertAfter(ICommonMenuConstants.GROUP_NEW, submenu);
        }
    }
}
