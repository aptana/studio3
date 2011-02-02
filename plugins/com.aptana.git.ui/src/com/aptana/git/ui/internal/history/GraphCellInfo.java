/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
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
