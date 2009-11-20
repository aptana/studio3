/*
 * Created on Feb 19, 2005
 *
 */
package com.aptana.radrails.editor.css;

import junit.framework.TestCase;

import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentPartitioner;

import com.aptana.radrails.editor.common.CompositePartitionScanner;
import com.aptana.radrails.editor.common.DocumentContentTypeManager;
import com.aptana.radrails.editor.common.ExtendedFastPartitioner;
import com.aptana.radrails.editor.common.IExtendedPartitioner;
import com.aptana.radrails.editor.common.NullPartitionerSwitchStrategy;
import com.aptana.radrails.editor.common.NullSubPartitionScanner;

/**
 * @author Chris
 * @author Sandip
 */
public class CSSSourcePartitionScannerTest extends TestCase
{

	private void assertContentType(String contentType, String code, int offset)
	{
		assertEquals("Content type doesn't match expectations for: " + code.charAt(offset), contentType,
				getContentType(code, offset));
	}

	private String getContentType(String content, int offset)
	{
		IDocument document = new Document(content);
		CompositePartitionScanner partitionScanner = new CompositePartitionScanner(
				CSSSourceConfiguration.getDefault().createSubPartitionScanner(),
				new NullSubPartitionScanner(),
				new NullPartitionerSwitchStrategy());
		IDocumentPartitioner partitioner = new ExtendedFastPartitioner(partitionScanner,
				CSSSourceConfiguration.getDefault().getContentTypes());
		partitionScanner.setPartitioner((IExtendedPartitioner) partitioner);
		partitioner.connect(document);
		document.setDocumentPartitioner(partitioner);
		DocumentContentTypeManager.getInstance().setDocumentContentType(document, ICSSConstants.CONTENT_TYPE_CSS);
		DocumentContentTypeManager.getInstance().registerConfiguration(document, CSSSourceConfiguration.getDefault());
		return partitioner.getContentType(offset);
	}

	public void testPartitioningOfCommentSpanningSingleLine()
	{
		String source =
//                     1         2         3         4         5
//           012345678901234567890123456789012345678901234567890
			"/* This is CSS comment on one Line */\n";

		assertContentType(CSSSourceConfiguration.MULTILINE_COMMENT, source, 0);
		assertContentType(CSSSourceConfiguration.MULTILINE_COMMENT, source, 1);
		assertContentType(CSSSourceConfiguration.MULTILINE_COMMENT, source, 2);
		assertContentType(CSSSourceConfiguration.MULTILINE_COMMENT, source, 34);
		assertContentType(CSSSourceConfiguration.MULTILINE_COMMENT, source, 35);
		assertContentType(CSSSourceConfiguration.MULTILINE_COMMENT, source, 36);
		assertContentType(CSSSourceConfiguration.DEFAULT, source, 37);
	}
	
	public void testPartitioningOfCommentSpanningMultipleLines()
	{
		String source = 
//                     1         2          3         4         5
//           01234567890123456789012 3456789012345678901234567890
			"/* This is CSS comment\nspanning multiple lines */\n";

		assertContentType(CSSSourceConfiguration.MULTILINE_COMMENT, source, 0);
		assertContentType(CSSSourceConfiguration.MULTILINE_COMMENT, source, 1);
		assertContentType(CSSSourceConfiguration.MULTILINE_COMMENT, source, 2);
		assertContentType(CSSSourceConfiguration.MULTILINE_COMMENT, source, 46);
		assertContentType(CSSSourceConfiguration.MULTILINE_COMMENT, source, 47);
		assertContentType(CSSSourceConfiguration.MULTILINE_COMMENT, source, 48);
		assertContentType(CSSSourceConfiguration.DEFAULT, source, 49);
	}
	
}
