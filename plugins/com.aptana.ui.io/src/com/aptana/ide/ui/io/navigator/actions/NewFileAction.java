/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.ide.ui.io.navigator.actions;

import java.io.IOException;
import java.io.OutputStream;

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

import com.aptana.core.io.vfs.IExtendedFileInfo;
import com.aptana.ide.core.io.preferences.PreferenceUtils;
import com.aptana.ide.ui.io.IOUIPlugin;
import com.aptana.ide.ui.io.Utils;
import com.aptana.ui.util.UIUtils;

/**
 * @author Michael Xia (mxia@aptana.com)
 */
public class NewFileAction extends BaseSelectionListenerAction {

    private IAdaptable fSelectedElement;
    private IWorkbenchWindow fWindow;

    public NewFileAction(IWorkbenchWindow window) {
        super(Messages.NewFileAction_Text);
        fWindow = window;
        setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(
                ISharedImages.IMG_OBJ_FILE));
        setToolTipText(Messages.NewFileAction_ToolTip);
    }

    public void run() {
        if (fSelectedElement == null) {
            return;
        }

        InputDialog input = new InputDialog(fWindow.getShell(), Messages.NewFileAction_InputTitle,
                Messages.NewFileAction_InputMessage, "", null); //$NON-NLS-1$
        if (input.open() == Window.OK) {
            createFile(input.getValue());
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

    private void createFile(final String filename) {
        final IFileStore fileStore = Utils.getFileStore(fSelectedElement);
        final boolean selectionIsDirectory = Utils.isDirectory(fSelectedElement);

        // run the file creation in a job
        Job job = new Job(Messages.NewFolderAction_JobTitle) {

            @Override
            protected IStatus run(IProgressMonitor monitor) {
                try {
                    IFileStore parentStore = fileStore;
                    Object element = fSelectedElement;
                    if (!selectionIsDirectory && fileStore.getParent() != null) {
                        parentStore = fileStore.getParent();
                        // TODO: needs to find the element corresponding to
                        // the parent folder
                        element = parentStore;
                    }

                    // creates an empty file
                    IFileStore newFile = parentStore.getChild(filename);
                    OutputStream out = newFile.openOutputStream(EFS.NONE, monitor);
                    try {
                        out.close();
                    } catch (IOException e) {
                    }

                    // sets the permissions
                    IFileInfo newInfo = newFile.fetchInfo(EFS.NONE, monitor);
                    if (newInfo instanceof IExtendedFileInfo) {
                        IExtendedFileInfo extendedInfo = (IExtendedFileInfo) newInfo;
                        extendedInfo.setPermissions(PreferenceUtils.getFilePermissions());
                        newFile.putInfo(extendedInfo, IExtendedFileInfo.SET_PERMISSIONS, monitor);
                    }

                    // opens it in the editor
                    EditorUtils.openFileInEditor(newFile);

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

    private void showError(Exception exception) {
        UIUtils.showErrorMessage(exception.getLocalizedMessage(), exception);
    }
}
