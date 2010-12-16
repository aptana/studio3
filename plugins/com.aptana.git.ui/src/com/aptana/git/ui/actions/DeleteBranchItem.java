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
import com.aptana.git.ui.internal.actions.DeleteBranchHandler;

public class DeleteBranchItem extends AbstractDynamicBranchItem
{

	public DeleteBranchItem()
	{
		super();
	}

	public DeleteBranchItem(String id)
	{
		super(id);
	}

	@Override
	protected IContributionItem[] getContributionItems()
	{
		IResource resource = getSelectedResource();
		if (resource == null)
		{
			return new IContributionItem[0];
		}

		final GitRepository repo = getGitRepositoryManager().getAttached(resource.getProject());
		if (repo == null)
		{
			return new IContributionItem[0];
		}

		Collection<IContributionItem> contributions = new ArrayList<IContributionItem>();
		for (final String branchName : repo.localBranches())
		{
			contributions.add(new DeleteBranchContributionItem(repo, branchName));
		}
		return contributions.toArray(new IContributionItem[contributions.size()]);
	}

	private class DeleteBranchContributionItem extends ContributionItem
	{

		private GitRepository repo;
		private String branchName;

		DeleteBranchContributionItem(GitRepository repo, String branchName)
		{
			this.repo = repo;
			this.branchName = branchName;
		}

		@Override
		public void fill(Menu menu, int index)
		{
			MenuItem menuItem = new MenuItem(menu, SWT.PUSH, index);
			menuItem.setText(branchName);
			menuItem.setEnabled(!branchName.equals(repo.currentBranch()));
			menuItem.addSelectionListener(new SelectionAdapter()
			{
				public void widgetSelected(SelectionEvent e)
				{
					// what to do when menu is subsequently selected.
					DeleteBranchHandler.deleteBranch(repo, branchName);
				}
			});
		}
	}
}
