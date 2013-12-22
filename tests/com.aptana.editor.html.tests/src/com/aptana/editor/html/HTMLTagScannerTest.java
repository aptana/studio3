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

	@Test
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

	@Test
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

	@Test
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

	@Test
	public void testWhitespaces()
	{
		String src = "<html attribute = \"chris\" >";
		IDocument document = new Document(src);
		scanner.setRange(document, 0, src.length());

		assertToken(getToken("punctuation.definition.tag.begin.html"), 0, 1);
		assertToken(getToken("entity.name.tag.structure.any.html"), 1, 4);
		assertToken(Token.WHITESPACE, 5, 1);
		assertToken(getToken("entity.other.attribute-name.html"), 6, 9);
		assertToken(Token.WHITESPACE, 15, 1);
		assertToken(getToken("punctuation.separator.key-value.html"), 16, 1);
		assertToken(Token.WHITESPACE, 17, 1);
		assertToken(getToken("string.quoted.double.html"), 18, 7);
		assertToken(Token.WHITESPACE, 25, 1);
		assertToken(getToken("punctuation.definition.tag.end.html"), 26, 1);
	}

	@Test
	public void testSelfClosingWhitespace()
	{
		String src = "<html attribute='nchris' />";
		IDocument document = new Document(src);
		scanner.setRange(document, 0, src.length());

		assertToken(getToken("punctuation.definition.tag.begin.html"), 0, 1);
		assertToken(getToken("entity.name.tag.structure.any.html"), 1, 4);
		assertToken(Token.WHITESPACE, 5, 1);
		assertToken(getToken("entity.other.attribute-name.html"), 6, 9);
		assertToken(getToken("punctuation.separator.key-value.html"), 15, 1);
		assertToken(getToken("string.quoted.single.html"), 16, 8);
		assertToken(Token.WHITESPACE, 24, 1);
		assertToken(getToken("punctuation.definition.tag.self_close.html"), 25, 2);
	}

	@Test
	public void testSelfClosing()
	{
		String src = "<html attribute='nchris'/>";
		IDocument document = new Document(src);
		scanner.setRange(document, 0, src.length());

		assertToken(getToken("punctuation.definition.tag.begin.html"), 0, 1);
		assertToken(getToken("entity.name.tag.structure.any.html"), 1, 4);
		assertToken(Token.WHITESPACE, 5, 1);
		assertToken(getToken("entity.other.attribute-name.html"), 6, 9);
		assertToken(getToken("punctuation.separator.key-value.html"), 15, 1);
		assertToken(getToken("string.quoted.single.html"), 16, 8);
		assertToken(getToken("punctuation.definition.tag.self_close.html"), 24, 2);
	}

	@Test
	public void testNoValueAttribute()
	{
		String src = "<html attribute='nchris' selected>";
		IDocument document = new Document(src);
		scanner.setRange(document, 0, src.length());

		assertToken(getToken("punctuation.definition.tag.begin.html"), 0, 1);
		assertToken(getToken("entity.name.tag.structure.any.html"), 1, 4);
		assertToken(Token.WHITESPACE, 5, 1);
		assertToken(getToken("entity.other.attribute-name.html"), 6, 9);
		assertToken(getToken("punctuation.separator.key-value.html"), 15, 1);
		assertToken(getToken("string.quoted.single.html"), 16, 8);
		assertToken(Token.WHITESPACE, 24, 1);
		assertToken(getToken("entity.other.attribute-name.html"), 25, 8);
		assertToken(getToken("punctuation.definition.tag.end.html"), 33, 1);
	}

	@Test
	public void testEmptyStyleAttribute()
	{
		String src = "<html style=''>";
		IDocument document = new Document(src);
		scanner.setRange(document, 0, src.length());

		assertToken(getToken("punctuation.definition.tag.begin.html"), 0, 1);
		assertToken(getToken("entity.name.tag.structure.any.html"), 1, 4);
		assertToken(Token.WHITESPACE, 5, 1);
		assertToken(getToken("entity.other.attribute-name.style.html"), 6, 5);
		assertToken(getToken("punctuation.separator.key-value.html"), 11, 1);
		assertToken(getToken("string.quoted.single.html"), 12, 2);
		assertToken(getToken("punctuation.definition.tag.end.html"), 14, 1);
	}

	@Test
	public void testStyleAttribute()
	{
		String src = "<html style='color:red'>";
		IDocument document = new Document(src);
		scanner.setRange(document, 0, src.length());

		assertToken(getToken("punctuation.definition.tag.begin.html"), 0, 1);
		assertToken(getToken("entity.name.tag.structure.any.html"), 1, 4);
		assertToken(Token.WHITESPACE, 5, 1);
		assertToken(getToken("entity.other.attribute-name.style.html"), 6, 5);
		assertToken(getToken("punctuation.separator.key-value.html"), 11, 1);
		assertToken(getToken("string.quoted.single.html"), 12, 1);
		assertToken(getToken("meta.property-name.css support.type.property-name.css"), 13, 5);
		assertToken(getToken("meta.property-value.css punctuation.separator.key-value.css"), 18, 1);
		assertToken(getToken("meta.property-value.css support.constant.color.w3c-standard-color-name.css"), 19, 3);
		assertToken(getToken("string.quoted.single.html"), 22, 1);
		assertToken(getToken("punctuation.definition.tag.end.html"), 23, 1);
	}

	@Test
	public void testEmptyScriptAttribute()
	{
		String src = "<body onload=''>";
		IDocument document = new Document(src);
		scanner.setRange(document, 0, src.length());

		assertToken(getToken("punctuation.definition.tag.begin.html"), 0, 1);
		assertToken(getToken("entity.name.tag.structure.any.html"), 1, 4);
		assertToken(Token.WHITESPACE, 5, 1);
		assertToken(getToken("entity.other.attribute-name.script.html"), 6, 6);
		assertToken(getToken("punctuation.separator.key-value.html"), 12, 1);
		assertToken(getToken("string.quoted.single.html"), 13, 2);
		assertToken(getToken("punctuation.definition.tag.end.html"), 15, 1);
	}

	@Test
	public void testScriptAttribute()
	{
		String src = "<body onclick='document.body'>";
		IDocument document = new Document(src);
		scanner.setRange(document, 0, src.length());

		assertToken(getToken("punctuation.definition.tag.begin.html"), 0, 1);
		assertToken(getToken("entity.name.tag.structure.any.html"), 1, 4);
		assertToken(Token.WHITESPACE, 5, 1);
		assertToken(getToken("entity.other.attribute-name.script.html"), 6, 7);
		assertToken(getToken("punctuation.separator.key-value.html"), 13, 1);
		assertToken(getToken("string.quoted.single.html"), 14, 1);
		assertToken(getToken("support.class.js"), 15, 8);
		assertToken(getToken("meta.delimiter.method.period.js"), 23, 1);
		assertToken(getToken("source.js"), 24, 4);
		assertToken(getToken("string.quoted.single.html"), 28, 1);
		assertToken(getToken("punctuation.definition.tag.end.html"), 29, 1);
	}

	@Test
	public void testIncompleteTag1()
	{
		String src = "<";
		IDocument document = new Document(src);
		scanner.setRange(document, 0, src.length());

		assertToken(getToken("punctuation.definition.tag.begin.html"), 0, 1);
	}

	@Test
	public void testIncompleteTag2()
	{
		String src = "<html";
		IDocument document = new Document(src);
		scanner.setRange(document, 0, src.length());

		assertToken(getToken("punctuation.definition.tag.begin.html"), 0, 1);
		assertToken(getToken("entity.name.tag.structure.any.html"), 1, 4);
	}

	@Test
	public void testIncompleteTagWithAttributeName()
	{
		String src = "<body class=";
		IDocument document = new Document(src);
		scanner.setRange(document, 0, src.length());

		assertToken(getToken("punctuation.definition.tag.begin.html"), 0, 1);
		assertToken(getToken("entity.name.tag.structure.any.html"), 1, 4);
		assertToken(Token.WHITESPACE, 5, 1);
		assertToken(getToken("entity.other.attribute-name.class.html"), 6, 5);
		assertToken(getToken("punctuation.separator.key-value.html"), 11, 1);
	}

	@Test
	public void testIncompleteTagWithIncompleteAttribute()
	{
		String src = "<body class='";
		IDocument document = new Document(src);
		scanner.setRange(document, 0, src.length());

		assertToken(getToken("punctuation.definition.tag.begin.html"), 0, 1);
		assertToken(getToken("entity.name.tag.structure.any.html"), 1, 4);
		assertToken(Token.WHITESPACE, 5, 1);
		assertToken(getToken("entity.other.attribute-name.class.html"), 6, 5);
		assertToken(getToken("punctuation.separator.key-value.html"), 11, 1);
		assertToken(getToken("string.quoted.single.html"), 12, 1);
	}

	@Test
	public void testIncompleteTagWithAttribute()
	{
		String src = "<body class=''";
		IDocument document = new Document(src);
		scanner.setRange(document, 0, src.length());

		assertToken(getToken("punctuation.definition.tag.begin.html"), 0, 1);
		assertToken(getToken("entity.name.tag.structure.any.html"), 1, 4);
		assertToken(Token.WHITESPACE, 5, 1);
		assertToken(getToken("entity.other.attribute-name.class.html"), 6, 5);
		assertToken(getToken("punctuation.separator.key-value.html"), 11, 1);
		assertToken(getToken("string.quoted.single.html"), 12, 2);
	}

	@Test
	public void testIncompleteCloseTag1()
	{
		String src = "</";
		IDocument document = new Document(src);
		scanner.setRange(document, 0, src.length());

		assertToken(getToken("punctuation.definition.tag.begin.html"), 0, 2);
	}

	@Test
	public void testIncompleteCloseTag2()
	{
		String src = "</html";
		IDocument document = new Document(src);
		scanner.setRange(document, 0, src.length());

		assertToken(getToken("punctuation.definition.tag.begin.html"), 0, 2);
		assertToken(getToken("entity.name.tag.structure.any.html"), 2, 4);
	}

	@Test
	public void testCompleteTag1()
	{
		String src = "<>";
		IDocument document = new Document(src);
		scanner.setRange(document, 0, src.length());

		assertToken(getToken("punctuation.definition.tag.begin.html"), 0, 1);
		assertToken(getToken("punctuation.definition.tag.end.html"), 1, 1);
	}

	@Test
	public void testCompleteCloseTag1()
	{
		String src = "</>";
		IDocument document = new Document(src);
		scanner.setRange(document, 0, src.length());

		assertToken(getToken("punctuation.definition.tag.begin.html"), 0, 2);
		assertToken(getToken("punctuation.definition.tag.end.html"), 2, 1);
	}

	@Test
	public void testCompleteTagWithAttribute()
	{
		String src = "< class=''>";
		IDocument document = new Document(src);
		scanner.setRange(document, 0, src.length());

		assertToken(getToken("punctuation.definition.tag.begin.html"), 0, 1);
		assertToken(Token.WHITESPACE, 1, 1);
		assertToken(getToken("entity.other.attribute-name.class.html"), 2, 5);
		assertToken(getToken("punctuation.separator.key-value.html"), 7, 1);
		assertToken(getToken("string.quoted.single.html"), 8, 2);
		assertToken(getToken("punctuation.definition.tag.end.html"), 10, 1);
	}

	@Test
	public void testCompleteTagWithIncompleteAttribute1()
	{
		String src = "< class='>";
		IDocument document = new Document(src);
		scanner.setRange(document, 0, src.length());

		assertToken(getToken("punctuation.definition.tag.begin.html"), 0, 1);
		assertToken(Token.WHITESPACE, 1, 1);
		assertToken(getToken("entity.other.attribute-name.class.html"), 2, 5);
		assertToken(getToken("punctuation.separator.key-value.html"), 7, 1);
		assertToken(getToken("string.quoted.single.html"), 8, 1);
		assertToken(getToken("punctuation.definition.tag.end.html"), 9, 1);
	}

	@Test
	public void testCompleteTagWithIncompleteAttribute2()
	{
		String src = "< class='/>";
		IDocument document = new Document(src);
		scanner.setRange(document, 0, src.length());

		assertToken(getToken("punctuation.definition.tag.begin.html"), 0, 1);
		assertToken(Token.WHITESPACE, 1, 1);
		assertToken(getToken("entity.other.attribute-name.class.html"), 2, 5);
		assertToken(getToken("punctuation.separator.key-value.html"), 7, 1);
		assertToken(getToken("string.quoted.single.html"), 8, 1);
		assertToken(getToken("punctuation.definition.tag.self_close.html"), 9, 2);
	}

	@Test
	public void testCompleteTagWithIncompleteAttribute3()
	{
		String src = "< class=' >";
		IDocument document = new Document(src);
		scanner.setRange(document, 0, src.length());

		assertToken(getToken("punctuation.definition.tag.begin.html"), 0, 1);
		assertToken(Token.WHITESPACE, 1, 1);
		assertToken(getToken("entity.other.attribute-name.class.html"), 2, 5);
		assertToken(getToken("punctuation.separator.key-value.html"), 7, 1);
		assertToken(getToken("string.quoted.single.html"), 8, 2);
		assertToken(getToken("punctuation.definition.tag.end.html"), 10, 1);
	}

	@Test
	public void testCompleteTagWithIncompleteAttribute4()
	{
		String src = "< class=' />";
		IDocument document = new Document(src);
		scanner.setRange(document, 0, src.length());

		assertToken(getToken("punctuation.definition.tag.begin.html"), 0, 1);
		assertToken(Token.WHITESPACE, 1, 1);
		assertToken(getToken("entity.other.attribute-name.class.html"), 2, 5);
		assertToken(getToken("punctuation.separator.key-value.html"), 7, 1);
		assertToken(getToken("string.quoted.single.html"), 8, 2);
		assertToken(getToken("punctuation.definition.tag.self_close.html"), 10, 2);
	}

}
