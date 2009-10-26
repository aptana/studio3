package com.aptana.git.ui.internal.history;

import java.util.List;

public class GitLane
{
	
	private static int s_colorIndex = 0;
	private String d_sha;
	private int d_index;

	GitLane(String sha)
	{
		d_index = s_colorIndex++;
		d_sha = sha;
	}
		
	GitLane()
	{
		d_index = s_colorIndex++;
	}

	boolean isCommit(String sha)
	{
		return d_sha.equals(sha);
	}
	
	String sha()
	{
		return d_sha;
	}

	int index()
	{
		return d_index;
	}

	void setSha(String sha)
	{
		d_sha = sha;
	}

	static void resetColors()
	{
		s_colorIndex = 0;
	}


}
