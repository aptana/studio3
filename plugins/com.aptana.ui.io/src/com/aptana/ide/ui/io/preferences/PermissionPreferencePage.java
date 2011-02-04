/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.ide.ui.io.preferences;

import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.osgi.service.prefs.BackingStoreException;

import com.aptana.ide.core.io.CoreIOPlugin;
import com.aptana.ide.core.io.preferences.IPreferenceConstants;
import com.aptana.ide.core.io.preferences.PreferenceUtils;

/**
 * @author Michael Xia (mxia@aptana.com)
 */
public class PermissionPreferencePage extends PreferencePage implements IWorkbenchPreferencePage
{

	private PermissionsGroup fFilePermissions;
	private PermissionsGroup fDirectoryPermissions;

	/**
	 * Constructor.
	 */
	public PermissionPreferencePage()
	{
	}

	/**
	 * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	public void init(IWorkbench workbench)
	{
		setDescription(Messages.PermissionPreferencePage_Notes);
	}

	/**
	 * @see org.eclipse.jface.preference.PreferencePage#performOk()
	 */
	public boolean performOk()
	{
		IEclipsePreferences prefs = (new InstanceScope()).getNode(CoreIOPlugin.PLUGIN_ID);
		prefs.putLong(IPreferenceConstants.FILE_PERMISSION, fFilePermissions.getPermissions());
		prefs.putLong(IPreferenceConstants.DIRECTORY_PERMISSION, fDirectoryPermissions.getPermissions());
		try
		{
			prefs.flush();
		}
		catch (BackingStoreException e)
		{
		}
		return super.performOk();
	}

	/**
	 * @see org.eclipse.jface.preference.PreferencePage#createContents(org.eclipse.swt.widgets.Composite)
	 */
	protected Control createContents(Composite parent)
	{
		Composite main = new Composite(parent, SWT.NONE);
		main.setLayout(GridLayoutFactory.fillDefaults().create());

		fFilePermissions = new PermissionsGroup(main);
		fFilePermissions.setText(Messages.PermissionPreferencePage_FileGroupTitle);
		fFilePermissions.setPermissions(PreferenceUtils.getFilePermissions());
		fFilePermissions.getControl().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

		fDirectoryPermissions = new PermissionsGroup(main);
		fDirectoryPermissions.setText(Messages.PermissionPreferencePage_DirectoryGroupTitle);
		fDirectoryPermissions.setPermissions(PreferenceUtils.getDirectoryPermissions());
		fDirectoryPermissions.getControl().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

		return main;
	}

	/**
	 * @see org.eclipse.jface.preference.PreferencePage#performDefaults()
	 */
	protected void performDefaults()
	{
		fFilePermissions
				.setPermissions(com.aptana.ide.core.io.preferences.PreferenceInitializer.DEFAULT_FILE_PERMISSIONS);
		fDirectoryPermissions
				.setPermissions(com.aptana.ide.core.io.preferences.PreferenceInitializer.DEFAULT_DIRECTORY_PERMISSIONS);
		super.performDefaults();
	}
}
