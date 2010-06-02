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
import org.eclipse.core.filesystem.IFileInfo;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.BaseSelectionListenerAction;

import com.aptana.ide.core.io.preferences.PreferenceUtils;
import com.aptana.ide.core.io.vfs.IExtendedFileInfo;
import com.aptana.ide.ui.io.IOUIPlugin;
import com.aptana.ide.ui.io.internal.Utils;
import com.aptana.ui.UIUtils;

/**
 * @author Michael Xia (mxia@aptana.com)
 */
public class NewFolderAction extends BaseSelectionListenerAction {

    private IAdaptable fSelectedElement;
    private IWorkbenchWindow fWindow;

    public NewFolderAction(IWorkbenchWindow window) {
        super(Messages.NewFolderAction_Text);
        fWindow = window;
        setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(
                ISharedImages.IMG_OBJ_FOLDER));
        setToolTipText(Messages.NewFolderAction_ToolTip);
    }

    public void run() {
        if (fSelectedElement == null) {
            return;
        }
        final IFileStore fileStore = Utils.getFileStore(fSelectedElement);
        final IFileInfo fileInfo = Utils.getFileInfo(fSelectedElement);

        InputDialog input = new InputDialog(fWindow.getShell(),
                Messages.NewFolderAction_InputTitle, Messages.NewFolderAction_InputMessage,
                "", null); //$NON-NLS-1$
        if (input.open() == Window.OK) {
            final String name = input.getValue();
            // run the folder creation in a job
            Job job = new Job(Messages.NewFolderAction_JobTitle) {

                @Override
                protected IStatus run(IProgressMonitor monitor) {
                    try {
                        IFileStore parentStore = fileStore;
                        Object element = fSelectedElement;
                        if (!fileInfo.isDirectory() && fileStore.getParent() != null) {
                            parentStore = fileStore.getParent();
                            // TODO: needs to find the element corresponding to
                            // the parent folder
                            element = null;
                        }
                        IFileStore newFolder = parentStore.getChild(name);
                        newFolder.mkdir(EFS.NONE, monitor);

                        // sets the permissions
                        IFileInfo newInfo = newFolder.fetchInfo(EFS.NONE, monitor);
                        if (newInfo instanceof IExtendedFileInfo) {
                            IExtendedFileInfo extendedInfo = (IExtendedFileInfo) newInfo;
                            extendedInfo.setPermissions(PreferenceUtils.getDirectoryPermissions());
                            newFolder.putInfo(extendedInfo, IExtendedFileInfo.SET_PERMISSIONS,
                                    monitor);
                        }

                        IOUIPlugin.refreshNavigatorView(element);
                    } catch (CoreException e) {
                        showError(e);
                    }
                    return Status.OK_STATUS;
                }
            };
            job.setUser(true);
            job.schedule();
        }
    }

    protected boolean updateSelection(IStructuredSelection selection) {
        fSelectedElement = null;

        if (selection != null && !selection.isEmpty()) {
            Object element = selection.getFirstElement();
            if (element instanceof IAdaptable) {
                fSelectedElement = (IAdaptable) element;
            }
        }

        return super.updateSelection(selection) && fSelectedElement != null;
    }

    private void showError(Exception exception) {
        UIUtils.showErrorMessage(exception.getLocalizedMessage(), exception);
    }
}
