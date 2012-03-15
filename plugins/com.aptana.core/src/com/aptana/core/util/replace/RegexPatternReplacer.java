/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.core.util.replace;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

import com.aptana.core.IMap;
import com.aptana.core.util.StringUtil;

/**
 * RegexPatternReplacer
 */
public class RegexPatternReplacer extends TextPatternReplacer
{
	private List<String> patternTable;

	/*
	 * (non-Javadoc)
	 * @see com.aptana.core.util.replace.TextPatternReplacer#getAction(java.util.regex.Matcher)
	 */
	@Override
	public IMap<String, String> getAction(Matcher m)
	{
		if (patternActions != null)
		{
			int groupCount = m.groupCount();

			if (groupCount == patternTable.size())
			{
				for (int i = 0; i < groupCount; i++)
				{
					if (!StringUtil.isEmpty(m.group(i + 1)))
					{
						return patternActions.get(patternTable.get(i));
					}
				}
			}
		}

		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.core.util.replace.TextPatternReplacer#getRegexString()
	 */
	@Override
	protected String getRegexString()
	{
		patternTable = new ArrayList<String>(patternActions.keySet());

		// build map
		if (patternActions != null && !patternActions.isEmpty())
		{
			return wrapInGroups(patternActions.keySet());
		}

		return StringUtil.EMPTY;
	}
}
