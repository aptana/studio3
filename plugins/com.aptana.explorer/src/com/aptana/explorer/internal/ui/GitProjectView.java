package com.aptana.explorer.internal.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ContributionItem;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IContributionItem;
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
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
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
import org.eclipse.ui.navigator.ICommonMenuConstants;
import org.eclipse.ui.progress.UIJob;

import com.aptana.explorer.ExplorerPlugin;
import com.aptana.git.core.GitPlugin;
import com.aptana.git.core.model.BranchAddedEvent;
import com.aptana.git.core.model.BranchChangedEvent;
import com.aptana.git.core.model.BranchRemovedEvent;
import com.aptana.git.core.model.ChangedFile;
import com.aptana.git.core.model.GitRepository;
import com.aptana.git.core.model.IGitRepositoriesListener;
import com.aptana.git.core.model.IGitRepositoryListener;
import com.aptana.git.core.model.IGitRepositoryManager;
import com.aptana.git.core.model.IndexChangedEvent;
import com.aptana.git.core.model.PullEvent;
import com.aptana.git.core.model.PushEvent;
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
class GitProjectView extends SingleProjectView implements IGitRepositoryListener, IGitRepositoriesListener
{
	private static final String DIRTY_SUFFIX = "*"; //$NON-NLS-1$
	private static final String GIT_CHANGED_FILES_FILTER = "GitChangedFilesFilterEnabled"; //$NON-NLS-1$
	private static final String PROJECT_DELIMITER = "######"; //$NON-NLS-1$
	private static final String COMMIT_ICON_PATH = "icons/full/elcl16/disk.png"; //$NON-NLS-1$
	private static final String PUSH_ICON_PATH = "icons/full/elcl16/arrow_right.png"; //$NON-NLS-1$
	private static final String PULL_ICON_PATH = "icons/full/elcl16/arrow_left.png"; //$NON-NLS-1$
	private static final String STASH_ICON_PATH = "icons/full/elcl16/arrow_down.png"; //$NON-NLS-1$
	private static final String UNSTASH_ICON_PATH = "icons/full/elcl16/arrow_up.png"; //$NON-NLS-1$
	private static final String CHANGED_FILE_FILTER_ICON_PATH = "icons/full/elcl16/filter.png"; //$NON-NLS-1$

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
			job.setSystem(true);
			job.setPriority(Job.SHORT);
			job.schedule(300);
		}

		// Calculate the pull indicators in a recurring background job!
		pullCalc = new Job("Calculating git pull indicators") //$NON-NLS-1$
		{

			@Override
			protected IStatus run(IProgressMonitor monitor)
			{
				if (monitor != null && monitor.isCanceled())
					return Status.CANCEL_STATUS;

				GitRepository repo = getGitRepositoryManager().getAttached(selectedProject);
				if (repo == null)
				{
					schedule(5 * 60 * 1000); // reschedule for 5 minutes after we return!
					return Status.OK_STATUS;
				}

				if (monitor != null && monitor.isCanceled())
					return Status.CANCEL_STATUS;

				synchronized (branchToPullIndicator)
				{
					branchToPullIndicator.clear();
				}
				for (String branch : repo.localBranches())
				{
					boolean shouldPull = repo.shouldPull(branch);
					synchronized (branchToPullIndicator)
					{
						branchToPullIndicator.put(branch, shouldPull);
					}
				}

				refreshUI(repo);

				if (monitor != null && monitor.isCanceled())
					return Status.CANCEL_STATUS;

				schedule(5 * 60 * 1000); // reschedule for 5 minutes after we return!
				return Status.OK_STATUS;
			}
		};
		pullCalc.setSystem(true);
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
		rightLabelGridData.horizontalIndent = -2;
		rightLabel.setLayoutData(rightLabelGridData);
	}

	@Override
	protected void fillCommandsMenu(MenuManager menuManager)
	{
		super.fillCommandsMenu(menuManager);

		if (selectedProject == null || !selectedProject.isAccessible())
			return;
		final GitRepository repository = getGitRepositoryManager().getAttached(selectedProject);
		if (repository != null)
		{
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
					createFilterMenuItem(menu);
				}
			});

			// Add the git file actions
			menuManager.appendToGroup(GROUP_GIT_SINGLE_FILES, new ContributionItem()
			{
				@Override
				public void fill(Menu menu, int index)
				{
					createStageMenuItem(menu, -1);
					createUnstageMenuItem(menu, -1);
					createDiffMenuItem(menu, -1);
					// TODO Conflicts
					createRevertMenuItem(menu, -1);
				}
			});
			// Add the git branching/misc items
			menuManager.appendToGroup(GROUP_GIT_BRANCHING, new ContributionItem()
			{
				@Override
				public void fill(Menu menu, int index)
				{
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
		else
		{
			// Set up Git groups
			menuManager.insertAfter(IContextMenuConstants.GROUP_ADDITIONS, new Separator(GROUP_GIT_PROJECTS));
		}

		// add the git project actions
		menuManager.appendToGroup(GROUP_GIT_PROJECTS, new ContributionItem()
		{
			@Override
			public void fill(Menu menu, int index)
			{
				if (repository == null)
				{
					createAttachRepoMenuItem(menu, -1);
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

		branchToPullIndicator = null;
		if (pullCalc != null)
		{
			pullCalc.cancel();
			pullCalc = null;
		}
		if (refreshUIJob != null)
		{
			refreshUIJob.cancel();
			refreshUIJob = null;
		}
		super.dispose();
	}

	private void createFilterMenuItem(Menu menu)
	{
		MenuItem gitFilter = new MenuItem(menu, SWT.CHECK);
		gitFilter.setImage(ExplorerPlugin.getImage(CHANGED_FILE_FILTER_ICON_PATH));
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
			fChangedFilesFilterProjects.remove(selectedProject);
		}
		super.removeFilter();
	}

	private void createAttachRepoMenuItem(Menu menu, int index)
	{
		if (index < 0)
		{
			index = menu.getItemCount();
		}
		MenuItem createRepo = new MenuItem(menu, SWT.PUSH, index);
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
							getGitRepositoryManager().createOrAttach(selectedProject, sub);
						}
						catch (CoreException e)
						{
							ExplorerPlugin.logError(e);
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

	private void createDiffMenuItem(Menu menu, int index)
	{
		if (index < 0)
		{
			index = menu.getItemCount();
		}
		MenuItem commit = new MenuItem(menu, SWT.PUSH, index);
		commit.setText(Messages.GitProjectView_DiffTooltip);
		commit.setEnabled(getGitRepositoryManager().getAttached(selectedProject) != null
				&& !getSelectedChangedFiles().isEmpty());
		commit.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				GitRepository repo = getGitRepositoryManager().getAttached(selectedProject);
				if (repo == null)
					return;

				Map<String, String> diffs = new HashMap<String, String>();
				List<ChangedFile> changedFiles = getSelectedChangedFiles();
				for (ChangedFile file : changedFiles)
				{
					if (file == null)
						continue;

					if (diffs.containsKey(file.getPath()))
						continue; // already calculated diff...
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

	private List<ChangedFile> getSelectedChangedFiles()
	{
		GitRepository repo = getGitRepositoryManager().getAttached(selectedProject);
		List<ChangedFile> changedFiles = new ArrayList<ChangedFile>();
		if (repo != null)
		{
			for (IResource resource : getSelectedFiles())
			{
				changedFiles.addAll(getChangedFilesForResource(repo, resource));
			}
		}
		return changedFiles;
	}

	private void createRevertMenuItem(Menu menu, int index)
	{
		createSimpleGitAction(menu, index, getSelectedUnstagedFiles(), Messages.GitProjectView_RevertTooltip,
				Messages.GitProjectView_RevertJobTitle, new RevertAction());
	}

	private void createStageMenuItem(Menu menu, int index)
	{
		createSimpleGitAction(menu, index, getSelectedUnstagedFiles(), Messages.GitProjectView_StageTooltip,
				Messages.GitProjectView_StageJobTitle, new StageAction());
	}

	private void createUnstageMenuItem(Menu menu, int index)
	{
		createSimpleGitAction(menu, index, getSelectedStagedFiles(), Messages.GitProjectView_UnstageTooltip,
				Messages.GitProjectView_UnstageJobTitle, new UnstageAction());
	}

	protected Set<IResource> getSelectedStagedFiles()
	{
		// Limit to only those changed files which have staged changes
		GitRepository repo = getGitRepositoryManager().getAttached(selectedProject);
		final Set<IResource> selected = new HashSet<IResource>();
		if (repo != null)
		{
			for (IResource resource : getSelectedFiles())
			{
				List<ChangedFile> changedFiles = getChangedFilesForResource(repo, resource);
				for (ChangedFile changedFile : changedFiles)
				{
					if (changedFile == null)
						continue;
					if (changedFile.hasStagedChanges())
					{
						selected.add(resource);
						break;
					}
				}
			}
		}
		return selected;
	}

	protected Set<IResource> getSelectedUnstagedFiles()
	{
		GitRepository repo = getGitRepositoryManager().getAttached(selectedProject);
		final Set<IResource> selected = new HashSet<IResource>();
		if (repo != null)
		{
			for (IResource resource : getSelectedFiles())
			{
				List<ChangedFile> changedFiles = getChangedFilesForResource(repo, resource);
				for (ChangedFile changedFile : changedFiles)
				{
					if (changedFile == null)
						continue;
					if (changedFile.hasUnstagedChanges())
					{
						selected.add(resource);
						break;
					}
				}
			}
		}
		return selected;
	}

	private List<ChangedFile> getChangedFilesForResource(GitRepository repo, IResource resource)
	{
		List<ChangedFile> files = new ArrayList<ChangedFile>();
		if (resource instanceof IContainer)
		{
			files.addAll(repo.getChangedFilesForContainer((IContainer) resource));
		}
		else
		{
			ChangedFile changedFile = repo.getChangedFileForResource(resource);
			if (changedFile != null)
				files.add(changedFile);
		}
		return files;
	}

	private void createSimpleGitAction(Menu menu, int index, final Set<IResource> selected, String tooltip,
			final String jobTitle, final GitAction action)
	{
		if (index < 0)
		{
			index = menu.getItemCount();
		}
		MenuItem stage = new MenuItem(menu, SWT.PUSH, index);
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
						refreshUI(getGitRepositoryManager().getAttached(selectedProject));
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
						refreshUI(getGitRepositoryManager().getAttached(selectedProject));
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
						refreshUI(getGitRepositoryManager().getAttached(selectedProject));
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
				IAction duh = new Action()
				{
				};
				action.setActivePart(duh, GitProjectView.this);
				UIJob job = new UIJob(Messages.GitProjectView_DisconnectJobTitle)
				{

					@Override
					public IStatus runInUIThread(IProgressMonitor monitor)
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
				action.run(null);
			}
		});
	}

	private void createShowGithubNetworkMenuItem(Menu menu)
	{
		MenuItem showGitHubNetwork = new MenuItem(menu, SWT.PUSH);
		showGitHubNetwork.setText(Messages.GitProjectView_LBL_ShowGitHubNetwork);
		final GithubNetworkAction action = new GithubNetworkAction();
		action.selectionChanged(null, new StructuredSelection(selectedProject));
		showGitHubNetwork.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{

				Job job = new UIJob(Messages.GitProjectView_ShowGitHubNetworkJobTitle)
				{
					@Override
					public IStatus runInUIThread(IProgressMonitor monitor)
					{
						action.run();
						refreshUI(getGitRepositoryManager().getAttached(selectedProject));
						return Status.OK_STATUS;
					}
				};
				job.setUser(true);
				job.setPriority(Job.LONG);
				job.schedule();
			}
		});
		showGitHubNetwork.setEnabled(action.isEnabled());
	}

	protected boolean setNewBranch(String branchName)
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
		if (repo.switchBranch(branchName))
		{
			refreshViewer(); // might be new file structure
			return true;
		}
		revertToCurrentBranch(repo);
		return false;
	}

	protected String stripIndicators(String branchName)
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
		Job job = new UIJob("") //$NON-NLS-1$
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

	protected String getRightArrowChar()
	{
		if (Platform.getOS().equals(Platform.OS_MACOSX))
		{
			return "\u2192"; //$NON-NLS-1$
		}
		return "->"; //$NON-NLS-1$
	}

	protected String getLeftArrowChar()
	{
		if (Platform.getOS().equals(Platform.OS_MACOSX))
		{
			return "\u2190"; //$NON-NLS-1$
		}
		return "<-"; //$NON-NLS-1$
	}

	public void indexChanged(final IndexChangedEvent e)
	{
		handleBranchEvent(e.getRepository());
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

	@Override
	public void pushed(PushEvent e)
	{
		// Need to recalc the push indicators on branch pulldown
		handleBranchEvent(e.getRepository());
	}

	protected void refreshUI(final GitRepository repository)
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
					ExplorerPlugin.logError(e.getMessage(), e);
					return new Status(IStatus.ERROR, ExplorerPlugin.PLUGIN_ID, e.getMessage(), e);
				}
			}
		};
		refreshUIJob.setSystem(true);
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

	@Override
	public void branchAdded(BranchAddedEvent e)
	{
		handleBranchEvent(e.getRepository());
	}

	@Override
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

	protected void addGitChangedFilesFilter()
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
						ExplorerPlugin.logError(e.getMessage(), e);
					}
				}
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
				branchNameMenuItem.setText(branchName + DIRTY_SUFFIX); //$NON-NLS-1$
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

	@Override
	protected void mangleContextMenu(Menu menu)
	{
		GitRepository repo = getGitRepositoryManager().getAttached(selectedProject);
		// Remove Team menu if project is attached to our git provider.
		if (repo != null || selectedProject == null || !selectedProject.isAccessible())
		{
			Set<String> toRemove = new HashSet<String>();
			toRemove.add("team.main"); //$NON-NLS-1$
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
						if ("team.main".equals(contrib.getId())) //$NON-NLS-1$
						{
							createAttachRepoMenuItem(menu, i + 1);
							break;
						}
					}
				}
				Set<String> toRemove = new HashSet<String>();
				toRemove.add("team.main"); //$NON-NLS-1$
				removeMenuItems(menu, toRemove);
			}
		}
		// Remove all the other stuff we normally do...
		super.mangleContextMenu(menu);
		if (repo == null)
			return;

		// Add the git actions Diff..., Stage/Unstage, Revert
		MenuItem additions = getAdditionsMenuItem(menu);
		int index = menu.indexOf(additions);
		if (index > 0)
			index++;
		createDiffMenuItem(menu, index++);
		createStageMenuItem(menu, index++);
		createUnstageMenuItem(menu, index++);
		createRevertMenuItem(menu, index);
	}

	private MenuItem getAdditionsMenuItem(Menu menu)
	{
		MenuItem[] items = menu.getItems();
		for (MenuItem item : items)
		{
			Object data = item.getData();
			if (data instanceof Separator)
			{
				Separator sep = (Separator) data;
				if (sep.getId().equals(ICommonMenuConstants.GROUP_ADDITIONS))
					return item;
			}
		}
		return null;
	}

	protected IGitRepositoryManager getGitRepositoryManager()
	{
		return GitPlugin.getDefault().getGitRepositoryManager();
	}
}
