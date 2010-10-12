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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.aptana.git.core.model.GitCommit;
import com.aptana.git.ui.GitUIPlugin;

/**
 * Traverses an in-order List of GitCommits to generate the necessary info to draw the branching history.
 * 
 * @author cwilliams
 */
class GitGrapher
{
	// GitX uses 32, but I bumped this up to 64 because the Git repo of Git actually uses more than 32 (!)
	private static final int MAX_LANES = 64;
	private List<GitLane> pl;
	private GraphCellInfo previous;

	GitGrapher()
	{
		pl = new ArrayList<GitLane>();
	}

	/**
	 * The method which traverse the list of GitCommits and returns a GraphCellInfo per commit to tell us how to
	 * generate the branch history graphics.
	 * 
	 * @param commits
	 *            an in-order List of GitCommits. The order is reverse chronological (newest to oldest).
	 * @return
	 */
	Map<GitCommit, GraphCellInfo> decorateCommits(List<GitCommit> commits)
	{
		GitLane.resetColors();
		Map<GitCommit, GraphCellInfo> decorations = new HashMap<GitCommit, GraphCellInfo>();
		for (GitCommit commit : commits)
		{
			GraphCellInfo decoration = decorateCommit(commit);
			decorations.put(commit, decoration);
		}
		return decorations;
	}

	/**
	 * Does the actual dirty work of figuring out the lanes and lines for a commit. This method uses the shared state of
	 * {@link #pl} and {@link #previous}. Must be called in reverse chronological order for commits!
	 * 
	 * @param commit
	 * @return
	 */
	private GraphCellInfo decorateCommit(GitCommit commit)
	{
		int i = 0, newPos = -1;
		List<GitLane> currentLanes = new ArrayList<GitLane>();
		List<GitLane> previousLanes = new ArrayList<GitLane>(pl);

		int maxLines = (previousLanes.size() + commit.parentCount() + 2) * 2;
		Set<GitGraphLine> lines = new HashSet<GitGraphLine>(maxLines);

		GitLane currentLane = null;
		boolean didFirst = false;

		// First, iterate over earlier columns and pass through any that don't want this commit
		if (previous != null)
		{
			// We can't count until numColumns here, as it's only used for the width of the cell.
			Iterator<GitLane> it = previousLanes.iterator();
			while (it.hasNext())
			{
				GitLane lane = it.next();
				i++;
				// This is our commit! We should do a "merge": move the line from
				// our upperMapping to their lowerMapping
				if (lane.isCommit(commit.sha()))
				{
					if (!didFirst)
					{
						didFirst = true;
						currentLanes.add(lane);
						currentLane = lane;
						newPos = currentLanes.size();
						lines.add(new GitGraphLine(true, i, newPos, lane.index())); // upper
						if (commit.hasParent()) // we have a parent, so draw a lower half
							lines.add(new GitGraphLine(false, newPos, newPos, lane.index())); // lower
					}
					else
					{
						lines.add(new GitGraphLine(true, i, newPos, lane.index())); // upper
						it.remove();
					}
				}
				else
				{
					// We are not this commit, so this is a 'passing' lane.
					currentLanes.add(lane);
					// upper. map previous lane to current lane
					lines.add(new GitGraphLine(true, i, currentLanes.size(), lane.index()));
					// lower, continue current lane down
					lines.add(new GitGraphLine(false, currentLanes.size(), currentLanes.size(), lane.index()));
				}
			}
		}
		// Add your own parents

		// If we already did the first parent, don't do so again
		if (!didFirst && currentLanes.size() < MAX_LANES && commit.hasParent())
		{
			GitLane newLane = new GitLane(commit.parents().get(0));
			currentLanes.add(newLane);
			newPos = currentLanes.size();
			lines.add(new GitGraphLine(false, newPos, newPos, newLane.index())); // upper
		}

		// Add all other parents

		// If we add at least one parent, we can go back a single column.
		// This boolean will tell us if that happened
		boolean addedParent = false;

		if (commit.parentCount() > 1)
		{
			// Skip the first parent!
			for (String parent : commit.parents().subList(1, commit.parentCount()))
			{
				boolean was_displayed = false;
				for (int x = 0; x < currentLanes.size(); x++)
				{
					GitLane lane = currentLanes.get(x);
					if (lane.isCommit(parent))
					{
						lines.add(new GitGraphLine(false, x + 1, newPos, lane.index())); // lower
						was_displayed = true;
						break;
					}
				}
				if (was_displayed)
					continue;

				if (currentLanes.size() >= MAX_LANES)
					break;

				// Really add this parent
				addedParent = true;
				GitLane newLane = new GitLane(parent);
				currentLanes.add(newLane);
				lines.add(new GitGraphLine(false, currentLanes.size(), newPos, newLane.index())); // lower
			}
		}

		previous = new GraphCellInfo(newPos, lines);
		if (lines.size() > maxLines)
			log("Number of lines: " + lines.size() + " vs allocated: " + maxLines); //$NON-NLS-1$ //$NON-NLS-2$

		// If a parent was added, we have room to not indent.
		if (addedParent)
			previous.numColumns = currentLanes.size() - 1;
		else
			previous.numColumns = currentLanes.size();

		// Update the current lane to point to the new parent
		if (currentLane != null && commit.parentCount() > 0)
			currentLane.setSha(commit.parents().get(0));
		else
			currentLanes.remove(currentLane);

		previousLanes.clear();

		pl = currentLanes;
		return previous;
	}

	private void log(String string)
	{
		if (GitUIPlugin.getDefault() != null)
			GitUIPlugin.logWarning(string);
		else
			System.out.println(string);
	}
}
