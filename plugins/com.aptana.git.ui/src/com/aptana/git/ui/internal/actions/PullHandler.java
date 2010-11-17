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
		// refreshAffectedProjects(); Handled by GitProjectRefresher
	}

	protected boolean supportsMultipleRepoOperation()
	{
		return true;
	}

}
