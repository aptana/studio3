/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.html;

import static org.junit.Assert.assertEquals;

import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentPartitioner;
import org.junit.After;
import org.junit.Test;

import com.aptana.editor.css.CSSSourceConfiguration;
import com.aptana.editor.js.JSSourceConfiguration;

/**
 * @author Chris
 * @author Sandip
 */
public class HTMLSourcePartitionScannerTest
{

	private IDocumentPartitioner partitioner;

	private void assertContentType(String contentType, String code, int offset)
	{
		assertEquals("Content type doesn't match expectations for: " + code.charAt(offset), contentType,
				getContentType(code, offset));
	}

	@After
	public void tearDown() throws Exception
	{
		partitioner = null;
	}

	private String getContentType(String content, int offset)
	{
		if (partitioner == null)
		{
			// FIXME Call HTMLDocumentProvider.connect?
			IDocument document = new Document(content);
			HTMLTestUtil.attachPartitioner(document);
			partitioner = document.getDocumentPartitioner();
		}
		return partitioner.getContentType(offset);
	}

	// TODO Add tests for script/style/tag/cdata/doctype/default

	@Test
	public void testPartitioningOfCommentSpanningSingleLine()
	{
		String source = "<!-- This is HTML comment on one Line -->\n";

		assertContentType(HTMLSourceConfiguration.HTML_COMMENT, source, 0);
		assertContentType(HTMLSourceConfiguration.HTML_COMMENT, source, 1);
		assertContentType(HTMLSourceConfiguration.HTML_COMMENT, source, 2);
		assertContentType(HTMLSourceConfiguration.HTML_COMMENT, source, 3);
		assertContentType(HTMLSourceConfiguration.HTML_COMMENT, source, 4);
		assertContentType(HTMLSourceConfiguration.HTML_COMMENT, source, 5);
		assertContentType(HTMLSourceConfiguration.HTML_COMMENT, source, 37);
		assertContentType(HTMLSourceConfiguration.HTML_COMMENT, source, 38);
		assertContentType(HTMLSourceConfiguration.HTML_COMMENT, source, 39);
		assertContentType(HTMLSourceConfiguration.HTML_COMMENT, source, 40);
		assertContentType(HTMLSourceConfiguration.DEFAULT, source, 41);
	}

	@Test
	public void testPartitioningOfCommentSpanningMultipleLines()
	{
		String source = "<!-- This is HTML comment\nspanning multiple lines -->\n";

		assertContentType(HTMLSourceConfiguration.HTML_COMMENT, source, 0);
		assertContentType(HTMLSourceConfiguration.HTML_COMMENT, source, 1);
		assertContentType(HTMLSourceConfiguration.HTML_COMMENT, source, 2);
		assertContentType(HTMLSourceConfiguration.HTML_COMMENT, source, 3);
		assertContentType(HTMLSourceConfiguration.HTML_COMMENT, source, 4);
		assertContentType(HTMLSourceConfiguration.HTML_COMMENT, source, 5);
		assertContentType(HTMLSourceConfiguration.HTML_COMMENT, source, 48);
		assertContentType(HTMLSourceConfiguration.HTML_COMMENT, source, 49);
		assertContentType(HTMLSourceConfiguration.HTML_COMMENT, source, 50);
		assertContentType(HTMLSourceConfiguration.HTML_COMMENT, source, 51);
		assertContentType(HTMLSourceConfiguration.HTML_COMMENT, source, 52);
		assertContentType(HTMLSourceConfiguration.DEFAULT, source, 53);
	}

	@Test
	public void testLessThan()
	{
		String source = "<";

		assertContentType(HTMLSourceConfiguration.HTML_TAG, source, 0);
	}

	@Test
	public void testAllPartitionTypes()
	{
		String source = "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01//EN\" \"http://www.w3.org/TR/html4/strict.dtd\">\n"
				+ "<html><head>\n"
				+ "<style>html {color: red;}</style>\n"
				+ "<script>var one = 1;</script>\n"
				+ "</head><body>\n" + "<!-- This is an HTML comment -->\n" + "<p>Text</p>\n" + "</body></html>";
		// DOCTYPE
		assertContentType(HTMLSourceConfiguration.HTML_DOCTYPE, source, 0);
		// html tag
		assertContentType(HTMLSourceConfiguration.HTML_TAG, source, 91); // '<'html
		// Style tag
		assertContentType(HTMLSourceConfiguration.HTML_STYLE, source, 104); // '<'style>
		assertContentType(HTMLSourceConfiguration.HTML_STYLE, source, 110); // style'>'html
		// Inlined CSS
		assertContentType(CSSSourceConfiguration.DEFAULT, source, 111); // style>'h'tml
		assertContentType(CSSSourceConfiguration.DEFAULT, source, 128); // '}'
		// End style tag
		assertContentType(HTMLSourceConfiguration.HTML_STYLE, source, 104); // '<'/style>
		assertContentType(HTMLSourceConfiguration.HTML_STYLE, source, 110); // /style'>'
		// script tag
		assertContentType(HTMLSourceConfiguration.HTML_SCRIPT, source, 138); // '<'script>
		assertContentType(HTMLSourceConfiguration.HTML_SCRIPT, source, 145); // <script'>'
		// Inlined JS
		assertContentType(JSSourceConfiguration.DEFAULT, source, 146); // <script>'v'
		assertContentType(JSSourceConfiguration.DEFAULT, source, 157); // ';'</script>
		// HTML Comment
		assertContentType(HTMLSourceConfiguration.HTML_COMMENT, source, 182); // '<'!--
		assertContentType(HTMLSourceConfiguration.HTML_COMMENT, source, 213); // --'>'
		// Text inside tag
		assertContentType(HTMLSourceConfiguration.DEFAULT, source, 218); // <p>'T'ext
		assertContentType(HTMLSourceConfiguration.DEFAULT, source, 221); // <p>Tex't'</p>
		// Close p tag
		assertContentType(HTMLSourceConfiguration.HTML_TAG_CLOSE, source, 222); // Text'<'/p>
	}

	@Test
	public void testHTML5()
	{
		String source = "<!DOCTYPE html>\n" + "<HTML><HEAD>\n" + "<STYLE>html {color: red;}</STYLE>\n"
				+ "<SCRIPT>var one = 1;</SCRIPT>\n" + "</HEAD><BODY>\n" + "</BODY></HTML>";
		// DOCTYPE
		assertContentType(HTMLSourceConfiguration.HTML_DOCTYPE, source, 0);
		// html tag
		assertContentType(HTMLSourceConfiguration.HTML_TAG, source, 16); // '<'html
		// Style tag
		assertContentType(HTMLSourceConfiguration.HTML_STYLE, source, 29); // '<'style>
		assertContentType(HTMLSourceConfiguration.HTML_STYLE, source, 35); // style'>'html
		// Inlined CSS
		assertContentType(CSSSourceConfiguration.DEFAULT, source, 36); // style>'h'tml
		assertContentType(CSSSourceConfiguration.DEFAULT, source, 53); // '}'
		// /style tag
		assertContentType(HTMLSourceConfiguration.HTML_TAG_CLOSE, source, 54); // '<'/style
		// script tag
		assertContentType(HTMLSourceConfiguration.HTML_SCRIPT, source, 63); // '<'script>
		assertContentType(HTMLSourceConfiguration.HTML_SCRIPT, source, 70); // <script'>'
		// Inlined JS
		assertContentType(JSSourceConfiguration.DEFAULT, source, 71); // <script>'v'
		assertContentType(JSSourceConfiguration.DEFAULT, source, 82); // ';'</script>
		// /script tag
		assertContentType(HTMLSourceConfiguration.HTML_TAG_CLOSE, source, 83); // '<'/script
	}

	@Test
	public void testLowercaseDoctype()
	{
		String source = "<!doctype html>";
		// DOCTYPE
		assertContentType(HTMLSourceConfiguration.HTML_DOCTYPE, source, 0);
	}

	@Test
	public void testMixedcaseDoctype()
	{
		String source = "<!DoCtYpE html>";
		// DOCTYPE
		assertContentType(HTMLSourceConfiguration.HTML_DOCTYPE, source, 0);
	}

	@Test
	public void testIncompleteTag1()
	{
		String source = "<html> <";
		// DOCTYPE
		assertContentType(HTMLSourceConfiguration.HTML_TAG, source, 0);
		assertContentType(HTMLSourceConfiguration.DEFAULT, source, 6);
		assertContentType(HTMLSourceConfiguration.HTML_TAG, source, 7);
	}

	@Test
	public void testIncompleteTag2()
	{
		String source = "<html> </";
		// DOCTYPE
		assertContentType(HTMLSourceConfiguration.HTML_TAG, source, 0);
		assertContentType(HTMLSourceConfiguration.DEFAULT, source, 6);
		assertContentType(HTMLSourceConfiguration.HTML_TAG_CLOSE, source, 7);
		assertContentType(HTMLSourceConfiguration.HTML_TAG_CLOSE, source, 8);
	}

	@Test
	public void testIncompleteTag3()
	{
		String source = "<html> <>";
		// DOCTYPE
		assertContentType(HTMLSourceConfiguration.HTML_TAG, source, 0);
		assertContentType(HTMLSourceConfiguration.DEFAULT, source, 6);
		assertContentType(HTMLSourceConfiguration.HTML_TAG, source, 7);
		assertContentType(HTMLSourceConfiguration.HTML_TAG, source, 8);
	}

	@Test
	public void testIncompleteTag4()
	{
		String source = "<html> </>";
		// DOCTYPE
		assertContentType(HTMLSourceConfiguration.HTML_TAG, source, 0);
		assertContentType(HTMLSourceConfiguration.DEFAULT, source, 6);
		assertContentType(HTMLSourceConfiguration.HTML_TAG_CLOSE, source, 7);
		assertContentType(HTMLSourceConfiguration.HTML_TAG_CLOSE, source, 8);
		assertContentType(HTMLSourceConfiguration.HTML_TAG_CLOSE, source, 9);
	}

	@Test
	public void testIncompleteTag5()
	{
		String source = "<</html>";
		// DOCTYPE
		assertContentType(HTMLSourceConfiguration.HTML_TAG, source, 0);
		assertContentType(HTMLSourceConfiguration.HTML_TAG_CLOSE, source, 1);
	}

	@Test
	public void testIncompleteTag6()
	{
		String source = "</<html>";
		// DOCTYPE
		assertContentType(HTMLSourceConfiguration.HTML_TAG_CLOSE, source, 0);
		assertContentType(HTMLSourceConfiguration.HTML_TAG_CLOSE, source, 1);
		assertContentType(HTMLSourceConfiguration.HTML_TAG, source, 2);
	}

	@Test
	public void testIncompleteTag7()
	{
		String source = "<></html>";
		// DOCTYPE
		assertContentType(HTMLSourceConfiguration.HTML_TAG, source, 0);
		assertContentType(HTMLSourceConfiguration.HTML_TAG, source, 1);
		assertContentType(HTMLSourceConfiguration.HTML_TAG_CLOSE, source, 2);
	}

	@Test
	public void testIncompleteTag8()
	{
		String source = "</><html>";
		// DOCTYPE
		assertContentType(HTMLSourceConfiguration.HTML_TAG_CLOSE, source, 0);
		assertContentType(HTMLSourceConfiguration.HTML_TAG_CLOSE, source, 1);
		assertContentType(HTMLSourceConfiguration.HTML_TAG_CLOSE, source, 2);
		assertContentType(HTMLSourceConfiguration.HTML_TAG, source, 3);
	}

}
