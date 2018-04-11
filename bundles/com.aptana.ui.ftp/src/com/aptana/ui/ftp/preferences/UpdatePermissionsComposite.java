/**
 * Aptana Studio
 * Copyright (c) 2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.ui.ftp.preferences;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;

import com.aptana.core.util.StringUtil;
import com.aptana.ide.core.io.preferences.PermissionDirection;
import com.aptana.ide.core.io.preferences.PreferenceInitializer;
import com.aptana.ide.core.io.preferences.PreferenceUtils;
import com.aptana.ide.ui.io.preferences.PermissionsGroup;
import com.aptana.ide.ui.io.preferences.PermissionsGroup.Listener;

public class UpdatePermissionsComposite extends Composite
{

	private static final String FOR_FILES = Messages.UpdatePermissionsComposite_ForFiles;
	private static final String FOR_FOLDERS = Messages.UpdatePermissionsComposite_ForFolders;
	private static final String[] COMBO_ITEMS = { FOR_FILES, FOR_FOLDERS };

	private Button updatePermCheckbox;
	private Button preservePermButton;
	private Button specificPermButton;
	private Combo forFileFolderCombo;
	private PermissionsGroup permissionsGroup;

	// a map to track the current permissions set for files and folders
	private Map<String, Long> permissionsMap;

	/**
	 * The constructor.
	 * 
	 * @param parent
	 *            the parent shell
	 * @param direction
	 *            indicates if this is for upload or download permissions
	 */
	public UpdatePermissionsComposite(Composite parent, PermissionDirection direction)
	{
		super(parent, SWT.NONE);
		setLayout(GridLayoutFactory.swtDefaults().create());
		permissionsMap = new HashMap<String, Long>();

		updatePermCheckbox = new Button(this, SWT.CHECK);
		updatePermCheckbox.setText(Messages.UpdatePermissionsComposite_LBL_UpdatePermissions);
		updatePermCheckbox.setSelection(PreferenceUtils.getUpdatePermissions(direction));
		updatePermCheckbox.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				updateStates();
			}
		});

		Composite composite = new Composite(this, SWT.NONE);
		composite.setLayout(GridLayoutFactory.fillDefaults().extendedMargins(20, 0, 0, 0).spacing(5, 0).create());
		composite.setLayoutData(GridDataFactory.fillDefaults().grab(true, false).create());
		preservePermButton = new Button(composite, SWT.RADIO);
		preservePermButton.setText(Messages.UpdatePermissionsComposite_LBL_ToSourcePermissions);
		boolean useSpecificPermissions = PreferenceUtils.getSpecificPermissions(direction);
		preservePermButton.setSelection(!useSpecificPermissions);
		preservePermButton.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				specificPermButton.setSelection(false);
				updateStates();
			}
		});

		Composite specificPermComp = new Composite(composite, SWT.NONE);
		specificPermComp.setLayout(GridLayoutFactory.fillDefaults().numColumns(2).create());
		specificPermButton = new Button(specificPermComp, SWT.RADIO);
		specificPermButton.setText(StringUtil
				.makeFormLabel(Messages.UpdatePermissionsComposite_LBL_ToSpecificPermissions));
		specificPermButton.setSelection(useSpecificPermissions);
		specificPermButton.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				preservePermButton.setSelection(false);
				updateStates();
			}
		});

		forFileFolderCombo = new Combo(specificPermComp, SWT.DROP_DOWN | SWT.READ_ONLY);
		forFileFolderCombo.setItems(COMBO_ITEMS);
		forFileFolderCombo.setText(COMBO_ITEMS[0]);
		forFileFolderCombo.addSelectionListener(new SelectionAdapter()
		{

			@Override
			public void widgetSelected(SelectionEvent e)
			{
				updatePermissionsGroup();
			}
		});

		permissionsGroup = new PermissionsGroup(specificPermComp);
		permissionsGroup.setLayoutData(GridDataFactory.fillDefaults().grab(true, false).span(2, 1).indent(20, 0)
				.create());
		permissionsMap.put(FOR_FILES, PreferenceUtils.getFilePermissions(direction));
		permissionsMap.put(FOR_FOLDERS, PreferenceUtils.getFolderPermissions(direction));
		updatePermissionsGroup();
		permissionsGroup.addListener(new Listener()
		{

			public void permissionsModified()
			{
				permissionsMap.put(forFileFolderCombo.getText(), permissionsGroup.getPermissions());
			}
		});

		updateStates();
	}

	public boolean getUpdatePermissions()
	{
		return updatePermCheckbox.getSelection();
	}

	public boolean getSpecificPermissions()
	{
		return specificPermButton.getSelection();
	}

	public long getFilePermissions()
	{
		return permissionsMap.get(FOR_FILES);
	}

	public long getFolderPermissions()
	{
		return permissionsMap.get(FOR_FOLDERS);
	}

	public void restoreDefaults()
	{
		updatePermCheckbox.setSelection(true);
		preservePermButton.setSelection(false);
		specificPermButton.setSelection(true);
		forFileFolderCombo.setText(COMBO_ITEMS[0]);
		permissionsMap.put(FOR_FILES, PreferenceInitializer.DEFAULT_FILE_PERMISSIONS);
		permissionsMap.put(FOR_FOLDERS, PreferenceInitializer.DEFAULT_DIRECTORY_PERMISSIONS);
		updatePermissionsGroup();
		updateStates();
	}

	private void updatePermissionsGroup()
	{
		if (isForFilesSelected())
		{
			permissionsGroup.setPermissions(permissionsMap.get(FOR_FILES));
		}
		else
		{
			permissionsGroup.setPermissions(permissionsMap.get(FOR_FOLDERS));
		}
	}

	private void updateStates()
	{
		boolean updatePermissions = updatePermCheckbox.getSelection();
		boolean useSpecificPermissions = specificPermButton.getSelection();
		preservePermButton.setEnabled(updatePermissions);
		specificPermButton.setEnabled(updatePermissions);
		forFileFolderCombo.setEnabled(updatePermissions && useSpecificPermissions);
		permissionsGroup.setEnabled(updatePermissions && useSpecificPermissions);
	}

	private boolean isForFilesSelected()
	{
		return FOR_FILES.equals(forFileFolderCombo.getText());
	}
}
