/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.xml.parsing.lexer;

import java.util.HashMap;
import java.util.Map;

import com.aptana.parsing.lexer.ITypePredicate;

public enum XMLTokenType implements ITypePredicate
{
	UNDEFINED, EOF, COMMENT, STRING, CDATA, DECLARATION, START_TAG, END_TAG, TEXT,

	// for the parser
	TAG_SELF_CLOSE, ATTRIBUTE, EQUAL, VALUE, SINGLE_QUOTED_STRING, DOUBLE_QUOTED_STRING, META, OTHER;

	private static Map<Short, XMLTokenType> fTokens = new HashMap<Short, XMLTokenType>();

	/**
	 * getToken
	 * 
	 * @param index
	 * @return
	 */
	public static XMLTokenType getToken(short index)
	{
		return fTokens.get(index);
	}

	/**
	 * static initializer
	 */
	static
	{
		for (XMLTokenType token : XMLTokenType.values())
		{
			fTokens.put(token.getIndex(), token);
		}
	}

	/**
	 * getIndex
	 * 
	 * @return
	 */
	public short getIndex()
	{
		return (short) ordinal();
	}

	/**
	 * isDefined
	 */
	public boolean isDefined()
	{
		return (this != UNDEFINED);
	}
}
