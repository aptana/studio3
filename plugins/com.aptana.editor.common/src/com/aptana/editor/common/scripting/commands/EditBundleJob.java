package com.aptana.editor.common.scripting.commands;

import java.io.File;
import java.io.IOException;
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

import com.aptana.editor.common.CommonEditorPlugin;
import com.aptana.git.core.model.GitExecutable;
import com.aptana.scripting.Activator;
import com.aptana.scripting.model.BundleElement;
import com.aptana.scripting.model.BundleManager;
import com.aptana.scripting.model.BundlePrecedence;
import com.aptana.util.IOUtil;
import com.aptana.util.ProcessUtil;

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
		// Bundle is a project one, so it already exists as a project in the user's workspace. Nothing to do.
		if (bundle.getBundlePrecedence() == BundlePrecedence.PROJECT)
			return Status.OK_STATUS;
		try
		{
			SubMonitor subMonitor = SubMonitor.convert(monitor, 100);

			File userBundlesDir = makeUserBundlesDirectory();
			subMonitor.worked(5);

			File destRuble = grabBundle(userBundlesDir);
			subMonitor.worked(75);

			createProjectIfNecessary(destRuble, subMonitor.newChild(20));

			// TODO Make the project the "current" one in App Explorer? (not necessary if we just created it, but if it
			// already existed, it may be...)
			subMonitor.done();
		}
		catch (CoreException e)
		{
			CommonEditorPlugin.logError(e);
			return e.getStatus();
		}
		catch (Exception e)
		{
			CommonEditorPlugin.logError(e);
			return new Status(IStatus.ERROR, CommonEditorPlugin.PLUGIN_ID, e.getMessage(), e);
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
	protected File grabBundle(File destinationDir) throws CoreException
	{
		File destRuble = new File(destinationDir, bundle.getBundleDirectory().getName());
		if (destRuble.isDirectory())
		{
			CommonEditorPlugin.logInfo("Trying to grab bundle, destination directory already exists: " + destRuble.getAbsolutePath()); //$NON-NLS-1$
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
	protected File makeUserBundlesDirectory() throws CoreException
	{
		File userBundlesDir = new File(BundleManager.getInstance().getUserBundlesPath());
		if (userBundlesDir.isDirectory())
			return userBundlesDir;
		if (userBundlesDir.mkdirs())
			return userBundlesDir;

		throw new CoreException(new Status(IStatus.ERROR, Activator.PLUGIN_ID,
				Messages.EditBundleJob_CantCreateUserBundlesDir_Error));
	}

	protected void createProjectIfNecessary(File projectLocation, IProgressMonitor monitor) throws CoreException
	{
		SubMonitor subMonitor = SubMonitor.convert(monitor, 100);
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		if (findMatchingProject(workspace, projectLocation) != null)
			return; // A project already exists with this location. Return it.

		// No project exists yet. Let's create on at this location and then open it.
		IProject theProject = workspace.getRoot().getProject(bundle.getDisplayName());
		if (!theProject.exists()) // it shouldn't...
		{
			IProjectDescription description = workspace.newProjectDescription(theProject.getName());
			description.setLocation(new Path(projectLocation.getAbsolutePath()));
			theProject.create(description, subMonitor.newChild(50));
			theProject.open(subMonitor.newChild(50));
		}
		subMonitor.done();
	}

	/**
	 * Attempts to do a local file copy if the repository is null. We only attempt this if the bundle is application
	 * level. (If it is user level, then it's already where we plan to copy it. If it's project, we shouldn't have to do
	 * anything).
	 * 
	 * @param destRuble
	 * @throws CoreException
	 */
	private void copyIfPossible(File destRuble) throws CoreException
	{
		if (bundle.getBundlePrecedence() != BundlePrecedence.APPLICATION)
		{
			// We really should never reach this state, since we shortcut right away for project level bundles; and user
			// level bundles should already exist in the user bundles dir...
			throw new CoreException(new Status(IStatus.ERROR, Activator.PLUGIN_ID,
					Messages.EditBundleJob_BundleHasNoRepository_Error));
		}
		try
		{
			IOUtil.copyDirectory(bundle.getBundleDirectory(), destRuble);
		}
		catch (IOException e)
		{
			throw new CoreException(new Status(IStatus.ERROR, Activator.PLUGIN_ID, e.getMessage(), e));
		}
	}

	private void grabCopyFromRepository(File workingDirectory, File destRuble) throws CoreException
	{
		String repoURI = bundle.getRepository();
		Map<Integer, String> result = null;
		if (looksLikeGitURI(repoURI))
		{
			// definitely looks like a git repo
			result = GitExecutable.instance().runInBackground(workingDirectory.getAbsolutePath(), "clone", repoURI, //$NON-NLS-1$
					destRuble.getAbsolutePath());
		}
		else if (looksLikeSVNURI(repoURI))
		{
			// wasn't git, but appears it's probably SVN
			result = ProcessUtil.runInBackground(
					"svn", workingDirectory.getAbsolutePath(), new String[] { "checkout", repoURI, //$NON-NLS-1$ //$NON-NLS-2$
							destRuble.getAbsolutePath() });
		}
		else
		{
			// we couldn't determine git or SVN, so let's just assume git.
			result = GitExecutable.instance().runInBackground(workingDirectory.getAbsolutePath(), "clone", repoURI, //$NON-NLS-1$
					destRuble.getAbsolutePath());
		}
		// Non-zero exit code, so we probably had an error...
		if (result.keySet().iterator().next() != 0)
		{
			throw new CoreException(new Status(IStatus.ERROR, Activator.PLUGIN_ID,
					Messages.EditBundleJob_GitCloneFailed_Error + result.values().iterator().next()));
		}
	}

	/**
	 * Tries to find the project whose location matches dest
	 * 
	 * @param workspace
	 * @param dest
	 * @return
	 */
	protected IProject findMatchingProject(IWorkspace workspace, File dest)
	{
		for (IProject project : workspace.getRoot().getProjects())
		{
			if (project.getLocation().toFile().equals(dest))
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
	@SuppressWarnings("nls")
	private boolean looksLikeSVNURI(String repoURI)
	{
		return repoURI.startsWith("svn:") || repoURI.startsWith("svn+") || repoURI.contains("/trunk")
				|| repoURI.contains("/tags") || repoURI.contains("/branches");
	}

	/**
	 * Attempt to determine if an URI is a Git URI.
	 * 
	 * @param repoURI
	 * @return
	 */
	@SuppressWarnings("nls")
	private boolean looksLikeGitURI(String repoURI)
	{
		return repoURI.startsWith("git:") || repoURI.endsWith(".git") || repoURI.contains("github.com");
	}

}
