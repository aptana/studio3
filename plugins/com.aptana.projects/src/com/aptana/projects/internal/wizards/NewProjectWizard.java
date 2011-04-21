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
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.ICommand;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceStatus;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExecutableExtension;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.WizardNewProjectCreationPage;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.ide.undo.CreateProjectOperation;
import org.eclipse.ui.ide.undo.WorkspaceUndoUtil;
import org.eclipse.ui.progress.UIJob;
import org.eclipse.ui.statushandlers.IStatusAdapterConstants;
import org.eclipse.ui.statushandlers.StatusAdapter;
import org.eclipse.ui.statushandlers.StatusManager;
import org.eclipse.ui.wizards.newresource.BasicNewProjectResourceWizard;
import org.eclipse.ui.wizards.newresource.BasicNewResourceWizard;

import com.aptana.core.build.UnifiedBuilder;
import com.aptana.core.projects.templates.IProjectTemplate;
import com.aptana.core.projects.templates.TemplateType;
import com.aptana.git.ui.CloneJob;
import com.aptana.git.ui.internal.actions.DisconnectHandler;
import com.aptana.projects.ProjectsPlugin;
import com.aptana.projects.WebProjectNature;
import com.aptana.projects.templates.ProjectTemplatesManager;
import com.aptana.scripting.model.AbstractElement;
import com.aptana.scripting.model.BundleManager;
import com.aptana.scripting.model.ProjectTemplateElement;
import com.aptana.scripting.model.filters.IModelFilter;

public class NewProjectWizard extends BasicNewResourceWizard implements IExecutableExtension
{

	/**
	 * The wizard ID
	 */
	public static final String ID = "com.aptana.ui.wizards.NewWebProject"; //$NON-NLS-1$

	private static final String IMAGE = "icons/web_project_wiz.png"; //$NON-NLS-1$

	private WizardNewProjectCreationPage mainPage;
	private ProjectTemplateSelectionPage templatesPage;
	private IProject newProject;
	private IConfigurationElement configElement;

	public NewProjectWizard()
	{
		IDialogSettings workbenchSettings = ProjectsPlugin.getDefault().getDialogSettings();
		IDialogSettings section = workbenchSettings.getSection("BasicNewProjectResourceWizard");//$NON-NLS-1$
		if (section == null)
		{
			section = workbenchSettings.addNewSection("BasicNewProjectResourceWizard");//$NON-NLS-1$
		}
		setDialogSettings(section);
	}

	@Override
	public void addPages()
	{
		super.addPages();

		mainPage = new WizardNewProjectCreationPage("basicNewProjectPage"); //$NON-NLS-1$
		mainPage.setTitle(Messages.NewProjectWizard_ProjectPage_Title);
		mainPage.setDescription(Messages.NewProjectWizard_ProjectPage_Description);
		addPage(mainPage);

		List<IProjectTemplate> templates = getProjectTemplates(new TemplateType[] { TemplateType.WEB, TemplateType.ALL });
		if (templates.size() > 0)
		{
			addPage(templatesPage = new ProjectTemplateSelectionPage("templateSelectionPage", templates)); //$NON-NLS-1$
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

	@Override
	public boolean performFinish()
	{
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
		setWindowTitle(Messages.NewProjectWizard_Title);
	}

	@Override
	protected void initializeDefaultPageImageDescriptor()
	{
		setDefaultPageImageDescriptor(ProjectsPlugin.getImageDescriptor(IMAGE));
	}

	protected void updatePerspective()
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
	 * @return the created project resource, or <code>null</code> if the project was not created
	 */
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

		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		final IProjectDescription description = workspace.newProjectDescription(newProjectHandle.getName());
		description.setLocationURI(location);
		description.setNatureIds(new String[] { WebProjectNature.ID });
		// Add Unified Builder
		ICommand command = description.newCommand();
		command.setBuilderName(UnifiedBuilder.ID);
		description.setBuildSpec(new ICommand[] { command });

		boolean fromGit = false;
		if (templatesPage != null)
		{
			IProjectTemplate template = templatesPage.getSelectedTemplate();
			if (template != null && !template.getLocation().endsWith(".zip")) //$NON-NLS-1$
			{
				// assumes to be creating the project from a git URL
				fromGit = true;
				doCloneFromGit(template, newProjectHandle, description);
			}
		}
		if (!fromGit)
		{
			try
			{
				doBasicCreateProject(newProjectHandle, description);
				if (templatesPage != null)
				{
					IProjectTemplate template = templatesPage.getSelectedTemplate();
					if (template != null)
					{
						extractZip(template, newProjectHandle, true);
					}
				}
			}
			catch (CoreException e)
			{
				return null;
			}
		}

		newProject = newProjectHandle;
		return newProject;
	}

	private void doBasicCreateProject(IProject project, final IProjectDescription description) throws CoreException
	{
		// create the new project operation
		IRunnableWithProgress op = new IRunnableWithProgress()
		{
			public void run(IProgressMonitor monitor) throws InvocationTargetException
			{
				CreateProjectOperation op = new CreateProjectOperation(description,
						Messages.NewProjectWizard_CreateOp_Title);
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
		};

		// run the new project creation operation
		try
		{
			getContainer().run(true, true, op);
		}
		catch (InterruptedException e)
		{
			throw new CoreException(new Status(IStatus.ERROR, ProjectsPlugin.PLUGIN_ID, e.getMessage(), e));
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
							Messages.NewProjectWizard_Warning_DirectoryExists, project.getName()), cause));
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
	}

	/**
	 * @param template
	 * @param project
	 * @param preExistingResources
	 *            A possible conflicting list of resources that the extraction should notify about to the user.
	 */
	public static void extractZip(IProjectTemplate template, IProject project, Set<IPath> preExistingResources)
	{
		extractZip(template, new File(template.getDirectory(), template.getLocation()), project,
				!preExistingResources.isEmpty(), preExistingResources);
	}

	public static void extractZip(IProjectTemplate template, IProject project, boolean promptForOverwrite)
	{
		Set<IPath> emptySet = Collections.emptySet();
		extractZip(template, new File(template.getDirectory(), template.getLocation()), project, promptForOverwrite,
				emptySet);
	}

	/**
	 * Extracts a zip into a given project.
	 * 
	 * @param template
	 *            A project template.
	 * @param zipPath
	 * @param project
	 * @param promptForOverwrite
	 *            Indicate that we should display a prompt in case the zip overwrites some of the existing project
	 *            files.
	 * @param preExistingResources
	 *            A defined list of resources that will be used when prompting for overwrite conflicts. In case of an
	 *            empty list, the function will prompt on any overwritten file.
	 */
	public static void extractZip(final IProjectTemplate template, final File zipPath, final IProject project,
			boolean promptForOverwrite, Set<IPath> preExistingResources)
	{
		final Map<IFile, ZipEntry> conflicts = new HashMap<IFile, ZipEntry>();
		if (zipPath.exists())
		{
			ZipFile zipFile = null;
			try
			{
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
									newFile.setContents(getInputStream(zipFile, entry, newFile, project), true, true,
											null);
								}
							}
							else
							{
								newFile.setContents(getInputStream(zipFile, entry, newFile, project), true, true, null);
							}
						}
						else
						{
							try
							{
								newFile.create(getInputStream(zipFile, entry, newFile, project), true, null);
							}
							catch (CoreException re)
							{
								if (re.getStatus().getCode() == IResourceStatus.CASE_VARIANT_EXISTS
										&& re.getStatus() instanceof IResourceStatus)
								{
									IResourceStatus rs = (IResourceStatus) re.getStatus();
									IFile newVariantFile = project.getParent().getFile(rs.getPath());
									newVariantFile.setContents(getInputStream(zipFile, entry, newVariantFile, project),
											true, true, null);
								}
								else
								{
									ProjectsPlugin.logError(Messages.NewProjectWizard_ZipFailure, re);
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
												getInputStream(finalZipFile, conflicts.get(file), iFile, project),
												true, true, null);
									}
								}
								catch (Exception e)
								{
									ProjectsPlugin.logError(
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
				ProjectsPlugin.logError(e.getMessage(), e);
			}
			catch (Exception e)
			{
				ProjectsPlugin.logError(MessageFormat.format(Messages.NewProjectWizard_ERR_UnzipFile, zipPath), e);
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
	 * @return An input stream for the content.
	 * @throws IOException
	 * @throws CoreException
	 */
	private static InputStream getInputStream(ZipFile zipFile, ZipEntry entry, IFile file, IProject project)
			throws IOException, CoreException
	{
		if (!isSupportedFile(file))
		{
			return zipFile.getInputStream(entry);
		}
		String content = applyTemplateVariables(new InputStreamReader(zipFile.getInputStream(entry)), file, project);
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
					Messages.NewProjectWizard_templateVariableApplyError, e));
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

	private void doCloneFromGit(IProjectTemplate template, final IProject projectHandle,
			final IProjectDescription projectDescription)
	{
		IPath path = mainPage.getLocationPath();
		// when default is used, getLocationPath() only returns the workspace root, so needs to append the project name
		// to the path
		if (mainPage.useDefaults())
		{
			path = path.append(projectDescription.getName());
		}
		// FIXME Run an IrunnableWithProgress in wizard container, have it just do job.run(monitor)!
		Job job = new CloneJob(template.getLocation(), path.toOSString(), true, true);
		job.addJobChangeListener(new JobChangeAdapter()
		{

			@Override
			public void done(IJobChangeEvent event)
			{
				try
				{
					projectHandle.setDescription(projectDescription, null);
				}
				catch (CoreException e)
				{
				}

				DisconnectHandler disconnect = new DisconnectHandler(new JobChangeAdapter()
				{

					@Override
					public void done(IJobChangeEvent event)
					{
						IFolder gitFolder = projectHandle.getFolder(".git"); //$NON-NLS-1$
						if (gitFolder.exists())
						{
							try
							{
								gitFolder.delete(true, new NullProgressMonitor());
							}
							catch (CoreException e)
							{
							}
						}
					}
				});
				List<IResource> selection = new ArrayList<IResource>();
				selection.add(projectHandle);
				disconnect.setSelectedResources(selection);
				try
				{
					disconnect.execute(new ExecutionEvent());
				}
				catch (ExecutionException e)
				{
					ProjectsPlugin.logError(Messages.NewProjectWizard_ERR_FailToDisconnect, e);
				}
			}
		});
		job.schedule();
	}

	private void openIndexFile()
	{
		IFile indexFile = newProject.getFile("index.html"); //$NON-NLS-1$
		if (indexFile.exists())
		{
			IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
			if (page != null)
			{
				try
				{
					IDE.openEditor(page, indexFile);
				}
				catch (PartInitException e)
				{
					ProjectsPlugin.logError(Messages.NewProjectWizard_ERR_OpeningIndex, e);
				}
			}
		}
	}
}
