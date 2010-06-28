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
import com.aptana.ide.ui.io.internal.Utils;
import com.aptana.ui.UIUtils;

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
