/**
 * Aptana Studio
 * Copyright (c) 2005-2013 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.projects.wizards;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
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
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.dialogs.WizardNewProjectReferencePage;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.ide.undo.CreateProjectOperation;
import org.eclipse.ui.ide.undo.WorkspaceUndoUtil;
import org.eclipse.ui.statushandlers.IStatusAdapterConstants;
import org.eclipse.ui.statushandlers.StatusAdapter;
import org.eclipse.ui.statushandlers.StatusManager;
import org.eclipse.ui.wizards.newresource.BasicNewProjectResourceWizard;
import org.eclipse.ui.wizards.newresource.BasicNewResourceWizard;

import com.aptana.core.logging.IdeLog;
import com.aptana.core.projects.templates.IProjectTemplate;
import com.aptana.core.projects.templates.ProjectTemplate;
import com.aptana.core.projects.templates.TemplateType;
import com.aptana.core.util.ArrayUtil;
import com.aptana.core.util.ProcessStatus;
import com.aptana.core.util.ResourceUtil;
import com.aptana.core.util.StringUtil;
import com.aptana.git.core.model.GitExecutable;
import com.aptana.git.ui.CloneJob;
import com.aptana.projects.ProjectsPlugin;
import com.aptana.projects.internal.wizards.Messages;
import com.aptana.projects.listeners.IStudioProjectListener;
import com.aptana.projects.templates.IDefaultProjectTemplate;
import com.aptana.ui.util.UIUtils;
import com.aptana.usage.AnalyticsEvent;
import com.aptana.usage.FeatureEvent;
import com.aptana.usage.IStudioAnalytics;
import com.aptana.usage.UsagePlugin;

/**
 * New Project Wizard base class.
 * 
 * @author Nam Le <nle@appcelerator.com>
 */
public abstract class AbstractNewProjectWizard extends BasicNewResourceWizard implements IExecutableExtension
{

	public static final String TEMPLATE_SELECTION_PAGE_NAME = "templateSelectionPage"; //$NON-NLS-1$

	// The pages in the wizard. DO NOT ACCESS VALUES FROM THEM OUTSIDE performFinish()!
	protected IWizardProjectCreationPage mainPage;
	protected ProjectTemplateSelectionPage templatesPage;
	protected WizardNewProjectReferencePage referencePage;

	protected String projectTemplateId;
	protected IProjectTemplate selectedTemplate;

	protected IConfigurationElement configElement;
	protected IProject newProject;

	private URI location; // null if defaults are used (under workspace)
	private IPath destPath; // absolute path to project we're creating.
	private IProject[] refProjects;

	// Specifies the steps to use in step indicator composite
	protected String[] stepNames;

	// Specifies abstract data that has to be passed between wizard pages for validation.
	protected Object data = new Object();

	private boolean deferProjectCreation;

	/**
	 * Constructs a new Web Project Wizard.
	 */
	public AbstractNewProjectWizard()
	{
		initDialogSettings();
	}

	/**
	 * Returns the project nature-id's.
	 * 
	 * @return The natures to be set to the project.
	 */
	protected abstract String[] getProjectNatures();

	/**
	 * Returns the project builder-id's.
	 * 
	 * @return The builders to be set to the project.
	 */
	protected abstract String[] getProjectBuilders();

	/**
	 * Returns a description string for the project creation operation.
	 * 
	 * @return a description string
	 */
	protected abstract String getProjectCreationDescription();

	/**
	 * Return an array of the template types that should be displayed for this wizard.
	 * 
	 * @return
	 */
	protected abstract TemplateType[] getProjectTemplateTypes();

	/**
	 * Initialize the wizard's dialog-settings.<br>
	 * Subclasses should override to provide specific initialization.
	 */
	protected void initDialogSettings()
	{
		IDialogSettings workbenchSettings = ProjectsPlugin.getDefault().getDialogSettings();
		IDialogSettings section = workbenchSettings.getSection("BasicNewProjectResourceWizard");//$NON-NLS-1$
		if (section == null)
		{
			section = workbenchSettings.addNewSection("BasicNewProjectResourceWizard");//$NON-NLS-1$
		}
		setDialogSettings(section);
	}

	/**
	 * Add pages to the wizard.<br>
	 * By default, we don't add the reference page to the base Web Project (subclasses may override).
	 * 
	 * @see org.eclipse.jface.wizard.Wizard#addPages()
	 */
	@Override
	public void addPages()
	{
		super.addPages();

		TemplateType[] templateTypes = getProjectTemplateTypes();
		validateProjectTemplate(templateTypes);

		LinkedHashMap<String, IStepIndicatorWizardPage> stepPages = new LinkedHashMap<String, IStepIndicatorWizardPage>();

		// Add the template selection page
		List<IProjectTemplate> templates = ProjectsPlugin.getDefault().getTemplatesManager()
				.getTemplates(templateTypes);
		if (hasNonDefaultTemplates(templates) && selectedTemplate == null)
		{
			addPage(templatesPage = new ProjectTemplateSelectionPage(TEMPLATE_SELECTION_PAGE_NAME, templates));
			stepPages.put(templatesPage.getStepName(), templatesPage);
		}

		// Add the main page where we set up the project name/location
		addPage(mainPage = createMainPage());
		if (mainPage instanceof IStepIndicatorWizardPage)
		{
			stepPages.put(((IStepIndicatorWizardPage) mainPage).getStepName(), (IStepIndicatorWizardPage) mainPage);
		}

		// Add contributed pages
		ProjectWizardContributionManager projectWizardContributionManager = ProjectsPlugin.getDefault()
				.getProjectWizardContributionManager();
		IWizardPage[] extraPages = projectWizardContributionManager.createPages(data, getProjectNatures());
		if (!ArrayUtil.isEmpty(extraPages))
		{
			for (IWizardPage page : extraPages)
			{
				addPage(page);
				if (page instanceof IStepIndicatorWizardPage)
				{
					stepPages.put(((IStepIndicatorWizardPage) page).getStepName(), (IStepIndicatorWizardPage) page);
				}
			}
		}

		// Set up the steps
		stepNames = stepPages.keySet().toArray(new String[stepPages.size()]);
		if (stepNames.length > 1)
		{
			for (IStepIndicatorWizardPage page : stepPages.values())
			{
				page.initStepIndicator(stepNames);
			}
		}

		// Finalize pages using contributors
		projectWizardContributionManager.finalizeWizardPages(getPages(), getProjectNatures());
	}

	protected boolean deferCreatingProject()
	{
		return deferProjectCreation;
	}

	protected void setDeferCreatingProject(boolean deferProjectCreation)
	{
		this.deferProjectCreation = deferProjectCreation;
	}

	protected IWizardProjectCreationPage createMainPage()
	{
		CommonWizardNewProjectCreationPage mainPage = new CommonWizardNewProjectCreationPage(
				"basicNewProjectPage", selectedTemplate); //$NON-NLS-1$
		mainPage.setTitle(Messages.NewProjectWizard_ProjectPage_Title);
		mainPage.setDescription(Messages.NewProjectWizard_ProjectPage_Description);
		return mainPage;
	}

	protected void doCreateProject(IProgressMonitor monitor) throws InvocationTargetException
	{
		IWorkspaceRunnable runnable = new IWorkspaceRunnable()
		{
			public void run(IProgressMonitor monitor) throws CoreException
			{
				SubMonitor subMonitor = SubMonitor.convert(monitor, 6);
				try
				{
					createNewProject(subMonitor.newChild(4));
				}
				catch (InvocationTargetException e)
				{
					throw new CoreException(new Status(IStatus.ERROR, ProjectsPlugin.PLUGIN_ID, 0, e.getMessage(),
							e.getTargetException()));
				}

				// Allow the project contributors to do work
				ProjectWizardContributionManager projectWizardContributionManager = ProjectsPlugin.getDefault()
						.getProjectWizardContributionManager();
				final IStatus contributorStatus = projectWizardContributionManager.performProjectFinish(newProject,
						subMonitor.newChild(1));
				if (contributorStatus != null && !contributorStatus.isOK())
				{
					// FIXME This UI code shouldn't be here, throw an exception up and handle it!
					// Show the error. Should we cancel project creation?
					UIUtils.getDisplay().syncExec(new Runnable()
					{
						public void run()
						{
							MessageDialog.openError(UIUtils.getActiveWorkbenchWindow().getShell(),
									Messages.AbstractNewProjectWizard_ProjectListenerErrorTitle,
									contributorStatus.getMessage());
						}
					});
				}

				// Perform post project hooks

				IStudioProjectListener[] projectListeners = new IStudioProjectListener[0];
				IProjectDescription description = newProject.getDescription();
				if (description != null)
				{
					projectListeners = ProjectsPlugin.getDefault().getProjectListenersManager()
							.getProjectListeners(description.getNatureIds());
				}

				int listenerSize = projectListeners.length;
				SubMonitor hookMonitor = SubMonitor.convert(subMonitor.newChild(1),
						Messages.AbstractNewProjectWizard_ProjectListener_TaskName, Math.max(1, listenerSize));

				for (IStudioProjectListener projectListener : projectListeners)
				{
					if (projectListener != null)
					{
						final IStatus status = projectListener.projectCreated(newProject, hookMonitor.newChild(1));

						// Show a dialog if there are failures
						if (status != null && status.matches(IStatus.ERROR))
						{
							// FIXME This UI code shouldn't be here, throw an exception up and handle it!
							UIUtils.getDisplay().syncExec(new Runnable()
							{
								public void run()
								{
									String message = status.getMessage();
									if (status instanceof ProcessStatus)
									{
										message = ((ProcessStatus) status).getStdErr();
									}
									MessageDialog.openError(UIUtils.getActiveWorkbenchWindow().getShell(),
											Messages.AbstractNewProjectWizard_ProjectListenerErrorTitle, message);
								}
							});
						}
					}
				}
			}
		};
		try
		{
			ResourcesPlugin.getWorkspace().run(runnable, monitor);
		}
		catch (CoreException e)
		{
			throw new InvocationTargetException(e, Messages.AbstractNewProjectWizard_ProjectListener_NoDescriptor_Error);
		}
	}

	@Override
	public boolean performFinish()
	{
		newProject = mainPage.getProjectHandle();
		destPath = mainPage.getLocationPath();
		location = null;
		if (!mainPage.useDefaults())
		{
			location = mainPage.getLocationURI();
		}
		else
		{
			destPath = destPath.append(newProject.getName());
		}

		if (templatesPage != null)
		{
			selectedTemplate = templatesPage.getSelectedTemplate();
		}

		if (referencePage != null)
		{
			refProjects = referencePage.getReferencedProjects();
		}

		if (!deferCreatingProject())
		{
			IStatus projectStatus = createAndRefreshProject(true, true, new NullProgressMonitor());
			return projectStatus.isOK();
		}
		return true;
	}

	protected IStatus createAndRefreshProject(boolean isBlocking, boolean revealProject, IProgressMonitor monitor)
	{
		IStatus resultStatus = Status.CANCEL_STATUS;
		try
		{
			if (isBlocking)
			{
				getContainer().run(true, true, new IRunnableWithProgress()
				{
					public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException
					{
						doCreateProject(monitor);
					}
				});
			}
			else
			{
				doCreateProject(monitor);
			}
			resultStatus = Status.OK_STATUS;
		}
		catch (InterruptedException e)
		{
			// StatusManager.getManager().handle(new Status(IStatus.ERROR, ProjectsPlugin.PLUGIN_ID, e.getMessage(), e),
			// StatusManager.BLOCK);
		}
		catch (InvocationTargetException e)
		{
			Throwable t = e.getTargetException();
			if (t instanceof ExecutionException && t.getCause() instanceof CoreException)
			{
				CoreException cause = (CoreException) t.getCause();
				resultStatus = cause.getStatus();
				StatusAdapter status;
				if (cause.getStatus().getCode() == IResourceStatus.CASE_VARIANT_EXISTS)
				{
					status = new StatusAdapter(new Status(IStatus.WARNING, ProjectsPlugin.PLUGIN_ID, NLS.bind(
							Messages.NewProjectWizard_Warning_DirectoryExists, mainPage.getProjectHandle().getName()),
							cause));
				}
				else
				{
					status = new StatusAdapter(new Status(cause.getStatus().getSeverity(), ProjectsPlugin.PLUGIN_ID,
							Messages.NewProjectWizard_CreationProblem, cause));
				}
				status.setProperty(IStatusAdapterConstants.TITLE_PROPERTY, Messages.NewProjectWizard_CreationProblem);
				StatusManager.getManager().handle(status, StatusManager.BLOCK);
			}
			else
			{
				resultStatus = new Status(IStatus.WARNING, ProjectsPlugin.PLUGIN_ID, 0, NLS.bind(
						Messages.NewProjectWizard_InternalError, t.getMessage()), t);
				StatusAdapter status = new StatusAdapter(resultStatus);
				status.setProperty(IStatusAdapterConstants.TITLE_PROPERTY, Messages.NewProjectWizard_CreationProblem);
				StatusManager.getManager().handle(status, StatusManager.LOG | StatusManager.BLOCK);
			}
		}

		if (!resultStatus.isOK())
		{
			return resultStatus;
		}

		if (revealProject)
		{
			// TODO Run all of this in a job?
			updatePerspective();
			selectAndReveal(newProject);
		}
		openIndexFile(revealProject);
		sendProjectCreateEvent();

		return resultStatus;
	}

	/**
	 * Returns the {@link IProject} reference that was created by this wizard. Note that the result may be
	 * <code>null</code> if called before the project was created.
	 * 
	 * @return An {@link IProject} (can be <code>null</code>).
	 */
	public IProject getCreatedProject()
	{
		return newProject;
	}

	protected abstract String getProjectCreateEventName();

	protected void sendProjectCreateEvent()
	{
		Map<String, String> payload = generatePayload();
		sendEvent(new FeatureEvent(getProjectCreateEventName(), payload));
	}

	private void sendEvent(AnalyticsEvent featureEvent)
	{
		UsagePlugin plugin = UsagePlugin.getDefault();
		if (plugin == null)
		{
			return;
		}
		IStudioAnalytics analytics = plugin.getStudioAnalytics();
		if (analytics == null)
		{
			return;
		}
		analytics.sendEvent(featureEvent);
	}

	/**
	 * Generates the payload used for analytics on project creation events.
	 * 
	 * @return
	 */
	protected Map<String, String> generatePayload()
	{
		Map<String, String> payload = new HashMap<String, String>();
		payload.put("name", newProject.getName()); //$NON-NLS-1$
		if (selectedTemplate != null)
		{
			String id = selectedTemplate.getId();
			if (StringUtil.isEmpty(id))
			{
				payload.put("template", "custom.template-" + selectedTemplate.getDisplayName()); //$NON-NLS-1$ //$NON-NLS-2$
			}
			else
			{
				payload.put("template", id); //$NON-NLS-1$
			}
		}
		return payload;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * org.eclipse.core.runtime.IExecutableExtension#setInitializationData(org.eclipse.core.runtime.IConfigurationElement
	 * , java.lang.String, java.lang.Object)
	 */
	public void setInitializationData(IConfigurationElement config, String propertyName, Object data)
			throws CoreException
	{
		configElement = config;

		if (ProjectTemplateSelectionPage.COMMAND_PROJECT_FROM_TEMPLATE_PROJECT_TEMPLATE_NAME.equals(propertyName))
		{
			if (data instanceof String)
			{
				projectTemplateId = (String) data;
			}
		}
	}

	protected void validateProjectTemplate(TemplateType[] templateType)
	{
		selectedTemplate = null;

		if (projectTemplateId != null)
		{
			List<IProjectTemplate> templates = ProjectsPlugin.getDefault().getTemplatesManager()
					.getTemplates(templateType);

			for (IProjectTemplate template : templates)
			{
				if (template.getId() != null && template.getId().equals(projectTemplateId))
				{
					selectedTemplate = template;
					return;
				}
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.wizards.newresource.BasicNewResourceWizard#init(org.eclipse.ui.IWorkbench,
	 * org.eclipse.jface.viewers.IStructuredSelection)
	 */
	@Override
	public void init(IWorkbench workbench, IStructuredSelection currentSelection)
	{
		super.init(workbench, currentSelection);
		setNeedsProgressMonitor(true);
	}

	private void updatePerspective()
	{
		BasicNewProjectResourceWizard.updatePerspective(configElement);
	}

	/**
	 * Creates a new project resource with the selected name.
	 * <p>
	 * In normal usage, this method is invoked after the user has pressed Finish on the wizard; the enablement of the
	 * Finish button implies that all controls on the pages currently contain valid values.
	 * </p>
	 * <p>
	 * Note that this wizard caches the new project once it has been successfully created; subsequent invocations of
	 * this method will answer the same project resource without attempting to create it again.
	 * </p>
	 * 
	 * @param monitor
	 *            TODO
	 * @return the created project resource, or <code>null</code> if the project was not created
	 */
	protected IProject createNewProject(IProgressMonitor monitor) throws InvocationTargetException
	{
		SubMonitor sub = SubMonitor.convert(monitor, 100);
		// Project description creation
		IProjectDescription description = ResourceUtil.getProjectDescription(destPath, getProjectNatures(),
				getProjectBuilders());
		description.setName(newProject.getName());
		description.setLocationURI(location);
		// Update the referenced project in case it was initialized.
		if (refProjects != null && refProjects.length > 0)
		{
			description.setReferencedProjects(refProjects);
		}
		sub.worked(10);

		if (!applySourcedProjectFilesAfterProjectCreated() && isCloneFromGit())
		{
			cloneFromGit(newProject, description, sub.newChild(90));
		}
		else
		{
			doBasicCreateProject(newProject, description, sub.newChild(75));
			if (!applySourcedProjectFilesAfterProjectCreated() && selectedTemplate != null && !isCloneFromGit())
			{
				selectedTemplate.apply(newProject, true);
			}
		}

		return newProject;
	}

	/**
	 * If a wizard needs to apply project files from an additional source (git clone or template) after a project has
	 * been generated (say by a script/process), this should return true.
	 * 
	 * @return
	 */
	protected boolean applySourcedProjectFilesAfterProjectCreated()
	{
		return false;
	}

	protected boolean isCloneFromGit()
	{
		// FIXME Move this logic into the IProjectTemplate#apply method?
		return selectedTemplate != null && !selectedTemplate.getLocation().endsWith(".zip"); //$NON-NLS-1$
	}

	/**
	 * Clone a project from a GIT repository.
	 * 
	 * @param newProjectHandle
	 * @param description
	 * @param monitor
	 * @throws InvocationTargetException
	 */
	protected void cloneFromGit(IProject newProjectHandle, IProjectDescription description, IProgressMonitor monitor)
			throws InvocationTargetException
	{
		doCloneFromGit(selectedTemplate.getLocation(), newProjectHandle, description, monitor);
	}

	/**
	 * Performs a git clone to a temporary location and then copies the files over top the already generated project.
	 * This is because git cannot clone into an existing directory.
	 * 
	 * @param monitor
	 * @throws Exception
	 */
	protected void cloneAfter(IProgressMonitor monitor) throws Exception
	{
		SubMonitor sub = SubMonitor.convert(monitor, Messages.AbstractNewProjectWizard_CloningFromGitMsg, 100);
		// clone to tmp dir then copy files over top the project!
		File tmpFile = File.createTempFile("delete_me", "tmp"); //$NON-NLS-1$ //$NON-NLS-2$
		File dest = new File(tmpFile.getParent(), "git_clone_tmp"); //$NON-NLS-1$
		GitExecutable.instance().clone(selectedTemplate.getLocation(), Path.fromOSString(dest.getAbsolutePath()), true,
				sub.newChild(85));

		IFileStore tmpClone = EFS.getStore(dest.toURI());
		// Wipe the .git folder before copying? Wipe the .project file before copying?
		IFileStore dotGit = tmpClone.getChild(".git"); //$NON-NLS-1$
		dotGit.delete(EFS.NONE, sub.newChild(2));

		IFileStore dotProject = tmpClone.getChild(IProjectDescription.DESCRIPTION_FILE_NAME);
		if (dotProject.fetchInfo().exists())
		{
			dotProject.delete(EFS.NONE, sub.newChild(1));
		}
		// OK, copy the cloned template's contents over
		IFileStore projectStore = EFS.getStore(newProject.getLocationURI());
		tmpClone.copy(projectStore, EFS.OVERWRITE, sub.newChild(9));
		// Now get rid of the temp clone!
		tmpClone.delete(EFS.NONE, sub.newChild(3));
		sub.done();
	}

	/**
	 * Perform a basic project creation.
	 * 
	 * @param project
	 * @param description
	 * @param subMonitor
	 * @throws CoreException
	 */
	private void doBasicCreateProject(IProject project, final IProjectDescription description, IProgressMonitor monitor)
			throws InvocationTargetException
	{
		CreateProjectOperation op = new CreateProjectOperation(description, getProjectCreationDescription());
		try
		{
			// see bug https://bugs.eclipse.org/bugs/show_bug.cgi?id=219901
			// directly execute the operation so that the undo state is
			// not preserved. Making this undoable resulted in too many
			// accidental file deletions.
			op.execute(monitor, WorkspaceUndoUtil.getUIInfoAdapter(getShell()));
		}
		catch (ExecutionException e)
		{
			throw new InvocationTargetException(e);
		}
	}

	/**
	 * Do a GIT clone from a given source URI
	 * 
	 * @param sourceURI
	 * @param projectHandle
	 * @param projectDescription
	 * @param monitor
	 * @throws InvocationTargetException
	 */
	protected void doCloneFromGit(String sourceURI, final IProject projectHandle,
			final IProjectDescription projectDescription, IProgressMonitor monitor) throws InvocationTargetException
	{
		SubMonitor sub = SubMonitor.convert(monitor, 100);
		CloneJob job = new CloneJob(sourceURI, destPath.toOSString(), true, true);
		// We're executing inside the wizard's container already, run sync.
		IStatus status = job.run(sub.newChild(100));
		if (!status.isOK())
		{
			if (status instanceof ProcessStatus)
			{
				ProcessStatus ps = (ProcessStatus) status;
				String stderr = ps.getStdErr();
				throw new InvocationTargetException(new CoreException(new Status(status.getSeverity(),
						status.getPlugin(), stderr)));
			}
			throw new InvocationTargetException(new CoreException(status));
		}
		sub.done();
	}

	protected void openIndexFile(boolean activatateEditor)
	{
		IFile indexFile = newProject.getFile("index.html"); //$NON-NLS-1$
		if (indexFile.exists())
		{
			IWorkbenchPage page = UIUtils.getActivePage();
			if (page != null)
			{
				try
				{
					IDE.openEditor(page, indexFile, activatateEditor);
				}
				catch (PartInitException e)
				{
					IdeLog.logError(ProjectsPlugin.getDefault(), Messages.NewProjectWizard_ERR_OpeningIndex, e);
				}
			}
		}
	}

	protected IPath getDestinationPath()
	{
		return destPath;
	}

	/**
	 * @deprecated Please use {@link IProjectTemplate#apply(IProject, boolean)}
	 * @param template
	 * @param project
	 * @param preExistingResources
	 *            A possible conflicting list of resources that the extraction should notify about to the user.
	 */
	public static void extractZip(IProjectTemplate template, IProject project, Set<IPath> preExistingResources)
	{
		template.apply(project, !preExistingResources.isEmpty());
	}

	/**
	 * @deprecated Please use {@link IProjectTemplate#apply(IProject, boolean)}
	 * @param template
	 * @param project
	 * @param promptForOverwrite
	 */
	public static void extractZip(IProjectTemplate template, IProject project, boolean promptForOverwrite)
	{
		template.apply(project, promptForOverwrite);
	}

	/**
	 * Extracts a zip into a given project.
	 * 
	 * @deprecated Please use {@link IProjectTemplate#apply(IProject, boolean)}
	 * @param zipPath
	 * @param project
	 * @param promptForOverwrite
	 *            Indicate that we should display a prompt in case the zip overwrites some of the existing project
	 *            files.
	 * @param preExistingResources
	 *            A defined list of resources that will be used when prompting for overwrite conflicts. In case of an
	 *            empty list, the function will prompt on any overwritten file.
	 * @param isReplacingParameters
	 */
	public static void extractZip(final File zipPath, final IProject project, boolean promptForOverwrite,
			Set<IPath> preExistingResources, final boolean isReplacingParameters)
	{
		new ProjectTemplate(zipPath.getAbsolutePath(), null, "", isReplacingParameters, "", null, "").apply(project, //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				promptForOverwrite);
	}

	private static boolean hasNonDefaultTemplates(List<IProjectTemplate> templates)
	{
		for (IProjectTemplate template : templates)
		{
			if (!(template instanceof IDefaultProjectTemplate))
			{
				return true;
			}
		}
		return false;
	}
}
