/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.explorer.internal.ui;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.NotEnabledException;
import org.eclipse.core.commands.NotHandledException;
import org.eclipse.core.commands.common.NotDefinedException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.team.core.RepositoryProvider;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.handlers.IHandlerService;
import org.eclipse.ui.progress.UIJob;

import com.aptana.core.logging.IdeLog;
import com.aptana.core.util.EclipseUtil;
import com.aptana.core.util.ProcessStatus;
import com.aptana.explorer.ExplorerPlugin;
import com.aptana.git.core.GitPlugin;
import com.aptana.git.core.IDebugScopes;
import com.aptana.git.core.IPreferenceConstants;
import com.aptana.git.core.model.BranchAddedEvent;
import com.aptana.git.core.model.BranchChangedEvent;
import com.aptana.git.core.model.BranchRemovedEvent;
import com.aptana.git.core.model.GitRepository;
import com.aptana.git.core.model.IGitRepositoriesListener;
import com.aptana.git.core.model.IGitRepositoryListener;
import com.aptana.git.core.model.IGitRepositoryManager;
import com.aptana.git.core.model.IndexChangedEvent;
import com.aptana.git.core.model.PullEvent;
import com.aptana.git.core.model.PushEvent;
import com.aptana.git.core.model.RepositoryAddedEvent;
import com.aptana.git.core.model.RepositoryRemovedEvent;
import com.aptana.git.ui.dialogs.CreateBranchDialog;

/**
 * Adds Git UI elements to the Single Project View.
 * 
 * @author cwilliams
 */
public class GitProjectView extends SingleProjectView implements IGitRepositoryListener, IGitRepositoriesListener
{
	private static final String TEAM_MAIN = "team.main"; //$NON-NLS-1$
	private static final String DIRTY_SUFFIX = "*"; //$NON-NLS-1$
	private static final String GIT_CHANGED_FILES_FILTER = "GitChangedFilesFilterEnabled"; //$NON-NLS-1$
	private static final String PROJECT_DELIMITER = "######"; //$NON-NLS-1$
	private static final String CHANGED_FILE_FILTER_ICON_PATH = "icons/full/elcl16/filter.png"; //$NON-NLS-1$

	private static final String CREATE_NEW_BRANCH_TEXT = Messages.GitProjectView_createNewBranchOption;

	private Label leftLabel;
	private GridData leftLabelGridData;
	private Label rightLabel;
	private GridData rightLabelGridData;
	private ToolBar branchesToolbar;
	private GridData branchesToolbarGridData;

	private ToolItem branchesToolItem;
	private Menu branchesMenu;

	private GitChangedFilesFilter fChangedFilesFilter;
	// keeps the list of projects which has turned on the changed files filter
	private Set<IProject> fChangedFilesFilterProjects;
	private Job pullCalc;
	private HashMap<String, Boolean> branchToPullIndicator = new HashMap<String, Boolean>();
	private UIJob refreshUIJob;

	public GitProjectView()
	{
		fChangedFilesFilterProjects = new HashSet<IProject>();
	}

	@Override
	public void createPartControl(Composite aParent)
	{
		super.createPartControl(aParent);

		getGitRepositoryManager().addListener(this);

		if (fChangedFilesFilterProjects.contains(selectedProject))
		{
			UIJob job = new UIJob("Turn on git filter initially") //$NON-NLS-1$
			{

				@Override
				public IStatus runInUIThread(IProgressMonitor monitor)
				{
					addGitChangedFilesFilter();
					return Status.OK_STATUS;
				}
			};
			EclipseUtil.setSystemForJob(job);
			job.setPriority(Job.SHORT);
			job.schedule(300);
		}

		// Calculate the pull indicators in a recurring background job!
		pullCalc = new Job("Calculating git pull indicators") //$NON-NLS-1$
		{

			@Override
			protected IStatus run(IProgressMonitor monitor)
			{
				// Don't do any work if user has turned off calc'ing pull indicators.
				boolean performFetches = Platform.getPreferencesService().getBoolean(GitPlugin.getPluginId(),
						IPreferenceConstants.GIT_CALCULATE_PULL_INDICATOR, false, null);
				if (!performFetches)
				{
					// FIXME Listen for change to this pref and then schedule if it gets turned on!
					return Status.OK_STATUS;
				}

				if (monitor != null && monitor.isCanceled())
					return Status.CANCEL_STATUS;

				GitRepository repo = getGitRepositoryManager().getAttached(selectedProject);
				if (repo == null)
				{
					// FIXME Don't reschedule, listen for repo attachment and then start running this.
					schedule(5 * 60 * 1000); // reschedule for 5 minutes after we return!
					return Status.OK_STATUS;
				}

				if (monitor != null && monitor.isCanceled())
					return Status.CANCEL_STATUS;

				synchronized (branchToPullIndicator)
				{
					branchToPullIndicator.clear();
				}
				Set<String> branchesToPull = repo.getOutOfDateBranches();
				for (String branch : repo.localBranches())
				{
					if (monitor != null && monitor.isCanceled())
						return Status.CANCEL_STATUS;
					synchronized (branchToPullIndicator)
					{
						branchToPullIndicator.put(branch, branchesToPull.contains(branch));
					}
				}

				refreshUI(repo);

				if (monitor != null && monitor.isCanceled())
					return Status.CANCEL_STATUS;

				// TODO Allow user to have control over how often we poll
				schedule(5 * 60 * 1000); // reschedule for 5 minutes after we return!
				return Status.OK_STATUS;
			}
		};
		EclipseUtil.setSystemForJob(pullCalc);
		pullCalc.setPriority(Job.LONG);
		pullCalc.schedule();
	}

	protected void doCreateToolbar(Composite toolbarComposite)
	{
		Composite branchComp = new Composite(toolbarComposite, SWT.NONE);

		GridLayout toolbarGridLayout = new GridLayout(3, false);
		toolbarGridLayout.marginWidth = 2;
		toolbarGridLayout.marginHeight = 0;
		toolbarGridLayout.horizontalSpacing = 0;

		branchComp.setLayout(toolbarGridLayout);
		createGitBranchCombo(branchComp);
	}

	private void createGitBranchCombo(Composite parent)
	{
		leftLabel = new Label(parent, SWT.NONE);
		leftLabel.setText("["); //$NON-NLS-1$
		leftLabelGridData = new GridData(SWT.END, SWT.CENTER, false, false);
		leftLabel.setLayoutData(leftLabelGridData);

		branchesToolbar = new ToolBar(parent, SWT.FLAT);
		branchesToolbarGridData = new GridData(SWT.BEGINNING, SWT.CENTER, false, false);
		branchesToolbar.setLayoutData(branchesToolbarGridData);

		branchesToolItem = new ToolItem(branchesToolbar, SWT.DROP_DOWN);

		branchesMenu = new Menu(branchesToolbar);
		branchesToolItem.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent selectionEvent)
			{
				Point toolbarLocation = branchesToolbar.getLocation();
				toolbarLocation = branchesToolbar.getParent().toDisplay(toolbarLocation.x, toolbarLocation.y);
				Point toolbarSize = branchesToolbar.getSize();
				branchesMenu.setLocation(toolbarLocation.x, toolbarLocation.y + toolbarSize.y + 2);
				branchesMenu.setVisible(true);
			}
		});

		rightLabel = new Label(parent, SWT.NONE);
		rightLabel.setText("]"); //$NON-NLS-1$
		rightLabelGridData = new GridData(SWT.BEGINNING, SWT.CENTER, false, false);
		rightLabel.setLayoutData(rightLabelGridData);
	}

	@Override
	public void dispose()
	{
		if (selectedProject != null)
		{
			GitRepository repo = getGitRepositoryManager().getAttached(selectedProject);
			if (repo != null)
				repo.removeListener(this);
		}
		getGitRepositoryManager().removeListener(this);

		if (pullCalc != null)
		{
			pullCalc.cancel();
			pullCalc = null;
		}
		branchToPullIndicator = null;
		if (refreshUIJob != null)
		{
			refreshUIJob.cancel();
			refreshUIJob = null;
		}
		super.dispose();
	}

	@Override
	protected void removeFilter()
	{
		if (fChangedFilesFilter != null)
		{
			getCommonViewer().removeFilter(fChangedFilesFilter);
			fChangedFilesFilter = null;
			fChangedFilesFilterProjects.remove(selectedProject);
		}
		super.removeFilter();
	}

	private boolean setNewBranch(String branchName)
	{
		// Strip off the indicators...
		branchName = stripIndicators(branchName);

		final GitRepository repo = getGitRepositoryManager().getAttached(selectedProject);
		if (repo == null)
			return false;
		if (branchName.equals(repo.currentBranch()))
			return false;

		// If user selected "Create New..." then pop a dialog to generate a new branch
		if (branchName.equals(CREATE_NEW_BRANCH_TEXT))
		{
			CreateBranchDialog dialog = new CreateBranchDialog(getSite().getShell(), repo);
			if (dialog.open() != Window.OK)
			{
				revertToCurrentBranch(repo);
				return false;
			}
			branchName = dialog.getValue().trim();
			boolean track = dialog.track();
			String startPoint = dialog.getStartPoint();
			if (!repo.createBranch(branchName, track, startPoint))
			{
				revertToCurrentBranch(repo);
				return false;
			}
		}
		IStatus switchStatus = repo.switchBranch(branchName, new NullProgressMonitor());
		if (switchStatus.isOK())
		{
			refreshViewer(); // might be new file structure
			return true;
		}
		String msg = switchStatus.getMessage();
		if (switchStatus instanceof ProcessStatus)
		{
			msg = ((ProcessStatus) switchStatus).getStdErr();
		}
		MessageDialog.openError(getSite().getShell(), Messages.GitProjectView_SwitchBranchFailedTitle, msg);
		// revertToCurrentBranch(repo);
		return false;
	}

	private String stripIndicators(String branchName)
	{
		if (branchName.endsWith(" " + getRightArrowChar())) //$NON-NLS-1$
			branchName = branchName.substring(0, branchName.length() - 2);
		if (branchName.endsWith(" " + getLeftArrowChar())) //$NON-NLS-1$
			branchName = branchName.substring(0, branchName.length() - 2);
		if (branchName.endsWith(DIRTY_SUFFIX))
			branchName = branchName.substring(0, branchName.length() - 1);
		return branchName;
	}

	private void revertToCurrentBranch(final GitRepository repo)
	{
		Job job = new UIJob("Reverting back to current branch") //$NON-NLS-1$
		{

			@Override
			public IStatus runInUIThread(IProgressMonitor monitor)
			{
				String currentBranchName = repo.currentBranch();
				if (repo.isDirty())
					currentBranchName += DIRTY_SUFFIX;
				branchesToolItem.setText(currentBranchName);
				MenuItem[] menuItems = branchesMenu.getItems();
				for (MenuItem menuItem : menuItems)
				{
					menuItem.setSelection(menuItem.getText().equals(currentBranchName));
				}
				branchesToolbar.pack(true);
				return Status.OK_STATUS;
			}
		};
		EclipseUtil.setSystemForJob(job);
		job.setPriority(Job.INTERACTIVE);
		job.schedule();
	}

	@Override
	protected void projectChanged(IProject oldProject, IProject newProject)
	{
		GitRepository oldRepo = getGitRepositoryManager().getAttached(oldProject);
		if (oldRepo != null)
		{
			oldRepo.removeListener(this);
		}

		super.projectChanged(oldProject, newProject);

		if (fChangedFilesFilterProjects.contains(newProject))
		{
			addGitChangedFilesFilter();
		}
		else
		{
			removeFilter();
		}
		GitRepository repo = getGitRepositoryManager().getAttached(newProject);
		refreshUI(repo);
		if (repo != null)
		{
			repo.addListener(this);
		}
	}

	private void populateBranches(GitRepository repo, IProgressMonitor monitor) throws CoreException
	{
		if (monitor != null && monitor.isCanceled())
			throw new CoreException(Status.CANCEL_STATUS);

		MenuItem[] menuItems = branchesMenu.getItems();
		for (MenuItem menuItem : menuItems)
		{
			menuItem.dispose();
		}
		branchesToolItem.setText(""); //$NON-NLS-1$
		if (repo == null)
			return;

		if (monitor != null && monitor.isCanceled())
			throw new CoreException(Status.CANCEL_STATUS);

		String currentBranchName = repo.currentBranch();
		String tooltip = currentBranchName;
		for (String branchName : repo.localBranches())
		{
			// Construct the menu item to for this project
			final MenuItem branchNameMenuItem = new MenuItem(branchesMenu, SWT.RADIO);
			String modifiedBranchName = branchName;
			if (branchName.equals(currentBranchName) && repo.isDirty())
			{
				modifiedBranchName += DIRTY_SUFFIX;
				tooltip += Messages.GitProjectView_BranchDirtyTooltipMessage;
			}

			synchronized (branchToPullIndicator)
			{
				if (branchToPullIndicator.containsKey(branchName) && branchToPullIndicator.get(branchName))
				{
					modifiedBranchName += " " + getLeftArrowChar(); //$NON-NLS-1$
					if (branchName.equals(currentBranchName))
						tooltip += Messages.GitProjectView_PullChangesTooltipMessage;
				}
			}
			String[] ahead = repo.commitsAhead(branchName);
			if (ahead != null && ahead.length > 0)
			{
				modifiedBranchName += " " + getRightArrowChar(); //$NON-NLS-1$
				if (branchName.equals(currentBranchName))
					tooltip += Messages.GitProjectView_PushChangesTooltipMessage;
			}

			branchNameMenuItem.setText(modifiedBranchName);
			branchNameMenuItem.setSelection(branchName.equals(currentBranchName));
			branchNameMenuItem.addSelectionListener(new SelectionAdapter()
			{
				public void widgetSelected(SelectionEvent e)
				{
					setNewBranch(branchNameMenuItem.getText());
				}
			});
			if (branchName.equals(currentBranchName))
			{
				if (modifiedBranchName.length() > 20)
				{
					// truncate the current branch name and append the indicators
					if (!currentBranchName.equals(modifiedBranchName))
					{
						currentBranchName = currentBranchName.substring(0, 15) + "..." //$NON-NLS-1$
								+ modifiedBranchName.substring(currentBranchName.length());
					}
					else
					{
						currentBranchName = currentBranchName.substring(0, 15) + "..."; //$NON-NLS-1$
					}
				}
				else
				{
					currentBranchName = modifiedBranchName;
				}
			}
		}

		if (monitor != null && monitor.isCanceled())
			throw new CoreException(Status.CANCEL_STATUS);

		new MenuItem(branchesMenu, SWT.SEPARATOR);

		final MenuItem branchNameMenuItem = new MenuItem(branchesMenu, SWT.PUSH);
		branchNameMenuItem.setText(CREATE_NEW_BRANCH_TEXT);
		branchNameMenuItem.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e)
			{
				setNewBranch(branchNameMenuItem.getText());
			}
		});
		branchesToolItem.setText(currentBranchName);
		if (tooltip != null)
		{
			branchesToolItem.setToolTipText(tooltip);
		}
		branchesToolbar.pack();
	}

	private String getRightArrowChar()
	{
		if (Platform.getOS().equals(Platform.OS_MACOSX))
		{
			return "\u2192"; //$NON-NLS-1$
		}
		return "->"; //$NON-NLS-1$
	}

	private String getLeftArrowChar()
	{
		if (Platform.getOS().equals(Platform.OS_MACOSX))
		{
			return "\u2190"; //$NON-NLS-1$
		}
		return "<-"; //$NON-NLS-1$
	}

	public void indexChanged(final IndexChangedEvent e)
	{
		// no op
	}

	public void pulled(final PullEvent e)
	{
		if (isCurrentProjectsRepository(e.getRepository()))
		{
			// When user has done a pull we need to force a recalc of the pull indicators right away!
			pullCalc.cancel();
			pullCalc.schedule();
		}
	}

	public void pushed(PushEvent e)
	{
		// Need to recalc the push indicators on branch pulldown
		handleBranchEvent(e.getRepository());
	}

	private void refreshUI(final GitRepository repository)
	{
		// If we get multiple requests queuing up, just cancel current and reschedule
		if (refreshUIJob != null)
			refreshUIJob.cancel();
		refreshUIJob = new UIJob("update UI for index changes") //$NON-NLS-1$
		{

			@Override
			public IStatus runInUIThread(IProgressMonitor monitor)
			{
				if (monitor != null && monitor.isCanceled())
					return Status.CANCEL_STATUS;
				try
				{
					// Update the branch list so we can reset the dirty status on the branch
					populateBranches(repository, monitor);
					if (monitor != null && monitor.isCanceled())
						return Status.CANCEL_STATUS;
					if (repository == null)
					{
						RowData rd = new RowData();
						rd.exclude = true;
						branchesToolbar.getParent().setLayoutData(rd);
						branchesToolbar.getParent().setVisible(false);
					}
					else
					{
						RowData rd = new RowData();
						rd.exclude = false;
						branchesToolbar.getParent().setLayoutData(rd);
						branchesToolbar.getParent().setVisible(true);
					}
					if (monitor != null && monitor.isCanceled())
						return Status.CANCEL_STATUS;
					branchesToolbar.getParent().getParent().layout(true, true);
					refreshViewer();
					return Status.OK_STATUS;
				}
				catch (CoreException e)
				{
					ExplorerPlugin.logError(e);
					return e.getStatus();
				}
				catch (Exception e)
				{
					IdeLog.logError(ExplorerPlugin.getDefault(), e, IDebugScopes.DEBUG);
					return new Status(IStatus.ERROR, ExplorerPlugin.PLUGIN_ID, e.getMessage(), e);
				}
			}
		};
		EclipseUtil.setSystemForJob(refreshUIJob);
		refreshUIJob.setPriority(Job.INTERACTIVE);
		refreshUIJob.schedule(100);
	}

	public void repositoryAdded(RepositoryAddedEvent e)
	{
		IProject changed = e.getProject();
		if (changed != null && changed.equals(selectedProject))
		{
			refreshUI(e.getRepository());
			e.getRepository().addListener(this);
		}
	}

	public void branchChanged(BranchChangedEvent e)
	{
		handleBranchEvent(e.getRepository());
	}

	public void branchAdded(BranchAddedEvent e)
	{
		handleBranchEvent(e.getRepository());
	}

	public void branchRemoved(BranchRemovedEvent e)
	{
		handleBranchEvent(e.getRepository());
	}

	private void handleBranchEvent(GitRepository repo)
	{
		if (isCurrentProjectsRepository(repo))
			refreshUI(repo);
	}

	private boolean isCurrentProjectsRepository(GitRepository repo)
	{
		GitRepository selectedRepo = getGitRepositoryManager().getAttached(selectedProject);
		return selectedRepo != null && selectedRepo.equals(repo);
	}

	public void repositoryRemoved(RepositoryRemovedEvent e)
	{
		IProject changed = e.getProject();
		if (changed != null && changed.equals(selectedProject))
		{
			e.getRepository().removeListener(this);
			refreshUI(null);
		}
	}

	private void addGitChangedFilesFilter()
	{
		removeFilter();
		fChangedFilesFilter = new GitChangedFilesFilter();
		getCommonViewer().addFilter(fChangedFilesFilter);
		getCommonViewer().expandAll();
		showFilterLabel(ExplorerPlugin.getImage(CHANGED_FILE_FILTER_ICON_PATH),
				Messages.GitProjectView_ChangedFilesFilterTooltip);
		fChangedFilesFilterProjects.add(selectedProject);
	}

	@Override
	public void saveState(IMemento aMemento)
	{
		StringBuilder text = new StringBuilder();
		for (IProject project : fChangedFilesFilterProjects)
		{
			text.append(project.getName()).append(PROJECT_DELIMITER);
		}
		int length = text.length();
		if (length > 0)
		{
			text.delete(length - PROJECT_DELIMITER.length(), length);
		}
		aMemento.putString(GIT_CHANGED_FILES_FILTER, text.toString());
		super.saveState(aMemento);
	}

	/**
	 * <p>
	 * Note: This method is for internal use only. Clients should not call this method.
	 * </p>
	 * 
	 * @see org.eclipse.ui.part.ViewPart#init(org.eclipse.ui.IViewSite, org.eclipse.ui.IMemento)
	 */
	public void init(IViewSite aSite, IMemento aMemento) throws PartInitException
	{
		super.init(aSite, aMemento);
		memento = aMemento;
		if (memento != null)
		{
			String filteredProjects = memento.getString(GIT_CHANGED_FILES_FILTER);
			if (filteredProjects != null && filteredProjects.length() > 0)
			{
				String[] projectNames = filteredProjects.split(PROJECT_DELIMITER);
				IWorkspaceRoot workspaceRoot = ResourcesPlugin.getWorkspace().getRoot();
				IProject project;
				for (String name : projectNames)
				{
					try
					{
						project = workspaceRoot.getProject(name);
						if (project.exists())
						{
							fChangedFilesFilterProjects.add(project);
						}
					}
					catch (Exception e)
					{
						IdeLog.logError(ExplorerPlugin.getDefault(), e, IDebugScopes.DEBUG);
					}
				}
			}
		}
	}

	@Override
	protected void mangleContextMenu(Menu menu)
	{
		GitRepository repo = getGitRepositoryManager().getAttached(selectedProject);
		// Remove Team menu if project is attached to our git provider.
		if (repo != null || selectedProject == null || !selectedProject.isAccessible())
		{
			Set<String> toRemove = new HashSet<String>();
			toRemove.add(TEAM_MAIN);
			removeMenuItems(menu, toRemove);
		}
		else
		{
			RepositoryProvider provider = RepositoryProvider.getProvider(selectedProject);
			if (provider == null)
			{
				// no other provider, keep Team menu, but modify it's submenu
				MenuItem[] menuItems = menu.getItems();
				for (int i = 0; i < menuItems.length; i++)
				{
					MenuItem menuItem = menuItems[i];
					Object data = menuItem.getData();
					if (data instanceof IContributionItem)
					{
						IContributionItem contrib = (IContributionItem) data;
						// Just replace team with initialize git repo
						if (TEAM_MAIN.equals(contrib.getId()))
						{
							addAttachItem(menu, i + 1);
							break;
						}
					}
				}
				Set<String> toRemove = new HashSet<String>();
				toRemove.add(TEAM_MAIN);
				removeMenuItems(menu, toRemove);
			}
		}
		// Remove all the other stuff we normally do...
		super.mangleContextMenu(menu);
	}

	private void addAttachItem(Menu menu, int index)
	{
		// FIXME Move this constant into an interface on Git UI plugin!
		final String commandId = "com.aptana.git.ui.command.attach"; //$NON-NLS-1$
		final MenuItem customizeMenuItem = new MenuItem(menu, SWT.PUSH, index);
		ICommandService commandService = (ICommandService) PlatformUI.getWorkbench().getService(ICommandService.class);
		Command c = commandService.getCommand(commandId);
		try
		{
			customizeMenuItem.setText(c.getName());
		}
		catch (NotDefinedException e2)
		{
			// ignore
		}
		customizeMenuItem.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e)
			{
				IHandlerService handlerService = (IHandlerService) PlatformUI.getWorkbench().getService(
						IHandlerService.class);
				try
				{
					handlerService.executeCommand(commandId, null);
				}
				catch (ExecutionException e1)
				{
				}
				catch (NotDefinedException e1)
				{
				}
				catch (NotEnabledException e1)
				{
				}
				catch (NotHandledException e1)
				{
				}
			}
		});
	}

	private IGitRepositoryManager getGitRepositoryManager()
	{
		return GitPlugin.getDefault().getGitRepositoryManager();
	}

	public void toggleChangedFilesFilter()
	{
		if (fChangedFilesFilter == null)
		{
			addGitChangedFilesFilter();
		}
		else
		{
			removeFilter();
		}
	}
}
