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

public class XMLScannerTest extends AbstractTokenScannerTestCase
{

	@Override
	protected ITokenScanner createTokenScanner()
	{
		return new XMLScanner()
		{
			protected IToken createToken(String string)
			{
				return XMLScannerTest.this.getToken(string);
			};
		};
	}

	@Test
	public void testEntities()
	{
		String src = "&nbsp; & text &#123; &#xabc012; &#bad; &#xnotgood;";
		IDocument document = new Document(src);
		scanner.setRange(document, 0, src.length());

		assertToken(getToken("constant.character.entity.xml"), 0, 6);
		assertToken(Token.WHITESPACE, 6, 1);
		assertToken(getToken("invalid.illegal.bad-ampersand.xml"), 7, 1);
		assertToken(Token.WHITESPACE, 8, 1);
		assertToken(getToken("text"), 9, 4);
		assertToken(Token.WHITESPACE, 13, 1);
		assertToken(getToken("constant.character.entity.xml"), 14, 6);
		assertToken(Token.WHITESPACE, 20, 1);
		assertToken(getToken("constant.character.entity.xml"), 21, 10);
		assertToken(Token.WHITESPACE, 31, 1);
		assertToken(getToken("invalid.illegal.bad-ampersand.xml"), 32, 1);
		assertToken(getToken("text"), 33, 1);
		assertToken(getToken("text"), 34, 3);
		assertToken(getToken("text"), 37, 1);
		assertToken(Token.WHITESPACE, 38, 1);
		assertToken(getToken("invalid.illegal.bad-ampersand.xml"), 39, 1);
		assertToken(getToken("text"), 40, 1);
		assertToken(getToken("text"), 41, 8);
		assertToken(getToken("text"), 49, 1);
	}

	@Test
	public void testEntityAndNormalWordWithNoSpaceBetween()
	{
		String src = "good&nbsp;good";
		IDocument document = new Document(src);
		scanner.setRange(document, 0, src.length());

		assertToken(getToken("text"), 0, 4);
		assertToken(getToken("constant.character.entity.xml"), 4, 6);
		assertToken(getToken("text"), 10, 4);
	}
}
