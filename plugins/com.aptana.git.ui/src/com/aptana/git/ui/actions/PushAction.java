package com.aptana.git.ui.actions;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;

import com.aptana.git.core.model.GitRepository;

public class PushAction extends SimpleGitCommandAction
{

	private static final String COMMAND = "push"; //$NON-NLS-1$

	@Override
	protected String[] getCommand()
	{
		return new String[] { COMMAND };
	}

	@Override
	protected void postLaunch()
	{
		refreshRepoIndex();
	}

	@Override
	public boolean isEnabled()
	{
		IResource[] resources = getSelectedResources();
		if (resources == null || resources.length != 1)
			return false;
		IProject project = resources[0].getProject();
		GitRepository repo = GitRepository.getAttached(project);
		if (repo == null)
			return false;
		String[] commits = repo.commitsAhead(repo.currentBranch());
		return commits != null && commits.length > 0;
	}
}
