/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
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
import com.aptana.git.ui.internal.actions.DeleteRemoteHandler;

public class DeleteRemoteItem extends AbstractDynamicBranchItem
{

	public DeleteRemoteItem()
	{
		super();
	}

	public DeleteRemoteItem(String id)
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
		for (final String remoteName : repo.remotes())
		{
			contributions.add(new DeleteRemoteContributionItem(repo, remoteName));
		}
		return contributions.toArray(new IContributionItem[contributions.size()]);
	}

	private static class DeleteRemoteContributionItem extends ContributionItem
	{

		private GitRepository repo;
		private String remoteName;

		DeleteRemoteContributionItem(GitRepository repo, String remoteName)
		{
			this.repo = repo;
			this.remoteName = remoteName;
		}

		@Override
		public void fill(Menu menu, int index)
		{
			MenuItem menuItem = new MenuItem(menu, SWT.PUSH, index);
			menuItem.setText(remoteName);
			menuItem.addSelectionListener(new SelectionAdapter()
			{
				public void widgetSelected(SelectionEvent e)
				{
					// what to do when menu is subsequently selected.
					DeleteRemoteHandler.deleteRemote(repo, remoteName);
				}
			});
		}
	}
}
