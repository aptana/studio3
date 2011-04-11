package com.aptana.deploy.ftp.ui.wizard;

import java.lang.reflect.InvocationTargetException;
import java.text.MessageFormat;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;

import com.aptana.deploy.ftp.FTPDeployPlugin;
import com.aptana.deploy.ftp.FTPDeployProvider;
import com.aptana.deploy.preferences.DeployPreferenceUtil;
import com.aptana.deploy.wizard.IDeployWizard;
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

public class FTPDeployWizard extends Wizard implements IDeployWizard
{

	private IProject project;

	@Override
	public void addPages()
	{
		super.addPages();

		addPage(new FTPDeployWizardPage(project));
	}

	public void init(IWorkbench workbench, IStructuredSelection selection)
	{
		Object element = selection.getFirstElement();
		if (element instanceof IResource)
		{
			IResource resource = (IResource) element;
			this.project = resource.getProject();
		}
	}

	IProject getProject()
	{
		return this.project;
	}

	@Override
	public boolean performFinish()
	{
		IWizardPage currentPage = getContainer().getCurrentPage();
		FTPDeployWizardPage page = (FTPDeployWizardPage) currentPage;
		IRunnableWithProgress runnable = createFTPDeployRunnable(page);

		DeployPreferenceUtil.setDeployType(project, FTPDeployProvider.ID);
		DeployPreferenceUtil.setDeployEndpoint(project, page.getConnectionPoint().getName());

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
					ISiteConnection[] sites = SiteConnectionUtils.findSites(project, destinationConnectionPoint);
					if (sites.length == 0)
					{
						// creates the site to link the project with the FTP connection
						IConnectionPoint sourceConnectionPoint = SyncUtils.findOrCreateConnectionPointFor(project);
						CoreIOPlugin.getConnectionPointManager().addConnectionPoint(sourceConnectionPoint);
						site = SiteConnectionUtils.createSite(MessageFormat.format("{0} <-> {1}", project.getName(), //$NON-NLS-1$
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
						String lastConnection = DeployPreferenceUtil.getDeployEndpoint(project);
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
						action.setSelection(new StructuredSelection(project));
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
