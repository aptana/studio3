/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.scope;

import java.util.Collections;
import java.util.List;

/**
 * MatchAnyScopeSelector
 */
public class MatchAnyScopeSelector implements IScopeSelector
{
	public int compareTo(IScopeSelector o)
	{
		if (o instanceof MatchAnyScopeSelector)
		{
			return 0;
		}
		return -1;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.scope.IScopeSelector#getMatchFragments()
	 */
	public int getMatchFragments()
	{
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.scope.IScopeSelector#getMatchLength()
	 */
	public int getMatchLength()
	{
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.scope.IScopeSelector#getMatchOffset()
	 */
	public int getMatchOffset()
	{
		return 0;
	}

	public List<Integer> getMatchResults()
	{
		return Collections.emptyList();
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.scope.IScopeSelector#matches(java.lang.String)
	 */
	public boolean matches(String scope)
	{
		return true;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.scope.IScopeSelector#matches(java.lang.String[])
	 */
	public boolean matches(String[] scopes)
	{
		return true;
	}
}
