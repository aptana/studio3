package com.aptana.git.ui.internal.wizards;

import java.io.File;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IImportWizard;
import org.eclipse.ui.IWorkbench;

import com.aptana.git.core.model.GitExecutable;
import com.aptana.git.ui.internal.Launcher;

public class CloneWizard extends Wizard implements IImportWizard
{

	private RepositorySelectionPage cloneSource;

	@Override
	public boolean performFinish()
	{
		final String sourceURI = cloneSource.getSource();
		final String dest = cloneSource.getDestination();
		Job job = new Job("Cloning git repo")
		{
			@Override
			protected IStatus run(IProgressMonitor monitor)
			{
				SubMonitor subMonitor = SubMonitor.convert(monitor, 200);
				try
				{
					ILaunch launch = Launcher.launch(GitExecutable.instance().path(), null, "clone", sourceURI, dest);
					while (!launch.isTerminated())
					{
						if (subMonitor.isCanceled())
							return Status.CANCEL_STATUS;
						Thread.yield();
					}
					subMonitor.worked(100);
					String projectName = dest;
					if (projectName.lastIndexOf(File.separator) != -1)
					{
						projectName = projectName.substring(projectName.lastIndexOf(File.separator) + 1);
					}
					IProjectDescription desc = ResourcesPlugin.getWorkspace().newProjectDescription(projectName);
					IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
					IPath locationPath = new Path(dest);
					if (Platform.getLocation().isPrefixOf(locationPath)) {
						desc.setLocation(null);
					} else {
						desc.setLocation(locationPath);
					}
					desc.setName(projectName);
					project.create(desc, subMonitor.newChild(30));
					project.open(IResource.BACKGROUND_REFRESH, subMonitor.newChild(70));
				}
				catch (CoreException e)
				{
					return e.getStatus();
				}
				finally
				{
					subMonitor.done();
				}
				return Status.OK_STATUS;
			}
		};
		job.setUser(true);
		job.schedule();
		return true;
	}

	public void init(IWorkbench workbench, IStructuredSelection selection)
	{
		cloneSource = new RepositorySelectionPage();
	}

	@Override
	public void addPages()
	{
		addPage(cloneSource);
	}
}
