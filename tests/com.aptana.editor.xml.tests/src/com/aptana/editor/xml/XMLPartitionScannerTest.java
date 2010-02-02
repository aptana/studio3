/*
 * Created on Feb 19, 2005
 *
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
			assertContentType(XMLSourceConfiguration.XML_COMMENT, source, i);
		}
	}

	public void testPartitioningOfCommentSpanningMultipleLines()
	{
		String source = "<!-- This is XML comment\nspanning multiple lines -->";
		for (int i = 0; i < source.length(); i++)
		{
			assertContentType(XMLSourceConfiguration.XML_COMMENT, source, i);
		}
	}

	public void testPartitioningOfOpeningTag()
	{
		String source = "<tag>";
		for (int i = 0; i < source.length(); i++)
		{
			assertContentType(XMLSourceConfiguration.XML_TAG, source, i);
		}
	}

	public void testPartitioningOfClosingTag()
	{
		String source = "</tag>";
		for (int i = 0; i < source.length(); i++)
		{
			assertContentType(XMLSourceConfiguration.XML_TAG, source, i);
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
			assertContentType(XMLSourceConfiguration.XML_TAG, source, i);
		}
		for (int i = 75; i <= 82; i++)
		{
			assertContentType(XMLSourceConfiguration.XML_TAG, source, i);
		}
		for (int i = 91; i <= 115; i++)
		{
			assertContentType(XMLSourceConfiguration.CDATA, source, i);
		}
		for (int i = 138; i <= 168; i++)
		{
			assertContentType(XMLSourceConfiguration.XML_COMMENT, source, i);
		}
	}

}
