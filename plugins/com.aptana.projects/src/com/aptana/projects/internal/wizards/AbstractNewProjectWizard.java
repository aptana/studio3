/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.projects.internal.wizards;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResourceStatus;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExecutableExtension;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.osgi.util.NLS;
import org.eclipse.team.core.RepositoryProvider;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.ContainerGenerator;
import org.eclipse.ui.dialogs.WizardNewProjectReferencePage;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.ide.undo.CreateProjectOperation;
import org.eclipse.ui.ide.undo.WorkspaceUndoUtil;
import org.eclipse.ui.progress.UIJob;
import org.eclipse.ui.statushandlers.IStatusAdapterConstants;
import org.eclipse.ui.statushandlers.StatusAdapter;
import org.eclipse.ui.statushandlers.StatusManager;
import org.eclipse.ui.wizards.newresource.BasicNewProjectResourceWizard;
import org.eclipse.ui.wizards.newresource.BasicNewResourceWizard;

import com.aptana.core.logging.IdeLog;
import com.aptana.core.projects.templates.IProjectTemplate;
import com.aptana.core.projects.templates.TemplateType;
import com.aptana.core.util.IOUtil;
import com.aptana.core.util.ProcessStatus;
import com.aptana.core.util.ResourceUtil;
import com.aptana.git.core.GitPlugin;
import com.aptana.git.core.model.GitExecutable;
import com.aptana.git.ui.CloneJob;
import com.aptana.projects.ProjectsPlugin;
import com.aptana.projects.templates.ProjectTemplatesManager;
import com.aptana.scripting.model.AbstractElement;
import com.aptana.scripting.model.BundleManager;
import com.aptana.scripting.model.ProjectTemplateElement;
import com.aptana.scripting.model.filters.IModelFilter;
import com.aptana.ui.util.UIUtils;
import com.aptana.usage.FeatureEvent;
import com.aptana.usage.StudioAnalytics;

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

		validateProjectTemplate(getProjectTemplateTypes());

		addPage(mainPage = createMainPage());

		List<String> steps = new ArrayList<String>();
		List<IStepIndicatorWizardPage> stepPages = new ArrayList<IStepIndicatorWizardPage>();

		if (mainPage instanceof IStepIndicatorWizardPage)
		{
			stepPages.add((IStepIndicatorWizardPage) mainPage);
			steps.add(((IStepIndicatorWizardPage) mainPage).getStepName());
		}

		List<IProjectTemplate> templates = getProjectTemplates(getProjectTemplateTypes());
		if (templates.size() > 0 && selectedTemplate == null)
		{
			addPage(templatesPage = new ProjectTemplateSelectionPage(TEMPLATE_SELECTION_PAGE_NAME, templates));
			if (templatesPage instanceof IStepIndicatorWizardPage)
			{
				stepPages.add((IStepIndicatorWizardPage) templatesPage);
				steps.add(((IStepIndicatorWizardPage) templatesPage).getStepName());
			}
		}

		stepNames = steps.toArray(new String[steps.size()]);
		for (IStepIndicatorWizardPage page : stepPages)
		{
			page.initStepIndicator(stepNames);
		}
	}

	protected IWizardProjectCreationPage createMainPage()
	{
		CommonWizardNewProjectCreationPage mainPage = new CommonWizardNewProjectCreationPage(
				"basicNewProjectPage", selectedTemplate); //$NON-NLS-1$
		mainPage.setTitle(Messages.NewProjectWizard_ProjectPage_Title);
		mainPage.setDescription(Messages.NewProjectWizard_ProjectPage_Description);
		return mainPage;
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

		boolean success = false;
		try
		{
			getContainer().run(true, true, new IRunnableWithProgress()
			{

				public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException
				{
					createNewProject(monitor);
				}
			});
			success = true;
		}
		catch (InterruptedException e)
		{
			StatusManager.getManager().handle(new Status(IStatus.ERROR, ProjectsPlugin.PLUGIN_ID, e.getMessage(), e),
					StatusManager.BLOCK);
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
				StatusAdapter status = new StatusAdapter(new Status(IStatus.WARNING, ProjectsPlugin.PLUGIN_ID, 0,
						NLS.bind(Messages.NewProjectWizard_InternalError, t.getMessage()), t));
				status.setProperty(IStatusAdapterConstants.TITLE_PROPERTY, Messages.NewProjectWizard_CreationProblem);
				StatusManager.getManager().handle(status, StatusManager.LOG | StatusManager.BLOCK);
			}
		}

		if (!success)
		{
			return false;
		}

		// TODO Run all of this in a job?
		updatePerspective();
		selectAndReveal(newProject);
		openIndexFile();
		sendProjectCreateEvent();

		return true;
	}

	protected abstract String getProjectCreateEventName();

	protected void sendProjectCreateEvent()
	{
		Map<String, String> payload = generatePayload();
		StudioAnalytics.getInstance().sendEvent(new FeatureEvent(getProjectCreateEventName(), payload));
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
			List<IProjectTemplate> templates = getProjectTemplates(templateType);

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
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IProjectDescription description = workspace.newProjectDescription(newProject.getName());
		description.setLocationURI(location);
		// Set the natures
		description.setNatureIds(getProjectNatures());
		// Set the builders
		for (String builder : getProjectBuilders())
		{
			ResourceUtil.addBuilder(description, builder);
		}
		// Update the referenced project in case it was initialized.
		if (refProjects != null && refProjects.length > 0)
		{
			description.setReferencedProjects(refProjects);
		}
		sub.worked(10);

		if (!cloneAfterProjectCreated() && isCloneFromGit())
		{
			cloneFromGit(newProject, description, sub.newChild(90));
		}
		else
		{
			doBasicCreateProject(newProject, description, sub.newChild(75));
			if (selectedTemplate != null && !isCloneFromGit())
			{
				extractZip(selectedTemplate, newProject, true);
			}
		}

		return newProject;
	}

	/**
	 * If a wizard needs to run git clone after a project has been generated (say by a script/process), this should
	 * return true.
	 * 
	 * @return
	 */
	protected boolean cloneAfterProjectCreated()
	{
		return false;
	}

	protected boolean isCloneFromGit()
	{
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
	 * Returns a list of {@link IProjectTemplate} that match the any of the given types.<br>
	 * Templates are loaded from the Rubles and from the "projectTemplates" extension point.
	 * 
	 * @param templateTypes
	 *            The Types to match to.
	 * @return A list of ProjectTemplateElement
	 */
	public static List<IProjectTemplate> getProjectTemplates(final TemplateType[] templateTypes)
	{
		if (templateTypes == null || templateTypes.length == 0)
		{
			return Collections.emptyList();
		}

		List<IProjectTemplate> templates = BundleManager.getInstance().getProjectTemplates(new IModelFilter()
		{
			public boolean include(AbstractElement element)
			{
				boolean result = false;

				if (element instanceof ProjectTemplateElement)
				{
					ProjectTemplateElement template = (ProjectTemplateElement) element;
					TemplateType type = template.getType();
					for (TemplateType t : templateTypes)
					{
						if (type == t)
						{
							result = true;
							break;
						}
					}
				}
				return result;
			}
		});
		ProjectTemplatesManager manager = new ProjectTemplatesManager();
		for (TemplateType t : templateTypes)
		{
			templates.addAll(manager.getTemplatesForType(t));
		}
		return templates;
	}

	/**
	 * @param template
	 * @param project
	 * @param preExistingResources
	 *            A possible conflicting list of resources that the extraction should notify about to the user.
	 */
	public static void extractZip(IProjectTemplate template, IProject project, Set<IPath> preExistingResources)
	{
		extractZip(new File(template.getDirectory(), template.getLocation()), project, !preExistingResources.isEmpty(),
				preExistingResources, template.isReplacingParameters());
	}

	public static void extractZip(IProjectTemplate template, IProject project, boolean promptForOverwrite)
	{
		Set<IPath> emptySet = Collections.emptySet();
		extractZip(new File(template.getDirectory(), template.getLocation()), project, promptForOverwrite, emptySet,
				template.isReplacingParameters());
	}

	/**
	 * Extracts a zip into a given project.
	 * 
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
		if (!zipPath.exists())
		{
			return;
		}

		ZipFile zipFile = null;
		try
		{
			final Map<IFile, ZipEntry> conflicts = new HashMap<IFile, ZipEntry>();
			zipFile = new ZipFile(zipPath, ZipFile.OPEN_READ);
			Enumeration<? extends ZipEntry> entries = zipFile.entries();
			ZipEntry entry;
			while (entries.hasMoreElements())
			{
				entry = entries.nextElement();

				if (entry.isDirectory())
				{
					IFolder newFolder = project.getFolder(Path.fromOSString(entry.getName()));
					if (!newFolder.exists())
					{
						newFolder.create(true, true, null);
					}
				}
				else
				{
					IFile newFile = project.getFile(Path.fromOSString(entry.getName()));
					if (newFile.exists())
					{
						if (promptForOverwrite)
						{
							// Add to the list of conflicts only when we didn't get any pre-existing list of
							// possible conflicting files, or when the pre-existing list of paths contains the
							// current file path.
							if (preExistingResources == null || preExistingResources.isEmpty()
									|| preExistingResources.contains(newFile.getLocation()))
							{
								conflicts.put(newFile, entry);
								// Remove the file for now. We will add it again if the user agrees the
								// overwrite it.
							}
							else
							{
								// The file exists right now, but was not in the pre-existing resources we check
								// against, so we just need to set it with the new content.
								newFile.setContents(
										getInputStream(zipFile, entry, newFile, project, isReplacingParameters), true,
										true, null);
							}
						}
						else
						{
							newFile.setContents(
									getInputStream(zipFile, entry, newFile, project, isReplacingParameters), true,
									true, null);
						}
					}
					else
					{
						try
						{
							// makes sure the parent path is created
							(new ContainerGenerator(newFile.getParent().getFullPath())).generateContainer(null);
							newFile.create(getInputStream(zipFile, entry, newFile, project, isReplacingParameters),
									true, null);
						}
						catch (CoreException re)
						{
							if (re.getStatus().getCode() == IResourceStatus.CASE_VARIANT_EXISTS
									&& re.getStatus() instanceof IResourceStatus)
							{
								IResourceStatus rs = (IResourceStatus) re.getStatus();
								IFile newVariantFile = project.getParent().getFile(rs.getPath());
								newVariantFile.setContents(
										getInputStream(zipFile, entry, newVariantFile, project, isReplacingParameters),
										true, true, null);
							}
							else
							{
								IdeLog.logError(ProjectsPlugin.getDefault(), Messages.NewProjectWizard_ZipFailure, re);
							}
						}
					}
				}
			}
			// Check if we had any conflicts. If so, display a dialog to let the user mark which
			// files he/she wishes to keep, and which would be overwritten by the Zip's content.
			if (!conflicts.isEmpty())
			{
				final ZipFile finalZipFile = zipFile;
				UIJob openDialogJob = new UIJob(Messages.OverwriteFilesSelectionDialog_overwriteFilesTitle)
				{
					public IStatus runInUIThread(IProgressMonitor monitor)
					{
						OverwriteFilesSelectionDialog overwriteFilesSelectionDialog = new OverwriteFilesSelectionDialog(
								conflicts.keySet(), Messages.NewProjectWizard_filesOverwriteMessage);
						if (overwriteFilesSelectionDialog.open() == Window.OK)
						{
							try
							{
								Object[] overwrittenFiles = overwriteFilesSelectionDialog.getResult();
								// Overwrite the selected files only.
								for (Object file : overwrittenFiles)
								{
									IFile iFile = (IFile) file;
									iFile.setContents(
											getInputStream(finalZipFile, conflicts.get(file), iFile, project,
													isReplacingParameters), true, true, null);
								}
							}
							catch (Exception e)
							{
								IdeLog.logError(ProjectsPlugin.getDefault(),
										MessageFormat.format(Messages.NewProjectWizard_ERR_UnzipFile, zipPath), e);
							}
						}
						return Status.OK_STATUS;
					}
				};
				openDialogJob.setSystem(true);
				openDialogJob.schedule();
				openDialogJob.join();
			}
		}
		catch (CoreException e)
		{
			IdeLog.logError(ProjectsPlugin.getDefault(), e);
		}
		catch (Exception e)
		{
			IdeLog.logError(ProjectsPlugin.getDefault(),
					MessageFormat.format(Messages.NewProjectWizard_ERR_UnzipFile, zipPath), e);
		}
		finally
		{
			if (zipFile != null)
			{
				try
				{
					zipFile.close();
				}
				catch (IOException e)
				{
					// ignores
				}
			}
		}
	}

	/**
	 * Returns an input stream for a zip entry. The returned input stream may be a stream that was generated after
	 * processing the file for template-variables.
	 * 
	 * @param zipFile
	 * @param entry
	 *            A zip entry
	 * @param file
	 *            An {@link IFile} reference.
	 * @param project
	 *            An {@link IProject} reference.
	 * @param isReplacingParameters
	 * @return An input stream for the content.
	 * @throws IOException
	 * @throws CoreException
	 */
	private static InputStream getInputStream(ZipFile zipFile, ZipEntry entry, IFile file, IProject project,
			boolean isReplacingParameters) throws IOException, CoreException
	{
		if (!isSupportedFile(file))
		{
			return zipFile.getInputStream(entry);
		}
		String content = null;
		if (isReplacingParameters)
		{
			try
			{
				// read and do template substitution
				content = applyTemplateVariables(new InputStreamReader(zipFile.getInputStream(entry)), file, project);
			}
			catch (Exception e)
			{
				IdeLog.logError(ProjectsPlugin.getDefault(),
						"Error applying a template. Trying to write the file as is, without template evaluation.", e); //$NON-NLS-1$
			}
		}
		if (content == null)
		{
			// In case we should not evaluate template tags, or had a previous error, read without template
			// substitution.
			content = IOUtil.read(zipFile.getInputStream(entry));
		}
		return new ByteArrayInputStream(content.getBytes());
	}

	/**
	 * Returns true if the given file can be evaluated for template-variables.<br>
	 * There is no good way of detecting what is binary and what is not, so we decide what is supported according to the
	 * existing editor's supported content types (file extensions).
	 * 
	 * @param file
	 * @return True if the file can be processed; False, otherwise.
	 */
	private static boolean isSupportedFile(IFile file)
	{
		IContentType contentType = Platform.getContentTypeManager().findContentTypeFor(file.getName());
		return PlatformUI.getWorkbench().getEditorRegistry().getDefaultEditor(file.getName(), contentType) != null;
	}

	/**
	 * Apply the project-template variables on the files that were extracted as the project contents.
	 * 
	 * @param reader
	 * @param file
	 * @param project
	 * @return A string content of the {@link InputStream}, <b>after</b> the variables substitution.
	 * @throws CoreException
	 */
	private static String applyTemplateVariables(Reader reader, IFile file, IProject project) throws CoreException
	{
		try
		{
			// Initialize the singleton Velocity
			Velocity.init();
		}
		catch (Exception e)
		{
			throw new CoreException(
					new Status(IStatus.ERROR, ProjectsPlugin.PLUGIN_ID, "Failed initialize Velocity", e)); //$NON-NLS-1$
		}
		try
		{
			IPath absoluteFilePath = file.getLocation();
			String filePathString = absoluteFilePath.toOSString();

			VelocityContext context = new VelocityContext();
			context.put("TM_NEW_FILE_BASENAME", absoluteFilePath.removeFileExtension().lastSegment()); //$NON-NLS-1$
			context.put("TM_NEW_FILE", filePathString); //$NON-NLS-1$
			context.put("TM_NEW_FILE_DIRECTORY", absoluteFilePath.removeLastSegments(1).toOSString()); //$NON-NLS-1$
			context.put("TM_PROJECTNAME", project.getName()); //$NON-NLS-1$
			Calendar calendar = Calendar.getInstance();
			context.put("TIME", calendar.getTime()); //$NON-NLS-1$
			context.put("YEAR", calendar.get(Calendar.YEAR)); //$NON-NLS-1$

			StringWriter writer = new StringWriter();
			Velocity.evaluate(context, writer, filePathString, reader);
			return writer.getBuffer().toString();
		}
		catch (Exception e)
		{
			throw new CoreException(new Status(IStatus.ERROR, ProjectsPlugin.PLUGIN_ID,
					Messages.NewProjectWizard_templateVariableApplyError));
		}
		finally
		{
			if (reader != null)
			{
				try
				{
					reader.close();
				}
				catch (IOException e)
				{
				}
			}
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
		IStatus status = job.run(sub.newChild(95));
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
		else
		{
			try
			{
				projectHandle.setDescription(projectDescription, sub.newChild(2));
				// Ensure that we disconnect our git support in case it auto-attached
				RepositoryProvider.unmap(projectHandle);
				GitPlugin.getDefault().getGitRepositoryManager().removeRepository(projectHandle);
				IFolder gitFolder = projectHandle.getFolder(".git"); //$NON-NLS-1$
				if (gitFolder.exists())
				{
					gitFolder.delete(true, sub.newChild(3));
				}
			}
			catch (CoreException e)
			{
				throw new InvocationTargetException(e);
			}
		}
		sub.done();
	}

	protected void openIndexFile()
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
					IdeLog.logError(ProjectsPlugin.getDefault(), Messages.NewProjectWizard_ERR_OpeningIndex, e);
				}
			}
		}
	}
}
