package com.aptana.git.core.model;

/**
 * Listeners for changes to the collection of git repositories.
 * 
 * @author cwilliams
 */
public interface IGitRepositoriesListener
{

	/**
	 * A project has been mapped to a new repository (new to us, may have already existed on disk).
	 * 
	 * @param e
	 */
	public void repositoryAdded(RepositoryAddedEvent e);

	/**
	 * Called when a repository is unmapped from a project
	 * 
	 * @param e
	 */
	public void repositoryRemoved(RepositoryRemovedEvent e);
}
