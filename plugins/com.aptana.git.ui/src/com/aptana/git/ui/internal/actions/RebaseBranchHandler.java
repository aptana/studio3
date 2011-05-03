package com.aptana.git.ui.internal.actions;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.osgi.util.NLS;

import com.aptana.git.core.model.GitRepository;
import com.aptana.git.ui.GitUIPlugin;
import com.aptana.git.ui.internal.Launcher;
import com.aptana.ui.MenuDialogItem;
import com.aptana.ui.QuickMenuDialog;

/**
 * Runs a "git rebase <branch>" on HEAD. So if you choose master and are on branch 'topic', it'd be rebasing topic on
 * master (or "git rebase master topic)".
 * 
 * @author cwilliams
 */
public class RebaseBranchHandler extends AbstractGitHandler
{

	@Override
	protected Object doExecute(ExecutionEvent event) throws ExecutionException
	{
		final GitRepository repo = getSelectedRepository();
		if (repo == null)
		{
			return null;
		}

		String currentBranch = repo.currentBranch();
		List<MenuDialogItem> listOfMaps = new ArrayList<MenuDialogItem>();
		for (String branch : repo.allBranches())
		{
			if (branch.equals(currentBranch))
			{
				continue;
			}
			listOfMaps.add(new MenuDialogItem(branch));
		}
		if (!listOfMaps.isEmpty())
		{
			QuickMenuDialog dialog = new QuickMenuDialog(getShell());
			dialog.setInput(listOfMaps);
			if (dialog.open() != -1)
			{
				MenuDialogItem item = listOfMaps.get(dialog.getReturnCode());
				rebaseBranch(repo, item.getText());
			}
		}
		return null;
	}

	public static void rebaseBranch(final GitRepository repo, final String branchName)
	{
		Job job = new Job(NLS.bind("git rebase {0}", branchName)) //$NON-NLS-1$
		{
			@Override
			protected IStatus run(IProgressMonitor monitor)
			{
				SubMonitor subMonitor = SubMonitor.convert(monitor, 100);

				repo.enterWriteProcess();
				try
				{
					ILaunch launch = Launcher.launch(repo, subMonitor.newChild(75), "rebase", //$NON-NLS-1$
							branchName);
					while (!launch.isTerminated())
					{
						Thread.sleep(50);
					}
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
				finally
				{
					repo.exitWriteProcess();
				}
				repo.index().refresh(subMonitor.newChild(25));
				return Status.OK_STATUS;
			}
		};
		job.setUser(true);
		job.setPriority(Job.LONG);
		job.schedule();
	}

}
