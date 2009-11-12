package com.aptana.git.ui.internal.actions;

import com.aptana.git.ui.actions.SimpleGitCommandAction;


public class StatusAction extends SimpleGitCommandAction
{

	@Override
	protected String[] getCommand()
	{
		return new String[] { "status" }; //$NON-NLS-1$
	}

	@Override
	protected void postLaunch()
	{
		// do nothing
	}
}
