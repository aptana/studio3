/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.ui.ftp.preferences;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.osgi.service.prefs.BackingStoreException;

import com.aptana.core.logging.IdeLog;
import com.aptana.core.util.EclipseUtil;
import com.aptana.core.util.StringUtil;
import com.aptana.filesystem.ftp.FTPPlugin;
import com.aptana.filesystem.ftp.preferences.FTPPreferenceInitializer;
import com.aptana.filesystem.ftp.preferences.IFTPPreferenceConstants;
import com.aptana.ide.core.io.preferences.PermissionDirection;
import com.aptana.ide.core.io.preferences.PreferenceUtils;
import com.aptana.ui.ftp.FTPUIPlugin;

/**
 * @author Michael Xia (mxia@appcelerator.com)
 */
public class FTPPreferencePage extends PreferencePage implements IWorkbenchPreferencePage
{

	private UpdatePermissionsComposite uploadPermComposite;
	private UpdatePermissionsComposite downloadPermComposite;
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
		PreferenceUtils.setUpdatePermissions(uploadPermComposite.getUpdatePermissions(), PermissionDirection.UPLOAD);
		PreferenceUtils
				.setSpecificPermissions(uploadPermComposite.getSpecificPermissions(), PermissionDirection.UPLOAD);
		PreferenceUtils.setFilePermissions(uploadPermComposite.getFilePermissions(), PermissionDirection.UPLOAD);
		PreferenceUtils.setFolderPermissions(uploadPermComposite.getFolderPermissions(), PermissionDirection.UPLOAD);
		PreferenceUtils
				.setUpdatePermissions(downloadPermComposite.getUpdatePermissions(), PermissionDirection.DOWNLOAD);
		PreferenceUtils.setSpecificPermissions(downloadPermComposite.getSpecificPermissions(),
				PermissionDirection.DOWNLOAD);
		PreferenceUtils.setFilePermissions(downloadPermComposite.getFilePermissions(), PermissionDirection.DOWNLOAD);
		PreferenceUtils
				.setFolderPermissions(downloadPermComposite.getFolderPermissions(), PermissionDirection.DOWNLOAD);

		IEclipsePreferences prefs = InstanceScope.INSTANCE.getNode(FTPPlugin.PLUGIN_ID);
		prefs.putInt(IFTPPreferenceConstants.KEEP_ALIVE_TIME, Integer.parseInt(fKeepAliveText.getText()));
		try
		{
			prefs.flush();
		}
		catch (BackingStoreException e)
		{
			IdeLog.logError(FTPUIPlugin.getDefault(), e);
		}
		return super.performOk();
	}

	@Override
	protected Control createContents(Composite parent)
	{
		Composite main = new Composite(parent, SWT.NONE);
		main.setLayout(GridLayoutFactory.fillDefaults().create());

		Group group = new Group(main, SWT.NONE);
		group.setText(Messages.FTPPreferencePage_LBL_Uploads);
		group.setLayout(GridLayoutFactory.fillDefaults().create());
		group.setLayoutData(GridDataFactory.fillDefaults().grab(true, false).create());
		uploadPermComposite = new UpdatePermissionsComposite(group, PermissionDirection.UPLOAD);
		uploadPermComposite.setLayoutData(GridDataFactory.fillDefaults().grab(true, true).create());

		group = new Group(main, SWT.NONE);
		group.setText(Messages.FTPPreferencePage_LBL_Downloads);
		group.setLayout(GridLayoutFactory.fillDefaults().create());
		group.setLayoutData(GridDataFactory.fillDefaults().grab(true, false).create());
		downloadPermComposite = new UpdatePermissionsComposite(group, PermissionDirection.DOWNLOAD);
		downloadPermComposite.setLayoutData(GridDataFactory.fillDefaults().grab(true, true).create());

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
		uploadPermComposite.restoreDefaults();
		downloadPermComposite.restoreDefaults();
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
					throw new NumberFormatException("negative"); //$NON-NLS-1$
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
