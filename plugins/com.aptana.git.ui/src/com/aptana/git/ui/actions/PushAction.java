package com.aptana.git.ui.actions;

import com.aptana.git.core.model.GitRepository;

public class PushAction extends SimpleGitCommandAction
{

	private static final String COMMAND = "push"; //$NON-NLS-1$

	@Override
	protected String[] getCommand()
	{
		return new String[] { COMMAND };
	}

	@Override
	protected void postLaunch()
	{
		getSelectedRepository().firePushEvent();
		refreshRepoIndex();
	}

	@Override
	public boolean isEnabled()
	{
		GitRepository repo = getSelectedRepository();
		if (repo == null)
			return false;
		String[] commits = repo.commitsAhead(repo.currentBranch());
		return commits != null && commits.length > 0;
	}
}
