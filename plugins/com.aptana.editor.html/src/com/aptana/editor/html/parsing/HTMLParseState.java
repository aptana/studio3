/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.html.parsing;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.aptana.editor.html.parsing.HTMLDocumentTypes.Type;
import com.aptana.parsing.ParseState;

/**
 * @author Kevin Lindsey
 * @author Michael Xia
 */
public class HTMLParseState extends ParseState
{

	@SuppressWarnings("nls")
	private static final String[] END_OPTIONAL_TAGS = { "body", "colgroup", "dd", "dt", "area", "html", "li", "option",
			"p", "tbody", "td", "tfoot", "th", "thead", "tr" };
	@SuppressWarnings("nls")
	private static final String[] END_FORBIDDEN_OR_EMPTY_TAGS = { "area", "base", "basefont", "br", "col", "frame",
			"hr", "img", "input", "isindex", "link", "meta", "param" };

	private Type fDocumentType;

	private static final Map<String, Integer> fEndTagInfo;
	static
	{
		fEndTagInfo = new HashMap<String, Integer>();
		for (String tag : END_OPTIONAL_TAGS)
		{
			fEndTagInfo.put(tag, HTMLTagInfo.END_OPTIONAL);
		}
		for (String tag : END_FORBIDDEN_OR_EMPTY_TAGS)
		{
			fEndTagInfo.put(tag, HTMLTagInfo.END_FORBIDDEN | HTMLTagInfo.EMPTY);
		}
	}

	public HTMLParseState()
	{
	}

	public Type getDocumentType()
	{
		return fDocumentType;
	}

	/**
	 * @param tagName
	 *            the name of the tag
	 * @return the closing type that the tag has
	 */
	public int getCloseTagType(String tagName)
	{
		String key = tagName.toLowerCase();
		if (fEndTagInfo.containsKey(key))
		{
			int info = fEndTagInfo.get(key);
			int type = info & HTMLTagInfo.END_MASK;
			if (type == HTMLTagInfo.END_FORBIDDEN || fDocumentType.compareTo(Type.XHTML_1_0_STRICT) < 0)
			{
				return type;
			}
		}
		return HTMLTagInfo.END_REQUIRED;
	}

	/**
	 * @param tagName
	 *            the name of the tag
	 * @return true if the tag is of the empty type, false otherwise
	 */
	public boolean isEmptyTagType(String tagName)
	{
		String key = tagName.toLowerCase();
		if (fEndTagInfo.containsKey(key))
		{
			return (fEndTagInfo.get(key) & HTMLTagInfo.EMPTY) == HTMLTagInfo.EMPTY;
		}
		return false;
	}

	@Override
	public void setEditState(String source, String insertedText, int startingOffset, int removedLength)
	{
		super.setEditState(source, insertedText, startingOffset, removedLength);
		fDocumentType = HTMLDocumentTypes.getType(source);
	}

	public static boolean isEndForbiddenOrEmptyTag(String name)
	{
		Set<String> set = new HashSet<String>(Arrays.asList(END_FORBIDDEN_OR_EMPTY_TAGS));
		return set.contains(name);
	}
}
