/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.common.tests;

import junit.framework.TestCase;

import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.ITokenScanner;
import org.eclipse.jface.text.rules.Token;

public abstract class AbstractTokenScannerTestCase extends TestCase
{
	protected ITokenScanner scanner;

	@Override
	protected void setUp() throws Exception
	{
		super.setUp();

		scanner = createTokenScanner();
	}

	@Override
	protected void tearDown() throws Exception
	{
		scanner = null;

		super.tearDown();
	}

	protected abstract ITokenScanner createTokenScanner();

	protected void assertToken(IToken token, int offset, int length)
	{
		assertToken(null, token, offset, length);
	}

	protected void assertToken(String msg, IToken token, int offset, int length)
	{
		assertEquals("Token scope doesn't match", token.getData(), scanner.nextToken().getData());
		assertEquals("Offsets don't match", offset, scanner.getTokenOffset());
		assertEquals("Lengths don't match", length, scanner.getTokenLength());
	}
	
	protected IToken getToken(String tokenName)
	{
		return new Token(tokenName);
	}

}
