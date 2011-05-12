/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.samples.ui.project;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.ICommand;
import org.eclipse.core.resources.IContainer;
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
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.dialogs.WizardNewProjectCreationPage;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.ide.undo.CreateProjectOperation;
import org.eclipse.ui.ide.undo.WorkspaceUndoUtil;
import org.eclipse.ui.statushandlers.IStatusAdapterConstants;
import org.eclipse.ui.statushandlers.StatusAdapter;
import org.eclipse.ui.statushandlers.StatusManager;
import org.eclipse.ui.wizards.newresource.BasicNewProjectResourceWizard;
import org.eclipse.ui.wizards.newresource.BasicNewResourceWizard;

import com.aptana.core.build.UnifiedBuilder;
import com.aptana.git.ui.CloneJob;
import com.aptana.git.ui.internal.actions.DisconnectHandler;
import com.aptana.projects.internal.wizards.NewProjectWizard;
import com.aptana.samples.handlers.ISampleProjectHandler;
import com.aptana.samples.model.ISample;
import com.aptana.samples.model.SampleEntry;
import com.aptana.samples.model.SamplesReference;
import com.aptana.samples.ui.SamplesUIPlugin;
import com.aptana.ui.util.UIUtils;

/**
 * @author Ingo Muschenetz
 * @author Michael Xia
 */
public class NewSampleProjectWizard extends BasicNewResourceWizard implements IExecutableExtension
{

	// the wizard ID
	public static final String ID = "com.aptana.samples.ui.NewSampleProjectWizard"; //$NON-NLS-1$

	private static final String NEWPROJECT_WIZARD = "BasicNewProjectResourceWizard"; //$NON-NLS-1$

	private ISample sample;
	private WizardNewProjectCreationPage mainPage;
	private IProject newProject;
	private IConfigurationElement configElement;

	/**
	 * A wizard to create a new sample project.
	 * 
	 * @param localSample
	 *            the root sample entry
	 */
	public NewSampleProjectWizard(ISample sample)
	{
		this.sample = sample;
		initDialogSettings();
	}

	@Override
	public void addPages()
	{
		super.addPages();

		mainPage = new WizardNewProjectCreationPage("basicNewProjectPage"); //$NON-NLS-1$
		mainPage.setTitle(Messages.NewSampleProjectWizard_ProjectPage_Title);
		mainPage.setDescription(Messages.NewSampleProjectWizard_ProjectPage_Description);
		addPage(mainPage);

		String name = sample.getName();
		if (name != null)
		{
			mainPage.setInitialProjectName(name);
		}
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

		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IProjectDescription description = workspace.newProjectDescription(newProjectHandle.getName());
		description.setLocationURI(location);
		SamplesReference samplesRef = sample.getReference();
		if (samplesRef != null)
		{
			description.setNatureIds(samplesRef.getNatures());
		}

		// Add Unified Builder
		ICommand command = description.newCommand();
		command.setBuilderName(UnifiedBuilder.ID);
		description.setBuildSpec(new ICommand[] { command });

		try
		{
			if (sample.isRemote())
			{
				cloneFromGit(sample.getPath(), newProjectHandle, description);
			}
			else
			{
				doBasicCreateProject(newProjectHandle, description);
				SampleEntry rootEntry = sample.getRootEntry();
				if (rootEntry.getFile().getName().endsWith(".zip")) //$NON-NLS-1$
				{
					Set<IPath> emptySet = Collections.emptySet();
					NewProjectWizard.extractZip(rootEntry.getFile(), newProjectHandle, true, emptySet);
				}
				else
				{
					copySampleSource(rootEntry, newProjectHandle);
				}
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

	private void doBasicCreateProject(IProject project, final IProjectDescription description) throws CoreException
	{
		// create the new project operation
		IRunnableWithProgress op = new IRunnableWithProgress()
		{
			public void run(IProgressMonitor monitor) throws InvocationTargetException
			{
				CreateProjectOperation op = new CreateProjectOperation(description,
						Messages.NewSampleProjectWizard_CreateOp_Title);
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

	private void copySampleSource(SampleEntry rootEntry, IProject project)
	{
		SampleEntry[] entries = rootEntry.getSubEntries();
		IProgressMonitor monitor = new NullProgressMonitor();
		IResource createdFile;
		if (entries != null)
		{
			for (SampleEntry entry : entries)
			{
				createdFile = createFile(project, entry.getFile(), monitor);
				if (createdFile instanceof IFolder)
				{
					createNestedEntries((IFolder) createdFile, entry, monitor);
				}
			}
		}

		SamplesReference samplesRef = sample.getReference();
		if (samplesRef != null)
		{
			String[] includes = samplesRef.getIncludePaths();
			File file;
			for (String include : includes)
			{
				file = new File(include);
				createdFile = createFile(project, file, monitor);
				if (createdFile instanceof IFolder)
				{
					createNestedFiles((IFolder) createdFile, file, monitor);
				}
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
		// FIXME Run an IrunnableWithProgress in wizard container, have it just do job.run(monitor)!
		Job job = new CloneJob(gitURL, path.toOSString(), true, true);
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
					SamplesUIPlugin.logError(Messages.NewSampleProjectWizard_ERR_FailToDisconnect, e);
				}

				doPostProjectCreation(newProject);
			}
		});
		job.schedule();
	}

	private void doPostProjectCreation(IProject newProject)
	{
		SamplesReference samplesRef = sample.getReference();
		if (samplesRef != null)
		{
			ISampleProjectHandler projectHandler = samplesRef.getProjectHandler();
			if (projectHandler != null)
			{
				projectHandler.projectCreated(newProject);
			}
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
					SamplesUIPlugin.logError(Messages.NewSampleProjectWizard_ERR_OpenIndexFile, e);
				}
			}
		}
	}

	/**
	 * Creates the workspace resource under the specific container (could be project or folder).
	 * 
	 * @param container
	 *            the parent directory in where the file is to be created
	 * @param file
	 *            the file
	 * @param monitor
	 *            the progress monitor
	 * @return the created resource
	 */
	private static IResource createFile(IContainer container, File file, IProgressMonitor monitor)
	{
		if (file.isDirectory())
		{
			IFolder folder = container.getFolder(new Path(file.getName()));
			if (!folder.exists())
			{
				try
				{
					folder.create(true, true, monitor);
				}
				catch (CoreException e)
				{
					return null;
				}
			}
			return folder;
		}

		IFile iFile = container.getFile(new Path(file.getName()));
		try
		{
			iFile.create(new FileInputStream(file), true, monitor);
			return iFile;
		}
		catch (FileNotFoundException e)
		{
		}
		catch (CoreException e)
		{
		}
		return null;
	}

	private static void createNestedEntries(IFolder folder, SampleEntry entry, IProgressMonitor monitor)
	{
		SampleEntry[] subentries = entry.getSubEntries();
		IResource createdFile;
		for (SampleEntry subentry : subentries)
		{
			createdFile = createFile(folder, subentry.getFile(), monitor);
			if (createdFile instanceof IFolder)
			{
				createNestedEntries((IFolder) createdFile, subentry, monitor);
			}
		}
	}

	private static void createNestedFiles(IFolder folder, File file, IProgressMonitor monitor)
	{
		File[] children = file.listFiles();
		IResource createdFile;
		for (File nestedFile : children)
		{
			createdFile = createFile(folder, nestedFile, monitor);
			if (createdFile instanceof IFolder)
			{
				createNestedFiles((IFolder) createdFile, nestedFile, monitor);
			}
		}
	}
}
