/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.ui.ftp.preferences;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.osgi.service.prefs.BackingStoreException;

import com.aptana.core.util.EclipseUtil;
import com.aptana.core.util.StringUtil;
import com.aptana.filesystem.ftp.FTPPlugin;
import com.aptana.filesystem.ftp.preferences.FTPPreferenceInitializer;
import com.aptana.filesystem.ftp.preferences.IFTPPreferenceConstants;
import com.aptana.ide.core.io.CoreIOPlugin;
import com.aptana.ide.core.io.preferences.IPreferenceConstants;
import com.aptana.ide.core.io.preferences.PreferenceInitializer;
import com.aptana.ide.core.io.preferences.PreferenceUtils;
import com.aptana.ide.ui.io.preferences.PermissionsGroup;

/**
 * @author Michael Xia (mxia@appcelerator.com)
 */
public class FTPPreferencePage extends PreferencePage implements IWorkbenchPreferencePage
{

	private PermissionsGroup fFilePermissions;
	private PermissionsGroup fDirectoryPermissions;
	private Text fKeepAliveText;

	/**
	 * Constructor.
	 */
	public FTPPreferencePage()
	{
	}

	public void init(IWorkbench workbench)
	{
		setDescription(Messages.FTPPreferencePage_Notes);
	}

	@Override
	public boolean performOk()
	{
		IEclipsePreferences prefs = (EclipseUtil.instanceScope()).getNode(CoreIOPlugin.PLUGIN_ID);
		prefs.putLong(IPreferenceConstants.FILE_PERMISSION, fFilePermissions.getPermissions());
		prefs.putLong(IPreferenceConstants.DIRECTORY_PERMISSION, fDirectoryPermissions.getPermissions());
		try
		{
			prefs.flush();
		}
		catch (BackingStoreException e)
		{
		}

		prefs = (EclipseUtil.instanceScope()).getNode(FTPPlugin.PLUGIN_ID);
		prefs.putInt(IFTPPreferenceConstants.KEEP_ALIVE_TIME, Integer.parseInt(fKeepAliveText.getText()));
		try
		{
			prefs.flush();
		}
		catch (BackingStoreException e)
		{
		}
		return super.performOk();
	}

	@Override
	protected Control createContents(Composite parent)
	{
		Composite main = new Composite(parent, SWT.NONE);
		main.setLayout(GridLayoutFactory.fillDefaults().create());

		fFilePermissions = new PermissionsGroup(main);
		fFilePermissions.setText(Messages.FTPPreferencePage_FileGroupTitle);
		fFilePermissions.setPermissions(PreferenceUtils.getFilePermissions());
		fFilePermissions.getControl().setLayoutData(GridDataFactory.fillDefaults().grab(true, false).create());

		fDirectoryPermissions = new PermissionsGroup(main);
		fDirectoryPermissions.setText(Messages.FTPPreferencePage_DirectoryGroupTitle);
		fDirectoryPermissions.setPermissions(PreferenceUtils.getDirectoryPermissions());
		fDirectoryPermissions.getControl().setLayoutData(GridDataFactory.fillDefaults().grab(true, false).create());

		Composite keepAlive = new Composite(main, SWT.NONE);
		keepAlive.setLayout(GridLayoutFactory.fillDefaults().numColumns(2).create());
		keepAlive.setLayoutData(GridDataFactory.fillDefaults().grab(true, false).create());

		Label label = new Label(keepAlive, SWT.NONE);
		label.setText(Messages.FTPPreferencePage_LBL_KeepAlive);
		fKeepAliveText = new Text(keepAlive, SWT.BORDER);
		int initialTime = Platform.getPreferencesService().getInt(FTPPlugin.PLUGIN_ID,
				IFTPPreferenceConstants.KEEP_ALIVE_TIME, FTPPreferenceInitializer.DEFAULT_KEEP_ALIVE_MINUTES, null);
		fKeepAliveText.setText(String.valueOf(initialTime));
		fKeepAliveText.setLayoutData(GridDataFactory.swtDefaults().align(SWT.FILL, SWT.CENTER).grab(true, false)
				.create());
		fKeepAliveText.addModifyListener(new ModifyListener()
		{

			public void modifyText(ModifyEvent e)
			{
				validate();
			}
		});

		return main;
	}

	@Override
	protected void performDefaults()
	{
		fFilePermissions.setPermissions(PreferenceInitializer.DEFAULT_FILE_PERMISSIONS);
		fDirectoryPermissions.setPermissions(PreferenceInitializer.DEFAULT_DIRECTORY_PERMISSIONS);
		fKeepAliveText.setText(String.valueOf(FTPPreferenceInitializer.DEFAULT_KEEP_ALIVE_MINUTES));

		super.performDefaults();
	}

	private void validate()
	{
		String error = null;
		String keepAliveMins = fKeepAliveText.getText();
		if (StringUtil.isEmpty(keepAliveMins))
		{
			error = Messages.FTPPreferencePage_ERR_Invalid_KeepAlive_Time;
		}
		else
		{
			// makes sure the keep-alive time is a positive integer
			try
			{
				int mins = Integer.parseInt(keepAliveMins);
				if (mins <= 0)
				{
					throw new NumberFormatException();
				}
			}
			catch (NumberFormatException e)
			{
				error = Messages.FTPPreferencePage_ERR_Invalid_KeepAlive_Time;
			}
		}
		setErrorMessage(error);
		setValid(error == null);
	}
}
