/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.html.parsing;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.aptana.editor.html.parsing.lexer.HTMLTokenType;
import com.aptana.parsing.lexer.Lexeme;
import com.aptana.parsing.lexer.Range;

/**
 * The class <code>HTMLUtilsTest</code> contains tests for the class <code>{@link HTMLUtils}</code>.
 */
public class HTMLUtilsTest
{
	/**
	 */
	@Test
	public void testGetAttributeValueRange() throws Exception
	{
		Lexeme<HTMLTokenType> lexeme = new Lexeme<HTMLTokenType>(null, 1, 1, "");
		int offset = 1;

		assertNull(HTMLUtils.getAttributeValueRange(null, offset));
		assertNull(HTMLUtils.getAttributeValueRange(lexeme, offset));
	}

	@Test
	public void testGetAttributeValueRangeSingleValueQuote() throws Exception
	{
		Lexeme<HTMLTokenType> lexemeSingleValueQuote = new Lexeme<HTMLTokenType>(HTMLTokenType.DOUBLE_QUOTED_STRING, 0,
				2, "\"a\"");
		assertEquals(null, HTMLUtils.getAttributeValueRange(lexemeSingleValueQuote, 0));
		assertEquals(new Range(1, 1), HTMLUtils.getAttributeValueRange(lexemeSingleValueQuote, 1));
		assertEquals(new Range(1, 1), HTMLUtils.getAttributeValueRange(lexemeSingleValueQuote, 2));
		assertEquals(null, HTMLUtils.getAttributeValueRange(lexemeSingleValueQuote, 3));
	}

	@Test
	public void testGetAttributeValueRangeTwoValueQuote() throws Exception
	{
		Lexeme<HTMLTokenType> lexemeTwoValueQuote = new Lexeme<HTMLTokenType>(HTMLTokenType.DOUBLE_QUOTED_STRING, 0, 4,
				"\"a b\"");
		assertEquals(null, HTMLUtils.getAttributeValueRange(lexemeTwoValueQuote, 0));
		assertEquals(new Range(1, 1), HTMLUtils.getAttributeValueRange(lexemeTwoValueQuote, 1));
		assertEquals(new Range(1, 1), HTMLUtils.getAttributeValueRange(lexemeTwoValueQuote, 2));
		assertEquals(new Range(3, 3), HTMLUtils.getAttributeValueRange(lexemeTwoValueQuote, 3));
		assertEquals(new Range(3, 3), HTMLUtils.getAttributeValueRange(lexemeTwoValueQuote, 4));
		assertEquals(null, HTMLUtils.getAttributeValueRange(lexemeTwoValueQuote, 5));
	}

	@Test
	public void testGetAttributeValueRangeThreeValueQuote() throws Exception
	{
		Lexeme<HTMLTokenType> lexemeThreeValueQuote = new Lexeme<HTMLTokenType>(HTMLTokenType.DOUBLE_QUOTED_STRING, 0,
				7, "\"a bb c\"");
		assertEquals(null, HTMLUtils.getAttributeValueRange(lexemeThreeValueQuote, 0));
		assertEquals(new Range(1, 1), HTMLUtils.getAttributeValueRange(lexemeThreeValueQuote, 1));
		assertEquals(new Range(1, 1), HTMLUtils.getAttributeValueRange(lexemeThreeValueQuote, 2));
		assertEquals(new Range(3, 4), HTMLUtils.getAttributeValueRange(lexemeThreeValueQuote, 3));
		assertEquals(new Range(3, 4), HTMLUtils.getAttributeValueRange(lexemeThreeValueQuote, 4));
		assertEquals(new Range(3, 4), HTMLUtils.getAttributeValueRange(lexemeThreeValueQuote, 5));
		assertEquals(new Range(6, 6), HTMLUtils.getAttributeValueRange(lexemeThreeValueQuote, 6));
		assertEquals(new Range(6, 6), HTMLUtils.getAttributeValueRange(lexemeThreeValueQuote, 7));
		assertEquals(null, HTMLUtils.getAttributeValueRange(lexemeThreeValueQuote, 8));
	}

	@Test
	public void testGetAttributeValueRangeSingleValueNoQuote() throws Exception
	{
		Lexeme<HTMLTokenType> lexemeSingleValueNoQuote = new Lexeme<HTMLTokenType>(HTMLTokenType.UNDEFINED, 0, 0, "a");
		assertEquals(new Range(0, 0), HTMLUtils.getAttributeValueRange(lexemeSingleValueNoQuote, 0));
		assertEquals(new Range(0, 0), HTMLUtils.getAttributeValueRange(lexemeSingleValueNoQuote, 1));
	}

	/**
	 */
	@Test
	public void testIsCSSAttribute() throws Exception
	{
		assertFalse(HTMLUtils.isCSSAttribute(null));
		assertFalse(HTMLUtils.isCSSAttribute(""));
		assertTrue(HTMLUtils.isCSSAttribute("style"));
	}

	/**
	 */
	@Test
	public void testIsJSAttribute() throws Exception
	{
		assertFalse(HTMLUtils.isJSAttribute(null, null));
		assertFalse(HTMLUtils.isJSAttribute("", ""));
		assertFalse(HTMLUtils.isJSAttribute("style", ""));
		// FIXME Appears to be a loading issue
		// assertTrue(HTMLUtils.isJSAttribute("div", "onclick"));
	}

	/**
	 */
	@Test
	public void testIsJavaScriptTag() throws Exception
	{
		assertFalse(HTMLUtils.isJavaScriptTag(""));
		assertFalse(HTMLUtils.isJavaScriptTag(null));
		assertTrue(HTMLUtils.isJavaScriptTag("<script type='javascript'>"));
		assertTrue(HTMLUtils.isJavaScriptTag("<script type=javascript>"));
		assertTrue(HTMLUtils.isJavaScriptTag("<script language=\"javascript\">"));
		assertTrue(HTMLUtils.isJavaScriptTag("<script>"));
		// FIXME This should not be correct.
		assertTrue(HTMLUtils.isJavaScriptTag("<div>"));
	}

	/**
	 */
	@Test
	public void testIsTagComplete() throws Exception
	{
		assertTrue(HTMLUtils.isTagComplete(">"));
		assertTrue(HTMLUtils.isTagComplete("<>"));
		assertTrue(HTMLUtils.isTagComplete("<div>"));
		assertFalse(HTMLUtils.isTagComplete("<div> "));
		assertFalse(HTMLUtils.isTagComplete(""));
		assertFalse(HTMLUtils.isTagComplete(null));
		assertFalse(HTMLUtils.isTagComplete("div"));
	}

	/**
	 */
	@Test
	public void testIsTagSelfClosing() throws Exception
	{
		assertTrue(HTMLUtils.isTagSelfClosing("<br />"));
		assertTrue(HTMLUtils.isTagSelfClosing("<script />"));
		assertFalse(HTMLUtils.isTagSelfClosing("</script>"));
		assertFalse(HTMLUtils.isTagSelfClosing("<br>"));
	}

	/**
	 */
	@Test
	public void testStripTagEndings() throws Exception
	{
		assertEquals("", HTMLUtils.stripTagEndings(""));
		assertEquals("tag", HTMLUtils.stripTagEndings("<tag>"));
		assertEquals("tag", HTMLUtils.stripTagEndings("</tag>"));
		assertEquals("tag", HTMLUtils.stripTagEndings("< tag >"));
		assertEquals("tag", HTMLUtils.stripTagEndings("</ tag>"));
		assertEquals("tag", HTMLUtils.stripTagEndings("</ tag"));
		assertEquals("tag", HTMLUtils.stripTagEndings("tag>"));
		assertEquals("tag", HTMLUtils.stripTagEndings(" tag >"));
		assertEquals("tag", HTMLUtils.stripTagEndings(" tag "));
	}
}
