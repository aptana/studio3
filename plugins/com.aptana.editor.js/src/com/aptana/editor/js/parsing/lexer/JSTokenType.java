/**
 * This file Copyright (c) 2005-2010 Aptana, Inc. This program is
 * dual-licensed under both the Aptana Public License and the GNU General
 * Public license. You may elect to use one or the other of these licenses.
 * 
 * This program is distributed in the hope that it will be useful, but
 * AS-IS and WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE, TITLE, or
 * NONINFRINGEMENT. Redistribution, except as permitted by whichever of
 * the GPL or APL you select, is prohibited.
 *
 * 1. For the GPL license (GPL), you can redistribute and/or modify this
 * program under the terms of the GNU General Public License,
 * Version 3, as published by the Free Software Foundation.  You should
 * have received a copy of the GNU General Public License, Version 3 along
 * with this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 * 
 * Aptana provides a special exception to allow redistribution of this file
 * with certain other free and open source software ("FOSS") code and certain additional terms
 * pursuant to Section 7 of the GPL. You may view the exception and these
 * terms on the web at http://www.aptana.com/legal/gpl/.
 * 
 * 2. For the Aptana Public License (APL), this program and the
 * accompanying materials are made available under the terms of the APL
 * v1.0 which accompanies this distribution, and is available at
 * http://www.aptana.com/legal/apl/.
 * 
 * You may view the GPL, Aptana's exception and additional terms, and the
 * APL in the file titled license.html at the root of the corresponding
 * plugin containing this source file.
 * 
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.js.parsing.lexer;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import com.aptana.parsing.lexer.ITypePredicate;

public enum JSTokenType implements ITypePredicate
{
	UNDEFINED("UNDEFINED"), //$NON-NLS-1$
	EOF("EOF"), //$NON-NLS-1$
	LPAREN("("), //$NON-NLS-1$
	IDENTIFIER("IDENTIFIER"), //$NON-NLS-1$
	LCURLY("{"), //$NON-NLS-1$
	LBRACKET("["), //$NON-NLS-1$
	PLUS_PLUS("++"), //$NON-NLS-1$
	MINUS_MINUS("--"), //$NON-NLS-1$
	STRING("STRING"), //$NON-NLS-1$
	NUMBER("NUMBER"), //$NON-NLS-1$
	MINUS("-"), //$NON-NLS-1$
	PLUS("+"), //$NON-NLS-1$
	FUNCTION("function"), //$NON-NLS-1$
	THIS("this"), //$NON-NLS-1$
	NEW("new"), //$NON-NLS-1$
	NULL("null"), //$NON-NLS-1$
	TRUE("true"), //$NON-NLS-1$
	FALSE("false"), //$NON-NLS-1$
	REGEX("REGEX"), //$NON-NLS-1$
	DELETE("delete"), //$NON-NLS-1$
	EXCLAMATION("!"), //$NON-NLS-1$
	TILDE("~"), //$NON-NLS-1$
	TYPEOF("typeof"), //$NON-NLS-1$
	VOID("void"), //$NON-NLS-1$
	SEMICOLON(";"), //$NON-NLS-1$
	COMMA(","), //$NON-NLS-1$
	VAR("var"), //$NON-NLS-1$
	WHILE("while"), //$NON-NLS-1$
	FOR("for"), //$NON-NLS-1$
	DO("do"), //$NON-NLS-1$
	SWITCH("switch"), //$NON-NLS-1$
	IF("if"), //$NON-NLS-1$
	CONTINUE("continue"), //$NON-NLS-1$
	BREAK("break"), //$NON-NLS-1$
	WITH("with"), //$NON-NLS-1$
	RETURN("return"), //$NON-NLS-1$
	THROW("throw"), //$NON-NLS-1$
	TRY("try"), //$NON-NLS-1$
	RPAREN(")"), //$NON-NLS-1$
	ELSE("else"), //$NON-NLS-1$
	RCURLY("}"), //$NON-NLS-1$
	COLON(":"), //$NON-NLS-1$
	RBRACKET("]"), //$NON-NLS-1$
	IN("in"), //$NON-NLS-1$
	EQUAL("="), //$NON-NLS-1$
	CASE("case"), //$NON-NLS-1$
	DOT("."), //$NON-NLS-1$
	LESS_LESS("<<"), //$NON-NLS-1$
	GREATER_GREATER(">>"), //$NON-NLS-1$
	GREATER_GREATER_GREATER(">>>"), //$NON-NLS-1$
	LESS("<"), //$NON-NLS-1$
	GREATER(">"), //$NON-NLS-1$
	LESS_EQUAL("<="), //$NON-NLS-1$
	GREATER_EQUAL(">="), //$NON-NLS-1$
	INSTANCEOF("instanceof"), //$NON-NLS-1$
	EQUAL_EQUAL("=="), //$NON-NLS-1$
	EXCLAMATION_EQUAL("!="), //$NON-NLS-1$
	EQUAL_EQUAL_EQUAL("==="), //$NON-NLS-1$
	EXCLAMATION_EQUAL_EQUAL("!=="), //$NON-NLS-1$
	AMPERSAND("&"), //$NON-NLS-1$
	CARET("^"), //$NON-NLS-1$
	PIPE("|"), //$NON-NLS-1$
	AMPERSAND_AMPERSAND("&&"), //$NON-NLS-1$
	STAR_EQUAL("*="), //$NON-NLS-1$
	FORWARD_SLASH_EQUAL("/="), //$NON-NLS-1$
	PERCENT_EQUAL("%="), //$NON-NLS-1$
	PLUS_EQUAL("+="), //$NON-NLS-1$
	MINUS_EQUAL("-="), //$NON-NLS-1$
	LESS_LESS_EQUAL("<<="), //$NON-NLS-1$
	GREATER_GREATER_EQUAL(">>="), //$NON-NLS-1$
	GREATER_GREATER_GREATER_EQUAL(">>>="), //$NON-NLS-1$
	AMPERSAND_EQUAL("&="), //$NON-NLS-1$
	CARET_EQUAL("^="), //$NON-NLS-1$
	PIPE_EQUAL("|="), //$NON-NLS-1$
	STAR("*"), //$NON-NLS-1$
	FORWARD_SLASH("/"), //$NON-NLS-1$
	PERCENT("%"), //$NON-NLS-1$
	QUESTION("?"), //$NON-NLS-1$
	PIPE_PIPE("||"), //$NON-NLS-1$
	DEFAULT("default"), //$NON-NLS-1$
	FINALLY("finally"), //$NON-NLS-1$
	CATCH("catch"), //$NON-NLS-1$
	SINGLELINE_COMMENT("SINGLELINE_COMMENT"), //$NON-NLS-1$
	MULTILINE_COMMENT("MULTILINE_COMMENT"), //$NON-NLS-1$
	SDOC("SDOC"), //$NON-NLS-1$
	VSDOC("VSDOC"); //$NON-NLS-1$

	private static Map<String, JSTokenType> NAME_MAP;
	
	private String _name;
	private short _index;

	/**
	 * static initializer
	 */
	static
	{
		short index = -1;
		
		for (JSTokenType type : EnumSet.allOf(JSTokenType.class))
		{
			type._index = index++;
		}
		
		NAME_MAP = new HashMap<String, JSTokenType>();

		for (JSTokenType type : EnumSet.allOf(JSTokenType.class))
		{
			NAME_MAP.put(type.getName(), type);
		}
	}

	/**
	 * CSSTokenTypes
	 * 
	 * @param name
	 */
	private JSTokenType(String name)
	{
		this._name = name;
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
		return (this != UNDEFINED);
	}

	/**
	 * toString
	 */
	public String toString()
	{
		return this.getName();
	}
}
