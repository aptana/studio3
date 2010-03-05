package com.aptana.editor.ruby;

import junit.framework.AssertionFailedError;
import junit.framework.TestCase;

import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.Token;

public class RubyRegexScannerTest extends TestCase
{

	private RubyRegexpScanner fScanner;

	@Override
	protected void setUp() throws Exception
	{
		fScanner = new RubyRegexpScanner()
		{
			@Override
			protected IToken getToken(String tokenName)
			{
				return RubyRegexScannerTest.this.getToken(tokenName);
			}
		};
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

		assertToken(getToken("string.regexp.ruby"), 0, 1);
		assertToken(getToken("constant.character.escape.ruby"), 1, 4);
		assertToken(getToken("string.regexp.ruby"), 5, 1);
		assertToken(getToken("constant.character.escape.ruby"), 6, 4);
		assertToken(getToken("string.regexp.ruby"), 10, 1);
		assertToken(getToken("string.regexp.ruby"), 11, 1);
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
