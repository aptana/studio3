package com.aptana.git.ui.actions;


public class PullAction extends SimpleGitCommandAction
{

	@Override
	protected String[] getCommand()
	{
		return new String[] { "pull" }; //$NON-NLS-1$
	}

	@Override
	protected void postLaunch()
	{
		getSelectedRepository().firePullEvent();
		// Refresh the in-memory index of the repo!
		refreshRepoIndex();		
//		refreshAffectedProjects(); Handled by GitProjectRefresher
	}
}
