package com.aptana.git.ui.actions;


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
