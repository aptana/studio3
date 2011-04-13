/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.deploy.redhat.ui.wizard;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;

import com.aptana.deploy.preferences.DeployPreferenceUtil;
import com.aptana.deploy.redhat.RedHatAPI;
import com.aptana.deploy.redhat.RedHatDeployProvider;
import com.aptana.deploy.redhat.RedHatPlugin;
import com.aptana.deploy.wizard.AbstractDeployWizard;

public class RedHatDeployWizard extends AbstractDeployWizard
{

	@Override
	public void addPages()
	{
		super.addPages();

		RedHatAPI api = new RedHatAPI();
		IStatus status = api.authenticate();
		if (status.isOK())
		{
			addPage(new RedHatDeployWizardPage());
		}
		else
		{
			addPage(new RedHatSignupWizardPage());
			// FIXME Do I need to do this?
			addPage(new RedHatDeployWizardPage());
		}
	}

	public void init(IWorkbench workbench, IStructuredSelection selection)
	{
		super.init(workbench, selection);
		setDefaultPageImageDescriptor(RedHatPlugin.getImageDescriptor(RedHatPlugin.WIZARD_IMAGE));
	}

	@Override
	public boolean performFinish()
	{
		IWizardPage currentPage = getContainer().getCurrentPage();
		RedHatDeployWizardPage page = (RedHatDeployWizardPage) currentPage;
		IRunnableWithProgress runnable = createRedHatDeployRunnable(page);

		DeployPreferenceUtil.setDeployType(getProject(), RedHatDeployProvider.ID);

		if (runnable != null)
		{
			try
			{
				getContainer().run(true, false, runnable);
			}
			catch (Exception e)
			{
				RedHatPlugin.logError(e);
			}
		}
		return true;
	}

	protected IRunnableWithProgress createRedHatDeployRunnable(RedHatDeployWizardPage page)
	{
		IRunnableWithProgress runnable;
		final String appname = page.getAppName();
		final String type = page.getType();
		final IPath destination = page.getDestination();
		runnable = new IRunnableWithProgress()
		{

			public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException
			{
				PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable()
				{

					public void run()
					{
						RedHatAPI api = new RedHatAPI();
						api.createApp(appname, type, destination);
					}
				});
			}

		};
		return runnable;
	}
}
