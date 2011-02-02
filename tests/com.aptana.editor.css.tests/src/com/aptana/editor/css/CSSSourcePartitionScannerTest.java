/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.css;

import junit.framework.TestCase;

import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentPartitioner;

import com.aptana.editor.common.ExtendedFastPartitioner;
import com.aptana.editor.common.IExtendedPartitioner;
import com.aptana.editor.common.NullPartitionerSwitchStrategy;
import com.aptana.editor.common.text.rules.CompositePartitionScanner;
import com.aptana.editor.common.text.rules.NullSubPartitionScanner;

/**
 * @author Chris
 * @author Sandip
 */
public class CSSSourcePartitionScannerTest extends TestCase
{

	private IDocumentPartitioner partitioner;

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
			CompositePartitionScanner partitionScanner = new CompositePartitionScanner(CSSSourceConfiguration
					.getDefault().createSubPartitionScanner(), new NullSubPartitionScanner(),
					new NullPartitionerSwitchStrategy());
			partitioner = new ExtendedFastPartitioner(partitionScanner, CSSSourceConfiguration.getDefault()
					.getContentTypes());
			partitionScanner.setPartitioner((IExtendedPartitioner) partitioner);
			partitioner.connect(document);
			document.setDocumentPartitioner(partitioner);
		}
		return partitioner.getContentType(offset);
	}

	public void testPartitioningOfDefaultPartition()
	{
		String source =
		// 1 2 3 4 5
		// 0123456789012345678901234567890123456 78901234567890
		" /* This is CSS comment on one Line */ \n";

		assertContentType(CSSSourceConfiguration.DEFAULT, source, 0);
		for (int i = 1; i <= 37; i++)
		{
			assertContentType(CSSSourceConfiguration.MULTILINE_COMMENT, source, i);
		}
		assertContentType(CSSSourceConfiguration.DEFAULT, source, 38);
		assertContentType(CSSSourceConfiguration.DEFAULT, source, 39);
	}

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
		assertContentType(CSSSourceConfiguration.DEFAULT, source, 37);
	}

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
		assertContentType(CSSSourceConfiguration.DEFAULT, source, 49);
	}

	public void testPartitioningOfSingleQuotedString()
	{
		String source =
		// 1 2 3 4 5
		// 01234567890123456789012345678901234567 8901234567890
		"' This is a single quoted CSS string'\n";
		for (int i = 0; i <= 36; i++)
		{
			assertContentType(CSSSourceConfiguration.STRING, source, i);
		}
		assertContentType(CSSSourceConfiguration.DEFAULT, source, 37);
	}

	public void testPartitioningOfEmptySingleQuotedString()
	{
		String source =
		// 1 2 3 4 5
		// 01234567890123456789012345678901234567 8901234567890
		"''\n";
		for (int i = 0; i <= 1; i++)
		{
			assertContentType(CSSSourceConfiguration.STRING, source, i);
		}
		assertContentType(CSSSourceConfiguration.DEFAULT, source, 2);
	}

	public void testPartitioningOfSingleQuotedStringWithEscape()
	{
		String source =
		// 1 2 3 4 5
		// 01234567890123456789012345678901234567 89012345678 9012
		"' This is a single quoted CSS string with escape \\' '\n";

		for (int i = 0; i <= 52; i++)
		{
			assertContentType(CSSSourceConfiguration.STRING, source, i);
		}
		assertContentType(CSSSourceConfiguration.DEFAULT, source, 53);
	}

	public void testPartitioningOfSingleQuotedStringWithDoubleQuote()
	{
		String source =
		// 1 2 3 4 5
		// 01234567890123456789012345678901234567 89012345678 9012
		"' This is a single quoted CSS string with double quote \" '\n";

		for (int i = 0; i <= 57; i++)
		{
			assertContentType(CSSSourceConfiguration.STRING, source, i);
		}
		assertContentType(CSSSourceConfiguration.DEFAULT, source, 58);
	}

	public void testPartitioningOfDoubleQuotedString()
	{
		String source =
		// 1 2 3 4 5
		// 0 123456789012345678901234567890123456 7 8901234567890
		"\" This is a double quoted CSS string\"\n";
		for (int i = 0; i <= 36; i++)
		{
			assertContentType(CSSSourceConfiguration.STRING, source, i);
		}
		assertContentType(CSSSourceConfiguration.DEFAULT, source, 37);
	}

	public void testPartitioningOfEmptyDoubleQuotedString()
	{
		String source =
		// 1 2 3 4 5
		// 01234567890123456789012345678901234567 8901234567890
		"\"\"\n";
		for (int i = 0; i <= 1; i++)
		{
			assertContentType(CSSSourceConfiguration.STRING, source, i);
		}
		assertContentType(CSSSourceConfiguration.DEFAULT, source, 2);
	}

	public void testPartitioningOfDoubleQuotedStringWithEscape()
	{
		String source =
		// 1 2 3 4 5
		// 0 1234567890123456789012345678901234567 89012345678 9 012
		"\" This is a double quoted CSS string with escape \\\" \"\n";

		for (int i = 0; i <= 52; i++)
		{
			assertContentType(CSSSourceConfiguration.STRING, source, i);
		}
		assertContentType(CSSSourceConfiguration.DEFAULT, source, 53);
	}

	public void testPartitioningOfDoubleQuotedStringWithSingleQuote()
	{
		String source =
		// 1 2 3 4 5
		// 0 1234567890123456789012345678901234567 89012345678 9 012
		"\" This is a double quoted CSS string with single quote ' \"\n";

		for (int i = 0; i <= 57; i++)
		{
			assertContentType(CSSSourceConfiguration.STRING, source, i);
		}
		assertContentType(CSSSourceConfiguration.DEFAULT, source, 58);
	}

	public void testPartitioningOfAllPartitions()
	{
		String source =
		// 1 2 3 4 5
		// 0123456789012345678901234567890123456 78901234567890
		" /* */ /**/ ' ' \" \" \n";

		assertContentType(CSSSourceConfiguration.DEFAULT, source, 0);
		assertContentType(CSSSourceConfiguration.MULTILINE_COMMENT, source, 1);
		assertContentType(CSSSourceConfiguration.MULTILINE_COMMENT, source, 5);
		assertContentType(CSSSourceConfiguration.DEFAULT, source, 6);
		assertContentType(CSSSourceConfiguration.MULTILINE_COMMENT, source, 7);
		assertContentType(CSSSourceConfiguration.MULTILINE_COMMENT, source, 10);
		assertContentType(CSSSourceConfiguration.DEFAULT, source, 11);
		assertContentType(CSSSourceConfiguration.STRING, source, 12);
		assertContentType(CSSSourceConfiguration.STRING, source, 14);
		assertContentType(CSSSourceConfiguration.DEFAULT, source, 15);
		assertContentType(CSSSourceConfiguration.STRING, source, 16);
		assertContentType(CSSSourceConfiguration.STRING, source, 18);
		assertContentType(CSSSourceConfiguration.DEFAULT, source, 19);
		assertContentType(CSSSourceConfiguration.DEFAULT, source, 20);
	}

}
