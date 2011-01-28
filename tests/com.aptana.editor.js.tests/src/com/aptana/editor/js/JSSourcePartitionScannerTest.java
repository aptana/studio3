/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.js;

import junit.framework.TestCase;

import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentPartitioner;
import org.eclipse.jface.text.rules.FastPartitioner;

import com.aptana.editor.common.CommonEditorPlugin;
import com.aptana.editor.common.IPartitioningConfiguration;

/**
 * @author Chris
 * @author Sandip
 */
public class JSSourcePartitionScannerTest extends TestCase
{
	private IDocumentPartitioner partitioner;

	private void assertContentType(String contentType, String code, int offset)
	{
		// HACK: Not sure how to force the default content type of our Document to
		// JSSourceConfiguration.DEFAULT, so we map those values to IDocument.DEFAULT_CONTENT_TYPE
		// as a workaround
		if (contentType.equals(JSSourceConfiguration.DEFAULT))
		{
			contentType = IDocument.DEFAULT_CONTENT_TYPE;
		}

		assertEquals("Content type doesn't match expectations for: " + code.charAt(offset), contentType, getContentType(code, offset));
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
			// NOTE: the following is based on SimpleDocumentProvider#connect(Object)
			IDocument document = new Document(content);
			IPartitioningConfiguration configuration = JSSourceConfiguration.getDefault();

			partitioner = new FastPartitioner(new JSSourcePartitionScanner(), configuration.getContentTypes());
			partitioner.connect(document);
			document.setDocumentPartitioner(partitioner);

			CommonEditorPlugin.getDefault().getDocumentScopeManager().registerConfiguration(document, configuration);
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

	public void testDivisions()
	{
		String source = "if ( x / s >= 0) { x = x / 10; }";

		for (int i = 0; i < source.length(); i++)
		{
			assertContentType(JSSourceConfiguration.DEFAULT, source, i);
		}
	}

	public void testEndDoubleSlashRegexp()
	{

		String source =
		// 1 2
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
