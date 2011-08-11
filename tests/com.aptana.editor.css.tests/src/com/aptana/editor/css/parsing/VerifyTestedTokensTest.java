/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.css.parsing;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import junit.framework.TestCase;

import com.aptana.core.util.StringUtil;
import com.aptana.editor.css.parsing.lexer.CSSTokenType;

/**
 * VerifyTestedTokensTest
 */
public class VerifyTestedTokensTest extends TestCase
{
	/**
	 * Maintain a list of all token types that were visited during testing
	 */
	public static final Set<CSSTokenType> TESTED_TOKEN_TYPES = EnumSet.noneOf(CSSTokenType.class);

	/**
	 * A set of token types that will not be covered by unit tests. See CSSTokenType for more details
	 */
	// @formatter:off
	private static final Set<CSSTokenType> IGNORED_TOKENS = EnumSet.of(
		CSSTokenType.EOF,
		CSSTokenType.LCURLY_MEDIA,
		CSSTokenType.RCURLY_MEDIA,
		CSSTokenType.META_MEDIA,
		CSSTokenType.META_RULE,
		CSSTokenType.META_SELECTOR,
		CSSTokenType.META_PROPERTY_VALUE,
		CSSTokenType.UNDEFINED,
		// the following are used for colorization but not parsing
		CSSTokenType.PROPERTY,
		CSSTokenType.MEDIA,
		CSSTokenType.FUNCTION,
		CSSTokenType.COLOR,
		CSSTokenType.DEPRECATED_COLOR,
		CSSTokenType.ELEMENT,
		CSSTokenType.FONT,
		CSSTokenType.VALUE
	);
	// @formatter:on

	public VerifyTestedTokensTest()
	{
		super("testTestedTokens");
	}

	public void testTestedTokens()
	{
		List<String> untestedTokens = new ArrayList<String>();

		for (CSSTokenType type : EnumSet.allOf(CSSTokenType.class))
		{
			if (!IGNORED_TOKENS.contains(type) && !TESTED_TOKEN_TYPES.contains(type))
			{
				untestedTokens.add(type.name());
			}
		}

		if (untestedTokens.size() > 0)
		{
			fail("The following CSSTokenTypes have not been tested: " + StringUtil.join(",", untestedTokens));
		}
	}
}
