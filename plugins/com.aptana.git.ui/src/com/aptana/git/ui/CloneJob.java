/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.git.ui;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
import org.eclipse.ui.statushandlers.StatusManager;

import com.aptana.core.CorePlugin;
import com.aptana.core.util.ProcessUtil;
import com.aptana.git.core.model.GitExecutable;
import com.aptana.git.ui.internal.sharing.ConnectProviderOperation;
import com.aptana.git.ui.internal.wizards.Messages;

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

	public CloneJob(String sourceURI, String dest)
	{
		this(sourceURI, dest, false);
	}

	public CloneJob(String sourceURI, String dest, boolean forceRootAsProject)
	{
		this(sourceURI, dest, forceRootAsProject, false);
	}

	public CloneJob(String sourceURI, String dest, boolean forceRootAsProject, boolean shallow)
	{
		super(Messages.CloneWizard_Job_title);
		setUser(true);
		this.sourceURI = sourceURI;
		this.dest = dest;
		this.forceRootAsProject = forceRootAsProject;
		this.shallowClone = shallow;
	}

	@Override
	public IStatus run(IProgressMonitor monitor)
	{
		SubMonitor subMonitor = SubMonitor.convert(monitor, 1000);
		try
		{
			if (GitExecutable.instance() == null)
			{
				throw new CoreException(new Status(IStatus.ERROR, GitUIPlugin.getPluginId(),
						Messages.CloneJob_UnableToFindGitExecutableError));
			}

			IPath gitPath = GitExecutable.instance().path();
			Map<String, String> env = GitExecutable.instance().getSSHEnvironment();
			Process p = null;
			if (shallowClone)
			{
				p = ProcessUtil.run(gitPath.toOSString(), null, env,
						"clone", "--progress", "--depth", "1", "--", sourceURI, dest); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
			}
			else
			{
				p = ProcessUtil.run(gitPath.toOSString(), null, env, "clone", "--progress", "--", sourceURI, dest); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			}
			if (p == null)
			{
				throw new CoreException(new Status(IStatus.ERROR, GitUIPlugin.getPluginId(), MessageFormat.format(
						Messages.CloneJob_UnableToLaunchGitError, sourceURI, dest)));
			}

			Runnable runnable = new CloneRunnable(p, subMonitor.newChild(900));
			Thread t = new Thread(runnable);
			t.start();
			t.join();

			subMonitor.setWorkRemaining(100);
			Collection<File> existingProjects = new ArrayList<File>();
			if (!forceRootAsProject)
			{
				// Search the children of the repo for existing projects!
				existingProjects = collectProjectFilesFromDirectory(new File(dest), null, subMonitor.newChild(25));
			}
			if (existingProjects.isEmpty())
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
			GitUIPlugin.logError(e);
			return e.getStatus();
		}
		catch (Throwable e)
		{
			GitUIPlugin.logError(e.getMessage(), e);
			return new Status(IStatus.ERROR, GitUIPlugin.getPluginId(), e.getMessage(), e);
		}
		finally
		{
			subMonitor.done();
		}
		return Status.OK_STATUS;
	}

	private class CloneRunnable implements Runnable
	{
		private Process p;
		private IProgressMonitor monitor;

		public CloneRunnable(Process p, IProgressMonitor monitor)
		{
			this.p = p;
			this.monitor = monitor;
		}

		public void run()
		{
			SubMonitor sub = SubMonitor.convert(monitor, 100);
			// FIXME Only sniff for "receiving objects", which is the meat of the operation
			Pattern percentPattern = Pattern.compile("^Receiving objects:\\s+(\\d+)%\\s\\((\\d+)/(\\d+)\\).+");
			InputStreamReader isr = null;
			int lastPercent = 0;
			try
			{
				isr = new InputStreamReader(p.getErrorStream(), "UTF-8"); //$NON-NLS-1$
				BufferedReader br = new BufferedReader(isr);
				String line = null;
				while ((line = br.readLine()) != null)
				{
					if (monitor.isCanceled())
					{
						p.destroy();
						return;
					}
					sub.subTask(line);
					// Else, read in the line and see if we can sniff progress
					Matcher m = percentPattern.matcher(line);
					if (m.find())
					{
						String percent = m.group(1);
						int percentInt = Integer.parseInt(percent);
						if (percentInt > lastPercent)
						{
							sub.worked(percentInt - lastPercent);
							lastPercent = percentInt;
						}
					}
				}
			}
			catch (IOException ioe)
			{
				CorePlugin.logError(ioe.getMessage(), ioe);
			}
			finally
			{
				if (isr != null)
				{
					try
					{
						isr.close();
					}
					catch (Exception e)
					{
					}
				}
				sub.done();
			}
		}
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
				GitUIPlugin.logError(exception.getMessage(), exception);
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
						GitUIPlugin.logError(exception.getMessage(), exception);
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

			ConnectProviderOperation connectProviderOperation = new ConnectProviderOperation(project);
			connectProviderOperation.run(sub.newChild(20));
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
			// monitor.beginTask(
			// UIText.WizardProjectsImportPage_CreateProjectsTask, 100);
			project.create(desc, new SubProgressMonitor(monitor, 30));
			project.open(IResource.BACKGROUND_REFRESH, new SubProgressMonitor(monitor, 50));
		}
		finally
		{
			monitor.done();
		}
	}

	private class ProjectRecord
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

}
