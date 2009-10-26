package com.aptana.git.ui.internal.history;

import java.util.Collection;
import java.util.Set;

public class GraphCellInfo
{

	public Collection<GitGraphLine> getLines()
	{
		return lines;
	}

	private Set<GitGraphLine> lines;
	private int position;
	int numColumns;
	
	public GraphCellInfo(int p, Set<GitGraphLine> lines)
	{
		this.position = p;
		this.lines = lines;
	}

	public int getPosition()
	{
		return position;
	}

}
