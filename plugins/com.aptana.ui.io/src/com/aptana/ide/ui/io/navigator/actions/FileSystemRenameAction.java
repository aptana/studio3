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
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TreeEditor;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.actions.BaseSelectionListenerAction;

import com.aptana.ui.UIUtils;
import com.aptana.ide.ui.io.IOUIPlugin;
import com.aptana.ide.ui.io.internal.Utils;

/**
 * @author Michael Xia (mxia@aptana.com)
 */
public class FileSystemRenameAction extends BaseSelectionListenerAction {

    private Shell fShell;
    private Tree fTree;

    public FileSystemRenameAction(Shell shell, Tree tree) {
        super(Messages.FileSystemRenameAction_Text);
        fShell = shell;
        fTree = tree;
        setToolTipText(Messages.FileSystemRenameAction_ToolTip);
    }

    public void run() {
        TreeItem[] items = fTree.getSelection();
        if (items.length > 0) {
            renameFile(items[0]);
        }
    }

    private void renameFile(final TreeItem item) {
        final Object data = item.getData();
        if (!(data instanceof IAdaptable)) {
            return;
        }
        final IFileStore fileStore = Utils.getFileStore((IAdaptable) data);
        if (fileStore == null) {
            return;
        }

        TreeItem parentItem = item.getParentItem();
        final Object parentData;
        if (parentItem == null) {
        	parentData = null;
        } else {
        	parentData = parentItem.getData();
        }

        if (Platform.OS_MACOSX.equals(Platform.getOS())) {
            // through a dialog
            final InputDialog dialog = new InputDialog(fShell,
                    Messages.FileSystemRenameAction_InputTitle,
                    Messages.FileSystemRenameAction_InputMessage, item
                            .getText(), null);
            if (dialog.open() == InputDialog.OK) {
                String newName = dialog.getValue();
                try {
                    renameTo(fileStore, newName, null);
                    item.setText(newName);
                    refresh(parentData);
                } catch (CoreException e) {
                    showError(e);
                }
            }
        } else {
            // in-place rename
            final TreeEditor editor = new TreeEditor(fTree);
            final Text text = new Text(fTree, SWT.BORDER);
            text.setText(item.getText());
            text.selectAll();
            editor.horizontalAlignment = SWT.LEFT;
            editor.verticalAlignment = SWT.TOP;
            editor.grabHorizontal = true;

            editor.setEditor(text, item);

            Listener textListener = new Listener() {
                public void handleEvent(final Event e) {
                    switch (e.detail) {
                    case SWT.TRAVERSE_RETURN:
                        String newName = text.getText();
                        try {
                            renameTo(fileStore, newName, null);
                            item.setText(newName);
                            refresh(parentData);
                        } catch (CoreException ex) {
                            showError(ex);
                        }
                        // fall through
                    case SWT.TRAVERSE_ESCAPE:
                        text.dispose();
                        e.doit = true;
                        e.detail = SWT.TRAVERSE_NONE;
                    default:
                        break;
                    }
                }
            };
            fTree.addSelectionListener(new SelectionAdapter() {

                public void widgetSelected(SelectionEvent e) {
                    fTree.removeSelectionListener(this);
                    text.dispose();
                }

            });
            FocusAdapter listener = new FocusAdapter() {
                public void focusLost(FocusEvent fe) {
                    String newName = text.getText();
                    try {
                        renameTo(fileStore, newName, null);
                        item.setText(newName);
                        refresh(parentData);
                    } catch (CoreException ex) {
                        showError(ex);
                    }
                    text.dispose();
                }
            };

            text.addFocusListener(listener);
            text.addListener(SWT.Traverse, textListener);
            text.setFocus();
        }
    }

    private static void refresh(Object element) {
        IOUIPlugin.refreshNavigatorView(element);
    }

    private static void showError(Exception exception) {
        UIUtils.showErrorMessage(exception.getLocalizedMessage(), exception);
    }

    /**
     * Renames the file to a new name.
     * 
     * @param oldStore
     *            the IFileStore object for the existing file
     * @param newName
     *            the name the file is renamed to
     * @param monitor
     *            the progress monitor
     * @return the new file store, or null if rename failed
     */
    private static IFileStore renameTo(IFileStore oldStore, String newName,
            IProgressMonitor monitor) throws CoreException {
        IFileStore parent = oldStore.getParent();
        if (parent == null) {
            return null;
        }
        IFileStore newStore = parent.getChild(newName);
        oldStore.move(newStore, EFS.NONE, monitor);
        return newStore;
    }
}
