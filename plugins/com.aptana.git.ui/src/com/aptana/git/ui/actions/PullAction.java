package com.aptana.git.ui.actions;

import com.aptana.git.ui.internal.actions.GitAction;

public class PullAction extends GitAction
{

	@Override
	protected String[] getCommand()
	{
		return new String[] { "pull" }; //$NON-NLS-1$
	}

	@Override
	public void run()
	{
		super.run();
		refreshAffectedProjects();
	}
}
