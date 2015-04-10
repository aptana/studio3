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
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IProjectNatureDescriptor;
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
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
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
import org.eclipse.ui.model.BaseWorkbenchContentProvider;
import org.eclipse.ui.wizards.datatransfer.IImportStructureProvider;
import org.eclipse.ui.wizards.datatransfer.ImportOperation;
import org.eclipse.ui.wizards.newresource.BasicNewResourceWizard;

import com.aptana.core.logging.IdeLog;
import com.aptana.core.util.ResourceUtil;
import com.aptana.core.util.StringUtil;
import com.aptana.projects.primary.natures.IPrimaryNatureContributor;
import com.aptana.projects.primary.natures.PrimaryNaturesManager;
import com.aptana.ui.epl.UIEplPlugin;
import com.aptana.ui.properties.NaturesLabelProvider;

/**
 * The WizardProjectsImportPage is the page that allows the user to import projects from a particular location.
 */
@SuppressWarnings("restriction")
public class WizardFolderImportPage extends WizardPage implements IOverwriteQuery, ICheckStateListener,
		SelectionListener
{
	private static final String APTANA_WEB_NATURE = "com.aptana.projects.webnature"; //$NON-NLS-1$

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

	private Button fMakePrimaryButton;
	private MenuItem fSetPrimaryMenuItem;

	private NaturesLabelProvider fLabelProvider;
	private CheckboxTableViewer fTableViewer;
	private String fPrimaryNature;
	private Map<String, String> fNatureDescriptions;

	// private ProjectRecord[] selectedProjects = new ProjectRecord[0];

	// Keep track of the directory that we browsed to last time
	// the wizard was invoked.
	private static String previouslyBrowsedDirectory = ""; //$NON-NLS-1$

	private Button browseDirectoriesButton;
	private Map<String, IPrimaryNatureContributor> natureContributors = new HashMap<String, IPrimaryNatureContributor>();

	/**
	 * Creates a new project creation wizard page.
	 * 
	 * @param natureContributors
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
		this(pageName, EplMessages.WizardFolderImportPage_ExistingFolderAsNewProject, null);
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
		setPageComplete(false);
		fNatureDescriptions = new HashMap<String, String>();
	}

	private void setPrimaryNatureFromContributions(IPath projectPath)
	{
		int highestPrimaryNatureRank = -1;
		String potentialPrimaryNature = null;
		for (String natureId : natureContributors.keySet())
		{
			IPrimaryNatureContributor primaryNatureContributor = natureContributors.get(natureId);
			int primaryNatureRank = primaryNatureContributor.getPrimaryNatureRank(projectPath);
			if (primaryNatureRank > highestPrimaryNatureRank)
			{
				potentialPrimaryNature = natureId;
				highestPrimaryNatureRank = primaryNatureRank;
			}
		}
		if (StringUtil.isEmpty(potentialPrimaryNature))
		{
			// Initially check off web nature if there are no potential ones.
			updatePrimaryNature(APTANA_WEB_NATURE);
		}
		else
		{
			updatePrimaryNature(potentialPrimaryNature);
		}
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

		Dialog.applyDialogFont(workArea);
		fLabelProvider = new NaturesLabelProvider(fNatureDescriptions);

		Label l = new Label(workArea, SWT.NONE);
		l.setText(EplMessages.WizardFolderImportPage_project_type_title);

		natureContributors = PrimaryNaturesManager.getManager().getContributorsMap();

		Composite tableComposite = new Composite(workArea, SWT.NONE);
		tableComposite.setLayout(GridLayoutFactory.fillDefaults().numColumns(2).create());
		tableComposite.setLayoutData(GridDataFactory.fillDefaults().grab(true, true).create());

		// Table for project natures
		fTableViewer = CheckboxTableViewer.newCheckList(tableComposite, SWT.TOP | SWT.BORDER);
		Table table = fTableViewer.getTable();
		table.setLinesVisible(true);
		table.setLayoutData(GridDataFactory.fillDefaults().grab(true, true).create());

		TableColumn column = new TableColumn(table, SWT.LEFT);
		column.setWidth(350);

		fTableViewer.setContentProvider(getContentProvider());
		fTableViewer.setLabelProvider(getLabelProvider());
		fTableViewer.setComparator(getViewerComperator());
		fTableViewer.setInput(ResourcesPlugin.getWorkspace());
		fTableViewer.addCheckStateListener(this);
		fTableViewer.addSelectionChangedListener(new ISelectionChangedListener()
		{
			public void selectionChanged(SelectionChangedEvent event)
			{
				updateButtons();
			}
		});
		table.setMenu(createMenu(table));

		// Add the buttons
		Composite buttons = new Composite(tableComposite, SWT.NONE);
		buttons.setLayout(GridLayoutFactory.fillDefaults().create());
		buttons.setLayoutData(GridDataFactory.fillDefaults().grab(false, true).create());
		fMakePrimaryButton = createButton(EplMessages.WizardFolderImportPage_make_primary_label, buttons);
		updateButtons();

		setPageComplete(false);
		setPrimaryNatureFromContributions(null);
		fTableViewer.setCheckedElements(new String[] { fPrimaryNature });

		if (!StringUtil.isEmpty(directoryPath))
		{
			directoryPathField.setText(directoryPath);
			setProjectName();
			setPageComplete(true);
		}
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
		l.setText(EplMessages.WizardFolderImportPage_SelectFolder);
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
		l.setText(EplMessages.WizardFolderImportPage_ProjectName);
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
			setErrorMessage(EplMessages.WizardFolderImportPage_ERR_NoFolderSelected);
			return false;
		}
		else if (!new File(directoryPathField.getText()).exists())
		{
			setErrorMessage(EplMessages.WizardFolderImportPage_ERR_FolderNotExist);
			return false;
		}
		else
		{
			String name = projectNameField.getText().trim();
			if (name.length() == 0)
			{
				setErrorMessage(EplMessages.WizardFolderImportPage_ERR_NoProjectName);
				return false;
			}
			if (projectsNames.contains(name))
			{
				setErrorMessage(EplMessages.WizardFolderImportPage_ERR_ProjectNameExists);
				return false;
			}

			IPath path = Path.fromOSString(directoryPathField.getText());
			setPrimaryNatureFromContributions(path);

			// Set a warning message if the imported project already contain certain project natures.
			IPath dotProjectPath = path.append(IProjectDescription.DESCRIPTION_FILE_NAME);

			IProjectDescription description = null;
			if (dotProjectPath.toFile().exists())
			{
				try
				{
					description = IDEWorkbenchPlugin.getPluginWorkspace().loadProjectDescription(dotProjectPath);
					if (description != null && description.getNatureIds().length > 0)
					{
						String delimiter = StringUtil.EMPTY;
						StringBuilder natures = new StringBuilder();
						for (String natureId : description.getNatureIds())
						{
							String nature = fLabelProvider.getText(natureId);
							if (StringUtil.isEmpty(nature))
							{
								nature = natureId;
							}
							natures.append(delimiter).append(nature);
							delimiter = ", "; //$NON-NLS-1$
						}
						String[] natureIds = description.getNatureIds();
						if (natureIds.length > 0 && !natureIds[0].equals(fPrimaryNature))
						{
							String[] oldNatures = natureIds;
							natureIds = new String[description.getNatureIds().length + 1];
							System.arraycopy(oldNatures, 0, natureIds, 1, oldNatures.length);
							natureIds[0] = fPrimaryNature;
						}
						// set the natures checked in the nature table as they are the most relevant ones.
						fTableViewer.setCheckedElements(natureIds);
						setMessage(EplMessages.WizardFolderImportPage_override_project_nature + natures.toString(),
								WARNING);
						setErrorMessage(null);
						return true;
					}
				}
				catch (CoreException e)
				{
					IdeLog.logWarning(UIEplPlugin.getDefault(), "Error reading project description for " + name, e); //$NON-NLS-1$
				}
			}
			// .project file does not exist. Lets get the natures from the Primary natures manager
			else
			{
				List<String> potentialNatures = PrimaryNaturesManager.getManager().getPotentialNaturesFromPath(path);

				// Add web nature to potential natures
				potentialNatures.add(APTANA_WEB_NATURE);

				// set the natures checked in the nature table as they are the most relevant ones.
				fTableViewer.setCheckedElements(potentialNatures.toArray(new String[potentialNatures.size()]));
			}
		}
		setMessage(null);
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
		String text = directoryPathField.getText();
		if (!StringUtil.isEmpty(text))
		{
			IPath path = new Path(text);
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
		File projectFile = new File(projectPath);
		if (projectFile.exists())
		{
			ProjectRecord pr = new ProjectRecord(projectFile, projectNameField.getText());
			IProject project = createExistingProject(pr);

			// Configure the project by its natures
			Object[] checkedNatures = fTableViewer.getCheckedElements();
			for (Object natureId : checkedNatures)
			{
				IPrimaryNatureContributor contributor = natureContributors.get(natureId);
				if (contributor != null)
				{
					try
					{
						contributor.configure(project);
					}
					catch (CoreException e)
					{
						IdeLog.logError(UIEplPlugin.getDefault(),
								MessageFormat.format("Error configurating project ''{0}'' while importing it", //$NON-NLS-1$
										project.getName()), e);
					}
				}
			}
			return project;
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
		Object[] checkedNatures = fTableViewer.getCheckedElements();
		final List<String> natureIds = new ArrayList<String>();
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

		for (Object nature : checkedNatures)
		{
			natureIds.add(nature.toString());
		}
		// promotes the primary nature to the front
		if (fPrimaryNature != null)
		{
			natureIds.remove(fPrimaryNature);
			natureIds.add(0, fPrimaryNature);
		}

		// if nothing is checked off, we use the default web nature
		if (natureIds.isEmpty())
		{
			natureIds.add(0, APTANA_WEB_NATURE);
		}

		record.description.setNatureIds(natureIds.toArray(new String[natureIds.size()]));

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
				project.setDescription(record.description, monitor);

				// We close and open the project to apply the natures correctly
				// project.close(monitor);
				// project.open(IResource.BACKGROUND_REFRESH, new SubProgressMonitor(monitor, 1000));

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
		if (directoryPathField != null && !StringUtil.isEmpty(directoryPath))
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

	private void updatePrimaryNature(String nature)
	{
		fPrimaryNature = nature;
		fLabelProvider.setPrimaryNature(fPrimaryNature);
		fTableViewer.refresh();
	}

	/**
	 * Returns a content provider for the list dialog. The content provider will include all available natures as
	 * strings.
	 * 
	 * @return the content provider that shows the natures (as string children)
	 */
	private IStructuredContentProvider getContentProvider()
	{
		return new BaseWorkbenchContentProvider()
		{
			@Override
			public Object[] getChildren(Object o)
			{
				if (!(o instanceof IWorkspace))
				{
					return new Object[0];
				}
				Set<String> elements = new HashSet<String>();
				// collect all available natures in the workspace
				IProjectNatureDescriptor[] natureDescriptors = ((IWorkspace) o).getNatureDescriptors();
				String natureId;
				for (IProjectNatureDescriptor descriptor : natureDescriptors)
				{
					natureId = descriptor.getNatureId();
					if (natureId != null)
					{
						if (ResourceUtil.isAptanaNature(natureId))
						{
							elements.add(natureId);
							fNatureDescriptions.put(natureId, descriptor.getLabel());
						}
					}
				}
				return elements.toArray();
			}
		};
	}

	private ILabelProvider getLabelProvider()
	{
		return fLabelProvider;
	}

	private ViewerComparator getViewerComperator()
	{
		return new ViewerComparator(new Comparator<String>()
		{

			public int compare(String o1, String o2)
			{
				// set Aptana natures ahead of others
				if (ResourceUtil.isAptanaNature(o1))
				{
					return ResourceUtil.isAptanaNature(o2) ? o1.compareTo(o2) : -1;
				}
				return ResourceUtil.isAptanaNature(o2) ? 1 : o1.compareTo(o2);
			}
		});
	}

	private Button createButton(String text, Composite parent)
	{
		Button button = new Button(parent, SWT.PUSH);
		button.setText(text);
		button.setLayoutData(GridDataFactory.fillDefaults().create());
		button.addSelectionListener(this);
		return button;
	}

	/**
	 * Updates the buttons' enablement.
	 */
	private void updateButtons()
	{
		StructuredSelection selection = (StructuredSelection) fTableViewer.getSelection();
		fMakePrimaryButton.setEnabled(!selection.isEmpty() && !isPrimary(selection.getFirstElement()));
	}

	/**
	 * Returns true if the given element string is set as the primary nature.
	 * 
	 * @param element
	 * @return true if the element is set as the primary nature, false otherwise
	 */
	protected boolean isPrimary(Object element)
	{
		return fPrimaryNature != null && fPrimaryNature.equals(element);
	}

	protected Menu createMenu(Table table)
	{
		Menu menu = new Menu(table);
		fSetPrimaryMenuItem = new MenuItem(menu, SWT.PUSH);
		fSetPrimaryMenuItem.setText(EplMessages.WizardFolderImportPage_set_primary_label);
		fSetPrimaryMenuItem.addSelectionListener(this);
		return menu;
	}

	public void checkStateChanged(CheckStateChangedEvent event)
	{
		// Check if the current checked items are the same as the initial ones.
		Object[] checkedElements = fTableViewer.getCheckedElements();
		if (fPrimaryNature == null)
		{
			// in case that the item was checked, set it as the primary
			if (event.getChecked())
			{
				updatePrimaryNature(event.getElement().toString());
				fTableViewer.refresh();
			}
		}
		else
		{
			if (!event.getChecked() && isPrimary(event.getElement()))
			{
				// find the next available item which is checked and set it to
				// the primary
				if (checkedElements.length == 0)
				{
					updatePrimaryNature(null);
				}
				else
				{
					updatePrimaryNature(checkedElements[0].toString());
				}
				fTableViewer.refresh();
			}
		}
		updateButtons();
	}

	public void widgetSelected(SelectionEvent e)
	{
		Object source = e.getSource();
		if (source == fSetPrimaryMenuItem || source == fMakePrimaryButton)
		{
			ISelection selection = fTableViewer.getSelection();
			if (!selection.isEmpty() && selection instanceof StructuredSelection)
			{
				Object firstElement = ((StructuredSelection) selection).getFirstElement();
				// make the element checked
				fTableViewer.setChecked(firstElement, true);
				// make it as primary
				updatePrimaryNature(firstElement.toString());
				fTableViewer.refresh();
				updateButtons();
			}
		}
	}

	public void widgetDefaultSelected(SelectionEvent e)
	{
	}
}
