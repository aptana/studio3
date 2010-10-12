/**
 * This file Copyright (c) 2005-2010 Aptana, Inc. This program is
 * dual-licensed under both the Aptana Public License and the GNU General
 * Public license. You may elect to use one or the other of these licenses.
 * 
 * This program is distributed in the hope that it will be useful, but
 * AS-IS and WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE, TITLE, or
 * NONINFRINGEMENT. Redistribution, except as permitted by whichever of
 * the GPL or APL you select, is prohibited.
 *
 * 1. For the GPL license (GPL), you can redistribute and/or modify this
 * program under the terms of the GNU General Public License,
 * Version 3, as published by the Free Software Foundation.  You should
 * have received a copy of the GNU General Public License, Version 3 along
 * with this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 * 
 * Aptana provides a special exception to allow redistribution of this file
 * with certain other free and open source software ("FOSS") code and certain additional terms
 * pursuant to Section 7 of the GPL. You may view the exception and these
 * terms on the web at http://www.aptana.com/legal/gpl/.
 * 
 * 2. For the Aptana Public License (APL), this program and the
 * accompanying materials are made available under the terms of the APL
 * v1.0 which accompanies this distribution, and is available at
 * http://www.aptana.com/legal/apl/.
 * 
 * You may view the GPL, Aptana's exception and additional terms, and the
 * APL in the file titled license.html at the root of the corresponding
 * plugin containing this source file.
 * 
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.html;

import junit.framework.TestCase;

import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;

import com.aptana.editor.common.ExtendedFastPartitioner;
import com.aptana.editor.common.NullPartitionerSwitchStrategy;
import com.aptana.editor.common.text.rules.CompositePartitionScanner;
import com.aptana.editor.common.text.rules.NullSubPartitionScanner;
import com.aptana.editor.css.CSSSourceConfiguration;
import com.aptana.editor.js.JSSourceConfiguration;

/**
 * @author Chris
 * @author Sandip
 */
public class HTMLSourcePartitionScannerTest extends TestCase
{

	private ExtendedFastPartitioner partitioner;

	private void assertContentType(String contentType, String code, int offset)
	{
		assertEquals("Content type doesn't match expectations for: " + code.charAt(offset), contentType,
				getContentType(code, offset));
	}

	@Override
	protected void tearDown() throws Exception
	{
		partitioner = null;
		super.tearDown();
	}

	private String getContentType(String content, int offset)
	{
		if (partitioner == null)
		{
			IDocument document = new Document(content);
			CompositePartitionScanner partitionScanner = new CompositePartitionScanner(HTMLSourceConfiguration
					.getDefault().createSubPartitionScanner(), new NullSubPartitionScanner(),
					new NullPartitionerSwitchStrategy());
			partitioner = new ExtendedFastPartitioner(partitionScanner, HTMLSourceConfiguration.getDefault()
					.getContentTypes());
			partitionScanner.setPartitioner(partitioner);
			partitioner.connect(document);
			document.setDocumentPartitioner(partitioner);
		}
		return partitioner.getContentType(offset);
	}

	// TODO Add tests for script/style/tag/cdata/doctype/default

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
		assertContentType(HTMLSourceConfiguration.HTML_TAG, source, 222); // Text'<'/p>
	}

	public void testHTML5()
	{
		String source = "<!DOCTYPE html>\n"
				+ "<HTML><HEAD>\n"
				+ "<STYLE>html {color: red;}</STYLE>\n"
				+ "<SCRIPT>var one = 1;</SCRIPT>\n"
				+ "</HEAD><BODY>\n"
				+ "</BODY></HTML>";
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
		// script tag
		assertContentType(HTMLSourceConfiguration.HTML_SCRIPT, source, 63); // '<'script>
		assertContentType(HTMLSourceConfiguration.HTML_SCRIPT, source, 70); // <script'>'
		// Inlined JS
		assertContentType(JSSourceConfiguration.DEFAULT, source, 71); // <script>'v'
		assertContentType(JSSourceConfiguration.DEFAULT, source, 82); // ';'</script>
	}

	public void testLowercaseDoctype()
	{
		String source = "<!doctype html>";
		// DOCTYPE
		assertContentType(HTMLSourceConfiguration.HTML_DOCTYPE, source, 0);
	}

	public void testMixedcaseDoctype()
	{
		String source = "<!DoCtYpE html>";
		// DOCTYPE
		assertContentType(HTMLSourceConfiguration.HTML_DOCTYPE, source, 0);
	}
}
