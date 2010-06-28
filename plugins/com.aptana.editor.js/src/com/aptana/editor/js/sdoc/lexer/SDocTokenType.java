package com.aptana.editor.js.sdoc.lexer;

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
	TYPE,				// 23
	ADVANCED,			// 24
	ALIAS,				// 25
	AUTHOR,				// 26
	CONSTRUCTOR,		// 27
	EXAMPLE,			// 28
	INTERNAL,			// 29
	METHOD,				// 30
	OVERVIEW,			// 31
	PRIVATE,			// 32
	SEE,				// 33
	UNKNOWN,			// 34
	END_DOCUMENTATION,	// 35
	ARROW,				// 36
	ELLIPSIS,			// 37
	LESS_THAN,			// 38
	GREATER_THAN,		// 39
	START_DOCUMENTATION,// 40
	
	WHITESPACE,			// 41
	TYPES,				// 42
	VALUE;				// 43
	
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
