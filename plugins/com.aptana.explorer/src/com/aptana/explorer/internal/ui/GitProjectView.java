package com.aptana.explorer.internal.ui;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.ContributionItem;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.progress.UIJob;

import com.aptana.explorer.ExplorerPlugin;
import com.aptana.git.core.model.BranchChangedEvent;
import com.aptana.git.core.model.GitRepository;
import com.aptana.git.core.model.IGitRepositoryListener;
import com.aptana.git.core.model.IndexChangedEvent;
import com.aptana.git.core.model.RepositoryAddedEvent;
import com.aptana.git.core.model.RepositoryRemovedEvent;
import com.aptana.git.ui.actions.CommitAction;
import com.aptana.git.ui.actions.GithubNetworkAction;
import com.aptana.git.ui.actions.PullAction;
import com.aptana.git.ui.actions.PushAction;
import com.aptana.git.ui.actions.StashAction;
import com.aptana.git.ui.actions.UnstashAction;
import com.aptana.git.ui.dialogs.CreateBranchDialog;

/**
 * Adds Git UI elements to the Single Project View.
 *
 * @author cwilliams
 */
public class GitProjectView extends SingleProjectView implements IGitRepositoryListener
{
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

	@Override
	public void createPartControl(Composite aParent)
	{
		super.createPartControl(aParent);

		GitRepository.addListener(this);
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
				branchesMenu.setLocation(toolbarLocation.x, toolbarLocation.y
						+ toolbarSize.y + 2);
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
		menuManager.add(new ContributionItem()
		{
			@Override
			public void fill(Menu menu, int index)
			{
				if (selectedProject != null)
				{
					GitRepository repository = GitRepository.getAttached(selectedProject);
					new MenuItem(menu, SWT.SEPARATOR);
					if (repository == null)
					{
						createRepoMenuItem(menu);
					}
					else
					{
						createCommitMenuItem(menu);
						String[] commitsAhead = repository.commitsAhead(repository.currentBranch());
						if (commitsAhead != null && commitsAhead.length > 0)
						{
							createPushMenuItem(menu);
						}
						if (repository.trackingRemote(repository.currentBranch()))
						{
							createPullMenuItem(menu);
						}
						createStashMenuItem(menu);
						createUnstashMenuItem(menu);
					}
				}
			}

			@Override
			public boolean isDynamic()
			{
				return true;
			}
		});

		super.fillCommandsMenu(menuManager);

		menuManager.add(new ContributionItem()
		{
			@Override
			public void fill(Menu menu, int index)
			{
				if (selectedProject != null)
				{
					GitRepository repository = GitRepository.getAttached(selectedProject);
					if (repository != null)
					{
						// Switch to branch
						MenuItem branchesMenuItem = new MenuItem(menu, SWT.CASCADE);
						branchesMenuItem.setText(Messages.GitProjectView_SwitchToBranch);

						Menu branchesMenu = new Menu(menu);
						String currentBranchName = repository.currentBranch();
						for (String branchName : repository.localBranches())
						{
							// Construct the menu item to for this branch
							final MenuItem branchNameMenuItem = new MenuItem(branchesMenu, SWT.RADIO);
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
						branchesMenuItem.setMenu(branchesMenu);

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

						// TODO Merge Branch

						new MenuItem(menu, SWT.SEPARATOR);

						createFilterMenuItem(menu);

						// TODO Show History

						createShowGithubNetworkMenuItem(menu);
					}
				}
			}

			@Override
			public boolean isDynamic()
			{
				return true;
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
		gitFilter.setSelection(fChangedFilesFilter != null);
		gitFilter.setText(Messages.GitProjectView_ChangedFilesFilterTooltip);
		gitFilter.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				if (fChangedFilesFilter == null)
				{
					fChangedFilesFilter = new GitChangedFilesFilter();
					getCommonViewer().addFilter(fChangedFilesFilter);
					getCommonViewer().expandAll();
					showFilterLabel(ExplorerPlugin.getImage("icons/full/elcl16/filter.png"), Messages.GitProjectView_ChangedFilesFilterTooltip); //$NON-NLS-1$
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

	private void createRepoMenuItem(Menu menu)
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

	private void createCommitMenuItem(Menu menu)
	{
		MenuItem commit = new MenuItem(menu, SWT.PUSH);
		commit.setImage(ExplorerPlugin.getImage("icons/full/elcl16/disk.png")); //$NON-NLS-1$
		commit.setText(Messages.GitProjectView_CommitTooltip);
		commit.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				CommitAction action = new CommitAction();
				ISelection selection = new StructuredSelection(selectedProject);
				action.selectionChanged(null, selection);
				action.run();
			}
		});
	}

	private void createPushMenuItem(Menu menu)
	{
		MenuItem push = new MenuItem(menu, SWT.PUSH);
		push.setImage(ExplorerPlugin.getImage("icons/full/elcl16/arrow_right.png")); //$NON-NLS-1$
		push.setText(Messages.GitProjectView_PushTooltip);
		push.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				final PushAction action = new PushAction();
				action.selectionChanged(null, new StructuredSelection(selectedProject));
				Job job = new Job(Messages.GitProjectView_PushJobTitle)
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

	private void createPullMenuItem(Menu menu)
	{
		MenuItem pull = new MenuItem(menu, SWT.PUSH);
		pull.setImage(ExplorerPlugin.getImage("icons/full/elcl16/arrow_left.png")); //$NON-NLS-1$
		pull.setText(Messages.GitProjectView_PullTooltip);
		pull.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				final PullAction action = new PullAction();
				action.selectionChanged(null, new StructuredSelection(selectedProject));
				Job job = new Job(Messages.GitProjectView_PullJobTitle)
				{

					@Override
					protected IStatus run(IProgressMonitor monitor)
					{
						action.run();
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
		stash.setImage(ExplorerPlugin.getImage("icons/full/elcl16/arrow_down.png")); //$NON-NLS-1$
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
		unstash.setImage(ExplorerPlugin.getImage("icons/full/elcl16/arrow_up.png")); //$NON-NLS-1$
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
			refreshViewer();
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
		// FIXME This doesn't seem to indicate proper dirty status and changed files on initial load!
		String currentBranchName = repo.currentBranch();
		for (String branchName : repo.localBranches())
		{
			// Construct the menu item to for this project
			final MenuItem branchNameMenuItem = new MenuItem(branchesMenu, SWT.RADIO);
			if (branchName.equals(currentBranchName) && repo.isDirty())
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

		if (repo.isDirty())
			currentBranchName += "*"; //$NON-NLS-1$
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
					repository.commitsAhead(repository.currentBranch());
					leftLabelGridData.exclude = false;
					leftLabel.setVisible(true);
					branchesToolbarGridData.exclude = false;
					branchesToolbar.setVisible(true);
					rightLabelGridData.exclude = false;
					rightLabel.setVisible(true);
				}
				branchesToolbar.getParent().layout();
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
}
