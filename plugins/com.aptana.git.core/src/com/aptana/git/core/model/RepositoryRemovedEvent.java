package com.aptana.git.core.model;

import org.eclipse.core.resources.IProject;

public class RepositoryRemovedEvent extends ProjectRepositoryEvent
{

	RepositoryRemovedEvent(GitRepository repository, IProject p)
	{
		super(repository, p);
	}

}
