/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.xml;

import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.ITokenScanner;
import org.eclipse.jface.text.rules.Token;
import org.junit.Test;

import com.aptana.editor.common.tests.AbstractTokenScannerTestCase;

public class XMLTagScannerTest extends AbstractTokenScannerTestCase
{

	@Override
	protected ITokenScanner createTokenScanner()
	{
		return new XMLTagScanner()
		{
			protected IToken createToken(String string)
			{
				return getToken(string);
			};
		};
	}

	@Test
	public void testBasicTokenizing()
	{
		String src = "<html id=\"chris\" class=\"cool\" height=\"100\">";
		IDocument document = new Document(src);
		scanner.setRange(document, 0, src.length());

		assertToken(getToken("punctuation.definition.tag.xml"), 0, 1);
		assertToken(getToken("entity.name.tag.xml"), 1, 4);
		assertToken(Token.WHITESPACE, 5, 1);
		assertToken(getToken("entity.other.attribute-name.xml"), 6, 2);
		assertToken(getToken("punctuation.separator.key-value.xml"), 8, 1);
		assertToken(getToken("string.quoted.double.xml"), 9, 7);
		assertToken(Token.WHITESPACE, 16, 1);
		assertToken(getToken("entity.other.attribute-name.xml"), 17, 5);
		assertToken(getToken("punctuation.separator.key-value.xml"), 22, 1);
		assertToken(getToken("string.quoted.double.xml"), 23, 6);
		assertToken(Token.WHITESPACE, 29, 1);
		assertToken(getToken("entity.other.attribute-name.xml"), 30, 6);
		assertToken(getToken("punctuation.separator.key-value.xml"), 36, 1);
		assertToken(getToken("string.quoted.double.xml"), 37, 5);
		assertToken(getToken("punctuation.definition.tag.xml"), 42, 1);
	}

	@Test
	public void testMultiLineSingleQuoteString()
	{
		String src = "<html attribute='\nchris'>";
		IDocument document = new Document(src);
		scanner.setRange(document, 0, src.length());

		assertToken(getToken("punctuation.definition.tag.xml"), 0, 1);
		assertToken(getToken("entity.name.tag.xml"), 1, 4);
		assertToken(Token.WHITESPACE, 5, 1);
		assertToken(getToken("entity.other.attribute-name.xml"), 6, 9);
		assertToken(getToken("punctuation.separator.key-value.xml"), 15, 1);
		assertToken(getToken("string.quoted.single.xml"), 16, 8);
		assertToken(getToken("punctuation.definition.tag.xml"), 24, 1);
	}

	@Test
	public void testMultiLineDoubleQuoteString()
	{
		String src = "<html attribute=\"\nchris\">";
		IDocument document = new Document(src);
		scanner.setRange(document, 0, src.length());

		assertToken(getToken("punctuation.definition.tag.xml"), 0, 1);
		assertToken(getToken("entity.name.tag.xml"), 1, 4);
		assertToken(Token.WHITESPACE, 5, 1);
		assertToken(getToken("entity.other.attribute-name.xml"), 6, 9);
		assertToken(getToken("punctuation.separator.key-value.xml"), 15, 1);
		assertToken(getToken("string.quoted.double.xml"), 16, 8);
		assertToken(getToken("punctuation.definition.tag.xml"), 24, 1);
	}
}
