package com.aptana.git.ui.internal.actions;

import com.aptana.git.core.model.GitRepository;

public class UnstashHandler extends AbstractSimpleGitCommandHandler
{

	@Override
	protected String[] getCommand()
	{
		return new String[] { "stash", "apply" }; //$NON-NLS-1$ //$NON-NLS-2$
	}

	@Override
	protected void postLaunch(GitRepository repo)
	{
		// refreshAffectedProjects(repo); // Should be handled by filewatcher?
	}
	// TODO Only enable if there's a "ref/stash" ref!
}
