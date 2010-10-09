/**
 * This file Copyright (c) 2005-2010 Aptana, Inc. This program is
 * dual-licensed under both the Aptana Public License and the GNU General
 * Public license. You may elect to use one or the other of these licenses.
 * 
 * This program is distributed in the hope that it will be useful, but
 * AS-IS and WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE, TITLE, or
 * NONINFRINGEMENT. Redistribution, except as permitted by whichever of
 * the GPL or APL you select, is prohibited.
 *
 * 1. For the GPL license (GPL), you can redistribute and/or modify this
 * program under the terms of the GNU General Public License,
 * Version 3, as published by the Free Software Foundation.  You should
 * have received a copy of the GNU General Public License, Version 3 along
 * with this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 * 
 * Aptana provides a special exception to allow redistribution of this file
 * with certain other free and open source software ("FOSS") code and certain additional terms
 * pursuant to Section 7 of the GPL. You may view the exception and these
 * terms on the web at http://www.aptana.com/legal/gpl/.
 * 
 * 2. For the Aptana Public License (APL), this program and the
 * accompanying materials are made available under the terms of the APL
 * v1.0 which accompanies this distribution, and is available at
 * http://www.aptana.com/legal/apl/.
 * 
 * You may view the GPL, Aptana's exception and additional terms, and the
 * APL in the file titled license.html at the root of the corresponding
 * plugin containing this source file.
 * 
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.git.ui;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

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
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchListener;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.core.IStreamListener;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.debug.core.model.IStreamMonitor;
import org.eclipse.ui.statushandlers.StatusManager;

import com.aptana.git.core.model.GitExecutable;
import com.aptana.git.ui.internal.Launcher;
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
	protected IStatus run(IProgressMonitor monitor)
	{
		SubMonitor subMonitor = SubMonitor.convert(monitor, 500);
		try
		{
			if (GitExecutable.instance() == null)
			{
				throw new CoreException(new Status(IStatus.ERROR, GitUIPlugin.getPluginId(),
						Messages.CloneJob_UnableToFindGitExecutableError));
			}
			ILaunchManager manager = DebugPlugin.getDefault().getLaunchManager();
			ILaunchListener listener = new ILaunchListener()
			{

				public void launchRemoved(ILaunch launch)
				{
				}

				public void launchChanged(ILaunch launch)
				{
					// TODO Make sure this is our launch!
					IProcess[] processes = launch.getProcesses();
					if (processes != null)
					{
						IProcess process = processes[0];
						// TODO Sniff the process output for percentages?
						process.getStreamsProxy().getOutputStreamMonitor().addListener(new IStreamListener()
						{

							public void streamAppended(String text, IStreamMonitor monitor)
							{
								System.out.println(text);
							}
						});
						process.getStreamsProxy().getErrorStreamMonitor().addListener(new IStreamListener()
						{

							public void streamAppended(String text, IStreamMonitor monitor)
							{
								System.err.println(text);
								// TODO Look for "Checking out files: \d+% (\d+/\d+) and report progress accordingly
							}
						});
					}
				}

				public void launchAdded(ILaunch launch)
				{
					// TODO Auto-generated method stub
				}
			};
			manager.addLaunchListener(listener);

			// FIXME This doesn't ever run in bg in 3.6!
			ILaunch launch;
			if (shallowClone)
			{
				launch = Launcher.launch(GitExecutable.instance().path().toOSString(), null, subMonitor.newChild(100),
						"clone", "--depth", "1", "--", sourceURI, dest); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			}
			else
			{
				launch = Launcher.launch(GitExecutable.instance().path().toOSString(), null, subMonitor.newChild(100),
						"clone", "--", sourceURI, dest); //$NON-NLS-1$ //$NON-NLS-2$
			}
			if (launch == null)
			{
				manager.removeLaunchListener(listener);
				throw new CoreException(new Status(IStatus.ERROR, GitUIPlugin.getPluginId(), MessageFormat.format(
						Messages.CloneJob_UnableToLaunchGitError, sourceURI, dest)));
			}
			while (!launch.isTerminated())
			{
				if (subMonitor.isCanceled())
					return Status.CANCEL_STATUS;
				Thread.yield();
			}
			manager.removeLaunchListener(listener);
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
				// FIXME what if there is no .project file?

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
				IPath locationPath = new Path(record.projectSystemFile.getAbsolutePath());

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

			doCreateProject(project, record.description, new SubProgressMonitor(monitor, 80));

			ConnectProviderOperation connectProviderOperation = new ConnectProviderOperation(project);
			connectProviderOperation.run(new SubProgressMonitor(monitor, 20));
		}
		finally
		{
			if (monitor != null)
				monitor.done();
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
					// couldn't get project name
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
