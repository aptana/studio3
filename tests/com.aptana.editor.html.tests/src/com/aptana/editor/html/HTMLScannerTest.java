package com.aptana.editor.html;

import junit.framework.AssertionFailedError;
import junit.framework.TestCase;

import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.Token;

public class HTMLScannerTest extends TestCase
{

	private HTMLScanner scanner;

	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		scanner = new HTMLScanner()
		{
			protected IToken createToken(String string)
			{
				return getToken(string);
			};
		};
	}

	protected IToken getToken(String string)
	{
		return new Token(string);
	}

	@Override
	protected void tearDown() throws Exception
	{
		scanner = null;
		super.tearDown();
	}

	public void testBasicTokenizing()
	{
		String src = "&nbsp; & text &#123; &#xabc012; &#bad; &#xnotgood;";
		IDocument document = new Document(src);
		scanner.setRange(document, 0, src.length());

		assertToken(getToken("constant.character.entity.html"), 0, 6);
		assertToken(Token.WHITESPACE, 6, 1);
		assertToken(getToken("invalid.illegal.bad-ampersand.html"), 7, 1);
		assertToken(Token.WHITESPACE, 8, 1);
		assertToken(getToken("text"), 9, 4);
		assertToken(Token.WHITESPACE, 13, 1);
		assertToken(getToken("constant.character.entity.html"), 14, 6);
		assertToken(Token.WHITESPACE, 20, 1);
		assertToken(getToken("constant.character.entity.html"), 21, 10);
		assertToken(Token.WHITESPACE, 31, 1);
		assertToken(getToken("invalid.illegal.bad-ampersand.html"), 32, 1);
		assertToken(getToken("text"), 33, 1);
		assertToken(getToken("text"), 34, 3);
		assertToken(getToken("text"), 37, 1);
		assertToken(Token.WHITESPACE, 38, 1);
		assertToken(getToken("invalid.illegal.bad-ampersand.html"), 39, 1);
		assertToken(getToken("text"), 40, 1);
		assertToken(getToken("text"), 41, 8);
		assertToken(getToken("text"), 49, 1);
	}

	private void assertToken(IToken token, int offset, int length)
	{
		assertToken(null, token, offset, length);
	}

	private void assertToken(String msg, IToken token, int offset, int length)
	{
		try
		{
			assertEquals(token.getData(), scanner.nextToken().getData());
			assertEquals(offset, scanner.getTokenOffset());
			assertEquals(length, scanner.getTokenLength());
		}
		catch (AssertionFailedError e)
		{
			System.out.println(msg);
			throw e;
		}

	}
}
