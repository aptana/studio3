/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.xml.core.parsing;

import java.util.HashMap;
import java.util.Map;

import com.aptana.core.util.StringUtil;
import com.aptana.parsing.lexer.ITypePredicate;
import com.aptana.xml.core.IXMLScopes;

public enum XMLTokenType implements ITypePredicate
{

	UNDEFINED(StringUtil.EMPTY), EOF("eof"), COMMENT(IXMLScopes.COMMENT_BLOCK_XML), //$NON-NLS-1$
	// STRING,
	DOCTYPE(IXMLScopes.META_TAG_SGML_DOCTYPE_XML),
	CDATA(IXMLScopes.STRING_UNQUOTED_CDATA_XML),
	DECLARATION(IXMLScopes.META_TAG_PREPROCESSOR_XML),
	START_TAG(IXMLScopes.PUNCTUATION_DEFINITION_TAG_XML),
	END_TAG(IXMLScopes.PUNCTUATION_DEFINITION_TAG_XML),
	TEXT(IXMLScopes.TEXT_XML),

	// for the parser
	TAG_SELF_CLOSE(IXMLScopes.PUNCTUATION_DEFINITION_TAG_XML),
	ATTRIBUTE(IXMLScopes.ENTITY_OTHER_ATTRIBUTE_NAME_XML),
	EQUAL(IXMLScopes.PUNCTUATION_SEPARATOR_KEY_VALUE_XML),
	VALUE("attr_value"), // used by attribute scanner to mark the value //$NON-NLS-1$
	SINGLE_QUOTED_STRING(IXMLScopes.STRING_QUOTED_SINGLE_XML),
	DOUBLE_QUOTED_STRING(IXMLScopes.STRING_QUOTED_DOUBLE_XML),
	TAG_NAME(IXMLScopes.ENTITY_NAME_TAG_XML),
	OTHER("attr_unused_scope"); // used by attribute scanner to mark something other than attr name or value //$NON-NLS-1$

	private static Map<Short, XMLTokenType> fTokens = new HashMap<Short, XMLTokenType>();
	static
	{
		for (XMLTokenType token : XMLTokenType.values())
		{
			fTokens.put(token.getIndex(), token);
		}
	}

	private String scope;

	private XMLTokenType(String scope)
	{
		this.scope = scope;
	}

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

	public String getScope()
	{
		return scope;
	}

	public static XMLTokenType getByScope(String scope)
	{
		for (XMLTokenType type : values())
		{
			if (type.getScope().equals(scope))
			{
				return type;
			}
		}
		return null;
	}
}
