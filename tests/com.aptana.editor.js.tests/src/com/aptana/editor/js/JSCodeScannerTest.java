package com.aptana.editor.js;

import junit.framework.TestCase;

import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.Token;

public class JSCodeScannerTest extends TestCase
{

	private JSCodeScanner scanner;

	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		scanner = new JSCodeScanner()
		{
			protected IToken createToken(String string)
			{
				return getToken(string);
			};
		};
	}

	@Override
	protected void tearDown() throws Exception
	{
		scanner = null;
		super.tearDown();
	}

	public void testBasicTokenizing()
	{
		String src = "var one = 1;";
		IDocument document = new Document(src);
		scanner.setRange(document, 0, src.length());

		assertToken(getToken("storage.type.js"), 0, 3);
		assertToken(Token.WHITESPACE, 3, 1);
		assertToken(getToken("source.js"), 4, 3);
		assertToken(Token.WHITESPACE, 7, 1);
		assertToken(getToken("keyword.operator.js"), 8, 1);
		assertToken(Token.WHITESPACE, 9, 1);
		assertToken(getToken("constant.numeric.js"), 10, 1);
		assertToken(getToken("punctuation.terminator.statement.js"), 11, 1);
	}

	private IToken getToken(String string)
	{
		return new Token(string);
	}

	private void assertToken(IToken token, int offset, int length)
	{
		assertEquals(token.getData(), scanner.nextToken().getData());
		assertEquals(offset, scanner.getTokenOffset());
		assertEquals(length, scanner.getTokenLength());
	}
}
