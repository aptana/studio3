/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.core.util.replace;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.aptana.core.IMap;
import com.aptana.core.util.CollectionsUtil;
import com.aptana.core.util.StringUtil;

/**
 * SimplePatternReplacer
 */
public class SimpleTextPatternReplacer extends TextPatternReplacer
{
	/**
	 * Add a pattern of raw text. A new action will be created to replace matches with a new text value
	 * 
	 * @param pattern
	 * @param replaceText
	 */
	public void addPattern(String pattern, final String replaceText)
	{
		addPattern(pattern, new IMap<String, String>()
		{
			public String map(String item)
			{
				return replaceText;
			}
		});
	}

	/**
	 * Get the action associated with the specified text. If no actions exists, null will be returned
	 * 
	 * @param matcher
	 * @return
	 */
	public IMap<String, String> getAction(Matcher matcher)
	{
		if (patternActions != null)
		{
			return patternActions.get(matcher.group());
		}

		return null;
	}

	/**
	 * Return the set of raw text patterns as a single regex string
	 * 
	 * @return
	 */
	protected String getRegexString()
	{
		if (patternActions != null && !patternActions.isEmpty())
		{
			List<String> keys = new ArrayList<String>(patternActions.keySet());

			Collections.sort(keys, new Comparator<String>()
			{
				public int compare(String o1, String o2)
				{
					return o2.compareTo(o1);
				}
			});

			keys = CollectionsUtil.map(keys, new IMap<String, String>()
			{
				public String map(String item)
				{
					return Pattern.quote(item);
				}
			});

			return wrapInGroups(keys);
		}

		return StringUtil.EMPTY;
	}
}
