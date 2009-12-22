package com.aptana.editor.xml;

import junit.framework.AssertionFailedError;
import junit.framework.TestCase;

import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.Token;

public class XMLTagScannerTest extends TestCase
{

	private XMLTagScanner scanner;

	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		scanner = new XMLTagScanner()
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

		assertToken(getToken("punctuation.definition.tag.xml"), 0, 1);
		assertToken(getToken("entity.name.tag.xml"), 1, 4);
		assertToken(Token.WHITESPACE, 5, 1);
		assertToken(getToken("entity.other.attribute-name.xml"), 6, 2);
		assertToken(getToken("punctuation.separator.key-value.xml"), 8, 1);
		assertToken(getToken("string.quoted.double.xml"), 9, 7);
		assertToken(Token.WHITESPACE, 16, 1);
		assertToken(getToken("entity.other.attribute-name.xml"), 17, 5);
		assertToken(getToken("punctuation.separator.key-value.xml"), 22, 1);
		assertToken(getToken("string.quoted.double.xml"), 23, 6);
		assertToken(Token.WHITESPACE, 29, 1);
		assertToken(getToken("entity.other.attribute-name.xml"), 30, 6);
		assertToken(getToken("punctuation.separator.key-value.xml"), 36, 1);
		assertToken(getToken("string.quoted.double.xml"), 37, 5);
		assertToken(getToken("punctuation.definition.tag.xml"), 42, 1);
	}
	
	public void testMultiLineSingleQuoteString()
	{
		String src = "<html attribute='\nchris'>";
		IDocument document = new Document(src);
		scanner.setRange(document, 0, src.length());

		assertToken(getToken("punctuation.definition.tag.xml"), 0, 1);
		assertToken(getToken("entity.name.tag.xml"), 1, 4);
		assertToken(Token.WHITESPACE, 5, 1);
		assertToken(getToken("entity.other.attribute-name.xml"), 6, 9);
		assertToken(getToken("punctuation.separator.key-value.xml"), 15, 1);
		assertToken(getToken("string.quoted.single.xml"), 16, 8);
		assertToken(getToken("punctuation.definition.tag.xml"), 24, 1);
	}
	
	public void testMultiLineDoubleQuoteString()
	{
		String src = "<html attribute=\"\nchris\">";
		IDocument document = new Document(src);
		scanner.setRange(document, 0, src.length());

		assertToken(getToken("punctuation.definition.tag.xml"), 0, 1);
		assertToken(getToken("entity.name.tag.xml"), 1, 4);
		assertToken(Token.WHITESPACE, 5, 1);
		assertToken(getToken("entity.other.attribute-name.xml"), 6, 9);
		assertToken(getToken("punctuation.separator.key-value.xml"), 15, 1);
		assertToken(getToken("string.quoted.double.xml"), 16, 8);
		assertToken(getToken("punctuation.definition.tag.xml"), 24, 1);
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
