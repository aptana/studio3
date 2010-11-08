package com.aptana.git.ui.internal.actions;

import com.aptana.git.core.model.GitRepository;

public class StatusHandler extends AbstractSimpleGitCommandHandler
{
	@Override
	protected String[] getCommand()
	{
		return new String[] { "status" }; //$NON-NLS-1$
	}

	@Override
	protected void postLaunch(GitRepository repo)
	{
		// do nothing
	}
}
