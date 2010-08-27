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
