package com.aptana.git.ui.internal.actions;

import com.aptana.git.core.model.GitRepository;

public class PushHandler extends AbstractSimpleGitCommandHandler
{

	private static final String COMMAND = "push"; //$NON-NLS-1$

	@Override
	protected String[] getCommand()
	{
		return new String[] { COMMAND };
	}

	@Override
	protected void postLaunch(GitRepository repo)
	{
		repo.firePushEvent();
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
