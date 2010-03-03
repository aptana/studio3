package com.aptana.explorer.internal.ui;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ContributionItem;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.search.ui.IContextMenuConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.progress.UIJob;

import com.aptana.explorer.ExplorerPlugin;
import com.aptana.git.core.model.BranchChangedEvent;
import com.aptana.git.core.model.ChangedFile;
import com.aptana.git.core.model.GitRepository;
import com.aptana.git.core.model.IGitRepositoryListener;
import com.aptana.git.core.model.IndexChangedEvent;
import com.aptana.git.core.model.RepositoryAddedEvent;
import com.aptana.git.core.model.RepositoryRemovedEvent;
import com.aptana.git.ui.DiffFormatter;
import com.aptana.git.ui.actions.AddRemoteAction;
import com.aptana.git.ui.actions.CommitAction;
import com.aptana.git.ui.actions.DeleteBranchAction;
import com.aptana.git.ui.actions.DisconnectAction;
import com.aptana.git.ui.actions.GitAction;
import com.aptana.git.ui.actions.GithubNetworkAction;
import com.aptana.git.ui.actions.MergeBranchAction;
import com.aptana.git.ui.actions.PullAction;
import com.aptana.git.ui.actions.PushAction;
import com.aptana.git.ui.actions.RevertAction;
import com.aptana.git.ui.actions.ShowResourceInHistoryAction;
import com.aptana.git.ui.actions.StageAction;
import com.aptana.git.ui.actions.StashAction;
import com.aptana.git.ui.actions.StatusAction;
import com.aptana.git.ui.actions.UnstageAction;
import com.aptana.git.ui.actions.UnstashAction;
import com.aptana.git.ui.dialogs.CreateBranchDialog;

/**
 * Adds Git UI elements to the Single Project View.
 * 
 * @author cwilliams
 */
class GitProjectView extends SingleProjectView implements IGitRepositoryListener
{
	private static final String GIT_CHANGED_FILES_FILTER = "GitChangedFilesFilterEnabled"; //$NON-NLS-1$
	private static final String COMMIT_ICON_PATH = "icons/full/elcl16/disk.png"; //$NON-NLS-1$
	private static final String PUSH_ICON_PATH = "icons/full/elcl16/arrow_right.png"; //$NON-NLS-1$
	private static final String PULL_ICON_PATH = "icons/full/elcl16/arrow_left.png"; //$NON-NLS-1$
	private static final String STASH_ICON_PATH = "icons/full/elcl16/arrow_down.png"; //$NON-NLS-1$
	private static final String UNSTASH_ICON_PATH = "icons/full/elcl16/arrow_up.png"; //$NON-NLS-1$
	private static final String CHAGED_FILE_FILTER_ICON_PATH = "icons/full/elcl16/filter.png"; //$NON-NLS-1$

	private static final String CREATE_NEW_BRANCH_TEXT = Messages.GitProjectView_createNewBranchOption;

	/**
	 * Group names for git sections of gear menu
	 */
	private static final String GROUP_GIT_SINGLE_FILES = "group.git.files"; //$NON-NLS-1$
	private static final String GROUP_GIT_PROJECTS = "group.git.project"; //$NON-NLS-1$
	private static final String GROUP_GIT_BRANCHING = "group.git.branching"; //$NON-NLS-1$

	private Label leftLabel;
	private GridData leftLabelGridData;
	private Label rightLabel;
	private GridData rightLabelGridData;
	private ToolBar branchesToolbar;
	private GridData branchesToolbarGridData;

	private ToolItem branchesToolItem;
	private Menu branchesMenu;

	private GitChangedFilesFilter fChangedFilesFilter;
	private boolean filterOnInitially;

	@Override
	public void createPartControl(Composite aParent)
	{
		super.createPartControl(aParent);

		GitRepository.addListener(this);

		if (filterOnInitially)
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
			job.setSystem(true);
			job.setPriority(Job.SHORT);
			job.schedule(300);
		}
	}

	protected void doCreateToolbar(Composite toolbarComposite)
	{
		createGitBranchCombo(toolbarComposite);
	}

	private void createGitBranchCombo(Composite parent)
	{
		// Increment number of columns of the layout
		((GridLayout) parent.getLayout()).numColumns += 3;

		leftLabel = new Label(parent, SWT.NONE);
		leftLabel.setText("["); //$NON-NLS-1$
		leftLabelGridData = new GridData(SWT.BEGINNING, SWT.CENTER, false, false);
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
	protected void fillCommandsMenu(MenuManager menuManager)
	{
		super.fillCommandsMenu(menuManager);

		// Set up Git groups
		menuManager.insertBefore(IContextMenuConstants.GROUP_ADDITIONS, new Separator(GROUP_GIT_SINGLE_FILES));
		menuManager.insertAfter(GROUP_GIT_SINGLE_FILES, new Separator(GROUP_GIT_PROJECTS));
		menuManager.insertAfter(GROUP_GIT_PROJECTS, new Separator(GROUP_GIT_BRANCHING));

		// Add git filter to filtering group
		menuManager.appendToGroup(IContextMenuConstants.GROUP_FILTERING, new ContributionItem()
		{
			@Override
			public void fill(Menu menu, int index)
			{
				if (selectedProject == null || !selectedProject.exists())
					return;
				GitRepository repository = GitRepository.getAttached(selectedProject);
				if (repository != null)
				{
					createFilterMenuItem(menu);
				}
			}
		});

		// Add the git file actions
		menuManager.appendToGroup(GROUP_GIT_SINGLE_FILES, new ContributionItem()
		{
			@Override
			public void fill(Menu menu, int index)
			{
				if (selectedProject == null || !selectedProject.exists())
					return;
				GitRepository repository = GitRepository.getAttached(selectedProject);
				if (repository == null)
				{
					return;
				}
				createStageMenuItem(menu);
				createUnstageMenuItem(menu);
				createDiffMenuItem(menu);
				// TODO Conflicts
				createRevertMenuItem(menu);
			}
		});
		// add the git project actions
		menuManager.appendToGroup(GROUP_GIT_PROJECTS, new ContributionItem()
		{
			@Override
			public void fill(Menu menu, int index)
			{
				if (selectedProject == null || !selectedProject.exists())
					return;
				GitRepository repository = GitRepository.getAttached(selectedProject);
				if (repository == null)
				{
					createAttachRepoMenuItem(menu);
				}
				else
				{
					createCommitMenuItem(menu);
					String[] commitsAhead = repository.commitsAhead(repository.currentBranch());
					if (commitsAhead != null && commitsAhead.length > 0)
					{
						// FIXME Always add, just don't make enabled unless we've got commits?
						createPushMenuItem(menu);
					}
					if (repository.trackingRemote(repository.currentBranch()))
					{
						// FIXME Always add, just don't make enabled unless we're tracking remote?
						createPullMenuItem(menu);
					}
					createGitStatusMenuItem(menu);
				}
			}
		});
		// Add the git branching/misc items
		menuManager.appendToGroup(GROUP_GIT_BRANCHING, new ContributionItem()
		{
			@Override
			public void fill(Menu menu, int index)
			{
				if (selectedProject == null || !selectedProject.exists())
					return;
				GitRepository repository = GitRepository.getAttached(selectedProject);
				if (repository == null)
					return;

				// Branch submenu, which contains...
				MenuItem branchMenuItem = new MenuItem(menu, SWT.CASCADE);
				branchMenuItem.setText(Messages.GitProjectView_BranchSubmenuLabel);
				Menu branchSubMenu = new Menu(menu);
				addSwitchBranchSubMenu(repository, branchSubMenu);
				addMergeBranchSubMenu(repository, branchSubMenu);
				addCreateBranchMenuItem(branchSubMenu);
				addDeleteBranchSubMenu(repository, branchSubMenu);
				branchMenuItem.setMenu(branchSubMenu);

				// More submenu, which contains...
				MenuItem moreMenuItem = new MenuItem(menu, SWT.CASCADE);
				moreMenuItem.setText(Messages.GitProjectView_MoreSubmenuLabel);
				Menu moreSubMenu = new Menu(menu);
				createStashMenuItem(moreSubMenu);
				createUnstashMenuItem(moreSubMenu);
				createAddRemoteMenuItem(moreSubMenu);
				createShowInHistoryMenuItem(moreSubMenu);
				createShowGithubNetworkMenuItem(moreSubMenu);
				createDisconnectMenuItem(moreSubMenu);
				moreMenuItem.setMenu(moreSubMenu);
			}
		});
	}

	@Override
	public void dispose()
	{
		GitRepository.removeListener(this);
		super.dispose();
	}

	private void createFilterMenuItem(Menu menu)
	{
		MenuItem gitFilter = new MenuItem(menu, SWT.CHECK);
		gitFilter.setImage(ExplorerPlugin.getImage(CHAGED_FILE_FILTER_ICON_PATH));
		gitFilter.setSelection(fChangedFilesFilter != null);
		gitFilter.setText(Messages.GitProjectView_ChangedFilesFilterTooltip);
		gitFilter.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
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
		});
	}

	@Override
	protected void removeFilter()
	{
		if (fChangedFilesFilter != null)
		{
			getCommonViewer().removeFilter(fChangedFilesFilter);
			fChangedFilesFilter = null;
		}
		super.removeFilter();
	}

	private void createAttachRepoMenuItem(Menu menu)
	{
		MenuItem createRepo = new MenuItem(menu, SWT.PUSH);
		createRepo.setText(Messages.GitProjectView_AttachGitRepo_button);
		createRepo.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				Job job = new Job(Messages.GitProjectView_AttachGitRepo_jobTitle)
				{
					@Override
					protected IStatus run(IProgressMonitor monitor)
					{
						SubMonitor sub = SubMonitor.convert(monitor, 100);
						try
						{
							GitRepository repo = GitRepository.getUnattachedExisting(selectedProject.getLocationURI());
							if (repo == null)
							{
								if (sub.isCanceled())
									return Status.CANCEL_STATUS;
								GitRepository.create(selectedProject.getLocationURI().getPath());
							}
							sub.worked(50);
							if (sub.isCanceled())
								return Status.CANCEL_STATUS;
							GitRepository.attachExisting(selectedProject, sub.newChild(50));
						}
						catch (CoreException e)
						{
							return e.getStatus();
						}
						finally
						{
							sub.done();
						}
						return Status.OK_STATUS;
					}
				};
				job.setUser(true);
				job.setPriority(Job.LONG);
				job.schedule();
			}
		});
	}

	private void createDiffMenuItem(Menu menu)
	{
		MenuItem commit = new MenuItem(menu, SWT.PUSH);
		commit.setText(Messages.GitProjectView_DiffTooltip);
		commit.setEnabled(GitRepository.getAttached(selectedProject) != null && !getSelectedFiles().isEmpty());
		commit.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				GitRepository repo = GitRepository.getAttached(selectedProject);
				if (repo == null)
					return;

				Map<String, String> diffs = new HashMap<String, String>();
				Set<IResource> resources = getSelectedFiles();
				for (IResource resource : resources)
				{
					ChangedFile file = repo.getChangedFileForResource(resource);
					if (file == null)
						continue;

					String diff = repo.index().diffForFile(file, file.hasStagedChanges(), 3);
					diffs.put(file.getPath(), diff);
				}
				if (diffs.isEmpty())
					return;
				String diff = ""; //$NON-NLS-1$
				try
				{
					diff = DiffFormatter.toHTML(diffs);
				}
				catch (Throwable t)
				{
					ExplorerPlugin.logError("Failed to turn diff into HTML", t); //$NON-NLS-1$
				}
				final String finalDiff = diff;
				MessageDialog dialog = new MessageDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow()
						.getShell(), Messages.GitProjectView_GitDiffDialogTitle, null,
						"", 0, new String[] { IDialogConstants.OK_LABEL }, 0) //$NON-NLS-1$
				{
					@Override
					protected Control createCustomArea(Composite parent)
					{
						Browser diffArea = new Browser(parent, SWT.BORDER);
						GridData data = new GridData(SWT.FILL, SWT.FILL, true, true);
						data.heightHint = 300;
						diffArea.setLayoutData(data);
						diffArea.setText(finalDiff);
						return diffArea;
					}

					@Override
					protected boolean isResizable()
					{
						return true;
					}
				};
				dialog.open();
			}
		});
	}

	private void createRevertMenuItem(Menu menu)
	{
		createSimpleGitAction(menu, getSelectedUnstagedFiles(), Messages.GitProjectView_RevertTooltip,
				Messages.GitProjectView_RevertJobTitle, new RevertAction());
	}

	private void createStageMenuItem(Menu menu)
	{
		createSimpleGitAction(menu, getSelectedUnstagedFiles(), Messages.GitProjectView_StageTooltip,
				Messages.GitProjectView_StageJobTitle, new StageAction());
	}

	private void createUnstageMenuItem(Menu menu)
	{
		createSimpleGitAction(menu, getSelectedStagedFiles(), Messages.GitProjectView_UnstageTooltip,
				Messages.GitProjectView_UnstageJobTitle, new UnstageAction());
	}

	protected Set<IResource> getSelectedStagedFiles()
	{
		// Limit to only those changed files which have staged changes
		GitRepository repo = GitRepository.getAttached(selectedProject);
		final Set<IResource> selected = new HashSet<IResource>();
		if (repo != null)
		{
			for (IResource resource : getSelectedFiles())
			{
				ChangedFile changedFile = repo.getChangedFileForResource(resource);
				if (changedFile == null)
					continue;
				if (changedFile.hasStagedChanges())
					selected.add(resource);
			}
		}
		return selected;
	}

	protected Set<IResource> getSelectedUnstagedFiles()
	{
		GitRepository repo = GitRepository.getAttached(selectedProject);
		final Set<IResource> selected = new HashSet<IResource>();
		if (repo != null)
		{
			for (IResource resource : getSelectedFiles())
			{
				ChangedFile changedFile = repo.getChangedFileForResource(resource);
				if (changedFile == null)
					continue;
				if (changedFile.hasUnstagedChanges())
					selected.add(resource);
			}
		}
		return selected;
	}

	private void createSimpleGitAction(Menu menu, final Set<IResource> selected, String tooltip, final String jobTitle,
			final GitAction action)
	{
		MenuItem stage = new MenuItem(menu, SWT.PUSH);
		stage.setText(tooltip);
		stage.setEnabled(!selected.isEmpty());
		stage.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				action.selectionChanged(null, new StructuredSelection(selected.toArray()));
				Job job = new Job(jobTitle)
				{

					@Override
					protected IStatus run(IProgressMonitor monitor)
					{
						action.run();
						refreshUI(GitRepository.getAttached(selectedProject));
						return Status.OK_STATUS;
					}
				};
				job.setUser(true);
				job.setPriority(Job.LONG);
				job.schedule();
			}
		});
	}

	private Set<IResource> getSelectedFiles()
	{
		ISelection sel = getCommonViewer().getSelection();
		Set<IResource> resources = new HashSet<IResource>();
		if (sel == null || sel.isEmpty())
			return resources;
		if (!(sel instanceof IStructuredSelection))
			return resources;
		IStructuredSelection structured = (IStructuredSelection) sel;
		for (Object element : structured.toList())
		{
			if (element == null)
				continue;

			if (element instanceof IResource)
				resources.add((IResource) element);

			if (element instanceof IAdaptable)
			{
				IAdaptable adapt = (IAdaptable) element;
				IResource resource = (IResource) adapt.getAdapter(IResource.class);
				if (resource != null)
					resources.add(resource);
			}
		}
		return resources;
	}

	private void createCommitMenuItem(Menu menu)
	{
		MenuItem commit = new MenuItem(menu, SWT.PUSH);
		commit.setImage(ExplorerPlugin.getImage(COMMIT_ICON_PATH));
		commit.setText(Messages.GitProjectView_CommitTooltip);
		commit.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				CommitAction action = new CommitAction();
				action.selectionChanged(null, new StructuredSelection(selectedProject));
				action.run();
			}
		});
	}

	private void createGitStatusMenuItem(Menu menu)
	{
		MenuItem commit = new MenuItem(menu, SWT.PUSH);
		commit.setText(Messages.GitProjectView_StatusTooltip);
		commit.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				StatusAction action = new StatusAction();
				action.selectionChanged(null, new StructuredSelection(selectedProject));
				action.run();
			}
		});
	}

	private void createAddRemoteMenuItem(Menu menu)
	{
		MenuItem commit = new MenuItem(menu, SWT.PUSH);
		commit.setText(Messages.GitProjectView_AddRemoteTooltip);
		commit.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				AddRemoteAction action = new AddRemoteAction();
				action.selectionChanged(null, new StructuredSelection(selectedProject));
				action.run();
			}
		});
	}

	private void createPushMenuItem(Menu menu)
	{
		createProjectLevelMenuItem(menu, PUSH_ICON_PATH, Messages.GitProjectView_PushTooltip,
				Messages.GitProjectView_PushJobTitle, new PushAction());
	}

	private void createPullMenuItem(Menu menu)
	{
		createProjectLevelMenuItem(menu, PULL_ICON_PATH, Messages.GitProjectView_PullTooltip,
				Messages.GitProjectView_PullJobTitle, new PullAction());
	}

	private void createProjectLevelMenuItem(Menu menu, String imagePath, String tooltip, final String jobTitle,
			final GitAction action)
	{
		MenuItem item = new MenuItem(menu, SWT.PUSH);
		if (imagePath != null)
		{
			item.setImage(ExplorerPlugin.getImage(imagePath));
		}
		item.setText(tooltip);
		item.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				action.selectionChanged(null, new StructuredSelection(selectedProject));
				Job job = new Job(jobTitle)
				{

					@Override
					protected IStatus run(IProgressMonitor monitor)
					{
						action.run();
						refreshUI(GitRepository.getAttached(selectedProject));
						return Status.OK_STATUS;
					}
				};
				job.setUser(true);
				job.setPriority(Job.LONG);
				job.schedule();
			}
		});
	}

	private void createStashMenuItem(Menu menu)
	{
		MenuItem stash = new MenuItem(menu, SWT.PUSH);
		stash.setImage(ExplorerPlugin.getImage(STASH_ICON_PATH));
		stash.setText(Messages.GitProjectView_StashTooltip);
		stash.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				final StashAction action = new StashAction();
				action.selectionChanged(null, new StructuredSelection(selectedProject));
				Job job = new Job(Messages.GitProjectView_StashJobTitle)
				{

					@Override
					protected IStatus run(IProgressMonitor monitor)
					{
						action.run();
						refreshUI(GitRepository.getAttached(selectedProject));
						return Status.OK_STATUS;
					}
				};
				job.setUser(true);
				job.setPriority(Job.LONG);
				job.schedule();
			}
		});
	}

	private void createUnstashMenuItem(Menu menu)
	{
		MenuItem unstash = new MenuItem(menu, SWT.PUSH);
		unstash.setImage(ExplorerPlugin.getImage(UNSTASH_ICON_PATH));
		unstash.setText(Messages.GitProjectView_UnstashTooltip);
		unstash.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				final UnstashAction action = new UnstashAction();
				action.selectionChanged(null, new StructuredSelection(selectedProject));
				Job job = new Job(Messages.GitProjectView_UnstashJobTitle)
				{

					@Override
					protected IStatus run(IProgressMonitor monitor)
					{
						action.run();
						refreshUI(GitRepository.getAttached(selectedProject));
						return Status.OK_STATUS;
					}
				};
				job.setUser(true);
				job.setPriority(Job.LONG);
				job.schedule();
			}
		});
	}

	private void createDisconnectMenuItem(Menu menu)
	{
		MenuItem disconnect = new MenuItem(menu, SWT.PUSH);
		disconnect.setText(Messages.GitProjectView_DisconnectTooltip);
		disconnect.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				final DisconnectAction action = new DisconnectAction();
				action.selectionChanged(null, new StructuredSelection(selectedProject));
				Job job = new Job(Messages.GitProjectView_DisconnectJobTitle)
				{

					@Override
					protected IStatus run(IProgressMonitor monitor)
					{
						action.run();
						refreshUI(null);
						return Status.OK_STATUS;
					}
				};
				job.setUser(true);
				job.setPriority(Job.LONG);
				job.schedule();
			}
		});
	}

	private void createShowInHistoryMenuItem(Menu menu)
	{
		MenuItem showGitHubNetwork = new MenuItem(menu, SWT.PUSH);
		showGitHubNetwork.setText(Messages.GitProjectView_LBL_ShowInHistory);
		showGitHubNetwork.addSelectionListener(new SelectionAdapter()
		{
			@SuppressWarnings("restriction")
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				final ShowResourceInHistoryAction action = new ShowResourceInHistoryAction();
				action.selectionChanged(null, new StructuredSelection(getSelectedFiles().toArray()));
			}
		});
	}

	private void createShowGithubNetworkMenuItem(Menu menu)
	{
		MenuItem showGitHubNetwork = new MenuItem(menu, SWT.PUSH);
		showGitHubNetwork.setText(Messages.GitProjectView_LBL_ShowGitHubNetwork);
		showGitHubNetwork.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				final GithubNetworkAction action = new GithubNetworkAction();
				action.selectionChanged(null, new StructuredSelection(selectedProject));
				Job job = new UIJob(Messages.GitProjectView_ShowGitHubNetworkJobTitle)
				{
					@Override
					public IStatus runInUIThread(IProgressMonitor monitor)
					{
						action.run();
						refreshUI(GitRepository.getAttached(selectedProject));
						return Status.OK_STATUS;
					}
				};
				job.setUser(true);
				job.setPriority(Job.LONG);
				job.schedule();
			}
		});
	}

	protected boolean setNewBranch(String branchName)
	{
		if (branchName.endsWith("*")) //$NON-NLS-1$
			branchName = branchName.substring(0, branchName.length() - 1);

		final GitRepository repo = GitRepository.getAttached(selectedProject);
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
		if (repo.switchBranch(branchName))
		{
			refreshViewer(); // might be new file structure
			return true;
		}
		revertToCurrentBranch(repo);
		return false;
	}

	private void revertToCurrentBranch(final GitRepository repo)
	{
		Job job = new UIJob("") //$NON-NLS-1$
		{

			@Override
			public IStatus runInUIThread(IProgressMonitor monitor)
			{
				String currentBranchName = repo.currentBranch();
				if (repo.isDirty())
					currentBranchName += "*"; //$NON-NLS-1$
				branchesToolItem.setText(currentBranchName);
				MenuItem[] menuItems = branchesMenu.getItems();
				for (MenuItem menuItem : menuItems)
				{
					menuItem.setSelection(menuItem.getText().equals(currentBranchName));
				}
				branchesToolbar.pack(true);
				// TODO Pop a dialog saying we couldn't branches
				return Status.OK_STATUS;
			}
		};
		job.setSystem(true);
		job.setPriority(Job.INTERACTIVE);
		job.schedule();
	}

	@Override
	protected void projectChanged(IProject oldProject, IProject newProject)
	{
		super.projectChanged(oldProject, newProject);
		if (fChangedFilesFilter != null)
		{
			removeFilter();
			addGitChangedFilesFilter();
		}
		refreshUI(GitRepository.getAttached(newProject));
	}

	private void populateBranches(GitRepository repo)
	{
		MenuItem[] menuItems = branchesMenu.getItems();
		for (MenuItem menuItem : menuItems)
		{
			menuItem.dispose();
		}
		branchesToolItem.setText(""); //$NON-NLS-1$
		if (repo == null)
			return;

		String currentBranchName = repo.currentBranch();
		for (String branchName : repo.localBranches())
		{
			// Construct the menu item to for this project
			final MenuItem branchNameMenuItem = new MenuItem(branchesMenu, SWT.RADIO);
			String modifiedBranchName = branchName;
			if (branchName.equals(currentBranchName) && repo.isDirty())
			{
				modifiedBranchName += "*"; //$NON-NLS-1$
			}
			if (repo.shouldPull(branchName))
				modifiedBranchName += " \u2190"; // left arrow //$NON-NLS-1$
			String[] ahead = repo.commitsAhead(branchName);
			if (ahead != null && ahead.length > 0)
				modifiedBranchName += " \u2192"; // right arrow //$NON-NLS-1$

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
				currentBranchName = modifiedBranchName;
		}

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
		branchesToolbar.pack();
	}

	public void indexChanged(final IndexChangedEvent e)
	{
		refreshUI(e.getRepository());
	}

	protected void refreshUI(final GitRepository repository)
	{
		Job job = new UIJob("update UI for index changes") //$NON-NLS-1$
		{

			@Override
			public IStatus runInUIThread(IProgressMonitor monitor)
			{
				// Update the branch list so we can reset the dirty status on the branch
				populateBranches(repository);
				if (repository == null)
				{
					leftLabelGridData.exclude = true;
					leftLabel.setVisible(false);
					branchesToolbarGridData.exclude = true;
					branchesToolbar.setVisible(false);
					rightLabelGridData.exclude = true;
					rightLabel.setVisible(false);
				}
				else
				{
					leftLabelGridData.exclude = false;
					leftLabel.setVisible(true);
					branchesToolbarGridData.exclude = false;
					branchesToolbar.setVisible(true);
					rightLabelGridData.exclude = false;
					rightLabel.setVisible(true);
				}
				branchesToolbar.getParent().getParent().layout(true, true);
				refreshViewer();
				return Status.OK_STATUS;
			}
		};
		job.setSystem(true);
		job.setPriority(Job.INTERACTIVE);
		job.schedule();
	}

	public void repositoryAdded(RepositoryAddedEvent e)
	{
		IProject changed = e.getProject();
		if (changed != null && changed.equals(selectedProject))
			refreshUI(e.getRepository());
	}

	public void branchChanged(BranchChangedEvent e)
	{
		GitRepository repo = e.getRepository();
		GitRepository selectedRepo = GitRepository.getAttached(selectedProject);
		if (selectedRepo != null && selectedRepo.equals(repo))
			refreshUI(e.getRepository());
	}

	public void repositoryRemoved(RepositoryRemovedEvent e)
	{
		IProject changed = e.getProject();
		if (changed != null && changed.equals(selectedProject))
			refreshUI(null);
	}

	protected void addGitChangedFilesFilter()
	{
		fChangedFilesFilter = new GitChangedFilesFilter();
		getCommonViewer().addFilter(fChangedFilesFilter);
		getCommonViewer().expandAll();
		showFilterLabel(ExplorerPlugin.getImage(CHAGED_FILE_FILTER_ICON_PATH),
				Messages.GitProjectView_ChangedFilesFilterTooltip);
	}

	@Override
	public void saveState(IMemento aMemento)
	{
		aMemento.putInteger(GIT_CHANGED_FILES_FILTER, (fChangedFilesFilter == null) ? 0 : 1);
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
			Integer gitFilterEnabled = memento.getInteger(GIT_CHANGED_FILES_FILTER);
			if (gitFilterEnabled != null && gitFilterEnabled.intValue() == 1)
			{
				filterOnInitially = true;
			}
		}
	}

	protected void addCreateBranchMenuItem(Menu menu)
	{
		// Create branch
		final MenuItem branchNameMenuItem = new MenuItem(menu, SWT.PUSH);
		branchNameMenuItem.setText(CREATE_NEW_BRANCH_TEXT);
		branchNameMenuItem.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e)
			{
				setNewBranch(branchNameMenuItem.getText());
			}
		});
	}

	protected void addSwitchBranchSubMenu(GitRepository repository, Menu menu)
	{
		// Switch to branch
		MenuItem switchBranchesMenuItem = new MenuItem(menu, SWT.CASCADE);
		switchBranchesMenuItem.setText(Messages.GitProjectView_SwitchToBranch);
		Menu switchBranchesSubMenu = new Menu(switchBranchesMenuItem);
		String currentBranchName = repository.currentBranch();
		for (String branchName : repository.localBranches())
		{
			// Construct the menu item to for this branch
			final MenuItem branchNameMenuItem = new MenuItem(switchBranchesSubMenu, SWT.RADIO);
			if (branchName.equals(currentBranchName) && repository.isDirty())
			{
				branchNameMenuItem.setText(branchName + "*"); //$NON-NLS-1$
			}
			else
			{
				branchNameMenuItem.setText(branchName);
			}
			branchNameMenuItem.setSelection(branchName.equals(currentBranchName));
			branchNameMenuItem.addSelectionListener(new SelectionAdapter()
			{
				public void widgetSelected(SelectionEvent e)
				{
					setNewBranch(branchNameMenuItem.getText());
				}
			});
		}
		// only 1 branch, no need to switch
		switchBranchesMenuItem.setEnabled(repository.localBranches().size() > 1);
		switchBranchesMenuItem.setMenu(switchBranchesSubMenu);
	}

	protected void addMergeBranchSubMenu(final GitRepository repository, Menu menu)
	{
		// Merge branch
		MenuItem mergeBranchMenuItem = new MenuItem(menu, SWT.CASCADE);
		mergeBranchMenuItem.setText(Messages.GitProjectView_MergeBranch);
		Menu mergeBranchSubmenu = new Menu(mergeBranchMenuItem);

		MergeBranchAction action = new MergeBranchAction();
		action.selectionChanged(new Action()
		{
		}, new StructuredSelection(selectedProject));
		action.fillMenu(mergeBranchSubmenu);
		mergeBranchMenuItem.setEnabled(repository.allBranches().size() > 1);
		mergeBranchMenuItem.setMenu(mergeBranchSubmenu);
	}

	protected void addDeleteBranchSubMenu(final GitRepository repository, Menu menu)
	{
		// Delete branch
		MenuItem deleteBranchMenuItem = new MenuItem(menu, SWT.CASCADE);
		deleteBranchMenuItem.setText(Messages.GitProjectView_DeleteBranch);
		Menu deleteBranchSubmenu = new Menu(deleteBranchMenuItem);
		DeleteBranchAction action = new DeleteBranchAction();
		action.selectionChanged(new Action()
		{
		}, new StructuredSelection(selectedProject));
		action.fillMenu(deleteBranchSubmenu);
		deleteBranchMenuItem.setEnabled(repository.localBranches().size() > 1);
		deleteBranchMenuItem.setMenu(deleteBranchSubmenu);
	}
}
