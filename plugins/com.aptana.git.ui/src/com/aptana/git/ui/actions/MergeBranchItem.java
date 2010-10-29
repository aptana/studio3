package com.aptana.git.ui.actions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

import org.eclipse.core.resources.IResource;
import org.eclipse.jface.action.ContributionItem;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

import com.aptana.git.core.model.GitRepository;
import com.aptana.git.ui.internal.actions.MergeBranchHandler;

public class MergeBranchItem extends AbstractDynamicBranchItem
{

	public MergeBranchItem()
	{
		super();
	}

	public MergeBranchItem(String id)
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

		Set<String> branches = repo.allBranches();
		for (final String branchName : branches)
		{
			contributions.add(new ContributionItem()
			{
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
							mergeBranch(repo, branchName);
						}
					});
				}
			});
		}
		return contributions.toArray(new IContributionItem[contributions.size()]);
	}

	private void mergeBranch(final GitRepository repo, final String branchName)
	{
		MergeBranchHandler.mergeBranch(repo, branchName);
	}
}
