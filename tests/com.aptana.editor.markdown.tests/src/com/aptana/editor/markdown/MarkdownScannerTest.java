package com.aptana.editor.markdown;

import junit.framework.AssertionFailedError;
import junit.framework.TestCase;

import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.Token;

public class MarkdownScannerTest extends TestCase
{
	// TODO Refactor out common code with other language's token scanner testcases!
	private MarkdownScanner scanner;

	@Override
	protected void setUp() throws Exception
	{
		super.setUp();

		scanner = new MarkdownScanner();
	}

	@Override
	protected void tearDown() throws Exception
	{
		scanner = null;

		super.tearDown();
	}

	public void testBasicTokenizing()
	{
		String src = "Everything else:";
		IDocument document = new Document(src);
		scanner.setRange(document, 0, src.length());

		assertToken(getToken(""), 0, 10);
		assertToken(Token.WHITESPACE, 10, 1);
		assertToken(getToken(""), 11, 4);
		assertToken(getToken(""), 15, 1);
	}

	public void testItalic()
	{
		String src = "There is *italics* here";
		IDocument document = new Document(src);
		scanner.setRange(document, 0, src.length());

		assertToken(getToken(""), 0, 5);
		assertToken(Token.WHITESPACE, 5, 1);
		assertToken(getToken(""), 6, 2);
		assertToken(Token.WHITESPACE, 8, 1);
		assertToken(getToken("markup.italic.markdown"), 9, 9);
		assertToken(Token.WHITESPACE, 18, 1);
		assertToken(getToken(""), 19, 4);
	}

	public void testEscapedAsteriskIsntItalic()
	{
		String src = "Ain't no \\*italics\\* here";
		IDocument document = new Document(src);
		scanner.setRange(document, 0, src.length());

		assertToken(getToken(""), 0, 3);
		assertToken(getToken(""), 3, 1);
		assertToken(getToken(""), 4, 1);
		assertToken(Token.WHITESPACE, 5, 1);
		assertToken(getToken(""), 6, 2);
		assertToken(Token.WHITESPACE, 8, 1);
		assertToken(getToken("constant.character.escape.markdown"), 9, 2);
		assertToken(getToken(""), 11, 7);
		assertToken(getToken("constant.character.escape.markdown"), 18, 2);
		assertToken(Token.WHITESPACE, 20, 1);
		assertToken(getToken(""), 21, 4);
	}

	public void testEscapedUnderscoreIsntItalic()
	{
		String src = "Ain't no \\_italics\\_ here";
		IDocument document = new Document(src);
		scanner.setRange(document, 0, src.length());

		assertToken(getToken(""), 0, 3);
		assertToken(getToken(""), 3, 1);
		assertToken(getToken(""), 4, 1);
		assertToken(Token.WHITESPACE, 5, 1);
		assertToken(getToken(""), 6, 2);
		assertToken(Token.WHITESPACE, 8, 1);
		assertToken(getToken("constant.character.escape.markdown"), 9, 2);
		assertToken(getToken(""), 11, 7);
		assertToken(getToken("constant.character.escape.markdown"), 18, 2);
		assertToken(Token.WHITESPACE, 20, 1);
		assertToken(getToken(""), 21, 4);
	}

	public void testItalicWithSpaceInside()
	{
		String src = "There is *ita lics* here";
		IDocument document = new Document(src);
		scanner.setRange(document, 0, src.length());

		assertToken(getToken(""), 0, 5);
		assertToken(Token.WHITESPACE, 5, 1);
		assertToken(getToken(""), 6, 2);
		assertToken(Token.WHITESPACE, 8, 1);
		assertToken(getToken("markup.italic.markdown"), 9, 10);
		assertToken(Token.WHITESPACE, 19, 1);
		assertToken(getToken(""), 20, 4);
	}

	public void testItalicsUsingUnderscores()
	{
		String src = "There is _italics_ here";
		IDocument document = new Document(src);
		scanner.setRange(document, 0, src.length());

		assertToken(getToken(""), 0, 5);
		assertToken(Token.WHITESPACE, 5, 1);
		assertToken(getToken(""), 6, 2);
		assertToken(Token.WHITESPACE, 8, 1);
		assertToken(getToken("markup.italic.markdown"), 9, 9);
		assertToken(Token.WHITESPACE, 18, 1);
		assertToken(getToken(""), 19, 4);
	}

	public void testItalicsUsingUnderscoresWithSpaceInside()
	{
		String src = "There is _ita lics_ here";
		IDocument document = new Document(src);
		scanner.setRange(document, 0, src.length());

		assertToken(getToken(""), 0, 5);
		assertToken(Token.WHITESPACE, 5, 1);
		assertToken(getToken(""), 6, 2);
		assertToken(Token.WHITESPACE, 8, 1);
		assertToken(getToken("markup.italic.markdown"), 9, 10);
		assertToken(Token.WHITESPACE, 19, 1);
		assertToken(getToken(""), 20, 4);
	}

	public void testUnderscoreSurroundedBySpaceIsLiteral()
	{
		String src = "There is _ no italics _ here";
		IDocument document = new Document(src);
		scanner.setRange(document, 0, src.length());

		assertToken(getToken(""), 0, 5);
		assertToken(Token.WHITESPACE, 5, 1);
		assertToken(getToken(""), 6, 2);
		assertToken(Token.WHITESPACE, 8, 1);
		assertToken(getToken(""), 9, 1);
		assertToken(Token.WHITESPACE, 10, 1);
		assertToken(getToken(""), 11, 2);
		assertToken(Token.WHITESPACE, 13, 1);
		assertToken(getToken(""), 14, 7);
		assertToken(Token.WHITESPACE, 21, 1);
		assertToken(getToken(""), 22, 1);
		assertToken(Token.WHITESPACE, 23, 1);
		assertToken(getToken(""), 24, 4);
	}

	public void testAsteriskSurroundedBySpaceIsLiteral()
	{
		String src = "There is * no italics * here";
		IDocument document = new Document(src);
		scanner.setRange(document, 0, src.length());

		assertToken(getToken(""), 0, 5);
		assertToken(Token.WHITESPACE, 5, 1);
		assertToken(getToken(""), 6, 2);
		assertToken(Token.WHITESPACE, 8, 1);
		assertToken(getToken(""), 9, 1);
		assertToken(Token.WHITESPACE, 10, 1);
		assertToken(getToken(""), 11, 2);
		assertToken(Token.WHITESPACE, 13, 1);
		assertToken(getToken(""), 14, 7);
		assertToken(Token.WHITESPACE, 21, 1);
		assertToken(getToken(""), 22, 1);
		assertToken(Token.WHITESPACE, 23, 1);
		assertToken(getToken(""), 24, 4);
	}

	public void testBold()
	{
		String src = "There is **bold** here";
		IDocument document = new Document(src);
		scanner.setRange(document, 0, src.length());

		assertToken(getToken(""), 0, 5);
		assertToken(Token.WHITESPACE, 5, 1);
		assertToken(getToken(""), 6, 2);
		assertToken(Token.WHITESPACE, 8, 1);
		assertToken(getToken("markup.bold.markdown"), 9, 8);
		assertToken(Token.WHITESPACE, 17, 1);
		assertToken(getToken(""), 18, 4);
	}

	public void testBoldWithSpaceInside()
	{
		String src = "There is **bo ld** here";
		IDocument document = new Document(src);
		scanner.setRange(document, 0, src.length());

		assertToken(getToken(""), 0, 5);
		assertToken(Token.WHITESPACE, 5, 1);
		assertToken(getToken(""), 6, 2);
		assertToken(Token.WHITESPACE, 8, 1);
		assertToken(getToken("markup.bold.markdown"), 9, 9);
		assertToken(Token.WHITESPACE, 18, 1);
		assertToken(getToken(""), 19, 4);
	}

	public void testBoldUsingUnderscores()
	{
		String src = "There is __bold__ here";
		IDocument document = new Document(src);
		scanner.setRange(document, 0, src.length());

		assertToken(getToken(""), 0, 5);
		assertToken(Token.WHITESPACE, 5, 1);
		assertToken(getToken(""), 6, 2);
		assertToken(Token.WHITESPACE, 8, 1);
		assertToken(getToken("markup.bold.markdown"), 9, 8);
		assertToken(Token.WHITESPACE, 17, 1);
		assertToken(getToken(""), 18, 4);
	}

	public void testBoldUsingUnderscoresWithSpaceInside()
	{
		String src = "There is __bo ld__ here";
		IDocument document = new Document(src);
		scanner.setRange(document, 0, src.length());

		assertToken(getToken(""), 0, 5);
		assertToken(Token.WHITESPACE, 5, 1);
		assertToken(getToken(""), 6, 2);
		assertToken(Token.WHITESPACE, 8, 1);
		assertToken(getToken("markup.bold.markdown"), 9, 9);
		assertToken(Token.WHITESPACE, 18, 1);
		assertToken(getToken(""), 19, 4);
	}

	// http://daringfireball.net/projects/markdown/syntax#link
	// TODO Test reference (i.e. [text][ref-id], and later the def of a ref: [ref-id] )
	// TODO Test link with title (i.e. [text](htttp://example.org "title")

	public void testLink()
	{
		String src = "[View full diff and commit history](http://github.com/paulirish/html5-boilerplate/compare/v0.9...v0.9.1)";
		IDocument document = new Document(src);
		scanner.setRange(document, 0, src.length());

		assertToken(getToken("string.other.link.title.markdown"), 0, 35); //$NON-NLS-1$
		assertToken(getToken("punctuation.definition.link.markdown"), 35, 1); //$NON-NLS-1$
		assertToken(getToken("markup.underline.link.markdown"), 36, 67); //$NON-NLS-1$
		assertToken(getToken("punctuation.definition.link.markdown"), 103, 1); //$NON-NLS-1$
	}

	private IToken getToken(String string)
	{
		return new Token(string);
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
