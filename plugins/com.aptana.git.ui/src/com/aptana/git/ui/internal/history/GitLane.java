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
	 * The SHA of the commit/parent this lane is tracking.
	 * 
	 * @return
	 */
	String sha()
	{
		return fSha;
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
