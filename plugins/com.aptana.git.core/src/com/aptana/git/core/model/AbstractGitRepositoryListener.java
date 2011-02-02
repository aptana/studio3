/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.git.core.model;

public abstract class AbstractGitRepositoryListener implements IGitRepositoryListener
{

	public void indexChanged(IndexChangedEvent e)
	{
		// do nothing
	}

	public void branchChanged(BranchChangedEvent e)
	{
		// do nothing
	}

	public void branchAdded(BranchAddedEvent e)
	{
		// do nothing
	}

	public void branchRemoved(BranchRemovedEvent e)
	{
		// do nothing
	}

	public void pulled(PullEvent e)
	{
		// do nothing
	}

	public void pushed(PushEvent e)
	{
		// do nothing
	}

}
