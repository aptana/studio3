/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
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

import com.aptana.core.logging.IdeLog;
import com.aptana.git.core.GitPlugin;
import com.aptana.git.core.IDebugScopes;
import com.aptana.git.core.model.GitRepository;
import com.aptana.git.ui.GitUIPlugin;
import com.aptana.git.ui.internal.Launcher;
import com.aptana.ui.MenuDialogItem;
import com.aptana.ui.QuickMenuDialog;

/**
 * Runs a "git push <remote> HEAD" for current branch. So if you choose 'origin' and are on branch 'master', it'd run
 * "git push origin master".
 * 
 * @author cwilliams
 */
public class PushToRemoteHandler extends AbstractGitHandler
{

	@Override
	protected Object doExecute(ExecutionEvent event) throws ExecutionException
	{
		final GitRepository repo = getSelectedRepository();
		if (repo == null)
		{
			return null;
		}

		final String currentBranch = repo.currentBranch();
		List<MenuDialogItem> remotes = new ArrayList<MenuDialogItem>();
		for (String remote : repo.remotes())
		{
			remotes.add(new MenuDialogItem(remote));
		}
		if (!remotes.isEmpty())
		{
			QuickMenuDialog dialog = new QuickMenuDialog(getShell());
			dialog.setInput(remotes);
			if (dialog.open() != -1)
			{
				MenuDialogItem item = remotes.get(dialog.getReturnCode());
				pushBranchToRemote(repo, currentBranch, item.getText());
			}
		}
		return null;
	}

	public static void pushBranchToRemote(final GitRepository repo, final String branchName, final String remoteName)
	{
		Job job = new Job(NLS.bind("git push {0} {1}", remoteName, branchName)) //$NON-NLS-1$
		{
			@Override
			protected IStatus run(IProgressMonitor monitor)
			{
				SubMonitor subMonitor = SubMonitor.convert(monitor, 100);
				if (subMonitor.isCanceled())
				{
					return Status.CANCEL_STATUS;
				}

				if (!repo.enterWriteProcess())
				{
					return new Status(IStatus.ERROR, GitPlugin.getPluginId(),
							Messages.GitLaunchDelegate_FailedToAcquireWriteLock);
				}
				try
				{
					ILaunch launch = Launcher.launch(repo, subMonitor.newChild(75), "push", //$NON-NLS-1$
							remoteName, branchName);
					while (!launch.isTerminated())
					{
						if (subMonitor.isCanceled())
						{
							launch.terminate();
							return Status.CANCEL_STATUS;
						}
						Thread.yield();
					}
				}
				catch (CoreException e)
				{
					IdeLog.log(GitUIPlugin.getDefault(), e.getStatus());
					return e.getStatus();
				}
				catch (Throwable e)
				{
					IdeLog.logError(GitUIPlugin.getDefault(), e, IDebugScopes.DEBUG);
					return new Status(IStatus.ERROR, GitUIPlugin.getPluginId(), e.getMessage());
				}
				finally
				{
					repo.exitWriteProcess();
				}
				subMonitor.done();
				return Status.OK_STATUS;
			}
		};
		job.setUser(true);
		job.setPriority(Job.LONG);
		job.schedule();
	}

}
