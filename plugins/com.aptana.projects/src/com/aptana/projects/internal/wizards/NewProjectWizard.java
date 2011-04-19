/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.projects.internal.wizards;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

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
import org.eclipse.core.runtime.Status;
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
import com.aptana.core.projectTemplates.IProjectTemplate;
import com.aptana.core.projectTemplates.TemplateType;
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
		extractZip(new File(template.getDirectory(), template.getLocation()), project, !preExistingResources.isEmpty(),
				preExistingResources);
	}

	public static void extractZip(IProjectTemplate template, IProject project, boolean promptForOverwrite)
	{
		Set<IPath> emptySet = Collections.emptySet();
		extractZip(new File(template.getDirectory(), template.getLocation()), project, promptForOverwrite, emptySet);
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
	 */
	public static void extractZip(final File zipPath, IProject project, boolean promptForOverwrite,
			Set<IPath> preExistingResources)
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
								}
								else
								{
									// The file exists right now, but was not in the pre-existing resources we check
									// against, so we just need to set it with the new content.
									((IFile) newFile).setContents(zipFile.getInputStream(entry), true, true, null);
								}
							}
							else
							{
								((IFile) newFile).setContents(zipFile.getInputStream(entry), true, true, null);
							}
						}
						else
						{
							try
							{
								newFile.create(zipFile.getInputStream(entry), true, null);
							}
							catch (CoreException re)
							{
								if (re.getStatus().getCode() == IResourceStatus.CASE_VARIANT_EXISTS
										&& re.getStatus() instanceof IResourceStatus)
								{
									IResourceStatus rs = (IResourceStatus) re.getStatus();
									IFile newVariantFile = project.getParent().getFile(rs.getPath());
									((IFile) newVariantFile).setContents(zipFile.getInputStream(entry), true, true,
											null);
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
										((IFile) file).setContents(finalZipFile.getInputStream(conflicts.get(file)),
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
