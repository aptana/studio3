/**
 * Aptana Studio
 * Copyright (c) 2005-2013 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.samples.ui.project;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.text.MessageFormat;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceStatus;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExecutableExtension;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.ide.undo.CreateProjectOperation;
import org.eclipse.ui.ide.undo.WorkspaceUndoUtil;
import org.eclipse.ui.statushandlers.IStatusAdapterConstants;
import org.eclipse.ui.statushandlers.StatusAdapter;
import org.eclipse.ui.statushandlers.StatusManager;
import org.eclipse.ui.wizards.newresource.BasicNewProjectResourceWizard;
import org.eclipse.ui.wizards.newresource.BasicNewResourceWizard;

import com.aptana.core.logging.IdeLog;
import com.aptana.core.util.ArrayUtil;
import com.aptana.core.util.FileUtil;
import com.aptana.core.util.ResourceUtil;
import com.aptana.git.core.model.GitRepository;
import com.aptana.git.ui.CloneJob;
import com.aptana.git.ui.actions.DisconnectHandler;
import com.aptana.projects.ProjectData;
import com.aptana.projects.ProjectsPlugin;
import com.aptana.projects.wizards.AbstractNewProjectWizard;
import com.aptana.projects.wizards.ProjectWizardContributionManager;
import com.aptana.samples.handlers.ISampleProjectHandler;
import com.aptana.samples.model.IProjectSample;
import com.aptana.samples.ui.SamplesUIPlugin;
import com.aptana.ui.util.UIUtils;

/**
 * @author Ingo Muschenetz
 * @author Michael Xia TODO Extend from {@link AbstractNewProjectWizard}
 */
public class NewSampleProjectWizard extends BasicNewResourceWizard implements IExecutableExtension
{

	// the wizard ID
	public static final String ID = "com.aptana.samples.ui.NewSampleProjectWizard"; //$NON-NLS-1$

	private static final String NEWPROJECT_WIZARD = "BasicNewProjectResourceWizard"; //$NON-NLS-1$

	private IProjectSample sample;
	private SampleNewProjectCreationPage mainPage;
	private IProject newProject;
	private IConfigurationElement configElement;

	/**
	 * A wizard to create a new sample project.
	 * 
	 * @param localSample
	 *            the root sample entry
	 */
	public NewSampleProjectWizard(IProjectSample sample)
	{
		this.sample = sample;
		initDialogSettings();
	}

	@Override
	public void addPages()
	{
		super.addPages();

		mainPage = new SampleNewProjectCreationPage("basicNewProjectPage", sample.getNatures()); //$NON-NLS-1$
		mainPage.setTitle(Messages.NewSampleProjectWizard_ProjectPage_Title);
		mainPage.setDescription(Messages.NewSampleProjectWizard_ProjectPage_Description);
		addPage(mainPage);

		String name = sample.getName();
		if (name != null)
		{
			mainPage.setInitialProjectName(name);
		}

		// Add contributed pages
		ProjectWizardContributionManager projectWizardContributionManager = ProjectsPlugin.getDefault()
				.getProjectWizardContributionManager();
		IWizardPage[] extraPages = projectWizardContributionManager.createPages(null, sample.getNatures());
		if (!ArrayUtil.isEmpty(extraPages))
		{
			for (IWizardPage page : extraPages)
			{
				addPage(page);
			}
		}

		// Finalize pages using contributors
		projectWizardContributionManager.finalizeWizardPages(getPages(), sample.getNatures());
	}

	@Override
	public boolean performFinish()
	{
		// TODO If location already exists, pop up a confirm dialog?
		createNewProject();
		if (newProject == null)
		{
			return false;
		}

		updatePerspective();
		selectAndReveal(newProject);
		openIndexFile();

		return true;
	}

	public void setInitializationData(IConfigurationElement config, String propertyName, Object data)
			throws CoreException
	{
		configElement = config;
	}

	@Override
	public void init(IWorkbench workbench, IStructuredSelection currentSelection)
	{
		super.init(workbench, currentSelection);
		setNeedsProgressMonitor(true);
		setWindowTitle(Messages.NewSampleProjectWizard_Title);
	}

	protected void updatePerspective()
	{
		BasicNewProjectResourceWizard.updatePerspective(configElement);
	}

	private void initDialogSettings()
	{
		IDialogSettings workbenchSettings = SamplesUIPlugin.getDefault().getDialogSettings();
		IDialogSettings section = workbenchSettings.getSection(NEWPROJECT_WIZARD);
		if (section == null)
		{
			section = workbenchSettings.addNewSection(NEWPROJECT_WIZARD);
		}
		setDialogSettings(section);
	}

	private IProject createNewProject()
	{
		if (newProject != null)
		{
			return newProject;
		}

		// get a project handle
		final IProject newProjectHandle = mainPage.getProjectHandle();
		// get a project descriptor
		URI location = null;
		if (!mainPage.useDefaults())
		{
			location = mainPage.getLocationURI();
		}

		IProjectDescription description = ResourceUtil.getProjectDescription(mainPage.getLocationPath(),
				sample.getNatures(), ArrayUtil.NO_STRINGS);
		description.setName(newProjectHandle.getName());
		description.setLocationURI(location);

		try
		{
			if (sample.isRemote())
			{
				cloneFromGit(sample.getLocation(), newProjectHandle, description);
			}
			else
			{
				ProjectData projectData = mainPage.getProjectData();

				// Initialize the basic project data
				projectData.project = newProjectHandle;
				projectData.directory = mainPage.getLocationURI().getRawPath();
				projectData.appURL = "http://"; // TODO: not used here. //$NON-NLS-1$
				doBasicCreateProject(newProjectHandle, description, sample, projectData);
				doPostProjectCreation(newProjectHandle);
			}
		}
		catch (CoreException e)
		{
			return null;
		}

		newProject = newProjectHandle;
		return newProject;
	}

	private void doBasicCreateProject(final IProject project, final IProjectDescription description,
			final IProjectSample sample, final ProjectData projectData) throws CoreException
	{
		// create the new project operation
		IRunnableWithProgress op = new IRunnableWithProgress()
		{
			public void run(IProgressMonitor monitor) throws InvocationTargetException
			{
				try
				{
					CreateProjectOperation op = new CreateProjectOperation(description,
							Messages.NewSampleProjectWizard_CreateOp_Title);
					// see bug https://bugs.eclipse.org/bugs/show_bug.cgi?id=219901
					// directly execute the operation so that the undo state is
					// not preserved. Making this undoable resulted in too many
					// accidental file deletions.
					op.execute(monitor, WorkspaceUndoUtil.getUIInfoAdapter(getShell()));
					IWorkspaceRunnable runnable = new IWorkspaceRunnable()
					{

						public void run(IProgressMonitor monitor) throws CoreException
						{
							IStatus status = sample.createNewProject(project, projectData, monitor);
							if (status != null && !status.isOK())
							{
								IdeLog.logError(SamplesUIPlugin.getDefault(), status.getMessage(),
										status.getException());
							}
						}
					};
					ResourcesPlugin.getWorkspace().run(runnable, monitor);
				}
				catch (CoreException e)
				{
					throw new InvocationTargetException(e);
				}
				catch (ExecutionException e)
				{
					throw new InvocationTargetException(e);
				}

			}
		};

		// run the new project creation operation
		try
		{
			getContainer().run(true, true, op);
		}
		catch (InterruptedException e)
		{
			throw new CoreException(new Status(IStatus.ERROR, SamplesUIPlugin.PLUGIN_ID, e.getMessage(), e));
		}
		catch (InvocationTargetException e)
		{
			Throwable t = e.getTargetException();
			if (t instanceof ExecutionException && t.getCause() instanceof CoreException)
			{
				CoreException cause = (CoreException) t.getCause();
				StatusAdapter status;
				if (cause.getStatus().getCode() == IResourceStatus.CASE_VARIANT_EXISTS)
				{
					status = new StatusAdapter(new Status(IStatus.WARNING, SamplesUIPlugin.PLUGIN_ID,
							MessageFormat.format(Messages.NewSampleProjectWizard_Warning_DirectoryExists,
									project.getName()), cause));
				}
				else
				{
					status = new StatusAdapter(new Status(cause.getStatus().getSeverity(), SamplesUIPlugin.PLUGIN_ID,
							Messages.NewSampleProjectWizard_CreationProblems, cause));
				}
				status.setProperty(IStatusAdapterConstants.TITLE_PROPERTY,
						Messages.NewSampleProjectWizard_CreationProblems);
				StatusManager.getManager().handle(status, StatusManager.BLOCK);
			}
			else
			{
				StatusAdapter status = new StatusAdapter(new Status(IStatus.WARNING, SamplesUIPlugin.PLUGIN_ID, 0,
						MessageFormat.format(Messages.NewSampleProjectWizard_InternalError, t.getMessage()), t));
				status.setProperty(IStatusAdapterConstants.TITLE_PROPERTY,
						Messages.NewSampleProjectWizard_CreationProblems);
				StatusManager.getManager().handle(status, StatusManager.LOG | StatusManager.BLOCK);
			}
		}
	}

	private void cloneFromGit(String gitURL, final IProject projectHandle, final IProjectDescription projectDescription)
	{
		IPath path = mainPage.getLocationPath();
		// when default is used, getLocationPath() only returns the workspace root, so needs to append the project name
		// to the path
		if (mainPage.useDefaults())
		{
			path = path.append(projectDescription.getName());
		}

		// Wipe the destination directory if it already exists, or git clone will fail.
		File directory = path.toFile();
		if (directory.exists())
		{
			FileUtil.deleteRecursively(directory);
		}

		// FIXME Run an IRunnableWithProgress in wizard container, have it just do job.run(monitor)!
		Job job = new CloneJob(gitURL, path.toOSString(), true, true);
		job.addJobChangeListener(new JobChangeAdapter()
		{
			@Override
			public void done(IJobChangeEvent event)
			{
				if (!event.getResult().isOK())
				{
					return;
				}

				try
				{
					projectHandle.setDescription(projectDescription, null);
					projectHandle.refreshLocal(IResource.DEPTH_INFINITE, null);
				}
				catch (CoreException e)
				{
					IdeLog.logError(SamplesUIPlugin.getDefault(), e);
				}

				// Stop tracking the git repo
				DisconnectHandler.disconnect(projectHandle, null);
				// Delete the .gitignore file and the .git folder
				File toDelete = new File(projectHandle.getLocation().toFile(), GitRepository.GITIGNORE);
				if (toDelete.exists())
				{
					toDelete.delete();
				}
				toDelete = new File(projectHandle.getLocation().toFile(), GitRepository.GIT_DIR);
				if (toDelete.exists())
				{
					FileUtil.deleteRecursively(toDelete);
				}
				doPostProjectCreation(projectHandle);
			}
		});
		job.schedule();
	}

	private void doPostProjectCreation(IProject newProject)
	{
		ISampleProjectHandler projectHandler = sample.getProjectHandler();
		if (projectHandler != null)
		{
			ProjectData projectData = mainPage.getProjectData();
			// Match the App name to the project name
			projectData.projectName = newProject.getName();
			projectHandler.projectCreated(newProject, projectData);
		}

		// Allow the project contributors to do work
		ProjectWizardContributionManager projectWizardContributionManager = ProjectsPlugin.getDefault()
				.getProjectWizardContributionManager();
		final IStatus contributorStatus = projectWizardContributionManager.performProjectFinish(newProject,
				new NullProgressMonitor());
		if (contributorStatus != null && contributorStatus.getSeverity() == Status.ERROR)
		{
			UIUtils.showErrorMessage(Messages.NewSampleProjectWizard_ServicesError, contributorStatus.getMessage());
		}
	}

	private void openIndexFile()
	{
		IFile indexFile = newProject.getFile("index.html"); //$NON-NLS-1$
		if (indexFile.exists())
		{
			IWorkbenchPage page = UIUtils.getActivePage();
			if (page != null)
			{
				try
				{
					IDE.openEditor(page, indexFile);
				}
				catch (PartInitException e)
				{
					IdeLog.logError(SamplesUIPlugin.getDefault(), Messages.NewSampleProjectWizard_ERR_OpenIndexFile, e);
				}
			}
		}
	}
}
