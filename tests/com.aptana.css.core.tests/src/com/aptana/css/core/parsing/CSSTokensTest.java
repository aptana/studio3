/**
 * Aptana Studio
 * Copyright (c) 2005-2013 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.css.core.parsing;

import org.junit.After;
import org.junit.Before;
import static org.junit.Assert.*;
import junit.framework.TestCase;
import beaver.Symbol;

public class CSSTokensTest
{
	class TokenInfo
	{
		public final CSSTokenType type;
		public final int offset;
		public final int length;

		public TokenInfo(CSSTokenType type, int offset, int length)
		{
			this.type = type;
			this.offset = offset;
			this.length = length;
		}
	}

	private CSSFlexScanner fScanner;

//	@Override
	@Before
	public void setUp() throws Exception
	{
		fScanner = new CSSFlexScanner();
	}

//	@Override
	@After
	public void tearDown() throws Exception
	{
		fScanner = null;
	}

	protected void assertToken(String source, CSSTokenType type)
	{
		assertToken(source, type, 0, source.length());
	}

	protected void assertToken(String source, CSSTokenType type, int offset, int length)
	{
		assertToken(source, new TokenInfo(type, offset, length));
		assertToken(source.toUpperCase(), new TokenInfo(type, offset, length));
	}

	protected void assertToken(String source, TokenInfo... infos)
	{
		setSource(source);

		for (TokenInfo info : infos)
		{
			try
			{
				Symbol token = fScanner.nextToken();

				// Allow null types when we don't need to verify the type, like with WHITESPACE, for example
				if (info.type != null)
				{
					// Add in token types that are being tested, regardless if they pass or not. This info is used by
					// another test to determine if we've covered all token types during testing
					VerifyTestedTokensTest.TESTED_TOKEN_TYPES.add(info.type);

					assertEquals("Checking token type for '" + source + "'", info.type.getIndex(), token.getId());
				}

				assertEquals("Checking token offset", info.offset, token.getStart());
				assertEquals("Checking token length", info.length, token.getEnd() - token.getStart() + 1);
			}
			catch (Exception t)
			{
				fail(t.getMessage());
			}
		}
	}

	protected void setSource(String source)
	{
		fScanner.setSource(source);
	}
}
