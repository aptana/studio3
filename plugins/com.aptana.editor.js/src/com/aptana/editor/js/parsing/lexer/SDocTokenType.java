package com.aptana.editor.js.parsing.lexer;

import java.util.EnumSet;

public enum SDocTokenType
{
	UNDEFINED,			// -1
	EOF,				// 0
	IDENTIFIER,			// 1
	RCURLY,				// 2
	LCURLY,				// 3
	RBRACKET,			// 4
	LBRACKET,			// 5
	COLON,				// 6
	TEXT,				// 7
	POUND,				// 8
	ERROR,				// 9
	FUNCTION,			// 10
	ARRAY,				// 11
	COMMA,				// 12
	PIPE,				// 13
	RPAREN,				// 14
	LPAREN,				// 15
	CLASS_DESCRIPTION,	// 16
	EXCEPTION,			// 17
	EXTENDS,			// 18
	NAMESPACE,			// 19
	PARAM,				// 20
	PROPERTY,			// 21
	RETURN,				// 22
	ADVANCED,			// 23
	ALIAS,				// 24
	AUTHOR,				// 25
	CONSTRUCTOR,		// 26
	EXAMPLE,			// 27
	INTERNAL,			// 28
	METHOD,				// 29
	OVERVIEW,			// 30
	PRIVATE,			// 31
	SEE,				// 32
	UNKNOWN,			// 33
	END_DOCUMENTATION,	// 34
	ARROW,				// 35
	ELLIPSIS,			// 36
	LESS_THAN,			// 37
	GREATER_THAN,		// 38
	START_DOCUMENTATION,// 39
	WHITESPACE,			// 40
	TYPES;				// 41
	
	private short _index;

	/**
	 * static initializer
	 */
	static
	{
		short index = -1;
		
		for (SDocTokenType type : EnumSet.allOf(SDocTokenType.class))
		{
			type._index = index++;
		}
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
