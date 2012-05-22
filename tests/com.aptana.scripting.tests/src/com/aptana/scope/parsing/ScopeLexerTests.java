/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.scope.parsing;

import junit.framework.TestCase;
import beaver.Symbol;

/**
 * ScopeLexerTests
 */
public class ScopeLexerTests extends TestCase
{
	private ScopeFlexScanner scanner;

	/*
	 * (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	@Override
	protected void setUp() throws Exception
	{
		super.setUp();

		scanner = new ScopeFlexScanner();
	}

	/*
	 * (non-Javadoc)
	 * @see junit.framework.TestCase#tearDown()
	 */
	@Override
	protected void tearDown() throws Exception
	{
		scanner = null;

		super.tearDown();
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

	public void testSimpleIdentifier()
	{
		assertTokenType("name", ScopeTokenType.IDENTIFIER);
	}

	public void testIdentifierWithDashes()
	{
		assertTokenType("name-with-dashes", ScopeTokenType.IDENTIFIER);
	}

	public void testIdentifierWithUnderscores()
	{
		assertTokenType("name_with_underscores", ScopeTokenType.IDENTIFIER);
	}

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

	public void testDottedIdentifier()
	{
		assertTokenType("name.another", ScopeTokenType.IDENTIFIER);
	}

	public void testDottedIdentifier2()
	{
		assertTokenType("name.another.yet-another", ScopeTokenType.IDENTIFIER);
	}

	public void testComma()
	{
		assertTokenType(",", ScopeTokenType.COMMA);
	}

	public void testAmpersand()
	{
		assertTokenType("&", ScopeTokenType.AMPERSAND);
	}

	public void testPipe()
	{
		assertTokenType("|", ScopeTokenType.PIPE);
	}

	public void testLeftParen()
	{
		assertTokenType("(", ScopeTokenType.LPAREN);
	}

	public void testRightParen()
	{
		assertTokenType(")", ScopeTokenType.RPAREN);
	}

	public void testMinus()
	{
		assertTokenType("-", ScopeTokenType.MINUS);
	}
}
