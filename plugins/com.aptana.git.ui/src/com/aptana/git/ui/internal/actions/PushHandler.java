package com.aptana.git.ui.internal.actions;

import org.eclipse.core.expressions.IEvaluationContext;

import com.aptana.git.core.model.GitRepository;

public class PushHandler extends AbstractSimpleGitCommandHandler
{

	private static final String COMMAND = "push"; //$NON-NLS-1$
	private boolean enabled;

	@Override
	protected String[] getCommand()
	{
		return new String[] { COMMAND };
	}

	@Override
	protected void postLaunch(GitRepository repo)
	{
		repo.firePushEvent();
		refreshRepoIndex(repo);
	}

	@Override
	public void setEnabled(Object evaluationContext)
	{
		this.evalContext = (IEvaluationContext) evaluationContext;
		for (GitRepository repo : getSelectedRepositories())
		{
			if (repo == null)
			{
				continue;
			}
			String[] commits = repo.commitsAhead(repo.currentBranch());
			if (commits != null && commits.length > 0)
			{
				this.enabled = true;
				return;
			}
		}
		this.enabled = false;
		this.evalContext = null;
	}

	@Override
	public boolean isEnabled()
	{
		return this.enabled;
	}

	protected boolean supportsMultipleRepoOperation()
	{
		return true;
	}
}
