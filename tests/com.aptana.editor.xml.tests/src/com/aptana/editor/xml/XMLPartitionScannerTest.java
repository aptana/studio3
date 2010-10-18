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
package com.aptana.editor.xml;

import java.text.MessageFormat;

import junit.framework.TestCase;

import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentPartitioner;
import org.eclipse.jface.text.rules.FastPartitioner;

/**
 * @author Chris
 * @author Sandip
 */
public class XMLPartitionScannerTest extends TestCase
{

	private IDocumentPartitioner partitioner;

	private void assertContentType(String contentType, String code, int offset)
	{
		assertEquals(MessageFormat.format("Content type doesn't match expectations for: {0}({1})", code.charAt(offset),
				offset), contentType, getContentType(code, offset));
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
			partitioner = new FastPartitioner(new XMLPartitionScanner(), XMLSourceConfiguration.CONTENT_TYPES);
			partitioner.connect(document);
			document.setDocumentPartitioner(partitioner);
		}
		return partitioner.getContentType(offset);
	}

	public void testPartitioningOfPreProcessorSpanningSingleLine()
	{
		String source = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>";
		for (int i = 0; i < source.length(); i++)
		{
			assertContentType(XMLSourceConfiguration.PRE_PROCESSOR, source, i);
		}
	}

	public void testPartitioningOfPreProcessorSpanningMultipleLines()
	{
		String source = "<?xml version=\"1.0\"\n encoding=\"ISO-8859-1\"?>";
		for (int i = 0; i < source.length(); i++)
		{
			assertContentType(XMLSourceConfiguration.PRE_PROCESSOR, source, i);
		}
	}

	public void testPartitioningOfCDataSpanningSingleLine()
	{
		String source = "<![CDATA[var one = 1;]]>";
		for (int i = 0; i < source.length(); i++)
		{
			assertContentType(XMLSourceConfiguration.CDATA, source, i);
		}
	}

	public void testPartitioningOfCDataSpanningMultipleLines()
	{
		String source = "<![CDATA[var\n one\n = 1;\n]]>";
		for (int i = 0; i < source.length(); i++)
		{
			assertContentType(XMLSourceConfiguration.CDATA, source, i);
		}
	}

	public void testPartitioningOfCommentSpanningSingleLine()
	{
		String source = "<!-- This is XML comment on one Line -->";
		for (int i = 0; i < source.length(); i++)
		{
			assertContentType(XMLSourceConfiguration.COMMENT, source, i);
		}
	}

	public void testPartitioningOfCommentSpanningMultipleLines()
	{
		String source = "<!-- This is XML comment\nspanning multiple lines -->";
		for (int i = 0; i < source.length(); i++)
		{
			assertContentType(XMLSourceConfiguration.COMMENT, source, i);
		}
	}

	public void testPartitioningOfOpeningTag()
	{
		String source = "<tag>";
		for (int i = 0; i < source.length(); i++)
		{
			assertContentType(XMLSourceConfiguration.TAG, source, i);
		}
	}

	public void testPartitioningOfClosingTag()
	{
		String source = "</tag>";
		for (int i = 0; i < source.length(); i++)
		{
			assertContentType(XMLSourceConfiguration.TAG, source, i);
		}
	}

	public void testAllPartitionTypes()
	{
		String source = "<?xml version=\"1.0\"\n encoding=\"ISO-8859-1\"?>\n"
				+ "<xml><head attr='single' name=\"double\"><style><![CDATA[var one =\n 1;]]></style></head><body>\n"
				+ "<!-- This is an XML comment -->\n" + "<p>Text</p></body></xml>";

		for (int i = 0; i <= 43; i++)
		{
			assertContentType(XMLSourceConfiguration.PRE_PROCESSOR, source, i);
		}
		for (int i = 61; i <= 68; i++)
		{
			assertContentType(XMLSourceConfiguration.TAG, source, i);
		}
		for (int i = 75; i <= 82; i++)
		{
			assertContentType(XMLSourceConfiguration.TAG, source, i);
		}
		for (int i = 91; i <= 115; i++)
		{
			assertContentType(XMLSourceConfiguration.CDATA, source, i);
		}
		for (int i = 138; i <= 168; i++)
		{
			assertContentType(XMLSourceConfiguration.COMMENT, source, i);
		}
	}

}
