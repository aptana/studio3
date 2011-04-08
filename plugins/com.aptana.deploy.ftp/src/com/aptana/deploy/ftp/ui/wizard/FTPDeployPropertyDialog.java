/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.deploy.ftp.ui.wizard;

import org.eclipse.core.resources.IProject;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import com.aptana.ide.core.io.IBaseRemoteConnectionPoint;
import com.aptana.ide.syncing.ui.preferences.SyncPreferenceUtil;
import com.aptana.ui.ftp.internal.FTPConnectionPropertyComposite;
import com.aptana.ui.secureftp.dialogs.CommonFTPConnectionPointPropertyDialog;

/**
 * @author Michael Xia (mxia@aptana.com)
 */
@SuppressWarnings("restriction")
public class FTPDeployPropertyDialog extends CommonFTPConnectionPointPropertyDialog
{

	private IProject fProject;

	public FTPDeployPropertyDialog(Shell parentShell)
	{
		super(parentShell);
	}

	public IProject getProject()
	{
		return fProject;
	}

	public void setProject(IProject project)
	{
		fProject = project;
	}

	@Override
	protected Control createDialogArea(Composite parent)
	{
		Control control = super.createDialogArea(parent);
		FTPDeployComposite deployComposite = (FTPDeployComposite) getConnectionComposite();
		boolean autoSync = SyncPreferenceUtil.isAutoSync(fProject);
		deployComposite.setAutoSyncSelected(autoSync);
		if (autoSync)
		{
			deployComposite.setSyncDirection(SyncPreferenceUtil.getAutoSyncDirection(fProject));
		}
		return control;
	}

	@Override
	protected void okPressed()
	{
		// persists the auto-sync setting
		FTPDeployComposite deployComposite = (FTPDeployComposite) getConnectionComposite();
		boolean autoSync = deployComposite.isAutoSyncSelected();
		SyncPreferenceUtil.setAutoSync(fProject, autoSync);
		if (autoSync)
		{
			SyncPreferenceUtil.setAutoSyncDirection(fProject, deployComposite.getSyncDirection());
		}
		super.okPressed();
	}

	@Override
	protected FTPConnectionPropertyComposite createConnectionComposite(Composite parent,
			IBaseRemoteConnectionPoint connectionPoint)
	{
		return new FTPDeployComposite(parent, SWT.NONE, connectionPoint, this);
	}
}
