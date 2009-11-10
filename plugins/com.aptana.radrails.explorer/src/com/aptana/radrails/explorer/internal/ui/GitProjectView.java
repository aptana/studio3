package com.aptana.radrails.explorer.internal.ui;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.progress.UIJob;

import com.aptana.git.core.model.BranchChangedEvent;
import com.aptana.git.core.model.ChangedFile;
import com.aptana.git.core.model.GitRepository;
import com.aptana.git.core.model.IGitRepositoryListener;
import com.aptana.git.core.model.IndexChangedEvent;
import com.aptana.git.core.model.RepositoryAddedEvent;
import com.aptana.git.core.model.RepositoryRemovedEvent;
import com.aptana.git.ui.actions.CommitAction;
import com.aptana.git.ui.actions.PullAction;
import com.aptana.git.ui.actions.PushAction;
import com.aptana.git.ui.actions.StashAction;
import com.aptana.git.ui.actions.UnstashAction;
import com.aptana.git.ui.dialogs.CreateBranchDialog;
import com.aptana.radrails.explorer.ExplorerPlugin;

/**
 * Adds Git UI elements to the Single Project View.
 * 
 * @author cwilliams
 */
public class GitProjectView extends SingleProjectView implements IGitRepositoryListener
{
	private static final String BRANCH_SEPARATOR = "--------"; //$NON-NLS-1$

	private static final String CREATE_NEW_BRANCH_TEXT = Messages.GitProjectView_createNewBranchOption;

	private Combo branchCombo;
	private StyledText summary;
	private Button pull;
	private Button push;
	private Button commit;
	private Button stash;
	private Button unstash;
	private Button gitFilter;
	private Label expandCollapse;
	private Composite gitStuff;
	private Composite gitDetails;

	private FormData showGitDetailsData;
	private FormData hideGitDetailsData;

	private Composite initGit;

	private Button createRepoButton;

	@Override
	public void createPartControl(Composite aParent)
	{
		super.createPartControl(aParent);

		GitRepository.addListener(this);

	}

	@Override
	protected Composite doCreatePartControl(Composite customComposite)
	{
		Composite bottom = super.doCreatePartControl(customComposite);

		// Create our special git stuff
		gitStuff = new Composite(customComposite, SWT.NONE);
		gitStuff.setLayout(new FormLayout());
		FormData gitStuffLayoutData = new FormData();
		gitStuffLayoutData.top = new FormAttachment(bottom);
		gitStuffLayoutData.left = new FormAttachment(0, 5);
		gitStuffLayoutData.right = new FormAttachment(100, -5);
		gitStuff.setLayoutData(gitStuffLayoutData);

		createGitDetailsComposite(gitStuff, bottom);
		createInitGitComposite(gitStuff, bottom);
		createExpandCollapseButton(gitDetails);
		createGitBranchCombo(gitDetails);
		createFilterButton(gitDetails);
		createCommitButton(gitDetails);
		createSummaryLabel(gitDetails);
		createPushButton(gitDetails);
		createPullButton(gitDetails);
		createStashButton(gitDetails);
		createUnstashButton(gitDetails);

		return gitStuff;
	}

	private void createExpandCollapseButton(Composite parent)
	{
		expandCollapse = new Label(parent, SWT.FLAT | SWT.CENTER);
		expandCollapse.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
		expandCollapse.setImage(ExplorerPlugin.getImage("icons/full/obj16/bullet_toggle_minus.png")); //$NON-NLS-1$
		expandCollapse.setToolTipText("Collapse");
		expandCollapse.addMouseListener(new MouseListener()
		{
			private boolean expanded = true;

			public void mouseUp(MouseEvent e)
			{
			}

			public void mouseDown(MouseEvent e)
			{
				expanded = !expanded;
				summary.setVisible(expanded);
				stash.setVisible(expanded);
				push.setVisible(expanded);
				pull.setVisible(expanded);
				unstash.setVisible(expanded);

				if (!expanded)
				{
					expandCollapse.setImage(ExplorerPlugin.getImage("icons/full/obj16/bullet_toggle_plus.png")); //$NON-NLS-1$
					expandCollapse.setToolTipText("Expand");
					FormData data = (FormData) gitDetails.getLayoutData();
					data.height = branchCombo.getBounds().height + 3; // margin of 3
					gitDetails.setLayoutData(data);
				}
				else
				{
					expandCollapse.setImage(ExplorerPlugin.getImage("icons/full/obj16/bullet_toggle_minus.png")); //$NON-NLS-1$
					expandCollapse.setToolTipText("Collapse");
					FormData data = (FormData) gitDetails.getLayoutData();
					data.height = SWT.DEFAULT;
					gitDetails.setLayoutData(data);
				}
				gitStuff.getParent().layout();
			}

			public void mouseDoubleClick(MouseEvent e)
			{
			}
		});
	}

	private void createStashButton(Composite parent)
	{
		stash = new Button(parent, SWT.FLAT | SWT.PUSH | SWT.CENTER);
		stash.setLayoutData(new GridData(SWT.END, SWT.CENTER, false, false));
		stash.setImage(ExplorerPlugin.getImage("icons/full/elcl16/arrow_down.png")); //$NON-NLS-1$
		stash.setToolTipText(Messages.GitProjectView_StashTooltip);
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
						action.run(null);
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

	private void createUnstashButton(Composite parent)
	{
		unstash = new Button(parent, SWT.FLAT | SWT.PUSH | SWT.CENTER);
		unstash.setLayoutData(new GridData(SWT.END, SWT.CENTER, false, false));
		unstash.setImage(ExplorerPlugin.getImage("icons/full/elcl16/arrow_up.png")); //$NON-NLS-1$
		unstash.setToolTipText(Messages.GitProjectView_UnstashTooltip);
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
						action.run(null);
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

	private void createPullButton(Composite parent)
	{
		pull = new Button(parent, SWT.FLAT | SWT.PUSH | SWT.CENTER);
		pull.setLayoutData(new GridData(SWT.END, SWT.CENTER, false, false));
		pull.setImage(ExplorerPlugin.getImage("icons/full/elcl16/arrow_left.png")); //$NON-NLS-1$
		pull.setToolTipText(Messages.GitProjectView_PullTooltip);
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
						action.run(null);
						return Status.OK_STATUS;
					}
				};
				job.setUser(true);
				job.setPriority(Job.LONG);
				job.schedule();
			}
		});
	}

	private void createPushButton(Composite parent)
	{
		push = new Button(parent, SWT.FLAT | SWT.PUSH | SWT.CENTER);
		push.setImage(ExplorerPlugin.getImage("icons/full/elcl16/arrow_right.png")); //$NON-NLS-1$
		push.setToolTipText(Messages.GitProjectView_PushTooltip);
		push.setLayoutData(new GridData(SWT.END, SWT.CENTER, false, false));
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
						action.run(null);
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

	private void createSummaryLabel(Composite parent)
	{
		summary = new StyledText(parent, SWT.WRAP);
		summary.setText(""); //$NON-NLS-1$
		summary.setEditable(false);
		summary.setEnabled(false);
		summary.setBackground(parent.getBackground());
		GridData summaryData = new GridData(SWT.BEGINNING, SWT.BEGINNING, true, true);
		summaryData.verticalSpan = 2;
		summaryData.horizontalSpan = 2;
		summary.setLayoutData(summaryData);
	}

	private void createFilterButton(Composite parent)
	{
		gitFilter = new Button(parent, SWT.FLAT | SWT.TOGGLE | SWT.CENTER);
		gitFilter.setLayoutData(new GridData(SWT.END, SWT.CENTER, false, false));
		gitFilter.setImage(ExplorerPlugin.getImage("icons/full/elcl16/filter.png")); //$NON-NLS-1$
		gitFilter.setToolTipText(Messages.GitProjectView_ChangedFilesFilterTooltip);
		gitFilter.addSelectionListener(new SelectionAdapter()
		{
			private GitChangedFilesFilter fChangedFilesFilter;

			@Override
			public void widgetSelected(SelectionEvent e)
			{
				if (fChangedFilesFilter == null)
				{
					fChangedFilesFilter = new GitChangedFilesFilter();
					getCommonViewer().addFilter(fChangedFilesFilter);
					getCommonViewer().expandAll();
				}
				else
				{
					getCommonViewer().removeFilter(fChangedFilesFilter);
					fChangedFilesFilter = null;
				}
			}
		});
	}

	private void createCommitButton(Composite parent)
	{
		commit = new Button(parent, SWT.FLAT | SWT.PUSH | SWT.CENTER);
		commit.setLayoutData(new GridData(SWT.END, SWT.CENTER, false, false));
		commit.setImage(ExplorerPlugin.getImage("icons/full/elcl16/disk.png")); //$NON-NLS-1$
		commit.setToolTipText(Messages.GitProjectView_CommitTooltip);
		commit.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				CommitAction action = new CommitAction();
				ISelection selection = new StructuredSelection(selectedProject);
				action.selectionChanged(null, selection);
				action.run(null);
			}
		});
	}

	private void createGitBranchCombo(Composite parent)
	{
		branchCombo = new Combo(parent, SWT.DROP_DOWN | SWT.READ_ONLY);
		GridData branchComboData = new GridData(SWT.BEGINNING, SWT.CENTER, true, false);
		branchCombo.setLayoutData(branchComboData);
		branchCombo.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				setNewBranch(branchCombo.getText());
			}
		});
	}

	private void createGitDetailsComposite(Composite parent, Composite top)
	{
		gitDetails = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout(4, false);
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		gitDetails.setLayout(layout);
		showGitDetailsData = new FormData();
		showGitDetailsData.top = new FormAttachment(top, 0);
		showGitDetailsData.bottom = new FormAttachment(100, 0);
		showGitDetailsData.right = new FormAttachment(100, 0);
		showGitDetailsData.left = new FormAttachment(0, 0);

		hideGitDetailsData = new FormData();
		hideGitDetailsData.top = new FormAttachment(0);
		hideGitDetailsData.bottom = new FormAttachment(0);
		hideGitDetailsData.right = new FormAttachment(0);
		hideGitDetailsData.left = new FormAttachment(0);

		gitDetails.setLayoutData(hideGitDetailsData);
	}

	private void createInitGitComposite(Composite parent, Composite top)
	{
		initGit = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout(1, false);
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		initGit.setLayout(layout);

		createRepoButton = new Button(initGit, SWT.PUSH);
		createRepoButton.setText("Attach Git repository");
		createRepoButton.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				createRepoButton.setEnabled(false);
				Job job = new Job("Initializing repo")
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
							Display.getDefault().asyncExec(new Runnable()
							{

								public void run()
								{
									createRepoButton.setEnabled(true);
								}
							});
						}
						return Status.OK_STATUS;
					}
				};
				job.setUser(true);
				job.setPriority(Job.LONG);
				job.schedule();
			}
		});

		initGit.setLayoutData(hideGitDetailsData);
	}

	@Override
	public void dispose()
	{
		GitRepository.removeListener(this);
		super.dispose();
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
		// If user selected separator, revert
		if (branchName.equals(BRANCH_SEPARATOR))
		{
			revertToCurrentBranch(repo);
			return false;
		}
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
		else
		{
			revertToCurrentBranch(repo);
			return false;
		}
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
				branchCombo.setText(currentBranchName);
				// TODO Pop a dialog saying we couldn't branches
				return Status.OK_STATUS;
			}
		};
		job.setSystem(true);
		job.setPriority(Job.INTERACTIVE);
		job.schedule();
	}

	@Override
	protected void projectChanged(IProject newSelectedProject)
	{
		super.projectChanged(newSelectedProject);
		refreshUI(GitRepository.getAttached(newSelectedProject));
	}

	private void populateBranches(GitRepository repo)
	{
		branchCombo.removeAll();
		if (repo == null)
			return;
		// FIXME This doesn't seem to indicate proper dirty status and changed files on initial load!
		String currentBranchName = repo.currentBranch();
		for (String branchName : repo.localBranches())
		{
			if (branchName.equals(currentBranchName) && repo.isDirty())
				branchCombo.add(branchName + "*"); //$NON-NLS-1$
			else
				branchCombo.add(branchName);
		}
		branchCombo.add(BRANCH_SEPARATOR);
		branchCombo.add(CREATE_NEW_BRANCH_TEXT);
		if (repo.isDirty())
			currentBranchName += "*"; //$NON-NLS-1$
		branchCombo.setText(currentBranchName);
		branchCombo.pack(true);
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
				updateSummaryText(repository);
				if (repository == null)
				{
					push.setEnabled(false);
					pull.setEnabled(false);
					stash.setEnabled(false);
					commit.setEnabled(false);
					gitFilter.setEnabled(false);
					push.setVisible(false);
					pull.setVisible(false);
					commit.setVisible(false);
					stash.setVisible(false);
					unstash.setVisible(false);
					gitFilter.setVisible(false);
					branchCombo.setVisible(false);
					summary.setVisible(false);
					gitDetails.setLayoutData(hideGitDetailsData);
					// TODO We need to detect if the project really has no repo or is just unattached to it's existing
					// one!
					initGit.setLayoutData(showGitDetailsData);
				}
				else
				{
					// Disable push unless there's a remote tracking branch and we have committed changes
					String[] commitsAhead = repository.commitsAhead(repository.currentBranch());
					push.setEnabled(commitsAhead != null && commitsAhead.length > 0);
					// Disable pull unless there's a remote tracking branch
					pull.setEnabled(repository.trackingRemote(repository.currentBranch()));
					// TODO Disable stash unless there are staged or unstaged (but not untracked) changes
					stash.setEnabled(true);
					// TODO Disable unstash unless there's a refs/stash ref
					unstash.setEnabled(true);
					// TODO Disable commit unless there are changes to commit
					commit.setEnabled(true);
					gitFilter.setEnabled(gitFilterEnabled(repository));
					push.setVisible(true);
					pull.setVisible(true);
					commit.setVisible(true);
					stash.setVisible(true);
					unstash.setVisible(true);
					summary.setVisible(true);
					gitFilter.setVisible(true);
					branchCombo.setVisible(true);
					gitDetails.setLayoutData(showGitDetailsData);
					initGit.setLayoutData(hideGitDetailsData);
					// Make the summary as wide as the project combo, and as tall as the 3 icons
					GridData summaryData = new GridData(SWT.BEGINNING, SWT.BEGINNING, true, true);
					summaryData.verticalSpan = 2;
					summaryData.horizontalSpan = 2;
					summaryData.widthHint = gitStuff.getBounds().width + expandCollapse.getBounds().width;
					// Minimum height should be to bottom of push, pull, stash icons ((2 * icon height) + (1 * space
					// between icons))
					summaryData.minimumHeight = (commit.getBounds().height * 2)
							+ ((GridLayout) gitDetails.getLayout()).verticalSpacing * 1;
					summary.setLayoutData(summaryData);
				}
				gitStuff.pack(true);
				gitStuff.getParent().layout();
				return Status.OK_STATUS;
			}
		};
		job.setSystem(true);
		job.setPriority(Job.INTERACTIVE);
		job.schedule();
	}

	private void updateSummaryText(GitRepository repo)
	{
		if (repo == null)
		{
			summary.setText(""); //$NON-NLS-1$
			summary.setStyleRanges(new StyleRange[0]);
			return;
		}
		Set<StyleRange> ranges = new HashSet<StyleRange>();
		StringBuilder builder = new StringBuilder();
		if (repo.hasMerges())
		{
			builder.append(Messages.GitProjectView_UnresolvedMerges_msg);
			ranges.add(new StyleRange(0, builder.length(), getSite().getShell().getDisplay().getSystemColor(
					SWT.COLOR_RED), null));
		}
		int stagedCount = 0;
		int addedCount = 0;
		int unstagedCount = 0;
		if (repo.index().changedFiles() != null)
		{
			for (ChangedFile file : repo.index().changedFiles())
			{
				if (file == null)
					continue;
				if (file.hasStagedChanges())
				{
					stagedCount++;
				}
				else if (file.getStatus().equals(ChangedFile.Status.NEW))
				{
					addedCount++;
				}
				else
				{
					unstagedCount++;
				}
			}
		}
		String branch = repo.currentBranch();
		String[] commitsAhead = repo.commitsAhead(branch);
		if (commitsAhead != null && commitsAhead.length > 0)
		{
			builder.append(NLS.bind(Messages.GitProjectView_BranchAhead_msg, new Object[] {
					repo.remoteTrackingBranch(branch).shortName(), commitsAhead.length }));
		}
		builder.append(NLS.bind(Messages.GitProjectView_FileCounts, new Object[] { stagedCount, unstagedCount,
				addedCount }));
		int legendStart = builder.length();
		builder.append(Messages.GitProjectView_FileCountsLabel);
		ranges.add(new StyleRange(legendStart, builder.length() - legendStart, null, null, SWT.ITALIC));
		summary.setText(builder.toString());
		for (StyleRange range : ranges)
		{
			summary.setStyleRange(range);
		}
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

	private boolean gitFilterEnabled(final GitRepository repository)
	{
		// TODO The files also have to be children of the active project!
		return !repository.index().changedFiles().isEmpty();
	}

	public void repositoryRemoved(RepositoryRemovedEvent e)
	{
		IProject changed = e.getProject();
		if (changed != null && changed.equals(selectedProject))
			refreshUI(null);
	}
}
