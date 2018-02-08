/**
 * Aptana Studio
 * Copyright (c) 2005-2013 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.explorer.internal.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IEclipsePreferences.IPreferenceChangeListener;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.search.ui.NewSearchUI;
import org.eclipse.search.ui.text.FileTextSearchScope;
import org.eclipse.search.ui.text.TextSearchQueryProvider;
import org.eclipse.search.ui.text.TextSearchQueryProvider.TextSearchInput;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.MenuEvent;
import org.eclipse.swt.events.MenuListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionGroup;
import org.eclipse.ui.actions.NewWizardAction;
import org.eclipse.ui.internal.WorkbenchPlugin;
import org.eclipse.ui.internal.dialogs.ImportExportWizard;
import org.eclipse.ui.internal.navigator.wizards.WizardShortcutAction;
import org.eclipse.ui.navigator.CommonNavigator;
import org.eclipse.ui.navigator.CommonViewer;
import org.eclipse.ui.part.PageBook;
import org.eclipse.ui.progress.UIJob;
import org.eclipse.ui.swt.IFocusService;
import org.eclipse.ui.wizards.IWizardDescriptor;
import org.eclipse.ui.wizards.IWizardRegistry;
import org.osgi.service.prefs.BackingStoreException;

import com.aptana.core.IScopeReference;
import com.aptana.core.logging.IdeLog;
import com.aptana.core.resources.IProjectContext;
import com.aptana.core.util.CollectionsUtil;
import com.aptana.core.util.EclipseUtil;
import com.aptana.explorer.ExplorerPlugin;
import com.aptana.explorer.IExplorerUIConstants;
import com.aptana.explorer.IPreferenceConstants;
import com.aptana.ui.util.UIUtils;
import com.aptana.ui.widgets.SearchComposite;

/**
 * Customized CommonNavigator that adds a project combo and focuses the view on a single project.
 * 
 * @author cwilliams
 */
@SuppressWarnings("restriction")
public abstract class SingleProjectView extends CommonNavigator implements SearchComposite.Client, IProjectContext
{

	private static final String RAILS_NATURE = "org.radrails.rails.core.railsnature"; //$NON-NLS-1$
	private static final String WEB_NATURE = "com.aptana.projects.webnature"; //$NON-NLS-1$
	private static final String PHP_NATURE = "com.aptana.editor.php.phpNature"; //$NON-NLS-1$

	/**
	 * Forced removal of context menu entries dynamically to match the context menu Andrew wants...
	 */
	private static final Set<String> TO_REMOVE = new HashSet<String>();
	static
	{
		TO_REMOVE.add("compareWithMenu"); //$NON-NLS-1$
		TO_REMOVE.add("replaceWithMenu"); //$NON-NLS-1$
		TO_REMOVE.add("org.eclipse.ui.framelist.goInto"); //$NON-NLS-1$
		TO_REMOVE.add("org.radrails.rails.ui.actions.RunScriptServerAction"); //$NON-NLS-1$
		TO_REMOVE.add("org.radrails.rails.ui.actions.DebugScriptServerAction"); //$NON-NLS-1$
	};

	private ToolItem projectToolItem;
	private Menu projectsMenu;

	protected IProject selectedProject;

	/**
	 * Listens for the addition/removal of projects.
	 */
	private ResourceListener fProjectsListener;

	private Composite filterComp;
	private CLabel filterLabel;
	private GridData filterLayoutData;

	// listen for external changes to active project
	private IPreferenceChangeListener fActiveProjectPrefChangeListener;

	private Label noProjectslabel;
	/**
	 * Composite holding the create project/import buttons
	 */
	private Composite noProjectButtonsComp;

	private Label closedProjectlabel;
	/**
	 * Composite holding the create project/import buttons, plus button to open project
	 */
	private Composite closedProjectButtonsComp;

	/**
	 * PageBook to swap between normal common viewer when there's at least one project, and composite containing buttons
	 * to add/import projects
	 */
	private PageBook pageBook;

	private Button createProjectWhenNoneButton;
	private Button importProjectWhenNoneButton;
	private Button openProjectButton;
	private Button createProjectWhenClosedButton;
	private Button importProjectWhenClosedButton;

	private static final String CLOSE_ICON = "icons/full/elcl16/close.png"; //$NON-NLS-1$

	@Override
	public void createPartControl(final Composite parent)
	{
		GridLayout gridLayout = (GridLayout) parent.getLayout();
		gridLayout.marginHeight = 0;
		gridLayout.verticalSpacing = 1;

		// Create toolbar
		Composite toolbarComposite = new Composite(parent, SWT.NONE);
		toolbarComposite.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));

		GridLayout toolbarGridLayout = new GridLayout(2, false);
		toolbarGridLayout.marginWidth = 0;
		toolbarGridLayout.marginHeight = 0;
		toolbarGridLayout.horizontalSpacing = 0;
		toolbarComposite.setLayout(toolbarGridLayout);

		// For project and branch....
		Composite pulldowns = new Composite(toolbarComposite, SWT.NONE);
		pulldowns.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
		RowLayout rowLayout = new RowLayout();
		rowLayout.wrap = true;
		rowLayout.spacing = 0;
		rowLayout.marginLeft = 0;
		rowLayout.marginRight = 0;
		rowLayout.marginBottom = 0;
		rowLayout.marginTop = 0;
		pulldowns.setLayout(rowLayout);

		// Projects combo
		createProjectCombo(pulldowns);

		// Let sub classes add to the toolbar (git branch)
		doCreateToolbar(pulldowns);

		createSearchComposite(parent);
		filterComp = createFilterComposite(parent);
		createNavigator(parent);

		// Remove the navigation actions
		getViewSite().getActionBars().getToolBarManager().remove("org.eclipse.ui.framelist.back"); //$NON-NLS-1$
		getViewSite().getActionBars().getToolBarManager().remove("org.eclipse.ui.framelist.forward"); //$NON-NLS-1$
		getViewSite().getActionBars().getToolBarManager().remove("org.eclipse.ui.framelist.up"); //$NON-NLS-1$

		addProjectResourceListener();
		setActiveProject(detectSelectedProject());
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Object getAdapter(Class adapter)
	{
		if (adapter == IScopeReference.class)
		{
			return new IScopeReference()
			{

				public String getScopeId()
				{
					if (selectedProject != null)
					{
						try
						{
							if (selectedProject.hasNature(RAILS_NATURE))
							{
								return "project.rails"; //$NON-NLS-1$
							}
							if (selectedProject.hasNature(WEB_NATURE))
							{
								return "project.web"; //$NON-NLS-1$
							}
							if (selectedProject.hasNature(PHP_NATURE))
							{
								return "project.php"; //$NON-NLS-1$
							}
						}
						catch (CoreException e)
						{
							ExplorerPlugin.logError(e);
						}
					}
					return null;
				}
			};
		}
		if (adapter == IProject.class)
		{
			return selectedProject;
		}
		return super.getAdapter(adapter);
	}

	@Override
	public void saveState(IMemento aMemento)
	{
		if (aMemento != null && this.selectedProject != null)
		{
			aMemento.putString(IPreferenceConstants.ACTIVE_PROJECT, this.selectedProject.getName());
		}
		super.saveState(aMemento);
	}

	protected abstract void doCreateToolbar(Composite toolbarComposite);

	private IProject[] createProjectCombo(Composite parent)
	{
		final ToolBar projectsToolbar = new ToolBar(parent, SWT.FLAT);
		projectToolItem = new ToolItem(projectsToolbar, SWT.DROP_DOWN);
		IProject[] projects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
		List<IProject> sortedProjects = CollectionsUtil.newList(projects);
		Collections.sort(sortedProjects, new CaseInsensitiveProjectComparator());

		projectsMenu = new Menu(projectsToolbar);
		for (IProject iProject : sortedProjects)
		{
			// hide projects that don't exist
			if (!iProject.exists())
			{
				continue;
			}

			// Construct the menu to attach to the above button.
			final MenuItem projectNameMenuItem = new MenuItem(projectsMenu, SWT.RADIO);
			projectNameMenuItem.setText(iProject.getName());
			projectNameMenuItem.setSelection(false);
			Image image = null;
			// Visually mark closed/open projects
			if (iProject.isOpen())
			{
				image = PlatformUI.getWorkbench().getSharedImages()
						.getImage(org.eclipse.ui.ide.IDE.SharedImages.IMG_OBJ_PROJECT);
			}
			else
			{
				image = PlatformUI.getWorkbench().getSharedImages()
						.getImage(org.eclipse.ui.ide.IDE.SharedImages.IMG_OBJ_PROJECT_CLOSED);
			}
			if (image != null)
			{
				projectNameMenuItem.setImage(image);
			}
			projectNameMenuItem.addSelectionListener(new SelectionAdapter()
			{

				@Override
				public void widgetSelected(SelectionEvent e)
				{
					String projectName = projectNameMenuItem.getText();
					projectToolItem.setText(projectName);
					setActiveProject(ResourcesPlugin.getWorkspace().getRoot().getProject(projectName));
				}
			});
		}

		projectToolItem.addSelectionListener(new SelectionAdapter()
		{

			@Override
			public void widgetSelected(SelectionEvent selectionEvent)
			{
				Point toolbarLocation = projectsToolbar.getLocation();
				toolbarLocation = projectsToolbar.getParent().toDisplay(toolbarLocation.x, toolbarLocation.y);
				Point toolbarSize = projectsToolbar.getSize();
				projectsMenu.setLocation(toolbarLocation.x, toolbarLocation.y + toolbarSize.y + 2);
				projectsMenu.setVisible(true);
			}
		});
		return projects;
	}

	protected Composite createSearchComposite(Composite myComposite)
	{
		SearchComposite search = new SearchComposite(myComposite, this);
		search.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

		// Register with focus service so that Cut/Copy/Paste/SelecAll handlers will work
		IFocusService focusService = (IFocusService) getViewSite().getService(IFocusService.class);
		focusService.addFocusTracker(search.getTextControl(), IExplorerUIConstants.VIEW_ID + ".searchText"); //$NON-NLS-1$

		return search;
	}

	private void createNavigator(Composite myComposite)
	{
		Composite viewer = new Composite(myComposite, SWT.BORDER);
		viewer.setLayout(new FillLayout());
		viewer.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		pageBook = new PageBook(viewer, SWT.NONE);

		super.createPartControl(pageBook);

		IProject selectedProject = detectSelectedProject();

		getCommonViewer().setInput(selectedProject);

		createNoProjectsComposite();
		createClosedProjectComposite();

		if (selectedProject != null)
		{
			if (selectedProject.isAccessible())
			{
				pageBook.showPage(getCommonViewer().getControl());
			}
			else
			{
				pageBook.showPage(closedProjectButtonsComp);
			}
		}
		else
		{
			pageBook.showPage(noProjectButtonsComp);
		}
		fixNavigatorManager();
	}

	protected Composite createNoProjectsComposite()
	{
		// Create a composite to add/import projects when there are none
		noProjectButtonsComp = new Composite(pageBook, SWT.NONE);

		GridLayoutFactory.fillDefaults().applyTo(noProjectButtonsComp);
		GridDataFactory.fillDefaults().grab(true, true).align(SWT.CENTER, SWT.CENTER).applyTo(noProjectButtonsComp);

		noProjectslabel = new Label(noProjectButtonsComp, SWT.WRAP);
		noProjectslabel.setText(Messages.SingleProjectView_NoProjectsDescription);
		GridDataFactory.fillDefaults().grab(true, false).align(SWT.CENTER, SWT.CENTER).indent(5, 10)
				.applyTo(noProjectslabel);

		createProjectWhenNoneButton = new Button(noProjectButtonsComp, SWT.FLAT);
		createProjectWhenNoneButton.setText(Messages.SingleProjectView_CreateProjectButtonLabel);
		createProjectWhenNoneButton.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				NewWizardAction action = new NewWizardAction(UIUtils.getActiveWorkbenchWindow());
				action.run();
			}
		});
		GridDataFactory.fillDefaults().grab(true, false).align(SWT.CENTER, SWT.CENTER).indent(0, 5)
				.applyTo(createProjectWhenNoneButton);

		importProjectWhenNoneButton = new Button(noProjectButtonsComp, SWT.FLAT);
		importProjectWhenNoneButton.setText(Messages.SingleProjectView_ImportProjectButtonLabel);
		importProjectWhenNoneButton.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				openImportWizard();
			}
		});
		GridDataFactory.fillDefaults().grab(true, false).align(SWT.CENTER, SWT.CENTER)
				.applyTo(importProjectWhenNoneButton);

		return noProjectButtonsComp;
	}

	protected Composite createClosedProjectComposite()
	{
		// Create a composite to open closed project or create/import project
		closedProjectButtonsComp = new Composite(pageBook, SWT.NONE);

		GridLayoutFactory.fillDefaults().applyTo(closedProjectButtonsComp);
		GridDataFactory.fillDefaults().grab(true, true).align(SWT.CENTER, SWT.CENTER).applyTo(closedProjectButtonsComp);

		closedProjectlabel = new Label(closedProjectButtonsComp, SWT.WRAP);
		closedProjectlabel.setText(Messages.SingleProjectView_ClosedProjectSelectedLabel);
		GridDataFactory.fillDefaults().grab(true, false).align(SWT.CENTER, SWT.CENTER).indent(5, 10)
				.applyTo(closedProjectlabel);

		openProjectButton = new Button(closedProjectButtonsComp, SWT.FLAT);
		openProjectButton.setText(Messages.SingleProjectView_OpenProjectButton);
		openProjectButton.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				try
				{
					selectedProject.open(new NullProgressMonitor());
					pageBook.showPage(getCommonViewer().getControl());
				}
				catch (CoreException e1)
				{
					// ignore
				}
			}
		});
		GridDataFactory.fillDefaults().grab(true, false).align(SWT.CENTER, SWT.CENTER).indent(0, 5)
				.applyTo(openProjectButton);

		createProjectWhenClosedButton = new Button(closedProjectButtonsComp, SWT.FLAT);
		createProjectWhenClosedButton.setText(Messages.SingleProjectView_CreateProjectButtonLabel);
		createProjectWhenClosedButton.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				NewWizardAction action = new NewWizardAction(UIUtils.getActiveWorkbenchWindow());
				action.run();
			}
		});
		GridDataFactory.fillDefaults().grab(true, false).align(SWT.CENTER, SWT.CENTER)
				.applyTo(createProjectWhenClosedButton);

		importProjectWhenClosedButton = new Button(closedProjectButtonsComp, SWT.FLAT);
		importProjectWhenClosedButton.setText(Messages.SingleProjectView_ImportProjectButtonLabel);
		importProjectWhenClosedButton.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				openImportWizard();
			}
		});
		GridDataFactory.fillDefaults().grab(true, false).align(SWT.CENTER, SWT.CENTER)
				.applyTo(importProjectWhenClosedButton);

		return closedProjectButtonsComp;
	}

	@Override
	protected CommonViewer createCommonViewer(Composite aParent)
	{
		CommonViewer aViewer = createCommonViewerObject(aParent);
		initListeners(aViewer);
		aViewer.getNavigatorContentService().restoreState(memento);
		return aViewer;
	}

	/**
	 * Force us to return the active project as the implicit selection if there' an empty selection. This fixes the
	 * issue where new file/Folder won't show in right click menu with no selection (like in a brand new generic
	 * project).
	 */
	protected CommonViewer createCommonViewerObject(Composite aParent)
	{
		return new CommonViewer(getViewSite().getId(), aParent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL
				| SWT.FULL_SELECTION)
		{

			@Override
			public ISelection getSelection()
			{
				ISelection sel = super.getSelection();
				if (sel.isEmpty() && selectedProject != null)
				{
					List<IProject> selectedProjectList = new ArrayList<IProject>();
					selectedProjectList.add(selectedProject);
					TreePath treePath = new TreePath(selectedProjectList.toArray());
					ITreeSelection treeSelection = new TreeSelection(treePath);
					return treeSelection;
				}
				return sel;
			}
		};
	}

	@Override
	protected ActionGroup createCommonActionGroup()
	{
		return new AppExplorerActionGroup(this, getCommonViewer(), getLinkHelperService());
	}

	private void fixNavigatorManager()
	{
		// HACK! This is to fix behavior that Eclipse bakes into
		// CommonNavigatorManager.UpdateActionBarsJob where it
		// forces the selection context for actions tied to the view to the
		// view's input *even if it already has a
		// perfectly fine and valid selection!* This forces the selection again
		// in a delayed job which hopefully runs
		// right after their %^$&^$!! job.
		UIJob job = new UIJob(getTitle())
		{

			@Override
			public IStatus runInUIThread(IProgressMonitor monitor)
			{
				getCommonViewer().setSelection(getCommonViewer().getSelection());
				return Status.OK_STATUS;
			}
		};
		EclipseUtil.setSystemForJob(job);
		job.setPriority(Job.BUILD);
		job.schedule(250);

		getCommonViewer().getTree().getMenu().addMenuListener(new MenuListener()
		{

			public void menuShown(MenuEvent e)
			{
				Menu menu = (Menu) e.getSource();
				mangleContextMenu(menu);
			}

			public void menuHidden(MenuEvent e)
			{
				// do nothing
			}
		});
	}

	private Composite createFilterComposite(final Composite myComposite)
	{
		Composite filter = new Composite(myComposite, SWT.NONE);
		GridLayout gridLayout = new GridLayout(2, false);
		gridLayout.marginWidth = 2;
		gridLayout.marginHeight = 0;
		gridLayout.marginBottom = 2;
		filter.setLayout(gridLayout);

		filterLayoutData = new GridData(SWT.FILL, SWT.CENTER, true, false);
		filterLayoutData.exclude = true;
		filter.setLayoutData(filterLayoutData);

		filterLabel = new CLabel(filter, SWT.LEFT);
		filterLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

		ToolBar toolBar = new ToolBar(filter, SWT.FLAT);
		toolBar.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));

		ToolItem toolItem = new ToolItem(toolBar, SWT.PUSH);
		toolItem.setImage(ExplorerPlugin.getImage(CLOSE_ICON));
		toolItem.addSelectionListener(new SelectionListener()
		{

			public void widgetSelected(SelectionEvent e)
			{
				removeFilter();
			}

			public void widgetDefaultSelected(SelectionEvent e)
			{
			}
		});

		return filter;
	}

	private void hideFilterLabel()
	{
		filterLayoutData.exclude = true;
		filterComp.setVisible(false);
		filterComp.getParent().layout();
	}

	protected void showFilterLabel(Image image, String text)
	{
		filterLabel.setImage(image);
		filterLabel.setText(text);
		filterLayoutData.exclude = false;
		filterComp.setVisible(true);
		filterComp.getParent().layout();
	}

	protected void removeFilter()
	{
		hideFilterLabel();
	}

	private void addProjectResourceListener()
	{
		fProjectsListener = new ResourceListener();
		ResourcesPlugin.getWorkspace().addResourceChangeListener(fProjectsListener, IResourceChangeEvent.POST_CHANGE);
	}

	private IProject detectSelectedProject()
	{
		IProject project = null;
		String activeProjectName = null;
		if (this.memento != null)
		{
			activeProjectName = this.memento.getString(IPreferenceConstants.ACTIVE_PROJECT);
		}
		if (activeProjectName != null)
		{
			project = ResourcesPlugin.getWorkspace().getRoot().getProject(activeProjectName);
		}
		if (project == null)
		{
			String value = Platform.getPreferencesService().getString(ExplorerPlugin.PLUGIN_ID,
					IPreferenceConstants.ACTIVE_PROJECT, null, null);
			if (value != null)
			{
				project = ResourcesPlugin.getWorkspace().getRoot().getProject(value);
			}
			if (project == null)
			{
				IProject[] projects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
				if (projects == null || projects.length == 0)
				{
					return null;
				}
				for (IProject proj : projects)
				{
					if (proj.isAccessible())
					{
						project = proj;
						break;
					}
				}
			}
		}
		return project;
	}

	public void setActiveProject(IProject newSelectedProject)
	{
		// Project may be same but it may have been closed or opened...
		boolean projectIsSame = (selectedProject != null && newSelectedProject != null && selectedProject
				.equals(newSelectedProject));

		IProject oldActiveProject = selectedProject;
		selectedProject = newSelectedProject;
		if (newSelectedProject != null)
		{
			// Save project in prefs
			if (!projectIsSame)
			{
				setActiveProject();
			}
			if (newSelectedProject.isAccessible())
			{
				// An open project, show it's contents
				pageBook.showPage(getCommonViewer().getControl());
			}
			else if (newSelectedProject.exists())
			{
				// Project exists, but is closed, show special page to allow opening
				pageBook.showPage(closedProjectButtonsComp);
			}
		}
		else
		{
			// A project that doesn't exist, remove from prefs and show page to create/import project
			unsetActiveProject();
			pageBook.showPage(noProjectButtonsComp);
		}
		if (!projectIsSame)
		{
			projectChanged(oldActiveProject, newSelectedProject);
		}
	}

	private void setActiveProject()
	{
		try
		{
			IEclipsePreferences prefs = InstanceScope.INSTANCE.getNode(ExplorerPlugin.PLUGIN_ID);
			prefs.put(IPreferenceConstants.ACTIVE_PROJECT, selectedProject.getName());
			prefs.flush();
		}
		catch (BackingStoreException e)
		{
			IdeLog.logError(ExplorerPlugin.getDefault(), e);
		}
	}

	private void unsetActiveProject()
	{
		try
		{
			IEclipsePreferences prefs = InstanceScope.INSTANCE.getNode(ExplorerPlugin.PLUGIN_ID);
			prefs.remove(IPreferenceConstants.ACTIVE_PROJECT);
			prefs.flush();
		}
		catch (BackingStoreException e)
		{
			IdeLog.logError(ExplorerPlugin.getDefault(), e);
		}
	}

	/**
	 * @param oldProject
	 * @param newProject
	 */
	protected void projectChanged(IProject oldProject, IProject newProject)
	{
		// Set project pulldown
		String newProjectName = ""; //$NON-NLS-1$
		if (newProject != null && newProject.exists())
		{
			newProjectName = newProject.getName();
		}
		projectToolItem.setText(newProjectName);
		MenuItem[] menuItems = projectsMenu.getItems();
		for (MenuItem menuItem : menuItems)
		{
			menuItem.setSelection(menuItem.getText().equals(newProjectName));
		}
		getCommonViewer().setInput(newProject);
		if (newProject == null)
		{
			// Clear the selection when there is no active project so the menus
			// get updated correctly
			getCommonViewer().setSelection(StructuredSelection.EMPTY);
		}
	}

	protected void refreshViewer()
	{
		if (getCommonViewer() == null || getCommonViewer().getTree() == null
				|| getCommonViewer().getTree().isDisposed())
		{
			return;
		}
		getCommonViewer().refresh(selectedProject, true);
	}

	@Override
	public void dispose()
	{
		super.dispose();

		removeProjectResourceListener();
		removeActiveProjectPrefListener();
	}

	private void removeProjectResourceListener()
	{
		ResourcesPlugin.getWorkspace().removeResourceChangeListener(fProjectsListener);
		fProjectsListener = null;
	}

	private void removeActiveProjectPrefListener()
	{
		if (fActiveProjectPrefChangeListener != null)
		{
			InstanceScope.INSTANCE.getNode(ExplorerPlugin.PLUGIN_ID).removePreferenceChangeListener(
					fActiveProjectPrefChangeListener);
		}
		fActiveProjectPrefChangeListener = null;
	}

	/**
	 * Listens for Project addition/removal to change the active project to new project added, or off the deleted
	 * project if it was active.
	 * 
	 * @author cwilliams
	 */
	private class ResourceListener implements IResourceChangeListener
	{

		public void resourceChanged(IResourceChangeEvent event)
		{
			IResourceDelta delta = event.getDelta();
			if (delta == null)
			{
				return;
			}
			try
			{
				delta.accept(new IResourceDeltaVisitor()
				{

					public boolean visit(final IResourceDelta delta) throws CoreException
					{
						IResource resource = delta.getResource();
						if (resource.getType() == IResource.FILE || resource.getType() == IResource.FOLDER)
						{
							return false;
						}
						if (resource.getType() == IResource.ROOT)
						{
							return true;
						}
						if (resource.getType() == IResource.PROJECT)
						{
							// a project was added, removed, or changed!
							if (delta.getKind() == IResourceDelta.ADDED
									|| (delta.getKind() == IResourceDelta.CHANGED
											&& (delta.getFlags() & IResourceDelta.OPEN) != 0 && resource.isAccessible()))
							{
								// Add to the projects menu and then switch to it!
								final String projectName = resource.getName();
								Display.getDefault().asyncExec(new Runnable()
								{

									public void run()
									{
										// Construct the menu item to for this project
										// Insert in alphabetical order
										int index = projectsMenu.getItemCount();
										MenuItem[] items = projectsMenu.getItems();
										for (int i = 0; i < items.length; i++)
										{
											String otherName = items[i].getText();
											int comparison = otherName.compareTo(projectName);
											if (comparison == 0)
											{
												// Don't add dupes!
												index = -1;
												break;
											}
											else if (comparison > 0)
											{
												index = i;
												break;
											}
										}
										if (index == -1)
										{
											return;
										}
										final MenuItem projectNameMenuItem = new MenuItem(projectsMenu, SWT.RADIO,
												index);
										projectNameMenuItem.setText(projectName);
										projectNameMenuItem.setImage(PlatformUI.getWorkbench().getSharedImages()
												.getImage(org.eclipse.ui.ide.IDE.SharedImages.IMG_OBJ_PROJECT));
										projectNameMenuItem.addSelectionListener(new SelectionAdapter()
										{
											public void widgetSelected(SelectionEvent e)
											{
												String projectName = projectNameMenuItem.getText();
												projectToolItem.setText(projectName);
												setActiveProject(ResourcesPlugin.getWorkspace().getRoot()
														.getProject(projectName));
											}
										});
										// only switch the active project if a project is added, not when a project is
										// opened
										if (delta.getKind() == IResourceDelta.ADDED)
										{
											projectNameMenuItem.setSelection(true);
											setActiveProject(ResourcesPlugin.getWorkspace().getRoot()
													.getProject(projectName));
										}
										projectToolItem.getParent().pack(true);
									}
								});
							}
							else if (delta.getKind() == IResourceDelta.REMOVED
									|| (delta.getKind() == IResourceDelta.CHANGED
											&& (delta.getFlags() & IResourceDelta.OPEN) != 0 && !resource
												.isAccessible()))
							{
								// Remove from menu and if it was the active
								// project, switch away from it!
								final String projectName = resource.getName();
								Display.getDefault().asyncExec(new Runnable()
								{

									public void run()
									{
										MenuItem[] menuItems = projectsMenu.getItems();
										for (MenuItem menuItem : menuItems)
										{
											if (menuItem.getText().equals(projectName))
											{
												// Remove the menu item
												menuItem.dispose();
												break;
											}
										}
										if (selectedProject != null && selectedProject.getName().equals(projectName))
										{
											IProject[] projects = ResourcesPlugin.getWorkspace().getRoot()
													.getProjects();
											boolean setProject = false;
											// Set active project to first open/accessible one!
											for (IProject p : projects)
											{
												if (p != null && p.isAccessible())
												{
													setActiveProject(p);
													setProject = true;
													break;
												}
											}
											// Failing that, set to first project that exists!
											if (!setProject)
											{
												for (IProject p : projects)
												{
													if (p != null && p.exists())
													{
														setActiveProject(p);
														setProject = true;
														break;
													}
												}
											}
											if (!setProject)
											{
												setActiveProject(null);
											}
										}
										projectToolItem.getParent().pack(true);
									}
								});
							}
						}
						return false;
					}
				});
			}
			catch (CoreException e)
			{
				ExplorerPlugin.logError(e);
			}
		}
	}

	public void search(String text, boolean isCaseSensitive, boolean isRegularExpression)
	{
		if (selectedProject == null)
		{
			return;
		}

		IResource searchResource = selectedProject;
		TextSearchPageInput input = new TextSearchPageInput(text, isCaseSensitive, isRegularExpression,
				FileTextSearchScope.newSearchScope(new IResource[] { searchResource }, new String[] { "*" }, false)); //$NON-NLS-1$
		try
		{
			NewSearchUI.runQueryInBackground(TextSearchQueryProvider.getPreferred().createQuery(input));
		}
		catch (CoreException e)
		{
			ExplorerPlugin.logError(e);
		}
	}

	/**
	 * Here we dynamically remove a large number of the right-click context menu's items for the App Explorer.
	 * 
	 * @param menu
	 */
	protected void mangleContextMenu(Menu menu)
	{
		// TODO If the selected project isn't accessible, remove new
		// file/folder, debug as
		if (selectedProject != null && selectedProject.isAccessible())
		{
			forceOurNewFileWizard(menu);
		}

		// Remove a whole bunch of the contributed items that we don't want
		removeMenuItems(menu, TO_REMOVE);
		// Check for two separators in a row, remove one if you see that...
		boolean lastWasSeparator = false;
		for (MenuItem menuItem : menu.getItems())
		{
			Object data = menuItem.getData();
			if (data instanceof Separator)
			{
				if (lastWasSeparator)
					menuItem.dispose();
				else
					lastWasSeparator = true;
			}
			else
			{
				lastWasSeparator = false;
			}
		}
	}

	private void forceOurNewFileWizard(Menu menu)
	{
		// Hack the New > File entry
		for (MenuItem menuItem : menu.getItems())
		{
			Object data = menuItem.getData();
			if (data instanceof IContributionItem)
			{
				IContributionItem contrib = (IContributionItem) data;
				if ("common.new.menu".equals(contrib.getId())) //$NON-NLS-1$
				{
					MenuManager manager = (MenuManager) contrib;
					// force an entry for our special template New File wizard!
					IWizardRegistry registry = PlatformUI.getWorkbench().getNewWizardRegistry();
					IWizardDescriptor desc = registry.findWizard("com.aptana.ui.wizards.new.file"); //$NON-NLS-1$
					manager.insertAfter("new", new WizardShortcutAction(PlatformUI.getWorkbench() //$NON-NLS-1$
							.getActiveWorkbenchWindow(), desc));
					manager.remove("new"); //$NON-NLS-1$
					break;
				}
			}
		}
	}

	protected void removeMenuItems(Menu menu, Set<String> idsToRemove)
	{
		if (idsToRemove == null || idsToRemove.isEmpty())
		{
			return;
		}
		for (MenuItem menuItem : menu.getItems())
		{
			Object data = menuItem.getData();
			if (data instanceof IContributionItem)
			{
				IContributionItem contrib = (IContributionItem) data;
				if (idsToRemove.contains(contrib.getId()))
				{
					menuItem.dispose();
				}
			}
		}
	}

	private static class TextSearchPageInput extends TextSearchInput
	{

		private final String fSearchText;
		private final boolean fIsCaseSensitive;
		private final boolean fIsRegEx;
		private final FileTextSearchScope fScope;

		public TextSearchPageInput(String searchText, boolean isCaseSensitive, boolean isRegEx,
				FileTextSearchScope scope)
		{
			fSearchText = searchText;
			fIsCaseSensitive = isCaseSensitive;
			fIsRegEx = isRegEx;
			fScope = scope;
		}

		public String getSearchText()
		{
			return fSearchText;
		}

		public boolean isCaseSensitiveSearch()
		{
			return fIsCaseSensitive;
		}

		public boolean isRegExSearch()
		{
			return fIsRegEx;
		}

		public FileTextSearchScope getScope()
		{
			return fScope;
		}
	}

	public IProject getActiveProject()
	{
		return selectedProject;
	}

	protected void openImportWizard()
	{
		ImportExportWizard wizard = new ImportExportWizard(ImportExportWizard.IMPORT);
		wizard.init(PlatformUI.getWorkbench(), StructuredSelection.EMPTY);

		IDialogSettings workbenchSettings = WorkbenchPlugin.getDefault().getDialogSettings();
		IDialogSettings wizardSettings = workbenchSettings.getSection("ImportExportAction"); //$NON-NLS-1$
		if (wizardSettings == null)
		{
			wizardSettings = workbenchSettings.addNewSection("ImportExportAction"); //$NON-NLS-1$
		}
		wizard.setDialogSettings(wizardSettings);
		wizard.setForcePreviousAndNextButtons(true);

		WizardDialog dialog = new WizardDialog(UIUtils.getActiveShell(), wizard);
		dialog.open();
	}
}
