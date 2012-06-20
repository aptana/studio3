/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.deploy.capistrano.ui.wizard;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;

import com.aptana.core.logging.IdeLog;
import com.aptana.deploy.capistrano.CapistranoDeployProvider;
import com.aptana.deploy.capistrano.CapistranoPlugin;
import com.aptana.deploy.preferences.DeployPreferenceUtil;
import com.aptana.deploy.ui.wizard.AbstractDeployWizard;

public class CapistranoDeployWizard extends AbstractDeployWizard
{

	private static final String IMG_PATH = "icons/newproj_wiz.png"; //$NON-NLS-1$

	public void init(IWorkbench workbench, IStructuredSelection selection)
	{
		super.init(workbench, selection);
		setDefaultPageImageDescriptor(CapistranoPlugin.getImageDescriptor(IMG_PATH));
	}

	@Override
	public void addPages()
	{
		super.addPages();

		if (InstallCapistranoGemPage.isCapistranoGemInstalled())
		{
			addPage(new CapifyProjectPage());
		}
		else
		{
			addPage(new InstallCapistranoGemPage());
		}
	}

	@Override
	public boolean performFinish()
	{
		IWizardPage currentPage = getContainer().getCurrentPage();
		CapifyProjectPage page = (CapifyProjectPage) currentPage;
		IRunnableWithProgress runnable = createCapifyRunnable(page);

		DeployPreferenceUtil.setDeployType(getProject(), CapistranoDeployProvider.ID);

		try
		{
			getContainer().run(true, false, runnable);
		}
		catch (Exception e)
		{
			IdeLog.logError(CapistranoPlugin.getDefault(), e);
		}

		return true;
	}

	protected IRunnableWithProgress createCapifyRunnable(CapifyProjectPage page)
	{
		return new IRunnableWithProgress()
		{

			public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException
			{
				SubMonitor sub = SubMonitor.convert(monitor, 100);
				try
				{
					// Just open the config/deploy.rb file in an editor
					PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable()
					{

						public void run()
						{
							try
							{
								IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow()
										.getActivePage();
								IFile file = getProject().getFile(new Path("config").append("deploy.rb")); //$NON-NLS-1$ //$NON-NLS-2$
								IDE.openEditor(page, file);
							}
							catch (PartInitException e)
							{
								throw new RuntimeException(e);
							}
						}
					});
				}
				catch (Exception e)
				{
					throw new InvocationTargetException(e);
				}
				finally
				{
					sub.done();
				}
			}
		};
	}
}