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

import com.aptana.ide.ui.io.internal.Utils;

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
