/*******************************************************************************
 * Copyright (C) 2009, Robin Rosenberg
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.aptana.git.ui.internal.sharing;

import java.io.File;
import java.net.URI;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;

import com.aptana.core.logging.IdeLog;
import com.aptana.core.util.StringUtil;
import com.aptana.git.core.GitPlugin;
import com.aptana.git.core.IDebugScopes;
import com.aptana.git.core.model.GitExecutable;
import com.aptana.git.core.model.GitRepository;
import com.aptana.git.core.model.IGitRepositoryManager;
import com.aptana.git.ui.GitUIPlugin;

/**
 * Wizard page for connecting projects to Git repositories.
 */
class ExistingOrNewPage extends WizardPage
{

	private final SharingWizard myWizard;
	private Button button;
	private Tree tree;
	private Text repositoryToCreate;
	private IPath minumumPath;

	ExistingOrNewPage(SharingWizard w)
	{
		super(ExistingOrNewPage.class.getName());
		setTitle(Messages.ExistingOrNewPage_Title);
		setDescription(Messages.ExistingOrNewPage_Description);
		// setImageDescriptor(UIIcons.WIZBAN_CONNECT_REPO);
		this.myWizard = w;
	}

	public void createControl(Composite parent)
	{
		Group g = new Group(parent, SWT.NONE);
		g.setLayout(new GridLayout(3, false));
		g.setLayoutData(GridDataFactory.fillDefaults().grab(true, true).create());
		tree = new Tree(g, SWT.BORDER | SWT.MULTI);
		tree.setHeaderVisible(true);
		tree.setLayout(new GridLayout());
		tree.setLayoutData(GridDataFactory.fillDefaults().grab(true, true).span(3, 1).create());
		TreeColumn c1 = new TreeColumn(tree, SWT.NONE);
		c1.setText(Messages.ExistingOrNewPage_ProjectColumn_Label);
		c1.setWidth(100);
		TreeColumn c2 = new TreeColumn(tree, SWT.NONE);
		c2.setText(Messages.ExistingOrNewPage_PathColumn_Label);
		c2.setWidth(400);
		TreeColumn c3 = new TreeColumn(tree, SWT.NONE);
		c3.setText(Messages.ExistingOrNewPage_RepositoryColumn_Label);
		c3.setWidth(200);
		for (IProject project : myWizard.projects)
		{
			TreeItem treeItem = new TreeItem(tree, SWT.NONE);
			treeItem.setData(project);
			treeItem.setText(0, project.getName());
			treeItem.setText(1, project.getLocation().toOSString());
			URI gitDir = getGitRepositoryManager().gitDirForURL(project.getLocationURI());
			if (gitDir == null)
			{
				treeItem.setText(2, StringUtil.EMPTY);
			}
			else
			{
				treeItem.setText(2, gitDir.getPath());
			}
		}

		button = new Button(g, SWT.PUSH);
		button.setLayoutData(GridDataFactory.fillDefaults().create());
		button.setText(Messages.ExistingOrNewPage_CreateButton_Label);
		button.addSelectionListener(new SelectionListener()
		{
			public void widgetSelected(SelectionEvent e)
			{
				IPath gitDir = Path.fromOSString(repositoryToCreate.getText()).append(GitRepository.GIT_DIR);
				try
				{
					if (GitExecutable.instance() == null)
					{
						throw new CoreException(new Status(IStatus.ERROR, GitUIPlugin.getPluginId(),
								Messages.ExistingOrNewPage_UnabletoFindGitExecutableError));
					}
					getGitRepositoryManager().create(gitDir.removeLastSegments(1));
					for (IProject project : getProjects())
					{
						// If we don't refresh the project directories right
						// now we won't later know that a .git directory
						// exists within it and we won't mark the .git
						// directory as a team-private member. Failure
						// to do so might allow someone to delete
						// the .git directory without us stopping them.
						// (Half lie, we should optimize so we do not
						// refresh when the .git is not within the project)
						//
						if (!gitDir.toString().contains("..")) //$NON-NLS-1$
							project.refreshLocal(IResource.DEPTH_ONE, new NullProgressMonitor());
					}
				}
				catch (CoreException e2)
				{
					IdeLog.logError(GitUIPlugin.getDefault(),
							NLS.bind(Messages.ExistingOrNewPage_ErrorFailedToRefreshRepository, gitDir), e2,
							IDebugScopes.DEBUG);
				}
				for (TreeItem ti : tree.getSelection())
				{
					ti.setText(2, gitDir.toString());
				}
				updateCreateOptions();
				getContainer().updateButtons();
			}

			public void widgetDefaultSelected(SelectionEvent e)
			{
			}
		});
		repositoryToCreate = new Text(g, SWT.SINGLE | SWT.BORDER);
		repositoryToCreate.setLayoutData(GridDataFactory.fillDefaults().grab(true, false).span(1, 1).create());
		repositoryToCreate.addListener(SWT.Modify, new Listener()
		{
			public void handleEvent(Event e)
			{
				if (e.text == null)
					return;
				IPath fromOSString = Path.fromOSString(e.text);
				button.setEnabled(minumumPath.matchingFirstSegments(fromOSString) == fromOSString.segmentCount());
			}
		});
		Text l = new Text(g, SWT.NONE);
		l.setEnabled(false);
		l.setEditable(false);
		l.setText(File.separatorChar + GitRepository.GIT_DIR);
		l.setLayoutData(GridDataFactory.fillDefaults().create());
		tree.addSelectionListener(new SelectionListener()
		{
			public void widgetSelected(SelectionEvent e)
			{
				updateCreateOptions();
			}

			public void widgetDefaultSelected(SelectionEvent e)
			{
				// Empty
			}
		});
		updateCreateOptions();
		setControl(g);
	}

	private IGitRepositoryManager getGitRepositoryManager()
	{
		return GitPlugin.getDefault().getGitRepositoryManager();
	}

	private void updateCreateOptions()
	{
		minumumPath = null;
		IPath p = null;
		for (TreeItem ti : tree.getSelection())
		{
			String path = ti.getText(2);
			if (path.length() != 0)
			{
				p = null;
				break;
			}
			String gitDirParentCandidate = ti.getText(1);
			IPath thisPath = Path.fromOSString(gitDirParentCandidate);
			if (p == null)
			{
				p = thisPath;
			}
			else
			{
				int n = p.matchingFirstSegments(thisPath);
				p = p.removeLastSegments(p.segmentCount() - n);
			}
		}
		minumumPath = p;
		if (p != null)
		{
			repositoryToCreate.setText(p.toOSString());
		}
		else
		{
			repositoryToCreate.setText(StringUtil.EMPTY);
		}
		button.setEnabled(p != null);
		repositoryToCreate.setEnabled(p != null);
		getContainer().updateButtons();
	}

	@Override
	public boolean isPageComplete()
	{
		if (tree.getItemCount() == 1)
		{
			TreeItem ti = tree.getItem(0);
			String path = ti.getText(2);
			if (path.length() == 0)
			{
				return false;
			}
		}
		else
		{
			if (tree.getSelectionCount() == 0)
				return false;
			for (TreeItem ti : tree.getSelection())
			{
				String path = ti.getText(2);
				if (path.length() == 0)
				{
					return false;
				}
			}
		}
		return true;
	}

	public IProject[] getProjects()
	{
		if (tree.getItemCount() == 1)
		{
			TreeItem ti = tree.getItem(0);
			return new IProject[] { (IProject) ti.getData() };
		}
		IProject[] ret = new IProject[tree.getSelection().length];
		for (int i = 0; i < ret.length; ++i)
		{
			ret[i] = (IProject) tree.getSelection()[i].getData();
		}
		return ret;
	}
}
