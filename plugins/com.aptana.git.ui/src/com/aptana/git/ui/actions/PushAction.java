package com.aptana.git.ui.actions;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;

import com.aptana.git.core.model.GitRepository;
import com.aptana.git.ui.internal.GitLightweightDecorator;

public class PushAction extends GitAction
{

	private static final String COMMAND = "push"; //$NON-NLS-1$

	@Override
	protected String[] getCommand()
	{
		return new String[] { COMMAND };
	}

	public void run()
	{
		super.run();

		// TODO It'd be nice if we could just tell it to update the labels of the projects attached to the repo (and
		// only the project, not it's children)!
		GitLightweightDecorator.refresh();
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
