package com.aptana.editor.js;

import junit.framework.AssertionFailedError;
import junit.framework.TestCase;

import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.Token;

public class JSRegexScannerTest extends TestCase
{
	private JSEscapeSequenceScanner fScanner;

	@Override
	protected void setUp() throws Exception
	{
		fScanner = new JSEscapeSequenceScanner("string.regexp.js");
	}

	@Override
	protected void tearDown() throws Exception
	{
		fScanner = null;
	}

	public void testBasicTokenizing()
	{
		String src = "[\\x20-\\x7F]+";
		IDocument document = new Document(src);
		fScanner.setRange(document, 0, src.length());

		assertToken(getToken("string.regexp.js"), 0, 1);
		assertToken(getToken("constant.character.escape.js"), 1, 4);
		assertToken(getToken("string.regexp.js"), 5, 1);
		assertToken(getToken("constant.character.escape.js"), 6, 4);
		assertToken(getToken("string.regexp.js"), 10, 1);
		assertToken(getToken("string.regexp.js"), 11, 1);
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
