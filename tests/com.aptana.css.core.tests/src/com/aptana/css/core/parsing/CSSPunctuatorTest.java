/**
 * Aptana Studio
 * Copyright (c) 2005-2013 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.css.core.parsing;

import org.junit.Test;

public class CSSPunctuatorTest extends CSSTokensTest
{

	@Test
	public void testColon()
	{
		assertToken(":", CSSTokenType.COLON, 0, 1); //$NON-NLS-1$
	}

	@Test
	public void testSemicolon()
	{
		assertToken(";", CSSTokenType.SEMICOLON, 0, 1); //$NON-NLS-1$
	}

	@Test
	public void testLCurly()
	{
		assertToken("{", CSSTokenType.LCURLY, 0, 1); //$NON-NLS-1$
	}

	@Test
	public void testRCurly()
	{
		assertToken("}", CSSTokenType.RCURLY, 0, 1); //$NON-NLS-1$
	}

	@Test
	public void testRParen()
	{
		assertToken(")", CSSTokenType.RPAREN, 0, 1); //$NON-NLS-1$
	}

	@Test
	public void testLBracket()
	{
		assertToken("[", CSSTokenType.LBRACKET, 0, 1); //$NON-NLS-1$
	}

	@Test
	public void testRBracket()
	{
		assertToken("]", CSSTokenType.RBRACKET, 0, 1); //$NON-NLS-1$
	}

	@Test
	public void testIncludes()
	{
		assertToken("~=", CSSTokenType.INCLUDES, 0, 2); //$NON-NLS-1$
	}

	@Test
	public void testDashMatch()
	{
		assertToken("|=", CSSTokenType.DASHMATCH, 0, 2); //$NON-NLS-1$
	}

	@Test
	public void testComma()
	{
		assertToken(",", CSSTokenType.COMMA, 0, 1); //$NON-NLS-1$
	}

	@Test
	public void testPlus()
	{
		assertToken("+", CSSTokenType.PLUS, 0, 1); //$NON-NLS-1$
	}

	@Test
	public void testMinus()
	{
		String source = "10 - 5";

		// @formatter:off
		assertToken(
			source,
			new TokenInfo(CSSTokenType.NUMBER, 0, 2),
			new TokenInfo(CSSTokenType.MINUS, 3, 1),
			new TokenInfo(CSSTokenType.NUMBER, 5, 1)
		);
		// @formatter:on
	}

	@Test
	public void testStar()
	{
		assertToken("*", CSSTokenType.STAR, 0, 1); //$NON-NLS-1$
	}

	@Test
	public void testGreater()
	{
		assertToken(">", CSSTokenType.GREATER, 0, 1); //$NON-NLS-1$
	}

	@Test
	public void testForwardSlash()
	{
		assertToken("/", CSSTokenType.SLASH, 0, 1); //$NON-NLS-1$
	}

	@Test
	public void testEqual()
	{
		assertToken("=", CSSTokenType.EQUAL, 0, 1); //$NON-NLS-1$
	}

	@Test
	public void testBeginsWith()
	{
		assertToken("^=", CSSTokenType.BEGINS_WITH, 0, 2); //$NON-NLS-1$
	}

	@Test
	public void testEndsWith()
	{
		assertToken("$=", CSSTokenType.ENDS_WITH, 0, 2); //$NON-NLS-1$
	}
}
