/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.css.parsing;

import org.eclipse.jface.text.rules.Token;

import com.aptana.editor.css.parsing.lexer.CSSTokenType;

public class CSSPunctuatorTest extends CSSTokensTest
{

	public void testColon()
	{
		assertToken(":", CSSTokenType.COLON, 0, 1); //$NON-NLS-1$
	}

	public void testSemicolon()
	{
		assertToken(";", CSSTokenType.SEMICOLON, 0, 1); //$NON-NLS-1$
	}

	public void testLCurly()
	{
		assertToken("{", CSSTokenType.LCURLY, 0, 1); //$NON-NLS-1$
	}

	public void testRCurly()
	{
		assertToken("}", CSSTokenType.RCURLY, 0, 1); //$NON-NLS-1$
	}

	public void testRParen()
	{
		assertToken(")", CSSTokenType.RPAREN, 0, 1); //$NON-NLS-1$
	}

	public void testLBracket()
	{
		assertToken("[", CSSTokenType.LBRACKET, 0, 1); //$NON-NLS-1$
	}

	public void testRBracket()
	{
		assertToken("]", CSSTokenType.RBRACKET, 0, 1); //$NON-NLS-1$
	}

	public void testIncludes()
	{
		assertToken("~=", CSSTokenType.INCLUDES, 0, 2); //$NON-NLS-1$
	}

	public void testDashMatch()
	{
		assertToken("|=", CSSTokenType.DASHMATCH, 0, 2); //$NON-NLS-1$
	}

	public void testComma()
	{
		assertToken(",", CSSTokenType.COMMA, 0, 1); //$NON-NLS-1$
	}

	public void testPlus()
	{
		assertToken("+", CSSTokenType.PLUS, 0, 1); //$NON-NLS-1$
	}

	public void testMinus()
	{
		setSource("10 - 5");
		assertToken(CSSTokenType.NUMBER, 0, 2); //$NON-NLS-1$
		assertToken(Token.WHITESPACE.getData(), 2, 1); //$NON-NLS-1$
		assertToken(CSSTokenType.MINUS, 3, 1); //$NON-NLS-1$
		assertToken(Token.WHITESPACE.getData(), 4, 1); //$NON-NLS-1$
		assertToken(CSSTokenType.NUMBER, 5, 1); //$NON-NLS-1$
	}

	public void testStar()
	{
		assertToken("*", CSSTokenType.STAR, 0, 1); //$NON-NLS-1$
	}

	public void testGreater()
	{
		assertToken(">", CSSTokenType.GREATER, 0, 1); //$NON-NLS-1$
	}

	public void testForwardSlash()
	{
		assertToken("/", CSSTokenType.SLASH, 0, 1); //$NON-NLS-1$
	}

	public void testEqual()
	{
		assertToken("=", CSSTokenType.EQUAL, 0, 1); //$NON-NLS-1$
	}
}
