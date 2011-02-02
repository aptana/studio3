/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.git.ui.internal.history;

/**
 * @author cwilliams
 */
class GitLane
{

	/**
	 * Global incrementing index. This is so that a new lane gets a new index.
	 */
	private static int fgIndex = 0;

	/**
	 * SHA representing the parent that this lane is tracking.
	 */
	private String fSha;

	/**
	 * Index of the current lane.
	 */
	private int fIndex;

	GitLane(String sha)
	{
		fIndex = fgIndex++;
		fSha = sha;
	}

	/**
	 * To determine if this lane is the one where the passed in commit's SHA is attached to.
	 * 
	 * @param sha
	 * @return
	 */
	boolean isCommit(String sha)
	{
		return fSha.equals(sha);
	}

	/**
	 * Index of the lane. Used to identify the lane for coloring and tracking.
	 * 
	 * @return
	 */
	int index()
	{
		return fIndex;
	}

	/**
	 * Set the new commit/parent SHA to track.
	 * 
	 * @param sha
	 */
	void setSha(String sha)
	{
		fSha = sha;
	}

	/**
	 * Used to reset our global incrementer.
	 */
	static void resetColors()
	{
		fgIndex = 0;
	}

}
