package com.aptana.editor.js;

import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.rules.ITokenScanner;

import com.aptana.editor.common.tests.AbstractTokenScannerTestCase;

public class JSDoubleQuotedStringScannerTest extends AbstractTokenScannerTestCase
{
	@Override
	protected ITokenScanner createTokenScanner()
	{
		return new JSEscapeSequenceScanner("string.quoted.double.js");
	}

	public void testBasicTokenizing()
	{
		String src = "This is a double quoted JS string with escape \\x20";
		IDocument document = new Document(src);
		scanner.setRange(document, 0, src.length());

		for (int i = 0; i < 46; ++i)
		{
			assertToken(getToken("string.quoted.double.js"), i, 1);
		}
		assertToken(getToken("constant.character.escape.js"), 46, 4);
	}
}
