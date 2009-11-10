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

	/**
	 * The working branch has been changed for a repository
	 * 
	 * @param e
	 */
	public void branchChanged(BranchChangedEvent e);

	/**
	 * Called when a repository is unmapped from a project
	 * 
	 * @param e
	 */
	public void repositoryRemoved(RepositoryRemovedEvent e);
}
