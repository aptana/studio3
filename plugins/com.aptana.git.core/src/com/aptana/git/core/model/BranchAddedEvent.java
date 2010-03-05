package com.aptana.git.core.model;

public class BranchAddedEvent extends RepositoryEvent
{

	private String branchName;

	BranchAddedEvent(GitRepository repository, String branchName)
	{
		super(repository);
		this.branchName = branchName;
	}

	public String getBranchName()
	{
		return branchName;
	}
}
