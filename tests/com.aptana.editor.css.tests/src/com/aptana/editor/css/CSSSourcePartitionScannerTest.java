/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.css;

import static org.junit.Assert.assertEquals;

import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentPartitioner;
import org.eclipse.jface.text.rules.FastPartitioner;
import org.eclipse.jface.text.rules.IPartitionTokenScanner;
import org.junit.After;
import org.junit.Test;

import com.aptana.editor.common.AbstractPartitionTestCase;
import com.aptana.editor.common.CommonEditorPlugin;
import com.aptana.editor.common.IPartitioningConfiguration;

/**
 * @author Chris
 * @author Sandip
 */
public class CSSSourcePartitionScannerTest extends AbstractPartitionTestCase
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

	@Override
	protected IPartitionTokenScanner createPartitionScanner()
	{
		return new OldCSSSourcePartitionScanner();
	}

	@Override
	protected String[] getContentTypes()
	{
		return CSSSourceConfiguration.getDefault().getContentTypes();
	}

	private String getContentType(String content, int offset)
	{
		if (partitioner == null)
		{
			// NOTE: the following is based on SimpleDocumentProvider#connect(Object)
			IDocument document = new Document(content);
			IPartitioningConfiguration configuration = CSSSourceConfiguration.getDefault();

			partitioner = new FastPartitioner(createPartitionScanner(), configuration.getContentTypes());
			partitioner.connect(document);
			document.setDocumentPartitioner(partitioner);

			CommonEditorPlugin.getDefault().getDocumentScopeManager().registerConfiguration(document, configuration);

		}
		return partitioner.getContentType(offset);
	}

	@Test
	public void testPartitioningOfDefaultPartition()
	{
		String source =
		// 1 2 3 4 5
		// 0123456789012345678901234567890123456 78901234567890
		" /* This is CSS comment on one Line */ \n";

		assertContentType(IDocument.DEFAULT_CONTENT_TYPE, source, 0);
		for (int i = 1; i <= 37; i++)
		{
			assertContentType(CSSSourceConfiguration.MULTILINE_COMMENT, source, i);
		}
		assertContentType(IDocument.DEFAULT_CONTENT_TYPE, source, 38);
		assertContentType(IDocument.DEFAULT_CONTENT_TYPE, source, 39);
	}

	@Test
	public void testPartitioningOfCommentSpanningSingleLine()
	{
		String source =
		// 1 2 3 4 5
		// 0123456789012345678901234567890123456 78901234567890
		"/* This is CSS comment on one Line */\n";

		for (int i = 0; i <= 36; i++)
		{
			assertContentType(CSSSourceConfiguration.MULTILINE_COMMENT, source, i);
		}
		assertContentType(IDocument.DEFAULT_CONTENT_TYPE, source, 37);
	}

	@Test
	public void testPartitioningOfCommentSpanningMultipleLines()
	{
		String source =
		// 1 2 3 4 5
		// 01234567890123456789012 345678901234567890123456789 0
		"/* This is CSS comment\nspanning multiple lines */\n";

		for (int i = 0; i <= 48; i++)
		{
			assertContentType(CSSSourceConfiguration.MULTILINE_COMMENT, source, i);
		}
		assertContentType(IDocument.DEFAULT_CONTENT_TYPE, source, 49);
	}

	@Test
	public void testPartitioningOfSingleQuotedString()
	{
		String source =
		// 1 2 3 4 5
		// 01234567890123456789012345678901234567 8901234567890
		"' This is a single quoted CSS string'\n";
		for (int i = 0; i <= 36; i++)
		{
			assertContentType(CSSSourceConfiguration.STRING_SINGLE, source, i);
		}
		assertContentType(IDocument.DEFAULT_CONTENT_TYPE, source, 37);
	}

	@Test
	public void testPartitioningOfEmptySingleQuotedString()
	{
		String source =
		// 1 2 3 4 5
		// 01234567890123456789012345678901234567 8901234567890
		"''\n";
		for (int i = 0; i <= 1; i++)
		{
			assertContentType(CSSSourceConfiguration.STRING_SINGLE, source, i);
		}
		assertContentType(IDocument.DEFAULT_CONTENT_TYPE, source, 2);
	}

	@Test
	public void testPartitioningOfSingleQuotedStringWithEscape()
	{
		String source =
		// 1 2 3 4 5
		// 01234567890123456789012345678901234567 89012345678 9012
		"' This is a single quoted CSS string with escape \\' '\n";

		for (int i = 0; i <= 52; i++)
		{
			assertContentType(CSSSourceConfiguration.STRING_SINGLE, source, i);
		}
		assertContentType(IDocument.DEFAULT_CONTENT_TYPE, source, 53);
	}

	@Test
	public void testPartitioningOfSingleQuotedStringWithDoubleQuote()
	{
		String source =
		// 1 2 3 4 5
		// 01234567890123456789012345678901234567 89012345678 9012
		"' This is a single quoted CSS string with double quote \" '\n";

		for (int i = 0; i <= 57; i++)
		{
			assertContentType(CSSSourceConfiguration.STRING_SINGLE, source, i);
		}
		assertContentType(IDocument.DEFAULT_CONTENT_TYPE, source, 58);
	}

	@Test
	public void testPartitioningOfDoubleQuotedString()
	{
		String source =
		// 1 2 3 4 5
		// 0 123456789012345678901234567890123456 7 8901234567890
		"\" This is a double quoted CSS string\"\n";
		for (int i = 0; i <= 36; i++)
		{
			assertContentType(CSSSourceConfiguration.STRING_DOUBLE, source, i);
		}
		assertContentType(IDocument.DEFAULT_CONTENT_TYPE, source, 37);
	}

	@Test
	public void testPartitioningOfEmptyDoubleQuotedString()
	{
		String source =
		// 1 2 3 4 5
		// 01234567890123456789012345678901234567 8901234567890
		"\"\"\n";
		for (int i = 0; i <= 1; i++)
		{
			assertContentType(CSSSourceConfiguration.STRING_DOUBLE, source, i);
		}
		assertContentType(IDocument.DEFAULT_CONTENT_TYPE, source, 2);
	}

	@Test
	public void testPartitioningOfDoubleQuotedStringWithEscape()
	{
		String source =
		// 1 2 3 4 5
		// 0 1234567890123456789012345678901234567 89012345678 9 012
		"\" This is a double quoted CSS string with escape \\\" \"\n";

		for (int i = 0; i <= 52; i++)
		{
			assertContentType(CSSSourceConfiguration.STRING_DOUBLE, source, i);
		}
		assertContentType(IDocument.DEFAULT_CONTENT_TYPE, source, 53);
	}

	@Test
	public void testPartitioningOfDoubleQuotedStringWithSingleQuote()
	{
		String source =
		// 1 2 3 4 5
		// 0 1234567890123456789012345678901234567 89012345678 9 012
		"\" This is a double quoted CSS string with single quote ' \"\n";

		for (int i = 0; i <= 57; i++)
		{
			assertContentType(CSSSourceConfiguration.STRING_DOUBLE, source, i);
		}
		assertContentType(IDocument.DEFAULT_CONTENT_TYPE, source, 58);
	}

	@Test
	public void testPartitioningOfAllPartitions()
	{
		String source =
		// 1 2 3 4 5
		// 0123456789012345678901234567890123456 78901234567890
		" /* */ /**/ ' ' \" \" \n";

		assertContentType(IDocument.DEFAULT_CONTENT_TYPE, source, 0);
		assertContentType(CSSSourceConfiguration.MULTILINE_COMMENT, source, 1);
		assertContentType(CSSSourceConfiguration.MULTILINE_COMMENT, source, 5);
		assertContentType(IDocument.DEFAULT_CONTENT_TYPE, source, 6);
		assertContentType(CSSSourceConfiguration.MULTILINE_COMMENT, source, 7);
		assertContentType(CSSSourceConfiguration.MULTILINE_COMMENT, source, 10);
		assertContentType(IDocument.DEFAULT_CONTENT_TYPE, source, 11);
		assertContentType(CSSSourceConfiguration.STRING_SINGLE, source, 12);
		assertContentType(CSSSourceConfiguration.STRING_SINGLE, source, 14);
		assertContentType(IDocument.DEFAULT_CONTENT_TYPE, source, 15);
		assertContentType(CSSSourceConfiguration.STRING_DOUBLE, source, 16);
		assertContentType(CSSSourceConfiguration.STRING_DOUBLE, source, 18);
		assertContentType(IDocument.DEFAULT_CONTENT_TYPE, source, 19);
		assertContentType(IDocument.DEFAULT_CONTENT_TYPE, source, 20);
	}

}
