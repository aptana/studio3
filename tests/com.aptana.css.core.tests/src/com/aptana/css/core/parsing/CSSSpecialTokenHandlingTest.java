/**
 * Aptana Studio
 * Copyright (c) 2005-2013 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.css.core.parsing;

import org.junit.Test;

/**
 * CSSSpecialTokenHandlingTest
 */
public class CSSSpecialTokenHandlingTest extends CSSTokensTest
{
	@Test
	public void testRGBIsId()
	{
		assertToken("#808080", CSSTokenType.ID, 0, 7); //$NON-NLS-1$
	}

	@Test
	public void testRGBIsId2()
	{
		// @formatter:off
		assertToken(
			"a#abc",
			new TokenInfo(CSSTokenType.IDENTIFIER, 0, 1),
			new TokenInfo(CSSTokenType.ID, 1, 4)
		);
		// @formatter:on
	}

	@Test
	public void testRGBIsColor()
	{
		// @formatter:off
		assertToken(
			"a{background:#abc}",
			new TokenInfo(CSSTokenType.IDENTIFIER, 0, 1),
			new TokenInfo(CSSTokenType.LCURLY, 1, 1),
			new TokenInfo(CSSTokenType.IDENTIFIER, 2, 10),
			new TokenInfo(CSSTokenType.COLON, 12, 1),
			new TokenInfo(CSSTokenType.RGB, 13, 4)
		);
		// @formatter:on
	}

	@Test
	public void testRGBIsIdInMedia()
	{
		// @formatter:off
		assertToken(
			"@media'print'{a#abc{background:red}}",
			new TokenInfo(CSSTokenType.MEDIA_KEYWORD, 0, 6),
			new TokenInfo(CSSTokenType.SINGLE_QUOTED_STRING, 6, 7),
			new TokenInfo(CSSTokenType.LCURLY, 13, 1),
			new TokenInfo(CSSTokenType.IDENTIFIER, 14, 1),
			new TokenInfo(CSSTokenType.ID, 15, 4),
			new TokenInfo(CSSTokenType.LCURLY, 19, 1),
			new TokenInfo(CSSTokenType.IDENTIFIER, 20, 10),
			new TokenInfo(CSSTokenType.COLON, 30, 1),
			new TokenInfo(CSSTokenType.IDENTIFIER, 31, 3)
		);
		// @formatter:on
	}

	@Test
	public void testRGBIsColorInMedia()
	{
		// @formatter:off
		assertToken(
			"@media'print'{a{background:#abc}}",
			new TokenInfo(CSSTokenType.MEDIA_KEYWORD, 0, 6),
			new TokenInfo(CSSTokenType.SINGLE_QUOTED_STRING, 6, 7),
			new TokenInfo(CSSTokenType.LCURLY, 13, 1),
			new TokenInfo(CSSTokenType.IDENTIFIER, 14, 1),
			new TokenInfo(CSSTokenType.LCURLY, 15, 1),
			new TokenInfo(CSSTokenType.IDENTIFIER, 16, 10),
			new TokenInfo(CSSTokenType.COLON, 26, 1),
			new TokenInfo(CSSTokenType.RGB, 27, 4)
		);
		// @formatter:on
	}

	@Test
	public void testmsViewPort()
	{
		// @formatter:off
		assertToken(
			"@-ms-viewport{width:auto}",
			new TokenInfo(CSSTokenType.MS_VIEWPORT, 0, 13),
			new TokenInfo(CSSTokenType.LCURLY, 13, 1),
			new TokenInfo(CSSTokenType.IDENTIFIER, 14, 5),
			new TokenInfo(CSSTokenType.COLON, 19, 1),
			new TokenInfo(CSSTokenType.IDENTIFIER, 20, 4),
			new TokenInfo(CSSTokenType.RCURLY, 24, 1)
		);
		// @formatter:on
	}
}
