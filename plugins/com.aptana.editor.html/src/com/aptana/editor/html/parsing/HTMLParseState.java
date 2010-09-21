/**
 * This file Copyright (c) 2005-2010 Aptana, Inc. This program is
 * dual-licensed under both the Aptana Public License and the GNU General
 * Public license. You may elect to use one or the other of these licenses.
 * 
 * This program is distributed in the hope that it will be useful, but
 * AS-IS and WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE, TITLE, or
 * NONINFRINGEMENT. Redistribution, except as permitted by whichever of
 * the GPL or APL you select, is prohibited.
 *
 * 1. For the GPL license (GPL), you can redistribute and/or modify this
 * program under the terms of the GNU General Public License,
 * Version 3, as published by the Free Software Foundation.  You should
 * have received a copy of the GNU General Public License, Version 3 along
 * with this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 * 
 * Aptana provides a special exception to allow redistribution of this file
 * with certain other free and open source software ("FOSS") code and certain additional terms
 * pursuant to Section 7 of the GPL. You may view the exception and these
 * terms on the web at http://www.aptana.com/legal/gpl/.
 * 
 * 2. For the Aptana Public License (APL), this program and the
 * accompanying materials are made available under the terms of the APL
 * v1.0 which accompanies this distribution, and is available at
 * http://www.aptana.com/legal/apl/.
 * 
 * You may view the GPL, Aptana's exception and additional terms, and the
 * APL in the file titled license.html at the root of the corresponding
 * plugin containing this source file.
 * 
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.html.parsing;

import java.util.HashMap;
import java.util.Map;

import com.aptana.editor.html.parsing.HTMLDocumentTypes.Type;
import com.aptana.parsing.ParseState;

/**
 * @author Kevin Lindsey
 * @author Michael Xia
 */
public class HTMLParseState extends ParseState
{

	// FIXME This is incorrect. These tags need to be determined at runtime based on the document type!
	@SuppressWarnings("nls")
	private static final String[] END_OPTIONAL_TAGS = { "body", "colgroup", "dd", "dt", "area", "html", "li", "option",
			"p", "tbody", "td", "tfoot", "th", "thead", "tr" };
	@SuppressWarnings("nls")
	private static final String[] END_FORBIDDEN_OR_EMPTY_TAGS = { "area", "base", "basefont", "br", "col", "frame",
			"hr", "img", "input", "isindex", "link", "meta", "param", };

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
			return fEndTagInfo.get(key) & HTMLTagInfo.END_MASK;
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
}
