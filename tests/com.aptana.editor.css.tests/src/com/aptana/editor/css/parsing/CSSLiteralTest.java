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

import com.aptana.editor.css.parsing.lexer.CSSTokens;

public class CSSLiteralTest extends CSSTokensTest
{

	public void testDoubleQuotedString()
	{
		assertToken("\"this is a string\"", CSSTokens.getTokenName(CSSTokens.STRING), 0, 18); //$NON-NLS-1$
	}

	public void testSingleQuotedString()
	{
		assertToken("'this is a string'", CSSTokens.getTokenName(CSSTokens.STRING), 0, 18); //$NON-NLS-1$
	}

	public void testNumber()
	{
		assertToken("10", CSSTokens.getTokenName(CSSTokens.NUMBER), 0, 2); //$NON-NLS-1$
	}

	public void testClass()
	{
		assertToken(".class", CSSTokens.getTokenName(CSSTokens.CLASS), 0, 6); //$NON-NLS-1$
	}

	public void testClassWithDashes()
	{
		assertToken(".class-with-dashes", CSSTokens.getTokenName(CSSTokens.CLASS), 0, 18); //$NON-NLS-1$
	}

	public void testHash()
	{
		assertToken("#hash", CSSTokens.getTokenName(CSSTokens.HASH), 0, 5); //$NON-NLS-1$
	}

	public void testHashLikeRGB1()
	{
		assertToken("#a", CSSTokens.getTokenName(CSSTokens.HASH), 0, 2); //$NON-NLS-1$
	}

	public void testHashLikeRGB2()
	{
		assertToken("#ab", CSSTokens.getTokenName(CSSTokens.HASH), 0, 3); //$NON-NLS-1$
	}

	public void testHashLikeRGB3()
	{
		assertToken("#abcde", CSSTokens.getTokenName(CSSTokens.HASH), 0, 6); //$NON-NLS-1$
	}

	public void testHashLikeRGB4()
	{
		assertToken("#abcdefa", CSSTokens.getTokenName(CSSTokens.HASH), 0, 8); //$NON-NLS-1$
	}

	public void testHashLikeRGB5()
	{
		assertToken("#abcx", CSSTokens.getTokenName(CSSTokens.HASH), 0, 5); //$NON-NLS-1$
	}

	public void testFunction()
	{
		setSource("function(");
		assertToken(CSSTokens.getTokenName(CSSTokens.IDENTIFIER), 0, 8); //$NON-NLS-1$
		assertToken(CSSTokens.getTokenName(CSSTokens.FUNCTION), 8, 1); //$NON-NLS-1$
	}

	public void testPercentage()
	{
		assertToken("10%", CSSTokens.getTokenName(CSSTokens.PERCENTAGE), 0, 3); //$NON-NLS-1$
	}

	public void testEms()
	{
		assertToken("10em", CSSTokens.getTokenName(CSSTokens.EMS), 0, 4); //$NON-NLS-1$
	}

	public void testExs()
	{
		assertToken("10ex", CSSTokens.getTokenName(CSSTokens.EXS), 0, 4); //$NON-NLS-1$
	}

	public void testPixels()
	{
		assertToken("10px", CSSTokens.getTokenName(CSSTokens.LENGTH), 0, 4); //$NON-NLS-1$
	}

	public void testCentimeters()
	{
		assertToken("10cm", CSSTokens.getTokenName(CSSTokens.LENGTH), 0, 4); //$NON-NLS-1$
	}

	public void testMillimeters()
	{
		assertToken("10mm", CSSTokens.getTokenName(CSSTokens.LENGTH), 0, 4); //$NON-NLS-1$
	}

	public void testInches()
	{
		assertToken("10in", CSSTokens.getTokenName(CSSTokens.LENGTH), 0, 4); //$NON-NLS-1$
	}

	public void testPoints()
	{
		assertToken("10pt", CSSTokens.getTokenName(CSSTokens.LENGTH), 0, 4); //$NON-NLS-1$
	}

	public void testPicas()
	{
		assertToken("10pc", CSSTokens.getTokenName(CSSTokens.LENGTH), 0, 4); //$NON-NLS-1$
	}

	public void testDegrees()
	{
		assertToken("10deg", CSSTokens.getTokenName(CSSTokens.ANGLE), 0, 5); //$NON-NLS-1$
	}

	public void testRads()
	{
		assertToken("10rad", CSSTokens.getTokenName(CSSTokens.ANGLE), 0, 5); //$NON-NLS-1$
	}

	public void testGrads()
	{
		assertToken("10grad", CSSTokens.getTokenName(CSSTokens.ANGLE), 0, 6); //$NON-NLS-1$
	}

	public void testMilliseconds()
	{
		assertToken("10ms", CSSTokens.getTokenName(CSSTokens.TIME), 0, 4); //$NON-NLS-1$
	}

	public void testSeconds()
	{
		assertToken("10s", CSSTokens.getTokenName(CSSTokens.TIME), 0, 3); //$NON-NLS-1$
	}

	public void testHertz()
	{
		assertToken("10hz", CSSTokens.getTokenName(CSSTokens.FREQUENCY), 0, 4); //$NON-NLS-1$
	}

	public void testKiloHertz()
	{
		assertToken("10khz", CSSTokens.getTokenName(CSSTokens.FREQUENCY), 0, 5); //$NON-NLS-1$
	}

	public void testColor()
	{
		assertToken("#808080", CSSTokens.getTokenName(CSSTokens.COLOR), 0, 7); //$NON-NLS-1$
	}

	public void testColorRGB()
	{
		assertToken("#abc", CSSTokens.getTokenName(CSSTokens.COLOR), 0, 4); //$NON-NLS-1$
	}
}
