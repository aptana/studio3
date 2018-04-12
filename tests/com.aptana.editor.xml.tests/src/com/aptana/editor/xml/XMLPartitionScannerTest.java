/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.xml;

import static org.junit.Assert.assertEquals;

import java.text.MessageFormat;

import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.junit.After;
import org.junit.Test;

import com.aptana.editor.common.ExtendedFastPartitioner;
import com.aptana.editor.common.NullPartitionerSwitchStrategy;
import com.aptana.editor.common.text.rules.CompositePartitionScanner;
import com.aptana.editor.common.text.rules.NullSubPartitionScanner;
import com.aptana.editor.dtd.DTDSourceConfiguration;

/**
 * @author Chris Williams
 * @author Max Stepanov
 */
public class XMLPartitionScannerTest {

	private ExtendedFastPartitioner partitioner;

	private void assertContentType(String contentType, String code, int offset) {
		assertEquals(MessageFormat.format("Content type doesn't match expectations for: {0}({1})", code.charAt(offset), offset), contentType, getContentType(code, offset));
	}

	private void assertContentType(String contentType, String code, int offset, int length) {
		for (int i = 0; i < length; ++i) {
			assertContentType(contentType, code, offset+i);
		}
	}

	@After
	public void tearDown() throws Exception {
		partitioner = null;
	}

	private String getContentType(String content, int offset) {
		if (partitioner == null) {
			IDocument document = new Document(content);
			CompositePartitionScanner partitionScanner = new CompositePartitionScanner(XMLSourceConfiguration.getDefault().createSubPartitionScanner(),
					new NullSubPartitionScanner(), new NullPartitionerSwitchStrategy());
			partitioner = new ExtendedFastPartitioner(partitionScanner, XMLSourceConfiguration.getDefault().getContentTypes());
			partitionScanner.setPartitioner(partitioner);
			partitioner.connect(document);
			document.setDocumentPartitioner(partitioner);
		}
		return partitioner.getContentType(offset);
	}

	@Test
	public void testPreProcessorSpanningSingleLine() {
		String source = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>";
		assertContentType(XMLSourceConfiguration.PRE_PROCESSOR, source, 0, source.length());
	}

	@Test
	public void testPreProcessorSpanningMultipleLines() {
		String source = "<?xml version=\"1.0\"\n encoding=\"ISO-8859-1\"?>";
		assertContentType(XMLSourceConfiguration.PRE_PROCESSOR, source, 0, source.length());
	}

	@Test
	public void testCDataSpanningSingleLine() {
		String source = "<![CDATA[var one = 1;]]>";
		assertContentType(XMLSourceConfiguration.CDATA, source, 0, source.length());
	}

	@Test
	public void testCDataSpanningMultipleLines() {
		String source = "<![CDATA[var\n one\n = 1;\n]]>";
		assertContentType(XMLSourceConfiguration.CDATA, source, 0, source.length());
	}

	@Test
	public void testCommentSpanningSingleLine() {
		String source = "<!-- This is XML comment on one Line -->";
		assertContentType(XMLSourceConfiguration.COMMENT, source, 0, source.length());
	}

	@Test
	public void testCommentSpanningMultipleLines() {
		String source = "<!-- This is XML comment\nspanning multiple lines -->";
		assertContentType(XMLSourceConfiguration.COMMENT, source, 0, source.length());
	}

	@Test
	public void testOpeningTag() {
		String source = "<tag>";
		assertContentType(XMLSourceConfiguration.TAG, source, 0, source.length());
	}

	@Test
	public void testClosingTag() {
		String source = "</tag>";
		assertContentType(XMLSourceConfiguration.TAG, source, 0, source.length());
	}

	@Test
	public void testAllPartitions() {
		String source = "<?xml version=\"1.0\"\n encoding=\"ISO-8859-1\"?>\n"
			+ "<xml><head attr='single' name=\"double\"><style><![CDATA[var one =\n 1;]]></style></head><body>\n"
			+ "<!-- This is an XML comment -->\n" + "<p>Text</p></body></xml>";

		assertContentType(XMLSourceConfiguration.PRE_PROCESSOR, source, 0, 44);
		assertContentType(XMLSourceConfiguration.DEFAULT, source, 44);
		assertContentType(XMLSourceConfiguration.TAG, source, 45, 5);
		assertContentType(XMLSourceConfiguration.TAG, source, 50, 34);
		assertContentType(XMLSourceConfiguration.TAG, source, 84,7);
		assertContentType(XMLSourceConfiguration.CDATA, source, 91, 25);
		assertContentType(XMLSourceConfiguration.TAG, source, 116, 8);
		assertContentType(XMLSourceConfiguration.TAG, source, 124, 7);
		assertContentType(XMLSourceConfiguration.TAG, source, 131, 6);
		assertContentType(XMLSourceConfiguration.DEFAULT, source, 137);
		assertContentType(XMLSourceConfiguration.COMMENT, source, 138, 31);
	}

	@Test
	public void testDocType() {
		String source = "<?xml version=\"1.0\"\n encoding=\"ISO-8859-1\"?>\n"
			+ "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">\n"
			+ "<html>";
		assertContentType(XMLSourceConfiguration.PRE_PROCESSOR, source, 0, 44);
		assertContentType(XMLSourceConfiguration.DEFAULT, source, 44);
		assertContentType(XMLSourceConfiguration.DOCTYPE, source, 45, 121);
		assertContentType(XMLSourceConfiguration.DEFAULT, source, 166);
		assertContentType(XMLSourceConfiguration.TAG, source, 167, 6);
	}

	@Test
	public void testDocTypeWithDTD() {
		String source = "<?xml version=\"1.0\"\n encoding=\"ISO-8859-1\"?>\n"
			+ "<!DOCTYPE note [\n"
			+"<!ELEMENT note    (to+,from?,heading,img,body*)>\n"
			+"]>\n"
			+ "<note></note>";
		assertContentType(XMLSourceConfiguration.PRE_PROCESSOR, source, 0, 44);
		assertContentType(XMLSourceConfiguration.DEFAULT, source, 44);
		assertContentType(XMLSourceConfiguration.DOCTYPE, source, 45, 16);
		assertContentType(DTDSourceConfiguration.DEFAULT, source, 61);
		assertContentType(DTDSourceConfiguration.TAG, source, 62, 48);
		assertContentType(DTDSourceConfiguration.DEFAULT, source, 110);
		assertContentType(XMLSourceConfiguration.DOCTYPE, source, 111, 2);
		assertContentType(XMLSourceConfiguration.DEFAULT, source, 113);
		assertContentType(XMLSourceConfiguration.TAG, source, 114, 6);
		assertContentType(XMLSourceConfiguration.TAG, source, 120, 7);
	}

}
