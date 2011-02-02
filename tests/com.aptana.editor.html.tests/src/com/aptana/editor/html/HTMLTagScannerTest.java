/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.html;

import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.ITokenScanner;
import org.eclipse.jface.text.rules.Token;

import com.aptana.editor.common.tests.AbstractTokenScannerTestCase;

public class HTMLTagScannerTest extends AbstractTokenScannerTestCase
{

	@Override
	protected ITokenScanner createTokenScanner()
	{
		return new HTMLTagScanner()
		{
			protected IToken createToken(String string)
			{
				return HTMLTagScannerTest.this.getToken(string);
			};
		};
	}

	public void testBasicTokenizing()
	{
		String src = "<html id=\"chris\" class=\"cool\" height=\"100\">";
		IDocument document = new Document(src);
		scanner.setRange(document, 0, src.length());

		assertToken(getToken("punctuation.definition.tag.begin.html"), 0, 1);
		assertToken(getToken("entity.name.tag.structure.any.html"), 1, 4);
		assertToken(Token.WHITESPACE, 5, 1);
		assertToken(getToken("entity.other.attribute-name.id.html"), 6, 2);
		assertToken(getToken("punctuation.separator.key-value.html"), 8, 1);
		assertToken(getToken("string.quoted.double.html"), 9, 7);
		assertToken(Token.WHITESPACE, 16, 1);
		assertToken(getToken("entity.other.attribute-name.class.html"), 17, 5);
		assertToken(getToken("punctuation.separator.key-value.html"), 22, 1);
		assertToken(getToken("string.quoted.double.html"), 23, 6);
		assertToken(Token.WHITESPACE, 29, 1);
		assertToken(getToken("entity.other.attribute-name.html"), 30, 6);
		assertToken(getToken("punctuation.separator.key-value.html"), 36, 1);
		assertToken(getToken("string.quoted.double.html"), 37, 5);
		assertToken(getToken("punctuation.definition.tag.end.html"), 42, 1);
	}

	public void testMultiLineSingleQuoteString()
	{
		String src = "<html attribute='\nchris'>";
		IDocument document = new Document(src);
		scanner.setRange(document, 0, src.length());

		assertToken(getToken("punctuation.definition.tag.begin.html"), 0, 1);
		assertToken(getToken("entity.name.tag.structure.any.html"), 1, 4);
		assertToken(Token.WHITESPACE, 5, 1);
		assertToken(getToken("entity.other.attribute-name.html"), 6, 9);
		assertToken(getToken("punctuation.separator.key-value.html"), 15, 1);
		assertToken(getToken("string.quoted.single.html"), 16, 8);
		assertToken(getToken("punctuation.definition.tag.end.html"), 24, 1);
	}

	public void testMultiLineDoubleQuoteString()
	{
		String src = "<html attribute=\"\nchris\">";
		IDocument document = new Document(src);
		scanner.setRange(document, 0, src.length());

		assertToken(getToken("punctuation.definition.tag.begin.html"), 0, 1);
		assertToken(getToken("entity.name.tag.structure.any.html"), 1, 4);
		assertToken(Token.WHITESPACE, 5, 1);
		assertToken(getToken("entity.other.attribute-name.html"), 6, 9);
		assertToken(getToken("punctuation.separator.key-value.html"), 15, 1);
		assertToken(getToken("string.quoted.double.html"), 16, 8);
		assertToken(getToken("punctuation.definition.tag.end.html"), 24, 1);
	}
}
