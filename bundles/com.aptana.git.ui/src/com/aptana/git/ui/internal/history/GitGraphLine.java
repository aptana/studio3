/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.git.ui.internal.history;

/**
 * Represents a line that needs to be drawn for a commit's branching history graphics. Each line represents one half of
 * the history for a given branch. So we need two lines to represent a branch that continues, one for a merge or split.
 * 
 * @author cwilliams
 */
class GitGraphLine
{

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
	 * Index of the lane. This is really just to track the lane this line is assigned to.
	 */
	private int index;

	GitGraphLine(boolean upper, int from, int to, int index)
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
		if (obj == this)
		{
			return true;
		}

		if (!(obj instanceof GitGraphLine))
		{
			return false;
		}

		GitGraphLine other = (GitGraphLine) obj;
		return upper == other.upper && from == other.from && to == other.to; // FIXME What about index?
	}
	
	@Override
	public int hashCode()
	{
		return (upper + "," + from + "," + to).hashCode(); //$NON-NLS-1$ //$NON-NLS-2$
	}

	boolean isUpper()
	{
		return upper;
	}

	int getFrom()
	{
		return from;
	}

	int getTo()
	{
		return to;
	}

	int getIndex()
	{
		return index;
	}

}
