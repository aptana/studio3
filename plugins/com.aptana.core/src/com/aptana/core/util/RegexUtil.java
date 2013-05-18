/**
 * Aptana Studio
 * Copyright (c) 2005-2013 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.core.util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * RegexUtil
 */
public class RegexUtil
{
	/**
	 * RegexUtil
	 */
	private RegexUtil()
	{
	}

	/**
	 * Convert a list of String into a regular expression. Each item is escaped so none of its content will be
	 * interpreted as regex syntax. If the list is null or empty, and empty string is returned.
	 * 
	 * @param list
	 * @return
	 */
	public static String createQuotedListPattern(List<String> list)
	{
		String result = StringUtil.EMPTY;

		if (list != null && list.isEmpty() == false)
		{
			List<String> quotedItems = new ArrayList<String>(list.size());

			for (String item : list)
			{
				quotedItems.add(Pattern.quote(item));
			}

			result = "(" + StringUtil.join("|", quotedItems) + ")"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		}

		return result;
	}

	/**
	 * Quote a pattern string. In case the given string is already properly quoted, the same string is returned.
	 * 
	 * @param expression
	 * @return A quoted string
	 * @see Pattern#quote(String)
	 */
	public static String quote(String expression)
	{
		if (!(expression.startsWith("\\Q") && expression.endsWith("\\E"))) //$NON-NLS-1$//$NON-NLS-2$
		{
			return Pattern.quote(expression);
		}
		return expression;
	}
}
