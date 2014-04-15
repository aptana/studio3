/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.js.internal.core.parsing.sdoc;

import org.junit.After;
import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;
import java.io.IOException;

import junit.framework.TestCase;
import beaver.Symbol;

import com.aptana.js.internal.core.parsing.sdoc.SDocFlexScanner;
import com.aptana.js.internal.core.parsing.sdoc.SDocTokenType;

public class SDocTypeTokenScannerTest
{
	private SDocFlexScanner _scanner;

	/*
	 * (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
//	@Override
	@Before
	public void setUp() throws Exception
	{
//		super.setUp();

		_scanner = new SDocFlexScanner();
	}

	/*
	 * (non-Javadoc)
	 * @see junit.framework.TestCase#tearDown()
	 */
//	@Override
	@After
	public void tearDown() throws Exception
	{
		_scanner = null;

//		super.tearDown();
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
	@Test
	public void testLeftParen()
	{
		String source = "(";

		lexemeTypeTests(source, SDocTokenType.LPAREN);
	}

	/**
	 * testRightParen
	 */
	@Test
	public void testRightParen()
	{
		String source = ")";

		lexemeTypeTests(source, SDocTokenType.RPAREN);
	}

	/**
	 * testLeftCurly
	 */
	@Test
	public void testLeftCurly()
	{
		String source = "{";

		lexemeTypeTests(source, SDocTokenType.LCURLY);
	}

	/**
	 * testRightCurly
	 */
	@Test
	public void testRightCurly()
	{
		String source = "}";

		lexemeTypeTests(source, SDocTokenType.RCURLY);
	}

	/**
	 * testLeftBracket
	 */
	@Test
	public void testLeftBracket()
	{
		String source = "[";

		lexemeTypeTests(source, SDocTokenType.LBRACKET);
	}

	/**
	 * testRightBracket
	 */
	@Test
	public void testRightBracket()
	{
		String source = "]";

		lexemeTypeTests(source, SDocTokenType.RBRACKET);
	}

	/**
	 * testLessThan
	 */
	@Test
	public void testLessThan()
	{
		String source = "<";

		lexemeTypeTests(source, SDocTokenType.LESS_THAN);
	}

	/**
	 * testGreaterThan
	 */
	@Test
	public void testGreaterThan()
	{
		String source = ">";

		lexemeTypeTests(source, SDocTokenType.GREATER_THAN);
	}

	/**
	 * testColon
	 */
	@Test
	public void testColon()
	{
		String source = ":";

		lexemeTypeTests(source, SDocTokenType.COLON);
	}

	/**
	 * testComma
	 */
	@Test
	public void testComma()
	{
		String source = ",";

		lexemeTypeTests(source, SDocTokenType.COMMA);
	}

	/**
	 * testPipe
	 */
	@Test
	public void testPipe()
	{
		String source = "|";

		lexemeTypeTests(source, SDocTokenType.PIPE);
	}

	/**
	 * testArrow
	 */
	@Test
	public void testArrow()
	{
		String source = "->";

		lexemeTypeTests(source, SDocTokenType.ARROW);
	}

	/**
	 * testEllipsis
	 */
	@Test
	public void testEllipsis()
	{
		String source = "...";

		lexemeTypeTests(source, SDocTokenType.ELLIPSIS);
	}

	/**
	 * testFunction
	 */
	@Test
	public void testFunction()
	{
		String source = "Function";

		lexemeTypeTests(source, SDocTokenType.FUNCTION);
	}

	/**
	 * testArray
	 */
	@Test
	public void testArray()
	{
		String source = "Array";

		lexemeTypeTests(source, SDocTokenType.ARRAY);
	}

	/**
	 * testSimpleIdentifiers
	 */
	@Test
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
	@Test
	public void testDottedIdentifier()
	{
		String source = "Namespace.ClassName";

		lexemeTypeTests(source, SDocTokenType.IDENTIFIER);
	}

	/**
	 * testNearIdentifiers
	 */
	@Test
	public void testNearIdentifiers()
	{
		String source = "Functions Arrays";

		lexemeTypeTests(source, SDocTokenType.IDENTIFIER, SDocTokenType.IDENTIFIER);
	}

	/**
	 * testCR
	 */
	@Test
	public void testCR()
	{
		String source = "\r";

		lexemeTypeTests(source, SDocTokenType.EOF);
	}

	/**
	 * testLF
	 */
	@Test
	public void testLF()
	{
		String source = "\n";

		lexemeTypeTests(source, SDocTokenType.EOF);
	}

	/**
	 * testCRLF
	 */
	@Test
	public void testCRLF()
	{
		String source = "\r\n";

		lexemeTypeTests(source, SDocTokenType.EOF);
	}

	/**
	 * testSpace
	 */
	@Test
	public void testSpace()
	{
		String source = " ";

		lexemeTypeTests(source, SDocTokenType.EOF);
	}

	/**
	 * testTab
	 */
	@Test
	public void testTab()
	{
		String source = "\t";

		lexemeTypeTests(source, SDocTokenType.EOF);
	}

	/**
	 * testNoTypes
	 */
	@Test
	public void testNoTypes()
	{
		String source = "{}";

		lexemeTypeTests(source, SDocTokenType.LCURLY, SDocTokenType.RCURLY);
	}

	/**
	 * testSimpleType
	 */
	@Test
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
	@Test
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
	@Test
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
	@Test
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
