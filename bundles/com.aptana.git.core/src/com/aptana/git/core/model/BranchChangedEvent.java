/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.git.core.model;

public class BranchChangedEvent extends RepositoryEvent
{

	private String oldBranchName;
	private String newBranchName;

	BranchChangedEvent(GitRepository repository, String oldBranchName, String newBranchName)
	{
		super(repository);
		this.oldBranchName = oldBranchName;
		this.newBranchName = newBranchName;
	}

	public String getOldBranchName()
	{
		return oldBranchName;
	}

	public String getNewBranchName()
	{
		return newBranchName;
	}
}
