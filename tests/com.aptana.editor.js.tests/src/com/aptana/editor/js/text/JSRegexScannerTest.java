/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.js.text;

import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.rules.ITokenScanner;
import org.junit.Test;

import com.aptana.editor.common.tests.AbstractTokenScannerTestCase;

public class JSRegexScannerTest extends AbstractTokenScannerTestCase
{
	@Override
	protected ITokenScanner createTokenScanner()
	{
		return new JSEscapeSequenceScanner("string.regexp.js");
	}

	@Test
	public void testBasicTokenizing()
	{
		String src = "[\\x20-\\x7F]+";
		IDocument document = new Document(src);
		scanner.setRange(document, 0, src.length());

		assertToken(getToken("string.regexp.js"), 0, 1);
		assertToken(getToken("constant.character.escape.js"), 1, 4);
		assertToken(getToken("string.regexp.js"), 5, 1);
		assertToken(getToken("constant.character.escape.js"), 6, 4);
		assertToken(getToken("string.regexp.js"), 10, 1);
		assertToken(getToken("string.regexp.js"), 11, 1);
	}
}
