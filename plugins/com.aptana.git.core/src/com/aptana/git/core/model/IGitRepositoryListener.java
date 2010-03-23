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
	 * A new local branch has been added for a repository
	 * 
	 * @param e
	 */
	public void branchAdded(BranchAddedEvent e);

	/**
	 * A local branch has been deleted for a repository
	 * 
	 * @param e
	 */
	public void branchRemoved(BranchRemovedEvent e);

	/**
	 * Called when a repository is unmapped from a project
	 * 
	 * @param e
	 */
	public void repositoryRemoved(RepositoryRemovedEvent e);

	/**
	 * Users has run a pull via our UI. FIXME Detect pulls externally!
	 * 
	 * @param e
	 */
	public void pulled(PullEvent e);

	/**
	 * Users has run a push via our UI. FIXME Detect pushes externally!
	 * 
	 * @param e
	 */
	public void pushed(PushEvent e);
}
