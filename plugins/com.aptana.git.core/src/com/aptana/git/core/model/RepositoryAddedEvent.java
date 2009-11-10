package com.aptana.git.core.model;

import org.eclipse.core.resources.IProject;

public class RepositoryAddedEvent extends ProjectRepositoryEvent
{

	RepositoryAddedEvent(GitRepository repository, IProject p)
	{
		super(repository, p);
	}
}
