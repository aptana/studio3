/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.deploy.wizard;

import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWizard;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;

import com.aptana.deploy.DeployPlugin;
import com.aptana.deploy.internal.wizard.CapifyProjectPage;
import com.aptana.deploy.internal.wizard.DeployWizardPage;
import com.aptana.deploy.preferences.DeployPreferenceUtil;
import com.aptana.deploy.preferences.IPreferenceConstants.DeployType;

public class DeployWizard extends Wizard implements IWorkbenchWizard
{

	private IProject project;

	@Override
	public boolean performFinish()
	{
		IRunnableWithProgress runnable = null;
		// check what the user chose, then do the heavy lifting, or tell the page to finish...
		IWizardPage currentPage = getContainer().getCurrentPage();
		String pageName = currentPage.getName();
		DeployType type = null;
		String deployEndpointName = null;
		if (CapifyProjectPage.NAME.equals(pageName))
		{
			CapifyProjectPage page = (CapifyProjectPage) currentPage;
			runnable = createCapifyRunnable(page);
			type = DeployType.CAPISTRANO;
		}

		// stores the deploy type and what application or FTP connection it's deploying to
		if (type != null)
		{
			DeployPreferenceUtil.setDeployType(project, type);
			if (deployEndpointName != null)
			{
				DeployPreferenceUtil.setDeployEndpoint(project, deployEndpointName);
			}
		}

		if (runnable != null)
		{
			try
			{
				getContainer().run(true, false, runnable);
			}
			catch (Exception e)
			{
				DeployPlugin.logError(e);
			}
		}
		return true;
	}

	protected IRunnableWithProgress createCapifyRunnable(CapifyProjectPage page)
	{
		IRunnableWithProgress runnable;
		runnable = new IRunnableWithProgress()
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
		return runnable;
	}

	@Override
	public void addPages()
	{
		// Add the first basic page where they choose the deployment option
		addPage(new DeployWizardPage(project));
		setForcePreviousAndNextButtons(true); // we only add one page here, but we calculate the next page
												// dynamically...
	}

	@Override
	public IWizardPage getNextPage(IWizardPage page)
	{
		// delegate to page because we modify the page list dynamically and don't statically add them via addPage
		return page.getNextPage();
	}

	@Override
	public IWizardPage getPreviousPage(IWizardPage page)
	{
		// delegate to page because we modify the page list dynamically and don't statically add them via addPage
		return page.getPreviousPage();
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

	public IProject getProject()
	{
		return project;
	}

	@Override
	public boolean canFinish()
	{
		IWizardPage page = getContainer().getCurrentPage();
		// We don't want getNextPage() getting invoked so early on first page, because it does auth check on Heroku
		// credentials...
		if (page.getName().equals(DeployWizardPage.NAME))
		{
			return false;
		}
		return page.isPageComplete() && page.getNextPage() == null;
	}

	/*
	 * Because we're dynamic and not adding pages the normal way, the pages aren't getting disposed individually. We
	 * need to track what pages are open and dispose them! (non-Javadoc)
	 * @see org.eclipse.jface.wizard.Wizard#dispose()
	 */
	@Override
	public void dispose()
	{
		try
		{
			// Find current page and traverse backwards through all the pages to collect them.
			Set<IWizardPage> pages = new HashSet<IWizardPage>();
			IWizardPage page = getContainer().getCurrentPage();
			while (page != null)
			{
				pages.add(page);
				page = page.getPreviousPage();
			}
			// traverse forward
			page = getContainer().getCurrentPage();
			while (page != null)
			{
				pages.add(page);
				page = page.getNextPage();
			}
			for (IWizardPage aPage : pages)
			{
				aPage.dispose();
			}
			pages = null;
		}
		finally
		{
			super.dispose();
		}
	}
}
