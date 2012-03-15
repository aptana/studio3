/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.git.ui.internal.actions;

import java.util.SortedSet;

import org.eclipse.core.resources.IResource;
import org.eclipse.jface.action.ContributionItem;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.team.ui.TeamUI;
import org.eclipse.team.ui.history.IHistoryPage;
import org.eclipse.team.ui.history.IHistoryView;
import org.eclipse.ui.menus.IWorkbenchContribution;
import org.eclipse.ui.services.IServiceLocator;

import com.aptana.git.core.GitPlugin;
import com.aptana.git.core.model.GitRef;
import com.aptana.git.core.model.GitRepository;
import com.aptana.git.core.model.IGitRepositoryManager;
import com.aptana.git.ui.internal.history.GitHistoryPage;

/**
 * This generates the list of branches/refs/tags to view the history for a given resource.
 * 
 * @author cwilliams
 */
public class SwitchHistoryBranchPulldownContributionItem extends ContributionItem implements IWorkbenchContribution
{

	public SwitchHistoryBranchPulldownContributionItem()
	{
	}

	public SwitchHistoryBranchPulldownContributionItem(String id)
	{
		super(id);
	}

	@Override
	public void fill(Menu menu, int index)
	{
		GitHistoryPage historyPage = getHistoryPage();
		if (historyPage == null)
		{
			return;
		}

		IResource resource = (IResource) historyPage.getInput();
		String currentRef = historyPage.getCurrentRef();

		IGitRepositoryManager manager = GitPlugin.getDefault().getGitRepositoryManager();
		GitRepository repo = manager.getAttached(resource.getProject());

		SortedSet<GitRef> refs = repo.simpleRefs();
		for (GitRef ref : refs)
		{
			String refName = ref.shortName();
			boolean enabled = !currentRef.equals(refName);
			IContributionItem item = new SwitchRefContributionItem(resource, refName, enabled);
			item.fill(menu, menu.getItemCount());
		}
	}

	protected GitHistoryPage getHistoryPage()
	{
		IHistoryView view = TeamUI.getHistoryView();
		IHistoryPage page = view.getHistoryPage();
		if (page instanceof GitHistoryPage)
		{
			return (GitHistoryPage) page;
		}
		return null;
	}

	public void initialize(IServiceLocator serviceLocator)
	{
		// ignore
	}

	private void switchRef(IResource manager, String refName)
	{
		GitHistoryPage page = getHistoryPage();
		if (page != null)
		{
			page.setRef(refName);
		}
	}

	/**
	 * For a given resource's history, a menu entry to switch ref (local/remote branch, tags) whose history we're
	 * grabbing/showing.
	 * 
	 * @author cwilliams
	 */
	private class SwitchRefContributionItem extends ContributionItem
	{
		private IResource resource;
		private String branchName;
		private boolean enabled;

		SwitchRefContributionItem(IResource resource, String refName, boolean enabled)
		{
			this.resource = resource;
			this.branchName = refName;
			this.enabled = enabled;
		}

		@Override
		public void fill(Menu menu, int index)
		{
			MenuItem menuItem = new MenuItem(menu, SWT.PUSH, index);
			menuItem.setText(branchName);
			menuItem.setEnabled(enabled);
			menuItem.addSelectionListener(new SelectionAdapter()
			{
				public void widgetSelected(SelectionEvent e)
				{
					// what to do when menu is subsequently selected.
					switchRef(resource, branchName);
				}
			});
		}
	}
}