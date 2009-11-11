package com.aptana.git.ui.actions;

import com.aptana.git.ui.internal.actions.SimpleGitCommandAction;

public class UnstashAction extends SimpleGitCommandAction
{

	@Override
	protected String[] getCommand()
	{
		return new String[] { "stash", "apply" }; //$NON-NLS-1$ //$NON-NLS-2$
	}

	@Override
	protected void postLaunch()
	{
		refreshAffectedProjects();
	}
	// TODO Only enable if there's a "ref/stash" ref!
}
