package com.aptana.git.core.model;

public class RepositoryAddedEvent
{

	private GitRepository repository;

	public RepositoryAddedEvent(GitRepository repository)
	{
		this.repository = repository;
	}

	public GitRepository getRepository()
	{
		return repository;
	}
}
