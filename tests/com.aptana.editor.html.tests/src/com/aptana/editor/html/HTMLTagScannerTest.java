package com.aptana.editor.html;

import junit.framework.AssertionFailedError;
import junit.framework.TestCase;

import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.Token;

public class HTMLTagScannerTest extends TestCase
{

	private HTMLTagScanner scanner;

	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		scanner = new HTMLTagScanner()
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
		String src = "<html id=\"chris\" class=\"cool\" height=\"100\">";
		IDocument document = new Document(src);
		scanner.setRange(document, 0, src.length());

		assertToken(getToken("punctuation.definition.tag.begin.html"), 0, 1);
		assertToken(getToken("entity.name.tag.structure.any.html"), 1, 4);
		assertToken(Token.WHITESPACE, 5, 1);
		assertToken(getToken("entity.other.attribute-name.id.html"), 6, 2);
		assertToken(getToken("punctuation.separator.key-value.html"), 8, 1);
		assertToken(getToken("string.quoted.double.html"), 9, 7);
		assertToken(Token.WHITESPACE, 16, 1);
		assertToken(getToken("entity.other.attribute-name.class.html"), 17, 5);
		assertToken(getToken("punctuation.separator.key-value.html"), 22, 1);
		assertToken(getToken("string.quoted.double.html"), 23, 6);
		assertToken(Token.WHITESPACE, 29, 1);
		assertToken(getToken("entity.other.attribute-name.html"), 30, 6);
		assertToken(getToken("punctuation.separator.key-value.html"), 36, 1);
		assertToken(getToken("string.quoted.double.html"), 37, 5);
		assertToken(getToken("punctuation.definition.tag.end.html"), 42, 1);
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
