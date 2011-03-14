/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.js.sdoc.parsing;

import org.eclipse.jface.text.rules.RuleBasedScanner;

import com.aptana.editor.js.sdoc.lexer.SDocTokenType;
import com.aptana.editor.js.sdoc.parsing.SDocTypeTokenScanner;

public class SDocTypeTokenScannerTests extends SDocScannerTestBase
{
	/* (non-Javadoc)
	 * @see com.aptana.editor.js.SDocScannerTestBase#createScanner()
	 */
	@Override
	protected RuleBasedScanner createScanner()
	{
		return new SDocTypeTokenScanner();
	}
	
	/**
	 * testLeftParen
	 */
	public void testLeftParen()
	{
		String source = "(";
		
		lexemeTypeTests(
			source,
			SDocTokenType.LPAREN
		);
	}
	
	/**
	 * testRightParen
	 */
	public void testRightParen()
	{
		String source = ")";
		
		lexemeTypeTests(
			source,
			SDocTokenType.RPAREN
		);
	}
	
	/**
	 * testLeftCurly
	 */
	public void testLeftCurly()
	{
		String source = "{";
		
		lexemeTypeTests(
			source,
			SDocTokenType.LCURLY
		);
	}
	
	/**
	 * testRightCurly
	 */
	public void testRightCurly()
	{
		String source = "}";
		
		lexemeTypeTests(
			source,
			SDocTokenType.RCURLY
		);
	}
	
	/**
	 * testLeftBracket
	 */
	public void testLeftBracket()
	{
		String source = "[";
		
		lexemeTypeTests(
			source,
			SDocTokenType.LBRACKET
		);
	}
	
	/**
	 * testRightBracket
	 */
	public void testRightBracket()
	{
		String source = "]";
		
		lexemeTypeTests(
			source,
			SDocTokenType.RBRACKET
		);
	}
	
	/**
	 * testLessThan
	 */
	public void testLessThan()
	{
		String source = "<";
		
		lexemeTypeTests(
			source,
			SDocTokenType.LESS_THAN
		);
	}
	
	/**
	 * testGreaterThan
	 */
	public void testGreaterThan()
	{
		String source = ">";
		
		lexemeTypeTests(
			source,
			SDocTokenType.GREATER_THAN
		);
	}
	
	/**
	 * testColon
	 */
	public void testColon()
	{
		String source = ":";
		
		lexemeTypeTests(
			source,
			SDocTokenType.COLON
		);
	}
	
	/**
	 * testComma
	 */
	public void testComma()
	{
		String source = ",";
		
		lexemeTypeTests(
			source,
			SDocTokenType.COMMA
		);
	}
	
	/**
	 * testPipe
	 */
	public void testPipe()
	{
		String source = "|";
		
		lexemeTypeTests(
			source,
			SDocTokenType.PIPE
		);
	}
	
	/**
	 * testArrow
	 */
	public void testArrow()
	{
		String source = "->";
		
		lexemeTypeTests(
			source,
			SDocTokenType.ARROW
		);
	}
	
	/**
	 * testEllipsis
	 */
	public void testEllipsis()
	{
		String source = "...";
		
		lexemeTypeTests(
			source,
			SDocTokenType.ELLIPSIS
		);
	}
	
	/**
	 * testFunction
	 */
	public void testFunction()
	{
		String source = "Function";
		
		lexemeTypeTests(
			source,
			SDocTokenType.FUNCTION
		);
	}
	
	/**
	 * testArray
	 */
	public void testArray()
	{
		String source = "Array";
		
		lexemeTypeTests(
			source,
			SDocTokenType.ARRAY
		);
	}
	
	/**
	 * testSimpleIdentifiers
	 */
	public void testSimpleIdentifiers()
	{
		String source = "Number $number _number";
		
		lexemeTypeTests(
			source,
			SDocTokenType.IDENTIFIER,
			SDocTokenType.WHITESPACE,
			SDocTokenType.IDENTIFIER,
			SDocTokenType.WHITESPACE,
			SDocTokenType.IDENTIFIER
		);
	}
	
	/**
	 * testNearIdentifiers
	 */
	public void testNearIdentifiers()
	{
		String source = "Functions Arrays";
		
		lexemeTypeTests(
			source,
			SDocTokenType.IDENTIFIER,
			SDocTokenType.WHITESPACE,
			SDocTokenType.IDENTIFIER
		);
	}
	
	/**
	 * testCR
	 */
	public void testCR()
	{
		String source = "\r";
		
		lexemeTypeTests(
			source,
			SDocTokenType.WHITESPACE
		);
	}
	
	/**
	 * testLF
	 */
	public void testLF()
	{
		String source = "\n";
		
		lexemeTypeTests(
			source,
			SDocTokenType.WHITESPACE
		);
	}
	
	/**
	 * testCRLF
	 */
	public void testCRLF()
	{
		String source = "\r\n";
		
		lexemeTypeTests(
			source,
			SDocTokenType.WHITESPACE
		);
	}
	
	/**
	 * testSpace
	 */
	public void testSpace()
	{
		String source = " ";
		
		lexemeTypeTests(
			source,
			SDocTokenType.WHITESPACE
		);
	}
	
	/**
	 * testTab
	 */
	public void testTab()
	{
		String source = "\t";
		
		lexemeTypeTests(
			source,
			SDocTokenType.WHITESPACE
		);
	}
	
	/**
	 * testNoTypes
	 */
	public void testNoTypes()
	{
		String source = "{}";
		
		lexemeTypeTests(
			source,
			SDocTokenType.LCURLY,
			SDocTokenType.RCURLY
		);
	}
	
	/**
	 * testSimpleType
	 */
	public void testSimpleType()
	{
		String source = "{Number}";
		
		lexemeTypeTests(
			source,
			SDocTokenType.LCURLY,
			SDocTokenType.IDENTIFIER,
			SDocTokenType.RCURLY
		);
	}
	
	/**
	 * testGenericArrayType
	 */
	public void testGenericArrayType()
	{
		String source = "{Array<String>}";
		
		lexemeTypeTests(
			source,
			SDocTokenType.LCURLY,
			SDocTokenType.ARRAY,
			SDocTokenType.LESS_THAN,
			SDocTokenType.IDENTIFIER,
			SDocTokenType.GREATER_THAN,
			SDocTokenType.RCURLY
		);
	}
	
	/**
	 * testFuntionType
	 */
	public void testFuntionType()
	{
		String source = "{Function(String)->Boolean}";
		
		lexemeTypeTests(
			source,
			SDocTokenType.LCURLY,
			SDocTokenType.FUNCTION,
			SDocTokenType.LPAREN,
			SDocTokenType.IDENTIFIER,
			SDocTokenType.RPAREN,
			SDocTokenType.ARROW,
			SDocTokenType.IDENTIFIER,
			SDocTokenType.RCURLY
		);
	}
	
	/**
	 * testFuntionType2
	 */
	public void testFuntionType2()
	{
		String source = "{Function(String):Boolean}";
		
		lexemeTypeTests(
			source,
			SDocTokenType.LCURLY,
			SDocTokenType.FUNCTION,
			SDocTokenType.LPAREN,
			SDocTokenType.IDENTIFIER,
			SDocTokenType.RPAREN,
			SDocTokenType.COLON,
			SDocTokenType.IDENTIFIER,
			SDocTokenType.RCURLY
		);
	}
}
