package com.aptana.git.ui.actions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.jface.action.ContributionItem;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

import com.aptana.git.core.model.GitExecutable;
import com.aptana.git.core.model.GitRepository;
import com.aptana.git.ui.GitUIPlugin;
import com.aptana.git.ui.internal.Launcher;

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
			return new IContributionItem[0];

		final GitRepository repo = getGitRepositoryManager().getAttached(resource.getProject());
		if (repo == null)
			return new IContributionItem[0];

		Collection<IContributionItem> contributions = new ArrayList<IContributionItem>();

		Set<String> branches = repo.allBranches();
		for (final String branchName : branches)
		{
			contributions.add(new ContributionItem()
			{
				@Override
				public void fill(Menu menu, int index)
				{
					MenuItem menuItem = new MenuItem(menu, SWT.PUSH, index++);
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

	protected void mergeBranch(final GitRepository repo, final String branchName)
	{
		Job job = new Job(NLS.bind("git merge {0}", branchName)) //$NON-NLS-1$
		{
			@Override
			protected IStatus run(IProgressMonitor monitor)
			{
				try
				{
					SubMonitor subMonitor = SubMonitor.convert(monitor, 100);
					ILaunch launch = Launcher.launch(GitExecutable.instance().path().toOSString(),
							repo.workingDirectory(), subMonitor.newChild(75), "merge", //$NON-NLS-1$
							branchName);
					while (!launch.isTerminated())
					{
						Thread.sleep(50);
					}
					repo.index().refresh(subMonitor.newChild(25));
				}
				catch (CoreException e)
				{
					GitUIPlugin.logError(e);
					return e.getStatus();
				}
				catch (Throwable e)
				{
					GitUIPlugin.logError(e.getMessage(), e);
					return new Status(IStatus.ERROR, GitUIPlugin.getPluginId(), e.getMessage());
				}
				return Status.OK_STATUS;
			}
		};
		job.setUser(true);
		job.setPriority(Job.LONG);
		job.schedule();
	}
}
