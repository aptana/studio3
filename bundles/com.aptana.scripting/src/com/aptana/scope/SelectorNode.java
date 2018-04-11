/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.scope;

import java.util.Collections;
import java.util.List;

import beaver.Symbol;

/**
 * SelectorNode
 */
public class SelectorNode extends Symbol implements ISelectorNode
{
	/*
	 * (non-Javadoc)
	 * @see com.aptana.scope.ISelectorNode#matchResults()
	 */
	public List<Integer> getMatchResults()
	{
		return Collections.emptyList();
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.scope.ISelectorNode#matches(com.aptana.scope.MatchContext)
	 */
	public boolean matches(MatchContext context)
	{
		return false;
	}
}
