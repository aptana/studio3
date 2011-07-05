/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.coffee.parsing.lexer;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import com.aptana.editor.coffee.parsing.Terminals;
import com.aptana.parsing.lexer.ITypePredicate;

public enum CoffeeTokenType implements ITypePredicate
{
	// TODO Hook up the scopes to Terminals etc!
	FUNCTION("support.function.misc.coffee", Terminals.IDENTIFIER), //$NON-NLS-1$

	// Stuff for the parser only:
	EOF("", Terminals.EOF), //$NON-NLS-1$
	LBRACKET("punctuation.bracket.coffee", Terminals.LBRACKET), //$NON-NLS-1$

	UNDEFINED("undefined.coffee", -1), //$NON-NLS-1$
	COMMENT("comment.block.coffee", 42); //$NON-NLS-1$

	private static final Map<String, CoffeeTokenType> NAME_MAP;
	private String _scope;
	private short beaverId;

	/**
	 * static
	 */
	static
	{
		NAME_MAP = new HashMap<String, CoffeeTokenType>();

		for (CoffeeTokenType type : EnumSet.allOf(CoffeeTokenType.class))
		{
			NAME_MAP.put(type.getScope(), type);
		}
	}

	/**
	 * get
	 * 
	 * @param scope
	 * @return
	 */
	public static final CoffeeTokenType get(String scope)
	{
		return (NAME_MAP.containsKey(scope)) ? NAME_MAP.get(scope) : UNDEFINED;
	}

	/**
	 * CoffeeTokenType
	 * 
	 * @param scope
	 */
	private CoffeeTokenType(String scope, int beaverId)
	{
		this._scope = scope;
		this.beaverId = (short) beaverId;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.parsing.lexer.ITypePredicate#getIndex()
	 */
	public short getIndex()
	{
		return this.beaverId;
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

	public short getShort()
	{
		return beaverId;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.css.parsing.lexer.ITypePredicate#isDefined()
	 */
	public boolean isDefined()
	{
		return (this != UNDEFINED);
	}

	/**
	 * toString
	 */
	public String toString()
	{
		return this.getShort() + ": " + this.getScope(); //$NON-NLS-1$
	}
}
