package com.aptana.git.ui.internal.actions;

import com.aptana.git.core.model.GitRepository;

public class StashHandler extends AbstractSimpleGitCommandHandler
{

	private static final String COMMAND = "stash"; //$NON-NLS-1$

	@Override
	protected String[] getCommand()
	{
		return new String[] { COMMAND };
	}

	@Override
	protected void postLaunch(GitRepository repo)
	{
//		refreshAffectedProjects(repo); // Should be handled by filewatcher?
	}
	// TODO Only enable if there are staged or unstaged files (but not untracked/new ones!)
}
