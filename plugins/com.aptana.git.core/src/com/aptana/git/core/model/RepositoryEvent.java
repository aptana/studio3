package com.aptana.git.core.model;

public abstract class RepositoryEvent
{
	private GitRepository repository;

	RepositoryEvent(GitRepository repository)
	{
		this.repository = repository;
	}

	public GitRepository getRepository()
	{
		return repository;
	}
}
