package com.aptana.radrails.explorer.internal.ui;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.navigator.CommonNavigator;

import com.aptana.git.core.model.ChangedFile;
import com.aptana.git.core.model.GitRepository;
import com.aptana.git.core.model.IGitRepositoryListener;
import com.aptana.git.core.model.IndexChangedEvent;
import com.aptana.git.core.model.RepositoryAddedEvent;
import com.aptana.git.ui.actions.CommitAction;
import com.aptana.git.ui.actions.PullAction;
import com.aptana.git.ui.actions.PushAction;
import com.aptana.git.ui.actions.StashAction;
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

	private ResourceListener fResourceListener;

	@Override
	public void createPartControl(Composite aParent)
	{
		// Create our own parent
		Composite myComposite = new Composite(aParent, SWT.NONE);
		myComposite.setLayout(new FormLayout());

		// Create our special git stuff
		Composite gitStuff = new Composite(myComposite, SWT.NONE);
		gitStuff.setLayout(new GridLayout(3, false));
		FormData data1 = new FormData();
		data1.left = new FormAttachment(0, 0);
		data1.top = new FormAttachment(0, 0);
		gitStuff.setLayoutData(data1);

		projectCombo = new Combo(gitStuff, SWT.DROP_DOWN | SWT.MULTI | SWT.READ_ONLY);
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
				super.widgetSelected(e);

				String projectName = projectCombo.getText();
				setActiveProject(projectName);
			}
		});

		branchCombo = new Combo(gitStuff, SWT.DROP_DOWN | SWT.READ_ONLY);
		branchCombo.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				super.widgetSelected(e);

				String branchName = branchCombo.getText();
				setNewBranch(branchName);
			}
		});

		// Add icon for commit (disk)
		Label commit = new Label(gitStuff, SWT.NONE);
		commit.setImage(ExplorerPlugin.getImage("icons/full/elcl16/disk.png"));
		commit.setToolTipText("Commit...");
		commit.addMouseListener(new MouseListener()
		{
			public void mouseDoubleClick(MouseEvent e)
			{
			}

			public void mouseUp(MouseEvent e)
			{
			}

			public void mouseDown(MouseEvent e)
			{
				CommitAction action = new CommitAction();
				ISelection selection = new StructuredSelection(selectedProject);
				action.selectionChanged(null, selection);
				action.run(null);
			}
		});

		summary = new Label(gitStuff, SWT.WRAP);
		summary.setText("");
		GridData summaryData = new GridData();
		summaryData.horizontalSpan = 2;
		summaryData.verticalSpan = 3;
		summary.setLayoutData(summaryData);

		Label push = new Label(gitStuff, SWT.NONE);
		push.setImage(ExplorerPlugin.getImage("icons/full/elcl16/arrow_right.png"));
		push.setToolTipText("Push");
		// TODO Disable unless there's a remote tracking branch and we have commits
		push.addMouseListener(new MouseListener()
		{
			public void mouseDoubleClick(MouseEvent e)
			{
			}

			public void mouseUp(MouseEvent e)
			{
			}

			public void mouseDown(MouseEvent e)
			{
				PushAction action = new PushAction();
				ISelection selection = new StructuredSelection(selectedProject);
				action.selectionChanged(null, selection);
				action.run(null);
			}
		});

		Label pull = new Label(gitStuff, SWT.NONE);
		pull.setImage(ExplorerPlugin.getImage("icons/full/elcl16/arrow_left.png"));
		pull.setToolTipText("Pull");
		// TODO Disable unless there's a remote tracking branch
		pull.addMouseListener(new MouseListener()
		{
			public void mouseDoubleClick(MouseEvent e)
			{
			}

			public void mouseUp(MouseEvent e)
			{
			}

			public void mouseDown(MouseEvent e)
			{
				PullAction action = new PullAction();
				ISelection selection = new StructuredSelection(selectedProject);
				action.selectionChanged(null, selection);
				action.run(null);
			}
		});

		Label stash = new Label(gitStuff, SWT.NONE);
		stash.setImage(ExplorerPlugin.getImage("icons/full/elcl16/arrow_down.png"));
		stash.setToolTipText("Stash");
		stash.addMouseListener(new MouseListener()
		{
			public void mouseDoubleClick(MouseEvent e)
			{
			}

			public void mouseUp(MouseEvent e)
			{
			}

			public void mouseDown(MouseEvent e)
			{
				StashAction action = new StashAction();
				ISelection selection = new StructuredSelection(selectedProject);
				action.selectionChanged(null, selection);
				action.run(null);
			}
		});

		// Now create the typical stuff for the navigator
		Composite viewer = new Composite(myComposite, SWT.NONE);
		viewer.setLayout(new FillLayout());
		FormData data2 = new FormData();
		data2.top = new FormAttachment(gitStuff);
		data2.bottom = new FormAttachment(100, 0);
		data2.right = new FormAttachment(100, 0);
		data2.left = new FormAttachment(0, 0);
		viewer.setLayoutData(data2);
		super.createPartControl(viewer);

		if (projects.length > 0)
			detectSelectedProject();
		fResourceListener = new ResourceListener();
		ResourcesPlugin.getWorkspace().addResourceChangeListener(fResourceListener, IResourceChangeEvent.POST_CHANGE);
		GitRepository.addListener(this);
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
		// FIXME What if the active project was deleted or renamed?
		projectCombo.removeAll();
		IProject[] projects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
		for (IProject iProject : projects)
		{
			projectCombo.add(iProject.getName());
		}
		projectCombo.setText(selectedProject.getName());
	}

	protected void setNewBranch(String branchName)
	{
		if (branchName.endsWith("*"))
			branchName = branchName.substring(0, branchName.length() - 1);

		GitRepository repo = GitRepository.getAttached(selectedProject);
		if (repo != null)
		{
			if (branchName.equals(repo.currentBranch()))
				return;
			repo.switchBranch(branchName);
			refreshViewer();
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
			branchCombo.removeAll();
			GitRepository repo = GitRepository.getAttached(newSelectedProject);
			updateSummaryText(repo);
			if (repo != null)
			{
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
			// Refresh the view so our filter gets updated!
			refreshViewer();
		}
		catch (CoreException e1)
		{
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	private void refreshViewer()
	{
		if (getCommonViewer() == null)
			return;
		getCommonViewer().refresh();
	}

	public void indexChanged(IndexChangedEvent e)
	{
		// TODO Update the branch list so we can reset the dirty status on the branch
		updateSummaryText(e.getRepository());
	}

	private void updateSummaryText(GitRepository repo)
	{
		if (repo == null)
			summary.setText("");
		int deletedCount = 0;
		int addedCount = 0;
		int modifiedCount = 0;
		for (ChangedFile file : repo.index().changedFiles())
		{
			if (file.getStatus().equals(ChangedFile.Status.DELETED))
			{
				deletedCount++;
			}
			else if (file.getStatus().equals(ChangedFile.Status.NEW))
			{
				addedCount++;
			}
			else
			{
				modifiedCount++;
			}
		}
		StringBuilder builder = new StringBuilder();
		builder.append(modifiedCount).append(" file(s) modified\n");
		builder.append(deletedCount).append(" file(s) deleted\n");
		builder.append(addedCount).append(" file(s) added");
		summary.setText(builder.toString());
	}

	public void repositoryAdded(RepositoryAddedEvent e)
	{
		// ignore
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

}
