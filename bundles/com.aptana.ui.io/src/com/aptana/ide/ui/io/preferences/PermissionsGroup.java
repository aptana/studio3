/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.ide.ui.io.preferences;

import java.util.LinkedHashSet;
import java.util.Set;

import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import com.aptana.core.io.vfs.IExtendedFileInfo;
import com.aptana.core.util.StringUtil;

/**
 * @author Michael Xia (mxia@aptana.com)
 */
public class PermissionsGroup extends Composite
{

	public static interface Listener
	{

		/**
		 * Notifies that the permissions are modified.
		 */
		public void permissionsModified();
	}

	private Button fUserRead;
	private Button fUserWrite;
	private Button fUserExecute;
	private Button fGroupRead;
	private Button fGroupWrite;
	private Button fGroupExecute;
	private Button fAllRead;
	private Button fAllWrite;
	private Button fAllExecute;

	private Set<Listener> fListeners;

	/**
	 * Constructor.
	 * 
	 * @param parent
	 *            the parent composite
	 */
	public PermissionsGroup(Composite parent)
	{
		super(parent, SWT.NONE);
		setLayout(GridLayoutFactory.fillDefaults().numColumns(4).spacing(5, 0).create());
		fListeners = new LinkedHashSet<Listener>();

		Label label = new Label(this, SWT.NONE);
		label.setText(StringUtil.makeFormLabel(Messages.PermissionsGroup_User));
		SelectionAdapter selectionAdapter = new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				firePermissionsModified();
			}
		};
		fUserRead = createPermissionButton(Messages.PermissionsGroup_Read, selectionAdapter);
		fUserWrite = createPermissionButton(Messages.PermissionsGroup_Write, selectionAdapter);
		fUserExecute = createPermissionButton(Messages.PermissionsGroup_Execute, selectionAdapter);

		label = new Label(this, SWT.NONE);
		label.setText(StringUtil.makeFormLabel(Messages.PermissionsGroup_Group));
		fGroupRead = createPermissionButton(Messages.PermissionsGroup_Read, selectionAdapter);
		fGroupWrite = createPermissionButton(Messages.PermissionsGroup_Write, selectionAdapter);
		fGroupExecute = createPermissionButton(Messages.PermissionsGroup_Execute, selectionAdapter);

		label = new Label(this, SWT.NONE);
		label.setText(StringUtil.makeFormLabel(Messages.PermissionsGroup_All));
		fAllRead = createPermissionButton(Messages.PermissionsGroup_Read, selectionAdapter);
		fAllWrite = createPermissionButton(Messages.PermissionsGroup_Write, selectionAdapter);
		fAllExecute = createPermissionButton(Messages.PermissionsGroup_Execute, selectionAdapter);
	}

	public void addListener(Listener listener)
	{
		fListeners.add(listener);
	}

	public void removeListener(Listener listener)
	{
		fListeners.remove(listener);
	}

	public long getPermissions()
	{
		long permissions = 0;
		permissions |= (fUserRead.getSelection() ? IExtendedFileInfo.PERMISSION_OWNER_READ : 0);
		permissions |= (fUserWrite.getSelection() ? IExtendedFileInfo.PERMISSION_OWNER_WRITE : 0);
		permissions |= (fUserExecute.getSelection() ? IExtendedFileInfo.PERMISSION_OWNER_EXECUTE : 0);
		permissions |= (fGroupRead.getSelection() ? IExtendedFileInfo.PERMISSION_GROUP_READ : 0);
		permissions |= (fGroupWrite.getSelection() ? IExtendedFileInfo.PERMISSION_GROUP_WRITE : 0);
		permissions |= (fGroupExecute.getSelection() ? IExtendedFileInfo.PERMISSION_GROUP_EXECUTE : 0);
		permissions |= (fAllRead.getSelection() ? IExtendedFileInfo.PERMISSION_OTHERS_READ : 0);
		permissions |= (fAllWrite.getSelection() ? IExtendedFileInfo.PERMISSION_OTHERS_WRITE : 0);
		permissions |= (fAllExecute.getSelection() ? IExtendedFileInfo.PERMISSION_OTHERS_EXECUTE : 0);

		return permissions;
	}

	public void setPermissions(long permissions)
	{
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

	@Override
	public void setEnabled(boolean enabled)
	{
		super.setEnabled(enabled);
		fUserRead.setEnabled(enabled);
		fUserWrite.setEnabled(enabled);
		fUserExecute.setEnabled(enabled);
		fGroupRead.setEnabled(enabled);
		fGroupWrite.setEnabled(enabled);
		fGroupExecute.setEnabled(enabled);
		fAllRead.setEnabled(enabled);
		fAllWrite.setEnabled(enabled);
		fAllExecute.setEnabled(enabled);
	}

	private void firePermissionsModified()
	{
		for (Listener listener : fListeners)
		{
			listener.permissionsModified();
		}
	}

	private Button createPermissionButton(String text, SelectionListener listener)
	{
		Button button = new Button(this, SWT.CHECK);
		button.setText(text);
		button.addSelectionListener(listener);
		return button;
	}
}
