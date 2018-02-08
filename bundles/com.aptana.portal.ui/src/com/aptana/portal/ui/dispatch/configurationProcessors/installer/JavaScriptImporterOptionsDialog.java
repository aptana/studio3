/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.portal.ui.dispatch.configurationProcessors.installer;

import java.net.URI;

import org.eclipse.core.filesystem.URIUtil;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.osgi.util.TextProcessor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.ElementTreeSelectionDialog;
import org.eclipse.ui.model.WorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;
import org.eclipse.ui.views.navigator.ResourceComparator;

import com.aptana.core.CoreStrings;
import com.aptana.core.util.StringUtil;
import com.aptana.portal.ui.PortalUIPlugin;
import com.aptana.portal.ui.dispatch.configurationProcessors.Messages;

/**
 * An import dialog that appears right after we download a JavaScript library through the developer-toolbox. <br>
 * The dialog lets the user choose the project and the location in the project that the JS library will be saved.
 * 
 * @author Shalom Gibly <sgibly@aptana.com>
 */
public class JavaScriptImporterOptionsDialog extends InstallerOptionsDialog
{
	public static final String ACTIVE_PROJECT_ATTR = "active_project"; //$NON-NLS-1$s

	private static final String RAILS_NATURE = "org.radrails.rails.core.railsnature"; //$NON-NLS-1$
	private static final String RAILS_JS_PATH = "/public/javascripts"; //$NON-NLS-1$
	private static final String JS_PATH = "/javascripts"; //$NON-NLS-1$
	private Combo projectsCombo;
	private Button useDefaultsButton;
	private Text path;
	private Label locationLb;
	private Button browseBt;
	// The final selected location. Set on okPressed.
	private String selectedLocation;

	/**
	 * Constructs a new dialog for importing a downloaded library into a project.
	 * 
	 * @param parentShell
	 * @param libraryName
	 */
	public JavaScriptImporterOptionsDialog(Shell parentShell, String libraryName)
	{
		super(Display.getDefault().getActiveShell(), capitalize(libraryName));
		setTitleImage(PortalUIPlugin.getDefault().getImageRegistry().get(PortalUIPlugin.JS_IMAGE));
	}

	/**
	 * Configure the shell to display a title.
	 */
	@Override
	protected void configureShell(Shell newShell)
	{
		super.configureShell(newShell);
		newShell.setText(Messages.InstallProcessor_installerShellTitle);
	}

	/**
	 * Set the attributes for this dialog.<br>
	 * By default, the active project is set.
	 */
	@Override
	protected void setAttributes()
	{
		attributes.put(ACTIVE_PROJECT_ATTR, PortalUIPlugin.getActiveProject());
	}

	/**
	 * Returns the selected, valid, location for the JavaScript library location. Note that this location might still
	 * need to be created under the current project.
	 * 
	 * @return The selected location for the JavaScript library.
	 */
	public String getSelectedLocation()
	{
		return selectedLocation;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog#okPressed()
	 */
	@Override
	protected void okPressed()
	{
		selectedLocation = path.getText();
		super.okPressed();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.TitleAreaDialog#setErrorMessage(java.lang.String)
	 */
	@Override
	public void setErrorMessage(String newErrorMessage)
	{
		super.setErrorMessage(newErrorMessage);
		Button button = getButton(IDialogConstants.OK_ID);
		if (button != null)
		{
			button.setEnabled(newErrorMessage == null);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.TitleAreaDialog#createContents(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected Control createContents(Composite parent)
	{
		Control c = super.createContents(parent);
		validatePath();
		return c;
	}

	/**
	 * Creates the components inside the 'Installer' group. <br>
	 * The controls that are added here allow project selection and folder selection under the selected project.<br>
	 * 
	 * @param group
	 * @return A composite.
	 */
	protected Composite createInstallerGroupControls(Composite group)
	{
		int columns = 3;
		Composite projectGroup = new Composite(group, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.numColumns = columns;
		projectGroup.setLayout(layout);
		projectGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		Label projectSelectionLable = new Label(projectGroup, SWT.NONE);
		projectSelectionLable.setText(Messages.ImportJavaScriptLibraryDialog_projectLable);
		projectsCombo = new Combo(projectGroup, SWT.DROP_DOWN | SWT.READ_ONLY);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 2;
		projectsCombo.setLayoutData(gd);

		useDefaultsButton = new Button(projectGroup, SWT.CHECK | SWT.RIGHT);
		useDefaultsButton.setText(Messages.ImportJavaScriptLibraryDialog_useDefaultLocation);
		useDefaultsButton.setSelection(true);
		gd = new GridData();
		gd.horizontalSpan = columns;
		useDefaultsButton.setLayoutData(gd);

		createUserPathArea(projectGroup, true);

		useDefaultsButton.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e)
			{
				boolean useDefaults = useDefaultsButton.getSelection();
				path.setText(TextProcessor.process(getDefaultPath()));
				setPathAreaEnabled(!useDefaults);
			}
		});
		setPathAreaEnabled(false);
		initProjectsCombo(projectsCombo);
		return group;
	}

	/**
	 * Adds the projects in the workspace and selects the active one (if resolved).
	 * 
	 * @param combo
	 */
	private void initProjectsCombo(final Combo combo)
	{
		IProject[] allProjects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
		for (IProject project : allProjects)
		{
			if (project.isAccessible())
			{
				combo.add(project.getName());
				combo.setData(project.getName(), project);
			}
		}
		if (combo.getItemCount() == 0)
		{
			return;
		}
		IProject activeProject = PortalUIPlugin.getActiveProject();
		String activeProjectName = (activeProject != null) ? activeProject.getName() : null;
		if (activeProject != null && activeProject.isAccessible())
		{
			int index = combo.indexOf(activeProjectName);
			combo.select(Math.max(0, index));
			attributes.put(ACTIVE_PROJECT_ATTR, activeProject);
		}
		else
		{
			if (combo.getItemCount() > 0)
			{
				combo.select(0);
				attributes.put(ACTIVE_PROJECT_ATTR, combo.getData(combo.getText()));
			}
		}
		path.setText(TextProcessor.process(getDefaultPath()));

		// track the selections from now on
		combo.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				attributes.put(ACTIVE_PROJECT_ATTR, combo.getData(combo.getText()));
				// update the path
				path.setText(TextProcessor.process(getDefaultPath()));
			}
		});
	}

	/**
	 * Create the area for user entry.
	 * 
	 * @param composite
	 * @param defaultEnabled
	 */
	private void createUserPathArea(Composite composite, boolean defaultEnabled)
	{
		locationLb = new Label(composite, SWT.NONE);
		locationLb.setText(Messages.ImportJavaScriptLibraryDialog_locationLabel);
		path = new Text(composite, SWT.BORDER);
		GridData data = new GridData(GridData.FILL_HORIZONTAL);
		data.widthHint = 250;
		path.setLayoutData(data);

		// browse button
		browseBt = new Button(composite, SWT.PUSH);
		browseBt.setText(StringUtil.ellipsify(CoreStrings.BROWSE));
		browseBt.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent event)
			{
				ElementTreeSelectionDialog dialog = new ElementTreeSelectionDialog(getShell(),
						new WorkbenchLabelProvider(), new WorkbenchContentProvider());
				dialog.setBlockOnOpen(true);
				dialog.setAllowMultiple(false);
				dialog.setHelpAvailable(false);
				dialog.setTitle(Messages.ImportJavaScriptLibraryDialog_folderSelectionDialogTitle);
				dialog.setMessage(Messages.ImportJavaScriptLibraryDialog_folderSelectionDialogMessage);
				final IProject project = (IProject) attributes.get(ACTIVE_PROJECT_ATTR);
				dialog.setInput(ResourcesPlugin.getWorkspace().getRoot());
				dialog.setComparator(new ResourceComparator(ResourceComparator.NAME));
				// Filter out files and other projects
				dialog.addFilter(new ViewerFilter()
				{
					@Override
					public boolean select(Viewer viewer, Object parentElement, Object element)
					{
						if (parentElement instanceof IResource)
						{
							if (element instanceof IProject)
							{
								return element.equals(project);
							}
							IProject parentProject = ((IResource) parentElement).getProject();
							if (!parentProject.equals(project))
							{
								return false;
							}
							if (element instanceof IResource)
							{
								IResource resource = (IResource) element;
								if (resource.isHidden())
								{
									return false;
								}
								if (resource.getLocation().toFile().isDirectory())
								{
									return true;
								}
							}
						}
						return false;
					}
				});
				int buttonId = dialog.open();
				if (buttonId == IDialogConstants.OK_ID)
				{
					IResource resource = (IResource) dialog.getFirstResult();
					path.setText(TextProcessor.process(project.getName() + '/'
							+ resource.getProjectRelativePath().toString()));
				}
			}
		});

		path.setText(TextProcessor.process(getDefaultPath()));

		// Validate any user-input changes
		path.addModifyListener(new ModifyListener()
		{
			public void modifyText(ModifyEvent e)
			{
				validatePath();
			}
		});
	}

	/**
	 * Returns the default JavaScript library installation path for currently selected project.
	 * 
	 * @return The default path to place the JavaScript library under the selected project.
	 */
	protected String getDefaultPath()
	{
		IProject project = (IProject) attributes.get(ACTIVE_PROJECT_ATTR);
		if (project == null)
		{
			return StringUtil.EMPTY;
		}
		boolean isRails = false;
		try
		{
			isRails = project.hasNature(RAILS_NATURE);
		}
		catch (CoreException e)
		{
		}
		return project.getName() + (isRails ? RAILS_JS_PATH : JS_PATH);
	}

	/**
	 * Validate the typed path and notify the user on errors.
	 */
	protected void validatePath()
	{
		String pathString = path.getText();
		String errorMsg = null;
		// Check for empty content/projects list.
		if (projectsCombo.getItemCount() == 0)
		{
			errorMsg = Messages.ImportJavaScriptLibraryDialog_noAccessibleProjectsError;
		}

		if (errorMsg == null && pathString.length() == 0)
		{
			errorMsg = Messages.ImportJavaScriptLibraryDialog_emptyPathError;
		}
		URI uri = URIUtil.toURI(pathString);
		// Check for valid URI
		if (errorMsg == null && uri == null)
		{
			errorMsg = Messages.ImportJavaScriptLibraryDialog_invalidPathError;
		}
		if (errorMsg == null)
		{
			// Check that the URI is actually located under the selected project
			IProject project = (IProject) attributes.get(ACTIVE_PROJECT_ATTR);
			if (project != null)
			{
				// make sure we add a '/' prefix to the URI path to have this check cross-platform (Windows os returns
				// the URI without it).
				String projectPath = project.getFullPath().toString();
				String uriPath = uri.getPath();
				if (uriPath != null && !uriPath.startsWith("/")) //$NON-NLS-1$
				{
					uriPath = '/' + uriPath;
				}
				if (uriPath == null || !uriPath.startsWith(projectPath + '/'))
				{
					errorMsg = Messages.ImportJavaScriptLibraryDialog_wrongProjectRootError;
				}
			}
		}
		// set error message
		setErrorMessage(errorMsg);
	}

	/*
	 * Set the enablement state of the path area.
	 * @param enabled
	 */
	private void setPathAreaEnabled(boolean enabled)
	{
		locationLb.setEnabled(enabled);
		path.setEnabled(enabled);
		browseBt.setEnabled(enabled);
	}
}
