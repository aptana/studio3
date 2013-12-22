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
import org.junit.Test;

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
	@Test
	public void testImportant() throws Exception
	{
		String src = "!  impORtant what!impORtantwhat\n!impORtant\n!important something";
		IDocument document = new Document(src);
		scanner.setRange(document, 0, src.length());
		assertTokens("support.constant.property-value.css", "null", "source.css",
				"support.constant.property-value.css", "source.css", "null", "support.constant.property-value.css",
				"null", "support.constant.property-value.css", "null", "source.css", "null");
	}

	@Test
	public void testNotConstruct() throws Exception
	{
		String src = "svg:not(:root) {overflow: hidden;}";
		IDocument document = new Document(src);
		scanner.setRange(document, 0, src.length());
		assertTokens("source.css", "meta.property-value.css punctuation.separator.key-value.css",
				"meta.property-value.css keyword.control.not.css",
				"meta.property-value.css punctuation.section.function.css",
				"meta.property-value.css punctuation.separator.key-value.css", "meta.property-value.css source.css",
				"meta.property-value.css punctuation.section.function.css", "meta.property-value.css",
				"meta.property-list.css meta.property-value.css punctuation.section.property-list.css",
				"meta.property-list.css meta.property-value.css meta.property-name.css support.type.property-name.css",
				"meta.property-list.css meta.property-value.css punctuation.separator.key-value.css",
				"meta.property-list.css meta.property-value.css",
				"meta.property-list.css meta.property-value.css support.constant.property-value.css",
				"meta.property-list.css meta.property-value.css punctuation.terminator.rule.css",
				"meta.property-list.css punctuation.section.property-list.css", "null");
	}
}
