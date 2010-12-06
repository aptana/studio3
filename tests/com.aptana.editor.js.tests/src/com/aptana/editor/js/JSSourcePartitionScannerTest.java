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
package com.aptana.editor.js;

import junit.framework.TestCase;

import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;

import com.aptana.editor.common.ExtendedFastPartitioner;
import com.aptana.editor.common.NullPartitionerSwitchStrategy;
import com.aptana.editor.common.text.rules.CompositePartitionScanner;
import com.aptana.editor.common.text.rules.NullSubPartitionScanner;

/**
 * @author Chris
 * @author Sandip
 */
public class JSSourcePartitionScannerTest extends TestCase
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
			CompositePartitionScanner partitionScanner = new CompositePartitionScanner(JSSourceConfiguration
					.getDefault().createSubPartitionScanner(), new NullSubPartitionScanner(),
					new NullPartitionerSwitchStrategy());
			partitioner = new ExtendedFastPartitioner(partitionScanner, JSSourceConfiguration.getDefault()
					.getContentTypes());
			partitionScanner.setPartitioner(partitioner);
			partitioner.connect(document);
			document.setDocumentPartitioner(partitioner);
		}
		return partitioner.getContentType(offset);
	}

	public void testPartitioningOfCommentSpanningSingleLine()
	{
		String source =
		// 1 2 3 4 5
		// 012345678901234567890123456789012345678901234567890
		"/* This is JS comment on one Line */\n";

		assertContentType(JSSourceConfiguration.JS_MULTILINE_COMMENT, source, 0);
		assertContentType(JSSourceConfiguration.JS_MULTILINE_COMMENT, source, 1);
		assertContentType(JSSourceConfiguration.JS_MULTILINE_COMMENT, source, 2);
		assertContentType(JSSourceConfiguration.JS_MULTILINE_COMMENT, source, 33);
		assertContentType(JSSourceConfiguration.JS_MULTILINE_COMMENT, source, 34);
		assertContentType(JSSourceConfiguration.JS_MULTILINE_COMMENT, source, 35);
		assertContentType(JSSourceConfiguration.DEFAULT, source, 36);
	}

	public void testDivisionIsntPickedUpAsRegexp()
	{
		String source =
		// 1 2 3 4 5
		// 012345678901234567890123456789012345678901234567890
		"var width = Math.floor(viewWidth / characterWidth);\n";

		assertContentType(JSSourceConfiguration.DEFAULT, source, 0);
		assertContentType(JSSourceConfiguration.DEFAULT, source, 1);
		assertContentType(JSSourceConfiguration.DEFAULT, source, 35);
	}
	
	public void testSimpleRegexp()
	{
		String source = "var regexp = /^ace$/;\n";

		assertContentType(JSSourceConfiguration.DEFAULT, source, 0);
		assertContentType(JSSourceConfiguration.JS_REGEXP, source, 13);
		assertContentType(JSSourceConfiguration.JS_REGEXP, source, 19);
		assertContentType(JSSourceConfiguration.DEFAULT, source, 20);
	}

	public void testRegexpWithEscapeCharacter()
	{
		String source = "var regexp = /^\\/ace$/;\n";

		assertContentType(JSSourceConfiguration.DEFAULT, source, 0);
		assertContentType(JSSourceConfiguration.JS_REGEXP, source, 13);
		assertContentType(JSSourceConfiguration.JS_REGEXP, source, 21);
		assertContentType(JSSourceConfiguration.DEFAULT, source, 22);
	}
	
	public void testComplexRegexp()
	{
		String source =
		// 1 2 3 4 5
		// 012345678901234567890123456789012345678901234567890
		"var regexp = /^\\/\\*-secure-([\\s\\S]*)\\*\\/\\s*$/;\n";

		assertContentType(JSSourceConfiguration.DEFAULT, source, 0);
		assertContentType(JSSourceConfiguration.JS_REGEXP, source, 13);
		assertContentType(JSSourceConfiguration.JS_REGEXP, source, 44);
		assertContentType(JSSourceConfiguration.DEFAULT, source, 45);
	}

	public void testComplexRegexp2()
	{

		String source =
		// 1 2 3
		// 01234 5678 90 123456789012 3 456789012345678
		"/^[^{\\[]*\\/\\*/.test();/\\\\/g// comment";

		assertContentType(JSSourceConfiguration.JS_REGEXP, source, 0);
		assertContentType(JSSourceConfiguration.JS_REGEXP, source, 13);
		assertContentType(JSSourceConfiguration.DEFAULT, source, 14);
		assertContentType(JSSourceConfiguration.DEFAULT, source, 21);
		assertContentType(JSSourceConfiguration.JS_REGEXP, source, 22);
		assertContentType(JSSourceConfiguration.JS_REGEXP, source, 26);
		assertContentType(JSSourceConfiguration.JS_SINGLELINE_COMMENT, source, 27);
	}
	
	// NOTE: the following is broken in JS partitioning
//	public void testDivisions()
//	{
//		String source = "if ( x / s >= 0) { x = x / 10; }";
//		
//		for (int i = 0; i < source.length(); i++)
//		{
//			assertContentType(JSSourceConfiguration.DEFAULT, source, i);
//		}
//	}

	public void testEndDoubleSlashRegexp()
	{

		String source =
		//           1          2
		// 01234567890 123456789012
		  "if (/Mobile\\//.test(){}";

		assertContentType(JSSourceConfiguration.DEFAULT, source, 0);
		assertContentType(JSSourceConfiguration.JS_REGEXP, source, 4);
		assertContentType(JSSourceConfiguration.JS_REGEXP, source, 13);
		assertContentType(JSSourceConfiguration.DEFAULT, source, 14);
	}

	public void testPartitioningOfCommentSpanningMultipleLines()
	{
		String source =
		// 1 2 3 4 5
		// 01234567890123456789012 3456789012345678901234567890
		"/* This is JS comment\nspanning multiple lines */\n";

		assertContentType(JSSourceConfiguration.JS_MULTILINE_COMMENT, source, 0);
		assertContentType(JSSourceConfiguration.JS_MULTILINE_COMMENT, source, 1);
		assertContentType(JSSourceConfiguration.JS_MULTILINE_COMMENT, source, 2);
		assertContentType(JSSourceConfiguration.JS_MULTILINE_COMMENT, source, 45);
		assertContentType(JSSourceConfiguration.JS_MULTILINE_COMMENT, source, 46);
		assertContentType(JSSourceConfiguration.JS_MULTILINE_COMMENT, source, 47);
		assertContentType(JSSourceConfiguration.DEFAULT, source, 48);
	}

	public void testPartitioningOfSingleQuotedString()
	{
		String source =
		// 1 2 3 4 5
		// 01234567890123456789012345678901234567 8901234567890
		"' This is a single quoted JS string'\n";
		for (int i = 0; i <= 35; i++)
		{
			assertContentType(JSSourceConfiguration.STRING_SINGLE, source, i);
		}
		assertContentType(JSSourceConfiguration.DEFAULT, source, 36);
	}

	public void testPartitioningOfEmptySingleQuotedString()
	{
		String source =
		// 1 2 3 4 5
		// 01234567890123456789012345678901234567 8901234567890
		"''\n";
		for (int i = 0; i <= 1; i++)
		{
			assertContentType(JSSourceConfiguration.STRING_SINGLE, source, i);
		}
		assertContentType(JSSourceConfiguration.DEFAULT, source, 2);
	}

	public void testPartitioningOfSingleQuotedStringWithEscape()
	{
		String source =
		// 1 2 3 4 5
		// 01234567890123456789012345678901234567 89012345678 9012
		"' This is a single quoted JS string with escape \\' '\n";

		for (int i = 0; i <= 51; i++)
		{
			assertContentType(JSSourceConfiguration.STRING_SINGLE, source, i);
		}
		assertContentType(JSSourceConfiguration.DEFAULT, source, 52);
	}

	public void testPartitioningOfSingleQuotedStringWithDoubleQuote()
	{
		String source =
		// 1 2 3 4 5
		// 01234567890123456789012345678901234567 89012345678 9012
		"' This is a single quoted JS string with double quote \" '\n";

		for (int i = 0; i <= 56; i++)
		{
			assertContentType(JSSourceConfiguration.STRING_SINGLE, source, i);
		}
		assertContentType(JSSourceConfiguration.DEFAULT, source, 57);
	}

	public void testPartitioningOfDoubleQuotedString()
	{
		String source =
		// 1 2 3 4 5
		// 0 123456789012345678901234567890123456 7 8901234567890
		"\" This is a double quoted JS string\"\n";
		for (int i = 0; i <= 35; i++)
		{
			assertContentType(JSSourceConfiguration.STRING_DOUBLE, source, i);
		}
		assertContentType(JSSourceConfiguration.DEFAULT, source, 36);
	}

	public void testPartitioningOfEmptyDoubleQuotedString()
	{
		String source =
		// 1 2 3 4 5
		// 01234567890123456789012345678901234567 8901234567890
		"\"\"\n";
		for (int i = 0; i <= 1; i++)
		{
			assertContentType(JSSourceConfiguration.STRING_DOUBLE, source, i);
		}
		assertContentType(JSSourceConfiguration.DEFAULT, source, 2);
	}

	public void testPartitioningOfDoubleQuotedStringWithEscape()
	{
		String source =
		// 1 2 3 4 5
		// 0 1234567890123456789012345678901234567 89012345678 9 012
		"\" This is a double quoted JS string with escape \\\" \"\n";

		for (int i = 0; i <= 51; i++)
		{
			assertContentType(JSSourceConfiguration.STRING_DOUBLE, source, i);
		}
		assertContentType(JSSourceConfiguration.DEFAULT, source, 52);
	}

	public void testPartitioningOfDoubleQuotedStringWithSingleQuote()
	{
		String source =
		// 1 2 3 4 5
		// 0 12345678901234567890123456789012345678901234567890123456 7
		"\" This is a double quoted JS string with single quote ' \"\n";

		for (int i = 0; i <= 56; i++)
		{
			assertContentType(JSSourceConfiguration.STRING_DOUBLE, source, i);
		}
		assertContentType(JSSourceConfiguration.DEFAULT, source, 57);
	}
}
