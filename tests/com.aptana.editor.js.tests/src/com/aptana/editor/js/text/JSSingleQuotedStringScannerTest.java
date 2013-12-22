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

public class JSSingleQuotedStringScannerTest extends AbstractTokenScannerTestCase
{
	@Override
	protected ITokenScanner createTokenScanner()
	{
		return new JSEscapeSequenceScanner("string.quoted.single.js");
	}

	@Test
	public void testBasicTokenizing()
	{
		String src = "This is a single quoted JS string with escape \\x20";
		IDocument document = new Document(src);
		scanner.setRange(document, 0, src.length());

		for (int i = 0; i < 46; ++i)
		{
			assertToken(getToken("string.quoted.single.js"), i, 1);
		}
		assertToken(getToken("constant.character.escape.js"), 46, 4);
	}

}
