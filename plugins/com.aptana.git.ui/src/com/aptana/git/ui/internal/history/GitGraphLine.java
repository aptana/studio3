package com.aptana.git.ui.internal.history;

public class GitGraphLine
{

	public boolean isUpper()
	{
		return upper;
	}

	public int getFrom()
	{
		return from;
	}

	public int getTo()
	{
		return to;
	}

	public int getIndex()
	{
		return index;
	}

	/**
	 * Each commit has two distinct regions: upper and lower. Upper sections deals with merges, lower with branches. So
	 * in one cell we can draw a merge and a branch
	 */
	private boolean upper;

	/**
	 * lane we are drawing from
	 */
	private int from;

	/**
	 * Lane we are drawing to.
	 */
	private int to;

	/**
	 * Index of the lane.(?!)
	 */
	private int index;

	public GitGraphLine(boolean upper, int from, int to, int index)
	{
		this.upper = upper;
		if (upper)
		{
			this.from = from;
			this.to = to;
		}
		else
		{
			this.from = to;
			this.to = from;
		}
		this.index = index;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (!(obj instanceof GitGraphLine))
			return false;

		GitGraphLine other = (GitGraphLine) obj;
		return upper == other.upper && from == other.from && to == other.to; // FIXME What about index?
	}

}
