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
	 * Users has run a pull via our UI.
	 * 
	 * @param e
	 */
	public void pulled(PullEvent e);

	/**
	 * Users has run a push via our UI.
	 * 
	 * @param e
	 */
	public void pushed(PushEvent e);
}
