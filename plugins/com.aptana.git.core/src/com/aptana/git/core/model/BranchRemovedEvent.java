package com.aptana.git.core.model;

public class BranchRemovedEvent extends RepositoryEvent
{

	private String branchName;

	BranchRemovedEvent(GitRepository repository, String branchName)
	{
		super(repository);
		this.branchName = branchName;
	}

	public String getBranchName()
	{
		return branchName;
	}
}
