/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.scope;

import java.util.List;

import com.aptana.core.util.StringUtil;

/**
 * GroupSelector
 */
public class GroupSelector extends SelectorNode
{
	private ISelectorNode child;

	/**
	 * GroupSelector
	 * 
	 * @param child
	 */
	public GroupSelector(ISelectorNode child)
	{
		this.child = child;
	}

	/**
	 * getChild
	 * 
	 * @return
	 */
	public ISelectorNode getChild()
	{
		return child;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.scope.SelectorNode#matchResults()
	 */
	@Override
	public List<Integer> getMatchResults()
	{
		return (child != null) ? child.getMatchResults() : super.getMatchResults();
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.scope.SelectorNode#matches(com.aptana.scope.MatchContext)
	 */
	@Override
	public boolean matches(MatchContext context)
	{
		return (child != null) ? child.matches(context) : super.matches(context);
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return (child != null) ? StringUtil.concat("(", child.toString(), ")") : "()"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}
}
