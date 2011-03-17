package com.aptana.git.ui.internal.actions;

import com.aptana.git.core.model.GitRepository;

public class PullHandler extends AbstractSimpleGitCommandHandler
{

	@Override
	protected String[] getCommand()
	{
		return new String[] { "pull" }; //$NON-NLS-1$
	}

	@Override
	protected void postLaunch(GitRepository repo)
	{
		repo.firePullEvent();
		// Refresh the in-memory index of the repo!
		refreshRepoIndex(repo);
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
			// Just check if we have any remotes to pull from
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
