/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
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
