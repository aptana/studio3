/*
 * Created on Feb 19, 2005
 *
 */
package com.aptana.radrails.editor.html;

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
public class HTMLSourcePartitionScannerTest extends TestCase
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
				HTMLSourceConfiguration.getDefault().createSubPartitionScanner(),
				new NullSubPartitionScanner(),
				new NullPartitionerSwitchStrategy());
		IDocumentPartitioner partitioner = new ExtendedFastPartitioner(partitionScanner,
					HTMLSourceConfiguration.getDefault().getContentTypes());
		partitionScanner.setPartitioner((IExtendedPartitioner) partitioner);
		partitioner.connect(document);
		document.setDocumentPartitioner(partitioner);
		DocumentContentTypeManager.getInstance().setDocumentContentType(document, IHTMLConstants.CONTENT_TYPE_HTML);
		DocumentContentTypeManager.getInstance().registerConfiguration(document, HTMLSourceConfiguration.getDefault());
		return partitioner.getContentType(offset);
	}

	public void testPartitioningOfCommentSpanningSingleLine()
	{
		String source = 
			"<!-- This is HTML comment on one Line -->\n";

		assertContentType(HTMLSourceConfiguration.HTML_COMMENT, source, 0);
		assertContentType(HTMLSourceConfiguration.HTML_COMMENT, source, 1);
		assertContentType(HTMLSourceConfiguration.HTML_COMMENT, source, 2);
		assertContentType(HTMLSourceConfiguration.HTML_COMMENT, source, 3);
		assertContentType(HTMLSourceConfiguration.HTML_COMMENT, source, 4);
		assertContentType(HTMLSourceConfiguration.HTML_COMMENT, source, 5);
		assertContentType(HTMLSourceConfiguration.HTML_COMMENT, source, 37);
		assertContentType(HTMLSourceConfiguration.HTML_COMMENT, source, 38);
		assertContentType(HTMLSourceConfiguration.HTML_COMMENT, source, 39);
		assertContentType(HTMLSourceConfiguration.HTML_COMMENT, source, 40);
		assertContentType(HTMLSourceConfiguration.DEFAULT, source, 41);
	}
	
	public void testPartitioningOfCommentSpanningMultipleLines()
	{
		String source = 
			"<!-- This is HTML comment\nspanning multiple lines -->\n";

		assertContentType(HTMLSourceConfiguration.HTML_COMMENT, source, 0);
		assertContentType(HTMLSourceConfiguration.HTML_COMMENT, source, 1);
		assertContentType(HTMLSourceConfiguration.HTML_COMMENT, source, 2);
		assertContentType(HTMLSourceConfiguration.HTML_COMMENT, source, 3);
		assertContentType(HTMLSourceConfiguration.HTML_COMMENT, source, 4);
		assertContentType(HTMLSourceConfiguration.HTML_COMMENT, source, 5);
		assertContentType(HTMLSourceConfiguration.HTML_COMMENT, source, 48);
		assertContentType(HTMLSourceConfiguration.HTML_COMMENT, source, 49);
		assertContentType(HTMLSourceConfiguration.HTML_COMMENT, source, 50);
		assertContentType(HTMLSourceConfiguration.HTML_COMMENT, source, 51);
		assertContentType(HTMLSourceConfiguration.HTML_COMMENT, source, 52);
		assertContentType(HTMLSourceConfiguration.DEFAULT, source, 53);
	}
	
}
