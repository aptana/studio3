/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.workbench.commands;

import java.io.IOException;
import java.text.MessageFormat;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

import com.aptana.core.logging.IdeLog;
import com.aptana.core.resources.IProjectContext;
import com.aptana.core.util.IOUtil;
import com.aptana.core.util.ProcessRunner;
import com.aptana.git.core.model.GitExecutable;
import com.aptana.scripting.ScriptingActivator;
import com.aptana.scripting.model.BundleElement;
import com.aptana.scripting.model.BundleManager;
import com.aptana.scripting.model.BundlePrecedence;
import com.aptana.workbench.WorkbenchPlugin;

/**
 * This job tries to grab down a local copy of a pre-installed application bundle. It will also generate a project for
 * the bundle.
 * 
 * @author cwilliams
 */
public class EditBundleJob extends Job
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
		// Bundle is a project one, so it already exists as a project in the user's workspace. Nothing to do.
		if (bundle.getBundlePrecedence() == BundlePrecedence.PROJECT)
		{
			return Status.OK_STATUS;
		}
		try
		{
			SubMonitor subMonitor = SubMonitor.convert(monitor, 100);

			IPath userBundlesDir = makeUserBundlesDirectory();
			subMonitor.worked(5);

			IPath destRuble = grabBundle(userBundlesDir);
			subMonitor.worked(75);

			createProjectIfNecessary(destRuble, subMonitor.newChild(20));

			// TODO Make the project the "current" one in App Explorer? (not necessary if we just created it, but if it
			// already existed, it may be...)
			subMonitor.done();
		}
		catch (CoreException e)
		{
			IdeLog.logError(WorkbenchPlugin.getDefault(), e);
			return e.getStatus();
		}
		catch (Exception e)
		{
			IdeLog.logError(WorkbenchPlugin.getDefault(), e);
			return new Status(IStatus.ERROR, WorkbenchPlugin.PLUGIN_ID, e.getMessage(), e);
		}
		return Status.OK_STATUS;
	}

	/**
	 * Grabs a copy of the bundle to the destination directory. This attempts to do a git clone if there's a repository
	 * set and it looks like a git URI. It also may try an SVN checkout if it looks like an SVN URI. Lastly if no URI is
	 * set and the bundle is pre-installed we'll do a local recursive copy of the bundle.
	 * 
	 * @param destinationDir
	 * @return
	 * @throws CoreException
	 */
	protected IPath grabBundle(IPath destinationDir) throws CoreException
	{
		IPath destRuble = destinationDir.append(bundle.getBundleDirectory().getName());
		if (destRuble.toFile().isDirectory())
		{
			IdeLog.logInfo(WorkbenchPlugin.getDefault(),
					"Trying to grab bundle, destination directory already exists: " + destRuble.toOSString()); //$NON-NLS-1$
			return destRuble; // Already exists, just return it.
		}

		if (bundle.getRepository() == null)
		{
			copyIfPossible(destRuble);
		}
		else
		{
			grabCopyFromRepository(destinationDir, destRuble);
		}
		return destRuble;
	}

	/**
	 * Makes the directory structure for the User Bundles directory if necessary.
	 * 
	 * @return the pointer to the user bundles dir
	 * @throws CoreException
	 *             if we were unable to make the directory structure for some reason.
	 */
	protected IPath makeUserBundlesDirectory() throws CoreException
	{
		IPath userBundlesDir = Path.fromOSString(BundleManager.getInstance().getUserBundlesPath());
		if (userBundlesDir.toFile().isDirectory())
		{
			return userBundlesDir;
		}
		if (userBundlesDir.toFile().mkdirs())
		{
			return userBundlesDir;
		}
		throw new CoreException(new Status(IStatus.ERROR, ScriptingActivator.PLUGIN_ID,
				Messages.EditBundleJob_CantCreateUserBundlesDir_Error));
	}

	protected void createProjectIfNecessary(IPath projectLocation, IProgressMonitor monitor) throws CoreException
	{
		SubMonitor subMonitor = SubMonitor.convert(monitor, 100);

		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IProject theProject = findMatchingProject(workspace, projectLocation);
		subMonitor.worked(10);
		boolean makeActive = true;
		if (theProject == null)
		{
			// Enforce unique name...
			int counter = 0;
			while (true)
			{
				String projectName = bundle.getDisplayName();
				if (counter > 0)
				{
					projectName += counter;
				}
				counter++;
				// No project exists yet. Let's create on at this location and then open it.
				theProject = workspace.getRoot().getProject(projectName);
				if (!projectExists(theProject)) // it shouldn't...
				{
					IProjectDescription description = workspace.newProjectDescription(theProject.getName());
					description.setLocation(projectLocation);
					theProject.create(description, subMonitor.newChild(45));
					theProject.open(subMonitor.newChild(45));
					makeActive = false;
					break;
				}
			}
		}

		if (makeActive)
		{
			makeProjectActiveInAppExplorer(theProject);
		}

		subMonitor.done();
	}

	private boolean projectExists(IProject theProject)
	{
		if (theProject.exists())
		{
			return true;
		}
		// Could be a case difference on Mac!
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IProject[] projects = workspace.getRoot().getProjects();
		for (IProject project : projects)
		{
			if (project == null)
			{
				continue;
			}
			if (theProject.getName().equalsIgnoreCase(project.getName()))
			{
				return true;
			}
		}
		return false;
	}

	protected void makeProjectActiveInAppExplorer(final IProject theProject)
	{
		PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable()
		{

			public void run()
			{
				IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
				IViewReference[] refs = window.getActivePage().getViewReferences();
				for (IViewReference ref : refs)
				{
					IWorkbenchPart part = ref.getPart(false);
					if (part instanceof IProjectContext)
					{
						IProjectContext projectContext = (IProjectContext) part;
						projectContext.setActiveProject(theProject);
						return;
					}
				}
			}
		});
	}

	/**
	 * Attempts to do a local file copy if the repository is null. We only attempt this if the bundle is application
	 * level. (If it is user level, then it's already where we plan to copy it. If it's project, we shouldn't have to do
	 * anything).
	 * 
	 * @param destRuble
	 * @throws CoreException
	 */
	private void copyIfPossible(IPath destRuble) throws CoreException
	{
		if (bundle.getBundlePrecedence() != BundlePrecedence.APPLICATION)
		{
			// We really should never reach this state, since we shortcut right away for project level bundles; and user
			// level bundles should already exist in the user bundles dir...
			throw new CoreException(new Status(IStatus.ERROR, ScriptingActivator.PLUGIN_ID,
					Messages.EditBundleJob_BundleHasNoRepository_Error));
		}
		try
		{
			IOUtil.copyDirectory(bundle.getBundleDirectory(), destRuble.toFile());
		}
		catch (IOException e)
		{
			throw new CoreException(new Status(IStatus.ERROR, ScriptingActivator.PLUGIN_ID, e.getMessage(), e));
		}
	}

	private void grabCopyFromRepository(IPath workingDirectory, IPath destRuble) throws CoreException
	{
		String repoURI = bundle.getRepository();
		IStatus result = null;
		if (looksLikeGitURI(repoURI))
		{
			if (GitExecutable.instance() == null)
			{
				throw new CoreException(new Status(IStatus.ERROR, ScriptingActivator.PLUGIN_ID,
						Messages.EditBundleJob_RequiresGitError));
			}
			// definitely looks like a git repo
			result = GitExecutable.instance().runInBackground(workingDirectory, "clone", repoURI, //$NON-NLS-1$
					destRuble.toOSString());
		}
		else if (looksLikeSVNURI(repoURI))
		{
			// FIXME What if svn isn't installed?
			// wasn't git, but appears it's probably SVN
			result = new ProcessRunner().runInBackground(workingDirectory, "svn", "checkout", repoURI, //$NON-NLS-1$ //$NON-NLS-2$
					destRuble.toOSString());
		}
		else
		{
			if (GitExecutable.instance() == null)
			{
				throw new CoreException(new Status(IStatus.ERROR, ScriptingActivator.PLUGIN_ID,
						Messages.EditBundleJob_RequiresGitError));
			}
			// we couldn't determine git or SVN, so let's just assume git.
			result = GitExecutable.instance().runInBackground(workingDirectory, "clone", repoURI, //$NON-NLS-1$
					destRuble.toOSString());
		}
		// Non-zero exit code, so we probably had an error...
		if (!result.isOK())
		{
			throw new CoreException(new Status(IStatus.ERROR, ScriptingActivator.PLUGIN_ID,
					Messages.EditBundleJob_GitCloneFailed_Error + result.getMessage()));
		}
	}

	/**
	 * Tries to find the project whose location matches dest
	 * 
	 * @param workspace
	 * @param dest
	 * @return
	 */
	protected IProject findMatchingProject(IWorkspace workspace, IPath dest)
	{
		for (IProject project : workspace.getRoot().getProjects())
		{
			if (dest.equals(project.getLocation()))
			{
				return project;
			}
		}
		return null;
	}

	/**
	 * Attempts to determine if an URI looks like an SVN URI.
	 * 
	 * @param repoURI
	 * @return
	 */
	private boolean looksLikeSVNURI(String repoURI)
	{
		return repoURI.startsWith("svn:") || repoURI.startsWith("svn+") || repoURI.contains("/trunk") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				|| repoURI.contains("/tags") || repoURI.contains("/branches"); //$NON-NLS-1$ //$NON-NLS-2$
	}

	/**
	 * Attempt to determine if an URI is a Git URI.
	 * 
	 * @param repoURI
	 * @return
	 */
	private boolean looksLikeGitURI(String repoURI)
	{
		return repoURI.startsWith("git:") || repoURI.endsWith(".git") || repoURI.contains("github.com"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}
}
