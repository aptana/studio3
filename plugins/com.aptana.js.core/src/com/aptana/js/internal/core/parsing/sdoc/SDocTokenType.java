/**
 * Aptana Studio
 * Copyright (c) 2005-2014 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.js.internal.core.parsing.sdoc;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;


enum SDocTokenType
{
	UNDEFINED(-1),

	EOF(Terminals.EOF),
	IDENTIFIER(Terminals.IDENTIFIER),
	RCURLY(Terminals.RCURLY),
	LCURLY(Terminals.LCURLY),
	RBRACKET(Terminals.RBRACKET),
	LBRACKET(Terminals.LBRACKET),
	COLON(Terminals.COLON),
	TEXT(Terminals.TEXT),
	POUND(Terminals.POUND),
	ERROR(Terminals.ERROR),
	FUNCTION(Terminals.FUNCTION),
	ARRAY(Terminals.ARRAY),
	CLASS(Terminals.CLASS),
	COMMA(Terminals.COMMA),
	PIPE(Terminals.PIPE),
	RPAREN(Terminals.RPAREN),
	LPAREN(Terminals.LPAREN),
	CLASS_DESCRIPTION(Terminals.CLASS_DESCRIPTION),
	EXCEPTION(Terminals.EXCEPTION),
	EXTENDS(Terminals.EXTENDS),
	NAMESPACE(Terminals.NAMESPACE),
	PARAM(Terminals.PARAM),
	PROPERTY(Terminals.PROPERTY),
	RETURN(Terminals.RETURN),
	TYPE(Terminals.TYPE),
	ADVANCED(Terminals.ADVANCED),
	ALIAS(Terminals.ALIAS),
	AUTHOR(Terminals.AUTHOR),
	CONSTRUCTOR(Terminals.CONSTRUCTOR),
	EXAMPLE(Terminals.EXAMPLE),
	INTERNAL(Terminals.INTERNAL),
	METHOD(Terminals.METHOD),
	MODULE(Terminals.MODULE),
	OVERVIEW(Terminals.OVERVIEW),
	PRIVATE(Terminals.PRIVATE),
	SEE(Terminals.SEE),
	UNKNOWN(Terminals.UNKNOWN),
	END_DOCUMENTATION(Terminals.END_DOCUMENTATION),
	ARROW(Terminals.ARROW),
	LESS_THAN(Terminals.LESS_THAN),
	GREATER_THAN(Terminals.GREATER_THAN),
	ELLIPSIS(Terminals.ELLIPSIS),
	START_DOCUMENTATION(Terminals.START_DOCUMENTATION),

	WHITESPACE(1024),
	TYPES(1025),
	VALUE(1026);

	private static Map<Short, SDocTokenType> ID_MAP;

	private short _index;

	/**
	 * static initializer
	 */
	static
	{
		ID_MAP = new HashMap<Short, SDocTokenType>();

		for (SDocTokenType type : EnumSet.allOf(SDocTokenType.class))
		{
			ID_MAP.put(type.getIndex(), type);
		}
	}

	private SDocTokenType(short beaverId)
	{
		this._index = beaverId;
	}

	private SDocTokenType(int index)
	{
		this((short) index);
	}

	public static SDocTokenType get(short id)
	{
		SDocTokenType result = UNDEFINED;

		if (ID_MAP.containsKey(id))
		{
			result = ID_MAP.get(id);
		}

		return result;
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
}
