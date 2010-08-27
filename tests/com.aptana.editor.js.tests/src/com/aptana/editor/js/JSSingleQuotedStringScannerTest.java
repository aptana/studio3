package com.aptana.editor.js;

import junit.framework.AssertionFailedError;
import junit.framework.TestCase;

import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.Token;

public class JSSingleQuotedStringScannerTest extends TestCase
{
	private JSEscapeSequenceScanner fScanner;

	@Override
	protected void setUp() throws Exception
	{
		fScanner = new JSEscapeSequenceScanner("string.quoted.single.js");
	}

	@Override
	protected void tearDown() throws Exception
	{
		fScanner = null;
	}

	public void testBasicTokenizing()
	{
		String src = "This is a single quoted JS string with escape \\x20";
		IDocument document = new Document(src);
		fScanner.setRange(document, 0, src.length());

		for (int i = 0; i < 46; ++i)
		{
			assertToken(getToken("string.quoted.single.js"), i, 1);
		}
		assertToken(getToken("constant.character.escape.js"), 46, 4);
	}

	private IToken getToken(String tokenName)
	{
		return new Token(tokenName);
	}

	private void assertToken(IToken token, int offset, int length)
	{
		assertToken(null, token, offset, length);
	}

	private void assertToken(String msg, IToken token, int offset, int length)
	{
		try
		{
			assertEquals(token.getData(), fScanner.nextToken().getData());
			assertEquals(offset, fScanner.getTokenOffset());
			assertEquals(length, fScanner.getTokenLength());
		}
		catch (AssertionFailedError e)
		{
			System.out.println(msg);
			throw e;
		}
	}
}
