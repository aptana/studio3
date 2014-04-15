/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.dtd;

import static org.junit.Assert.assertEquals;

import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentPartitioner;
import org.junit.After;
import org.junit.Test;

import com.aptana.editor.common.ExtendedFastPartitioner;
import com.aptana.editor.common.IExtendedPartitioner;
import com.aptana.editor.common.NullPartitionerSwitchStrategy;
import com.aptana.editor.common.text.rules.CompositePartitionScanner;
import com.aptana.editor.common.text.rules.NullSubPartitionScanner;

/**
 */
public class DTDSourcePartitionScannerTest
{

	private IDocumentPartitioner partitioner;

	private void assertContentType(String contentType, String code, int offset)
	{
		assertEquals("Content type doesn't match expectations for: " + code.charAt(offset), contentType,
				getContentType(code, offset));
	}

	private void assertContentType(String contentType, String code, int offset, int length)
	{
		for (int i = 0; i < length; ++i)
		{
			assertContentType(contentType, code, offset + i);
		}
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
			IDocument document = new Document(content);
			CompositePartitionScanner partitionScanner = new CompositePartitionScanner(DTDSourceConfiguration
					.getDefault().createSubPartitionScanner(), new NullSubPartitionScanner(),
					new NullPartitionerSwitchStrategy());
			partitioner = new ExtendedFastPartitioner(partitionScanner, DTDSourceConfiguration.getDefault()
					.getContentTypes());
			partitionScanner.setPartitioner((IExtendedPartitioner) partitioner);
			partitioner.connect(document);
			document.setDocumentPartitioner(partitioner);
		}
		return partitioner.getContentType(offset);
	}

	@Test
	public void testDefaultPartition()
	{
		String source = " <!-- comment -->  ";
		assertContentType(DTDSourceConfiguration.DEFAULT, source, 0);
		assertContentType(DTDSourceConfiguration.COMMENT, source, 1, 16);
		assertContentType(DTDSourceConfiguration.DEFAULT, source, 17);
		assertContentType(DTDSourceConfiguration.DEFAULT, source, 18);
	}

	@Test
	public void testCommentPartition()
	{
		String source = " <!-- comment\ncomment\r\ncomment <!ATTR> -->  ";
		assertContentType(DTDSourceConfiguration.DEFAULT, source, 0);
		assertContentType(DTDSourceConfiguration.COMMENT, source, 1, 41);
		assertContentType(DTDSourceConfiguration.DEFAULT, source, 42);
		assertContentType(DTDSourceConfiguration.DEFAULT, source, 43);
	}

	@Test
	public void testTagPartition()
	{
		String source = " <!ELEMENT note   \n\n (to+,from?,heading,img,body*)> ";
		assertContentType(DTDSourceConfiguration.DEFAULT, source, 0);
		assertContentType(DTDSourceConfiguration.TAG, source, 1, 50);
		assertContentType(DTDSourceConfiguration.DEFAULT, source, 51);
	}

	@Test
	public void testSectionPartition()
	{
		String source = " <![%draft;[\n<!ELEMENT book (comments*, title, body, supplements?)>\n]]> ";
		assertContentType(DTDSourceConfiguration.DEFAULT, source, 0);
		assertContentType(DTDSourceConfiguration.SECTION, source, 1, 11);
		assertContentType(DTDSourceConfiguration.DEFAULT, source, 12);
		assertContentType(DTDSourceConfiguration.TAG, source, 13, 54);
		assertContentType(DTDSourceConfiguration.DEFAULT, source, 67);
		assertContentType(DTDSourceConfiguration.SECTION, source, 68, 3);
		assertContentType(DTDSourceConfiguration.DEFAULT, source, 71);
	}

	@Test
	public void testPIPartition()
	{
		String source = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<!-- comment -->  ";
		assertContentType(DTDSourceConfiguration.PROLOG, source, 0, 38);
		assertContentType(DTDSourceConfiguration.DEFAULT, source, 38);
		assertContentType(DTDSourceConfiguration.COMMENT, source, 39, 16);
		assertContentType(DTDSourceConfiguration.DEFAULT, source, 55);
	}

	@Test
	public void testPIPartition2()
	{
		String source = "<?tst version=\"1.0\" encoding=\"UTF-8\"?>\n<!-- comment -->  ";
		assertContentType(DTDSourceConfiguration.PI, source, 0, 38);
		assertContentType(DTDSourceConfiguration.DEFAULT, source, 38);
		assertContentType(DTDSourceConfiguration.COMMENT, source, 39, 16);
		assertContentType(DTDSourceConfiguration.DEFAULT, source, 55);
	}

}
