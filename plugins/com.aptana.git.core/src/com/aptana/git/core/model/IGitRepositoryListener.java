package com.aptana.git.core.model;

public interface IGitRepositoryListener
{

	/**
	 * A repository's index has changed.
	 * 
	 * @param e
	 */
	public void indexChanged(IndexChangedEvent e);

	/**
	 * A project has been mapped to a new repository (new to us, may have already existed on disk).
	 * 
	 * @param e
	 */
	public void repositoryAdded(RepositoryAddedEvent e);
}
