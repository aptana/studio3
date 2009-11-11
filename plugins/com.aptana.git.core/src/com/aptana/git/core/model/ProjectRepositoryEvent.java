package com.aptana.git.core.model;

import org.eclipse.core.resources.IProject;

public abstract class ProjectRepositoryEvent extends RepositoryEvent
{
	private IProject project;

	ProjectRepositoryEvent(GitRepository repository, IProject p)
	{
		super(repository);
		this.project = p;
	}

	public IProject getProject()
	{
		return project;
	}
}
