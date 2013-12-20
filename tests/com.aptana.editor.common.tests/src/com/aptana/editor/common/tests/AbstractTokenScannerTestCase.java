/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.common.tests;

import org.junit.After;
import org.junit.Before;
import static org.junit.Assert.*;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.ITokenScanner;
import org.eclipse.jface.text.rules.Token;

import com.aptana.core.util.StringUtil;

public abstract class AbstractTokenScannerTestCase
{
	protected ITokenScanner scanner;

//	@Override
	@Before
	public void setUp() throws Exception
	{
//		super.setUp();

		scanner = createTokenScanner();
	}

//	@Override
	@After
	public void tearDown() throws Exception
	{
		scanner = null;

//		super.tearDown();
	}

	protected abstract ITokenScanner createTokenScanner();

	protected void assertToken(IToken token, int offset, int length)
	{
		assertToken(null, token, offset, length);
	}

	protected void assertToken(String msg, IToken token, int offset, int length)
	{
		IToken nextToken = scanner.nextToken();
		if (nextToken == null)
		{
			fail("Not expecting null token!");
		}
		assertEquals("Token scope doesn't match", token.getData(), nextToken.getData());
		assertEquals("Offsets don't match", offset, scanner.getTokenOffset());
		assertEquals("Lengths don't match", length, scanner.getTokenLength());
	}

	protected IToken getToken(String tokenName)
	{
		return new Token(tokenName);
	}

	protected void assertTokens(Object... tokens)
	{
		assertTokensMsg("", tokens);
	}

	/**
	 * @param expectedTokens
	 *            array of Strings or IToken to match against all the tokens in the scanner.
	 */
	protected void assertTokensMsg(String msg, Object... expectedTokens)
	{
		List<String> found = new ArrayList<String>(expectedTokens.length);
		List<String> expected = new ArrayList<String>(expectedTokens.length);

		for (Object expectedToken : expectedTokens)
		{
			if (expectedToken instanceof String)
			{
				expected.add("\"" + expectedToken + "\",");
			}
			else if (expectedToken instanceof IToken)
			{
				expected.add("\"" + ((IToken) expectedToken).getData() + "\",");
			}
			else
			{
				fail("Expecting object to be an IToken at this point.");
			}
		}

		IToken token = null;
		while (token != Token.EOF)
		{
			token = scanner.nextToken();
			found.add("\"" + token.getData() + "\",");
		}
		if (msg.length() > 0)
		{
			assertEquals(msg, StringUtil.join("\n", expected), StringUtil.join("\n", found));
		}
		else
		{
			assertEquals(StringUtil.join("\n", expected), StringUtil.join("\n", found));
		}
	}

	/**
	 * Helper just to print the tokens available.
	 */
	protected void printTokens()
	{
		IToken nextToken = scanner.nextToken();
		while (nextToken != Token.EOF)
		{
			System.out.println("\"" + nextToken.getData() + "\",");
			nextToken = scanner.nextToken();
		}

	}
}
