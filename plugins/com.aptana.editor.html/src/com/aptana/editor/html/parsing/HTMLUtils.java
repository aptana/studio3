/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.html.parsing;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.aptana.core.IMap;
import com.aptana.core.util.CollectionsUtil;
import com.aptana.core.util.StringUtil;
import com.aptana.editor.html.contentassist.HTMLIndexQueryHelper;
import com.aptana.editor.html.contentassist.model.EventElement;
import com.aptana.editor.html.parsing.lexer.HTMLTokenType;
import com.aptana.parsing.lexer.Lexeme;
import com.aptana.parsing.lexer.Range;

public class HTMLUtils
{
	private static final String STYLE = "style"; //$NON-NLS-1$
	private static final String JAVASCRIPT = "javascript"; //$NON-NLS-1$

	/**
	 * Cache from event name to event metadata
	 */
	private static Set<String> fgEventsMap;

	/**
	 * Determine if the specified attribute name indicates an attribute that may contain CSS
	 * 
	 * @param name
	 * @return
	 */
	public static boolean isCSSAttribute(String name)
	{
		boolean result = false;

		if (name != null)
		{
			result = name.equalsIgnoreCase(STYLE);
		}

		return result;
	}

	/**
	 * Determine if the specified attribute name indicates an attribute that may contain JS
	 * 
	 * @param elementName
	 * @param attributeName
	 * @return
	 */
	public static boolean isJSAttribute(String elementName, String attributeName)
	{
		if (elementName == null || attributeName == null)
		{
			return false;
		}
		
		// Support HTML "data-" attributes which mostly used with JavaScript

		if(attributeName.matches("^(data-[a-zA-Z-])$")) {
			return true;
		}

		// Load all events once and then generate the unique set of names for them.
		// TODO Do we need to verify that the attribute is attached to the element?
		if (fgEventsMap == null)
		{
			List<EventElement> events = new HTMLIndexQueryHelper().getEvents();
			fgEventsMap = new HashSet<String>(CollectionsUtil.map(events, new IMap<EventElement, String>()
			{
				public String map(EventElement item)
				{
					return item.getName();
				}
			}));
		}
		return fgEventsMap.contains(attributeName.toLowerCase());
	}

	/**
	 * Removes the "<" and "</" from the beginning and ">" from the end of a tag. This is used on end/close tags
	 * specifically in the HTMLParser.
	 * 
	 * @param tag
	 *            the tag text to strip
	 * @return a string with the necessary items removed
	 */
	public static String stripTagEndings(String tag)
	{
		if (tag == null)
		{
			return null;
		}

		String trimmed = tag.trim(); // strip leading and trailing whitespace
		int length = trimmed.length();
		if (length > 0 && trimmed.charAt(0) == '<')
		{
			if (length > 1 && trimmed.charAt(1) == '/')
			{
				trimmed = trimmed.substring(2).trim();
			}
			else
			{
				trimmed = trimmed.substring(1).trim();
			}
			// Re-calculate length
			length = trimmed.length();
		}
		if (length > 0 && trimmed.charAt(length - 1) == '>')
		{
			trimmed = trimmed.substring(0, length - 1).trim();
		}
		return trimmed;
	}

	/**
	 * Returns true if the specified tag contents has self-closing tag
	 * 
	 * @param tagContents
	 * @return
	 */
	public static boolean isTagSelfClosing(String tagContents)
	{
		return isTagComplete(tagContents) && tagContents.length() >= 2
				&& tagContents.charAt(tagContents.length() - 2) == '/';
	}

	/**
	 * Returns true if the specified tag contents has valid ending
	 * 
	 * @param tagContents
	 * @return
	 */
	public static boolean isTagComplete(String tagContents)
	{
		if (tagContents == null || tagContents.length() < 1)
		{
			return false;
		}
		return tagContents.charAt(tagContents.length() - 1) == '>';
	}

	/**
	 * Returns true if the specified tag contents has JavaScript &lt;script&gt; tag
	 * 
	 * @param tagContents
	 * @return
	 */
	public static boolean isJavaScriptTag(String tagContents)
	{
		if (!isTagComplete(tagContents))
		{
			return false;
		}
		String type = getTagAttribute(tagContents, "type"); //$NON-NLS-1$
		if (type != null && type.toLowerCase().contains(JAVASCRIPT))
		{
			return true;
		}
		String language = getTagAttribute(tagContents, "language"); //$NON-NLS-1$
		if (language != null && language.toLowerCase().contains(JAVASCRIPT))
		{
			return true;
		}
		return type == null && language == null;
	}

	/**
	 * @param tagContents
	 * @param attributeName
	 * @return
	 */
	private static String getTagAttribute(String tagContents, String attributeName)
	{
		Matcher matcher = Pattern
				.compile(".*\\s+" + attributeName + "=\"([a-zA-Z_/0-9-]+)\".*").matcher(tagContents.toLowerCase()); //$NON-NLS-1$ //$NON-NLS-2$
		if (matcher.matches())
		{
			return matcher.group(1);
		}
		return null;
	}

	/**
	 * @param lexeme
	 * @param offset
	 * @return
	 */
	public static Range getAttributeValueRange(Lexeme<HTMLTokenType> lexeme, int offset)
	{
		if (lexeme == null || lexeme.getType() == null)
		{
			return null;
		}

		switch (lexeme.getType())
		{
			case SINGLE_QUOTED_STRING:
			case DOUBLE_QUOTED_STRING:

				// if offset is at the start or the end of the quoted string, return null range
				if (offset <= lexeme.getStartingOffset() || offset > lexeme.getEndingOffset())
				{
					return null;
				}

				if (lexeme.getLength() >= 2)
				{
					// trim off the quotes
					int startingOffset = lexeme.getStartingOffset() + 1;
					String text = lexeme.getText().substring(1, lexeme.getLength() - 1);

					int start = StringUtil.findPreviousWhitespaceOffset(text, offset - startingOffset);
					int end = StringUtil.findNextWhitespaceOffset(text, offset - startingOffset);
					if (start < 0)
					{
						start = 0;
					}
					else
					{
						start++; // start includes whitespace char. Need to advance
					}
					if (end < 0)
					{
						end = text.length();
					}
					return new Range(start + startingOffset, end + startingOffset - 1);
				}

			default:
				return new Range(lexeme.getStartingOffset(), lexeme.getEndingOffset());
		}
	}
}
