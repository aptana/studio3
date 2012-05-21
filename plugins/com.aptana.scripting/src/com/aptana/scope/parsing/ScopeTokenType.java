/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.scope.parsing;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

/**
 * ScopeTokenType
 */
public enum ScopeTokenType
{
	// @formatter:off
	ERROR(-2),
	UNDEFINED(-1),
	EOF(Terminals.EOF),
	COMMA(Terminals.COMMA),
	PIPE(Terminals.PIPE),
	AMPERSAND(Terminals.AMPERSAND),
	LPAREN(Terminals.LPAREN),
	RPAREN(Terminals.RPAREN),
	IDENTIFIER(Terminals.IDENTIFIER),
	MINUS(Terminals.MINUS);
	// @formatter:on

	private static Map<Short, ScopeTokenType> ID_MAP;

	private short _index;

	/**
	 * static initializer
	 */
	static
	{
		ID_MAP = new HashMap<Short, ScopeTokenType>();

		for (ScopeTokenType type : EnumSet.allOf(ScopeTokenType.class))
		{
			ID_MAP.put(type.getIndex(), type);
		}
	}

	/**
	 * ScopeTokenType
	 * 
	 * @param beaverId
	 */
	private ScopeTokenType(short beaverId)
	{
		this._index = beaverId;
	}

	/**
	 * ScopeTokenType
	 * 
	 * @param index
	 */
	private ScopeTokenType(int index)
	{
		this((short) index);
	}

	/**
	 * get
	 * 
	 * @param id
	 * @return
	 */
	public static ScopeTokenType get(short id)
	{
		ScopeTokenType result = UNDEFINED;

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
