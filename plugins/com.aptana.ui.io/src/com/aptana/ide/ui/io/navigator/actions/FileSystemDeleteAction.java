/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.ide.ui.io.navigator.actions;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeListener;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.actions.BaseSelectionListenerAction;

import com.aptana.ide.ui.io.IOUIPlugin;
import com.aptana.ide.ui.io.Utils;
import com.aptana.ui.util.UIUtils;

/**
 * @author Michael Xia (mxia@aptana.com)
 */
public class FileSystemDeleteAction extends BaseSelectionListenerAction {

    private Shell fShell;
    private Tree fTree;
    private List<IFileStore> fFiles;
    private Set<Object> fParentElements;

    private List<IJobChangeListener> fListeners;

    public FileSystemDeleteAction(Shell shell, Tree tree) {
        super(Messages.FileSystemDeleteAction_Text);
        fShell = shell;
        fTree = tree;
        fFiles = new ArrayList<IFileStore>();
        fParentElements = new HashSet<Object>();
        fListeners = new ArrayList<IJobChangeListener>();
    }

    public void addJobListener(IJobChangeListener listener) {
        if (!fListeners.contains(listener)) {
            fListeners.add(listener);
        }
    }

    public void removeJobListener(IJobChangeListener listener) {
        fListeners.remove(listener);
    }

    public void run() {
        if (fFiles.isEmpty()) {
            return;
        }

        String message;
        int count = fFiles.size();
        if (count == 1) {
            message = MessageFormat.format(Messages.FileSystemDeleteAction_Confirm_SingleFile,
                    fFiles.get(0));
        } else {
            message = MessageFormat.format(Messages.FileSystemDeleteAction_Confirm_MultipleFiles,
                    count);
        }
        if (MessageDialog.openQuestion(fShell, Messages.FileSystemDeleteAction_Confirm_Title,
                message)) {
            delete(fFiles.toArray(new IFileStore[fFiles.size()]));
        }
    }

    public boolean updateSelection(IStructuredSelection selection) {
        fFiles.clear();
        fParentElements.clear();

        TreeItem[] items = fTree.getSelection();
        Object element;
        IFileStore fileStore;
        TreeItem parentItem;
        for (TreeItem item : items) {
        	element = item.getData();
        	fileStore = Utils.getFileStore(element);
        	if (fileStore != null) {
        		fFiles.add(fileStore);
        		parentItem = item.getParentItem();
        		if (parentItem != null) {
        			fParentElements.add(parentItem.getData());
        		}
        	}
        }

        return super.updateSelection(selection) && !fFiles.isEmpty();
    }

    private void delete(final IFileStore[] files) {
        // performs the deletion in a job
        Job deleteJob = new Job(Messages.FileSystemDeleteAction_JobTitle) {

            @Override
            protected IStatus run(IProgressMonitor monitor) {
                monitor.beginTask(Messages.FileSystemDeleteAction_Task, files.length);
                for (IFileStore file : files) {
                    monitor.subTask(MessageFormat.format(Messages.FileSystemDeleteAction_SubTask,
                            file.toString()));
                    deleteFile(file, monitor);
                    monitor.worked(1);
                    if (monitor.isCanceled()) {
                        return Status.CANCEL_STATUS;
                    }
                }
                monitor.done();

                for (Object element : fParentElements) {
                	IOUIPlugin.refreshNavigatorView(element);
                }

                return Status.OK_STATUS;
            }

            public boolean belongsTo(Object family) {
                if (Messages.FileSystemDeleteAction_JobTitle.equals(family)) {
                    return true;
                }
                return super.belongsTo(family);
            }
        };
        deleteJob.setUser(true);
        for (IJobChangeListener listener : fListeners) {
            deleteJob.addJobChangeListener(listener);
        }
        deleteJob.schedule();
    }

    private static boolean deleteFile(IFileStore file, IProgressMonitor monitor) {
        try {
            file.delete(EFS.NONE, monitor);
            return true;
        } catch (CoreException e) {
            UIUtils.showErrorMessage(e.getLocalizedMessage(), e);
            return false;
        }
    }
}
