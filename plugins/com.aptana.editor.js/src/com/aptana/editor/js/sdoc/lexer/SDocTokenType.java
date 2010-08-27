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
	CLASS,				// 12
	COMMA,				// 13
	PIPE,				// 14
	RPAREN,				// 15
	LPAREN,				// 16
	CLASS_DESCRIPTION,	// 17
	EXCEPTION,			// 18
	EXTENDS,			// 19
	NAMESPACE,			// 20
	PARAM,				// 21
	PROPERTY,			// 22
	RETURN,				// 23
	TYPE,				// 24
	ADVANCED,			// 25
	ALIAS,				// 26
	AUTHOR,				// 27
	CONSTRUCTOR,		// 28
	EXAMPLE,			// 29
	INTERNAL,			// 30
	METHOD,				// 31
	OVERVIEW,			// 32
	PRIVATE,			// 33
	SEE,				// 34
	UNKNOWN,			// 35
	END_DOCUMENTATION,	// 36
	ARROW,				// 37
	LESS_THAN,			// 38
	GREATER_THAN,		// 39
	ELLIPSIS,			// 40
	START_DOCUMENTATION,// 41
	
	WHITESPACE,			// 42
	TYPES,				// 43
	VALUE;				// 44
	
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
