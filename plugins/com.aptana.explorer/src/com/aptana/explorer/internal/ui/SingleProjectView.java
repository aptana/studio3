package com.aptana.explorer.internal.ui;

import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.MessageFormat;
import java.util.HashSet;
import java.util.Set;

import net.contentobjects.jnotify.IJNotify;
import net.contentobjects.jnotify.JNotifyException;

import org.eclipse.core.resources.IFile;
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
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IEclipsePreferences.IPreferenceChangeListener;
import org.eclipse.core.runtime.preferences.IEclipsePreferences.PreferenceChangeEvent;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.action.ContributionItem;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.search.ui.IContextMenuConstants;
import org.eclipse.search.ui.NewSearchUI;
import org.eclipse.search.ui.text.FileTextSearchScope;
import org.eclipse.search.ui.text.TextSearchQueryProvider;
import org.eclipse.search.ui.text.TextSearchQueryProvider.TextSearchInput;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.OpenWindowListener;
import org.eclipse.swt.browser.WindowEvent;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MenuEvent;
import org.eclipse.swt.events.MenuListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
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
import org.eclipse.team.core.RepositoryProvider;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.ISizeProvider;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.DeleteResourceAction;
import org.eclipse.ui.browser.IWorkbenchBrowserSupport;
import org.eclipse.ui.internal.browser.BrowserViewer;
import org.eclipse.ui.internal.browser.WebBrowserEditor;
import org.eclipse.ui.internal.browser.WebBrowserEditorInput;
import org.eclipse.ui.internal.navigator.wizards.WizardShortcutAction;
import org.eclipse.ui.menus.IMenuService;
import org.eclipse.ui.navigator.CommonNavigator;
import org.eclipse.ui.navigator.CommonViewer;
import org.eclipse.ui.progress.UIJob;
import org.eclipse.ui.swt.IFocusService;
import org.eclipse.ui.wizards.IWizardDescriptor;
import org.eclipse.ui.wizards.IWizardRegistry;
import org.osgi.service.prefs.BackingStoreException;

import com.aptana.editor.common.CommonEditorPlugin;
import com.aptana.editor.common.theme.IThemeManager;
import com.aptana.editor.common.theme.TreeThemer;
import com.aptana.explorer.ExplorerPlugin;
import com.aptana.explorer.IExplorerUIConstants;
import com.aptana.explorer.IPreferenceConstants;
import com.aptana.filewatcher.FileWatcher;
import com.aptana.git.core.GitPlugin;
import com.aptana.git.core.model.GitRepository;
import com.aptana.git.core.model.IGitRepositoryManager;
import com.aptana.terminal.views.TerminalView;
import com.aptana.usage.PingStartup;
import com.aptana.util.EclipseUtils;

/**
 * Customized CommonNavigator that adds a project combo and focuses the view on a single project.
 * 
 * @author cwilliams
 */
@SuppressWarnings("restriction")
public abstract class SingleProjectView extends CommonNavigator implements ISizeProvider
{

	private static final String GEAR_MENU_ID = "com.aptana.explorer.gear"; //$NON-NLS-1$

	/**
	 * Forced removal of context menu entries dynamically to match the context menu Andrew wants...
	 */
	private static final Set<String> TO_REMOVE = new HashSet<String>();
	static
	{
		TO_REMOVE.add("org.eclipse.ui.PasteAction"); //$NON-NLS-1$
		TO_REMOVE.add("org.eclipse.ui.CopyAction"); //$NON-NLS-1$
		TO_REMOVE.add("org.eclipse.ui.MoveResourceAction"); //$NON-NLS-1$
		TO_REMOVE.add("import"); //$NON-NLS-1$
		TO_REMOVE.add("export"); //$NON-NLS-1$
		TO_REMOVE.add("org.eclipse.debug.ui.contextualLaunch.run.submenu"); //$NON-NLS-1$
		//		TO_REMOVE.add("org.eclipse.debug.ui.contextualLaunch.debug.submenu"); //$NON-NLS-1$
		TO_REMOVE.add("org.eclipse.debug.ui.contextualLaunch.profile.submenu"); //$NON-NLS-1$
		TO_REMOVE.add("compareWithMenu"); //$NON-NLS-1$
		TO_REMOVE.add("replaceWithMenu"); //$NON-NLS-1$
		TO_REMOVE.add("org.eclipse.ui.framelist.goInto"); //$NON-NLS-1$
		TO_REMOVE.add("addFromHistoryAction"); //$NON-NLS-1$
		TO_REMOVE.add("org.radrails.rails.ui.actions.RunScriptServerAction"); //$NON-NLS-1$
	};

	private ToolItem projectToolItem;
	private Menu projectsMenu;

	protected IProject selectedProject;
	/**
	 * Listens for the addition/removal of projects.
	 */
	private ResourceListener fProjectsListener;

	/**
	 * The text to initially show in the filter text control.
	 */
	protected String initialText = Messages.SingleProjectView_InitialFileFilterText;
	private Text searchText;
	private Composite filterComp;
	private CLabel filterLabel;
	private GridData filterLayoutData;
	protected boolean caseSensitiveSearch;
	protected boolean regularExpressionSearch;

	/**
	 * Used as a handle for the filesystem watcher on the selected project.
	 */
	private Integer watcher;

	// listen for external changes to active project
	private IPreferenceChangeListener fActiveProjectPrefChangeListener;

	protected boolean isWindows = Platform.getOS().equals(Platform.OS_WIN32);
	protected boolean isMacOSX = Platform.getOS().equals(Platform.OS_MACOSX);
	// use the hard-coded constant since it is only defined in Eclipse 3.5
	protected boolean isCocoa = Platform.getWS().equals("cocoa"); //$NON-NLS-1$

	private TreeThemer treeThemer;

	// memento wasn't declared protected until Eclipse 3.5, so store it ourselves
	protected IMemento memento;

	/**
	 * Message area
	 */
	private Browser browser;
	private IPreferenceChangeListener fThemeChangeListener;

	private Composite normal;

	private Composite browserComposite;

	private Job updateMessageAreaJob;
	private static final String BASE_MESSAGE_URL = "http://aptana.com/tools/content/"; //$NON-NLS-1$
	// private static final String BASE_MESSAGE_URL = "http://localhost:3000/tools/content/"; //$NON-NLS-1$
	private static final int MINIMUM_BROWSER_HEIGHT = 150;
	private static final int MINIMUM_BROWSER_WIDTH = 310;
	private static final String BROWSER_ID = "message.area.browser"; //$NON-NLS-1$

	private static final String CASE_SENSITIVE_ICON_PATH = "icons/full/elcl16/casesensitive.png"; //$NON-NLS-1$
	private static final String REGULAR_EXPRESSION_ICON_PATH = "icons/full/elcl16/regularexpression.png"; //$NON-NLS-1$

	protected static final String GROUP_RUN = "group.run"; //$NON-NLS-1$

	@Override
	public void createPartControl(final Composite parent)
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
		branchComboData.minimumWidth = 24;
		commandsToolBar.setLayoutData(branchComboData);

		commandsToolItem.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent selectionEvent)
			{
				Point toolbarLocation = commandsToolBar.getLocation();
				toolbarLocation = commandsToolBar.getParent().toDisplay(toolbarLocation.x, toolbarLocation.y);
				Point toolbarSize = commandsToolBar.getSize();
				final MenuManager commandsMenuManager = new MenuManager(null, GEAR_MENU_ID);
				fillCommandsMenu(commandsMenuManager);
				IMenuService menuService = (IMenuService) getSite().getService(IMenuService.class);
				menuService.populateContributionManager(commandsMenuManager, "toolbar:" + commandsMenuManager.getId()); //$NON-NLS-1$
				final Menu commandsMenu = commandsMenuManager.createContextMenu(commandsToolBar);
				commandsMenu.setLocation(toolbarLocation.x, toolbarLocation.y + toolbarSize.y + 2);
				commandsMenu.setVisible(true);
			}
		});

		// Holds normal contents, then splitter, then message area
		final Composite master = new Composite(parent, SWT.NONE);
		master.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		// Normal contents, wraps the search, filter and navigator areas
		normal = new Composite(master, SWT.NONE);
		normal.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		normal.setLayout(gridLayout);

		createSearchComposite(normal);
		filterComp = createFilterComposite(normal);
		createNavigator(normal);

		browserComposite = createBrowserComposite(master);

		master.setLayout(new FormLayout());

		FormData normalData = new FormData();
		normalData.left = new FormAttachment(0, 0);
		normalData.right = new FormAttachment(100, 0);
		normalData.top = new FormAttachment(0, 0);
		normalData.bottom = new FormAttachment(browserComposite, 0);
		normal.setLayoutData(normalData);

		// Browser message area
		final FormData browserData = new FormData();
		browserData.left = new FormAttachment(0, 0);
		browserData.right = new FormAttachment(100, 0);
		browserData.top = new FormAttachment(100, -MINIMUM_BROWSER_HEIGHT);
		browserData.bottom = new FormAttachment(100, 0);
		browserComposite.setLayoutData(browserData);

		updateMessageArea();

		// Force relayout on resize of view so that splitter gets resized.
		parent.addListener(SWT.Resize, new Listener()
		{

			@Override
			public void handleEvent(Event event)
			{
				browserData.top = new FormAttachment(100, -MINIMUM_BROWSER_HEIGHT);
				browserData.bottom = new FormAttachment(100, 0);
				parent.layout();
			}
		});

		// Remove the navigation actions
		getViewSite().getActionBars().getToolBarManager().remove("org.eclipse.ui.framelist.back"); //$NON-NLS-1$
		getViewSite().getActionBars().getToolBarManager().remove("org.eclipse.ui.framelist.forward"); //$NON-NLS-1$
		getViewSite().getActionBars().getToolBarManager().remove("org.eclipse.ui.framelist.up"); //$NON-NLS-1$

		addProjectResourceListener();
		IProject project = detectSelectedProject();
		if (project != null)
		{
			setActiveProject(project.getName());
		}
		listenToActiveProjectPrefChanges();

		hookToThemes();
	}

	public void init(IViewSite aSite, IMemento aMemento) throws PartInitException
	{
		super.init(aSite, aMemento);
		this.memento = aMemento;
	}

	protected abstract void doCreateToolbar(Composite toolbarComposite);

	protected void fillCommandsMenu(MenuManager menuManager)
	{
		// Filtering
		// Run
		// Git single files
		// Git project level
		// Git branching
		// Git misc
		// Misc project/properties
		menuManager.add(new Separator(IContextMenuConstants.GROUP_FILTERING));
		menuManager.add(new Separator(GROUP_RUN));
		menuManager.add(new Separator(IContextMenuConstants.GROUP_ADDITIONS));
		menuManager.add(new Separator(IContextMenuConstants.GROUP_PROPERTIES));

		// Add run related items
		// Open Terminal
		menuManager.appendToGroup(GROUP_RUN, new ContributionItem()
		{
			@Override
			public void fill(Menu menu, int index)
			{
				final MenuItem terminalMenuItem = new MenuItem(menu, SWT.PUSH);
				terminalMenuItem.setText(Messages.SingleProjectView_OpenTerminalMenuItem_LBL);
				terminalMenuItem.addSelectionListener(new SelectionAdapter()
				{
					public void widgetSelected(SelectionEvent e)
					{
						// Open a terminal on active project!
						TerminalView.openView(selectedProject.getName(), selectedProject.getName(),
								selectedProject.getLocation());
					}
				});
				terminalMenuItem.setEnabled(selectedProject != null && selectedProject.exists());

				IProject[] projects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
				if (projects.length > 0)
				{
					new MenuItem(menu, SWT.SEPARATOR);

					MenuItem projectsMenuItem = new MenuItem(menu, SWT.CASCADE);
					projectsMenuItem.setText(Messages.SingleProjectView_SwitchToApplication);

					Menu projectsMenu = new Menu(menu);
					for (IProject iProject : projects)
					{
						// hide closed projects
						if (!iProject.isAccessible())
						{
							continue;
						}
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

		// Stick Delete in Properties area
		menuManager.appendToGroup(IContextMenuConstants.GROUP_PROPERTIES, new ContributionItem()
		{
			@Override
			public void fill(Menu menu, int index)
			{
				final MenuItem terminalMenuItem = new MenuItem(menu, SWT.PUSH);
				terminalMenuItem.setText(Messages.SingleProjectView_DeleteProjectMenuItem_LBL);
				terminalMenuItem.addSelectionListener(new SelectionAdapter()
				{
					public void widgetSelected(SelectionEvent e)
					{
						DeleteResourceAction action = new DeleteResourceAction(getSite());
						action.selectionChanged(new StructuredSelection(selectedProject));
						action.run();
					}
				});
				boolean enabled = (selectedProject != null && selectedProject.exists());
				ISharedImages images = PlatformUI.getWorkbench().getSharedImages();
				terminalMenuItem.setImage(enabled ? images.getImage(ISharedImages.IMG_TOOL_DELETE) : images
						.getImage(ISharedImages.IMG_TOOL_DELETE_DISABLED));
				terminalMenuItem.setEnabled(enabled);
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
			// hide closed projects
			if (!iProject.isAccessible())
			{
				continue;
			}

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
		focusService.addFocusTracker(searchText, IExplorerUIConstants.VIEW_ID + ".searchText"); //$NON-NLS-1$

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
		getCommonViewer().setInput(detectSelectedProject());
		fixNavigatorManager();
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
					return new StructuredSelection(selectedProject);
				return sel;
			}
		};
	}

	protected void fixNavigatorManager()
	{
		// HACK! This is to fix behavior that Eclipse bakes into CommonNavigatorManager.UpdateActionBarsJob where it
		// forces the selection context for actions tied to the view to the view's input *even if it already has a
		// perfectly fine and valid selection!* This forces the selection again in a delayed job which hopefully runs
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
		job.setSystem(true);
		job.setPriority(Job.BUILD);
		job.schedule(250);

		getCommonViewer().getTree().getMenu().addMenuListener(new MenuListener()
		{

			@Override
			public void menuShown(MenuEvent e)
			{
				Menu menu = (Menu) e.getSource();
				mangleContextMenu(menu);
			}

			@Override
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

	private Composite createBrowserComposite(final Composite myComposite)
	{
		Composite browserParent = new Composite(myComposite, SWT.NONE);
		FillLayout layout = new FillLayout();
		layout.marginWidth = 1;
		browserParent.setLayout(layout);

		GridData layoutData = new GridData(SWT.FILL, SWT.FILL, true, false);
		layoutData.minimumHeight = MINIMUM_BROWSER_HEIGHT;
		layoutData.minimumWidth = MINIMUM_BROWSER_WIDTH;
		browserParent.setLayoutData(layoutData);

		browser = new Browser(browserParent, SWT.NONE);
		browser.setText("<html><head></head><body style=\"background-color: #"
				+ toHex(getThemeManager().getCurrentTheme().getBackground()) + "; color: #"
				+ toHex(getThemeManager().getCurrentTheme().getForeground()) + ";\"><h3>Loading...</h3></body></html>");
		// Open links with target of _new in an internal browser editor
		browser.addOpenWindowListener(new OpenWindowListener()
		{

			@Override
			public void open(WindowEvent event)
			{
				try
				{
					int style = IWorkbenchBrowserSupport.NAVIGATION_BAR | IWorkbenchBrowserSupport.LOCATION_BAR
							| IWorkbenchBrowserSupport.STATUS;
					WebBrowserEditorInput input = new WebBrowserEditorInput(null, style, BROWSER_ID);
					IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
					IEditorPart editorPart = page.openEditor(input, WebBrowserEditor.WEB_BROWSER_EDITOR_ID);
					WebBrowserEditor webBrowserEditor = (WebBrowserEditor) editorPart;
					Field f = WebBrowserEditor.class.getDeclaredField("webBrowser"); //$NON-NLS-1$
					f.setAccessible(true);
					BrowserViewer viewer = (BrowserViewer) f.get(webBrowserEditor);
					event.browser = viewer.getBrowser();
				}
				catch (Exception e)
				{
					ExplorerPlugin.logError(e.getMessage(), e);
				}
			}
		});
		return browserParent;
	}

	@SuppressWarnings("nls")
	private String getURLForProject()
	{
		StringBuilder builder = new StringBuilder(BASE_MESSAGE_URL);
		builder.append("?v=");
		builder.append(getVersion());

		builder.append("&bg=");
		builder.append(toHex(getThemeManager().getCurrentTheme().getBackground()));
		builder.append("&fg=");
		builder.append(toHex(getThemeManager().getCurrentTheme().getForeground()));

		// "chrome"
		builder.append("&ch=");// FIXME Grab one of the actual parent widgets and grab it's bg?
		Color color = PlatformUI.getWorkbench().getDisplay().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND);
		builder.append(toHex(color.getRGB()));

		// project type
		builder.append("&p=");
		builder.append(getProjectType());

		// version control
		builder.append("&vc=");
		builder.append(getVersionControl());

		// github
		builder.append("&gh=");
		builder.append(hasGithubRemote() ? '1' : '0');

		// timestamp to force updates to server (bypass browser cache)
		builder.append("&ts=");
		builder.append(System.currentTimeMillis());

		// guid that relates to a single install of the IDE
		builder.append("&id=");
		builder.append(getGUID());

		// deploy info
		builder.append(getDeployParam());

		// for debugging output
		// builder.append("&debug=1");

		return builder.toString();
	}

	/**
	 * additional parameter &dep=VALUE where VALUE is one of (in decreasing order of precedence):
	 * <ul>
	 * <li>ch (deploy/default.rb at project root)</li>
	 * <li>cs (chef solo - deploy/solo.rb at project root)</li>
	 * <li>cap (Capfile or capfile at root)</li>
	 * </ul>
	 * 
	 * @return
	 */
	@SuppressWarnings("nls")
	private String getDeployParam()
	{
		if (selectedProject != null && selectedProject.exists())
		{
			IFile file = selectedProject.getFile("deploy/default.rb");
			if (file.exists())
				return "&dep=ch";
			file = selectedProject.getFile("deploy/solo.rb");
			if (file.exists())
				return "&dep=cs";
			file = selectedProject.getFile("Capfile");
			if (file.exists())
				return "&dep=cap";
			file = selectedProject.getFile("capfile");
			if (file.exists())
				return "&dep=cap";
		}
		return "";
	}

	private String getGUID()
	{
		return PingStartup.getApplicationId();
	}

	private boolean hasGithubRemote()
	{
		if (selectedProject == null)
			return false;
		final GitRepository repo = getGitRepositoryManager().getAttached(selectedProject);
		if (repo == null)
			return false;
		Set<String> remoteURLs = repo.remoteURLs();
		if (remoteURLs != null)
		{
			for (String remoteURL : remoteURLs)
			{
				if (remoteURL.contains("github.com")) //$NON-NLS-1$
					return true;
			}
		}
		return false;
	}

	private char getVersionControl()
	{
		// G for git, S for SVN, H for Mercurial, N for none, O for other
		if (selectedProject == null)
			return 'N';
		if (getGitRepositoryManager().getAttached(selectedProject) != null)
			return 'G';
		RepositoryProvider provider = RepositoryProvider.getProvider(selectedProject);
		if (provider == null)
			return 'N';
		String id = provider.getID();
		if (id == null)
			return 'O';
		if (id.equals("org.tigris.subversion.subclipse.core.svnnature")) // subclipse //$NON-NLS-1$
			return 'S';
		if (id.equals("org.eclipse.team.svn.core.svnnature")) // subversive //$NON-NLS-1$
			return 'S';
		if (id.equals("org.eclipse.egit.core")) // egit //$NON-NLS-1$
			return 'G';
		if (id.equals("com.vectrace.MercurialEclipse.team.MercurialTeamProvider")) // hgEclipse //$NON-NLS-1$
			return 'M';
		if (id.equals("org.eclipse.team.cvs.core.cvsnature")) // CVS //$NON-NLS-1$
			return 'C';
		return 'O';
	}

	protected IGitRepositoryManager getGitRepositoryManager()
	{
		return GitPlugin.getDefault().getGitRepositoryManager();
	}

	private char getProjectType()
	{
		if (selectedProject != null)
		{
			// R for Rails, P for pydev, W for web, O for other. How do we determine? Check natures?
			try
			{
				// FIXME This id is a constant in the rails plugins...
				if (selectedProject.hasNature("org.radrails.rails.core.railsnature")) //$NON-NLS-1$
					return 'R';
			}
			catch (CoreException e)
			{
				ExplorerPlugin.logError(e);
			}
		}
		// TODO How do we determine if project is "web"? check for HTML/JS/CSS files?
		return 'O';
	}

	private String getVersion()
	{
		// FIXME Do we want this plugin's version, or some other version?
		return EclipseUtils.getPluginVersion(ExplorerPlugin.getDefault());
	}

	private String toHex(RGB rgb)
	{
		// FIXME This and pad are copy-pasted from Theme class
		return MessageFormat.format("{0}{1}{2}", pad(Integer.toHexString(rgb.red), 2, '0'), pad(Integer //$NON-NLS-1$
				.toHexString(rgb.green), 2, '0'), pad(Integer.toHexString(rgb.blue), 2, '0'));
	}

	private String pad(String string, int desiredLength, char padChar)
	{
		while (string.length() < desiredLength)
			string = padChar + string;
		return string;
	}

	protected void hideFilterLable()
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
		hideFilterLable();
	}

	private void addProjectResourceListener()
	{
		fProjectsListener = new ResourceListener();
		ResourcesPlugin.getWorkspace().addResourceChangeListener(fProjectsListener, IResourceChangeEvent.POST_CHANGE);
	}

	private void listenToActiveProjectPrefChanges()
	{
		fActiveProjectPrefChangeListener = new IPreferenceChangeListener()
		{

			public void preferenceChange(PreferenceChangeEvent event)
			{
				if (!event.getKey().equals(IPreferenceConstants.ACTIVE_PROJECT))
					return;
				final IProject oldActiveProject = selectedProject;
				Object obj = event.getNewValue();
				if (obj == null)
					return;
				String newProjectName = (String) obj;
				if (oldActiveProject != null && newProjectName.equals(oldActiveProject.getName()))
					return;
				final IProject newSelectedProject = ResourcesPlugin.getWorkspace().getRoot().getProject(newProjectName);
				selectedProject = newSelectedProject;
				PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable()
				{

					@Override
					public void run()
					{
						projectChanged(oldActiveProject, newSelectedProject);
					}
				});
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
		treeThemer = new TreeThemer(getCommonViewer());
		treeThemer.apply();
		// list for theme changes, update message area
		fThemeChangeListener = new IPreferenceChangeListener()
		{

			public void preferenceChange(PreferenceChangeEvent event)
			{
				if (!event.getKey().equals(IThemeManager.THEME_CHANGED))
					return;
				updateMessageArea();
			}
		};
		new InstanceScope().getNode(CommonEditorPlugin.PLUGIN_ID).addPreferenceChangeListener(fThemeChangeListener);
	}

	protected IThemeManager getThemeManager()
	{
		return CommonEditorPlugin.getDefault().getThemeManager();
	}

	private IProject detectSelectedProject()
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
				return null;
			project = projects[0];
		}
		return project;
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
		removeFileWatcher();
		try
		{
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
		getCommonViewer().setInput(newProject);
		// Update the tree since project changed
		// updateViewer(oldProject, newProject); // no structural change, just project changed
		updateMessageArea();
	}

	private void updateMessageArea()
	{
		if (updateMessageAreaJob == null)
		{
			updateMessageAreaJob = new Job("Updating App Explorer message area")
			{
				boolean shouldCancel = false;

				@Override
				protected IStatus run(IProgressMonitor monitor)
				{
					if (monitor != null && monitor.isCanceled() && shouldCancel)
						return Status.CANCEL_STATUS;
					boolean connected = false;
					HttpURLConnection connection = null;
					try
					{
						connection = (HttpURLConnection) new URL(BASE_MESSAGE_URL).openConnection();
						connection.setRequestMethod("HEAD"); // Don't ask for content //$NON-NLS-1$
						connection.setAllowUserInteraction(false);
						connection.connect();
						connected = true;

					}
					catch (Exception e)
					{
						connected = false;
					}
					finally
					{
						if (connection != null)
							connection.disconnect();
					}
					final boolean wasConnected = connected;
					if (monitor != null && monitor.isCanceled() && shouldCancel)
						return Status.CANCEL_STATUS;

					PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable()
					{

						@Override
						public void run()
						{
							if (wasConnected)
							{
								browser.setUrl(getURLForProject());
								// Show the message area
								FormData fd = (FormData) normal.getLayoutData();
								fd.bottom.control = browserComposite;
								fd.bottom.numerator = 0;

								FormData fd2 = (FormData) browserComposite.getLayoutData();
								fd2.top.offset = -MINIMUM_BROWSER_HEIGHT;
							}
							else
							{
								// Hide the message area!
								FormData fd = (FormData) normal.getLayoutData();
								fd.bottom.control = null;
								fd.bottom.numerator = 100;

								FormData fd2 = (FormData) browserComposite.getLayoutData();
								fd2.top.offset = 0;
							}
						}
					});
					shouldCancel = true;
					return Status.OK_STATUS;
				}
			};
			updateMessageAreaJob.setSystem(true);
			updateMessageAreaJob.setPriority(Job.SHORT);
		}
		else
		{
			updateMessageAreaJob.cancel();
		}
		updateMessageAreaJob.schedule(25);
	}

	private void removeFileWatcher()
	{
		try
		{
			if (watcher != null)
			{
				FileWatcher.removeWatch(watcher);
			}
		}
		catch (JNotifyException e)
		{
			ExplorerPlugin.logError(e.getMessage(), e);
		}
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
		// FIXME Need to update the element plus all it's children recursively if we want to call "update"
		// List<Object> nonNulls = new ArrayList<Object>();
		for (Object element : elements)
		{
			if (element == null)
				continue;
			// nonNulls.add(element);
			getCommonViewer().refresh(element);
		}
		// getCommonViewer().update(nonNulls.toArray(), null);
	}

	@Override
	public void dispose()
	{
		removeFileWatcher();
		treeThemer.dispose();
		treeThemer = null;
		removeProjectResourceListener();
		removeActiveProjectPrefListener();
		removeThemeListener();
		super.dispose();
	}

	private void removeThemeListener()
	{
		if (fThemeChangeListener != null)
		{
			new InstanceScope().getNode(CommonEditorPlugin.PLUGIN_ID).removePreferenceChangeListener(
					fThemeChangeListener);
		}
		fThemeChangeListener = null;
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
			new InstanceScope().getNode(ExplorerPlugin.PLUGIN_ID).removePreferenceChangeListener(
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
											return;
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
							else if (delta.getKind() == IResourceDelta.REMOVED
									|| (delta.getKind() == IResourceDelta.CHANGED
											&& (delta.getFlags() & IResourceDelta.OPEN) != 0 && !resource
											.isAccessible()))
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
											if (projects.length > 0 && projects[0].isAccessible())
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

	/**
	 * Here we dynamically remove a large number of the right-click context menu's items for the App Explorer.
	 * 
	 * @param menu
	 */
	protected void mangleContextMenu(Menu menu)
	{
		// TODO If the selected project isn't accessible, remove new file/folder, debug as
		forceOurNewFileWizard(menu);

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

	@SuppressWarnings({ "nls" })
	protected void forceOurNewFileWizard(Menu menu)
	{
		// Hack the New > File entry
		for (MenuItem menuItem : menu.getItems())
		{
			Object data = menuItem.getData();
			if (data instanceof IContributionItem)
			{
				IContributionItem contrib = (IContributionItem) data;
				if ("common.new.menu".equals(contrib.getId()))
				{
					MenuManager manager = (MenuManager) contrib;
					// force an entry for our special template New File wizard!
					IWizardRegistry registry = PlatformUI.getWorkbench().getNewWizardRegistry();
					IWizardDescriptor desc = registry.findWizard("com.aptana.ui.wizards.new.file");
					manager.insertAfter("new", new WizardShortcutAction(PlatformUI.getWorkbench()
							.getActiveWorkbenchWindow(), desc));
					manager.remove("new");
					break;
				}
			}
		}
	}

	protected void removeMenuItems(Menu menu, Set<String> idsToRemove)
	{
		if (idsToRemove == null || idsToRemove.isEmpty())
			return;
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

	public int computePreferredSize(boolean width, int availableParallel, int availablePerpendicular,
			int preferredResult)
	{
		if (width)
		{
			return Math.max(MINIMUM_BROWSER_WIDTH, preferredResult);
		}
		return preferredResult;
	}

	public int getSizeFlags(boolean width)
	{
		if (width)
			return SWT.MIN;
		return SWT.NONE;
	}
}
