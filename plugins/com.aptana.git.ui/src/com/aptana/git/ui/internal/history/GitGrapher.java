package com.aptana.git.ui.internal.history;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.aptana.git.core.model.GitCommit;
import com.aptana.git.core.model.GitRepository;
import com.aptana.git.core.model.GitRevList;
import com.aptana.git.core.model.GitRevSpecifier;

public class GitGrapher
{
	private static final int MAX_LANES = 32;
	private List<GitLane> pl;
	private GraphCellInfo previous;

	GitGrapher()
	{
		pl = new ArrayList<GitLane>();
	}

	public Map<GitCommit, GraphCellInfo> decorateCommits(List<GitCommit> commits)
	{
		Map<GitCommit, GraphCellInfo> decorations = new HashMap<GitCommit, GraphCellInfo>();
		for (GitCommit commit : commits)
		{
			GraphCellInfo decoration = decorateCommit(commit);
			decorations.put(commit, decoration);
		}
		return decorations;
	}

	// TODO Walk through the commits and create lanes to track hierarchy
	// private void generateLanes(List<GitCommit> commits)
	// {
	// GitLane currentLane = null;
	// List<GitLane> lanes = new ArrayList<GitLane>();
	// lanes.add(new GitLane(commits.get(0).sha())); // add a lane for root path
	// for (GitCommit gitCommit : commits)
	// {
	// // TODO What if two (or more) lanes have the same SHA at this point? We need to merge them...
	// mergeLanes(lanes);
	// for (GitLane lane : lanes)
	// { // draw the lanes
	// if (lane.isCommit(gitCommit.sha())) // our lane for this commit
	// {
	// System.out.print("* ");
	// currentLane = lane;
	// }
	// else
	// System.out.print("| ");
	// }
	// System.out.println(" " + gitCommit.sha());
	//
	// // for first parent, add it to our current lane?
	// String firstParent = gitCommit.parents().get(0);
	// currentLane.setSha(firstParent);
	// add_line(currentLane.index(), currentLane.index()); // vertical line, stay in current lane
	// if (gitCommit.parentCount() > 1)
	// { // Split off new lane(s)
	// // Adding a new lane for each parent > 1
	// for (String parent : gitCommit.parents().subList(1, gitCommit.parentCount()))
	// {
	// GitLane newLane = new GitLane(parent);
	// lanes.add(newLane);
	// // Add a line from our current lane center to new lane track
	// add_line(currentLane.index(), newLane.index());
	// }
	// System.out.println("|\\");
	// }
	// }
	// }

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
					// We are not this commit.
					currentLanes.add(lane);
					lines.add(new GitGraphLine(true, i, currentLanes.size(), lane.index())); // upper. map previous lane
					// to current lane
					lines.add(new GitGraphLine(false, currentLanes.size(), currentLanes.size(), lane.index())); // lower,
					// continue
					// current
					// lane
					// down
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
			lines.add(new GitGraphLine(true, newPos, newPos, newLane.index()));
		}

		// Add all other parents

		// If we add at least one parent, we can go back a single column.
		// This boolean will tell us if that happened
		boolean addedParent = false;

		if (commit.parentCount() > 1)
		{
			for (String parent : commit.parents().subList(1, commit.parentCount())) // Skip the first parent!
			{
				int x = 0;
				boolean was_displayed = false;
				Iterator<GitLane> it = currentLanes.iterator();
				while (it.hasNext())
				{
					x++;
					GitLane lane = it.next();
					if (lane.isCommit(parent))
					{
						lines.add(new GitGraphLine(false, x, newPos, lane.index()));
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
			log("Number of lines: " + lines.size() + " vs allocated: " + maxLines);

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
		// TODO Auto-generated method stub

	}

	public static void main(String[] args) throws URISyntaxException
	{
		URI path = new URI(null, null, "/Users/cwilliams/workspaces/master/gitx", null);
		GitRepository repo = GitRepository.getUnattachedExisting(path);
		GitRevList list = new GitRevList(repo);
		list.walkRevisionListWithSpecifier(new GitRevSpecifier("master"), -1);
		List<GitCommit> commits = list.getCommits();
		Map<GitCommit, GraphCellInfo> decorations = new GitGrapher().decorateCommits(commits);
		for (GitCommit commit : commits)
		{
			GraphCellInfo info = decorations.get(commit);
			// fill with spaces
			StringBuilder upper = new StringBuilder();
			for (int i = 0; i < info.getLines().size(); i++)
			{
				upper.append("  ");
			}
			upper.append(commit.sha());
			StringBuilder lower = new StringBuilder();
			for (int i = 0; i < info.getLines().size(); i++)
			{
				lower.append("  ");
			}
			for (GitGraphLine line : info.getLines())
			{
				StringBuilder target = upper;
				if (!line.isUpper())
					target = lower;
				int from = line.getFrom();
				int to = line.getTo();
				int stringIndex = (from - 1) * 2;
				if (from == to)
				{
					target.setCharAt(stringIndex, '|');
				}
				else if (from > to)
				{
					if (line.isUpper())
						target.setCharAt(stringIndex - 1, '/');
					else
						target.setCharAt(stringIndex - 1, '\\');

				}
				else
				{
					if (line.isUpper())
						target.setCharAt(stringIndex - 1, '\\');
					else
						target.setCharAt(stringIndex - 1, '/');
				}
			}
			System.out.println(upper.toString());
			System.out.println(lower.toString());
		}
	}
}
