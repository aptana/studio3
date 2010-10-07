package com.aptana.editor.js;

import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.ITokenScanner;

import com.aptana.editor.common.tests.AbstractTokenScannerTestCase;

public class JSDocScannerTest extends AbstractTokenScannerTestCase
{

	@Override
	protected ITokenScanner createTokenScanner()
	{
		return new JSDocScanner()
		{
			@Override
			protected IToken getToken(String tokenName)
			{
				return JSDocScannerTest.this.getToken(tokenName);
			}
		};
	}

	public void testBasicTokenizing()
	{
		String src = "@param {int} <i>size</i>";
		IDocument document = new Document(src);
		scanner.setRange(document, 0, src.length());

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
}
