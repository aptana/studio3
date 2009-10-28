package com.aptana.radrails.explorer.internal.ui;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.navigator.CommonNavigator;

import com.aptana.git.core.model.GitRepository;
import com.aptana.radrails.explorer.ExplorerPlugin;

public class GitProjectView extends CommonNavigator
{
	/**
	 * Property we assign to a project to make it the active one that this view is filtered to.
	 */
	private static final String ACTIVE_PROJECT = "activeProject";

	private Combo projectCombo;
	protected IProject selectedProject;

	private Combo branchCombo;

	@Override
	public void createPartControl(Composite aParent)
	{
		// Create our own parent
		Composite myComposite = new Composite(aParent, SWT.NONE);
		myComposite.setLayout(new FormLayout());

		// Create our special git stuff
		Composite gitStuff = new Composite(myComposite, SWT.NONE);
		gitStuff.setLayout(new RowLayout());
		FormData data1 = new FormData();
		data1.left = new FormAttachment(0, 0);
		data1.top = new FormAttachment(0, 0);
		gitStuff.setLayoutData(data1);

		Label projectLabel = new Label(gitStuff, SWT.NONE);
		projectLabel.setText("Project: ");

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

		if (projects.length > 0)
			detectSelectProject();

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
	}

	protected void setNewBranch(String branchName)
	{
		// TODO Switch to the new branch if possible, refresh the viewer's contents
		GitRepository repo = GitRepository.getAttached(selectedProject);
		if (repo != null)
		{
			repo.switchBranch(branchName);
			refreshViewer();
		}
	}

	private void detectSelectProject()
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
				selectedProject
						.setPersistentProperty(new QualifiedName(ExplorerPlugin.PLUGIN_ID, ACTIVE_PROJECT), null);
			selectedProject = newSelectedProject;
			selectedProject.setPersistentProperty(new QualifiedName(ExplorerPlugin.PLUGIN_ID, ACTIVE_PROJECT),
					Boolean.TRUE.toString());
			branchCombo.removeAll();
			GitRepository repo = GitRepository.getAttached(newSelectedProject);
			if (repo != null)
			{
				// TODO Change the branch name to get a star appended if it's "dirty"
				for (String branchName : repo.localBranches())
				{
					branchCombo.add(branchName);
				}
				branchCombo.setText(repo.currentBranch());
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

}
