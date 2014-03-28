/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.html.parsing;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.aptana.core.util.CollectionsUtil;
import com.aptana.editor.html.parsing.HTMLDocumentTypes.Type;
import com.aptana.parsing.ParseState;
import com.aptana.parsing.lexer.IRange;

/**
 * @author Kevin Lindsey
 * @author Michael Xia
 */
public class HTMLParseState extends ParseState
{

	@SuppressWarnings("nls")
	private static final Set<String> END_OPTIONAL_TAGS = CollectionsUtil.newSet("body", "colgroup", "dd", "dt", "area",
			"html", "li", "option", "p", "tbody", "td", "tfoot", "th", "thead", "tr");

	@SuppressWarnings("nls")
	private static final Set<String> END_FORBIDDEN_OR_EMPTY_TAGS = CollectionsUtil.newSet("area", "base", "basefont",
			"br", "col", "frame", "hr", "img", "input", "isindex", "link", "meta", "param", "track");

	private Type fDocumentType;

	private static final Map<String, Integer> fEndTagInfo;
	static
	{
		fEndTagInfo = new HashMap<String, Integer>(END_OPTIONAL_TAGS.size() + END_FORBIDDEN_OR_EMPTY_TAGS.size());
		for (String tag : END_OPTIONAL_TAGS)
		{
			fEndTagInfo.put(tag, IHTMLTagInfo.END_OPTIONAL);
		}
		for (String tag : END_FORBIDDEN_OR_EMPTY_TAGS)
		{
			fEndTagInfo.put(tag, IHTMLTagInfo.END_FORBIDDEN | IHTMLTagInfo.EMPTY);
		}
	}

	public HTMLParseState(String source)
	{
		this(source, 0);
	}

	public HTMLParseState(String source, int startingOffset)
	{
		this(source, startingOffset, null);
	}

	public HTMLParseState(String source, int startingOffset, IRange[] ranges)
	{
		super(source, startingOffset, ranges);
		fDocumentType = HTMLDocumentTypes.getType(source);
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
			int type = info & IHTMLTagInfo.END_MASK;
			if (type == IHTMLTagInfo.END_FORBIDDEN || fDocumentType.compareTo(Type.XHTML_1_0_STRICT) < 0)
			{
				return type;
			}
		}
		return IHTMLTagInfo.END_REQUIRED;
	}

	/**
	 * @param tagName
	 *            the name of the tag
	 * @return true if the tag is of the empty type, false otherwise
	 */
	// FIXME Determine empty tag types by document type? Do they differ over various versions of HTML?
	public static boolean isEmptyTagType(String tagName)
	{
		String key = tagName.toLowerCase();
		if (fEndTagInfo.containsKey(key))
		{
			return (fEndTagInfo.get(key) & IHTMLTagInfo.EMPTY) == IHTMLTagInfo.EMPTY;
		}
		return false;
	}

	public static boolean isEndForbiddenOrEmptyTag(String name)
	{
		return END_FORBIDDEN_OR_EMPTY_TAGS.contains(name);
	}
}
