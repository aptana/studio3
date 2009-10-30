package com.aptana.radrails.explorer.internal.ui;

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
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.navigator.CommonNavigator;
import org.eclipse.ui.progress.UIJob;

import com.aptana.git.core.model.BranchChangedEvent;
import com.aptana.git.core.model.ChangedFile;
import com.aptana.git.core.model.GitRepository;
import com.aptana.git.core.model.IGitRepositoryListener;
import com.aptana.git.core.model.IndexChangedEvent;
import com.aptana.git.core.model.RepositoryAddedEvent;
import com.aptana.git.ui.actions.CommitAction;
import com.aptana.git.ui.actions.PullAction;
import com.aptana.git.ui.actions.PushAction;
import com.aptana.git.ui.actions.StashAction;
import com.aptana.git.ui.actions.UnstashAction;
import com.aptana.radrails.explorer.ExplorerPlugin;

public class GitProjectView extends CommonNavigator implements IGitRepositoryListener
{
	/**
	 * Property we assign to a project to make it the active one that this view is filtered to.
	 */
	private static final String ACTIVE_PROJECT = "activeProject";

	private Combo projectCombo;
	protected IProject selectedProject;
	private Combo branchCombo;
	private Label summary;
	private Button pull;
	private Button push;
	private Button commit;
	private Button stash;
	private Button unstash;
	private Composite gitStuff;
	private Composite gitDetails;

	private FormData showGitDetailsData;
	private FormData hideGitDetailsData;

	private ResourceListener fResourceListener;

	@Override
	public void createPartControl(Composite aParent)
	{
		// Create our own parent
		Composite myComposite = new Composite(aParent, SWT.NONE);
		myComposite.setLayout(new FormLayout());

		// Create our special git stuff
		gitStuff = new Composite(myComposite, SWT.NONE);
		gitStuff.setLayout(new FormLayout());
		FormData gitStuffLayoutData = new FormData();
		gitStuffLayoutData.top = new FormAttachment(0, 5);
		gitStuffLayoutData.left = new FormAttachment(0, 5);
		gitStuffLayoutData.right= new FormAttachment(100, -5);
		gitStuff.setLayoutData(gitStuffLayoutData);

		IProject[] projects = createProjectCombo(gitStuff);
		createGitDetailsComposite(gitStuff);
		createGitBranchCombo(gitDetails);
		createCommitButton(gitDetails);
		createSummaryLabel(gitDetails);
		createPushButton(gitDetails);
		createPullButton(gitDetails);
		createStashButton(gitDetails);
		createUnstashButton(gitDetails);

		// Now create the typical stuff for the navigator
		createNavigator(myComposite);

		fResourceListener = new ResourceListener();
		ResourcesPlugin.getWorkspace().addResourceChangeListener(fResourceListener, IResourceChangeEvent.POST_CHANGE);
		GitRepository.addListener(this);
		if (projects.length > 0)
			detectSelectedProject();
	}

	private void createNavigator(Composite myComposite)
	{
		Composite viewer = new Composite(myComposite, SWT.NONE);
		viewer.setLayout(new FillLayout());
		FormData data2 = new FormData();
		data2.top = new FormAttachment(gitStuff);
		data2.bottom = new FormAttachment(100, 0);
		data2.right = new FormAttachment(100, 0);
		data2.left = new FormAttachment(0, 0);
		viewer.setLayoutData(data2);
		super.createPartControl(viewer);
	}

	private void createStashButton(Composite parent)
	{
		stash = new Button(parent, SWT.FLAT | SWT.PUSH | SWT.CENTER);
		stash.setLayoutData(new GridData(SWT.END, SWT.CENTER, false, false));
		stash.setImage(ExplorerPlugin.getImage("icons/full/elcl16/arrow_down.png"));
		stash.setToolTipText("Stash");
		stash.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				final StashAction action = new StashAction();
				action.selectionChanged(null, new StructuredSelection(selectedProject));
				Job job = new Job("git stash")
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
		unstash.setImage(ExplorerPlugin.getImage("icons/full/elcl16/arrow_up.png"));
		unstash.setToolTipText("Unstash");
		unstash.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				final UnstashAction action = new UnstashAction();
				action.selectionChanged(null, new StructuredSelection(selectedProject));
				Job job = new Job("git unstash")
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
		pull.setImage(ExplorerPlugin.getImage("icons/full/elcl16/arrow_left.png"));
		pull.setToolTipText("Pull");
		pull.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				final PullAction action = new PullAction();
				action.selectionChanged(null, new StructuredSelection(selectedProject));
				Job job = new Job("git pull")
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
		push.setImage(ExplorerPlugin.getImage("icons/full/elcl16/arrow_right.png"));
		push.setToolTipText("Push");
		push.setLayoutData(new GridData(SWT.END, SWT.CENTER, false, false));
		push.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				final PushAction action = new PushAction();
				action.selectionChanged(null, new StructuredSelection(selectedProject));
				Job job = new Job("git push")
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
		summary = new Label(parent, SWT.WRAP);
		summary.setText("");
		Font font = summary.getFont();
		FontData[] oldData = font.getFontData();
		FontData[] newData = new FontData[oldData.length];
		System.arraycopy(oldData, 0, newData, 0, oldData.length);
		for (int i = 0; i < newData.length; i++)
		{
			FontData data = newData[i];
			data.setStyle(data.getStyle() | SWT.ITALIC);
		}
		Font newFont = new Font(font.getDevice(), newData);
		summary.setFont(newFont);
		GridData summaryData = new GridData(SWT.BEGINNING, SWT.BEGINNING, true, true);
		summaryData.verticalSpan = 3;
		summary.setLayoutData(summaryData);
	}

	private void createCommitButton(Composite parent)
	{
		commit = new Button(parent, SWT.FLAT | SWT.PUSH | SWT.CENTER);
		commit.setLayoutData(new GridData(SWT.END, SWT.CENTER, false, false));
		commit.setImage(ExplorerPlugin.getImage("icons/full/elcl16/disk.png"));
		commit.setToolTipText("Commit...");
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
		branchComboData.horizontalSpan = 2;
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

	private void createGitDetailsComposite(Composite parent)
	{
		gitDetails = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout(3, false);
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		gitDetails.setLayout(layout);
		showGitDetailsData = new FormData();
		showGitDetailsData.top = new FormAttachment(projectCombo, 0);
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

	private IProject[] createProjectCombo(Composite parent)
	{
		projectCombo = new Combo(parent, SWT.DROP_DOWN | SWT.MULTI | SWT.READ_ONLY);
		FormData projectData = new FormData();
		projectData.left = new FormAttachment(0, 0);
		projectData.top = new FormAttachment(0, 0);
		projectData.right = new FormAttachment(100, 0);
		projectCombo.setLayoutData(projectData);
		IProject[] projects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
		for (IProject iProject : projects)
		{
			projectCombo.add(iProject.getName());
		}
		projectCombo.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				setActiveProject(projectCombo.getText());
			}
		});
		return projects;
	}

	@Override
	public void dispose()
	{
		ResourcesPlugin.getWorkspace().removeResourceChangeListener(fResourceListener);
		GitRepository.removeListener(this);
		super.dispose();
	}

	protected void reloadProjects()
	{
		Job job = new UIJob("Reload Projects")
		{

			@Override
			public IStatus runInUIThread(IProgressMonitor monitor)
			{
				// FIXME What if the active project was deleted or renamed?
				projectCombo.removeAll();
				IProject[] projects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
				for (IProject iProject : projects)
				{
					projectCombo.add(iProject.getName());
				}
				projectCombo.setText(selectedProject.getName());
				return Status.OK_STATUS;
			}
		};
		job.setSystem(true);
		job.setPriority(Job.INTERACTIVE);
		job.schedule();
	}

	protected boolean setNewBranch(String branchName)
	{
		if (branchName.endsWith("*"))
			branchName = branchName.substring(0, branchName.length() - 1);

		final GitRepository repo = GitRepository.getAttached(selectedProject);
		if (repo == null)
			return false;
		if (branchName.equals(repo.currentBranch()))
			return false;
		if (repo.switchBranch(branchName))
		{
			refreshViewer();
			return true;
		}
		else
		{
			Job job = new UIJob("")
			{

				@Override
				public IStatus runInUIThread(IProgressMonitor monitor)
				{
					String currentBranchName = repo.currentBranch();
					if (repo.isDirty())
						currentBranchName += "*";
					branchCombo.setText(currentBranchName);
					// TODO Pop a dialog saying we couldn't branches
					return Status.OK_STATUS;
				}
			};
			job.setSystem(true);
			job.setPriority(Job.INTERACTIVE);
			job.schedule();
			return false;
		}
	}

	private void detectSelectedProject()
	{
		IProject[] projects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
		if (projects == null)
			return;
		for (IProject iProject : projects)
		{
			try
			{
				String value = iProject.getPersistentProperty(new QualifiedName(ExplorerPlugin.PLUGIN_ID,
						ACTIVE_PROJECT));
				if (value != null && value.equals(Boolean.TRUE.toString()))
				{
					projectCombo.setText(iProject.getName());
					setActiveProject(iProject.getName());
					return;
				}
			}
			catch (CoreException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	protected void setActiveProject(String projectName)
	{
		IProject newSelectedProject = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
		if (newSelectedProject == null || (selectedProject != null && newSelectedProject.equals(selectedProject)))
			return;
		try
		{
			if (selectedProject != null)
			{
				selectedProject
						.setPersistentProperty(new QualifiedName(ExplorerPlugin.PLUGIN_ID, ACTIVE_PROJECT), null);
			}
			selectedProject = newSelectedProject;
			selectedProject.setPersistentProperty(new QualifiedName(ExplorerPlugin.PLUGIN_ID, ACTIVE_PROJECT),
					Boolean.TRUE.toString());
			refreshUI(GitRepository.getAttached(newSelectedProject));
			// Refresh the view so our filter gets updated!
			refreshViewer();
		}
		catch (CoreException e1)
		{
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
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
				branchCombo.add(branchName + "*");
			else
				branchCombo.add(branchName);
		}
		if (repo.isDirty())
			currentBranchName += "*";
		branchCombo.setText(currentBranchName);
		branchCombo.pack(true);
	}

	private void refreshViewer()
	{
		if (getCommonViewer() == null)
			return;
		getCommonViewer().refresh();
	}

	public void indexChanged(final IndexChangedEvent e)
	{
		refreshUI(e.getRepository());
	}

	protected void refreshUI(final GitRepository repository)
	{
		Job job = new UIJob("update UI for index changes")
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
					push.setVisible(false);
					pull.setVisible(false);
					commit.setVisible(false);
					stash.setVisible(false);
					unstash.setVisible(false);
					branchCombo.setVisible(false);
					summary.setVisible(false);
					gitDetails.setLayoutData(hideGitDetailsData);
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
					push.setVisible(true);
					pull.setVisible(true);
					commit.setVisible(true);
					stash.setVisible(true);
					unstash.setVisible(true);
					summary.setVisible(true);
					branchCombo.setVisible(true);
					gitDetails.setLayoutData(showGitDetailsData);
					// Make the summary as wide as the project combo, and as tall as the 3 icons
					GridData summaryData = new GridData(SWT.BEGINNING, SWT.BEGINNING, true, true);
					summaryData.verticalSpan = 3;
					summaryData.widthHint = projectCombo.getBounds().width;
					// Minimum height should be to bottom of push, pull, stash icons ((3 * icon height) + (2 * space
					// between icons))
					summaryData.minimumHeight = (commit.getBounds().height * 3)
							+ ((GridLayout) gitDetails.getLayout()).verticalSpacing * 2;
					summary.setLayoutData(summaryData);
				}
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
			summary.setText("");
			return;
		}
		StringBuilder builder = new StringBuilder();
		if (repo.hasMerges())
		{
			builder.append("You have unresolved merges!\n");
			summary.setForeground(getSite().getShell().getDisplay().getSystemColor(SWT.COLOR_RED));
		}
		else
		{
			summary.setForeground(getSite().getShell().getDisplay().getSystemColor(SWT.COLOR_BLACK));
		}
		// TODO Check if repo has merges and add that in red if it does!
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
			builder.append("Your branch is ahead of '");
			builder.append(repo.remoteTrackingBranch(branch).shortName()).append("' by ");
			builder.append(commitsAhead.length).append(" commit(s)\n");
		}
		builder.append(stagedCount).append("/");
		builder.append(unstagedCount).append("/");
		builder.append(addedCount).append("\n");
		builder.append("staged/unstaged/untracked");
		summary.setText(builder.toString());
	}

	public void repositoryAdded(RepositoryAddedEvent e)
	{
		// TODO Someone may have just attached the current project to a repo! We need to update our UI if they did
		GitRepository repo = e.getRepository();
		GitRepository selectedRepo = GitRepository.getAttached(selectedProject);
		if (selectedRepo != null && selectedRepo.equals(repo))
			refreshUI(e.getRepository());
	}

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
							reloadProjects();
						}
						return false;
					}
				});
			}
			catch (CoreException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public void branchChanged(BranchChangedEvent e)
	{
		GitRepository repo = e.getRepository();
		GitRepository selectedRepo = GitRepository.getAttached(selectedProject);
		if (selectedRepo != null && selectedRepo.equals(repo))
			refreshUI(e.getRepository());
	}

}
