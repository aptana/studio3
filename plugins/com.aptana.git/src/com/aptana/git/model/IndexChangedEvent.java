package com.aptana.git.model;

public class IndexChangedEvent
{

	private GitRepository repository;

	public IndexChangedEvent(GitRepository repository)
	{
		this.repository = repository;
	}

	public GitRepository getRepository()
	{
		return repository;
	}
}
