/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.js.parsing.lexer;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import com.aptana.editor.js.parsing.Terminals;
import com.aptana.parsing.lexer.ITypePredicate;

public enum JSTokenType implements ITypePredicate
{
	UNDEFINED("UNDEFINED", -1), //$NON-NLS-1$
	EOF("EOF", Terminals.EOF), //$NON-NLS-1$
	LPAREN("(", Terminals.LPAREN), //$NON-NLS-1$
	IDENTIFIER("IDENTIFIER", Terminals.IDENTIFIER), //$NON-NLS-1$
	LCURLY("{", Terminals.LCURLY), //$NON-NLS-1$
	LBRACKET("[", Terminals.LBRACKET), //$NON-NLS-1$
	PLUS_PLUS("++", Terminals.PLUS_PLUS), //$NON-NLS-1$
	MINUS_MINUS("--", Terminals.MINUS_MINUS), //$NON-NLS-1$
	STRING("STRING", Terminals.STRING), //$NON-NLS-1$
	NUMBER("NUMBER", Terminals.NUMBER), //$NON-NLS-1$
	MINUS("-", Terminals.MINUS), //$NON-NLS-1$
	PLUS("+", Terminals.PLUS), //$NON-NLS-1$
	FUNCTION("function", Terminals.FUNCTION), //$NON-NLS-1$
	THIS("this", Terminals.THIS), //$NON-NLS-1$
	NEW("new", Terminals.NEW), //$NON-NLS-1$
	NULL("null", Terminals.NULL), //$NON-NLS-1$
	TRUE("true", Terminals.TRUE), //$NON-NLS-1$
	FALSE("false", Terminals.FALSE), //$NON-NLS-1$
	REGEX("REGEX", Terminals.REGEX), //$NON-NLS-1$
	DELETE("delete", Terminals.DELETE), //$NON-NLS-1$
	EXCLAMATION("!", Terminals.EXCLAMATION), //$NON-NLS-1$
	TILDE("~", Terminals.TILDE), //$NON-NLS-1$
	TYPEOF("typeof", Terminals.TYPEOF), //$NON-NLS-1$
	VOID("void", Terminals.VOID), //$NON-NLS-1$
	SEMICOLON(";", Terminals.SEMICOLON), //$NON-NLS-1$
	COMMA(",", Terminals.COMMA), //$NON-NLS-1$
	VAR("var", Terminals.VAR), //$NON-NLS-1$
	WHILE("while", Terminals.WHILE), //$NON-NLS-1$
	FOR("for", Terminals.FOR), //$NON-NLS-1$
	DO("do", Terminals.DO), //$NON-NLS-1$
	SWITCH("switch", Terminals.SWITCH), //$NON-NLS-1$
	IF("if", Terminals.IF), //$NON-NLS-1$
	CONTINUE("continue", Terminals.CONTINUE), //$NON-NLS-1$
	BREAK("break", Terminals.BREAK), //$NON-NLS-1$
	WITH("with", Terminals.WITH), //$NON-NLS-1$
	RETURN("return", Terminals.RETURN), //$NON-NLS-1$
	THROW("throw", Terminals.THROW), //$NON-NLS-1$
	TRY("try", Terminals.TRY), //$NON-NLS-1$
	RPAREN(")", Terminals.RPAREN), //$NON-NLS-1$
	ELSE("else", Terminals.ELSE), //$NON-NLS-1$
	RCURLY("}", Terminals.RCURLY), //$NON-NLS-1$
	COLON(":", Terminals.COLON), //$NON-NLS-1$
	RBRACKET("]", Terminals.RBRACKET), //$NON-NLS-1$
	IN("in", Terminals.IN), //$NON-NLS-1$
	EQUAL("=", Terminals.EQUAL), //$NON-NLS-1$
	CASE("case", Terminals.CASE), //$NON-NLS-1$
	DOT(".", Terminals.DOT), //$NON-NLS-1$
	LESS_LESS("<<", Terminals.LESS_LESS), //$NON-NLS-1$
	GREATER_GREATER(">>", Terminals.GREATER_GREATER), //$NON-NLS-1$
	GREATER_GREATER_GREATER(">>>", Terminals.GREATER_GREATER_GREATER), //$NON-NLS-1$
	LESS("<", Terminals.LESS), //$NON-NLS-1$
	GREATER(">", Terminals.GREATER), //$NON-NLS-1$
	LESS_EQUAL("<=", Terminals.LESS_EQUAL), //$NON-NLS-1$
	GREATER_EQUAL(">=", Terminals.GREATER_EQUAL), //$NON-NLS-1$
	INSTANCEOF("instanceof", Terminals.INSTANCEOF), //$NON-NLS-1$
	EQUAL_EQUAL("==", Terminals.EQUAL_EQUAL), //$NON-NLS-1$
	EXCLAMATION_EQUAL("!=", Terminals.EXCLAMATION_EQUAL), //$NON-NLS-1$
	EQUAL_EQUAL_EQUAL("===", Terminals.EQUAL_EQUAL_EQUAL), //$NON-NLS-1$
	EXCLAMATION_EQUAL_EQUAL("!==", Terminals.EXCLAMATION_EQUAL_EQUAL), //$NON-NLS-1$
	AMPERSAND("&", Terminals.AMPERSAND), //$NON-NLS-1$
	CARET("^", Terminals.CARET), //$NON-NLS-1$
	PIPE("|", Terminals.PIPE), //$NON-NLS-1$
	AMPERSAND_AMPERSAND("&&", Terminals.AMPERSAND_AMPERSAND), //$NON-NLS-1$
	STAR_EQUAL("*=", Terminals.STAR_EQUAL), //$NON-NLS-1$
	FORWARD_SLASH_EQUAL("/=", Terminals.FORWARD_SLASH_EQUAL), //$NON-NLS-1$
	PERCENT_EQUAL("%=", Terminals.PERCENT_EQUAL), //$NON-NLS-1$
	PLUS_EQUAL("+=", Terminals.PLUS_EQUAL), //$NON-NLS-1$
	MINUS_EQUAL("-=", Terminals.MINUS_EQUAL), //$NON-NLS-1$
	LESS_LESS_EQUAL("<<=", Terminals.LESS_LESS_EQUAL), //$NON-NLS-1$
	GREATER_GREATER_EQUAL(">>=", Terminals.GREATER_GREATER_EQUAL), //$NON-NLS-1$
	GREATER_GREATER_GREATER_EQUAL(">>>=", Terminals.GREATER_GREATER_GREATER_EQUAL), //$NON-NLS-1$
	AMPERSAND_EQUAL("&=", Terminals.AMPERSAND_EQUAL), //$NON-NLS-1$
	CARET_EQUAL("^=", Terminals.CARET_EQUAL), //$NON-NLS-1$
	PIPE_EQUAL("|=", Terminals.PIPE_EQUAL), //$NON-NLS-1$
	STAR("*", Terminals.STAR), //$NON-NLS-1$
	FORWARD_SLASH("/", Terminals.FORWARD_SLASH), //$NON-NLS-1$
	PERCENT("%", Terminals.PERCENT), //$NON-NLS-1$
	QUESTION("?", Terminals.QUESTION), //$NON-NLS-1$
	PIPE_PIPE("||", Terminals.PIPE_PIPE), //$NON-NLS-1$
	DEFAULT("default", Terminals.DEFAULT), //$NON-NLS-1$
	FINALLY("finally", Terminals.FINALLY), //$NON-NLS-1$
	CATCH("catch", Terminals.CATCH), //$NON-NLS-1$

	SINGLELINE_COMMENT("SINGLELINE_COMMENT", 1024), //$NON-NLS-1$
	MULTILINE_COMMENT("MULTILINE_COMMENT", 1025), //$NON-NLS-1$
	SDOC("SDOC", 1026), //$NON-NLS-1$
	VSDOC("VSDOC", 1027); //$NON-NLS-1$

	private static Map<String, JSTokenType> NAME_MAP;
	private static Map<Short, JSTokenType> ID_MAP;

	private String _name;
	private short _index;

	/**
	 * static initializer
	 */
	static
	{
		NAME_MAP = new HashMap<String, JSTokenType>();
		ID_MAP = new HashMap<Short, JSTokenType>();

		for (JSTokenType type : EnumSet.allOf(JSTokenType.class))
		{
			NAME_MAP.put(type.getName(), type);
			ID_MAP.put(type.getIndex(), type);
		}
	}

	/**
	 * JSTokenType
	 * 
	 * @param name
	 */
	private JSTokenType(String name, short beaverId)
	{
		this._name = name;
		this._index = beaverId;
	}

	private JSTokenType(String name, int index)
	{
		this(name, (short) index);
	}

	/**
	 * get
	 * 
	 * @param name
	 * @return
	 */
	public static JSTokenType get(String name)
	{
		JSTokenType result = UNDEFINED;

		if (NAME_MAP.containsKey(name))
		{
			result = NAME_MAP.get(name);
		}

		return result;
	}

	public static JSTokenType get(short id)
	{
		JSTokenType result = UNDEFINED;

		if (ID_MAP.containsKey(id))
		{
			result = ID_MAP.get(id);
		}

		return result;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.parsing.lexer.ITypePredicate#getIndex()
	 */
	public short getIndex()
	{
		return this._index;
	}

	/**
	 * getName
	 * 
	 * @return
	 */
	public String getName()
	{
		return this._name;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.css.parsing.lexer.ITypePredicate#isDefined()
	 */
	public boolean isDefined()
	{
		return this != UNDEFINED;
	}

	/**
	 * toString
	 */
	public String toString()
	{
		return this.getName();
	}
}
