package com.aptana.editor.common.text;

import junit.framework.TestCase;

import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.Token;

public class SingleTokenScannerTest extends TestCase
{

	public void testReturnsSingleTokenForEntireRangeAndThenEOF() throws Exception
	{
		IToken token = new Token("my_token");
		SingleTokenScanner scanner = new SingleTokenScanner(token);
		scanner.setRange(null, 0, 100);
		assertEquals(token, scanner.nextToken());
		assertEquals(0, scanner.getTokenOffset());
		assertEquals(100, scanner.getTokenLength());
		assertEquals(Token.EOF, scanner.nextToken());
	}

}
