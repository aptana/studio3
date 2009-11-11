package com.aptana.git.ui.actions;

import com.aptana.git.ui.internal.actions.GitAction;

public class UnstashAction extends GitAction
{

	@Override
	protected String[] getCommand()
	{
		return new String[] { "stash", "apply" }; //$NON-NLS-1$ //$NON-NLS-2$
	}

	@Override
	public void run()
	{
		super.run();
		refreshAffectedProjects();
	}
	// TODO Only enable if there's a "ref/stash" ref!
}
