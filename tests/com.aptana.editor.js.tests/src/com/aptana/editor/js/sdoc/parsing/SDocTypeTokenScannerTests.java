/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.js.sdoc.parsing;

import java.io.IOException;

import junit.framework.TestCase;
import beaver.Symbol;

import com.aptana.editor.js.sdoc.lexer.SDocTokenType;

public class SDocTypeTokenScannerTests extends TestCase
{
	private SDocFlexScanner _scanner;

	/*
	 * (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	@Override
	protected void setUp() throws Exception
	{
		super.setUp();

		_scanner = new SDocFlexScanner();
	}

	/*
	 * (non-Javadoc)
	 * @see junit.framework.TestCase#tearDown()
	 */
	@Override
	protected void tearDown() throws Exception
	{
		_scanner = null;

		super.tearDown();
	}

	/**
	 * lexemeTypeTests
	 * 
	 * @param source
	 * @param types
	 * @throws beaver.Scanner.Exception
	 * @throws IOException
	 */
	protected void lexemeTypeTests(String source, SDocTokenType... types)
	{
		_scanner.setSource(source);
		_scanner.yybegin(SDocFlexScanner.TYPES);

		for (int i = 0; i < types.length; i++)
		{
			try
			{
				SDocTokenType targetType = types[i];
				Symbol token = _scanner.nextToken();
				SDocTokenType actualType = SDocTokenType.get(token.getId());

				assertEquals("at index " + i, targetType, actualType);
			}
			catch (Throwable t)
			{
				fail(t.getMessage());
			}
		}
	}

	/**
	 * testLeftParen
	 */
	public void testLeftParen()
	{
		String source = "(";

		lexemeTypeTests(source, SDocTokenType.LPAREN);
	}

	/**
	 * testRightParen
	 */
	public void testRightParen()
	{
		String source = ")";

		lexemeTypeTests(source, SDocTokenType.RPAREN);
	}

	/**
	 * testLeftCurly
	 */
	public void testLeftCurly()
	{
		String source = "{";

		lexemeTypeTests(source, SDocTokenType.LCURLY);
	}

	/**
	 * testRightCurly
	 */
	public void testRightCurly()
	{
		String source = "}";

		lexemeTypeTests(source, SDocTokenType.RCURLY);
	}

	/**
	 * testLeftBracket
	 */
	public void testLeftBracket()
	{
		String source = "[";

		lexemeTypeTests(source, SDocTokenType.LBRACKET);
	}

	/**
	 * testRightBracket
	 */
	public void testRightBracket()
	{
		String source = "]";

		lexemeTypeTests(source, SDocTokenType.RBRACKET);
	}

	/**
	 * testLessThan
	 */
	public void testLessThan()
	{
		String source = "<";

		lexemeTypeTests(source, SDocTokenType.LESS_THAN);
	}

	/**
	 * testGreaterThan
	 */
	public void testGreaterThan()
	{
		String source = ">";

		lexemeTypeTests(source, SDocTokenType.GREATER_THAN);
	}

	/**
	 * testColon
	 */
	public void testColon()
	{
		String source = ":";

		lexemeTypeTests(source, SDocTokenType.COLON);
	}

	/**
	 * testComma
	 */
	public void testComma()
	{
		String source = ",";

		lexemeTypeTests(source, SDocTokenType.COMMA);
	}

	/**
	 * testPipe
	 */
	public void testPipe()
	{
		String source = "|";

		lexemeTypeTests(source, SDocTokenType.PIPE);
	}

	/**
	 * testArrow
	 */
	public void testArrow()
	{
		String source = "->";

		lexemeTypeTests(source, SDocTokenType.ARROW);
	}

	/**
	 * testEllipsis
	 */
	public void testEllipsis()
	{
		String source = "...";

		lexemeTypeTests(source, SDocTokenType.ELLIPSIS);
	}

	/**
	 * testFunction
	 */
	public void testFunction()
	{
		String source = "Function";

		lexemeTypeTests(source, SDocTokenType.FUNCTION);
	}

	/**
	 * testArray
	 */
	public void testArray()
	{
		String source = "Array";

		lexemeTypeTests(source, SDocTokenType.ARRAY);
	}

	/**
	 * testSimpleIdentifiers
	 */
	public void testSimpleIdentifiers()
	{
		String source = "Number $number _number";

		// @formatter:off
		lexemeTypeTests(
			source,
			SDocTokenType.IDENTIFIER,
			SDocTokenType.IDENTIFIER,
			SDocTokenType.IDENTIFIER
		);
		// @formatter:on
	}

	/**
	 * testDottedIdentifier
	 */
	public void testDottedIdentifier()
	{
		String source = "Namespace.ClassName";

		lexemeTypeTests(source, SDocTokenType.IDENTIFIER);
	}

	/**
	 * testNearIdentifiers
	 */
	public void testNearIdentifiers()
	{
		String source = "Functions Arrays";

		lexemeTypeTests(source, SDocTokenType.IDENTIFIER, SDocTokenType.IDENTIFIER);
	}

	/**
	 * testCR
	 */
	public void testCR()
	{
		String source = "\r";

		lexemeTypeTests(source, SDocTokenType.EOF);
	}

	/**
	 * testLF
	 */
	public void testLF()
	{
		String source = "\n";

		lexemeTypeTests(source, SDocTokenType.EOF);
	}

	/**
	 * testCRLF
	 */
	public void testCRLF()
	{
		String source = "\r\n";

		lexemeTypeTests(source, SDocTokenType.EOF);
	}

	/**
	 * testSpace
	 */
	public void testSpace()
	{
		String source = " ";

		lexemeTypeTests(source, SDocTokenType.EOF);
	}

	/**
	 * testTab
	 */
	public void testTab()
	{
		String source = "\t";

		lexemeTypeTests(source, SDocTokenType.EOF);
	}

	/**
	 * testNoTypes
	 */
	public void testNoTypes()
	{
		String source = "{}";

		lexemeTypeTests(source, SDocTokenType.LCURLY, SDocTokenType.RCURLY);
	}

	/**
	 * testSimpleType
	 */
	public void testSimpleType()
	{
		String source = "{Number}";

		// formatter:off
		lexemeTypeTests(source, SDocTokenType.LCURLY, SDocTokenType.IDENTIFIER, SDocTokenType.RCURLY);
		// formatter:on
	}

	/**
	 * testGenericArrayType
	 */
	public void testGenericArrayType()
	{
		String source = "{Array<String>}";

		// @formatter:off
		lexemeTypeTests(
			source,
			SDocTokenType.LCURLY,
			SDocTokenType.ARRAY,
			SDocTokenType.LESS_THAN,
			SDocTokenType.IDENTIFIER,
			SDocTokenType.GREATER_THAN, 
			SDocTokenType.RCURLY
		);
		// @formatter:on
	}

	/**
	 * testFuntionType
	 */
	public void testFuntionType()
	{
		String source = "{Function(String)->Boolean}";

		// @formatter:off
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
		// @formatter:on
	}

	/**
	 * testFuntionType2
	 */
	public void testFuntionType2()
	{
		String source = "{Function(String):Boolean}";

		// @formatter:off
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
		// @formatter:on
	}
}
