package com.aptana.editor.common.scripting.commands;

import java.io.File;
import java.text.MessageFormat;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.core.runtime.jobs.Job;

import com.aptana.git.core.model.GitExecutable;
import com.aptana.scripting.Activator;
import com.aptana.scripting.model.BundleElement;
import com.aptana.scripting.model.BundleManager;

/**
 * This job tries to grab down a local copy of a pre-installed application bundle. It will also generate a project for
 * the bundle.
 * 
 * @author cwilliams
 */
class EditBundleJob extends Job
{

	private BundleElement bundle;

	public EditBundleJob(BundleElement bundle)
	{
		super(MessageFormat.format(Messages.EditBundleJob_Name, bundle.getDisplayName()));
		this.bundle = bundle;
	}

	@Override
	protected IStatus run(IProgressMonitor monitor)
	{
		try
		{
			SubMonitor subMonitor = SubMonitor.convert(monitor, 100);
			String path = BundleManager.getInstance().getUserBundlesPath();
			File userBundlesDir = new File(path);
			if (!userBundlesDir.isDirectory())
			{
				if (!userBundlesDir.mkdirs())
				{
					return new Status(IStatus.ERROR, Activator.PLUGIN_ID, Messages.EditBundleJob_CantCreateUserBundlesDir_Error);
				}
			}
			subMonitor.worked(5);

			File destRuble = new File(userBundlesDir, bundle.getBundleDirectory().getName());
			if (!destRuble.isDirectory())
			{
				// run git clone...
				// TODO What about bundles in SVN?!
				String repoURI = bundle.getRepository();
				if (repoURI == null)
				{
					// TODO Do a file copy...
					return new Status(IStatus.ERROR, Activator.PLUGIN_ID,
							Messages.EditBundleJob_BundleHasNoRepository_Error);
				}
				Map<Integer, String> result = GitExecutable.instance().runInBackground(path, "clone", repoURI, //$NON-NLS-1$
						destRuble.getAbsolutePath());
				if (result.keySet().iterator().next() != 0)
				{
					return new Status(IStatus.ERROR, Activator.PLUGIN_ID, Messages.EditBundleJob_GitCloneFailed_Error
							+ result.values().iterator().next());
				}
			}
			subMonitor.worked(75);

			IWorkspace workspace = ResourcesPlugin.getWorkspace();
			IProject theProject = null;
			IProject[] projects = workspace.getRoot().getProjects();
			for (IProject project : projects)
			{
				if (project.getLocation().toFile().equals(destRuble))
				{
					theProject = project;
					break;
				}
			}
			if (theProject == null)
			{
				theProject = workspace.getRoot().getProject(bundle.getDisplayName());
				if (!theProject.exists()) // it shouldn't...
				{
					IProjectDescription description = workspace.newProjectDescription(theProject.getName());
					description.setLocation(new Path(destRuble.getAbsolutePath()));
					theProject.create(description, subMonitor.newChild(10));
					theProject.open(subMonitor.newChild(10));
				}
			}
			else
			{
				// TODO Make the existing project the "current" one in App Explorer?
			}
			subMonitor.done();
		}
		catch (CoreException e)
		{
			return e.getStatus();
		}
		return Status.OK_STATUS;
	}

}
