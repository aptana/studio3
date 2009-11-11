package com.aptana.git.ui.actions;

import com.aptana.git.ui.internal.GitLightweightDecorator;

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
		refreshAffectedProjects();
		// TODO It'd be nice if we could just tell it to update the labels of the projects attached to the repo (and
		// only the project, not it's children)!
		GitLightweightDecorator.refresh();
	}
}
