/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.git.ui;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.ui.statushandlers.StatusManager;
import org.osgi.service.prefs.BackingStoreException;

import com.aptana.core.logging.IdeLog;
import com.aptana.core.util.EclipseUtil;
import com.aptana.git.core.GitPlugin;
import com.aptana.git.core.IDebugScopes;
import com.aptana.git.core.IPreferenceConstants;
import com.aptana.git.core.model.GitExecutable;
import com.aptana.git.core.model.GitRepository;
import com.aptana.git.ui.internal.sharing.ConnectProviderOperation;
import com.aptana.git.ui.internal.wizards.Messages;
import com.aptana.projects.primary.natures.IPrimaryNatureContributor;
import com.aptana.projects.primary.natures.PrimaryNaturesManager;
import com.aptana.ui.util.UIUtils;

//FIXME Move to some different package?
public class CloneJob extends Job
{

	/**
	 * The name of the folder containing metadata information for the workspace.
	 */
	private static final String METADATA_FOLDER = ".metadata"; //$NON-NLS-1$

	private String sourceURI;
	private String dest;
	private boolean forceRootAsProject;
	private boolean shallowClone;

	private Set<IProject> createdProjects;

	private Map<String, IPrimaryNatureContributor> natureContributors = new HashMap<String, IPrimaryNatureContributor>();

	public CloneJob(String sourceURI, String dest)
	{
		this(sourceURI, dest, false);
	}

	public CloneJob(String sourceURI, String dest, boolean forceRootAsProject)
	{
		this(sourceURI, dest, forceRootAsProject, false);
	}

	/**
	 * @param sourceURI
	 *            The source repo we're cloning from
	 * @param dest
	 *            The destination to clone to.
	 * @param forceRootAsProject
	 *            boolean. If true, we force the root of the repo to be the root of a new project
	 * @param connectProvider
	 *            boolean. if set to false, we do not attach our git support and we remove the .git dir from the
	 *            project. Please note that if connectProvider is false, we also do not clone over teh full history of
	 *            the repo since it will be deleted.
	 */
	public CloneJob(String sourceURI, String dest, boolean forceRootAsProject, boolean shallow)
	{
		super(Messages.CloneWizard_Job_title);
		setUser(true);
		this.sourceURI = sourceURI;
		this.dest = dest;
		this.forceRootAsProject = forceRootAsProject;
		this.shallowClone = shallow;
		this.createdProjects = new HashSet<IProject>();
		natureContributors = PrimaryNaturesManager.getManager().getContributorsMap();
	}

	@Override
	public IStatus run(IProgressMonitor monitor)
	{
		SubMonitor subMonitor = SubMonitor.convert(monitor, 1000);
		try
		{
			if (getGitExecutable() == null)
			{
				throw new CoreException(new Status(IStatus.ERROR, GitUIPlugin.getPluginId(),
						Messages.CloneJob_UnableToFindGitExecutableError));
			}

			IStatus result = getGitExecutable().clone(sourceURI, Path.fromOSString(dest), shallowClone,
					subMonitor.newChild(900));
			if (!result.isOK())
			{
				return result;
			}

			Collection<File> existingProjects = null;
			if (!forceRootAsProject)
			{
				// Search the children of the repo for existing projects!
				existingProjects = collectProjectFilesFromDirectory(new File(dest), null, subMonitor.newChild(25));
			}
			// If there are no existing projects, or just one, make the repo root the project root.
			if (existingProjects == null || existingProjects.size() <= 1)
			{
				// No projects found. Turn the root of the repo into a project!
				createExistingProject(new File(dest, IProjectDescription.DESCRIPTION_FILE_NAME),
						subMonitor.newChild(75));
			}
			else
			{
				// TODO Should probably prompt user which projects to import
				int step = 75 / existingProjects.size();
				for (File file : existingProjects)
				{
					if (file == null)
					{
						continue;
					}
					createExistingProject(file, subMonitor.newChild(step));
				}
			}
		}
		catch (CoreException e)
		{
			IdeLog.log(GitUIPlugin.getDefault(), e.getStatus());
			return e.getStatus();
		}
		catch (Throwable e)
		{
			IdeLog.logError(GitUIPlugin.getDefault(), e, IDebugScopes.DEBUG);
			return new Status(IStatus.ERROR, GitUIPlugin.getPluginId(), e.getMessage(), e);
		}
		finally
		{
			subMonitor.done();
		}
		return Status.OK_STATUS;
	}

	private void setNatureFromContributions(IProject project)
	{
		List<String> potentialNatures = new ArrayList<String>(natureContributors.size());
		for (String natureId : natureContributors.keySet())
		{
			IPrimaryNatureContributor primaryNatureContributor = natureContributors.get(natureId);
			int primaryNatureRank = primaryNatureContributor.getPrimaryNatureRank(project.getLocation());
			if (primaryNatureRank == IPrimaryNatureContributor.CAN_BE_PRIMARY)
			{
				potentialNatures.add(natureId);
			}
			else if (primaryNatureRank == IPrimaryNatureContributor.IS_PRIMARY)
			{
				potentialNatures.add(0, natureId);
			}
		}
		if (potentialNatures.size() > 0)
		{
			String[] natureIds = (String[]) potentialNatures.toArray(new String[potentialNatures.size()]);
			try
			{
				IProjectDescription description = project.getDescription();
				description.setNatureIds(natureIds);
				project.setDescription(description, null);
			}
			catch (CoreException e)
			{
				IdeLog.logError(GitUIPlugin.getDefault(), e);
			}
		}
	}

	protected GitExecutable getGitExecutable()
	{
		return GitExecutable.instance();
	}

	/**
	 * Collect the list of .project files that are under directory into files.
	 * 
	 * @param directory
	 * @param directoriesVisited
	 *            Set of canonical paths of directories, used as recursion guard
	 * @param monitor
	 *            The monitor to report to
	 * @return boolean <code>true</code> if the operation was completed.
	 */
	private Collection<File> collectProjectFilesFromDirectory(File directory, Set<String> directoriesVisited,
			IProgressMonitor monitor)
	{

		if (monitor.isCanceled())
		{
			return Collections.emptyList();
		}
		// monitor.subTask(NLS.bind(
		// UIText.WizardProjectsImportPage_CheckingMessage, directory
		// .getPath()));
		File[] contents = directory.listFiles();
		if (contents == null)
		{
			return Collections.emptyList();
		}

		Collection<File> files = new HashSet<File>();

		// Initialize recursion guard for recursive symbolic links
		if (directoriesVisited == null)
		{
			directoriesVisited = new HashSet<String>();
			try
			{
				directoriesVisited.add(directory.getCanonicalPath());
			}
			catch (IOException exception)
			{
				IdeLog.logError(GitUIPlugin.getDefault(), exception, IDebugScopes.DEBUG);
				StatusManager.getManager()
						.handle(new Status(IStatus.ERROR, GitUIPlugin.getPluginId(), exception.getLocalizedMessage(),
								exception));
			}
		}

		// first look for project description files
		for (File file : contents)
		{
			if (file == null)
			{
				continue;
			}
			if (file.isFile() && file.getName().equals(IProjectDescription.DESCRIPTION_FILE_NAME))
			{
				files.add(file);
				// don't search sub-directories since we can't have nested
				// projects
				return files;
			}
		}
		// no project description found, so recurse into sub-directories
		for (File file : contents)
		{
			if (file == null)
			{
				continue;
			}
			if (file.isDirectory())
			{
				if (!file.getName().equals(METADATA_FOLDER))
				{
					try
					{
						String canonicalPath = file.getCanonicalPath();
						if (!directoriesVisited.add(canonicalPath))
						{
							// already been here --> do not recurse
							continue;
						}
					}
					catch (IOException exception)
					{
						IdeLog.logError(GitUIPlugin.getDefault(), exception, IDebugScopes.DEBUG);
						StatusManager.getManager().handle(
								new Status(IStatus.ERROR, GitUIPlugin.getPluginId(), exception.getLocalizedMessage(),
										exception));

					}
					files.addAll(collectProjectFilesFromDirectory(file, directoriesVisited, monitor));
				}
			}
		}
		return files;
	}

	/**
	 * Imports a project with a pre-existing .project file. If it is successful returns true.
	 * 
	 * @param existingDotProjectFile
	 * @param monitor
	 * @return boolean <code>true</code> if successful
	 * @throws CoreException
	 */
	private boolean createExistingProject(final File existingDotProjectFile, IProgressMonitor monitor)
			throws CoreException
	{
		SubMonitor sub = SubMonitor.convert(monitor, 100);
		try
		{
			ProjectRecord record = new ProjectRecord(existingDotProjectFile);
			String projectName = record.getProjectName();
			final IWorkspace workspace = ResourcesPlugin.getWorkspace();
			final IProject project = workspace.getRoot().getProject(projectName);
			// createdProjects.add(project);
			if (record.description == null)
			{
				// error case
				record.description = workspace.newProjectDescription(projectName);
				IPath locationPath = new Path(record.projectSystemFile.getParent());

				// If it is under the root use the default location
				if (Platform.getLocation().isPrefixOf(locationPath))
				{
					record.description.setLocation(null);
				}
				else
				{
					record.description.setLocation(locationPath);
				}
			}
			else
			{
				record.description.setName(projectName);
			}
			sub.worked(5);

			doCreateProject(project, record.description, sub.newChild(75));

			if (!this.shallowClone)
			{
				ConnectProviderOperation connectProviderOperation = new ConnectProviderOperation(project);
				connectProviderOperation.run(sub.newChild(20));
			}
			else
			{
				// explicitly delete the .git folder
				IFolder gitFolder = project.getFolder(GitRepository.GIT_DIR);
				if (gitFolder.exists())
				{
					try
					{
						gitFolder.delete(true, sub.newChild(20));
					}
					catch (CoreException e)
					{
						IdeLog.logError(GitUIPlugin.getDefault(), e);
					}
				}
			}
			UIUtils.getDisplay().asyncExec(new Runnable()
			{
				public void run()
				{
					// Set the primary natures - Ideally this does not need to run in UI thread, however some nature
					// contributors rely on UI calls to determine the nature of the project.
					setNatureFromContributions(project);
				}
			});
		}
		finally
		{
			if (sub != null)
			{
				sub.done();
			}
		}
		return true;
	}

	protected void doCreateProject(final IProject project, final IProjectDescription desc, IProgressMonitor monitor)
			throws CoreException
	{
		try
		{
			// Turn off auto-attaching git temporarily here!
			boolean autoAttach = Platform.getPreferencesService().getBoolean(GitPlugin.getPluginId(),
					IPreferenceConstants.AUTO_ATTACH_REPOS, true, null);

			IEclipsePreferences prefs = EclipseUtil.instanceScope().getNode(GitPlugin.PLUGIN_ID);
			if (autoAttach)
			{
				// Default value is true, so assuem they explicitly set false in instance prefs
				prefs.putBoolean(IPreferenceConstants.AUTO_ATTACH_REPOS, false);
				try
				{
					prefs.flush();
				}
				catch (BackingStoreException e)
				{
					// ignore
				}
			}

			project.create(desc, new SubProgressMonitor(monitor, 30));
			project.open(IResource.BACKGROUND_REFRESH, new SubProgressMonitor(monitor, 50));
			createdProjects.add(project);

			if (autoAttach)
			{
				prefs.remove(IPreferenceConstants.AUTO_ATTACH_REPOS);
				try
				{
					prefs.flush();
				}
				catch (BackingStoreException e)
				{
					// ignore
				}
			}
		}
		finally
		{
			monitor.done();
		}
	}

	private static class ProjectRecord
	{

		File projectSystemFile;
		String projectName;
		IProjectDescription description;

		/**
		 * Create a record for a project based on the info in the file.
		 * 
		 * @param file
		 */
		ProjectRecord(File file)
		{
			projectSystemFile = file;
			setProjectName();
		}

		/**
		 * Set the name of the project based on the projectFile.
		 */
		private void setProjectName()
		{
			// If we don't have the project name try again
			if (projectName == null)
			{
				try
				{
					IPath path = new Path(projectSystemFile.getPath());
					// if the file is in the default location, use the directory
					// name as the project name
					if (isDefaultLocation(path))
					{
						projectName = path.segment(path.segmentCount() - 2);
						description = ResourcesPlugin.getWorkspace().newProjectDescription(projectName);
					}
					else
					{
						description = ResourcesPlugin.getWorkspace().loadProjectDescription(path);
						projectName = description.getName();
					}
				}
				catch (CoreException e)
				{
					// couldn't get project name, use parent directory name
					projectName = projectSystemFile.getParentFile().getName();
				}
			}
		}

		/**
		 * Returns whether the given project description file path is in the default location for a project
		 * 
		 * @param path
		 *            The path to examine
		 * @return Whether the given path is the default location for a project
		 */
		private boolean isDefaultLocation(IPath path)
		{
			// The project description file must at least be within the project,
			// which is within the workspace location
			if (path.segmentCount() < 2)
				return false;
			return path.removeLastSegments(2).toFile().equals(Platform.getLocation().toFile());
		}

		/**
		 * Get the name of the project
		 * 
		 * @return String
		 */
		public String getProjectName()
		{
			return projectName;
		}
	}

	/**
	 * Returns an unmodifiable set of IProjects that got created by this job.
	 * 
	 * @return
	 */
	public Set<IProject> getCreatedProjects()
	{
		return Collections.unmodifiableSet(this.createdProjects);
	}
}
