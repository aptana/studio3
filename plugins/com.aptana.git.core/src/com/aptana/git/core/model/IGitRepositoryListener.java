/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
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
	 * User has run a pull via our UI.
	 * 
	 * @param e
	 */
	public void pulled(PullEvent e);

	/**
	 * User has run a push via our UI.
	 * 
	 * @param e
	 */
	public void pushed(PushEvent e);
}
