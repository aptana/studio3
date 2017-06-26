/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.js.core.parsing;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import com.aptana.js.core.JSLanguageConstants;
import com.aptana.parsing.lexer.ITypePredicate;

public enum JSTokenType implements ITypePredicate
{
	UNDEFINED("UNDEFINED", -1), //$NON-NLS-1$
	EOF("EOF", Terminals.EOF), //$NON-NLS-1$
	LPAREN(JSLanguageConstants.LPAREN, Terminals.LPAREN),
	IDENTIFIER("IDENTIFIER", Terminals.IDENTIFIER), //$NON-NLS-1$
	LCURLY(JSLanguageConstants.LCURLY, Terminals.LCURLY),
	LBRACKET(JSLanguageConstants.LBRACKET, Terminals.LBRACKET),
	PLUS_PLUS(JSLanguageConstants.PLUS_PLUS, Terminals.PLUS_PLUS),
	MINUS_MINUS(JSLanguageConstants.MINUS_MINUS, Terminals.MINUS_MINUS),
	STRING("STRING", Terminals.STRING), //$NON-NLS-1$
	NUMBER("NUMBER", Terminals.NUMBER), //$NON-NLS-1$
	MINUS(JSLanguageConstants.MINUS, Terminals.MINUS),
	PLUS(JSLanguageConstants.PLUS, Terminals.PLUS),
	FUNCTION(JSLanguageConstants.FUNCTION, Terminals.FUNCTION),
	THIS(JSLanguageConstants.THIS, Terminals.THIS),
	NEW(JSLanguageConstants.NEW, Terminals.NEW),
	NULL(JSLanguageConstants.NULL, Terminals.NULL),
	TRUE(JSLanguageConstants.TRUE, Terminals.TRUE),
	FALSE(JSLanguageConstants.FALSE, Terminals.FALSE),
	TEMPLATE_HEAD("TEMPLATE_HEAD", Terminals.TEMPLATE_HEAD), //$NON-NLS-1$
	NO_SUB_TEMPLATE("NO_SUB_TEMPLATE", Terminals.NO_SUB_TEMPLATE), //$NON-NLS-1$
	TEMPLATE_MIDDLE("TEMPLATE_MIDDLE", Terminals.TEMPLATE_MIDDLE), //$NON-NLS-1$
	TEMPLATE_TAIL("TEMPLATE_TAIL", Terminals.TEMPLATE_TAIL), //$NON-NLS-1$
	REGEX("REGEX", Terminals.REGEX), //$NON-NLS-1$
	DELETE(JSLanguageConstants.DELETE, Terminals.DELETE),
	EXCLAMATION(JSLanguageConstants.EXCLAMATION, Terminals.EXCLAMATION),
	TILDE(JSLanguageConstants.TILDE, Terminals.TILDE),
	TYPEOF(JSLanguageConstants.TYPEOF, Terminals.TYPEOF),
	VOID(JSLanguageConstants.VOID, Terminals.VOID),
	SEMICOLON(JSLanguageConstants.SEMICOLON, Terminals.SEMICOLON),
	COMMA(JSLanguageConstants.COMMA, Terminals.COMMA),
	VAR(JSLanguageConstants.VAR, Terminals.VAR),
	WHILE(JSLanguageConstants.WHILE, Terminals.WHILE),
	FOR(JSLanguageConstants.FOR, Terminals.FOR),
	DO(JSLanguageConstants.DO, Terminals.DO),
	SWITCH(JSLanguageConstants.SWITCH, Terminals.SWITCH),
	IF(JSLanguageConstants.IF, Terminals.IF),
	OF(JSLanguageConstants.OF, Terminals.OF),
	CONTINUE(JSLanguageConstants.CONTINUE, Terminals.CONTINUE),
	BREAK(JSLanguageConstants.BREAK, Terminals.BREAK),
	WITH(JSLanguageConstants.WITH, Terminals.WITH),
	RETURN(JSLanguageConstants.RETURN, Terminals.RETURN),
	THROW(JSLanguageConstants.THROW, Terminals.THROW),
	TRY(JSLanguageConstants.TRY, Terminals.TRY),
	RPAREN(JSLanguageConstants.RPAREN, Terminals.RPAREN),
	ELSE(JSLanguageConstants.ELSE, Terminals.ELSE),
	RCURLY(JSLanguageConstants.RCURLY, Terminals.RCURLY),
	COLON(JSLanguageConstants.COLON, Terminals.COLON),
	RBRACKET(JSLanguageConstants.RBRACKET, Terminals.RBRACKET),
	IN(JSLanguageConstants.IN, Terminals.IN),
	EQUAL(JSLanguageConstants.EQUAL, Terminals.EQUAL),
	CASE(JSLanguageConstants.CASE, Terminals.CASE),
	TARGET(JSLanguageConstants.TARGET, Terminals.TARGET),
	DOT_DOT_DOT(JSLanguageConstants.DOT_DOT_DOT, Terminals.DOT_DOT_DOT),
	DOT(JSLanguageConstants.DOT, Terminals.DOT),
	LESS_LESS(JSLanguageConstants.LESS_LESS, Terminals.LESS_LESS),
	GREATER_GREATER(JSLanguageConstants.GREATER_GREATER, Terminals.GREATER_GREATER),
	GREATER_GREATER_GREATER(JSLanguageConstants.GREATER_GREATER_GREATER, Terminals.GREATER_GREATER_GREATER),
	LESS(JSLanguageConstants.LESS, Terminals.LESS),
	GREATER(JSLanguageConstants.GREATER, Terminals.GREATER),
	LESS_EQUAL(JSLanguageConstants.LESS_EQUAL, Terminals.LESS_EQUAL),
	GREATER_EQUAL(JSLanguageConstants.GREATER_EQUAL, Terminals.GREATER_EQUAL),
	INSTANCEOF(JSLanguageConstants.INSTANCEOF, Terminals.INSTANCEOF),
	EQUAL_EQUAL(JSLanguageConstants.EQUAL_EQUAL, Terminals.EQUAL_EQUAL),
	ARROW(JSLanguageConstants.ARROW, Terminals.ARROW),
	EXCLAMATION_EQUAL(JSLanguageConstants.EXCLAMATION_EQUAL, Terminals.EXCLAMATION_EQUAL),
	EQUAL_EQUAL_EQUAL(JSLanguageConstants.EQUAL_EQUAL_EQUAL, Terminals.EQUAL_EQUAL_EQUAL),
	EXCLAMATION_EQUAL_EQUAL(JSLanguageConstants.EXCLAMATION_EQUAL_EQUAL, Terminals.EXCLAMATION_EQUAL_EQUAL),
	AMPERSAND(JSLanguageConstants.AMPERSAND, Terminals.AMPERSAND),
	CARET(JSLanguageConstants.CARET, Terminals.CARET),
	PIPE(JSLanguageConstants.PIPE, Terminals.PIPE),
	AMPERSAND_AMPERSAND(JSLanguageConstants.AMPERSAND_AMPERSAND, Terminals.AMPERSAND_AMPERSAND),
	STAR_EQUAL(JSLanguageConstants.STAR_EQUAL, Terminals.STAR_EQUAL),
	FORWARD_SLASH_EQUAL(JSLanguageConstants.FORWARD_SLASH_EQUAL, Terminals.FORWARD_SLASH_EQUAL),
	PERCENT_EQUAL(JSLanguageConstants.PERCENT_EQUAL, Terminals.PERCENT_EQUAL),
	PLUS_EQUAL(JSLanguageConstants.PLUS_EQUAL, Terminals.PLUS_EQUAL),
	MINUS_EQUAL(JSLanguageConstants.MINUS_EQUAL, Terminals.MINUS_EQUAL),
	LESS_LESS_EQUAL(JSLanguageConstants.LESS_LESS_EQUAL, Terminals.LESS_LESS_EQUAL),
	GREATER_GREATER_EQUAL(JSLanguageConstants.GREATER_GREATER_EQUAL, Terminals.GREATER_GREATER_EQUAL),
	GREATER_GREATER_GREATER_EQUAL(JSLanguageConstants.GREATER_GREATER_GREATER_EQUAL, Terminals.GREATER_GREATER_GREATER_EQUAL),
	AMPERSAND_EQUAL(JSLanguageConstants.AMPERSAND_EQUAL, Terminals.AMPERSAND_EQUAL),
	CARET_EQUAL(JSLanguageConstants.CARET_EQUAL, Terminals.CARET_EQUAL),
	PIPE_EQUAL(JSLanguageConstants.PIPE_EQUAL, Terminals.PIPE_EQUAL),
	STAR(JSLanguageConstants.STAR, Terminals.STAR),
	FORWARD_SLASH(JSLanguageConstants.FORWARD_SLASH, Terminals.FORWARD_SLASH),
	PERCENT(JSLanguageConstants.PERCENT, Terminals.PERCENT),
	QUESTION(JSLanguageConstants.QUESTION, Terminals.QUESTION),
	PIPE_PIPE(JSLanguageConstants.PIPE_PIPE, Terminals.PIPE_PIPE),
	DEFAULT(JSLanguageConstants.DEFAULT, Terminals.DEFAULT),
	FINALLY(JSLanguageConstants.FINALLY, Terminals.FINALLY),
	CATCH(JSLanguageConstants.CATCH, Terminals.CATCH),
	DEBUGGER("debugger", Terminals.DEBUGGER),
	CLASS(JSLanguageConstants.CLASS, Terminals.CLASS),
	ENUM("enum", Terminals.ENUM),
	EXPORT(JSLanguageConstants.EXPORT, Terminals.EXPORT),
	EXTENDS(JSLanguageConstants.EXTENDS, Terminals.EXTENDS),
	IMPORT(JSLanguageConstants.IMPORT, Terminals.IMPORT),
	SUPER(JSLanguageConstants.SUPER, Terminals.SUPER),
	IMPLEMENTS("implements", Terminals.IMPLEMENTS),
	INTERFACE("interface", Terminals.INTERFACE),
	LET(JSLanguageConstants.LET, Terminals.LET),
	AWAIT(JSLanguageConstants.AWAIT, Terminals.AWAIT),
	CONST(JSLanguageConstants.CONST, Terminals.CONST),
	PACKAGE("package", Terminals.PACKAGE),
	PRIVATE("private", Terminals.PRIVATE),
	PROTECTED("protected", Terminals.PROTECTED),
	PUBLIC("public", Terminals.PUBLIC),
	STATIC("static", Terminals.STATIC),
	FROM(JSLanguageConstants.FROM, Terminals.FROM),
	YIELD(JSLanguageConstants.YIELD, Terminals.YIELD),
	SET(JSLanguageConstants.SET, Terminals.SET),
	GET(JSLanguageConstants.GET, Terminals.GET),
	AS(JSLanguageConstants.AS, Terminals.AS),
	
	
	SINGLELINE_COMMENT("SINGLELINE_COMMENT", 1024), //$NON-NLS-1$
	MULTILINE_COMMENT("MULTILINE_COMMENT", 1025), //$NON-NLS-1$
	SDOC("SDOC", 1026), //$NON-NLS-1$
	VSDOC("VSDOC", 1027), //$NON-NLS-1$

	// Note: STRING_SINGLE and STRING_DOUBLE do not map to Terminals.STRING because they should not
	// override it later on in the mappings (also, they are just needed for the coloring scanner and
	// not outside of that scope).
	STRING_SINGLE("STRING_SINGLE", 1028), //$NON-NLS-1$
	STRING_DOUBLE("STRING_DOUBLE", 1029); //$NON-NLS-1$

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