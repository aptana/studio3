/**
 * Aptana Studio
 * Copyright (c) 2013 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.git.ui.internal.actions;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.osgi.util.NLS;

import com.aptana.core.util.StringUtil;
import com.aptana.git.core.model.GitRepository;
import com.aptana.ui.MenuDialogItem;
import com.aptana.ui.QuickMenuDialog;

/**
 * Runs a "git pull <remote> <branch>". Will prompt the user to select a remote/branch combination.
 * 
 * @author cwilliams
 */
public class PullFromRemoteHandler extends AbstractGitHandler
{

	@Override
	protected Object doExecute(ExecutionEvent event) throws ExecutionException
	{
		final GitRepository repo = getSelectedRepository();
		if (repo == null)
		{
			return null;
		}

		List<MenuDialogItem> remoteBranches = new ArrayList<MenuDialogItem>();
		for (String remoteBranch : repo.remoteBranches())
		{
			remoteBranches.add(new MenuDialogItem(remoteBranch));
		}
		if (!remoteBranches.isEmpty())
		{
			QuickMenuDialog dialog = new QuickMenuDialog(getShell(), Messages.PullFromRemoteHandler_PopupTitle);
			dialog.setInput(remoteBranches);
			if (dialog.open() != -1)
			{
				MenuDialogItem item = remoteBranches.get(dialog.getReturnCode());

				pullFromRemoteBranch(repo, item.getText());
			}
		}
		return null;
	}

	/**
	 * @param repo
	 * @param remoteBranch
	 *            A combination of remote and branch in the form: "<remote>/<branch>"
	 */
	public static void pullFromRemoteBranch(final GitRepository repo, final String remoteBranch)
	{
		List<String> parts = StringUtil.split(remoteBranch, GitRepository.BRANCH_DELIMITER);
		final String remote = parts.get(0);
		final String branch = parts.get(1);
		Job job = new Job(NLS.bind("git pull {0} {1}", remote, branch)) //$NON-NLS-1$
		{
			@Override
			protected IStatus run(IProgressMonitor monitor)
			{
				SubMonitor subMonitor = SubMonitor.convert(monitor, 100);
				if (subMonitor.isCanceled())
				{
					return Status.CANCEL_STATUS;
				}

				return repo.pull(remote, branch);
			}
		};
		job.setUser(true);
		job.setPriority(Job.LONG);
		job.schedule();
	}

}
