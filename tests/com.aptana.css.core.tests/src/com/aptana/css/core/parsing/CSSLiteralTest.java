/**
 * Aptana Studio
 * Copyright (c) 2005-2013 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.css.core.parsing;

import java.util.List;
import java.util.Random;

import com.aptana.core.util.CollectionsUtil;
import com.aptana.core.util.ListCrossProduct;
import com.aptana.core.util.StringUtil;

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
		Random r = new Random();
		final int maxHex = (int) Math.pow(16, 6); // 16777216

		// Spot check 10 random 6-character hex values.
		for (int i = 0; i < 10; i++)
		{
			int value = r.nextInt(maxHex);
			String text = "{#" + StringUtil.pad(Integer.toHexString(value), 6, '0') + "}";

			// @formatter:off
			assertToken(
				text,
				new TokenInfo(CSSTokenType.LCURLY, 0, 1),
				new TokenInfo(CSSTokenType.RGB, 1, text.length() - 2),
				new TokenInfo(CSSTokenType.RCURLY, text.length() - 1, 1)
			);
			// @formatter:on
		}

		// Commented out for now because this takes minutes to run on build machine.
		// ListCrossProduct<String> crossProduct = new ListCrossProduct<String>();
//		// @formatter:off
//		List<String> hexValues = CollectionsUtil.newList(
//			"0", "1", "2", "3", "4", "5", "6", "7", "8", "9",
//			"A", "B", "C", "D", "E", "F"
//		);
//		// @formatter:on
		//
		// crossProduct.addList(CollectionsUtil.newList("{#"));
		// crossProduct.addList(hexValues);
		// crossProduct.addList(hexValues);
		// crossProduct.addList(hexValues);
		// crossProduct.addList(hexValues);
		// crossProduct.addList(hexValues);
		// crossProduct.addList(hexValues);
		// crossProduct.addList(CollectionsUtil.newList("}"));
		//
		// for (List<String> list : crossProduct)
		// {
		// String text = StringUtil.concat(list);
		//
//			// @formatter:off
//			assertToken(
//				text,
//				new TokenInfo(CSSTokenType.LCURLY, 0, 1),
//				new TokenInfo(CSSTokenType.RGB, 1, text.length() - 2),
//				new TokenInfo(CSSTokenType.RCURLY, text.length() - 1, 1)
//			);
//			// @formatter:on
		// }
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

	private ListCrossProduct<String> getNumberCrossProduct(String prefix, String suffix)
	{
		ListCrossProduct<String> crossProduct = new ListCrossProduct<String>();

		if (!StringUtil.isEmpty(prefix))
		{
			crossProduct.addList(CollectionsUtil.newList(prefix));
		}

		crossProduct.addList(CollectionsUtil.newList("", "-", "+"));
		crossProduct.addList(CollectionsUtil.newList("", "0", "1", "2", "3", "4", "5", "6", "7", "8", "9"));
		crossProduct.addList(CollectionsUtil.newList("."));
		crossProduct.addList(CollectionsUtil.newList("0", "1", "2", "3", "4", "5", "6", "7", "8", "9"));

		if (!StringUtil.isEmpty(suffix))
		{
			crossProduct.addList(CollectionsUtil.newList(suffix));
		}

		return crossProduct;
	}

	protected void assertTokens(String prefix, CSSTokenType prefixType, String content, CSSTokenType contentType,
			String suffix, CSSTokenType suffixType)
	{
		ListCrossProduct<String> crossProduct = getNumberCrossProduct(prefix, content + suffix);

		for (List<String> list : crossProduct)
		{
			String text = StringUtil.concat(list);
			int contentStart = prefix.length();
			int suffixStart = text.length() - suffix.length();

			// @formatter:off
			assertToken(
				text,
				new TokenInfo(prefixType, 0, contentStart),
				new TokenInfo(contentType, contentStart, suffixStart - contentStart),
				new TokenInfo(suffixType, suffixStart, suffix.length())
			);
			// @formatter:on
		}
	}

	public void testEms()
	{
		assertTokens("{", CSSTokenType.LCURLY, "em", CSSTokenType.EMS, "}", CSSTokenType.RCURLY);
	}

	public void testExs()
	{
		assertTokens("{", CSSTokenType.LCURLY, "ex", CSSTokenType.EXS, "}", CSSTokenType.RCURLY);
	}

	public void testPixels()
	{
		assertTokens("{", CSSTokenType.LCURLY, "px", CSSTokenType.LENGTH, "}", CSSTokenType.RCURLY);
	}

	public void testCentimeters()
	{
		assertTokens("{", CSSTokenType.LCURLY, "cm", CSSTokenType.LENGTH, "}", CSSTokenType.RCURLY);
	}

	public void testMillimeters()
	{
		assertTokens("{", CSSTokenType.LCURLY, "mm", CSSTokenType.LENGTH, "}", CSSTokenType.RCURLY);
	}

	public void testInches()
	{
		assertTokens("{", CSSTokenType.LCURLY, "in", CSSTokenType.LENGTH, "}", CSSTokenType.RCURLY);
	}

	public void testPoints()
	{
		assertTokens("{", CSSTokenType.LCURLY, "pt", CSSTokenType.LENGTH, "}", CSSTokenType.RCURLY);
	}

	public void testPicas()
	{
		assertTokens("{", CSSTokenType.LCURLY, "pc", CSSTokenType.LENGTH, "}", CSSTokenType.RCURLY);
	}

	public void testDegrees()
	{
		assertTokens("{", CSSTokenType.LCURLY, "deg", CSSTokenType.ANGLE, "}", CSSTokenType.RCURLY);
	}

	public void testRads()
	{
		assertTokens("{", CSSTokenType.LCURLY, "rad", CSSTokenType.ANGLE, "}", CSSTokenType.RCURLY);
	}

	public void testGrads()
	{
		assertTokens("{", CSSTokenType.LCURLY, "grad", CSSTokenType.ANGLE, "}", CSSTokenType.RCURLY);
	}

	public void testMilliseconds()
	{
		assertTokens("{", CSSTokenType.LCURLY, "ms", CSSTokenType.TIME, "}", CSSTokenType.RCURLY);
	}

	public void testSeconds()
	{
		assertTokens("{", CSSTokenType.LCURLY, "s", CSSTokenType.TIME, "}", CSSTokenType.RCURLY);
	}

	public void testHertz()
	{
		assertTokens("{", CSSTokenType.LCURLY, "hz", CSSTokenType.FREQUENCY, "}", CSSTokenType.RCURLY);
	}

	public void testKiloHertz()
	{
		assertTokens("{", CSSTokenType.LCURLY, "khz", CSSTokenType.FREQUENCY, "}", CSSTokenType.RCURLY);
	}

	// NOTE: Moved color tests to CSSSpecialTokenHandlingTest now that we have special handling when we're inside and
	// outside of a rule body
}
