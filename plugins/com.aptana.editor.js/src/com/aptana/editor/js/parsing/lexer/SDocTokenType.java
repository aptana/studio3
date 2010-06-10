package com.aptana.editor.js.parsing.lexer;

import java.util.EnumSet;

public enum SDocTokenType
{
	UNDEFINED,			// -
	EOF,				// -
	IDENTIFIER,			//
	RCURLY,				//
	LCURLY,				//
	RBRACKET,			//
	LBRACKET,			//
	COLON,				//
	TEXT,				//
	POUND,				//
	ERROR,				// -
	FUNCTION,			//
	ARRAY,				//
	COMMA,				//
	PIPE,				//
	RPAREN,				//
	LPAREN,				//
	CLASS_DESCRIPTION,	//
	EXCEPTION,			//
	EXTENDS,			//
	NAMESPACE,			//
	PARAM,				//
	PROPERTY,			//
	RETURN,				//
	ADVANCED,			//
	ALIAS,				//
	AUTHOR,				//
	CONSTRUCTOR,		//
	EXAMPLE,			//
	INTERNAL,			//
	METHOD,				//
	OVERVIEW,			//
	PRIVATE,			//
	SEE,				//
	UNKNOWN,			//
	END_DOCUMENTATION,
	ARROW,
	ELLIPSIS,
	LESS_THAN,			//
	GREATER_THAN,		//
	START_DOCUMENTATION,
	WHITESPACE,			//
	TYPES;				//
	
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
