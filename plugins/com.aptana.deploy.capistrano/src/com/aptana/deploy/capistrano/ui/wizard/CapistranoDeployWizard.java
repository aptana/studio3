package com.aptana.deploy.capistrano.ui.wizard;

import java.lang.reflect.InvocationTargetException;

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
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;

import com.aptana.deploy.capistrano.CapistranoPlugin;
import com.aptana.deploy.preferences.DeployPreferenceUtil;
import com.aptana.deploy.preferences.IPreferenceConstants.DeployType;
import com.aptana.deploy.wizard.IDeployWizard;

public class CapistranoDeployWizard extends Wizard implements IDeployWizard
{

	private IProject project;

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
		CapifyProjectPage page = (CapifyProjectPage) currentPage;
		DeployType type = DeployType.CAPISTRANO;
		IRunnableWithProgress runnable = createCapifyRunnable(page);

		DeployPreferenceUtil.setDeployType(project, type);

		try
		{
			getContainer().run(true, false, runnable);
		}
		catch (Exception e)
		{
			CapistranoPlugin.logError(e);
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
}