/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.idl.parsing.lexer;

import java.util.EnumSet;

@SuppressWarnings("nls")
public enum IDLTokenType
{
	UNDEFINED("source.idl"), // -1
	EOF("source.idl"), // 0

	IDENTIFIER("source.idl"), // 1
	DOUBLE_COLON("keyword.operator.idl"), // 2
	SEMICOLON("punctuation.terminator.statement.idl"), // 3
	LBRACKET("meta.brace.square.idl"), // 4
	LONG("keyword.operator.idl"), // 5
	RCURLY("meta.brace.curly.idl"), // 6
	SHORT("keyword.operator.idl"), // 7
	LPAREN("meta.brace.round.idl"), // 8
	SEQUENCE("keyword.operator.idl"), // 9
	UNSIGNED("keyword.operator.idl"), // 10
	BOOLEAN("keyword.operator.idl"), // 11
	OCTET("keyword.operator.idl"), // 12
	FLOAT("keyword.operator.idl"), // 13
	DOUBLE("keyword.operator.idl"), // 14
	DOMSTRING("keyword.operator.idl"), // 15
	ANY("keyword.operator.idl"), // 16
	OBJECT("keyword.operator.idl"), // 17
	QUESTION("keyword.operator.idl"), // 18
	RPAREN("meta.brace.round.idl"), // 19
	MODULE("keyword.operator.idl"), // 20
	INTERFACE("keyword.operator.idl"), // 21
	EXCEPTION("keyword.operator.idl"), // 22
	TYPEDEF("keyword.operator.idl"), // 23
	COMMA("meta.delimiter.object.comma.idl"), // 24
	LCURLY("meta.brace.curly.idl"), // 25
	ATTRIBUTE("keyword.operator.idl"), // 26
	GETTER("keyword.operator.idl"), // 27
	SETTER("keyword.operator.idl"), // 28
	CREATOR("keyword.operator.idl"), // 29
	DELETER("keyword.operator.idl"), // 30
	CALLER("keyword.operator.idl"), // 31
	EQUAL("keyword.operator.idl"), // 32
	CONST("keyword.operator.idl"), // 33
	RBRACKET("meta.brace.square.idl"), // 34
	READONLY("keyword.operator.idl"), // 35
	VOID("constant.language.void.idl"), // 36
	LESS_THAN("keyword.operator.idl"), // 37
	GREATER_THAN("keyword.operator.idl"), // 38
	IMPLEMENTS("keyword.operator.idl"), // 39
	IN("keyword.operator.idl"), // 40
	OPTIONAL("keyword.operator.idl"), // 41
	ELLIPSIS("keyword.operator.idl"), // 42
	COLON("keyword.operator.idl"), // 43
	NUMBER("constant.numeric.js"), // 44
	TRUE("constant.language.boolean.true.idl"), // 45
	FALSE("constant.language.boolean.false.idl"), // 46
	STRINGIFIER("keyword.operator.idl"), // 47
	GETRAISES("keyword.operator.idl"), // 48
	SETRAISES("keyword.operator.idl"), // 49
	OMITTABLE("keyword.operator.idl"), // 50
	RAISES("keyword.operator.idl"), // 51

	OTHER("source.idl"),
	STRING("source.idl"),

	COMMENT("comment.dtd"),
	DOC_COMMENT("comment.dtd"),
	MULTILINE_COMMENT("comment.dtd");

	private short _index;
	private String _scope;

	/**
	 * static initializer
	 */
	static
	{
		short index = -1;

		for (IDLTokenType type : EnumSet.allOf(IDLTokenType.class))
		{
			type._index = index++;
		}
	}

	/**
	 * IDLTokenType
	 * 
	 * @param scope
	 */
	private IDLTokenType(String scope)
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
