/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
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
import com.aptana.git.ui.internal.actions.PushTagsHandler;

/**
 * Represents a named remote, which all tags will be pushed to.
 * 
 * @author cwilliams
 */
public class PushTagsItem extends AbstractDynamicBranchItem
{

	public PushTagsItem()
	{
		super();
	}

	public PushTagsItem(String id)
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
		for (final String remote : repo.remotes())
		{
			contributions.add(new RemoteContributionItem(repo, remote));
		}
		return contributions.toArray(new IContributionItem[contributions.size()]);
	}

	private static class RemoteContributionItem extends ContributionItem
	{
		private GitRepository repo;
		private String remote;

		RemoteContributionItem(GitRepository repo, String remote)
		{
			this.repo = repo;
			this.remote = remote;
		}

		@Override
		public void fill(Menu menu, int index)
		{
			MenuItem menuItem = new MenuItem(menu, SWT.PUSH, index);
			menuItem.setText(remote);
			menuItem.addSelectionListener(new SelectionAdapter()
			{
				public void widgetSelected(SelectionEvent e)
				{
					// what to do when menu is subsequently selected.
					PushTagsHandler.pushTagsToRemote(repo, remote);
				}
			});
		}
	}
}
