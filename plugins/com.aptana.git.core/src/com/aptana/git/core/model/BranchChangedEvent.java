package com.aptana.git.core.model;

public class BranchChangedEvent extends RepositoryEvent
{

	private String oldBranchName;
	private String newBranchName;

	BranchChangedEvent(GitRepository repository, String oldBranchName, String newBranchName)
	{
		super(repository);
		this.oldBranchName = oldBranchName;
		this.newBranchName = newBranchName;
	}

	public String getOldBranchName()
	{
		return oldBranchName;
	}

	public String getNewBranchName()
	{
		return newBranchName;
	}
}
