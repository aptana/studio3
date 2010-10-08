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
