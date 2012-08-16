/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.css;

import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.rules.ITokenScanner;

/**
 * Note: inheriting from CSSCodeScannerTest because it must pass all of its existing tests.
 */
public class CSSCodeScannerFlexTest extends CSSCodeScannerTest
{

	@Override
	protected ITokenScanner createTokenScanner()
	{
		return new CSSCodeScannerFlex();
	}

	@Override
	public void testImportant() throws Exception
	{
		String src = "!  impORtant what!impORtantwhat\n!impORtant\n!important something";
		IDocument document = new Document(src);
		scanner.setRange(document, 0, src.length());
		assertTokens("support.constant.property-value.css", "null", "source.css",
				"support.constant.property-value.css", "source.css", "null", "support.constant.property-value.css",
				"null", "support.constant.property-value.css", "null", "source.css", "null");
	}
}
