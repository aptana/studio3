package com.aptana.git.ui.internal.history;

import java.util.Collection;
import java.util.Set;

/**
 * Holds the lines for a given cell so that we can graphically draw the branching history.
 * 
 * @author cwilliams
 */
class GraphCellInfo
{

	private Set<GitGraphLine> lines;
	private int position;
	int numColumns;

	GraphCellInfo(int p, Set<GitGraphLine> lines)
	{
		this.position = p;
		this.lines = lines;
	}

	/**
	 * Index of the lane that this commit/cell is specifically attached to.
	 * 
	 * @return
	 */
	int getPosition()
	{
		return position;
	}

	/**
	 * The collection of lines that need to be drawn. Specified in upper and lower halves, and in lane indices.
	 * 
	 * @return
	 */
	Collection<GitGraphLine> getLines()
	{
		return lines;
	}

}
