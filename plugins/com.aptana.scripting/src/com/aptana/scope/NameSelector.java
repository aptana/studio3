/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.scope;

import java.util.List;

import com.aptana.core.util.CollectionsUtil;
import com.aptana.core.util.StringUtil;

public class NameSelector extends SelectorNode
{
	private String _name;
	private int matchLength = 0;

	/**
	 * NameSelector
	 * 
	 * @param name
	 */
	public NameSelector(String name)
	{
		this._name = name;
	}

	public List<Integer> getMatchResults()
	{
		// This is always just one segment, so only one value, and it is the length of this match
		return CollectionsUtil.newList(matchLength);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.scope.ISelectorNode#matches(com.aptana.scope.MatchContext)
	 */
	public boolean matches(MatchContext context)
	{
		matchLength = 0;
		boolean result = false;

		if (context != null && !StringUtil.isEmpty(this._name))
		{
			String step = context.getCurrentStep();

			if (step != null && step.startsWith(this._name))
			{
				// step matches as a prefix, now make sure we matched the whole step
				// or up to a period
				int nameLength = this._name.length();
				int scopeLength = step.length();

				if (scopeLength == nameLength || step.charAt(nameLength) == '.')
				{
					result = true;
					matchLength = nameLength;
					context.advance();
				}
			}
		}

		return result;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return this._name;
	}
}
