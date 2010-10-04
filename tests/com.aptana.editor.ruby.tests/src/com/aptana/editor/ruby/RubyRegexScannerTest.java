package com.aptana.editor.ruby;

import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.ITokenScanner;

import com.aptana.editor.common.tests.AbstractTokenScannerTestCase;

public class RubyRegexScannerTest extends AbstractTokenScannerTestCase
{

	@Override
	protected ITokenScanner createTokenScanner()
	{
		return new RubyRegexpScanner()
		{
			@Override
			protected IToken getToken(String tokenName)
			{
				return RubyRegexScannerTest.this.getToken(tokenName);
			}
		};
	}

	public void testBasicTokenizing()
	{
		String src = "[\\x20-\\x7F]+";
		IDocument document = new Document(src);
		scanner.setRange(document, 0, src.length());

		assertToken(getToken("string.regexp.ruby"), 0, 1);
		assertToken(getToken("constant.character.escape.ruby"), 1, 4);
		assertToken(getToken("string.regexp.ruby"), 5, 1);
		assertToken(getToken("constant.character.escape.ruby"), 6, 4);
		assertToken(getToken("string.regexp.ruby"), 10, 1);
		assertToken(getToken("string.regexp.ruby"), 11, 1);
	}

}
