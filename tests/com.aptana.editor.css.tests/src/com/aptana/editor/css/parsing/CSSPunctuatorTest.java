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

import com.aptana.editor.css.parsing.lexer.CSSTokens;

public class CSSPunctuatorTest extends CSSTokensTest
{

	public void testColon()
	{
		assertToken(":", CSSTokens.getTokenName(CSSTokens.COLON), 0, 1); //$NON-NLS-1$
	}

	public void testSemicolon()
	{
		assertToken(";", CSSTokens.getTokenName(CSSTokens.SEMICOLON), 0, 1); //$NON-NLS-1$
	}

	public void testLCurly()
	{
		assertToken("{", CSSTokens.getTokenName(CSSTokens.LCURLY), 0, 1); //$NON-NLS-1$
	}

	public void testRCurly()
	{
		assertToken("}", CSSTokens.getTokenName(CSSTokens.RCURLY), 0, 1); //$NON-NLS-1$
	}

	public void testRParen()
	{
		assertToken(")", CSSTokens.getTokenName(CSSTokens.RPAREN), 0, 1); //$NON-NLS-1$
	}

	public void testLBracket()
	{
		assertToken("[", CSSTokens.getTokenName(CSSTokens.LBRACKET), 0, 1); //$NON-NLS-1$
	}

	public void testRBracket()
	{
		assertToken("]", CSSTokens.getTokenName(CSSTokens.RBRACKET), 0, 1); //$NON-NLS-1$
	}

	public void testIncludes()
	{
		assertToken("~=", CSSTokens.getTokenName(CSSTokens.INCLUDES), 0, 2); //$NON-NLS-1$
	}

	public void testDashMatch()
	{
		assertToken("|=", CSSTokens.getTokenName(CSSTokens.DASHMATCH), 0, 2); //$NON-NLS-1$
	}

	public void testComma()
	{
		assertToken(",", CSSTokens.getTokenName(CSSTokens.COMMA), 0, 1); //$NON-NLS-1$
	}

	public void testPlus()
	{
		assertToken("+", CSSTokens.getTokenName(CSSTokens.PLUS), 0, 1); //$NON-NLS-1$
	}

	public void testMinus()
	{
		setSource("10 - 5");
		assertToken(CSSTokens.getTokenName(CSSTokens.NUMBER), 0, 2); //$NON-NLS-1$
		assertToken(Token.WHITESPACE.getData(), 2, 1); //$NON-NLS-1$
		assertToken(CSSTokens.getTokenName(CSSTokens.MINUS), 3, 1); //$NON-NLS-1$
		assertToken(Token.WHITESPACE.getData(), 4, 1); //$NON-NLS-1$
		assertToken(CSSTokens.getTokenName(CSSTokens.NUMBER), 5, 1); //$NON-NLS-1$
	}

	public void testStar()
	{
		assertToken("*", CSSTokens.getTokenName(CSSTokens.STAR), 0, 1); //$NON-NLS-1$
	}

	public void testGreater()
	{
		assertToken(">", CSSTokens.getTokenName(CSSTokens.GREATER), 0, 1); //$NON-NLS-1$
	}

	public void testForwardSlash()
	{
		assertToken("/", CSSTokens.getTokenName(CSSTokens.FORWARD_SLASH), 0, 1); //$NON-NLS-1$
	}

	public void testEqual()
	{
		assertToken("=", CSSTokens.getTokenName(CSSTokens.EQUAL), 0, 1); //$NON-NLS-1$
	}
}
