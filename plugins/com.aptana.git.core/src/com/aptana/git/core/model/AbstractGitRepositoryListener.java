package com.aptana.git.core.model;

public abstract class AbstractGitRepositoryListener implements IGitRepositoryListener
{

	@Override
	public void indexChanged(IndexChangedEvent e)
	{
		// do nothing
	}

	@Override
	public void branchChanged(BranchChangedEvent e)
	{
		// do nothing
	}

	@Override
	public void branchAdded(BranchAddedEvent e)
	{
		// do nothing
	}

	@Override
	public void branchRemoved(BranchRemovedEvent e)
	{
		// do nothing
	}

	@Override
	public void pulled(PullEvent e)
	{
		// do nothing
	}

	@Override
	public void pushed(PushEvent e)
	{
		// do nothing
	}

}
