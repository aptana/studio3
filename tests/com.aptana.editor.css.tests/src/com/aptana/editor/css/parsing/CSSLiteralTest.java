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

import com.aptana.editor.css.parsing.lexer.CSSTokenType;

public class CSSLiteralTest extends CSSTokensTest
{

	public void testDoubleQuotedString()
	{
		assertToken("\"this is a string\"", CSSTokenType.DOUBLE_QUOTED_STRING, 0, 18); //$NON-NLS-1$
	}

	public void testSingleQuotedString()
	{
		assertToken("'this is a string'", CSSTokenType.SINGLE_QUOTED_STRING, 0, 18); //$NON-NLS-1$
	}

	public void testNumber()
	{
		assertToken("10", CSSTokenType.NUMBER, 0, 2); //$NON-NLS-1$
	}

	public void testClass()
	{
		assertToken(".class", CSSTokenType.CLASS, 0, 6); //$NON-NLS-1$
	}

	public void testClassWithDashes()
	{
		assertToken(".class-with-dashes", CSSTokenType.CLASS, 0, 18); //$NON-NLS-1$
	}

	public void testHash()
	{
		assertToken("#hash", CSSTokenType.ID, 0, 5); //$NON-NLS-1$
	}

	public void testHashLikeRGB1()
	{
		assertToken("#a", CSSTokenType.ID, 0, 2); //$NON-NLS-1$
	}

	public void testHashLikeRGB2()
	{
		assertToken("#ab", CSSTokenType.ID, 0, 3); //$NON-NLS-1$
	}

	public void testHashLikeRGB3()
	{
		assertToken("#abcde", CSSTokenType.ID, 0, 6); //$NON-NLS-1$
	}

	public void testHashLikeRGB4()
	{
		assertToken("#abcdefa", CSSTokenType.ID, 0, 8); //$NON-NLS-1$
	}

	public void testHashLikeRGB5()
	{
		assertToken("#abcx", CSSTokenType.ID, 0, 5); //$NON-NLS-1$
	}

	public void testFunction()
	{
		setSource("function(");
		assertToken(CSSTokenType.IDENTIFIER, 0, 8); //$NON-NLS-1$
		assertToken(CSSTokenType.LPAREN, 8, 1); //$NON-NLS-1$
	}

	public void testPercentage()
	{
		assertToken("10%", CSSTokenType.PERCENTAGE, 0, 3); //$NON-NLS-1$
	}

	public void testEms()
	{
		assertToken("10em", CSSTokenType.EMS, 0, 4); //$NON-NLS-1$
	}

	public void testExs()
	{
		assertToken("10ex", CSSTokenType.EXS, 0, 4); //$NON-NLS-1$
	}

	public void testPixels()
	{
		assertToken("10px", CSSTokenType.LENGTH, 0, 4); //$NON-NLS-1$
	}

	public void testCentimeters()
	{
		assertToken("10cm", CSSTokenType.LENGTH, 0, 4); //$NON-NLS-1$
	}

	public void testMillimeters()
	{
		assertToken("10mm", CSSTokenType.LENGTH, 0, 4); //$NON-NLS-1$
	}

	public void testInches()
	{
		assertToken("10in", CSSTokenType.LENGTH, 0, 4); //$NON-NLS-1$
	}

	public void testPoints()
	{
		assertToken("10pt", CSSTokenType.LENGTH, 0, 4); //$NON-NLS-1$
	}

	public void testPicas()
	{
		assertToken("10pc", CSSTokenType.LENGTH, 0, 4); //$NON-NLS-1$
	}

	public void testDegrees()
	{
		assertToken("10deg", CSSTokenType.ANGLE, 0, 5); //$NON-NLS-1$
	}

	public void testRads()
	{
		assertToken("10rad", CSSTokenType.ANGLE, 0, 5); //$NON-NLS-1$
	}

	public void testGrads()
	{
		assertToken("10grad", CSSTokenType.ANGLE, 0, 6); //$NON-NLS-1$
	}

	public void testMilliseconds()
	{
		assertToken("10ms", CSSTokenType.TIME, 0, 4); //$NON-NLS-1$
	}

	public void testSeconds()
	{
		assertToken("10s", CSSTokenType.TIME, 0, 3); //$NON-NLS-1$
	}

	public void testHertz()
	{
		assertToken("10hz", CSSTokenType.FREQUENCY, 0, 4); //$NON-NLS-1$
	}

	public void testKiloHertz()
	{
		assertToken("10khz", CSSTokenType.FREQUENCY, 0, 5); //$NON-NLS-1$
	}

	public void testColor()
	{
		assertToken("#808080", CSSTokenType.RGB, 0, 7); //$NON-NLS-1$
	}

	public void testColorRGB()
	{
		assertToken("#abc", CSSTokenType.RGB, 0, 4); //$NON-NLS-1$
	}
}
