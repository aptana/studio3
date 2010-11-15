/**
 * This file Copyright (c) 2005-2010 Aptana, Inc. This program is
 * dual-licensed under both the Aptana Public License and the GNU General
 * Public license. You may elect to use one or the other of these licenses.
 * 
 * This program is distributed in the hope that it will be useful, but
 * AS-IS and WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE, TITLE, or
 * NONINFRINGEMENT. Redistribution, except as permitted by whichever of
 * the GPL or APL you select, is prohibited.
 *
 * 1. For the GPL license (GPL), you can redistribute and/or modify this
 * program under the terms of the GNU General Public License,
 * Version 3, as published by the Free Software Foundation.  You should
 * have received a copy of the GNU General Public License, Version 3 along
 * with this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 * 
 * Aptana provides a special exception to allow redistribution of this file
 * with certain other free and open source software ("FOSS") code and certain additional terms
 * pursuant to Section 7 of the GPL. You may view the exception and these
 * terms on the web at http://www.aptana.com/legal/gpl/.
 * 
 * 2. For the Aptana Public License (APL), this program and the
 * accompanying materials are made available under the terms of the APL
 * v1.0 which accompanies this distribution, and is available at
 * http://www.aptana.com/legal/apl/.
 * 
 * You may view the GPL, Aptana's exception and additional terms, and the
 * APL in the file titled license.html at the root of the corresponding
 * plugin containing this source file.
 * 
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.explorer.internal.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IEclipsePreferences.IPreferenceChangeListener;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.action.ContributionItem;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.search.ui.IContextMenuConstants;
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
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.DeleteResourceAction;
import org.eclipse.ui.internal.navigator.wizards.WizardShortcutAction;
import org.eclipse.ui.menus.IMenuService;
import org.eclipse.ui.menus.MenuUtil;
import org.eclipse.ui.navigator.CommonNavigator;
import org.eclipse.ui.navigator.CommonViewer;
import org.eclipse.ui.navigator.ICommonFilterDescriptor;
import org.eclipse.ui.navigator.INavigatorFilterService;
import org.eclipse.ui.progress.UIJob;
import org.eclipse.ui.swt.IFocusService;
import org.eclipse.ui.wizards.IWizardDescriptor;
import org.eclipse.ui.wizards.IWizardRegistry;
import org.osgi.service.prefs.BackingStoreException;

import com.aptana.core.IScopeReference;
import com.aptana.core.ShellExecutable;
import com.aptana.core.resources.IProjectContext;
import com.aptana.core.util.ExecutableUtil;
import com.aptana.core.util.ProcessUtil;
import com.aptana.deploy.dialogs.FTPDeployPropertyDialog;
import com.aptana.deploy.preferences.DeployPreferenceUtil;
import com.aptana.deploy.preferences.IPreferenceConstants.DeployType;
import com.aptana.explorer.ExplorerPlugin;
import com.aptana.explorer.IExplorerUIConstants;
import com.aptana.explorer.IPreferenceConstants;
import com.aptana.git.core.GitPlugin;
import com.aptana.git.core.model.GitRepository;
import com.aptana.ide.core.io.IConnectionPoint;
import com.aptana.ide.syncing.core.ISiteConnection;
import com.aptana.ide.syncing.core.ResourceSynchronizationUtils;
import com.aptana.ide.syncing.core.SiteConnectionUtils;
import com.aptana.ide.syncing.ui.actions.DownloadAction;
import com.aptana.ide.syncing.ui.actions.UploadAction;
import com.aptana.ide.syncing.ui.dialogs.ChooseSiteConnectionDialog;
import com.aptana.scripting.model.BundleElement;
import com.aptana.scripting.model.BundleEntry;
import com.aptana.scripting.model.BundleManager;
import com.aptana.scripting.model.CommandElement;
import com.aptana.theme.IControlThemerFactory;
import com.aptana.theme.IThemeManager;
import com.aptana.theme.ThemePlugin;
import com.aptana.ui.UIUtils;
import com.aptana.ui.widgets.SearchComposite;

/**
 * Customized CommonNavigator that adds a project combo and focuses the view on a single project.
 * 
 * @author cwilliams
 */
@SuppressWarnings("restriction")
public abstract class SingleProjectView extends CommonNavigator implements SearchComposite.Client, IProjectContext
{

	/**
	 * Pref key to track whether we turned off ".*" filename filter that is on by default.
	 */
	private static final String TURNED_OFF_DOT_STAR_FILE_FILTER = "turnedOffDotStarFileFilter"; //$NON-NLS-1$

	private static final String RAILS_NATURE = "org.radrails.rails.core.railsnature"; //$NON-NLS-1$
	private static final String WEB_NATURE = "com.aptana.projects.webnature"; //$NON-NLS-1$
	private static final String PHP_NATURE = "com.aptana.editor.php.phpNature"; //$NON-NLS-1$
	private static final String BUNDLE_HEROKU = "Heroku"; //$NON-NLS-1$
	private static final String BUNDLE_ENGINE_YARD = "Engine Yard"; //$NON-NLS-1$

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
		TO_REMOVE.add("org.radrails.rails.ui.actions.DebugScriptServerAction"); //$NON-NLS-1$
	};

	private ToolItem deployToolItem;
	private ToolItem projectToolItem;
	private Menu projectsMenu;

	private ISiteConnection[] siteConnections;
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

	// memento wasn't declared protected until Eclipse 3.5, so store it ourselves
	protected IMemento memento;

	private static final String GEAR_MENU_ICON = "icons/full/elcl16/command.png"; //$NON-NLS-1$
	private static final String DEPLOY_MENU_ICON = "icons/full/elcl16/network_arrow.png"; //$NON-NLS-1$
	private static final String UPLOAD_MENU_ICON = "icons/full/elcl16/arrow_up.png"; //$NON-NLS-1$
	private static final String DOWNLOAD_MENU_ICON = "icons/full/elcl16/arrow_down.png"; //$NON-NLS-1$
	private static final String CLOSE_ICON = "icons/full/elcl16/close.png"; //$NON-NLS-1$
	// private static final String[] animationImage = {
	private static final String[] animationImageUp = { "icons/full/elcl16/arrow_up.png" }; //$NON-NLS-1$
	private static final String[] animationImageDown = { "icons/full/elcl16/arrow_down.png" }; //$NON-NLS-1$

	private static final String GROUP_DEPLOY = "group.deploy"; //$NON-NLS-1$
	private static final String GROUP_HEROKU_COMMANDS = "group.herokucommands"; //$NON-NLS-1$
	private static final String GROUP_CAP = "group.cap"; //$NON-NLS-1$
	private static final String GROUP_FTP_SETTINGS = "group.ftp_settings"; //$NON-NLS-1$
	private static final String GROUP_FTP = "group.ftp"; //$NON-NLS-1$
	private static final String GROUP_WIZARD = "group.wizard"; //$NON-NLS-1$
	private static final String GROUP_EY_COMMANDS = "group.ey"; //$NON-NLS-1$

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

		// Now deploy button and gear...
		Composite toolbarButtons = new Composite(toolbarComposite, SWT.NONE);
		GridData buttonsData = new GridData(SWT.END, SWT.BEGINNING, false, false);
		buttonsData.minimumWidth = 68;
		toolbarButtons.setLayoutData(buttonsData);
		GridLayout toolbarButtonsLayout = new GridLayout(2, false);
		toolbarButtonsLayout.marginHeight = 0;
		toolbarButtonsLayout.marginWidth = 0;
		toolbarButtons.setLayout(toolbarButtonsLayout);

		// Create Deploy menu
		createDeployMenu(toolbarButtons);

		// Now create Commands menu
		final ToolBar commandsToolBar = new ToolBar(toolbarButtons, SWT.FLAT);
		ToolItem commandsToolItem = new ToolItem(commandsToolBar, SWT.DROP_DOWN);
		commandsToolItem.setImage(ExplorerPlugin.getImage(GEAR_MENU_ICON));
		commandsToolItem.setToolTipText(Messages.SingleProjectView_TTP_Commands);
		GridData gearMenuData = new GridData(SWT.END, SWT.CENTER, false, false);
		gearMenuData.minimumWidth = 24;
		commandsToolBar.setLayoutData(gearMenuData);

		commandsToolItem.addSelectionListener(new SelectionAdapter()
		{

			@Override
			public void widgetSelected(SelectionEvent selectionEvent)
			{
				Point toolbarLocation = commandsToolBar.getLocation();
				toolbarLocation = commandsToolBar.getParent().toDisplay(toolbarLocation.x, toolbarLocation.y);
				Point toolbarSize = commandsToolBar.getSize();
				final MenuManager commandsMenuManager = new MenuManager(null, IExplorerUIConstants.GEAR_MENU_ID);
				IMenuService menuService = (IMenuService) getSite().getService(IMenuService.class);
				menuService.populateContributionManager(commandsMenuManager,
						MenuUtil.menuUri(commandsMenuManager.getId()));
				fillCommandsMenu(commandsMenuManager);
				final Menu commandsMenu = commandsMenuManager.createContextMenu(commandsToolBar);
				commandsMenu.setLocation(toolbarLocation.x, toolbarLocation.y + toolbarSize.y + 2);
				commandsMenu.setVisible(true);
			}
		});

		createSearchComposite(parent);
		filterComp = createFilterComposite(parent);
		createNavigator(parent);

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

		hookToThemes();
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

	public void init(IViewSite aSite, IMemento aMemento) throws PartInitException
	{
		super.init(aSite, aMemento);
		this.memento = aMemento;
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

	private void fillCommandsMenu(MenuManager menuManager)
	{
		// Filtering
		// Run
		// Git single files
		// Git project level
		// Git branching
		// Git misc
		// Misc project/properties

		// Stick Delete in Properties area
		menuManager.appendToGroup(IContextMenuConstants.GROUP_PROPERTIES, new ContributionItem()
		{

			@Override
			public void fill(Menu menu, int index)
			{
				MenuItem item = new MenuItem(menu, SWT.PUSH);
				item.setText(Messages.SingleProjectView_DeleteProjectMenuItem_LBL);
				item.addSelectionListener(new SelectionAdapter()
				{
					@Override
					public void widgetSelected(SelectionEvent e)
					{
						DeployPreferenceUtil.setDeployType(selectedProject, DeployType.NONE);
						DeleteResourceAction action = new DeleteResourceAction(getSite());
						action.selectionChanged(new StructuredSelection(selectedProject));
						action.run();
					}
				});
				boolean enabled = (selectedProject != null && selectedProject.exists());
				ISharedImages images = PlatformUI.getWorkbench().getSharedImages();
				item.setImage(enabled ? images.getImage(ISharedImages.IMG_TOOL_DELETE) : images
						.getImage(ISharedImages.IMG_TOOL_DELETE_DISABLED));
				item.setEnabled(enabled);
			}

			@Override
			public boolean isDynamic()
			{
				return true;
			}
		});
	}

	private void createDeployMenu(Composite parent)
	{
		final ToolBar deployToolBar = new ToolBar(parent, SWT.FLAT);
		deployToolItem = new ToolItem(deployToolBar, SWT.DROP_DOWN);
		deployToolItem.setImage(ExplorerPlugin.getImage(DEPLOY_MENU_ICON));
		deployToolItem.setToolTipText(Messages.SingleProjectView_TTP_Deploy);
		GridData deployComboData = new GridData(SWT.END, SWT.CENTER, true, false);
		deployComboData.minimumWidth = 24;
		deployToolBar.setLayoutData(deployComboData);

		deployToolItem.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent selectionEvent)
			{
				Point toolbarLocation = deployToolBar.getLocation();
				toolbarLocation = deployToolBar.getParent().toDisplay(toolbarLocation.x, toolbarLocation.y);
				Point toolbarSize = deployToolBar.getSize();
				// FIXME Move declaration/filling of menu to ext pt, that means removing fillDeployMenu!
				final MenuManager deployMenuManager = new MenuManager(null, IExplorerUIConstants.DEPLOY_MENU_ID);
				fillDeployMenu(deployMenuManager);
				IMenuService menuService = (IMenuService) getSite().getService(IMenuService.class);
				menuService.populateContributionManager(deployMenuManager, MenuUtil.menuUri(deployMenuManager.getId()));
				final Menu commandsMenu = deployMenuManager.createContextMenu(deployToolBar);
				commandsMenu.setLocation(toolbarLocation.x, toolbarLocation.y + toolbarSize.y + 2);
				commandsMenu.setVisible(true);
			}
		});
	}

	private void fillDeployMenu(MenuManager menuManager)
	{
		if (selectedProject != null && selectedProject.isAccessible())
		{
			DeployType type = DeployPreferenceUtil.getDeployType(selectedProject);
			if (type == null)
			{
				if (isCapistranoProject())
				{
					// insert commands for capistrano here
					menuManager.add(new Separator(GROUP_CAP));
				}
				else if (isHerokuProject())
				{
					addHerokuMenuCommands(menuManager);
				}
				else if (isFTPProject())
				{
					addFTPMenuCommands(menuManager);
				}
			}
			else if (type == DeployType.HEROKU)
			{
				addHerokuMenuCommands(menuManager);
			}
			// Still need to call isFTPProject to populate siteConnections variable
			else if ((type == DeployType.FTP) && isFTPProject())
			{
				addFTPMenuCommands(menuManager);
			}
			else if (type == DeployType.CAPISTRANO)
			{
				menuManager.add(new Separator(GROUP_CAP));
			}
			else if (isEngineYardProject())
			{
				addEngineYardMenuCommands(menuManager);
			}
		}
		menuManager.add(new Separator(GROUP_WIZARD));
	}

	private void addFTPMenuCommands(MenuManager menuManager)
	{
		menuManager.add(new Separator(GROUP_FTP));

		ISiteConnection site = null;
		if (siteConnections == null)
		{
			siteConnections = SiteConnectionUtils.findSitesForSource(selectedProject, true);
		}
		if (siteConnections.length > 1)
		{
			// try for last remembered site first
			String lastConnection = ResourceSynchronizationUtils.getLastSyncConnection(selectedProject);
			if (lastConnection == null)
			{
				lastConnection = DeployPreferenceUtil.getDeployEndpoint(selectedProject);
			}
			if (lastConnection != null)
			{
				site = SiteConnectionUtils.getSiteWithDestination(lastConnection, siteConnections);
			}
		}
		else if (siteConnections.length == 1)
		{
			site = siteConnections[0];
		}
		final ISiteConnection lastSiteConnection = site;
		menuManager.appendToGroup(GROUP_FTP, new ContributionItem()
		{

			@Override
			public void fill(Menu menu, int index)
			{

				SelectionAdapter uploadAdapter = new SelectionAdapter()
				{

					@Override
					public void widgetSelected(SelectionEvent e)
					{
						UploadAction action = new UploadAction();
						action.setActivePart(null, PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
								.getActivePart());
						action.setSelection(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getSelectionService()
								.getSelection());
						action.setSelectedSite(lastSiteConnection);
						action.addJobListener(new JobChangeAdapter()
						{

							private AnimatedIconThread iconThread;

							@Override
							public void running(IJobChangeEvent e)
							{
								iconThread = new AnimatedIconThread(DEPLOY_MENU_ICON, animationImageUp, UIUtils
										.getDisplay(), deployToolItem);
								iconThread.start();
							}

							@Override
							public void done(IJobChangeEvent e)
							{
								UIUtils.getDisplay().asyncExec(new Runnable()
								{
									public void run()
									{
										iconThread.terminate();
									}
								});
							}
						});
						action.run(null);
					}
				};

				MenuItem ul = createSubMenuItemWithListener(menu, Messages.SingleProjectView_UploadItem, uploadAdapter);
				ul.setImage(ExplorerPlugin.getImage(UPLOAD_MENU_ICON));
				ul.setAccelerator(SWT.MOD1 | SWT.MOD2 | 'U');

				SelectionAdapter downloadAdapter = new SelectionAdapter()
				{

					@Override
					public void widgetSelected(SelectionEvent e)
					{
						DownloadAction action = new DownloadAction();
						action.setActivePart(null, PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
								.getActivePart());
						action.setSelection(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getSelectionService()
								.getSelection());
						action.setSelectedSite(lastSiteConnection);
						action.addJobListener(new JobChangeAdapter()
						{

							private AnimatedIconThread iconThread;

							@Override
							public void running(IJobChangeEvent e)
							{
								iconThread = new AnimatedIconThread(DEPLOY_MENU_ICON, animationImageDown, UIUtils
										.getDisplay(), deployToolItem);
								iconThread.start();
							}

							@Override
							public void done(IJobChangeEvent e)
							{
								UIUtils.getDisplay().asyncExec(new Runnable()
								{
									public void run()
									{
										iconThread.terminate();
									}
								});
							}
						});
						action.run(null);
					}
				};
				MenuItem dl = createSubMenuItemWithListener(menu, Messages.SingleProjectView_DownloadItem,
						downloadAdapter);
				dl.setImage(ExplorerPlugin.getImage(DOWNLOAD_MENU_ICON));
				dl.setAccelerator(SWT.MOD1 | SWT.MOD2 | 'D');
			}

			@Override
			public boolean isDynamic()
			{
				return true;
			}
		});

		menuManager.add(new Separator(GROUP_FTP_SETTINGS));
		menuManager.appendToGroup(GROUP_FTP_SETTINGS, new ContributionItem()
		{

			@Override
			public void fill(Menu menu, int index)
			{
				MenuItem settingsItem = new MenuItem(menu, SWT.PUSH);
				settingsItem.setText(Messages.SingleProjectView_FTPSettingItem);
				settingsItem.addSelectionListener(new SelectionAdapter()
				{

					@Override
					public void widgetSelected(SelectionEvent e)
					{
						FTPDeployPropertyDialog settingsDialog = new FTPDeployPropertyDialog(PlatformUI.getWorkbench()
								.getActiveWorkbenchWindow().getShell());
						settingsDialog.setProject(selectedProject);
						if (lastSiteConnection != null)
						{
							settingsDialog.setPropertySource(lastSiteConnection.getDestination());
						}
						else if (siteConnections.length > 1)
						{
							ChooseSiteConnectionDialog dialog = new ChooseSiteConnectionDialog(PlatformUI
									.getWorkbench().getActiveWorkbenchWindow().getShell(), siteConnections);
							dialog.setShowRememberMyDecision(true);
							dialog.open();

							IConnectionPoint destination = dialog.getSelectedSite().getDestination();
							if (destination != null)
							{
								Boolean rememberMyDecision = dialog.isRememberMyDecision();
								if (rememberMyDecision)
								{
									ResourceSynchronizationUtils.setRememberDecision(selectedProject,
											rememberMyDecision);
								}
								// remembers the last sync connection
								ResourceSynchronizationUtils.setLastSyncConnection(selectedProject,
										destination.getName());
							}
							settingsDialog.setPropertySource(destination);
						}
						settingsDialog.open();
					}
				});
			}

			@Override
			public boolean isDynamic()
			{
				return true;
			}
		});
	}

	private void addHerokuMenuCommands(MenuManager menuManager)
	{
		menuManager.add(new Separator(GROUP_DEPLOY));
		menuManager.add(new Separator(GROUP_HEROKU_COMMANDS));

		menuManager.appendToGroup(GROUP_HEROKU_COMMANDS, new ContributionItem()
		{
			@Override
			public void fill(Menu menu, int index)
			{
				MenuItem item = new MenuItem(menu, SWT.PUSH);
				item.setText(Messages.SingleProjectView_OpenBrowserItem);
				item.addSelectionListener(new SelectionAdapter()
				{
					public void widgetSelected(SelectionEvent e)
					{
						// run heroku info
						Map<String, String> env = new HashMap<String, String>();
						env.putAll(ShellExecutable.getEnvironment());
						IPath workingDir = selectedProject.getLocation();
						IPath herokuPath = ExecutableUtil.find("heroku", true, null); //$NON-NLS-1$
						String output = ProcessUtil.outputForCommand(herokuPath.toOSString(), workingDir, env, "info"); //$NON-NLS-1$

						try
						{
							// extract url from heroku info
							if (output != null && output.contains("Web URL:")) //$NON-NLS-1$
							{
								String URL = output.split("Web URL:")[1].split("\n")[0].replace(" ", ""); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$

								// Determine which OS and open url
								if (Platform.OS_MACOSX.equals(Platform.getOS()))
								{
									ProcessUtil.run("open", null, (Map<String, String>) null, URL); //$NON-NLS-1$
								}
								else if (Platform.OS_WIN32.equals(Platform.getOS()))
								{
									ProcessUtil.run("cmd", null, (Map<String, String>) null, "/c", "start " + URL); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
								}
								else
								{
									ProcessUtil.run("x-www-browser", null, (Map<String, String>) null, URL); //$NON-NLS-1$
								}
							}
						}
						catch (Exception e1)
						{
							ExplorerPlugin.logError(e1.getMessage(), e1);
						}
					}
				});

				// Sharing Submenu
				final MenuItem sharingMenuItem = new MenuItem(menu, SWT.CASCADE);
				sharingMenuItem.setText(Messages.SingleProjectView_SharingSubmenuLabel);
				Menu sharingSubMenu = new Menu(menu);

				createDeploySubMenuItem(sharingSubMenu, "Add Collaborator", BUNDLE_HEROKU); //$NON-NLS-1$
				createDeploySubMenuItem(sharingSubMenu, "Remove Collaborator", BUNDLE_HEROKU); //$NON-NLS-1$
				sharingMenuItem.setMenu(sharingSubMenu);

				// Database
				final MenuItem databaseMenuItem = new MenuItem(menu, SWT.CASCADE);
				databaseMenuItem.setText(Messages.SingleProjectView_DatabaseSubmenuLabel);
				Menu databaseSubMenu = new Menu(menu);

				createDeploySubMenuItem(databaseSubMenu, "Rake db:migrate on Heroku", BUNDLE_HEROKU); //$NON-NLS-1$
				createDeploySubMenuItem(databaseSubMenu, "Push Local Database to Heroku", BUNDLE_HEROKU); //$NON-NLS-1$
				createDeploySubMenuItem(databaseSubMenu, "Pull Remote Database from Heroku", BUNDLE_HEROKU); //$NON-NLS-1$

				databaseMenuItem.setMenu(databaseSubMenu);

				// Maintenance
				final MenuItem maintenanceMenuItem = new MenuItem(menu, SWT.CASCADE);
				maintenanceMenuItem.setText(Messages.SingleProjectView_MaintenanceSubmenuLabel);
				Menu maintanenceSubMenu = new Menu(menu);

				createDeploySubMenuItem(maintanenceSubMenu, "Turn Maintence On", BUNDLE_HEROKU); //$NON-NLS-1$
				createDeploySubMenuItem(maintanenceSubMenu, "Turn Maintence Off", BUNDLE_HEROKU); //$NON-NLS-1$

				maintenanceMenuItem.setMenu(maintanenceSubMenu);

				// Remote
				final MenuItem remoteMenuItem = new MenuItem(menu, SWT.CASCADE);
				remoteMenuItem.setText(Messages.SingleProjectView_RemoteSubmenuLabel);
				Menu remoteSubMenu = new Menu(menu);

				createDeploySubMenuItem(remoteSubMenu, "Console", BUNDLE_HEROKU); //$NON-NLS-1$
				createDeploySubMenuItem(remoteSubMenu, "Rake Command", BUNDLE_HEROKU); //$NON-NLS-1$

				remoteMenuItem.setMenu(remoteSubMenu);

				// config vars
				final MenuItem configMenuItem = new MenuItem(menu, SWT.CASCADE);
				configMenuItem.setText(Messages.SingleProjectView_ConfigVarsSubmenuLabel);
				Menu configSubMenu = new Menu(menu);

				createDeploySubMenuItem(configSubMenu, "Add Config Var", BUNDLE_HEROKU); //$NON-NLS-1$
				createDeploySubMenuItem(configSubMenu, "Clear Config Vars", BUNDLE_HEROKU); //$NON-NLS-1$

				configMenuItem.setMenu(configSubMenu);

				// may want to add backup commands
				createDeploySubMenuItem(menu, "App Info", BUNDLE_HEROKU); //$NON-NLS-1$
				createDeploySubMenuItem(menu, "Rename App", BUNDLE_HEROKU); //$NON-NLS-1$
			}

			@Override
			public boolean isDynamic()
			{
				return true;
			}
		});
	}

	private void addEngineYardMenuCommands(MenuManager menuManager)
	{
		menuManager.add(new Separator(GROUP_DEPLOY));
		menuManager.add(new Separator(GROUP_EY_COMMANDS));

		menuManager.appendToGroup(GROUP_EY_COMMANDS, new ContributionItem()
		{

			@Override
			public void fill(Menu menu, int index)
			{

				// open ssh session

				MenuItem item = new MenuItem(menu, SWT.PUSH);
				item.setText(Messages.SingleProjectView_OpenSSHSubmenuLabel);
				item.addSelectionListener(new SelectionAdapter()
				{
					public void widgetSelected(SelectionEvent e)
					{
						final CommandElement command;
						command = getBundleCommand(BUNDLE_ENGINE_YARD, "Open SSH Session"); //$NON-NLS-1$
						command.execute();
					}
				});

				// Deployment Submenu
				final MenuItem deploymentMenuItem = new MenuItem(menu, SWT.CASCADE);
				deploymentMenuItem.setText(Messages.SingleProjectView_DeploymentSubmenuLabel);
				Menu deploymentSubMenu = new Menu(menu);

				createDeploySubMenuItem(deploymentSubMenu, "List Environments", BUNDLE_ENGINE_YARD); //$NON-NLS-1$
				createDeploySubMenuItem(deploymentSubMenu, "Retrieve Logs", BUNDLE_ENGINE_YARD); //$NON-NLS-1$
				createDeploySubMenuItem(deploymentSubMenu, "Rebuild Environment", BUNDLE_ENGINE_YARD); //$NON-NLS-1$
				createDeploySubMenuItem(deploymentSubMenu, "Rollback App", BUNDLE_ENGINE_YARD); //$NON-NLS-1$

				deploymentMenuItem.setMenu(deploymentSubMenu);

				// Recipes Submenu
				final MenuItem recipesMenuItem = new MenuItem(menu, SWT.CASCADE);
				recipesMenuItem.setText(Messages.SingleProjectView_RecipesSubmenuLabel);
				Menu recipesSubMenu = new Menu(menu);

				createDeploySubMenuItem(recipesSubMenu, "Apply Recipes", BUNDLE_ENGINE_YARD); //$NON-NLS-1$
				createDeploySubMenuItem(recipesSubMenu, "Upload Recipes", BUNDLE_ENGINE_YARD); //$NON-NLS-1$
				createDeploySubMenuItem(recipesSubMenu, "Download Recipes", BUNDLE_ENGINE_YARD); //$NON-NLS-1$

				recipesMenuItem.setMenu(recipesSubMenu);

				// Maintenance Submenu
				final MenuItem maintenanceMenuItem = new MenuItem(menu, SWT.CASCADE);
				maintenanceMenuItem.setText(Messages.SingleProjectView_MaintenanceSubmenuLabel);
				Menu maintenanceSubMenu = new Menu(menu);

				createDeploySubMenuItem(maintenanceSubMenu, "Turn Maintenance On", BUNDLE_ENGINE_YARD); //$NON-NLS-1$
				createDeploySubMenuItem(maintenanceSubMenu, "Turn Maintenance Off", BUNDLE_ENGINE_YARD); //$NON-NLS-1$

				maintenanceMenuItem.setMenu(maintenanceSubMenu);

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

				@Override
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

	private Composite createSearchComposite(Composite myComposite)
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

		super.createPartControl(viewer);
		turnOffDotStarFileFilterOnFirstStartup();
		getCommonViewer().setInput(detectSelectedProject());
		fixNavigatorManager();
	}

	private void turnOffDotStarFileFilterOnFirstStartup()
	{
		if (!Platform.getPreferencesService().getBoolean(ExplorerPlugin.PLUGIN_ID, TURNED_OFF_DOT_STAR_FILE_FILTER,
				false, null))
		{
			INavigatorFilterService filterService = getCommonViewer().getNavigatorContentService().getFilterService();
			ICommonFilterDescriptor[] descs = filterService.getVisibleFilterDescriptors();
			List<String> ids = new ArrayList<String>();
			for (ICommonFilterDescriptor desc : descs)
			{
				// Remove the .* filter
				if (!desc.getId().equals("org.eclipse.ui.navigator.resources.filters.startsWithDot")) //$NON-NLS-1$
				{
					ids.add(desc.getId());
				}
			}

			filterService.setActiveFilterIds(ids.toArray(new String[0]));
			ViewerFilter[] visibleFilters = filterService.getVisibleFilters(true);
			getCommonViewer().setFilters(visibleFilters);

			IEclipsePreferences prefs = new InstanceScope().getNode(ExplorerPlugin.PLUGIN_ID);
			prefs.putBoolean(TURNED_OFF_DOT_STAR_FILE_FILTER, true);
			try
			{
				prefs.flush();
			}
			catch (BackingStoreException e)
			{
				// ignore
			}
		}
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
		job.setSystem(true);
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

	/**
	 * Hooks up to the active theme.
	 */
	private void hookToThemes()
	{
		getControlThemerFactory().apply(getCommonViewer());
	}

	protected IThemeManager getThemeManager()
	{
		return ThemePlugin.getDefault().getThemeManager();
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
				project = projects[0];
			}
		}
		return project;
	}

	private void setActiveProject(String projectName)
	{
		IProject newSelectedProject = null;
		if (projectName != null && projectName.trim().length() > 0)
			newSelectedProject = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
		if (selectedProject != null && newSelectedProject != null && selectedProject.equals(newSelectedProject))
		{
			return;
		}

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

	public void setActiveProject(IProject project)
	{
		setActiveProject(project.getName());
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
		getControlThemerFactory().dispose(getCommonViewer());
		removeProjectResourceListener();
		removeActiveProjectPrefListener();
		super.dispose();
	}

	private IControlThemerFactory getControlThemerFactory()
	{
		return ThemePlugin.getDefault().getControlThemerFactory();
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
			{
				return;
			}
			try
			{
				delta.accept(new IResourceDeltaVisitor()
				{

					public boolean visit(IResourceDelta delta) throws CoreException
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
								// Add to the projects menu and then switch to
								// it!
								final String projectName = resource.getName();
								Display.getDefault().asyncExec(new Runnable()
								{

									public void run()
									{
										// Construct the menu item to for this
										// project
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

	private MenuItem createSubMenuItemWithListener(Menu menu, String text, SelectionListener listener)
	{
		MenuItem synchronizeItem = new MenuItem(menu, SWT.PUSH);
		synchronizeItem.setText(text);
		synchronizeItem.addSelectionListener(listener);
		return synchronizeItem;
	}

	private void createDeploySubMenuItem(Menu menu, String cmd, String bundle)
	{
		final CommandElement command;
		command = getBundleCommand(bundle, cmd);

		MenuItem item = new MenuItem(menu, SWT.PUSH);
		item.setText(cmd);
		item.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				command.execute();
			}
		});

	}

	private boolean isCapistranoProject()
	{
		return selectedProject.getFile("Capfile").exists(); //$NON-NLS-1$
	}

	private boolean isFTPProject()
	{
		siteConnections = SiteConnectionUtils.findSitesForSource(selectedProject);
		return siteConnections.length > 0;
	}

	private boolean isHerokuProject()
	{
		GitRepository repo = GitPlugin.getDefault().getGitRepositoryManager().getAttached(selectedProject);
		if (repo != null)
		{
			for (String remote : repo.remotes())
			{
				if (remote.indexOf("heroku") != -1) { //$NON-NLS-1$
					return true;
				}
			}
			for (String remoteURL : repo.remoteURLs())
			{
				if (remoteURL.indexOf("heroku.com") != -1) { //$NON-NLS-1$
					return true;
				}
			}
		}
		return false;
	}

	private boolean isEngineYardProject()
	{
		DeployType type = DeployPreferenceUtil.getDeployType(selectedProject);

		// Engine Yard gem does not work in Windows
		if (!Platform.OS_WIN32.equals(Platform.getOS()))
		{
			if (type.equals(DeployType.ENGINEYARD))
			{
				return true;
			}
		}

		return false;
	}

	private CommandElement getBundleCommand(String bundleName, String commandName)
	{
		BundleEntry entry = BundleManager.getInstance().getBundleEntry(bundleName);
		if (entry == null)
		{
			return null;
		}
		for (BundleElement bundle : entry.getContributingBundles())
		{
			CommandElement command = bundle.getCommandByName(commandName);
			if (command != null)
			{
				return command;
			}
		}
		return null;
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
}
