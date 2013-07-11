/**
 * Aptana Studio
 * Copyright (c) 2013 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.git.ui.actions;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.core.resources.IResource;
import org.eclipse.jface.action.ContributionItem;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

import com.aptana.git.core.model.GitRepository;
import com.aptana.git.ui.internal.actions.PullFromRemoteHandler;

/**
 * Item to pull from named remote to current branch
 * 
 * @author cwilliams
 */
public class PullFromRemoteItem extends AbstractDynamicBranchItem
{

	public PullFromRemoteItem()
	{
		super();
	}

	public PullFromRemoteItem(String id)
	{
		super(id);
	}

	@Override
	protected IContributionItem[] getContributionItems()
	{
		IResource resource = getSelectedResource();
		if (resource == null)
		{
			return NO_CONTRIBUTION_ITEMS;
		}

		final GitRepository repo = getGitRepositoryManager().getAttached(resource.getProject());
		if (repo == null)
		{
			return NO_CONTRIBUTION_ITEMS;
		}

		Collection<IContributionItem> contributions = new ArrayList<IContributionItem>();
		for (final String remoteBranch : repo.remoteBranches())
		{
			contributions.add(new RemoteContributionItem(repo, remoteBranch));
		}
		return contributions.toArray(new IContributionItem[contributions.size()]);
	}

	private static class RemoteContributionItem extends ContributionItem
	{
		private GitRepository repo;
		private String remoteBranch;

		RemoteContributionItem(GitRepository repo, String remoteBranch)
		{
			this.repo = repo;
			this.remoteBranch = remoteBranch;
		}

		@Override
		public void fill(Menu menu, int index)
		{
			MenuItem menuItem = new MenuItem(menu, SWT.PUSH, index);
			menuItem.setText(remoteBranch);
			menuItem.addSelectionListener(new SelectionAdapter()
			{
				public void widgetSelected(SelectionEvent e)
				{
					// what to do when menu is subsequently selected.
					PullFromRemoteHandler.pullFromRemoteBranch(repo, remoteBranch);
				}
			});
		}
	}
}
