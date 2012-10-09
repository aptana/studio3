/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.js;

import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.rules.ITokenScanner;
import org.eclipse.jface.text.rules.Token;

/**
 * Note: inheriting all tests from the superclass (i.e.: should work the same way that the previous code scanner did
 * work). Overridden tests can be used to know the differences among scanners (note that some test-cases were tweaked in
 * the superclass itself).
 */
public class JSCodeScannerJFlexTest extends JSCodeScannerOldTest
{
	@Override
	protected ITokenScanner createTokenScanner()
	{
		return new JSCodeScanner();
	}

	/**
	 * Overridden because the flex-based scanner matches a regexp when the original didn't
	 */
	@Override
	public void testOperatorTokens()
	{
		// Note: the original testOperatorTokens did have a '/' in the end which was removed because
		// it matched a regexp with /= %= += -= &= |= ^= ? ! % & * - + ~ = < > ^ | /
		String src = ">>>= >>> <<= >>= === !== >> << != <= >= == -- ++ && || *= /= %= += -= &= |= ^= ? ! % & * - + ~ = < > ^ | ";
		IDocument document = new Document(src);
		scanner.setRange(document, 0, src.length());

		assertToken(getToken("keyword.operator.js"), 0, 4);
		assertToken(Token.WHITESPACE, 4, 1);

		for (int i = 5; i < 25; i += 4)
		{
			assertToken(src.substring(i, i + 4), getToken("keyword.operator.js"), i, 3);
			assertToken(Token.WHITESPACE, i + 3, 1);
		}
		for (int i = 25; i < 79; i += 3)
		{
			assertToken(src.substring(i, i + 3), getToken("keyword.operator.js"), i, 2);
			assertToken(Token.WHITESPACE, i + 2, 1);
		}
		for (int i = 79; i < src.length(); i += 2)
		{
			assertToken(src.substring(i, i + 2), getToken("keyword.operator.js"), i, 1);
			assertToken(Token.WHITESPACE, i + 1, 1);
		}

		src = "/ ";
		document = new Document(src);
		scanner.setRange(document, 0, src.length());
		assertToken(src.substring(0, 1), getToken("keyword.operator.js"), 0, 1);
		assertToken(Token.WHITESPACE, 1, 1);

	}

	/**
	 * This scanner properly skips comments, whereas the old one didn't.
	 */
	@Override
	public void testNumberRegression()
	{
		String src = "var i = 1+\n//\n2;";
		IDocument document = new Document(src);
		scanner.setRange(document, 0, src.length());

		assertToken(getToken("storage.type.js"), 0, 3);
		assertToken(Token.WHITESPACE, 3, 1);
		assertToken(getToken("source.js"), 4, 1);
		assertToken(Token.WHITESPACE, 5, 1);
		assertToken(getToken("keyword.operator.js"), 6, 1);
		assertToken(Token.WHITESPACE, 7, 1);
		assertToken(getToken("constant.numeric.js"), 8, 1);
		assertToken(getToken("keyword.operator.js"), 9, 1);
		assertToken(Token.WHITESPACE, 10, 4);
		// assertToken(getToken("keyword.operator.js"), 11, 1); // technically not correct, but this scanner doesn't
		// // encounter comments normally
		// assertToken(getToken("keyword.operator.js"), 12, 1); // technically not correct, but this scanner doesn't
		// // encounter comments normally
		// assertToken(Token.WHITESPACE, 13, 1);
		assertToken(getToken("constant.numeric.js"), 14, 1);
		assertToken(getToken("punctuation.terminator.statement.js"), 15, 1);
	}

	/**
	 * This is a new test for corner-cases in the jflex scanner that the old scanner did not support.
	 */
	public void testFunctionHandlingOnJFlex() throws Exception
	{
		String src = "a = a = function"; // a should be a function name (i.e.: deal with look-ahead issues).
		IDocument document = new Document(src);
		scanner.setRange(document, 0, src.length());
		assertTokens("entity.name.function.js", "null", "keyword.operator.js", "null", "entity.name.function.js",
				"null", "keyword.operator.js", "null", "storage.type.function.js", "null");
	}
}
