/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.core.util.replace;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.aptana.core.IMap;
import com.aptana.core.util.StringUtil;

/**
 * TextPatternReplacer
 */
public abstract class TextPatternReplacer
{
	private static final String CLOSE_GROUP = ")"; //$NON-NLS-1$
	private static final String OPEN_GROUP = "("; //$NON-NLS-1$
	private static final String OR_OPERATOR = "|"; //$NON-NLS-1$
	private static final String GROUP_DELIMITER = CLOSE_GROUP + OR_OPERATOR + OPEN_GROUP;

	public static final IMap<String, String> REPLACE_WITH_NOTHING = new IMap<String, String>()
	{
		public String map(String item)
		{
			return StringUtil.EMPTY;
		}
	};

	protected Map<String, IMap<String, String>> patternActions;
	private Pattern pattern;

	/**
	 * Add a pattern of text. Any matches of this text will be replaced with the empty string
	 * 
	 * @param text
	 */
	public void addPattern(String pattern)
	{
		addPattern(pattern, null);
	}

	/**
	 * Add a pattern of text and an associated action to fire when that text matches.
	 * 
	 * @param text
	 *            The raw text to match. Note that the text is quoted before being converted to a regular expression so
	 *            characters normally included in the regex grammar are safe to use here
	 * @param action
	 */
	public void addPattern(String pattern, IMap<String, String> action)
	{
		if (pattern != null)
		{
			if (patternActions == null)
			{
				// NOTE: some pattern replacers rely on add order, so we preserve it
				patternActions = new LinkedHashMap<String, IMap<String, String>>();
			}

			if (!patternActions.containsKey(pattern))
			{
				if (action == null)
				{
					action = REPLACE_WITH_NOTHING;
				}

				patternActions.put(pattern, action);

				// reset pattern, if we already have one, to force a recompile
				pattern = null;
			}
		}
	}

	/**
	 * Get the action associated with the specified text. If no actions exists, null will be returned
	 * 
	 * @param text
	 * @return
	 */
	public abstract IMap<String, String> getAction(Matcher m);

	/**
	 * Get the compiled regex pattern that will match all raw text values added to this instance
	 * 
	 * @return
	 */
	public Pattern getPattern()
	{
		if (pattern == null)
		{
			String regexString = getRegexString();

			if (!StringUtil.isEmpty(regexString))
			{
				pattern = Pattern.compile(regexString);
			}
		}

		return pattern;
	}

	/**
	 * Return the set of raw text patterns as a single regex string
	 * 
	 * @return
	 */
	protected abstract String getRegexString();

	/**
	 * Return the text to replace the current match.
	 * 
	 * @param matcher
	 * @return
	 */
	protected String getReplacementText(Matcher matcher)
	{
		IMap<String, String> action = getAction(matcher);

		if (action != null)
		{
			return action.map(matcher.group());
		}

		return matcher.group();
	}

	/**
	 * Apply the set of pattern string and their associated actions to the specified text
	 * 
	 * @param text
	 *            The text to search and replace
	 * @return
	 */
	public String searchAndReplace(String text)
	{
		if (!StringUtil.isEmpty(text))
		{
			Pattern p = getPattern();

			if (p != null)
			{
				Matcher m = p.matcher(text);
				StringBuffer buffer = new StringBuffer();

				while (m.find())
				{
					// We have to escape any dollar sign in the replacement text before applying it. Otherwise, an
					// IllegalArgumentException can be thrown.
					String replacementText = getReplacementText(m);
					replacementText = replacementText.replaceAll("\\$", "\\\\\\$"); //$NON-NLS-1$//$NON-NLS-2$
					m.appendReplacement(buffer, replacementText);
				}

				m.appendTail(buffer);

				return buffer.toString();
			}
		}

		// do nothing if we didn't have a valid pattern
		return text;
	}

	/**
	 * Wrap items in a collection into separate capturing groups
	 * 
	 * @param items
	 * @return
	 */
	protected String wrapInGroups(Collection<String> items)
	{
		// @formatter:off
		return StringUtil.concat(
			OPEN_GROUP,
			StringUtil.join(GROUP_DELIMITER, items),
			CLOSE_GROUP
		);
		// @formatter:on
	}
}
