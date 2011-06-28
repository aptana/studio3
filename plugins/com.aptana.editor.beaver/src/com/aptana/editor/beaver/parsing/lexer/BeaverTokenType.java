/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.beaver.parsing.lexer;

import java.util.EnumSet;

@SuppressWarnings("nls")
public enum BeaverTokenType
{
	STRING_DOUBLE("string.quoted.double.js"),
	STRING_SINGLE("string.quoted.single.js"),
	HEADER("keyword.operator.beaver"),
	PACKAGE("keyword.operator.beaver"),
	IMPORT("keyword.operator.beaver"),
	CLASS("keyword.operator.beaver"),
	EMBED("keyword.operator.beaver"),
	INIT("keyword.operator.beaver"),
	TERMINALS("keyword.operator.beaver"),
	TYPEOF("keyword.operator.beaver"),
	LEFT("keyword.operator.beaver"),
	RIGHT("keyword.operator.beaver"),
	GOAL("keyword.operator.beaver"),
	IMPLEMENTS("keyword.operator.beaver"),
	BLOCK("keyword.operator.beaver"),
	SEMICOLON("keyword.operator.beaver"),
	EQUAL("keyword.operator.beaver"),
	PIPE("keyword.operator.beaver"),
	QUESTION("keyword.operator.beaver"),
	STAR("keyword.operator.beaver"),
	PLUS("keyword.operator.beaver"),
	IDENTIFIER("source.identifier.beaver"),
	COMMENT("comment.line.double-slash.beaver"),
	NONASSOC("keyword.operator.beaver"),
	MULTILINE_COMMENT("");

	private short _index;
	private String _scope;

	/**
	 * static initializer
	 */
	static
	{
		short index = -1;

		for (BeaverTokenType type : EnumSet.allOf(BeaverTokenType.class))
		{
			type._index = index++;
		}
	}

	/**
	 * BeaverTokenType
	 * 
	 * @param scope
	 */
	private BeaverTokenType(String scope)
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
