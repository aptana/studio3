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
import org.eclipse.jface.text.IDocumentPartitioner;
import org.eclipse.jface.text.rules.FastPartitioner;
import org.eclipse.jface.text.rules.ITokenScanner;
import org.eclipse.jface.text.rules.Token;
import org.junit.Test;

import com.aptana.editor.common.tests.AbstractTokenScannerTestCase;

public class CSSCodeScannerTest extends AbstractTokenScannerTestCase
{
	@SuppressWarnings("deprecation")
	@Override
	protected ITokenScanner createTokenScanner()
	{
		return new CSSCodeScannerRuleBased();
	}

	@Test
	public void testH1Through6()
	{
		String src = "h1 h2 h3 h4 h5 h6 ";
		IDocument document = new Document(src);
		scanner.setRange(document, 0, src.length());

		for (int i = 0; i < src.length(); i += 3)
		{
			assertToken(getToken("meta.selector.css entity.name.tag.css"), i, 2);
			assertToken(getToken("meta.selector.css"), i + 2, 1);
		}
	}

	@Test
	public void testNum()
	{
		String src = "10em;";
		IDocument document = new Document(src);
		scanner.setRange(document, 0, src.length());

		assertToken(getToken("constant.numeric.css"), 0, 2);
		assertToken(getToken("keyword.other.unit.css"), 2, 2);
		assertToken(getToken("punctuation.terminator.rule.css"), 4, 1);
	}

	@Test
	public void testImportant() throws Exception
	{
		String src = "!  impORtant what!impORtantwhat\n!impORtant\n!important something";
		IDocument document = new Document(src);
		scanner.setRange(document, 0, src.length());
		assertTokens("", "null", "source.css", "null", "source.css", "", "source.css", "null", "", "source.css",
				"null", "", "source.css", "null", "source.css", "null");
	}

	@Test
	public void testIdentifierWithKeyword()
	{
		String src = "table-row-group";
		IDocument document = new Document(src);
		scanner.setRange(document, 0, src.length());

		assertToken(getToken("source.css"), 0, 15);
	}

	@Test
	public void testBrowserSpecificPropertyNames2()
	{
		String src = "body {\n-moz-border-radius: 4px;\n" + "-webkit-border-radius: 4px";
		IDocument document = new Document(src);
		scanner.setRange(document, 0, src.length());
		assertTokens("meta.selector.css entity.name.tag.css", "meta.selector.css",
				"meta.property-list.css punctuation.section.property-list.css", "meta.property-list.css",
				"meta.property-list.css meta.property-name.css support.type.property-name.css",
				"meta.property-list.css meta.property-value.css punctuation.separator.key-value.css",
				"meta.property-list.css meta.property-value.css",
				"meta.property-list.css meta.property-value.css constant.numeric.css",
				"meta.property-list.css meta.property-value.css keyword.other.unit.css",
				"meta.property-list.css meta.property-value.css punctuation.terminator.rule.css",
				"meta.property-list.css",
				"meta.property-list.css meta.property-name.css support.type.property-name.css",
				"meta.property-list.css meta.property-value.css punctuation.separator.key-value.css",
				"meta.property-list.css meta.property-value.css",
				"meta.property-list.css meta.property-value.css constant.numeric.css",
				"meta.property-list.css meta.property-value.css keyword.other.unit.css", "null");
	}

	@Test
	public void testBrowserSpecificPropertyNames()
	{
		String src = "body {\n-moz-border-radius: 4px;\n" + "-webkit-border-radius: 4px";
		IDocument document = new Document(src);
		scanner.setRange(document, 7, src.length() - 7);

		assertToken(getToken("meta.property-list.css meta.property-name.css support.type.property-name.css"), 7, 18);
		assertToken(getToken("meta.property-list.css meta.property-value.css punctuation.separator.key-value.css"), 25,
				1);
		assertToken(getToken("meta.property-list.css meta.property-value.css"), 26, 1);
		assertToken(getToken("meta.property-list.css meta.property-value.css constant.numeric.css"), 27, 1);
		assertToken(getToken("meta.property-list.css meta.property-value.css keyword.other.unit.css"), 28, 2);
		assertToken(getToken("meta.property-list.css meta.property-value.css punctuation.terminator.rule.css"), 30, 1);
		assertToken(getToken("meta.property-list.css"), 31, 1);
		assertToken(getToken("meta.property-list.css meta.property-name.css support.type.property-name.css"), 32, 21);
		assertToken(getToken("meta.property-list.css meta.property-value.css punctuation.separator.key-value.css"), 53,
				1);
		assertToken(getToken("meta.property-list.css meta.property-value.css"), 54, 1);
		assertToken(getToken("meta.property-list.css meta.property-value.css constant.numeric.css"), 55, 1);
		assertToken(getToken("meta.property-list.css meta.property-value.css keyword.other.unit.css"), 56, 2);
	}

	@Test
	public void testURLFunctionArgWithNoString()
	{
		String src = "background: url(/images/blah_header.jpg)";
		IDocument document = new Document(src);
		scanner.setRange(document, 0, src.length());

		assertToken(getToken("meta.property-name.css support.type.property-name.css"), 0, 10);
		assertToken(getToken("meta.property-value.css punctuation.separator.key-value.css"), 10, 1);
		assertToken(getToken("meta.property-value.css"), 11, 1);
		assertToken(getToken("meta.property-value.css support.function.misc.css"), 12, 3);
		assertToken(getToken("meta.property-value.css punctuation.section.function.css"), 15, 1);
	}

	@Test
	public void testURLFunctionArgWithNoString2()
	{
		String src = "background: url(/images/blah_header.jpg)";
		IDocument document = new Document(src);
		scanner.setRange(document, 0, src.length());

		assertTokens("meta.property-name.css support.type.property-name.css",
				"meta.property-value.css punctuation.separator.key-value.css", "meta.property-value.css",
				"meta.property-value.css support.function.misc.css",
				"meta.property-value.css punctuation.section.function.css",
				"meta.property-value.css punctuation.slash.css", "meta.property-value.css source.css",
				"meta.property-value.css punctuation.slash.css", "meta.property-value.css source.css",
				"meta.selector.css meta.property-value.css entity.other.attribute-name.class.css",
				"meta.selector.css meta.property-value.css punctuation.section.function.css", "null");
	}

	@Test
	public void testSmallCaps()
	{
		String src = "small { font: small-caps; }";
		IDocument document = new Document(src);
		scanner.setRange(document, 0, src.length());

		assertToken(getToken("meta.selector.css entity.name.tag.css"), 0, 5);
		assertToken(getToken("meta.selector.css"), 5, 1);
		assertToken(getToken("meta.property-list.css punctuation.section.property-list.css"), 6, 1);
		assertToken(getToken("meta.property-list.css"), 7, 1);
		assertToken(getToken("meta.property-list.css meta.property-name.css support.type.property-name.css"), 8, 4);
		assertToken(getToken("meta.property-list.css meta.property-value.css punctuation.separator.key-value.css"), 12,
				1);
		assertToken(getToken("meta.property-list.css meta.property-value.css"), 13, 1);
		assertToken(getToken("meta.property-list.css meta.property-value.css support.constant.property-value.css"), 14,
				10);
		assertToken(getToken("meta.property-list.css meta.property-value.css punctuation.terminator.rule.css"), 24, 1);
		assertToken(getToken("meta.property-list.css"), 25, 1);
		assertToken(getToken("meta.property-list.css punctuation.section.property-list.css"), 26, 1);
	}

	@Test
	public void testSmallCaps2()
	{
		String src = "background: url(/images/blah_header.jpg)";
		IDocument document = new Document(src);
		scanner.setRange(document, 0, src.length());

		assertTokens("meta.property-name.css support.type.property-name.css",
				"meta.property-value.css punctuation.separator.key-value.css", "meta.property-value.css",
				"meta.property-value.css support.function.misc.css",
				"meta.property-value.css punctuation.section.function.css",
				"meta.property-value.css punctuation.slash.css", "meta.property-value.css source.css",
				"meta.property-value.css punctuation.slash.css", "meta.property-value.css source.css",
				"meta.selector.css meta.property-value.css entity.other.attribute-name.class.css",
				"meta.selector.css meta.property-value.css punctuation.section.function.css", "null");
	}

	@Test
	public void testCSSEmTag()
	{
		// the preceding elements are to make sure "em" does not corrupt the rest of tokenizing
		String src = "textarea.JScript, textarea.HTML {height:10em;}";
		IDocument document = new Document(src);
		scanner.setRange(document, 0, src.length());

		assertToken(getToken("meta.selector.css entity.name.tag.css"), 0, 8);
		assertToken(getToken("meta.selector.css entity.other.attribute-name.class.css"), 8, 8);
		assertToken(getToken("meta.selector.css punctuation.separator.css"), 16, 1);
		assertToken(getToken("meta.selector.css"), 17, 1);
		assertToken(getToken("meta.selector.css entity.name.tag.css"), 18, 8);
		assertToken(getToken("meta.selector.css entity.other.attribute-name.class.css"), 26, 5);
		assertToken(getToken("meta.selector.css"), 31, 1);
		assertToken(getToken("meta.property-list.css punctuation.section.property-list.css"), 32, 1);
		assertToken(getToken("meta.property-list.css meta.property-name.css support.type.property-name.css"), 33, 6);
		assertToken(getToken("meta.property-list.css meta.property-value.css punctuation.separator.key-value.css"), 39,
				1);
		assertToken(getToken("meta.property-list.css meta.property-value.css constant.numeric.css"), 40, 2);
		assertToken(getToken("meta.property-list.css meta.property-value.css keyword.other.unit.css"), 42, 2); // "em"
		assertToken(getToken("meta.property-list.css meta.property-value.css punctuation.terminator.rule.css"), 44, 1);
		assertToken(getToken("meta.property-list.css punctuation.section.property-list.css"), 45, 1);
	}

	@Test
	public void testCSSEmTag2()
	{
		// the preceding elements are to make sure "em" does not corrupt the rest of tokenizing
		String src = "textarea.JScript, textarea.HTML {height:10em;}";
		IDocument document = new Document(src);
		scanner.setRange(document, 0, src.length());
		assertTokens("meta.selector.css entity.name.tag.css",
				"meta.selector.css entity.other.attribute-name.class.css",
				"meta.selector.css punctuation.separator.css", "meta.selector.css",
				"meta.selector.css entity.name.tag.css", "meta.selector.css entity.other.attribute-name.class.css",
				"meta.selector.css", "meta.property-list.css punctuation.section.property-list.css",
				"meta.property-list.css meta.property-name.css support.type.property-name.css",
				"meta.property-list.css meta.property-value.css punctuation.separator.key-value.css",
				"meta.property-list.css meta.property-value.css constant.numeric.css",
				"meta.property-list.css meta.property-value.css keyword.other.unit.css",
				"meta.property-list.css meta.property-value.css punctuation.terminator.rule.css",
				"meta.property-list.css punctuation.section.property-list.css", "null");
	}

	@Test
	public void testBasicTokenizing()
	{
		String src = "html { color: red; background-color: #333; }";
		IDocument document = new Document(src);
		scanner.setRange(document, 0, src.length());

		assertToken(getToken("meta.selector.css entity.name.tag.css"), 0, 4);
		assertToken(getToken("meta.selector.css"), 4, 1);
		assertToken(getToken("meta.property-list.css punctuation.section.property-list.css"), 5, 1);
		assertToken(getToken("meta.property-list.css"), 6, 1);
		assertToken(getToken("meta.property-list.css meta.property-name.css support.type.property-name.css"), 7, 5);
		assertToken(getToken("meta.property-list.css meta.property-value.css punctuation.separator.key-value.css"), 12,
				1);
		assertToken(getToken("meta.property-list.css meta.property-value.css"), 13, 1);
		assertToken(
				getToken("meta.property-list.css meta.property-value.css support.constant.color.w3c-standard-color-name.css"),
				14, 3);
		assertToken(getToken("meta.property-list.css meta.property-value.css punctuation.terminator.rule.css"), 17, 1);
		assertToken(getToken("meta.property-list.css"), 18, 1);
		assertToken(getToken("meta.property-list.css meta.property-name.css support.type.property-name.css"), 19, 16);
		assertToken(getToken("meta.property-list.css meta.property-value.css punctuation.separator.key-value.css"), 35,
				1);
		assertToken(getToken("meta.property-list.css meta.property-value.css"), 36, 1);
		assertToken(getToken("meta.property-list.css meta.property-value.css constant.other.color.rgb-value.css"), 37,
				4);
		assertToken(getToken("meta.property-list.css meta.property-value.css punctuation.terminator.rule.css"), 41, 1);
		assertToken(getToken("meta.property-list.css"), 42, 1);
		assertToken(getToken("meta.property-list.css punctuation.section.property-list.css"), 43, 1);
	}

	@Test
	public void testBasicTokenizing3()
	{
		String src = "html { color: red; background-color: #333; }";
		IDocument document = new Document(src);
		scanner.setRange(document, 0, src.length());

		assertTokens("meta.selector.css entity.name.tag.css", "meta.selector.css",
				"meta.property-list.css punctuation.section.property-list.css", "meta.property-list.css",
				"meta.property-list.css meta.property-name.css support.type.property-name.css",
				"meta.property-list.css meta.property-value.css punctuation.separator.key-value.css",
				"meta.property-list.css meta.property-value.css",
				"meta.property-list.css meta.property-value.css support.constant.color.w3c-standard-color-name.css",
				"meta.property-list.css meta.property-value.css punctuation.terminator.rule.css",
				"meta.property-list.css",
				"meta.property-list.css meta.property-name.css support.type.property-name.css",
				"meta.property-list.css meta.property-value.css punctuation.separator.key-value.css",
				"meta.property-list.css meta.property-value.css",
				"meta.property-list.css meta.property-value.css constant.other.color.rgb-value.css",
				"meta.property-list.css meta.property-value.css punctuation.terminator.rule.css",
				"meta.property-list.css", "meta.property-list.css punctuation.section.property-list.css", "null");
	}

	@Test
	public void testBasicTokenizing4()
	{
		String src = "body {\n" + // 1
				"  background-image: url();\n" + // 2
				"  background-position-x: left;\n" + // 3
				"  background-position-y: top;\n" + // 4
				"  background-repeat: repeat-x;\n" + // 5
				"  font-family: Verdana, Geneva, Arial, Helvetica, sans-serif;\n" + // 6
				"}\n" + // 7
				"\n" + // 8
				".main {\n" + // 9
				"  border: 1px dotted #222222;\n" + // 10
				"  margin: 5px;\n" + // 11
				"}\n" + // 12
				"\n" + // 13
				".header {\n" + // 14
				"  background-color: #FFFFFF;\n" + // 15
				"  color: #444444;\n" + // 16
				"  font-size: xx-large;\n" + // 17
				"}\n" + // 18
				"\n" + // 19
				".menu {\n" + // 20
				"  border-top: 2px solid #FC7F22;\n" + // 21
				"  background-color: #3B3B3B;\n" + // 22
				"  color: #FFFFFF;\n" + // 23
				"  text-align: right;\n" + // 24
				"  vertical-align: right;\n" + // 25
				"  font-size: small;\n" + // 26
				"}\n" + // 27
				"\n" + // 28
				".menu a {\n" + // 29
				"  color: #DDDDDD;\n" + // 30
				"  text-decoration: none;\n" + // 31
				"}\n"; // 32
		IDocument document = new Document(src);
		scanner.setRange(document, 0, src.length());

		assertTokens("meta.selector.css entity.name.tag.css", "meta.selector.css",
				"meta.property-list.css punctuation.section.property-list.css", "meta.property-list.css",
				"meta.property-list.css meta.property-name.css support.type.property-name.css",
				"meta.property-list.css meta.property-value.css punctuation.separator.key-value.css",
				"meta.property-list.css meta.property-value.css",
				"meta.property-list.css meta.property-value.css support.function.misc.css",
				"meta.property-list.css meta.property-value.css punctuation.section.function.css",
				"meta.property-list.css meta.property-value.css punctuation.section.function.css",
				"meta.property-list.css meta.property-value.css punctuation.terminator.rule.css",
				"meta.property-list.css",
				"meta.property-list.css meta.property-name.css support.type.property-name.css",
				"meta.property-list.css meta.property-value.css punctuation.separator.key-value.css",
				"meta.property-list.css meta.property-value.css",
				"meta.property-list.css meta.property-value.css support.constant.property-value.css",
				"meta.property-list.css meta.property-value.css punctuation.terminator.rule.css",
				"meta.property-list.css",
				"meta.property-list.css meta.property-name.css support.type.property-name.css",
				"meta.property-list.css meta.property-value.css punctuation.separator.key-value.css",
				"meta.property-list.css meta.property-value.css",
				"meta.property-list.css meta.property-value.css support.constant.property-value.css",
				"meta.property-list.css meta.property-value.css punctuation.terminator.rule.css",
				"meta.property-list.css",
				"meta.property-list.css meta.property-name.css support.type.property-name.css",
				"meta.property-list.css meta.property-value.css punctuation.separator.key-value.css",
				"meta.property-list.css meta.property-value.css",
				"meta.property-list.css meta.property-value.css support.constant.property-value.css",
				"meta.property-list.css meta.property-value.css punctuation.terminator.rule.css",
				"meta.property-list.css",
				"meta.property-list.css meta.property-name.css support.type.property-name.css",
				"meta.property-list.css meta.property-value.css punctuation.separator.key-value.css",
				"meta.property-list.css meta.property-value.css",
				"meta.property-list.css meta.property-value.css support.constant.font-name.css",
				"meta.property-list.css meta.property-value.css punctuation.separator.css",
				"meta.property-list.css meta.property-value.css",
				"meta.property-list.css meta.property-value.css support.constant.font-name.css",
				"meta.property-list.css meta.property-value.css punctuation.separator.css",
				"meta.property-list.css meta.property-value.css",
				"meta.property-list.css meta.property-value.css support.constant.font-name.css",
				"meta.property-list.css meta.property-value.css punctuation.separator.css",
				"meta.property-list.css meta.property-value.css",
				"meta.property-list.css meta.property-value.css support.constant.font-name.css",
				"meta.property-list.css meta.property-value.css punctuation.separator.css",
				"meta.property-list.css meta.property-value.css",
				"meta.property-list.css meta.property-value.css support.constant.font-name.css",
				"meta.property-list.css meta.property-value.css punctuation.terminator.rule.css",
				"meta.property-list.css", "meta.property-list.css punctuation.section.property-list.css", "null",
				"meta.selector.css entity.other.attribute-name.class.css", "meta.selector.css",
				"meta.property-list.css punctuation.section.property-list.css", "meta.property-list.css",
				"meta.property-list.css meta.property-name.css support.type.property-name.css",
				"meta.property-list.css meta.property-value.css punctuation.separator.key-value.css",
				"meta.property-list.css meta.property-value.css",
				"meta.property-list.css meta.property-value.css constant.numeric.css",
				"meta.property-list.css meta.property-value.css keyword.other.unit.css",
				"meta.property-list.css meta.property-value.css",
				"meta.property-list.css meta.property-value.css support.constant.property-value.css",
				"meta.property-list.css meta.property-value.css",
				"meta.property-list.css meta.property-value.css constant.other.color.rgb-value.css",
				"meta.property-list.css meta.property-value.css punctuation.terminator.rule.css",
				"meta.property-list.css",
				"meta.property-list.css meta.property-name.css support.type.property-name.css",
				"meta.property-list.css meta.property-value.css punctuation.separator.key-value.css",
				"meta.property-list.css meta.property-value.css",
				"meta.property-list.css meta.property-value.css constant.numeric.css",
				"meta.property-list.css meta.property-value.css keyword.other.unit.css",
				"meta.property-list.css meta.property-value.css punctuation.terminator.rule.css",
				"meta.property-list.css", "meta.property-list.css punctuation.section.property-list.css", "null",
				"meta.selector.css entity.other.attribute-name.class.css", "meta.selector.css",
				"meta.property-list.css punctuation.section.property-list.css", "meta.property-list.css",
				"meta.property-list.css meta.property-name.css support.type.property-name.css",
				"meta.property-list.css meta.property-value.css punctuation.separator.key-value.css",
				"meta.property-list.css meta.property-value.css",
				"meta.property-list.css meta.property-value.css constant.other.color.rgb-value.css",
				"meta.property-list.css meta.property-value.css punctuation.terminator.rule.css",
				"meta.property-list.css",
				"meta.property-list.css meta.property-name.css support.type.property-name.css",
				"meta.property-list.css meta.property-value.css punctuation.separator.key-value.css",
				"meta.property-list.css meta.property-value.css",
				"meta.property-list.css meta.property-value.css constant.other.color.rgb-value.css",
				"meta.property-list.css meta.property-value.css punctuation.terminator.rule.css",
				"meta.property-list.css",
				"meta.property-list.css meta.property-name.css support.type.property-name.css",
				"meta.property-list.css meta.property-value.css punctuation.separator.key-value.css",
				"meta.property-list.css meta.property-value.css",
				"meta.property-list.css meta.property-value.css support.constant.property-value.css",
				"meta.property-list.css meta.property-value.css punctuation.terminator.rule.css",
				"meta.property-list.css", "meta.property-list.css punctuation.section.property-list.css", "null",
				"meta.selector.css entity.other.attribute-name.class.css", "meta.selector.css",
				"meta.property-list.css punctuation.section.property-list.css", "meta.property-list.css",
				"meta.property-list.css meta.property-name.css support.type.property-name.css",
				"meta.property-list.css meta.property-value.css punctuation.separator.key-value.css",
				"meta.property-list.css meta.property-value.css",
				"meta.property-list.css meta.property-value.css constant.numeric.css",
				"meta.property-list.css meta.property-value.css keyword.other.unit.css",
				"meta.property-list.css meta.property-value.css",
				"meta.property-list.css meta.property-value.css support.constant.property-value.css",
				"meta.property-list.css meta.property-value.css",
				"meta.property-list.css meta.property-value.css constant.other.color.rgb-value.css",
				"meta.property-list.css meta.property-value.css punctuation.terminator.rule.css",
				"meta.property-list.css",
				"meta.property-list.css meta.property-name.css support.type.property-name.css",
				"meta.property-list.css meta.property-value.css punctuation.separator.key-value.css",
				"meta.property-list.css meta.property-value.css",
				"meta.property-list.css meta.property-value.css constant.other.color.rgb-value.css",
				"meta.property-list.css meta.property-value.css punctuation.terminator.rule.css",
				"meta.property-list.css",
				"meta.property-list.css meta.property-name.css support.type.property-name.css",
				"meta.property-list.css meta.property-value.css punctuation.separator.key-value.css",
				"meta.property-list.css meta.property-value.css",
				"meta.property-list.css meta.property-value.css constant.other.color.rgb-value.css",
				"meta.property-list.css meta.property-value.css punctuation.terminator.rule.css",
				"meta.property-list.css",
				"meta.property-list.css meta.property-name.css support.type.property-name.css",
				"meta.property-list.css meta.property-value.css punctuation.separator.key-value.css",
				"meta.property-list.css meta.property-value.css",
				"meta.property-list.css meta.property-value.css support.constant.property-value.css",
				"meta.property-list.css meta.property-value.css punctuation.terminator.rule.css",
				"meta.property-list.css",
				"meta.property-list.css meta.property-name.css support.type.property-name.css",
				"meta.property-list.css meta.property-value.css punctuation.separator.key-value.css",
				"meta.property-list.css meta.property-value.css",
				"meta.property-list.css meta.property-value.css support.constant.property-value.css",
				"meta.property-list.css meta.property-value.css punctuation.terminator.rule.css",
				"meta.property-list.css",
				"meta.property-list.css meta.property-name.css support.type.property-name.css",
				"meta.property-list.css meta.property-value.css punctuation.separator.key-value.css",
				"meta.property-list.css meta.property-value.css",
				"meta.property-list.css meta.property-value.css entity.name.tag.css",
				"meta.property-list.css meta.property-value.css punctuation.terminator.rule.css",
				"meta.property-list.css", "meta.property-list.css punctuation.section.property-list.css",
				"meta.selector.css", "meta.selector.css entity.other.attribute-name.class.css", "meta.selector.css",
				"meta.selector.css entity.name.tag.css", "meta.selector.css",
				"meta.property-list.css punctuation.section.property-list.css", "meta.property-list.css",
				"meta.property-list.css meta.property-name.css support.type.property-name.css",
				"meta.property-list.css meta.property-value.css punctuation.separator.key-value.css",
				"meta.property-list.css meta.property-value.css",
				"meta.property-list.css meta.property-value.css constant.other.color.rgb-value.css",
				"meta.property-list.css meta.property-value.css punctuation.terminator.rule.css",
				"meta.property-list.css",
				"meta.property-list.css meta.property-name.css support.type.property-name.css",
				"meta.property-list.css meta.property-value.css punctuation.separator.key-value.css",
				"meta.property-list.css meta.property-value.css",
				"meta.property-list.css meta.property-value.css support.constant.property-value.css",
				"meta.property-list.css meta.property-value.css punctuation.terminator.rule.css",
				"meta.property-list.css", "meta.property-list.css punctuation.section.property-list.css", "null",
				"null");
	}

	@Test
	public void testBasicTokenizing2()
	{
		String src = "body {\n" + // 1
				"  background-image: url();\n" + // 2
				"  background-position-x: left;\n" + // 3
				"  background-position-y: top;\n" + // 4
				"  background-repeat: repeat-x;\n" + // 5
				"  font-family: Verdana, Geneva, Arial, Helvetica, sans-serif;\n" + // 6
				"}\n" + // 7
				"\n" + // 8
				".main {\n" + // 9
				"  border: 1px dotted #222222;\n" + // 10
				"  margin: 5px;\n" + // 11
				"}\n" + // 12
				"\n" + // 13
				".header {\n" + // 14
				"  background-color: #FFFFFF;\n" + // 15
				"  color: #444444;\n" + // 16
				"  font-size: xx-large;\n" + // 17
				"}\n" + // 18
				"\n" + // 19
				".menu {\n" + // 20
				"  border-top: 2px solid #FC7F22;\n" + // 21
				"  background-color: #3B3B3B;\n" + // 22
				"  color: #FFFFFF;\n" + // 23
				"  text-align: right;\n" + // 24
				"  vertical-align: right;\n" + // 25
				"  font-size: small;\n" + // 26
				"}\n" + // 27
				"\n" + // 28
				".menu a {\n" + // 29
				"  color: #DDDDDD;\n" + // 30
				"  text-decoration: none;\n" + // 31
				"}\n"; // 32
		IDocument document = new Document(src);
		scanner.setRange(document, 0, src.length());
		// line 1
		assertToken(getToken("meta.selector.css entity.name.tag.css"), 0, 4);
		assertToken(getToken("meta.selector.css"), 4, 1);
		assertToken(getToken("meta.property-list.css punctuation.section.property-list.css"), 5, 1);
		assertToken(getToken("meta.property-list.css"), 6, 3);
		// line 2
		assertToken(getToken("meta.property-list.css meta.property-name.css support.type.property-name.css"), 9, 16);
		assertToken(getToken("meta.property-list.css meta.property-value.css punctuation.separator.key-value.css"), 25,
				1);
		assertToken(getToken("meta.property-list.css meta.property-value.css"), 26, 1);
		assertToken(getToken("meta.property-list.css meta.property-value.css support.function.misc.css"), 27, 3);
		assertToken(getToken("meta.property-list.css meta.property-value.css punctuation.section.function.css"), 30, 1);
		assertToken(getToken("meta.property-list.css meta.property-value.css punctuation.section.function.css"), 31, 1);
		assertToken(getToken("meta.property-list.css meta.property-value.css punctuation.terminator.rule.css"), 32, 1);
		assertToken(getToken("meta.property-list.css"), 33, 3);
		// line 3
		assertToken(getToken("meta.property-list.css meta.property-name.css support.type.property-name.css"), 36, 21);
		assertToken(getToken("meta.property-list.css meta.property-value.css punctuation.separator.key-value.css"), 57,
				1);
		assertToken(getToken("meta.property-list.css meta.property-value.css"), 58, 1);
		assertToken(getToken("meta.property-list.css meta.property-value.css support.constant.property-value.css"), 59,
				4);
		assertToken(getToken("meta.property-list.css meta.property-value.css punctuation.terminator.rule.css"), 63, 1);
		assertToken(getToken("meta.property-list.css"), 64, 3);
		// line 4
		assertToken(getToken("meta.property-list.css meta.property-name.css support.type.property-name.css"), 67, 21);
		assertToken(getToken("meta.property-list.css meta.property-value.css punctuation.separator.key-value.css"), 88,
				1);
		assertToken(getToken("meta.property-list.css meta.property-value.css"), 89, 1);
		assertToken(getToken("meta.property-list.css meta.property-value.css support.constant.property-value.css"), 90,
				3);
		assertToken(getToken("meta.property-list.css meta.property-value.css punctuation.terminator.rule.css"), 93, 1);
		assertToken(getToken("meta.property-list.css"), 94, 3);
		// line 5
		assertToken(getToken("meta.property-list.css meta.property-name.css support.type.property-name.css"), 97, 17);
		assertToken(getToken("meta.property-list.css meta.property-value.css punctuation.separator.key-value.css"),
				114, 1);
		assertToken(getToken("meta.property-list.css meta.property-value.css"), 115, 1);
		assertToken(getToken("meta.property-list.css meta.property-value.css support.constant.property-value.css"),
				116, 8);
		assertToken(getToken("meta.property-list.css meta.property-value.css punctuation.terminator.rule.css"), 124, 1);
		assertToken(getToken("meta.property-list.css"), 125, 3);
		// line 6
		assertToken(getToken("meta.property-list.css meta.property-name.css support.type.property-name.css"), 128, 11);
		assertToken(getToken("meta.property-list.css meta.property-value.css punctuation.separator.key-value.css"),
				139, 1);
		assertToken(getToken("meta.property-list.css meta.property-value.css"), 140, 1);
		assertToken(getToken("meta.property-list.css meta.property-value.css support.constant.font-name.css"), 141, 7);
		assertToken(getToken("meta.property-list.css meta.property-value.css punctuation.separator.css"), 148, 1);
		assertToken(getToken("meta.property-list.css meta.property-value.css"), 149, 1);
		assertToken(getToken("meta.property-list.css meta.property-value.css support.constant.font-name.css"), 150, 6);
		assertToken(getToken("meta.property-list.css meta.property-value.css punctuation.separator.css"), 156, 1);
		assertToken(getToken("meta.property-list.css meta.property-value.css"), 157, 1);
		assertToken(getToken("meta.property-list.css meta.property-value.css support.constant.font-name.css"), 158, 5);
		assertToken(getToken("meta.property-list.css meta.property-value.css punctuation.separator.css"), 163, 1);
		assertToken(getToken("meta.property-list.css meta.property-value.css"), 164, 1);
		assertToken(getToken("meta.property-list.css meta.property-value.css support.constant.font-name.css"), 165, 9);
		assertToken(getToken("meta.property-list.css meta.property-value.css punctuation.separator.css"), 174, 1);
		assertToken(getToken("meta.property-list.css meta.property-value.css"), 175, 1);
		assertToken(getToken("meta.property-list.css meta.property-value.css support.constant.font-name.css"), 176, 10);
		assertToken(getToken("meta.property-list.css meta.property-value.css punctuation.terminator.rule.css"), 186, 1);
		assertToken(getToken("meta.property-list.css"), 187, 1);
		// line 7
		assertToken(getToken("meta.property-list.css punctuation.section.property-list.css"), 188, 1);
		assertToken(Token.WHITESPACE, 189, 2);
		// line 9
		assertToken(getToken("meta.selector.css entity.other.attribute-name.class.css"), 191, 5);
		assertToken(getToken("meta.selector.css"), 196, 1);
		assertToken(getToken("meta.property-list.css punctuation.section.property-list.css"), 197, 1);
		assertToken(getToken("meta.property-list.css"), 198, 3);
		// line 10 border: 1px dotted #222222;
		assertToken(getToken("meta.property-list.css meta.property-name.css support.type.property-name.css"), 201, 6);
		assertToken(getToken("meta.property-list.css meta.property-value.css punctuation.separator.key-value.css"),
				207, 1);
		assertToken(getToken("meta.property-list.css meta.property-value.css"), 208, 1);
		assertToken(getToken("meta.property-list.css meta.property-value.css constant.numeric.css"), 209, 1);
		assertToken(getToken("meta.property-list.css meta.property-value.css keyword.other.unit.css"), 210, 2);
		assertToken(getToken("meta.property-list.css meta.property-value.css"), 212, 1);
		assertToken(getToken("meta.property-list.css meta.property-value.css support.constant.property-value.css"),
				213, 6);
		assertToken(getToken("meta.property-list.css meta.property-value.css"), 219, 1);
		assertToken(getToken("meta.property-list.css meta.property-value.css constant.other.color.rgb-value.css"), 220,
				7);
		assertToken(getToken("meta.property-list.css meta.property-value.css punctuation.terminator.rule.css"), 227, 1);
		assertToken(getToken("meta.property-list.css"), 228, 3);
		// line 11 margin: 5px;
		assertToken(getToken("meta.property-list.css meta.property-name.css support.type.property-name.css"), 231, 6);
		assertToken(getToken("meta.property-list.css meta.property-value.css punctuation.separator.key-value.css"),
				237, 1);
		assertToken(getToken("meta.property-list.css meta.property-value.css"), 238, 1);
		assertToken(getToken("meta.property-list.css meta.property-value.css constant.numeric.css"), 239, 1);
		assertToken(getToken("meta.property-list.css meta.property-value.css keyword.other.unit.css"), 240, 2);
		assertToken(getToken("meta.property-list.css meta.property-value.css punctuation.terminator.rule.css"), 242, 1);
		assertToken(getToken("meta.property-list.css"), 243, 1);
		// line 12
		assertToken(getToken("meta.property-list.css punctuation.section.property-list.css"), 244, 1);
		assertToken(Token.WHITESPACE, 245, 2);
		// line 13 .header {
		assertToken(getToken("meta.selector.css entity.other.attribute-name.class.css"), 247, 7);
		assertToken(getToken("meta.selector.css"), 254, 1);
		assertToken(getToken("meta.property-list.css punctuation.section.property-list.css"), 255, 1);
		assertToken(getToken("meta.property-list.css"), 256, 3);
		// line 14 background-color: #FFFFFF;
		assertToken(getToken("meta.property-list.css meta.property-name.css support.type.property-name.css"), 259, 16);
		assertToken(getToken("meta.property-list.css meta.property-value.css punctuation.separator.key-value.css"),
				275, 1);
		assertToken(getToken("meta.property-list.css meta.property-value.css"), 276, 1);
		assertToken(getToken("meta.property-list.css meta.property-value.css constant.other.color.rgb-value.css"), 277,
				7);
		assertToken(getToken("meta.property-list.css meta.property-value.css punctuation.terminator.rule.css"), 284, 1);
		assertToken(getToken("meta.property-list.css"), 285, 3);
		// line 15 color: #444444;
		assertToken(getToken("meta.property-list.css meta.property-name.css support.type.property-name.css"), 288, 5);
		assertToken(getToken("meta.property-list.css meta.property-value.css punctuation.separator.key-value.css"),
				293, 1);
		assertToken(getToken("meta.property-list.css meta.property-value.css"), 294, 1);
		assertToken(getToken("meta.property-list.css meta.property-value.css constant.other.color.rgb-value.css"), 295,
				7);
		assertToken(getToken("meta.property-list.css meta.property-value.css punctuation.terminator.rule.css"), 302, 1);
		assertToken(getToken("meta.property-list.css"), 303, 3);
		// line 16 font-size: xx-large;
		assertToken(getToken("meta.property-list.css meta.property-name.css support.type.property-name.css"), 306, 9);
		assertToken(getToken("meta.property-list.css meta.property-value.css punctuation.separator.key-value.css"),
				315, 1);
		assertToken(getToken("meta.property-list.css meta.property-value.css"), 316, 1);
		assertToken(getToken("meta.property-list.css meta.property-value.css support.constant.property-value.css"),
				317, 8);
		assertToken(getToken("meta.property-list.css meta.property-value.css punctuation.terminator.rule.css"), 325, 1);
		assertToken(getToken("meta.property-list.css"), 326, 1);
		// line 17 }
		assertToken(getToken("meta.property-list.css punctuation.section.property-list.css"), 327, 1);
		assertToken(Token.WHITESPACE, 328, 2);
		// line 19 .menu {
		assertToken(getToken("meta.selector.css entity.other.attribute-name.class.css"), 330, 5);
		assertToken(getToken("meta.selector.css"), 335, 1);
		assertToken(getToken("meta.property-list.css punctuation.section.property-list.css"), 336, 1);
		assertToken(getToken("meta.property-list.css"), 337, 3);
		// line 20
	}

	@Test
	public void testMediaWithRules()
	{
		String src = "@media screen {\n" + //
				"  * { font-family: sans-serif }\n" + //
				"}\n" + //
				"body { } "; //
		IDocument document = new Document(src);
		scanner.setRange(document, 0, src.length());

		assertToken(getToken("meta.at-rule.media.css keyword.control.at-rule.media.css"), 0, 6); // @media
		assertToken(getToken("meta.at-rule.media.css"), 6, 1);
		assertToken(getToken("meta.at-rule.media.css support.constant.media.css"), 7, 6); // screen
		assertToken(getToken("meta.at-rule.media.css"), 13, 1);
		assertToken(getToken("meta.at-rule.media.css punctuation.section.at-rule.media.css"), 14, 1); // {
		assertToken(getToken("meta.at-rule.media.css"), 15, 3);
		assertToken(getToken("meta.at-rule.media.css meta.selector.css entity.name.tag.wildcard.css"), 18, 1); // *
		assertToken(getToken("meta.at-rule.media.css meta.selector.css"), 19, 1);
		assertToken(getToken("meta.at-rule.media.css meta.property-list.css punctuation.section.property-list.css"),
				20, 1); // {
		assertToken(getToken("meta.at-rule.media.css meta.property-list.css"), 21, 1);
		// font-family
		assertToken(
				getToken("meta.at-rule.media.css meta.property-list.css meta.property-name.css support.type.property-name.css"),
				22, 11);
		assertToken(
				getToken("meta.at-rule.media.css meta.property-list.css meta.property-value.css punctuation.separator.key-value.css"),
				33, 1);
		assertToken(getToken("meta.at-rule.media.css meta.property-list.css meta.property-value.css"), 34, 1);
		// sans-serif
		assertToken(
				getToken("meta.at-rule.media.css meta.property-list.css meta.property-value.css support.constant.font-name.css"),
				35, 10);
		assertToken(getToken("meta.at-rule.media.css meta.property-list.css meta.property-value.css"), 45, 1);
		assertToken(getToken("meta.at-rule.media.css meta.property-list.css punctuation.section.property-list.css"),
				46, 1); // }
		assertToken(getToken("meta.at-rule.media.css"), 47, 1);
		assertToken(getToken("meta.at-rule.media.css punctuation.section.at-rule.media.css"), 48, 1); // }
		assertToken(Token.WHITESPACE, 49, 1);
		// body
		assertToken(getToken("meta.selector.css entity.name.tag.css"), 50, 4);
		assertToken(getToken("meta.selector.css"), 54, 1);
		assertToken(getToken("meta.property-list.css punctuation.section.property-list.css"), 55, 1); // {
		assertToken(getToken("meta.property-list.css"), 56, 1);
		assertToken(getToken("meta.property-list.css punctuation.section.property-list.css"), 57, 1); // }
		assertToken(Token.WHITESPACE, 58, 1);
	}

	@Test
	public void testMediaWithRules2()
	{
		String src = "@media screen {\n" + //
				"  * { font-family: sans-serif }\n" + //
				"}\n" + //
				"body { } "; //
		IDocument document = new Document(src);
		scanner.setRange(document, 0, src.length());
		assertTokens(
				"meta.at-rule.media.css keyword.control.at-rule.media.css",
				"meta.at-rule.media.css",
				"meta.at-rule.media.css support.constant.media.css",
				"meta.at-rule.media.css",
				"meta.at-rule.media.css punctuation.section.at-rule.media.css",
				"meta.at-rule.media.css",
				"meta.at-rule.media.css meta.selector.css entity.name.tag.wildcard.css",
				"meta.at-rule.media.css meta.selector.css",
				"meta.at-rule.media.css meta.property-list.css punctuation.section.property-list.css",
				"meta.at-rule.media.css meta.property-list.css",
				"meta.at-rule.media.css meta.property-list.css meta.property-name.css support.type.property-name.css",
				"meta.at-rule.media.css meta.property-list.css meta.property-value.css punctuation.separator.key-value.css",
				"meta.at-rule.media.css meta.property-list.css meta.property-value.css",
				"meta.at-rule.media.css meta.property-list.css meta.property-value.css support.constant.font-name.css",
				"meta.at-rule.media.css meta.property-list.css meta.property-value.css",
				"meta.at-rule.media.css meta.property-list.css punctuation.section.property-list.css",
				"meta.at-rule.media.css", "meta.at-rule.media.css punctuation.section.at-rule.media.css", "null",
				"meta.selector.css entity.name.tag.css", "meta.selector.css",
				"meta.property-list.css punctuation.section.property-list.css", "meta.property-list.css",
				"meta.property-list.css punctuation.section.property-list.css", "null", "null");
	}

	@Test
	public void testCurliesInStringsBeforePartition()
	{
		String src = "@import 'ch{.css';\n" + //
				" body { } "; //
		IDocument document = new Document(src);
		partition(document); // Partition first, as we rely on them to determine how to skip strings/comments
		scanner.setRange(document, 17, src.length() - 17);

		assertToken(getToken("punctuation.terminator.rule.css"), 17, 1); // ;
		assertToken(Token.WHITESPACE, 18, 2);
		// body
		assertToken(getToken("meta.selector.css entity.name.tag.css"), 20, 4);
		assertToken(getToken("meta.selector.css"), 24, 1);
		assertToken(getToken("meta.property-list.css punctuation.section.property-list.css"), 25, 1); // {
		assertToken(getToken("meta.property-list.css"), 26, 1);
		assertToken(getToken("meta.property-list.css punctuation.section.property-list.css"), 27, 1); // }
		assertToken(Token.WHITESPACE, 28, 1);
	}

	@Test
	public void testCurliesInCommentBeforePartition()
	{
		String src = "/*         {   */;\n" + //
				" body { } "; //
		IDocument document = new Document(src);
		partition(document); // Partition first, as we rely on them to determine how to skip strings/comments
		scanner.setRange(document, 17, src.length() - 17);

		assertToken(getToken("punctuation.terminator.rule.css"), 17, 1); // ;
		assertToken(Token.WHITESPACE, 18, 2);
		// body
		assertToken(getToken("meta.selector.css entity.name.tag.css"), 20, 4);
		assertToken(getToken("meta.selector.css"), 24, 1);
		assertToken(getToken("meta.property-list.css punctuation.section.property-list.css"), 25, 1); // {
		assertToken(getToken("meta.property-list.css"), 26, 1);
		assertToken(getToken("meta.property-list.css punctuation.section.property-list.css"), 27, 1); // }
		assertToken(Token.WHITESPACE, 28, 1);
	}

	@Test
	public void testMediaSplitAcrossPartitions()
	{
		String src = "@media screen {\n" + // 1
				"  /*   */\n" + // 2
				"  body {}\n" + // 3
				"}\n"; // 4
		IDocument document = new Document(src);
		partition(document); // Partition first, as we rely on them to determine how to skip strings/comments
		scanner.setRange(document, 25, src.length() - 25);

		assertToken(getToken("meta.at-rule.media.css"), 25, 3);
		// body
		assertToken(getToken("meta.at-rule.media.css meta.selector.css entity.name.tag.css"), 28, 4);
		assertToken(getToken("meta.at-rule.media.css meta.selector.css"), 32, 1);
		assertToken(getToken("meta.at-rule.media.css meta.property-list.css punctuation.section.property-list.css"),
				33, 1); // {
		assertToken(getToken("meta.at-rule.media.css meta.property-list.css punctuation.section.property-list.css"),
				34, 1); // }
		assertToken(getToken("meta.at-rule.media.css"), 35, 1);
		assertToken(getToken("meta.at-rule.media.css punctuation.section.at-rule.media.css"), 36, 1); // }
		assertToken(Token.WHITESPACE, 37, 1);
	}

	@Test
	public void testPropertyListSplitAcrossPartitions()
	{
		String src = "body {\n" + // 1
				"  /*   */\n" + // 2
				"  color: red;\n" + // 3
				"}\n"; // 4
		IDocument document = new Document(src);
		partition(document); // Partition first, as we rely on them to determine how to skip strings/comments
		scanner.setRange(document, 16, src.length() - 16);

		assertToken(getToken("meta.property-list.css"), 16, 3);
		// color
		assertToken(getToken("meta.property-list.css meta.property-name.css support.type.property-name.css"), 19, 5);
		// :
		assertToken(getToken("meta.property-list.css meta.property-value.css punctuation.separator.key-value.css"), 24,
				1);
		assertToken(getToken("meta.property-list.css meta.property-value.css"), 25, 1);
		// red
		assertToken(
				getToken("meta.property-list.css meta.property-value.css support.constant.color.w3c-standard-color-name.css"),
				26, 3);
		assertToken(getToken("meta.property-list.css meta.property-value.css punctuation.terminator.rule.css"), 29, 1);
		assertToken(getToken("meta.property-list.css"), 30, 1);
		assertToken(getToken("meta.property-list.css punctuation.section.property-list.css"), 31, 1); // }
		assertToken(Token.WHITESPACE, 32, 1);
	}

	@Test
	public void testMediaClosedBeforePartitionSplit()
	{
		String src = "@media screen {\n" + // 1
				"}\n" + // 2
				"/* */\n" + // 3
				"body {}\n"; // 4
		IDocument document = new Document(src);
		partition(document); // Partition first, as we rely on them to determine how to skip strings/comments
		scanner.setRange(document, 24, src.length() - 24);

		// body
		assertToken(getToken("meta.selector.css entity.name.tag.css"), 24, 4);
		assertToken(getToken("meta.selector.css"), 28, 1);
		assertToken(getToken("meta.property-list.css punctuation.section.property-list.css"), 29, 1); // {
		assertToken(getToken("meta.property-list.css punctuation.section.property-list.css"), 30, 1); // }
		assertToken(Token.WHITESPACE, 31, 1);
	}

	@Test
	public void testTopAndLeftPropertyNames()
	{
		String src = ".class {\n" + // 1
				"  top: 0;\n" + // 2
				"  left: 0;\n" + // 3
				"}\n"; // 4
		IDocument document = new Document(src);
		scanner.setRange(document, 0, src.length());

		// body
		assertToken(getToken("meta.selector.css entity.other.attribute-name.class.css"), 0, 6);
		assertToken(getToken("meta.selector.css"), 6, 1);
		assertToken(getToken("meta.property-list.css punctuation.section.property-list.css"), 7, 1); // {
		assertToken(getToken("meta.property-list.css"), 8, 3);
		// top
		assertToken(getToken("meta.property-list.css meta.property-name.css support.type.property-name.css"), 11, 3);
		assertToken(getToken("meta.property-list.css meta.property-value.css punctuation.separator.key-value.css"), 14,
				1); // :
		assertToken(getToken("meta.property-list.css meta.property-value.css"), 15, 1);
		assertToken(getToken("meta.property-list.css meta.property-value.css constant.numeric.css"), 16, 1); // 0
		assertToken(getToken("meta.property-list.css meta.property-value.css punctuation.terminator.rule.css"), 17, 1); // ;
		assertToken(getToken("meta.property-list.css"), 18, 3);
		// left
		assertToken(getToken("meta.property-list.css meta.property-name.css support.type.property-name.css"), 21, 4);
		assertToken(getToken("meta.property-list.css meta.property-value.css punctuation.separator.key-value.css"), 25,
				1); // :
		assertToken(getToken("meta.property-list.css meta.property-value.css"), 26, 1);
		assertToken(getToken("meta.property-list.css meta.property-value.css constant.numeric.css"), 27, 1); // 0
		assertToken(getToken("meta.property-list.css meta.property-value.css punctuation.terminator.rule.css"), 28, 1); // ;
		assertToken(getToken("meta.property-list.css"), 29, 1);
		assertToken(getToken("meta.property-list.css punctuation.section.property-list.css"), 30, 1); // }
		assertToken(Token.WHITESPACE, 31, 1);

	}

	// FIXME We don't retain the meta scopes in non-default partitions, i.e. comments/strings!

	protected void partition(IDocument document)
	{
		IDocumentPartitioner partitioner = new FastPartitioner(new CSSSourcePartitionScannerJFlex(),
				CSSSourceConfiguration.CONTENT_TYPES);
		partitioner.connect(document);
		document.setDocumentPartitioner(partitioner);
	}
}
