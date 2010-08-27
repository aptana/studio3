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

import java.lang.reflect.Method;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchCommandConstants;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionGroup;
import org.eclipse.ui.actions.TextActionHandler;
import org.eclipse.ui.navigator.ICommonMenuConstants;

import com.aptana.ide.core.io.IConnectionPoint;
import com.aptana.ide.core.io.LocalRoot;

public class FileSystemEditActionGroup extends ActionGroup {

    private Clipboard fClipboard;
    private Shell fShell;
    private Tree fTree;

    private FileSystemCopyAction fCopyAction;
    private FileSystemPasteAction fPasteAction;
    private FileSystemDeleteAction fDeleteAction;

    private TextActionHandler fTextActionHandler;

    public FileSystemEditActionGroup(Shell shell, Tree tree) {
        fShell = shell;
        fTree = tree;
        makeActions();
    }

    @Override
    public void dispose() {
        if (fClipboard != null) {
            fClipboard.dispose();
            fClipboard = null;
        }
        super.dispose();
    }

    @Override
    public void fillContextMenu(IMenuManager menu) {
        IStructuredSelection selection = getSelection();
        fCopyAction.selectionChanged(selection);
        //menu.appendToGroup(ICommonMenuConstants.GROUP_EDIT, fCopyAction);
        fPasteAction.selectionChanged(selection);
        //menu.appendToGroup(ICommonMenuConstants.GROUP_EDIT, fPasteAction);

        if (selection != null && !selection.isEmpty()) {
            Object[] elements = selection.toArray();
            boolean allFileSystemObjects = true;
            for (Object element : elements) {
                if (element instanceof LocalRoot || element instanceof IConnectionPoint) {
                    allFileSystemObjects = false;
                    break;
                }
            }
            if (allFileSystemObjects) {
                menu.appendToGroup(ICommonMenuConstants.GROUP_EDIT, fDeleteAction);
            }
        }
    }

    @Override
    public void fillActionBars(IActionBars actionBars) {
        if (fTextActionHandler == null) {
            fTextActionHandler = new TextActionHandler(actionBars);
        }

        // fTextActionHandler.setCopyAction(fCopyAction);
        // fTextActionHandler.setPasteAction(fPasteAction);
        fTextActionHandler.setDeleteAction(fDeleteAction);
        updateActionBars();
        
//      fTextActionHandler.updateActionBars(); // 3.6+ only, so we need to use reflection
        try
		{
			Method m = TextActionHandler.class.getMethod("updateActionBars"); //$NON-NLS-1$
			m.invoke(fTextActionHandler);
		}
		catch (Exception e)
		{
			// ignore
		}

    }

    @Override
    public void updateActionBars() {
        IStructuredSelection selection = getSelection();

        fCopyAction.selectionChanged(selection);
        fPasteAction.selectionChanged(selection);
        fDeleteAction.selectionChanged(selection);
    }

    /**
     * Handles a key pressed event by invoking the appropriate action.
     * 
     * @param event
     *            The key event
     */
    public void handleKeyPressed(KeyEvent event) {
        if (event.character == SWT.DEL && event.stateMask == 0) {
            if (fDeleteAction.isEnabled()) {
                fDeleteAction.run();
            }
            event.doit = false;
        }
    }

    protected void makeActions() {
        fClipboard = new Clipboard(fShell.getDisplay());

        ISharedImages images = PlatformUI.getWorkbench().getSharedImages();

        fPasteAction = new FileSystemPasteAction(fShell, fClipboard);
        fPasteAction.setDisabledImageDescriptor(images
                .getImageDescriptor(ISharedImages.IMG_TOOL_PASTE_DISABLED));
        fPasteAction.setImageDescriptor(images.getImageDescriptor(ISharedImages.IMG_TOOL_PASTE));
        // fPasteAction.setActionDefinitionId(IWorkbenchCommandConstants.EDIT_PASTE);

        fCopyAction = new FileSystemCopyAction(fShell, fClipboard, fPasteAction);
        fCopyAction.setDisabledImageDescriptor(images
                .getImageDescriptor(ISharedImages.IMG_TOOL_COPY_DISABLED));
        fCopyAction.setImageDescriptor(images.getImageDescriptor(ISharedImages.IMG_TOOL_COPY));
        // fCopyAction.setActionDefinitionId(IWorkbenchCommandConstants.EDIT_COPY);

        fDeleteAction = new FileSystemDeleteAction(fShell, fTree);
        fDeleteAction.setDisabledImageDescriptor(images
                .getImageDescriptor(ISharedImages.IMG_TOOL_DELETE_DISABLED));
        fDeleteAction.setImageDescriptor(images.getImageDescriptor(ISharedImages.IMG_TOOL_DELETE));
        fDeleteAction.setActionDefinitionId(IWorkbenchCommandConstants.EDIT_DELETE);
    }

    private IStructuredSelection getSelection() {
        return (IStructuredSelection) getContext().getSelection();
    }
}
