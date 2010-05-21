package com.aptana.editor.js.parsing.lexer;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import com.aptana.parsing.lexer.ITypePredicate;

public enum JSTokenType implements ITypePredicate
{
	UNDEFINED("UNDEFINED"),
	EOF("EOF"),
	LPAREN("("),
	IDENTIFIER("IDENTIFIER"),
	LCURLY("{"),
	LBRACKET("["),
	PLUS_PLUS("++"),
	MINUS_MINUS("--"),
	STRING("STRING"),
	NUMBER("NUMBER"),
	MINUS("-"),
	PLUS("+"),
	FUNCTION("function"),
	THIS("this"),
	NEW("new"),
	NULL("null"),
	TRUE("true"),
	FALSE("false"),
	REGEX("REGEX"),
	DELETE("delete"),
	EXCLAMATION("!"),
	TILDE("~"),
	TYPEOF("typeof"),
	VOID("void"),
	SEMICOLON(";"),
	COMMA(","),
	VAR("var"),
	WHILE("while"),
	FOR("for"),
	DO("do"),
	SWITCH("switch"),
	IF("if"),
	CONTINUE("continue"),
	BREAK("break"),
	WITH("with"),
	RETURN("return"),
	THROW("throw"),
	TRY("try"),
	RPAREN(")"),
	ELSE("else"),
	RCURLY("}"),
	COLON(":"),
	RBRACKET("]"),
	IN("in"),
	EQUAL("="),
	CASE("case"),
	DOT("."),
	LESS_LESS("<<"),
	GREATER_GREATER(">>"),
	GREATER_GREATER_GREATER(">>>"),
	LESS("<"),
	GREATER(">"),
	LESS_EQUAL("<="),
	GREATER_EQUAL(">="),
	INSTANCEOF("instanceof"),
	EQUAL_EQUAL("=="),
	EXCLAMATION_EQUAL("!="),
	EQUAL_EQUAL_EQUAL("==="),
	EXCLAMATION_EQUAL_EQUAL("!=="),
	AMPERSAND("&"),
	CARET("^"),
	PIPE("|"),
	AMPERSAND_AMPERSAND("&&"),
	STAR_EQUAL("*="),
	FORWARD_SLASH_EQUAL("/="),
	PERCENT_EQUAL("%="),
	PLUS_EQUAL("+="),
	MINUS_EQUAL("-="),
	LESS_LESS_EQUAL("<<="),
	GREATER_GREATER_EQUAL(">>="),
	GREATER_GREATER_GREATER_EQUAL(">>>="),
	AMPERSAND_EQUAL("&="),
	CARET_EQUAL("^="),
	PIPE_EQUAL("|="),
	STAR("*"),
	FORWARD_SLASH("/"),
	PERCENT("%"),
	QUESTION("?"),
	PIPE_PIPE("||"),
	DEFAULT("default"),
	FINALLY("finally"),
	CATCH("catch"),
	SINGLELINE_COMMENT("SINGLELINE_COMMENT"),
	MULTILINE_COMMENT("MULTILINE_COMMENT"),
	DOC("DOC");

	private static Map<String, JSTokenType> NAME_MAP;
	
	private String _name;
	private short _index;

	/**
	 * static
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
	@Override
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
