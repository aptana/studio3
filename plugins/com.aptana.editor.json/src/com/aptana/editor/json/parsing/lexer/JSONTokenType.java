/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.json.parsing.lexer;

import com.aptana.editor.json.parsing.Terminals;

@SuppressWarnings("nls")
public enum JSONTokenType
{
	UNDEFINED("", -1),
	EOF("", Terminals.EOF),
	LCURLY("keyword.operator.json", Terminals.LCURLY),
	LBRACKET("keyword.operator.json", Terminals.LBRACKET),
	NUMBER("keyword.operator.json", Terminals.NUMBER),
	TRUE("keyword.operator.json", Terminals.TRUE),
	FALSE("keyword.operator.json", Terminals.FALSE),
	NULL("keyword.operator.json", Terminals.NULL),
	STRING_DOUBLE("string.quoted.double.json", Terminals.STRING_DOUBLE),
	STRING_SINGLE("string.quoted.single.json", Terminals.STRING_SINGLE),
	RCURLY("keyword.operator.json", Terminals.RCURLY),
	PROPERTY("property.json", Terminals.PROPERTY),
	RBRACKET("keyword.operator.json", Terminals.RBRACKET),
	COMMA("keyword.operator.json", Terminals.COMMA),
	COLON("keyword.operator.json", Terminals.COLON),

	COMMENT("comment.json", 14);

	private short _index;
	private String _scope;

	/**
	 * JSONTokenType
	 * 
	 * @param scope
	 */
	private JSONTokenType(String scope, short beaverId)
	{
		this._scope = scope;
		this._index = beaverId;
	}

	private JSONTokenType(String scope, int index)
	{
		this(scope, (short) index);
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
