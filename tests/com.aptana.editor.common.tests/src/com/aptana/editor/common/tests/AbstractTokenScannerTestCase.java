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
		assertEquals(token.getData(), scanner.nextToken().getData());
		assertEquals(offset, scanner.getTokenOffset());
		assertEquals(length, scanner.getTokenLength());
	}
	
	protected IToken getToken(String tokenName)
	{
		return new Token(tokenName);
	}

}
