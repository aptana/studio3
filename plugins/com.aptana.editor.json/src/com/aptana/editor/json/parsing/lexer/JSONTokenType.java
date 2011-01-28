/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.json.parsing.lexer;

import java.util.EnumSet;

@SuppressWarnings("nls")
public enum JSONTokenType
{
	UNDEFINED(""),	// -1
	EOF(""),	// 0
	LCURLY("keyword.operator.json"),	// 1
	LBRACKET("keyword.operator.json"),	// 2
	NUMBER("keyword.operator.json"),	// 3
	TRUE("keyword.operator.json"),	// 4
	FALSE("keyword.operator.json"),	// 5
	NULL("keyword.operator.json"),	// 6
	STRING_DOUBLE("string.quoted.double.json"),	// 7
	STRING_SINGLE("string.quoted.single.json"),	// 8
	RCURLY("keyword.operator.json"),	// 9
	PROPERTY("property.json"),	// 10
	RBRACKET("keyword.operator.json"),	// 11
	COMMA("keyword.operator.json"),	// 12
	COLON("keyword.operator.json"),	// 13
	
	COMMENT("comment.json");

	private short _index;
	private String _scope;

	/**
	 * static initializer
	 */
	static
	{
		short index = -1;

		for (JSONTokenType type : EnumSet.allOf(JSONTokenType.class))
		{
			type._index = index++;
		}
	}

	/**
	 * BeaverTokenType
	 * 
	 * @param scope
	 */
	private JSONTokenType(String scope)
	{
		this._scope = scope;
	}

	/**
	 * getIndex
	 * 
	 * @return
	 */
	public short getIndex()
	{
		return this._index;
	}

	/**
	 * getScope
	 * 
	 * @return
	 */
	public String getScope()
	{
		return this._scope;
	}
}
