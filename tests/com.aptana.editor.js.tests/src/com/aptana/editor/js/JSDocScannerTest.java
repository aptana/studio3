package com.aptana.editor.js;

import junit.framework.AssertionFailedError;
import junit.framework.TestCase;

import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.Token;

public class JSDocScannerTest extends TestCase
{

	private JSDocScanner fScanner;

	@Override
	protected void setUp() throws Exception
	{
		fScanner = new JSDocScanner()
		{
			@Override
			protected IToken getToken(String tokenName)
			{
				return JSDocScannerTest.this.getToken(tokenName);
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
		String src = "@param {int} <i>size</i>";
		IDocument document = new Document(src);
		fScanner.setRange(document, 0, src.length());

		assertToken(getToken("meta.tag.documentation.js"), 0, 6);
		assertToken(getToken("comment.block.documentation.js"), 6, 1);
		assertToken(getToken("markup.underline.link"), 7, 5);
		assertToken(getToken("comment.block.documentation.js"), 12, 1);
		assertToken(getToken("text.html.basic"), 13, 3);
		assertToken(getToken("comment.block.documentation.js"), 16, 1);
		assertToken(getToken("comment.block.documentation.js"), 17, 1);
		assertToken(getToken("comment.block.documentation.js"), 18, 1);
		assertToken(getToken("comment.block.documentation.js"), 19, 1);
		assertToken(getToken("text.html.basic"), 20, 4);
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
