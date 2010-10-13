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
package com.aptana.editor.sass;

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
@SuppressWarnings("nls")
public class SassSourcePartitionScannerTest extends TestCase
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
			CompositePartitionScanner partitionScanner = new CompositePartitionScanner(SassSourceConfiguration
					.getDefault().createSubPartitionScanner(), new NullSubPartitionScanner(),
					new NullPartitionerSwitchStrategy());
			partitioner = new ExtendedFastPartitioner(partitionScanner, SassSourceConfiguration.getDefault()
					.getContentTypes());
			partitionScanner.setPartitioner((IExtendedPartitioner) partitioner);
			partitioner.connect(document);
			document.setDocumentPartitioner(partitioner);
		}
		return partitioner.getContentType(offset);
	}

	public void testPartitioningOfEmittedCommentLine()
	{
		String source =
		// 01234567890123456789012345678901234 5678901234567890
		  " /* This is Sass comment on one Line\n ";

		assertContentType(SassSourceConfiguration.DEFAULT, source, 0);
		for (int i = 1; i <= 35; i++)
		{
			assertContentType(SassSourceConfiguration.COMMENT, source, i);
		}
		assertContentType(SassSourceConfiguration.DEFAULT, source, 37);
	}
	
	public void testPartitioningOfSilentCommentLine()
	{
		String source =
		// 01234567890123456789012345678901234 5678901234567890
		  " // This is Sass comment on one Line\n ";

		assertContentType(SassSourceConfiguration.DEFAULT, source, 0);
		for (int i = 1; i <= 35; i++)
		{
			assertContentType(SassSourceConfiguration.COMMENT, source, i);
		}
		assertContentType(SassSourceConfiguration.DEFAULT, source, 37);
	}

	// TODO Add tests for comments that nest content across multiple lines!
//	public void testPartitioningOfNestedSilentComment()
//	{
//		String source =
//		// 01234567890123456789012 345678901234567890123456789 0
//		"/* This is Sass comment\nspanning multiple lines */\n";
//
//		for (int i = 0; i <= 48; i++)
//		{
//			assertContentType(SassSourceConfiguration.COMMENT, source, i);
//		}
//		assertContentType(SassSourceConfiguration.DEFAULT, source, 49);
//	}

	public void testPartitioningOfSingleQuotedString()
	{
		String source =
		// 012345678901234567890123456789012345678 901234567890
		  "' This is a single quoted Sass string'\n";
		for (int i = 0; i <= 37; i++)
		{
			assertContentType(SassSourceConfiguration.STRING_SINGLE, source, i);
		}
		assertContentType(SassSourceConfiguration.DEFAULT, source, 38);
	}

	public void testPartitioningOfEmptySingleQuotedString()
	{
		String source =
		// 1 2 3 4 5
		// 01234567890123456789012345678901234567 8901234567890
		"''\n";
		for (int i = 0; i <= 1; i++)
		{
			assertContentType(SassSourceConfiguration.STRING_SINGLE, source, i);
		}
		assertContentType(SassSourceConfiguration.DEFAULT, source, 2);
	}

	public void testPartitioningOfSingleQuotedStringWithEscape()
	{
		String source =
		// 012345678901234567890123456789012345678901234567890 1234 56
		  "' This is a single quoted Sass string with escape \\' '\n";

		for (int i = 0; i <= 53; i++)
		{
			assertContentType(SassSourceConfiguration.STRING_SINGLE, source, i);
		}
		assertContentType(SassSourceConfiguration.DEFAULT, source, 54);
	}

	public void testPartitioningOfSingleQuotedStringWithDoubleQuote()
	{
		String source =
		// 012345678901234567890123456789012345678901234567890123456 789 012
		  "' This is a single quoted Sass string with double quote \" '\n";

		for (int i = 0; i <= 58; i++)
		{
			assertContentType(SassSourceConfiguration.STRING_SINGLE, source, i);
		}
		assertContentType(SassSourceConfiguration.DEFAULT, source, 59);
	}

	public void testPartitioningOfDoubleQuotedString()
	{
		String source =
		// 0 1234567890123456789012345678901234567 8 901234567890
		  "\" This is a double quoted Sass string\"\n";
		for (int i = 0; i <= 37; i++)
		{
			assertContentType(SassSourceConfiguration.STRING_DOUBLE, source, i);
		}
		assertContentType(SassSourceConfiguration.DEFAULT, source, 38);
	}

	public void testPartitioningOfEmptyDoubleQuotedString()
	{
		String source =
		// 0 1 2 34567890123456789012345678901234567 8901234567890
		  "\"\"\n";
		for (int i = 0; i <= 1; i++)
		{
			assertContentType(SassSourceConfiguration.STRING_DOUBLE, source, i);
		}
		assertContentType(SassSourceConfiguration.DEFAULT, source, 2);
	}

	public void testPartitioningOfDoubleQuotedStringWithEscape()
	{
		String source =
		// 0 12345678901234567890123456789012345678901234567890 1 23 4 5
		  "\" This is a double quoted Sass string with escape \\\" \"\n";

		for (int i = 0; i <= 53; i++)
		{
			assertContentType(SassSourceConfiguration.STRING_DOUBLE, source, i);
		}
		assertContentType(SassSourceConfiguration.DEFAULT, source, 54);
	}

	public void testPartitioningOfDoubleQuotedStringWithSingleQuote()
	{
		String source =
		// 0 1234567890123456789012345678901234567890123456789012345678 9 01
		  "\" This is a double quoted Sass string with single quote ' \"\n";

		for (int i = 0; i <= 58; i++)
		{
			assertContentType(SassSourceConfiguration.STRING_DOUBLE, source, i);
		}
		assertContentType(SassSourceConfiguration.DEFAULT, source, 59);
	}

	public void testPartitioningOfAllPartitions()
	{
		String source =
		// 012345678901 23456789012 3456789012345678 9012345678 9012345 6 7890
		  " /* emitted\n // silent\n val = 'single'\n other = \"double\"\n";

		assertContentType(SassSourceConfiguration.DEFAULT, source, 0);
		assertContentType(SassSourceConfiguration.COMMENT, source, 1);
		assertContentType(SassSourceConfiguration.COMMENT, source, 10);
		assertContentType(SassSourceConfiguration.DEFAULT, source, 12);
		assertContentType(SassSourceConfiguration.COMMENT, source, 13);
		assertContentType(SassSourceConfiguration.COMMENT, source, 21);
		assertContentType(SassSourceConfiguration.DEFAULT, source, 23);
		assertContentType(SassSourceConfiguration.DEFAULT, source, 29);
		assertContentType(SassSourceConfiguration.STRING_SINGLE, source, 30);
		assertContentType(SassSourceConfiguration.STRING_SINGLE, source, 37);
		assertContentType(SassSourceConfiguration.DEFAULT, source, 38);
		assertContentType(SassSourceConfiguration.DEFAULT, source, 47);
		assertContentType(SassSourceConfiguration.STRING_DOUBLE, source, 48);
		assertContentType(SassSourceConfiguration.STRING_DOUBLE, source, 55);
		assertContentType(SassSourceConfiguration.DEFAULT, source, 56);
	}

}
