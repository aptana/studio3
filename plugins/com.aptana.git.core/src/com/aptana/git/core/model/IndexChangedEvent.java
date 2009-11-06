package com.aptana.git.core.model;

import java.util.Collection;
import java.util.Collections;

public class IndexChangedEvent extends RepositoryEvent
{

	private Collection<ChangedFile> changedFiles;

	IndexChangedEvent(GitRepository repository, Collection<ChangedFile> changedFiles)
	{
		super(repository);
		this.changedFiles = changedFiles;
	}

	public Collection<ChangedFile> changedFiles()
	{
		return Collections.unmodifiableCollection(changedFiles);
	}

}
