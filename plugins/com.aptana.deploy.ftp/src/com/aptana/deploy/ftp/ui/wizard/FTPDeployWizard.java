/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.deploy.ftp.ui.wizard;

import java.lang.reflect.InvocationTargetException;
import java.text.MessageFormat;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;

import com.aptana.deploy.ftp.FTPDeployPlugin;
import com.aptana.deploy.ftp.FTPDeployProvider;
import com.aptana.deploy.preferences.DeployPreferenceUtil;
import com.aptana.deploy.wizard.AbstractDeployWizard;
import com.aptana.ide.core.io.CoreIOPlugin;
import com.aptana.ide.core.io.IConnectionPoint;
import com.aptana.ide.syncing.core.ISiteConnection;
import com.aptana.ide.syncing.core.SiteConnectionUtils;
import com.aptana.ide.syncing.core.SyncingPlugin;
import com.aptana.ide.syncing.ui.actions.BaseSyncAction;
import com.aptana.ide.syncing.ui.actions.DownloadAction;
import com.aptana.ide.syncing.ui.actions.SynchronizeProjectAction;
import com.aptana.ide.syncing.ui.actions.UploadAction;
import com.aptana.ide.syncing.ui.internal.SyncUtils;
import com.aptana.ide.syncing.ui.preferences.IPreferenceConstants.SyncDirection;

public class FTPDeployWizard extends AbstractDeployWizard
{

	@Override
	public void addPages()
	{
		super.addPages();

		addPage(new FTPDeployWizardPage(getProject()));
	}

	@Override
	public boolean performFinish()
	{
		IWizardPage currentPage = getContainer().getCurrentPage();
		FTPDeployWizardPage page = (FTPDeployWizardPage) currentPage;
		IRunnableWithProgress runnable = createFTPDeployRunnable(page);

		DeployPreferenceUtil.setDeployType(getProject(), FTPDeployProvider.ID);
		DeployPreferenceUtil.setDeployEndpoint(getProject(), page.getConnectionPoint().getName());

		if (runnable != null)
		{
			try
			{
				getContainer().run(true, false, runnable);
			}
			catch (Exception e)
			{
				FTPDeployPlugin.logError(e);
			}
		}
		return true;
	}

	protected IRunnableWithProgress createFTPDeployRunnable(FTPDeployWizardPage page)
	{
		if (!page.completePage())
		{
			return null;
		}
		final IConnectionPoint destinationConnectionPoint = page.getConnectionPoint();
		final boolean isAutoSyncSelected = page.isAutoSyncSelected();
		final SyncDirection direction = page.getSyncDirection();
		final IWorkbenchPart activePart = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
				.getActivePart();

		IRunnableWithProgress runnable = new IRunnableWithProgress()
		{

			public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException
			{
				SubMonitor sub = SubMonitor.convert(monitor, 100);
				try
				{
					ISiteConnection site = null;
					ISiteConnection[] sites = SiteConnectionUtils.findSites(getProject(), destinationConnectionPoint);
					if (sites.length == 0)
					{
						// creates the site to link the project with the FTP connection
						IConnectionPoint sourceConnectionPoint = SyncUtils.findOrCreateConnectionPointFor(getProject());
						CoreIOPlugin.getConnectionPointManager().addConnectionPoint(sourceConnectionPoint);
						site = SiteConnectionUtils.createSite(
								MessageFormat.format("{0} <-> {1}", getProject().getName(), //$NON-NLS-1$
										destinationConnectionPoint.getName()), sourceConnectionPoint,
								destinationConnectionPoint);
						SyncingPlugin.getSiteConnectionManager().addSiteConnection(site);
					}
					else if (sites.length == 1)
					{
						// the site to link the project with the FTP connection already exists
						site = sites[0];
					}
					else
					{
						// multiple FTP connections are associated with the project; finds the last one
						// try for last remembered site first
						String lastConnection = DeployPreferenceUtil.getDeployEndpoint(getProject());
						if (lastConnection != null)
						{
							site = SiteConnectionUtils.getSiteWithDestination(lastConnection, sites);
						}
					}

					if (isAutoSyncSelected)
					{
						BaseSyncAction action = null;
						switch (direction)
						{
							case UPLOAD:
								action = new UploadAction();
								break;
							case DOWNLOAD:
								action = new DownloadAction();
								break;
							case BOTH:
								action = new SynchronizeProjectAction();
						}
						action.setActivePart(null, activePart);
						action.setSelection(new StructuredSelection(getProject()));
						action.setSelectedSite(site);
						action.run(null);
					}
				}
				finally
				{
					sub.done();
				}
			}
		};
		return runnable;
	}

}
