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
import org.junit.Test;

import com.aptana.core.util.StringUtil;
import com.aptana.editor.common.tests.AbstractTokenScannerTestCase;

public class HTMLDoctypeScannerTest extends AbstractTokenScannerTestCase
{

	@Override
	protected ITokenScanner createTokenScanner()
	{
		return new HTMLDoctypeScanner()
		{
			protected IToken createToken(String string)
			{
				return HTMLDoctypeScannerTest.this.getToken(string);
			};
		};
	}

	@Test
	public void testHTML5Doctype()
	{
		String src = "<!DOCTYPE html>";
		IDocument document = new Document(src);
		scanner.setRange(document, 0, src.length());

		assertToken(getToken("punctuation.definition.tag.begin.html"), 0, 1);
		assertToken(getToken(StringUtil.EMPTY), 1, 1);
		assertToken(getToken("entity.name.tag.doctype.html"), 2, 7);
		assertToken(Token.WHITESPACE, 9, 1);
		assertToken(getToken(StringUtil.EMPTY), 10, 4);
		assertToken(getToken("punctuation.definition.tag.end.html"), 14, 1);
	}

	@Test
	public void testHTML4Doctype()
	{
		String src = "<!DOCTYPE PUBLIC '-//W3C//DTD HTML 4.0//EN'>";
		IDocument document = new Document(src);
		scanner.setRange(document, 0, src.length());

		assertToken(getToken("punctuation.definition.tag.begin.html"), 0, 1);
		assertToken(getToken(StringUtil.EMPTY), 1, 1);
		assertToken(getToken("entity.name.tag.doctype.html"), 2, 7);
		assertToken(Token.WHITESPACE, 9, 1);
		assertToken(getToken(StringUtil.EMPTY), 10, 6);
		assertToken(Token.WHITESPACE, 16, 1);
		assertToken(getToken("string.quoted.single.doctype.identifiers-and-DTDs.html"), 17, 26);
		assertToken(getToken("punctuation.definition.tag.end.html"), 43, 1);
	}

	@Test
	public void testHTML4StrictDoctype()
	{
		String src = "<!DOCTYPE PUBLIC '-//W3C//DTD HTML 4.0//EN' 'http://www.w3.org/TR/REC-html40/strict.dtd'>";
		IDocument document = new Document(src);
		scanner.setRange(document, 0, src.length());

		assertToken(getToken("punctuation.definition.tag.begin.html"), 0, 1);
		assertToken(getToken(StringUtil.EMPTY), 1, 1);
		assertToken(getToken("entity.name.tag.doctype.html"), 2, 7);
		assertToken(Token.WHITESPACE, 9, 1);
		assertToken(getToken(StringUtil.EMPTY), 10, 6);
		assertToken(Token.WHITESPACE, 16, 1);
		assertToken(getToken("string.quoted.single.doctype.identifiers-and-DTDs.html"), 17, 26);
		assertToken(Token.WHITESPACE, 43, 1);
		assertToken(getToken("string.quoted.single.doctype.identifiers-and-DTDs.html"), 44, 44);
		assertToken(getToken("punctuation.definition.tag.end.html"), 88, 1);
	}

	@Test
	public void testHTML401Doctype()
	{
		String src = "<!DOCTYPE PUBLIC \"-//W3C//DTD HTML 4.01//EN\">";
		IDocument document = new Document(src);
		scanner.setRange(document, 0, src.length());

		assertToken(getToken("punctuation.definition.tag.begin.html"), 0, 1);
		assertToken(getToken(StringUtil.EMPTY), 1, 1);
		assertToken(getToken("entity.name.tag.doctype.html"), 2, 7);
		assertToken(Token.WHITESPACE, 9, 1);
		assertToken(getToken(StringUtil.EMPTY), 10, 6);
		assertToken(Token.WHITESPACE, 16, 1);
		assertToken(getToken("string.quoted.double.doctype.identifiers-and-DTDs.html"), 17, 27);
		assertToken(getToken("punctuation.definition.tag.end.html"), 44, 1);
	}

	@Test
	public void testHTML401StrictDoctype()
	{
		String src = "<!DOCTYPE PUBLIC '-//W3C//DTD HTML 4.01//EN' 'http://www.w3.org/TR/html4/strict.dtd'>";
		IDocument document = new Document(src);
		scanner.setRange(document, 0, src.length());

		assertToken(getToken("punctuation.definition.tag.begin.html"), 0, 1);
		assertToken(getToken(StringUtil.EMPTY), 1, 1);
		assertToken(getToken("entity.name.tag.doctype.html"), 2, 7);
		assertToken(Token.WHITESPACE, 9, 1);
		assertToken(getToken(StringUtil.EMPTY), 10, 6);
		assertToken(Token.WHITESPACE, 16, 1);
		assertToken(getToken("string.quoted.single.doctype.identifiers-and-DTDs.html"), 17, 27);
		assertToken(Token.WHITESPACE, 44, 1);
		assertToken(getToken("string.quoted.single.doctype.identifiers-and-DTDs.html"), 45, 39);
		assertToken(getToken("punctuation.definition.tag.end.html"), 84, 1);
	}

	@Test
	public void testXHTML1StrictDoctype()
	{
		String src = "<!DOCTYPE PUBLIC '-//W3C//DTD XHTML 1.0 Strict//EN' 'http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd'>";
		IDocument document = new Document(src);
		scanner.setRange(document, 0, src.length());

		assertToken(getToken("punctuation.definition.tag.begin.html"), 0, 1);
		assertToken(getToken(StringUtil.EMPTY), 1, 1);
		assertToken(getToken("entity.name.tag.doctype.html"), 2, 7);
		assertToken(Token.WHITESPACE, 9, 1);
		assertToken(getToken(StringUtil.EMPTY), 10, 6);
		assertToken(Token.WHITESPACE, 16, 1);
		assertToken(getToken("string.quoted.single.doctype.identifiers-and-DTDs.html"), 17, 34);
		assertToken(Token.WHITESPACE, 51, 1);
		assertToken(getToken("string.quoted.single.doctype.identifiers-and-DTDs.html"), 52, 51);
		assertToken(getToken("punctuation.definition.tag.end.html"), 103, 1);
	}

	@Test
	public void testXHTML11StrictDoctype()
	{
		String src = "<!DOCTYPE PUBLIC '-//W3C//DTD XHTML 1.1//EN' 'http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd'>";
		IDocument document = new Document(src);
		scanner.setRange(document, 0, src.length());

		assertToken(getToken("punctuation.definition.tag.begin.html"), 0, 1);
		assertToken(getToken(StringUtil.EMPTY), 1, 1);
		assertToken(getToken("entity.name.tag.doctype.html"), 2, 7);
		assertToken(Token.WHITESPACE, 9, 1);
		assertToken(getToken(StringUtil.EMPTY), 10, 6);
		assertToken(Token.WHITESPACE, 16, 1);
		assertToken(getToken("string.quoted.single.doctype.identifiers-and-DTDs.html"), 17, 27);
		assertToken(Token.WHITESPACE, 44, 1);
		assertToken(getToken("string.quoted.single.doctype.identifiers-and-DTDs.html"), 45, 46);
		assertToken(getToken("punctuation.definition.tag.end.html"), 91, 1);
	}

}
