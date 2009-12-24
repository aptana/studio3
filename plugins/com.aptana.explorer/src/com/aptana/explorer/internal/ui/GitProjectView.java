package com.aptana.explorer.internal.ui;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.MenuManager;
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
import com.aptana.git.ui.dialogs.CreateBranchDialog;

/**
 * Adds Git UI elements to the Single Project View.
 * 
 * @author cwilliams
 */
public class GitProjectView extends SingleProjectView implements IGitRepositoryListener
{
	private static final String CREATE_NEW_BRANCH_TEXT = Messages.GitProjectView_createNewBranchOption;
	
	private Label branchLabel;
	private GridData branchLabelGridData;
	private ToolBar branchesToolbar;
	private GridData branchesToolbarGridData;

	private ToolItem branchesToolItem;
	private Menu branchesMenu;

	@Override
	public void createPartControl(Composite aParent)
	{
		super.createPartControl(aParent);

		GitRepository.addListener(this);
	}
	
	protected void doCreateToolbar(Composite toolbarComposite) {
		createGitBranchCombo(toolbarComposite);
	}

	private void createGitBranchCombo(Composite parent)
	{
		((GridLayout) parent.getLayout()).numColumns += 2;
		
		branchLabel = new Label(parent, SWT.NONE);
		branchLabel.setText("Branch : ");
		branchLabelGridData = new GridData(SWT.BEGINNING, SWT.CENTER, false, false);
		branchLabelGridData.horizontalIndent = 2;
		branchLabel.setLayoutData(branchLabelGridData);
		
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
	}

	@Override
	protected void fillCommandsMenu(MenuManager menuManager) {
		super.fillCommandsMenu(menuManager);
		Menu menu = menuManager.getMenu();
		// Fill the menu
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
				for (MenuItem menuItem : menuItems) {
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
		for (MenuItem menuItem : menuItems) {
			menuItem.dispose();
		}		
		branchesToolItem.setText("");
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
		branchNameMenuItem.setSelection(true);
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
					branchesToolbarGridData.exclude = true;
					branchesToolbar.setVisible(false);
					branchLabelGridData.exclude = true;
					branchLabel.setVisible(false);
				}
				else
				{
					repository.commitsAhead(repository.currentBranch());
					branchesToolbarGridData.exclude = false;
					branchesToolbar.setVisible(true);
					branchLabelGridData.exclude = false;
					branchLabel.setVisible(true);
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
