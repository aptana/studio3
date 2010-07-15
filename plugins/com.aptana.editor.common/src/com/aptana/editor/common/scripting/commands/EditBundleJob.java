package com.aptana.editor.common.scripting.commands;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.Map;

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
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.osgi.service.prefs.BackingStoreException;

import com.aptana.core.util.IOUtil;
import com.aptana.core.util.ProcessUtil;
import com.aptana.editor.common.CommonEditorPlugin;
import com.aptana.git.core.model.GitExecutable;
import com.aptana.scripting.Activator;
import com.aptana.scripting.model.BundleElement;
import com.aptana.scripting.model.BundleManager;
import com.aptana.scripting.model.BundlePrecedence;

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
	protected IPath grabBundle(IPath destinationDir) throws CoreException
	{
		IPath destRuble = destinationDir.append(bundle.getBundleDirectory().getName());
		if (destRuble.toFile().isDirectory())
		{
			CommonEditorPlugin
					.logInfo("Trying to grab bundle, destination directory already exists: " + destRuble.toOSString()); //$NON-NLS-1$
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
			return userBundlesDir;
		if (userBundlesDir.toFile().mkdirs())
			return userBundlesDir;

		throw new CoreException(new Status(IStatus.ERROR, Activator.PLUGIN_ID,
				Messages.EditBundleJob_CantCreateUserBundlesDir_Error));
	}

	protected void createProjectIfNecessary(IPath projectLocation, IProgressMonitor monitor) throws CoreException
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
			description.setLocation(projectLocation);
			theProject.create(description, subMonitor.newChild(50));
			theProject.open(subMonitor.newChild(50));
		}
		else
		{
			// FIXME These refer to IDs/prefs in a plugin that depends on this one!
			try
			{
				IEclipsePreferences prefs = new InstanceScope().getNode("com.aptana.explorer"); // ExplorerPlugin.PLUGIN_ID //$NON-NLS-1$
				prefs.put("activeProject", theProject.getName()); // com.aptana.explorer.IPreferenceConstants.ACTIVE_PROJECT //$NON-NLS-1$
				prefs.flush();
			}
			catch (BackingStoreException e)
			{
				CommonEditorPlugin.logError(e);
			}
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
	private void copyIfPossible(IPath destRuble) throws CoreException
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
			IOUtil.copyDirectory(bundle.getBundleDirectory(), destRuble.toFile());
		}
		catch (IOException e)
		{
			throw new CoreException(new Status(IStatus.ERROR, Activator.PLUGIN_ID, e.getMessage(), e));
		}
	}

	private void grabCopyFromRepository(IPath workingDirectory, IPath destRuble) throws CoreException
	{
		String repoURI = bundle.getRepository();
		Map<Integer, String> result = null;
		if (looksLikeGitURI(repoURI))
		{
			if (GitExecutable.instance() == null)
			{
				throw new CoreException(new Status(IStatus.ERROR, Activator.PLUGIN_ID,
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
			result = ProcessUtil.runInBackground("svn", workingDirectory, new String[] { "checkout", repoURI, //$NON-NLS-1$ //$NON-NLS-2$
					destRuble.toOSString() });
		}
		else
		{
			if (GitExecutable.instance() == null)
			{
				throw new CoreException(new Status(IStatus.ERROR, Activator.PLUGIN_ID,
						Messages.EditBundleJob_RequiresGitError));
			}
			// we couldn't determine git or SVN, so let's just assume git.
			result = GitExecutable.instance().runInBackground(workingDirectory, "clone", repoURI, //$NON-NLS-1$
					destRuble.toOSString());
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
	protected IProject findMatchingProject(IWorkspace workspace, IPath dest)
	{
		for (IProject project : workspace.getRoot().getProjects())
		{
			if (project.getLocation().equals(dest))
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
