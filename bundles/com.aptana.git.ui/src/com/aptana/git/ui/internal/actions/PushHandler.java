/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.git.ui.internal.actions;

import java.util.Set;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

import com.aptana.core.util.ArrayUtil;
import com.aptana.core.util.CollectionsUtil;
import com.aptana.git.core.model.GitRepository;

/**
 * @author cwilliams
 */
public class PushHandler extends AbstractSimpleGitCommandHandler
{

	/**
	 * Base command
	 */
	private static final String COMMAND = "push"; //$NON-NLS-1$

	/**
	 * The "default" remote
	 */
	private static final String ORIGIN = "origin"; //$NON-NLS-1$

	@Override
	protected Object doExecute(ExecutionEvent event) throws ExecutionException
	{
		final Set<GitRepository> repos = getSelectedRepositories();
		if (CollectionsUtil.isEmpty(repos))
		{
			openError(Messages.CommitAction_NoRepo_Title, Messages.CommitAction_NoRepo_Message);
			return null;
		}
		if (!supportsMultipleRepoOperation() && repos.size() > 1)
		{
			openError(Messages.CommitAction_MultipleRepos_Title, Messages.CommitAction_MultipleRepos_Message);
			return null;
		}

		final String[] command = getCommand();
		if (ArrayUtil.isEmpty(command))
		{
			return null;
		}

		// Run one job per repo
		for (GitRepository repo : repos)
		{
			// FIXME http://jira.appcelerator.org/browse/APSTUD-4032
			// If we can detect if the user really needs to do a git push -u <remote> <branch>, do that instead to avoid
			// hang! How can we tell?
			// 1. There's a remote named 'origin'
			// 2. The remote has no matching branch for current
			String currentBranch = repo.currentBranch();
			if (repo.remotes().contains(ORIGIN) && repo.matchingRemoteBranch(currentBranch) == null)
			{
				PushToRemoteHandler.pushBranchToRemote(repo, currentBranch, ORIGIN);
			}
			else
			{
				// run normal push for repo!
				runCommandAsJob(command, repo);
			}
		}
		return null;
	}

	@Override
	protected String[] getCommand()
	{
		return new String[] { COMMAND };
	}

	@Override
	protected void postLaunch(GitRepository repo)
	{
		repo.firePushEvent();
	}

	@Override
	protected boolean calculateEnabled()
	{
		for (GitRepository repo : getSelectedRepositories())
		{
			if (repo == null)
			{
				continue;
			}
			// TODO Explicitly check if there's any remote tracking branches?
			// Just check if we have any remotes to push to
			if (!repo.remotes().isEmpty())
			{
				return true;
			}
		}
		return false;
	}

	protected boolean supportsMultipleRepoOperation()
	{
		return true;
	}
}
