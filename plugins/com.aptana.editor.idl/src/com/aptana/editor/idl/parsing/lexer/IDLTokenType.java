/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.idl.parsing.lexer;

import com.aptana.editor.idl.parsing.Terminals;

@SuppressWarnings("nls")
public enum IDLTokenType
{
	UNDEFINED("source.idl", -1),
	EOF("source.idl", Terminals.EOF),

	IDENTIFIER("source.idl", Terminals.IDENTIFIER),
	DOUBLE_COLON("keyword.operator.idl", Terminals.DOUBLE_COLON),
	SEMICOLON("punctuation.terminator.statement.idl", Terminals.SEMICOLON),
	LBRACKET("meta.brace.square.idl", Terminals.LBRACKET),
	LONG("keyword.operator.idl", Terminals.LONG),
	RCURLY("meta.brace.curly.idl", Terminals.RCURLY),
	SHORT("keyword.operator.idl", Terminals.SHORT),
	LPAREN("meta.brace.round.idl", Terminals.LPAREN),
	SEQUENCE("keyword.operator.idl", Terminals.SEQUENCE),
	UNSIGNED("keyword.operator.idl", Terminals.UNSIGNED),
	BOOLEAN("keyword.operator.idl", Terminals.BOOLEAN),
	OCTET("keyword.operator.idl", Terminals.OCTET),
	FLOAT("keyword.operator.idl", Terminals.FLOAT),
	DOUBLE("keyword.operator.idl", Terminals.DOUBLE),
	DOMSTRING("keyword.operator.idl", Terminals.DOMSTRING),
	ANY("keyword.operator.idl", Terminals.ANY),
	OBJECT("keyword.operator.idl", Terminals.OBJECT),
	QUESTION("keyword.operator.idl", Terminals.QUESTION),
	RPAREN("meta.brace.round.idl", Terminals.RPAREN),
	MODULE("keyword.operator.idl", Terminals.MODULE),
	INTERFACE("keyword.operator.idl", Terminals.INTERFACE),
	EXCEPTION("keyword.operator.idl", Terminals.EXCEPTION),
	TYPEDEF("keyword.operator.idl", Terminals.TYPEDEF),
	COMMA("meta.delimiter.object.comma.idl", Terminals.COMMA),
	LCURLY("meta.brace.curly.idl", Terminals.LCURLY),
	ATTRIBUTE("keyword.operator.idl", Terminals.ATTRIBUTE),
	GETTER("keyword.operator.idl", Terminals.GETTER),
	SETTER("keyword.operator.idl", Terminals.SETTER),
	CREATOR("keyword.operator.idl", Terminals.CREATOR),
	DELETER("keyword.operator.idl", Terminals.DELETER),
	CALLER("keyword.operator.idl", Terminals.CALLER),
	EQUAL("keyword.operator.idl", Terminals.EQUAL),
	CONST("keyword.operator.idl", Terminals.CONST),
	RBRACKET("meta.brace.square.idl", Terminals.RBRACKET),
	READONLY("keyword.operator.idl", Terminals.READONLY),
	VOID("constant.language.void.idl", Terminals.VOID),
	LESS_THAN("keyword.operator.idl", Terminals.LESS_THAN),
	GREATER_THAN("keyword.operator.idl", Terminals.GREATER_THAN),
	IMPLEMENTS("keyword.operator.idl", Terminals.IMPLEMENTS),
	IN("keyword.operator.idl", Terminals.IN),
	OPTIONAL("keyword.operator.idl", Terminals.OPTIONAL),
	ELLIPSIS("keyword.operator.idl", Terminals.ELLIPSIS),
	COLON("keyword.operator.idl", Terminals.COLON),
	NUMBER("constant.numeric.js", Terminals.NUMBER),
	TRUE("constant.language.boolean.true.idl", Terminals.TRUE),
	FALSE("constant.language.boolean.false.idl", Terminals.FALSE),
	STRINGIFIER("keyword.operator.idl", Terminals.STRINGIFIER),
	GETRAISES("keyword.operator.idl", Terminals.GETRAISES),
	SETRAISES("keyword.operator.idl", Terminals.SETRAISES),
	OMITTABLE("keyword.operator.idl", Terminals.OMITTABLE),
	RAISES("keyword.operator.idl", Terminals.RAISES),

	OTHER("source.idl", 52),
	STRING("source.idl", 53),

	COMMENT("comment.dtd", 54),
	DOC_COMMENT("comment.dtd", 55),
	MULTILINE_COMMENT("comment.dtd", 56);

	private short _index;
	private String _scope;

	/**
	 * IDLTokenType
	 * 
	 * @param scope
	 */
	private IDLTokenType(String scope, short beaverId)
	{
		this._scope = scope;
		this._index = beaverId;
	}

	private IDLTokenType(String scope, int index)
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
