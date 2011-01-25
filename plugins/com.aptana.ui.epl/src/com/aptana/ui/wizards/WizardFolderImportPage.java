/*******************************************************************************
 * Copyright (c) 2000, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package com.aptana.ui.wizards;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceStatus;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.WorkspaceModifyOperation;
import org.eclipse.ui.dialogs.IOverwriteQuery;
import org.eclipse.ui.internal.ide.IDEWorkbenchMessages;
import org.eclipse.ui.internal.ide.IDEWorkbenchPlugin;
import org.eclipse.ui.internal.wizards.datatransfer.DataTransferMessages;
import org.eclipse.ui.wizards.datatransfer.IImportStructureProvider;
import org.eclipse.ui.wizards.datatransfer.ImportOperation;
import org.eclipse.ui.wizards.newresource.BasicNewResourceWizard;

/**
 * The WizardProjectsImportPage is the page that allows the user to import projects from a particular location.
 */
@SuppressWarnings("restriction")
public class WizardFolderImportPage extends WizardPage implements IOverwriteQuery
{
	/**
	 * An internal class for projects
	 * 
	 * @author Ingo Muschenetz
	 */
	@SuppressWarnings("unused")
	private static class ProjectRecord
	{
		File projectSystemFile;

		Object projectArchiveFile;

		String projectName;

		Object parent;

		int level;

		IProjectDescription description;

		// ILeveledImportStructureProvider provider;

		/**
		 * Create a record for a project based on the info in the file.
		 * 
		 * @param file
		 */
		ProjectRecord(File file, String name)
		{
			projectSystemFile = file;
			setProjectName(name);
		}

		/**
		 * Set the name of the project based on the projectFile.
		 */
		private void setProjectName(String name)
		{
			projectName = name;
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

	private Text directoryPathField;
	private Text projectNameField;
	private String directoryPath;
	private ModifyListener modifyListener;
	private HashSet<String> projectsNames;

	// private ProjectRecord[] selectedProjects = new ProjectRecord[0];

	// Keep track of the directory that we browsed to last time
	// the wizard was invoked.
	private static String previouslyBrowsedDirectory = ""; //$NON-NLS-1$

	private Button browseDirectoriesButton;

	/**
	 * Creates a new project creation wizard page.
	 */
	public WizardFolderImportPage()
	{
		this("wizardExternalProjectsPage"); //$NON-NLS-1$
	}

	/**
	 * Create a new instance of the receiver.
	 * 
	 * @param pageName
	 */
	public WizardFolderImportPage(String pageName)
	{
		super(pageName);
		setPageComplete(false);
		setTitle(Messages.WizardFolderImportPage_ExistingFolderAsNewProject);
	}

	/**
	 * Create a new instance of the receiver.
	 * 
	 * @param pageName
	 * @param title
	 * @param titleImage
	 */
	public WizardFolderImportPage(String pageName, String title, ImageDescriptor titleImage)
	{
		super(pageName, title, titleImage);
	}

	/**
	 * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
	 */
	public void createControl(Composite parent)
	{
		// Collect the existing project names to avoid conflicts.
		IProject[] projects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
		projectsNames = new HashSet<String>();
		for (IProject project : projects)
		{
			projectsNames.add(project.getName());
		}
		modifyListener = new InputModifyListener();
		initializeDialogUnits(parent);

		Composite workArea = new Composite(parent, SWT.NONE);
		setControl(workArea);

		workArea.setLayout(new GridLayout());
		workArea.setLayoutData(new GridData(GridData.FILL_BOTH | GridData.GRAB_HORIZONTAL | GridData.GRAB_VERTICAL));

		createProjectsRoot(workArea);
		if (directoryPath != null)
		{
			directoryPathField.setText(directoryPath);
			setProjectName();
			setPageComplete(true);
		}
		Dialog.applyDialogFont(workArea);
		setPageComplete(validate());
	}

	/**
	 * Create the area where you select the root directory for the projects.
	 * 
	 * @param workArea
	 *            Composite
	 */
	private void createProjectsRoot(Composite workArea)
	{

		// project specification group
		Composite projectGroup = new Composite(workArea, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.numColumns = 3;
		layout.makeColumnsEqualWidth = false;
		layout.marginWidth = 0;
		projectGroup.setLayout(layout);
		projectGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		// project location entry field
		Label l = new Label(projectGroup, SWT.NONE);
		l.setText(Messages.WizardFolderImportPage_SelectFolder);
		this.directoryPathField = new Text(projectGroup, SWT.BORDER);
		this.directoryPathField.addModifyListener(modifyListener);
		this.directoryPathField.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.GRAB_HORIZONTAL));

		// browse button
		browseDirectoriesButton = new Button(projectGroup, SWT.PUSH);
		browseDirectoriesButton.setText(DataTransferMessages.DataTransfer_browse);
		setButtonLayoutData(browseDirectoriesButton);

		browseDirectoriesButton.addSelectionListener(new SelectionAdapter()
		{
			/*
			 * (non-Javadoc)
			 * @see org.eclipse.swt.events.SelectionAdapter#widgetS elected(org.eclipse.swt.events.SelectionEvent)
			 */
			public void widgetSelected(SelectionEvent e)
			{
				handleLocationDirectoryButtonPressed();
			}

		});

		// project name entry field
		l = new Label(projectGroup, SWT.NONE);
		l.setText(Messages.WizardFolderImportPage_ProjectName);
		projectNameField = new Text(projectGroup, SWT.BORDER);
		projectNameField.addModifyListener(modifyListener);
		projectNameField.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.GRAB_HORIZONTAL));
	}

	/**
	 * Input validation.
	 */
	protected boolean validate()
	{
		if (directoryPathField.getText().trim().length() == 0)
		{
			setErrorMessage(Messages.WizardFolderImportPage_ERR_NoFolderSelected);
			return false;
		}
		else if (!new File(directoryPathField.getText()).exists())
		{
			setErrorMessage(Messages.WizardFolderImportPage_ERR_FolderNotExist);
			return false;
		}
		else
		{
			String name = projectNameField.getText().trim();
			if (name.length() == 0)
			{
				setErrorMessage(Messages.WizardFolderImportPage_ERR_NoProjectName);
				return false;
			}
			if (projectsNames.contains(name))
			{
				setErrorMessage(Messages.WizardFolderImportPage_ERR_ProjectNameExists);
				return false;
			}
		}
		setErrorMessage(null);
		return true;
	}

	/**
	 * Display an error dialog with the specified message.
	 * 
	 * @param message
	 *            the error message
	 */
	protected void displayErrorDialog(String message)
	{
		MessageDialog.openError(getContainer().getShell(), getErrorDialogTitle(), message);
	}

	/**
	 * Get the title for an error dialog. Subclasses should override.
	 * 
	 * @return String
	 */
	protected String getErrorDialogTitle()
	{
		return IDEWorkbenchMessages.WizardExportPage_internalErrorTitle;
	}

	/**
	 * The browse button has been selected. Select the location.
	 */
	protected void handleLocationDirectoryButtonPressed()
	{

		DirectoryDialog dialog = new DirectoryDialog(directoryPathField.getShell());
		dialog.setMessage(DataTransferMessages.WizardProjectsImportPage_SelectDialogTitle);

		String dirName = directoryPathField.getText().trim();
		if (dirName.length() == 0)
		{
			dirName = previouslyBrowsedDirectory;
		}

		if (dirName.length() == 0)
		{
			dialog.setFilterPath(IDEWorkbenchPlugin.getPluginWorkspace().getRoot().getLocation().toOSString());
		}
		else
		{
			File path = new File(dirName);
			if (path.exists())
			{
				dialog.setFilterPath(new Path(dirName).toOSString());
			}
		}

		String selectedDirectory = dialog.open();
		if (selectedDirectory != null)
		{
			previouslyBrowsedDirectory = selectedDirectory;
			directoryPathField.setText(previouslyBrowsedDirectory);
		}

		setProjectName();

		setPageComplete(directoryPathField.getText() != null);

	}

	private void setProjectName()
	{
		if (directoryPathField.getText() != null)
		{
			IPath path = new Path(directoryPathField.getText());
			if (path.segmentCount() > 0)
			{
				projectNameField.setText(path.lastSegment());
			}
		}
	}

	/**
	 * Create and returns the project. In case the operation fails, <code>null</code> is returned.
	 * 
	 * @return a new project (or null if failed)
	 */
	public IProject createProject()
	{
		String projectPath = directoryPathField.getText();
		File project = new File(projectPath);
		if (project.exists())
		{
			ProjectRecord pr = new ProjectRecord(project, projectNameField.getText());
			return createExistingProject(pr);
		}
		return null;
	}

	/**
	 * Create the selected projects
	 * 
	 * @return boolean <code>true</code> if all project creations were successful.
	 * @deprecated since Aptana Studio 1.2.3. Use {@link #createProject()}
	 */
	public boolean createProjects()
	{
		return createProject() != null;
	}

	/**
	 * Create the project described in record.
	 * 
	 * @param record
	 * @return an new project if successful, null if failed.
	 */
	private IProject createExistingProject(final ProjectRecord record)
	{

		String projectName = record.getProjectName();
		final IWorkspace workspace = ResourcesPlugin.getWorkspace();
		final IProject project = workspace.getRoot().getProject(projectName);
		if (record.description == null)
		{
			record.description = workspace.newProjectDescription(projectName);
			IPath locationPath = new Path(record.projectSystemFile.getAbsolutePath());
			// IPath locationPath = new
			// Path(record.projectFile.getFullPath(record.projectFile.getRoot()));

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

		WorkspaceModifyOperation op = new WorkspaceModifyOperation()
		{
			protected void execute(IProgressMonitor monitor) throws CoreException
			{
				monitor.beginTask("", 2000); //$NON-NLS-1$
				project.create(record.description, new SubProgressMonitor(monitor, 1000));
				if (monitor.isCanceled())
				{
					throw new OperationCanceledException();
				}
				project.open(IResource.BACKGROUND_REFRESH, new SubProgressMonitor(monitor, 1000));
			}
		};
		// run the new project creation operation
		try
		{
			getContainer().run(true, true, op);
		}
		catch (InterruptedException e)
		{
			return null;
		}
		catch (InvocationTargetException e)
		{
			// ie.- one of the steps resulted in a core exception
			Throwable t = e.getTargetException();
			if (((CoreException) t).getStatus().getCode() == IResourceStatus.CASE_VARIANT_EXISTS)
			{
				MessageDialog.openError(getShell(), DataTransferMessages.WizardExternalProjectImportPage_errorMessage,
						NLS.bind(DataTransferMessages.WizardExternalProjectImportPage_caseVariantExistsError,
								record.description.getName()));
			}
			else
			{
				ErrorDialog.openError(getShell(), DataTransferMessages.WizardExternalProjectImportPage_errorMessage,
						((CoreException) t).getLocalizedMessage(), ((CoreException) t).getStatus());
			}
			return null;
		}

		try
		{
			showView("com.aptana.ide.ui.io.fileExplorerView", PlatformUI.getWorkbench().getActiveWorkbenchWindow()); //$NON-NLS-1$
		}
		catch (PartInitException e)
		{
		}

		BasicNewResourceWizard.selectAndReveal(project, PlatformUI.getWorkbench().getActiveWorkbenchWindow());
		return project;
	}

	/**
	 * Return a list of all files in the project
	 * 
	 * @param files
	 * @param provider
	 *            The provider for the parent file
	 * @param entry
	 *            The root directory of the project
	 * @return A list of all files in the project
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected boolean getFilesForProject(Collection files, IImportStructureProvider provider, Object entry)
	{
		List children = provider.getChildren(entry);
		Iterator childrenEnum = children.iterator();

		while (childrenEnum.hasNext())
		{
			Object child = childrenEnum.next();
			// Add the child, this way we get every files except the project
			// folder itself which we don't want
			files.add(child);
			// We don't have isDirectory for tar so must check for children
			// instead
			if (provider.isFolder(child))
			{
				getFilesForProject(files, provider, child);
			}
		}
		return true;
	}

	/**
	 * Execute the passed import operation. Answer a boolean indicating success.
	 * 
	 * @param op
	 * @return boolean
	 */
	protected boolean executeImportOperation(ImportOperation op)
	{
		// initializeOperation(op);
		try
		{
			getContainer().run(true, true, op);
		}
		catch (InterruptedException e)
		{
			return false;
		}
		catch (InvocationTargetException e)
		{
			// displayErrorDialog(e.getTargetException());
			return false;
		}

		IStatus status = op.getStatus();
		if (!status.isOK())
		{
			ErrorDialog.openError(getContainer().getShell(), DataTransferMessages.FileImport_importProblems, null, // no
					// special
					// message
					status);
			return false;
		}

		return true;
	}

	/**
	 * The <code>WizardDataTransfer</code> implementation of this <code>IOverwriteQuery</code> method asks the user
	 * whether the existing resource at the given path should be overwritten.
	 * 
	 * @param pathString
	 * @return the user's reply: one of <code>"YES"</code>, <code>"NO"</code>, <code>"ALL"</code>, or
	 *         <code>"CANCEL"</code>
	 */
	public String queryOverwrite(String pathString)
	{

		Path path = new Path(pathString);

		String messageString;
		// Break the message up if there is a file name and a directory
		// and there are at least 2 segments.
		if (path.getFileExtension() == null || path.segmentCount() < 2)
		{
			messageString = NLS.bind(IDEWorkbenchMessages.WizardDataTransfer_existsQuestion, pathString);
		}

		else
		{
			messageString = NLS.bind(IDEWorkbenchMessages.WizardDataTransfer_overwriteNameAndPathQuestion,
					path.lastSegment(), path.removeLastSegments(1).toOSString());
		}

		final MessageDialog dialog = new MessageDialog(getContainer().getShell(), IDEWorkbenchMessages.Question, null,
				messageString, MessageDialog.QUESTION, new String[] { IDialogConstants.YES_LABEL,
						IDialogConstants.YES_TO_ALL_LABEL, IDialogConstants.NO_LABEL, IDialogConstants.NO_TO_ALL_LABEL,
						IDialogConstants.CANCEL_LABEL }, 0);
		String[] response = new String[] { YES, ALL, NO, NO_ALL, CANCEL };
		// run in syncExec because callback is from an operation,
		// which is probably not running in the UI thread.
		getControl().getDisplay().syncExec(new Runnable()
		{
			public void run()
			{
				dialog.open();
			}
		});
		return dialog.getReturnCode() < 0 ? CANCEL : response[dialog.getReturnCode()];
	}

	/**
	 * @return Returns the directoryPathField.
	 */
	public String getDirectoryPath()
	{
		return directoryPath;
	}

	/**
	 * @param directoryPath
	 *            The directoryPathField to set.
	 */
	public void setDirectoryPath(String directoryPath)
	{
		this.directoryPath = directoryPath;
		if (directoryPathField != null)
		{
			directoryPathField.setText(directoryPath);
			setProjectName();
			setPageComplete(true);
		}
	}

	/**
	 * Show a specific view
	 * 
	 * @param viewId
	 *            The ID of the view to show
	 * @param window
	 *            The active window
	 * @return The IViewPart of the activated view.
	 * @throws PartInitException
	 */
	public static IViewPart showView(String viewId, IWorkbenchWindow window) throws PartInitException
	{
		IWorkbenchPage page = window.getActivePage();
		if (page != null)
		{
			return page.showView(viewId);
		}
		return null;
	}

	private class InputModifyListener implements ModifyListener
	{
		public void modifyText(ModifyEvent e)
		{
			setPageComplete(validate());
		}
	}
}
