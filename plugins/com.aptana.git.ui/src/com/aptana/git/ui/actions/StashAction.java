package com.aptana.git.ui.actions;


public class StashAction extends GitAction
{

	private static final String COMMAND = "stash"; //$NON-NLS-1$

	@Override
	protected String[] getCommand()
	{
		return new String[] { COMMAND };
	}

	@Override
	public void run()
	{
		super.run();
		refreshAffectedProjects();
	}
	// TODO Only enable if there are staged or unstaged files (but not untracked/new ones!)

}
