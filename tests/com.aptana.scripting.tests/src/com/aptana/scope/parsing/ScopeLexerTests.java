/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.scope.parsing;

import org.junit.After;
import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;
import junit.framework.TestCase;
import beaver.Symbol;

/**
 * ScopeLexerTests
 */
public class ScopeLexerTests
{
	private ScopeFlexScanner scanner;

	/*
	 * (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
//	@Override
	@Before
	public void setUp() throws Exception
	{
//		super.setUp();

		scanner = new ScopeFlexScanner();
	}

	/*
	 * (non-Javadoc)
	 * @see junit.framework.TestCase#tearDown()
	 */
//	@Override
	@After
	public void tearDown() throws Exception
	{
		scanner = null;

//		super.tearDown();
	}

	protected void assertTokenType(String source, ScopeTokenType type)
	{
		scanner.setSource(source);

		try
		{
			Symbol symbol = scanner.nextToken();

			assertNotNull("Expected a non-null symbol", symbol);

			int start = symbol.getStart();
			int length = symbol.getEnd() - symbol.getStart() + 1;

			assertEquals("Unexpected token type", type.getIndex(), symbol.getId());
			assertEquals("Unexpected token to start", 0, start);
			assertEquals("Unexpected token length", source.length(), length);
		}
		catch (Exception e)
		{
			fail(e.getMessage());
		}
	}

	@Test
	public void testSimpleIdentifier()
	{
		assertTokenType("name", ScopeTokenType.IDENTIFIER);
	}

	@Test
	public void testIdentifierWithDashes()
	{
		assertTokenType("name-with-dashes", ScopeTokenType.IDENTIFIER);
	}

	@Test
	public void testIdentifierWithUnderscores()
	{
		assertTokenType("name_with_underscores", ScopeTokenType.IDENTIFIER);
	}

	@Test
	public void testIdentifierWithNumbers()
	{
		assertTokenType("name0", ScopeTokenType.IDENTIFIER);
		assertTokenType("name1", ScopeTokenType.IDENTIFIER);
		assertTokenType("name2", ScopeTokenType.IDENTIFIER);
		assertTokenType("name3", ScopeTokenType.IDENTIFIER);
		assertTokenType("name4", ScopeTokenType.IDENTIFIER);
		assertTokenType("name5", ScopeTokenType.IDENTIFIER);
		assertTokenType("name6", ScopeTokenType.IDENTIFIER);
		assertTokenType("name7", ScopeTokenType.IDENTIFIER);
		assertTokenType("name8", ScopeTokenType.IDENTIFIER);
		assertTokenType("name9", ScopeTokenType.IDENTIFIER);
		assertTokenType("name10", ScopeTokenType.IDENTIFIER);
	}

	@Test
	public void testDottedIdentifier()
	{
		assertTokenType("name.another", ScopeTokenType.IDENTIFIER);
	}

	@Test
	public void testDottedIdentifier2()
	{
		assertTokenType("name.another.yet-another", ScopeTokenType.IDENTIFIER);
	}

	@Test
	public void testComma()
	{
		assertTokenType(",", ScopeTokenType.COMMA);
	}

	@Test
	public void testAmpersand()
	{
		assertTokenType("&", ScopeTokenType.AMPERSAND);
	}

	@Test
	public void testPipe()
	{
		assertTokenType("|", ScopeTokenType.PIPE);
	}

	@Test
	public void testLeftParen()
	{
		assertTokenType("(", ScopeTokenType.LPAREN);
	}

	@Test
	public void testRightParen()
	{
		assertTokenType(")", ScopeTokenType.RPAREN);
	}

	@Test
	public void testMinus()
	{
		assertTokenType("-", ScopeTokenType.MINUS);
	}
}
