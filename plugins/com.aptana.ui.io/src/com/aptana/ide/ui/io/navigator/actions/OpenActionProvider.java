/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.ide.ui.io.navigator.actions;

import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.navigator.CommonActionProvider;
import org.eclipse.ui.navigator.ICommonActionConstants;
import org.eclipse.ui.navigator.ICommonActionExtensionSite;
import org.eclipse.ui.navigator.ICommonMenuConstants;
import org.eclipse.ui.navigator.ICommonViewerWorkbenchSite;

import com.aptana.ui.io.epl.OpenWithMenu;
import com.aptana.ui.io.epl.OpenWithMenu.Client;

/**
 * @author Michael Xia (mxia@aptana.com)
 */
public class OpenActionProvider extends CommonActionProvider {

    private OpenFileAction fOpenFileAction;
    private ICommonViewerWorkbenchSite fSite;
    private boolean fContribute;

    public OpenActionProvider() {
    }

    public void init(ICommonActionExtensionSite aSite) {
        super.init(aSite);

        if (aSite.getViewSite() instanceof ICommonViewerWorkbenchSite) {
            fSite = (ICommonViewerWorkbenchSite) aSite.getViewSite();
            fOpenFileAction = new OpenFileAction();
            fContribute = true;
        }
    }

    public void fillContextMenu(IMenuManager menu) {
        if (fContribute) {
            fOpenFileAction.selectionChanged(getSelection());
            menu.insertAfter(ICommonMenuConstants.GROUP_OPEN, fOpenFileAction);
            addOpenWithMenu(menu);
        }
    }

    public void fillActionBars(IActionBars actionBars) {
        if (fContribute) {
            fOpenFileAction.selectionChanged(getSelection());
            actionBars.setGlobalActionHandler(ICommonActionConstants.OPEN,
                    fOpenFileAction);
        }
    }

    private void addOpenWithMenu(IMenuManager menu) {
        IStructuredSelection selection = getSelection();
        if (selection == null || selection.size() != 1) {
            return;
        }

        Object element = selection.getFirstElement();
        if (!(element instanceof IAdaptable)) {
            return;
        }
        IMenuManager submenu = new MenuManager(
                Messages.OpenActionProvider_LBL_OpenWith,
                ICommonMenuConstants.GROUP_OPEN_WITH);
        submenu.add(new GroupMarker(ICommonMenuConstants.GROUP_TOP));
		submenu.add(new OpenWithMenu(fSite.getPage(), (IAdaptable) element, new Client()
		{

			public void openEditor(IFileStore file, IEditorDescriptor editorDescriptor)
			{
				EditorUtils.openFileInEditor(file, editorDescriptor);
			}
		}));
        submenu.add(new GroupMarker(ICommonMenuConstants.GROUP_ADDITIONS));

        // adds the submenu
        if (submenu.getItems().length > 2 && submenu.isEnabled()) {
            menu.appendToGroup(ICommonMenuConstants.GROUP_OPEN_WITH, submenu);
        }
    }

    private IStructuredSelection getSelection() {
        return (IStructuredSelection) getContext().getSelection();
    }
}
