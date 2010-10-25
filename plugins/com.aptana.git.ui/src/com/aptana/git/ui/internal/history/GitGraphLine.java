/**
 * This file Copyright (c) 2005-2010 Aptana, Inc. This program is
 * dual-licensed under both the Aptana Public License and the GNU General
 * Public license. You may elect to use one or the other of these licenses.
 * 
 * This program is distributed in the hope that it will be useful, but
 * AS-IS and WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE, TITLE, or
 * NONINFRINGEMENT. Redistribution, except as permitted by whichever of
 * the GPL or APL you select, is prohibited.
 *
 * 1. For the GPL license (GPL), you can redistribute and/or modify this
 * program under the terms of the GNU General Public License,
 * Version 3, as published by the Free Software Foundation.  You should
 * have received a copy of the GNU General Public License, Version 3 along
 * with this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 * 
 * Aptana provides a special exception to allow redistribution of this file
 * with certain other free and open source software ("FOSS") code and certain additional terms
 * pursuant to Section 7 of the GPL. You may view the exception and these
 * terms on the web at http://www.aptana.com/legal/gpl/.
 * 
 * 2. For the Aptana Public License (APL), this program and the
 * accompanying materials are made available under the terms of the APL
 * v1.0 which accompanies this distribution, and is available at
 * http://www.aptana.com/legal/apl/.
 * 
 * You may view the GPL, Aptana's exception and additional terms, and the
 * APL in the file titled license.html at the root of the corresponding
 * plugin containing this source file.
 * 
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
		if (!(obj instanceof GitGraphLine))
			return false;

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
