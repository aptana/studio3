package com.aptana.git.ui.internal.actions;


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
