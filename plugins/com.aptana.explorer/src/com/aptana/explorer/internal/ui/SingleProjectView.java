package com.aptana.explorer.internal.ui;

import java.util.ArrayList;
import java.util.List;

import net.contentobjects.jnotify.IJNotify;
import net.contentobjects.jnotify.JNotifyException;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences.IPreferenceChangeListener;
import org.eclipse.core.runtime.preferences.IEclipsePreferences.PreferenceChangeEvent;
import org.eclipse.jface.action.ContributionItem;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.jface.viewers.ViewerColumn;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.search.ui.NewSearchUI;
import org.eclipse.search.ui.text.FileTextSearchScope;
import org.eclipse.search.ui.text.TextSearchQueryProvider;
import org.eclipse.search.ui.text.TextSearchQueryProvider.TextSearchInput;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontMetrics;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.menus.CommandContributionItem;
import org.eclipse.ui.menus.CommandContributionItemParameter;
import org.eclipse.ui.navigator.CommonNavigator;
import org.eclipse.ui.navigator.CommonViewer;
import org.eclipse.ui.progress.UIJob;
import org.eclipse.ui.swt.IFocusService;
import org.osgi.service.prefs.BackingStoreException;

import com.aptana.editor.common.CommonEditorPlugin;
import com.aptana.editor.common.theme.IThemeManager;
import com.aptana.explorer.ExplorerPlugin;
import com.aptana.explorer.IPreferenceConstants;
import com.aptana.filewatcher.FileWatcher;
import com.aptana.terminal.views.TerminalView;

/**
 * Customized CommonNavigator that adds a project combo and focuses the view on a single project.
 * 
 * @author cwilliams
 */
public abstract class SingleProjectView extends CommonNavigator
{

	public static final String ID = "com.aptana.explorer.view"; //$NON-NLS-1$

	private ToolItem projectToolItem;

	protected IProject selectedProject;
	private ResourceListener fResourceListener;
	private ViewerFilter activeProjectFilter;

	/**
	 * The text to initially show in the filter text control.
	 */
	protected String initialText = Messages.GitProjectView_InitialFileFilterText;
	private Text searchText;
	protected boolean caseSensitiveSearch;
	protected boolean regularExpressionSearch;

	private Integer watcher;

	private IPreferenceChangeListener fThemeChangeListener;

	private IPreferenceChangeListener fActiveProjectPrefChangeListener;

	private IPropertyChangeListener fontListener;

	private Menu projectsMenu;

	private CLabel filterLabel;
	private GridData filterLayoutData;

	private static final String CASE_SENSITIVE_ICON_PATH = "icons/full/elcl16/casesensitive.png"; //$NON-NLS-1$
	private static final String REGULAR_EXPRESSION_ICON_PATH = "icons/full/elcl16/regularexpression.png"; //$NON-NLS-1$

	@Override
	public void createPartControl(Composite parent)
	{
		GridLayout gridLayout = (GridLayout) parent.getLayout();
		gridLayout.marginHeight = 0;
		gridLayout.marginTop = 0;
		gridLayout.marginBottom = 0;
		gridLayout.verticalSpacing = 1;

		// Create toolbar
		Composite toolbarComposite = new Composite(parent, SWT.NONE);
		GridData toolbarGridData = new GridData(SWT.FILL, SWT.CENTER, true, false);
		toolbarComposite.setLayoutData(toolbarGridData);

		GridLayout toolbarGridLayout = new GridLayout(2, false);
		toolbarGridLayout.marginWidth = 2;
		toolbarGridLayout.marginHeight = 0;
		toolbarGridLayout.horizontalSpacing = 0;
		toolbarComposite.setLayout(toolbarGridLayout);

		// Projects combo
		createProjectCombo(toolbarComposite);

		// Let sub classes add to the toolbar
		doCreateToolbar(toolbarComposite);

		// Now create Commands menu
		final ToolBar commandsToolBar = new ToolBar(toolbarComposite, SWT.FLAT);
		ToolItem commandsToolItem = new ToolItem(commandsToolBar, SWT.DROP_DOWN);
		commandsToolItem.setImage(ExplorerPlugin.getImage("icons/full/elcl16/command.png")); //$NON-NLS-1$
		GridData branchComboData = new GridData(SWT.END, SWT.CENTER, true, false);
		commandsToolBar.setLayoutData(branchComboData);

		final MenuManager commandsMenuManager = new MenuManager();
		final Menu commandsMenu = commandsMenuManager.createContextMenu(commandsToolBar);
		commandsToolItem.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent selectionEvent)
			{
				Point toolbarLocation = commandsToolBar.getLocation();
				toolbarLocation = commandsToolBar.getParent().toDisplay(toolbarLocation.x, toolbarLocation.y);
				Point toolbarSize = commandsToolBar.getSize();
				commandsMenu.setLocation(toolbarLocation.x, toolbarLocation.y + toolbarSize.y + 2);
				commandsMenu.setVisible(true);
			}
		});

		// Run script/server
		CommandContributionItemParameter runScriptServer = new CommandContributionItemParameter(getSite(),
				Messages.SingleProjectView_RunMenuTitle, "org.radrails.rails.ui.command.server", //$NON-NLS-1$
				SWT.PUSH);
		commandsMenuManager.add(new CommandContributionItem(runScriptServer)
		{
			@Override
			public boolean isEnabled()
			{
				return super.isEnabled() && selectedProject != null && selectedProject.exists();
			}
		});

		// Run Last launched
		CommandContributionItemParameter runLastCCIP = new CommandContributionItemParameter(getSite(), "RunLast", //$NON-NLS-1$
				"org.eclipse.debug.ui.commands.RunLast", //$NON-NLS-1$
				SWT.PUSH);
		commandsMenuManager.add(new CommandContributionItem(runLastCCIP)
		{
			@Override
			public boolean isEnabled()
			{
				return super.isEnabled() && selectedProject != null && selectedProject.exists();
			}
		});

		// Debug last launched
		CommandContributionItemParameter debugLastCCIP = new CommandContributionItemParameter(getSite(), "DebugLast", //$NON-NLS-1$
				"org.eclipse.debug.ui.commands.DebugLast", //$NON-NLS-1$
				SWT.PUSH);
		commandsMenuManager.add(new CommandContributionItem(debugLastCCIP)
		{
			@Override
			public boolean isEnabled()
			{
				return super.isEnabled() && selectedProject != null && selectedProject.exists();
			}
		});

		new MenuItem(commandsMenu, SWT.SEPARATOR);

		fillCommandsMenu(commandsMenuManager);

		// Create search
		createSearchComposite(parent);

		createNavigator(parent);

		createFilterComposite(parent);

		addProjectResourceListener();
		detectSelectedProject();
		addSingleProjectFilter();
		listenToActiveProjectPrefChanges();

		hookToThemes();
	}

	protected abstract void doCreateToolbar(Composite toolbarComposite);

	protected void fillCommandsMenu(MenuManager menuManager)
	{
		menuManager.add(new ContributionItem()
		{
			@Override
			public void fill(Menu menu, int index)
			{
				new MenuItem(menu, SWT.SEPARATOR);
				final MenuItem terminalMenuItem = new MenuItem(menu, SWT.PUSH);
				terminalMenuItem.setText(Messages.SingleProjectView_OpenTerminalMenuItem_LBL);
				terminalMenuItem.addSelectionListener(new SelectionAdapter()
				{
					public void widgetSelected(SelectionEvent e)
					{
						// Open a terminal on active project!
						TerminalView.open(selectedProject.getName(), selectedProject.getName(), selectedProject
								.getLocation().toOSString());
					}
				});
				terminalMenuItem.setEnabled(selectedProject != null && selectedProject.exists());

				IProject[] projects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
				if (projects.length > 0)
				{
					new MenuItem(menu, SWT.SEPARATOR);

					MenuItem projectsMenuItem = new MenuItem(menu, SWT.CASCADE);
					projectsMenuItem.setText(Messages.SingleProjectView_SwitchToApplication); // TODO

					Menu projectsMenu = new Menu(menu);
					for (IProject iProject : projects)
					{
						// Construct the menu to attach to the above button.
						final MenuItem projectNameMenuItem = new MenuItem(projectsMenu, SWT.RADIO);
						projectNameMenuItem.setText(iProject.getName());
						projectNameMenuItem.setSelection(selectedProject != null
								&& iProject.getName().equals(selectedProject.getName()));
						projectNameMenuItem.addSelectionListener(new SelectionAdapter()
						{
							public void widgetSelected(SelectionEvent e)
							{
								String projectName = projectNameMenuItem.getText();
								projectToolItem.setText(projectName);
								setActiveProject(projectName);
							}
						});
					}
					projectsMenuItem.setMenu(projectsMenu);
				}
			}

			@Override
			public boolean isDynamic()
			{
				return true;
			}
		});
	}

	private IProject[] createProjectCombo(Composite parent)
	{
		final ToolBar projectsToolbar = new ToolBar(parent, SWT.FLAT);
		projectToolItem = new ToolItem(projectsToolbar, SWT.DROP_DOWN);
		GridData projectsToolbarGridData = new GridData(SWT.BEGINNING, SWT.CENTER, false, false);
		projectsToolbar.setLayoutData(projectsToolbarGridData);
		IProject[] projects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
		projectsMenu = new Menu(projectsToolbar);
		for (IProject iProject : projects)
		{
			// Construct the menu to attach to the above button.
			final MenuItem projectNameMenuItem = new MenuItem(projectsMenu, SWT.RADIO);
			projectNameMenuItem.setText(iProject.getName());
			projectNameMenuItem.setSelection(false);
			projectNameMenuItem.addSelectionListener(new SelectionAdapter()
			{
				public void widgetSelected(SelectionEvent e)
				{
					String projectName = projectNameMenuItem.getText();
					projectToolItem.setText(projectName);
					setActiveProject(projectName);
				}
			});
		}

		projectToolItem.addSelectionListener(new SelectionAdapter()
		{
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

	private Composite createSearchComposite(Composite myComposite)
	{
		Composite search = new Composite(myComposite, SWT.NONE);
		GridLayout searchGridLayout = new GridLayout(2, false);
		searchGridLayout.marginWidth = 2;
		searchGridLayout.marginHeight = 0;
		search.setLayout(searchGridLayout);

		GridData searchGridData = new GridData(SWT.FILL, SWT.CENTER, true, false);
		search.setLayoutData(searchGridData);

		searchText = new Text(search, SWT.SINGLE | SWT.BORDER | SWT.SEARCH | SWT.ICON_CANCEL | SWT.ICON_SEARCH);
		searchText.setText(initialText);
		searchText.setToolTipText(Messages.SingleProjectView_Wildcard);
		searchText.setForeground(searchText.getDisplay().getSystemColor(SWT.COLOR_TITLE_INACTIVE_FOREGROUND));

		// Register with focus service so that Cut/Copy/Paste/SelecAll handlers will work.
		IFocusService focusService = (IFocusService) getViewSite().getService(IFocusService.class);
		focusService.addFocusTracker(searchText, ID + ".searchText"); //$NON-NLS-1$

		searchText.addFocusListener(new FocusListener()
		{
			@Override
			public void focusLost(FocusEvent e)
			{
				if (searchText.getText().length() == 0)
				{
					searchText.setText(initialText);
				}
				searchText.setForeground(searchText.getDisplay().getSystemColor(SWT.COLOR_TITLE_INACTIVE_FOREGROUND));
			}

			@Override
			public void focusGained(FocusEvent e)
			{
				if (searchText.getText().equals(initialText))
				{
					searchText.setText(""); //$NON-NLS-1$
				}
				searchText.setForeground(null);
			}
		});

		searchText.addKeyListener(new KeyListener()
		{
			@Override
			public void keyReleased(KeyEvent e)
			{
			}

			@Override
			public void keyPressed(KeyEvent e)
			{
				if (!e.doit)
				{
					return;
				}

				if (e.keyCode == 0x0D)
				{
					searchText();
					e.doit = false;
				}
			}
		});

		GridData gridData = new GridData(SWT.FILL, SWT.CENTER, true, false);
		// // if the text widget supported cancel then it will have it's own
		// // integrated button. We can take all of the space.
		// if ((searchText.getStyle() & SWT.ICON_CANCEL) != 0)
		// gridData.horizontalSpan = 2;
		searchText.setLayoutData(gridData);

		// Button for search options
		final ToolBar toolbar = new ToolBar(search, SWT.NONE);
		GridData toolbarGridData = new GridData(SWT.FILL, SWT.CENTER, false, false);
		toolbar.setLayoutData(toolbarGridData);

		final ToolItem caseSensitiveMenuItem = new ToolItem(toolbar, SWT.CHECK);
		caseSensitiveMenuItem.setImage(ExplorerPlugin.getImage(CASE_SENSITIVE_ICON_PATH));
		caseSensitiveMenuItem.setToolTipText(Messages.SingleProjectView_CaseSensitive);
		caseSensitiveMenuItem.setSelection(caseSensitiveSearch);
		caseSensitiveMenuItem.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e)
			{
				setCaseSensitiveSearch(caseSensitiveMenuItem.getSelection());
				searchText.setFocus();
			}
		});

		final ToolItem regularExressionMenuItem = new ToolItem(toolbar, SWT.CHECK);
		regularExressionMenuItem.setImage(ExplorerPlugin.getImage(REGULAR_EXPRESSION_ICON_PATH));
		regularExressionMenuItem.setToolTipText(Messages.SingleProjectView_RegularExpression);
		regularExressionMenuItem.setSelection(regularExpressionSearch);
		regularExressionMenuItem.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e)
			{
				setRegularExpressionSearch(regularExressionMenuItem.getSelection());
				searchText.setFocus();
			}
		});

		return search;
	}

	protected void createNavigator(Composite myComposite)
	{
		Composite viewer = new Composite(myComposite, SWT.BORDER);
		FillLayout fillLayout = new FillLayout();
		fillLayout.marginWidth = 0;
		fillLayout.marginHeight = 0;
		viewer.setLayout(fillLayout);

		GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
		viewer.setLayoutData(gridData);

		super.createPartControl(viewer);
	}

	@Override
	protected CommonViewer createCommonViewer(Composite aParent)
	{
		// Always expand to project's contents initially
		CommonViewer viewer = super.createCommonViewer(aParent);
		viewer.setAutoExpandLevel(2);
		return viewer;
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

		GridData gridData = new GridData(SWT.FILL, SWT.CENTER, true, false);
		filterLabel.setLayoutData(gridData);

		ToolBar toolBar = new ToolBar(filter, SWT.FLAT);
		toolBar.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));

		ToolItem toolItem = new ToolItem(toolBar, SWT.PUSH);
		toolItem.setImage(ExplorerPlugin.getImage("icons/full/elcl16/close.png")); //$NON-NLS-1$
		toolItem.addSelectionListener(new SelectionListener()
		{

			@Override
			public void widgetSelected(SelectionEvent e)
			{
				removeFilter();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e)
			{
			}
		});

		return filter;
	}

	protected void hideFilterLable()
	{
		filterLayoutData.exclude = true;
		filterLabel.setVisible(false);
		filterLabel.getParent().getParent().layout();
	}

	protected void showFilterLabel(Image image, String text)
	{
		filterLabel.setImage(image);
		filterLabel.setText(text);
		filterLayoutData.exclude = false;
		filterLabel.setVisible(true);
		filterLabel.getParent().getParent().layout();
	}

	protected void removeFilter()
	{
		hideFilterLable();
	}

	private void addSingleProjectFilter()
	{
		activeProjectFilter = new ViewerFilter()
		{

			@Override
			public boolean select(Viewer viewer, Object parentElement, Object element)
			{
				if (selectedProject == null)
					return false;
				IResource resource = null;
				if (element instanceof IResource)
				{
					resource = (IResource) element;
				}
				if (resource == null)
				{
					if (element instanceof IAdaptable)
					{
						IAdaptable adapt = (IAdaptable) element;
						resource = (IResource) adapt.getAdapter(IResource.class);
					}
				}

				if (resource == null)
					return false;

				IProject project = resource.getProject();
				return selectedProject.equals(project);
			}
		};
		getCommonViewer().addFilter(activeProjectFilter);
		// When user manually edits filters, they get blown away and then re-added. We need to listen to this indirectly
		// and re-add our filter!
		getCommonViewer().addSelectionChangedListener(new ISelectionChangedListener()
		{

			public void selectionChanged(SelectionChangedEvent event)
			{
				ISelection selection = event.getSelection();
				if (selection == null || !selection.isEmpty())
					return;
				// check to see if our filter got wiped out!
				ViewerFilter[] filters = getCommonViewer().getFilters();
				for (ViewerFilter viewerFilter : filters)
				{
					if (viewerFilter.equals(activeProjectFilter))
						return;
				}
				getCommonViewer().addFilter(activeProjectFilter);
			}
		});
	}

	private void addProjectResourceListener()
	{
		fResourceListener = new ResourceListener();
		ResourcesPlugin.getWorkspace().addResourceChangeListener(fResourceListener, IResourceChangeEvent.POST_CHANGE);
	}

	private void listenToActiveProjectPrefChanges()
	{
		fActiveProjectPrefChangeListener = new IPreferenceChangeListener()
		{

			public void preferenceChange(PreferenceChangeEvent event)
			{
				if (!event.getKey().equals(IPreferenceConstants.ACTIVE_PROJECT))
					return;
				IProject oldActiveProject = selectedProject;
				Object obj = event.getNewValue();
				if (obj == null)
					return;
				String newProjectName = (String) obj;
				if (oldActiveProject != null && newProjectName.equals(oldActiveProject.getName()))
					return;
				IProject newSelectedProject = ResourcesPlugin.getWorkspace().getRoot().getProject(newProjectName);
				selectedProject = newSelectedProject;
				projectChanged(oldActiveProject, newSelectedProject);
			}
		};
		new InstanceScope().getNode(ExplorerPlugin.PLUGIN_ID).addPreferenceChangeListener(
				fActiveProjectPrefChangeListener);
	}

	/**
	 * Hooks up to the active theme.
	 */
	private void hookToThemes()
	{
		themeChanged();
		overrideTreeDrawing();
		overrideLabelProvider();
		listenForThemeChanges();
	}

	private void overrideTreeDrawing()
	{
		final Tree tree = getCommonViewer().getTree();
		// Override selection color to match what is set in theme
		tree.addListener(SWT.EraseItem, new Listener()
		{
			public void handleEvent(Event event)
			{
				if ((event.detail & SWT.SELECTED) != 0)
				{
					Tree tree = (Tree) event.widget;
					int clientWidth = tree.getClientArea().width;

					GC gc = event.gc;
					Color oldBackground = gc.getBackground();

					gc.setBackground(CommonEditorPlugin.getDefault().getColorManager().getColor(
							getThemeManager().getCurrentTheme().getSelection()));
					gc.fillRectangle(0, event.y, clientWidth, event.height);
					gc.setBackground(oldBackground);

					event.detail &= ~SWT.SELECTED;
					event.detail &= ~SWT.BACKGROUND;
				}
			}
		});

		// Hack to force a specific row height and width based on font
		tree.addListener(SWT.MeasureItem, new Listener()
		{
			public void handleEvent(Event event)
			{
				Font font = JFaceResources.getFont(IThemeManager.VIEW_FONT_NAME);
				if (font == null)
				{
					font = JFaceResources.getTextFont();
				}
				if (font != null)
				{
					event.gc.setFont(font);
					FontMetrics metrics = event.gc.getFontMetrics();
					int height = metrics.getHeight() + 2;
					TreeItem item = (TreeItem) event.item;
					int width = event.gc.stringExtent(item.getText()).x + 24;
					event.height = height;
					if (width > event.width)
						event.width = width;
				}
			}
		});

		fontListener = new IPropertyChangeListener()
		{

			@Override
			public void propertyChange(PropertyChangeEvent event)
			{
				if (!event.getProperty().equals(IThemeManager.VIEW_FONT_NAME))
					return;
				Display.getCurrent().asyncExec(new Runnable()
				{

					@Override
					public void run()
					{
						// OK, the app explorer font changed. We need to force a refresh of the app explorer tree!
						updateViewer(selectedProject); // no structural change
						tree.redraw();
						tree.update();
					}
				});

			}
		};
		JFaceResources.getFontRegistry().addListener(fontListener);
	}

	private void overrideLabelProvider()
	{
		ViewerColumn viewer = (ViewerColumn) getCommonViewer().getTree().getData("org.eclipse.jface.columnViewer"); //$NON-NLS-1$
		ColumnViewer colViewer = viewer.getViewer();
		final CellLabelProvider provider = (CellLabelProvider) colViewer.getLabelProvider();
		viewer.setLabelProvider(new CellLabelProvider()
		{

			@Override
			public void update(ViewerCell cell)
			{
				provider.update(cell);
				Font font = JFaceResources.getFont(IThemeManager.VIEW_FONT_NAME);
				if (font == null)
				{
					font = JFaceResources.getTextFont();
				}
				if (font != null)
				{
					cell.setFont(font);
				}

				cell.setForeground(CommonEditorPlugin.getDefault().getColorManager().getColor(
						getThemeManager().getCurrentTheme().getForeground()));
			}
		});
	}

	protected IThemeManager getThemeManager()
	{
		return CommonEditorPlugin.getDefault().getThemeManager();
	}

	private void listenForThemeChanges()
	{
		fThemeChangeListener = new IPreferenceChangeListener()
		{

			@Override
			public void preferenceChange(PreferenceChangeEvent event)
			{
				if (event.getKey().equals(IThemeManager.THEME_CHANGED))
				{
					themeChanged();
				}
			}
		};
		new InstanceScope().getNode(CommonEditorPlugin.PLUGIN_ID).addPreferenceChangeListener(fThemeChangeListener);
	}

	private void detectSelectedProject()
	{
		String value = Platform.getPreferencesService().getString(ExplorerPlugin.PLUGIN_ID,
				IPreferenceConstants.ACTIVE_PROJECT, null, null);
		IProject project = null;
		if (value != null)
		{
			project = ResourcesPlugin.getWorkspace().getRoot().getProject(value);
		}
		if (project == null)
		{
			IProject[] projects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
			if (projects == null || projects.length == 0)
				return;
			project = projects[0];
		}
		if (project != null)
		{
			setActiveProject(project.getName());
		}
	}

	protected void setActiveProject(String projectName)
	{
		IProject newSelectedProject = null;
		if (projectName != null && projectName.trim().length() > 0)
			newSelectedProject = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
		if (selectedProject != null && newSelectedProject != null && selectedProject.equals(newSelectedProject))
			return;

		if (selectedProject != null)
		{
			unsetActiveProject();
		}
		IProject oldActiveProject = selectedProject;
		selectedProject = newSelectedProject;
		if (newSelectedProject != null)
		{
			setActiveProject();
		}
		projectChanged(oldActiveProject, newSelectedProject);
	}

	private void setActiveProject()
	{
		try
		{
			IEclipsePreferences prefs = new InstanceScope().getNode(ExplorerPlugin.PLUGIN_ID);
			prefs.put(IPreferenceConstants.ACTIVE_PROJECT, selectedProject.getName());
			prefs.flush();
		}
		catch (BackingStoreException e)
		{
			ExplorerPlugin.logError(e.getMessage(), e);
		}
	}

	private void unsetActiveProject()
	{
		try
		{
			IEclipsePreferences prefs = new InstanceScope().getNode(ExplorerPlugin.PLUGIN_ID);
			prefs.remove(IPreferenceConstants.ACTIVE_PROJECT);
			prefs.flush();
		}
		catch (BackingStoreException e)
		{
			ExplorerPlugin.logError(e.getMessage(), e);
		}
	}

	/**
	 * @param oldProject
	 * @param newProject
	 */
	protected void projectChanged(IProject oldProject, IProject newProject)
	{
		// Set/unset file watcher
		try
		{
			if (watcher != null)
			{
				FileWatcher.removeWatch(watcher);
			}
			if (newProject != null && newProject.exists() && newProject.getLocation() != null)
			{
				watcher = FileWatcher.addWatch(newProject.getLocation().toOSString(), IJNotify.FILE_ANY, true,
						new FileDeltaRefreshAdapter());
			}
		}
		catch (JNotifyException e)
		{
			ExplorerPlugin.logError(e.getMessage(), e);
		}
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
		// Update the tree since filter changed
		updateViewer(oldProject, newProject); // no structural change, just filter changed
		// Expand the new project one level. Have to schedule after short delay or it seems the tree's model is not yet
		// ready...
		UIJob job = new UIJob("Expand selected project")
		{

			@Override
			public IStatus runInUIThread(IProgressMonitor monitor)
			{
				if (selectedProject != null && selectedProject.exists() && getCommonViewer() != null)
				{
					// Expand selected project
					getCommonViewer().setExpandedState(selectedProject, true);
				}
				return Status.OK_STATUS;
			}
		};
		job.setPriority(Job.INTERACTIVE);
		job.schedule(250);
	}

	protected void refreshViewer()
	{
		if (getCommonViewer() == null)
			return;
		getCommonViewer().refresh(selectedProject, true);
	}

	protected void updateViewer(Object... elements)
	{
		if (getCommonViewer() == null)
			return;
		List<Object> nonNulls = new ArrayList<Object>();
		for (Object element : elements)
		{
			if (element == null)
				continue;
			nonNulls.add(element);
		}
		getCommonViewer().update(nonNulls.toArray(), null);
	}

	@Override
	public void dispose()
	{
		removeFontListener();
		removeProjectResourceListener();
		removeActiveProjectPrefListener();
		removeSingleProjectFilter();
		removeThemeListener();
		super.dispose();
	}

	private void removeFontListener()
	{
		if (fontListener != null)
		{
			JFaceResources.getFontRegistry().removeListener(fontListener);
			fontListener = null;
		}
	}

	private void removeProjectResourceListener()
	{
		ResourcesPlugin.getWorkspace().removeResourceChangeListener(fResourceListener);
		fResourceListener = null;
	}

	private void removeActiveProjectPrefListener()
	{
		if (fActiveProjectPrefChangeListener != null)
		{
			new InstanceScope().getNode(ExplorerPlugin.PLUGIN_ID).removePreferenceChangeListener(
					fActiveProjectPrefChangeListener);
		}
		fActiveProjectPrefChangeListener = null;
	}

	private void removeSingleProjectFilter()
	{
		if (getCommonViewer() != null && activeProjectFilter != null)
		{
			getCommonViewer().removeFilter(activeProjectFilter);
		}
		activeProjectFilter = null;
	}

	private void removeThemeListener()
	{
		if (fThemeChangeListener != null)
		{
			new InstanceScope().getNode(CommonEditorPlugin.PLUGIN_ID).removePreferenceChangeListener(
					fThemeChangeListener);
			fThemeChangeListener = null;
		}
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
				return;
			try
			{
				delta.accept(new IResourceDeltaVisitor()
				{

					public boolean visit(IResourceDelta delta) throws CoreException
					{
						IResource resource = delta.getResource();
						if (resource.getType() == IResource.FILE || resource.getType() == IResource.FOLDER)
							return false;
						if (resource.getType() == IResource.ROOT)
							return true;
						if (resource.getType() == IResource.PROJECT)
						{
							// a project was added, removed, or changed!
							if (delta.getKind() == IResourceDelta.ADDED)
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
											if (items[i].getText().compareTo(projectName) > 0)
											{
												index = i;
												break;
											}
										}
										final MenuItem projectNameMenuItem = new MenuItem(projectsMenu, SWT.RADIO,
												index);
										projectNameMenuItem.setText(projectName);
										projectNameMenuItem.setSelection(true);
										projectNameMenuItem.addSelectionListener(new SelectionAdapter()
										{
											public void widgetSelected(SelectionEvent e)
											{
												String projectName = projectNameMenuItem.getText();
												projectToolItem.setText(projectName);
												setActiveProject(projectName);
											}
										});
										setActiveProject(projectName);
										projectToolItem.getParent().pack(true);
									}
								});
							}
							else if (delta.getKind() == IResourceDelta.REMOVED)
							{
								// Remove from menu and if it was the active project, switch away from it!
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
											String newActiveProject = ""; //$NON-NLS-1$
											if (projects.length > 0)
											{
												newActiveProject = projects[0].getName();
											}
											setActiveProject(newActiveProject);
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

	private boolean isCaseSensitiveSearch()
	{
		return caseSensitiveSearch;
	}

	private void setCaseSensitiveSearch(boolean caseSensitiveSearch)
	{
		this.caseSensitiveSearch = caseSensitiveSearch;
	}

	private boolean isRegularExpressionSearch()
	{
		return regularExpressionSearch;
	}

	private void setRegularExpressionSearch(boolean regularExpressionSearch)
	{
		this.regularExpressionSearch = regularExpressionSearch;
	}

	/**
	 * Search the text in project.
	 */
	protected void searchText()
	{
		if (selectedProject == null)
		{
			return;
		}
		String textToSearch = searchText.getText();
		if (textToSearch.length() == 0)
		{
			return;
		}

		IResource searchResource = selectedProject;
		TextSearchPageInput input = new TextSearchPageInput(textToSearch, isCaseSensitiveSearch(),
				isRegularExpressionSearch(), FileTextSearchScope.newSearchScope(new IResource[] { searchResource },
						new String[] { "*" }, false)); //$NON-NLS-1$
		try
		{
			NewSearchUI.runQueryInBackground(TextSearchQueryProvider.getPreferred().createQuery(input));
		}
		catch (CoreException e)
		{
			ExplorerPlugin.logError(e);
		}
	}

	protected void themeChanged()
	{
		getCommonViewer().getTree().setBackground(
				CommonEditorPlugin.getDefault().getColorManager().getColor(
						getThemeManager().getCurrentTheme().getBackground()));
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

}
