/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.css.parsing;

import java.util.List;

import com.aptana.core.util.CollectionsUtil;
import com.aptana.core.util.StringUtil;
import com.aptana.editor.common.tests.util.ListCrossProduct;
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
		ListCrossProduct<String> crossProduct = new ListCrossProduct<String>();

		crossProduct.addList(CollectionsUtil.newList("", "-", "+"));
		crossProduct.addList(CollectionsUtil.newList("0", "1", "2", "3", "4", "5", "6", "7", "8", "9"));
		crossProduct.addList(CollectionsUtil.newList("."));
		crossProduct.addList(CollectionsUtil.newList("0", "1", "2", "3", "4", "5", "6", "7", "8", "9"));

		for (List<String> list : crossProduct)
		{
			String text = StringUtil.concat(list);

			assertToken(text, CSSTokenType.NUMBER, 0, text.length());
		}
	}

	public void testNumber2()
	{
		ListCrossProduct<String> crossProduct = new ListCrossProduct<String>();

		crossProduct.addList(CollectionsUtil.newList("{"));
		crossProduct.addList(CollectionsUtil.newList("", "-", "+"));
		crossProduct.addList(CollectionsUtil.newList("", "0", "1", "2", "3", "4", "5", "6", "7", "8", "9"));
		crossProduct.addList(CollectionsUtil.newList("."));
		crossProduct.addList(CollectionsUtil.newList("0", "1", "2", "3", "4", "5", "6", "7", "8", "9"));
		crossProduct.addList(CollectionsUtil.newList("}"));

		for (List<String> list : crossProduct)
		{
			String text = StringUtil.concat(list);

			// @formatter:off
			assertToken(
				text,
				new TokenInfo(CSSTokenType.LCURLY, 0, 1),
				new TokenInfo(CSSTokenType.NUMBER, 1, text.length() - 2),
				new TokenInfo(CSSTokenType.RCURLY, text.length() - 1, 1)
			);
			// @formatter:on
		}
	}

	public void testRGB()
	{
		ListCrossProduct<String> crossProduct = new ListCrossProduct<String>();
		// @formatter:off
		List<String> hexValues = CollectionsUtil.newList(
			"0", "1", "2", "3", "4", "5", "6", "7", "8", "9",
			"A", "B", "C", "D", "E", "F",
			"a", "b", "c", "d", "e", "f"
		);
		// @formatter:on

		crossProduct.addList(CollectionsUtil.newList("{#"));
		crossProduct.addList(hexValues);
		crossProduct.addList(hexValues);
		crossProduct.addList(hexValues);
		crossProduct.addList(CollectionsUtil.newList("}"));

		for (List<String> list : crossProduct)
		{
			String text = StringUtil.concat(list);

			// @formatter:off
			assertToken(
				text,
				new TokenInfo(CSSTokenType.LCURLY, 0, 1),
				new TokenInfo(CSSTokenType.RGB, 1, text.length() - 2),
				new TokenInfo(CSSTokenType.RCURLY, text.length() - 1, 1)
			);
			// @formatter:on
		}
	}

	/*
	 * NOTE: only test uppercase hex values; otherwise, this unit test will take a very long time
	 */
	public void testRGB2()
	{
		ListCrossProduct<String> crossProduct = new ListCrossProduct<String>();
		// @formatter:off
		List<String> hexValues = CollectionsUtil.newList(
			"0", "1", "2", "3", "4", "5", "6", "7", "8", "9",
			"A", "B", "C", "D", "E", "F"
		);
		// @formatter:on

		crossProduct.addList(CollectionsUtil.newList("{#"));
		crossProduct.addList(hexValues);
		crossProduct.addList(hexValues);
		crossProduct.addList(hexValues);
		crossProduct.addList(hexValues);
		crossProduct.addList(hexValues);
		crossProduct.addList(hexValues);
		crossProduct.addList(CollectionsUtil.newList("}"));

		for (List<String> list : crossProduct)
		{
			String text = StringUtil.concat(list);

			// @formatter:off
			assertToken(
				text,
				new TokenInfo(CSSTokenType.LCURLY, 0, 1),
				new TokenInfo(CSSTokenType.RGB, 1, text.length() - 2),
				new TokenInfo(CSSTokenType.RCURLY, text.length() - 1, 1)
			);
			// @formatter:on
		}
	}

	public void testClass()
	{
		assertToken(".class", CSSTokenType.CLASS, 0, 6); //$NON-NLS-1$
	}
	
	public void testClass2()
	{
		String source = "{.class}";
		
		// @formatter:off
		assertToken(
			source, //$NON-NLS-1$
			new TokenInfo(CSSTokenType.LCURLY, 0, 1),
			new TokenInfo(CSSTokenType.CLASS, 1, source.length() - 2),
			new TokenInfo(CSSTokenType.RCURLY, source.length() - 1, 1)
		);
		// @formatter:on
	}

	public void testClassWithDashes()
	{
		assertToken(".class-with-dashes", CSSTokenType.CLASS, 0, 18); //$NON-NLS-1$
	}

	public void testHash()
	{
		assertToken("#hash", CSSTokenType.ID, 0, 5); //$NON-NLS-1$
	}

	public void testHash2()
	{
		String source = "{#hash}";

		// @formatter:off
		assertToken(
			source, //$NON-NLS-1$
			new TokenInfo(CSSTokenType.LCURLY, 0, 1),
			new TokenInfo(CSSTokenType.ID, 1, source.length() - 2),
			new TokenInfo(CSSTokenType.RCURLY, source.length() - 1, 1)
		);
		// @formatter:on
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
		String source = "function("; //$NON-NLS-1$

		// @formatter:off
		assertToken(
			source,
			new TokenInfo(CSSTokenType.IDENTIFIER, 0, 8),
			new TokenInfo(CSSTokenType.LPAREN, 8, 1)
		);
		// @formatter:on
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

	// NOTE: Moved color tests to CSSSpecialTokenHandlingTest now that we have special handling when we're inside and
	// outside of a rule body
}
