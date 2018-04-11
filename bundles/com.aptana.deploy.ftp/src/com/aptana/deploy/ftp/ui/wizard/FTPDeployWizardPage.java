/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.deploy.ftp.ui.wizard;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;

import com.aptana.deploy.ftp.FTPDeployPlugin;
import com.aptana.deploy.preferences.DeployPreferenceUtil;
import com.aptana.ide.core.io.IBaseRemoteConnectionPoint;
import com.aptana.ide.core.io.IConnectionPoint;
import com.aptana.ide.syncing.core.ISiteConnection;
import com.aptana.ide.syncing.core.SiteConnectionUtils;
import com.aptana.ide.syncing.ui.preferences.IPreferenceConstants.SyncDirection;
import com.aptana.ide.syncing.ui.preferences.SyncPreferenceUtil;
import com.aptana.ui.ftp.internal.FTPConnectionPropertyComposite;

@SuppressWarnings("restriction")
public class FTPDeployWizardPage extends WizardPage implements FTPConnectionPropertyComposite.IListener
{

	public static final String NAME = "FTPDeployment"; //$NON-NLS-1$
	private static final String ICON_PATH = "icons/ftp.png"; //$NON-NLS-1$

	private IContainer container;
	private FTPDeployComposite ftpConnectionComposite;
	private IBaseRemoteConnectionPoint connectionPoint;

	protected FTPDeployWizardPage(IContainer container)
	{
		super(NAME, Messages.FTPDeployWizardPage_Title, FTPDeployPlugin.getImageDescriptor(ICON_PATH));
		this.container = container;
		// checks if the project/folder already has an associated FTP connection and fills the info automatically if one
		// exists
		ISiteConnection[] sites = SiteConnectionUtils.findSitesForSource(container, true);
		String lastConnection = DeployPreferenceUtil.getDeployEndpoint(container);
		IConnectionPoint connection;
		for (ISiteConnection site : sites)
		{
			connection = site.getDestination();
			if ((connection != null && connection.getName().equals(lastConnection))
					|| (lastConnection == null && connection instanceof IBaseRemoteConnectionPoint))
			{
				connectionPoint = (IBaseRemoteConnectionPoint) connection;
				break;
			}
		}
	}

	public IBaseRemoteConnectionPoint getConnectionPoint()
	{
		return ftpConnectionComposite.getConnectionPoint();
	}

	public boolean isAutoSyncSelected()
	{
		return ftpConnectionComposite.isAutoSyncSelected();
	}

	public SyncDirection getSyncDirection()
	{
		return ftpConnectionComposite.getSyncDirection();
	}

	public boolean completePage()
	{
		boolean complete = ftpConnectionComposite.completeConnection();
		// persists the auto-sync setting
		boolean autoSync = isAutoSyncSelected();
		IProject project = container.getProject();
		SyncPreferenceUtil.setAutoSync(project, autoSync);
		if (autoSync)
		{
			SyncPreferenceUtil.setAutoSyncDirection(project, getSyncDirection());
		}

		return complete;
	}

	public void createControl(Composite parent)
	{
		ftpConnectionComposite = new FTPDeployComposite(parent, SWT.NONE, connectionPoint, this);
		ftpConnectionComposite
				.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_FILL | GridData.HORIZONTAL_ALIGN_FILL));
		setControl(ftpConnectionComposite);

		initializeDialogUnits(parent);
		Dialog.applyDialogFont(ftpConnectionComposite);

		IProject project = container.getProject();
		boolean autoSync = SyncPreferenceUtil.isAutoSync(project);
		ftpConnectionComposite.setAutoSyncSelected(autoSync);
		if (autoSync)
		{
			ftpConnectionComposite.setSyncDirection(SyncPreferenceUtil.getAutoSyncDirection(project));
		}
		ftpConnectionComposite.validate();
	}

	@Override
	public IWizardPage getNextPage()
	{
		return null;
	}

	public boolean close()
	{
		return false;
	}

	public void error(String message)
	{
		if (message == null)
		{
			setErrorMessage(null);
			setMessage(null);
		}
		else
		{
			setErrorMessage(message);
		}
		setPageComplete(message == null);
	}

	public void layoutShell()
	{
	}

	public void lockUI(boolean lock)
	{
	}

	public void setValid(boolean valid)
	{
	}
}
