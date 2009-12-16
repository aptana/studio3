package com.aptana.editor.css;

import junit.framework.TestCase;

import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.Token;

public class CSSCodeScannerTest extends TestCase
{

	private CSSCodeScanner scanner;

	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		scanner = new CSSCodeScanner()
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
		String src = "html { color: red; background-color: #333; }";
		IDocument document = new Document(src);
		scanner.setRange(document, 0, src.length());

		assertToken(getToken("entity.name.tag.css"), 0, 4);
		assertToken(Token.WHITESPACE, 4, 1);
		assertToken(getToken("punctuation.section.property-list.css"), 5, 1);
		assertToken(Token.WHITESPACE, 6, 1);
		assertToken(getToken("support.type.property-name.css"), 7, 5);
		assertToken(getToken("punctuation.separator.key-value.css"), 12, 1);
		assertToken(Token.WHITESPACE, 13, 1);
		assertToken(getToken("support.constant.color.w3c-standard-color-name.css"), 14, 3);
		assertToken(getToken("punctuation.terminator.rule.css"), 17, 1);
		assertToken(Token.WHITESPACE, 18, 1);
		assertToken(getToken("support.type.property-name.css"), 19, 16);
		assertToken(getToken("punctuation.separator.key-value.css"), 35, 1);
		assertToken(Token.WHITESPACE, 36, 1);
		assertToken(getToken("constant.other.color.rgb-value.css"), 37, 4);
		assertToken(getToken("punctuation.terminator.rule.css"), 41, 1);
		assertToken(Token.WHITESPACE, 42, 1);
		assertToken(getToken("punctuation.section.property-list.css"), 43, 1);
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
