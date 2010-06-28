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
 * with certain Eclipse Public Licensed code and certain additional terms
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
package com.aptana.ide.ui.io.preferences;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;

import com.aptana.core.util.StringUtil;
import com.aptana.ide.core.io.vfs.IExtendedFileInfo;

/**
 * @author Michael Xia (mxia@aptana.com)
 */
public class PermissionsGroup {

    private Group fGroup;
    private Button fUserRead;
    private Button fUserWrite;
    private Button fUserExecute;
    private Button fGroupRead;
    private Button fGroupWrite;
    private Button fGroupExecute;
    private Button fAllRead;
    private Button fAllWrite;
    private Button fAllExecute;

    /**
     * Constructor.
     * 
     * @param parent
     *            the parent composite
     */
    public PermissionsGroup(Composite parent) {
        fGroup = createContents(parent);
    }

    /**
     * Gets the main control for the widget.
     * 
     * @return the main control
     */
    public Control getControl() {
        return fGroup;
    }

    public long getPermissions() {
        long permissions = 0;
        permissions |= (fUserRead.getSelection() ? IExtendedFileInfo.PERMISSION_OWNER_READ : 0);
        permissions |= (fUserWrite.getSelection() ? IExtendedFileInfo.PERMISSION_OWNER_WRITE : 0);
        permissions |= (fUserExecute.getSelection() ? IExtendedFileInfo.PERMISSION_OWNER_EXECUTE
                : 0);
        permissions |= (fGroupRead.getSelection() ? IExtendedFileInfo.PERMISSION_GROUP_READ : 0);
        permissions |= (fGroupWrite.getSelection() ? IExtendedFileInfo.PERMISSION_GROUP_WRITE : 0);
        permissions |= (fGroupExecute.getSelection() ? IExtendedFileInfo.PERMISSION_GROUP_EXECUTE
                : 0);
        permissions |= (fAllRead.getSelection() ? IExtendedFileInfo.PERMISSION_OTHERS_READ : 0);
        permissions |= (fAllWrite.getSelection() ? IExtendedFileInfo.PERMISSION_OTHERS_WRITE : 0);
        permissions |= (fAllExecute.getSelection() ? IExtendedFileInfo.PERMISSION_OTHERS_EXECUTE
                : 0);

        return permissions;
    }

    public void setPermissions(long permissions) {
        fUserRead.setSelection((permissions & IExtendedFileInfo.PERMISSION_OWNER_READ) != 0);
        fUserWrite.setSelection((permissions & IExtendedFileInfo.PERMISSION_OWNER_WRITE) != 0);
        fUserExecute.setSelection((permissions & IExtendedFileInfo.PERMISSION_OWNER_EXECUTE) != 0);
        fGroupRead.setSelection((permissions & IExtendedFileInfo.PERMISSION_GROUP_READ) != 0);
        fGroupWrite.setSelection((permissions & IExtendedFileInfo.PERMISSION_GROUP_WRITE) != 0);
        fGroupExecute.setSelection((permissions & IExtendedFileInfo.PERMISSION_GROUP_EXECUTE) != 0);
        fAllRead.setSelection((permissions & IExtendedFileInfo.PERMISSION_OTHERS_READ) != 0);
        fAllWrite.setSelection((permissions & IExtendedFileInfo.PERMISSION_OTHERS_WRITE) != 0);
        fAllExecute.setSelection((permissions & IExtendedFileInfo.PERMISSION_OTHERS_EXECUTE) != 0);
    }

    public void setText(String title) {
        if (!isDisposed()) {
            fGroup.setText(title);
        }
    }

    private Group createContents(Composite parent) {
        Group group = new Group(parent, SWT.NONE);
        group.setText(Messages.PermissionsGroup_Title);
        GridLayout layout = new GridLayout();
        layout.numColumns = 4;
        group.setLayout(layout);

        Label label = new Label(group, SWT.NONE);
        label.setText(StringUtil.makeFormLabel(Messages.PermissionsGroup_User));
        fUserRead = new Button(group, SWT.CHECK);
        fUserRead.setText(Messages.PermissionsGroup_Read);
        fUserWrite = new Button(group, SWT.CHECK);
        fUserWrite.setText(Messages.PermissionsGroup_Write);
        fUserExecute = new Button(group, SWT.CHECK);
        fUserExecute.setText(Messages.PermissionsGroup_Execute);

        label = new Label(group, SWT.NONE);
        label.setText(StringUtil.makeFormLabel(Messages.PermissionsGroup_Group));
        fGroupRead = new Button(group, SWT.CHECK);
        fGroupRead.setText(Messages.PermissionsGroup_Read);
        fGroupWrite = new Button(group, SWT.CHECK);
        fGroupWrite.setText(Messages.PermissionsGroup_Write);
        fGroupExecute = new Button(group, SWT.CHECK);
        fGroupExecute.setText(Messages.PermissionsGroup_Execute);

        label = new Label(group, SWT.NONE);
        label.setText(StringUtil.makeFormLabel(Messages.PermissionsGroup_All));
        fAllRead = new Button(group, SWT.CHECK);
        fAllRead.setText(Messages.PermissionsGroup_Read);
        fAllWrite = new Button(group, SWT.CHECK);
        fAllWrite.setText(Messages.PermissionsGroup_Write);
        fAllExecute = new Button(group, SWT.CHECK);
        fAllExecute.setText(Messages.PermissionsGroup_Execute);

        return group;
    }

    private boolean isDisposed() {
        return fGroup == null || fGroup.isDisposed();
    }
}
