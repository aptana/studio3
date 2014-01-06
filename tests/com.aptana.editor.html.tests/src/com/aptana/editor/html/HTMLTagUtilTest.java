/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.html;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.eclipse.jface.text.IDocument;
import org.junit.Test;

import com.aptana.core.util.StringUtil;
import com.aptana.editor.html.parsing.lexer.HTMLTokenType;
import com.aptana.parsing.lexer.Lexeme;

public class HTMLTagUtilTest
{
	private static final Pattern CURSOR = Pattern.compile("\\|");

	/**
	 * createDocument
	 * 
	 * @param partitionType
	 * @param source
	 * @return
	 */
	protected IDocument createDocument(String source, boolean stripCursor)
	{
		return HTMLTestUtil.createDocument(source, stripCursor);
	}

	protected String findOffsets(String source, ArrayList<Integer> cursorOffsets)
	{
		// find offsets
		int offset = source.indexOf('|');

		while (offset != -1)
		{
			// NOTE: we have to account for the deletion of previous offsets
			cursorOffsets.add(offset - cursorOffsets.size());
			offset = source.indexOf('|', offset + 1);
		}

		if (cursorOffsets.isEmpty())
		{
			// use last position if we didn't find any cursors
			cursorOffsets.add(source.length());
		}
		else
		{
			// clean source
			source = CURSOR.matcher(source).replaceAll(StringUtil.EMPTY);
		}

		return source;
	}

	@Test
	public void testIsTag()
	{
		assertTrue(HTMLTagUtil.isTag(new Lexeme<HTMLTokenType>(HTMLTokenType.BLOCK_TAG, 0, 2, "<a>")));
		assertTrue(HTMLTagUtil.isTag(new Lexeme<HTMLTokenType>(HTMLTokenType.INLINE_TAG, 0, 2, "<a>")));
		assertTrue(HTMLTagUtil.isTag(new Lexeme<HTMLTokenType>(HTMLTokenType.STRUCTURE_TAG, 0, 2, "<a>")));
		assertFalse(HTMLTagUtil.isTag(new Lexeme<HTMLTokenType>(HTMLTokenType.ATTRIBUTE, 0, 2, "<a>")));
	}

	@Test
	public void testCloseTags1()
	{
		IDocument document = createDocument("<a>Test <b>Item</b>", false); //$NON-NLS-1$

		// Should be no unclosed tags at this point
		assertEquals(0, HTMLTagUtil.getUnclosedTagNames(document, 0).size());
	}

	@Test
	public void testCloseTags2()
	{
		IDocument document = createDocument("<a>Test <b>Item</b>", false); //$NON-NLS-1$
		assertEquals(0, HTMLTagUtil.getUnclosedTagNames(document, 1).size());
	}

	@Test
	public void testCloseTags3()
	{
		IDocument document = createDocument("<a>Test <b>Item</b>", false); //$NON-NLS-1$
		assertEquals(0, HTMLTagUtil.getUnclosedTagNames(document, 2).size());
	}

	@Test
	public void testCloseTags4()
	{
		IDocument document = createDocument("<a>Test <b>Item</b>", false); //$NON-NLS-1$
		assertEquals(1, HTMLTagUtil.getUnclosedTagNames(document, 3).size());
	}

	@Test
	public void testCloseTags5()
	{
		IDocument document = createDocument("<a>Test <b>Item</b>", false); //$NON-NLS-1$
		// show unclosed tag once we get past the '>'
		assertEquals(1, HTMLTagUtil.getUnclosedTagNames(document, 4).size());
	}

	@Test
	public void testCloseTags6()
	{
		IDocument document = createDocument("<a>Test</a> <b>Item</b>", false); //$NON-NLS-1$
		// show unclosed tag once we get past the '>', but not here, since tag is already closed later
		assertEquals(0, HTMLTagUtil.getUnclosedTagNames(document, 4).size());
	}

	@Test
	public void testCloseTags7()
	{
		String doc = "<html><head></head><body><h1>New Web Project Page</h1><div><span><| </body></html>";
		ArrayList<Integer> offsets = new ArrayList<Integer>();
		doc = findOffsets(doc, offsets);
		IDocument document = createDocument(doc, false);
		List<String> unclosed = HTMLTagUtil.getUnclosedTagNames(document, offsets.get(0));
		assertEquals(2, unclosed.size());
		// order is important
		assertEquals("span,div", StringUtil.join(",", unclosed));
	}

	@Test
	public void testCloseTags8()
	{
		String doc = "<html><head></head><body><h1>New Web Project Page</h1><div><a><| </a></body></html>";
		ArrayList<Integer> offsets = new ArrayList<Integer>();
		doc = findOffsets(doc, offsets);
		IDocument document = createDocument(doc, false);
		List<String> unclosed = HTMLTagUtil.getUnclosedTagNames(document, offsets.get(0));
		assertEquals(0, unclosed.size());
		// should present nothing
	}

	@Test
	public void testCloseTags9()
	{
		String doc = "<html><head></head><body><h1>New Web Project Page</h1><div><span><br></span><| </body></html>";
		ArrayList<Integer> offsets = new ArrayList<Integer>();
		doc = findOffsets(doc, offsets);
		IDocument document = createDocument(doc, false);
		List<String> unclosed = HTMLTagUtil.getUnclosedTagNames(document, offsets.get(0));
		assertEquals(1, unclosed.size());
		// should present /div
	}

}
