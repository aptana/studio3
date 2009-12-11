/*
 * Created on Feb 19, 2005
 *
 */
package com.aptana.editor.js;

import junit.framework.TestCase;

import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentPartitioner;

import com.aptana.editor.common.CompositePartitionScanner;
import com.aptana.editor.common.DocumentContentTypeManager;
import com.aptana.editor.common.ExtendedFastPartitioner;
import com.aptana.editor.common.IExtendedPartitioner;
import com.aptana.editor.common.NullPartitionerSwitchStrategy;
import com.aptana.editor.common.NullSubPartitionScanner;

/**
 * @author Chris
 * @author Sandip
 */
public class JSSourcePartitionScannerTest extends TestCase
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
				JSSourceConfiguration.getDefault().createSubPartitionScanner(),
				new NullSubPartitionScanner(),
				new NullPartitionerSwitchStrategy());
		IDocumentPartitioner partitioner = new ExtendedFastPartitioner(partitionScanner,
				JSSourceConfiguration.getDefault().getContentTypes());
		partitionScanner.setPartitioner((IExtendedPartitioner) partitioner);
		partitioner.connect(document);
		document.setDocumentPartitioner(partitioner);
		DocumentContentTypeManager.getInstance().setDocumentContentType(document, IJSConstants.CONTENT_TYPE_JS);
		DocumentContentTypeManager.getInstance().registerConfiguration(document, JSSourceConfiguration.getDefault());
		return partitioner.getContentType(offset);
	}

	public void testPartitioningOfCommentSpanningSingleLine()
	{
		String source =
//                     1         2         3         4         5
//           012345678901234567890123456789012345678901234567890
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
//                     1         2         3         4         5
//           012345678901234567890123456789012345678901234567890
			"var width = Math.floor(viewWidth / characterWidth);\n";

		assertContentType(JSSourceConfiguration.DEFAULT, source, 0);
		assertContentType(JSSourceConfiguration.DEFAULT, source, 1);
		assertContentType(JSSourceConfiguration.DEFAULT, source, 35);
	}
	
	public void testComplexRegexp()
	{
		String source =
//                     1         2         3         4         5
//           012345678901234567890123456789012345678901234567890
			"var regexp = /^\\/\\*-secure-([\\s\\S]*)\\*\\/\\s*$/;\n";

		assertContentType(JSSourceConfiguration.DEFAULT, source, 0);
		assertContentType(JSSourceConfiguration.JS_REGEXP, source, 13);
		assertContentType(JSSourceConfiguration.JS_REGEXP, source, 44);
		assertContentType(JSSourceConfiguration.DEFAULT, source, 45);
	}
		
	public void testPartitioningOfCommentSpanningMultipleLines()
	{
		String source = 
//                     1         2          3         4         5
//           01234567890123456789012 3456789012345678901234567890
			"/* This is JS comment\nspanning multiple lines */\n";

		assertContentType(JSSourceConfiguration.JS_MULTILINE_COMMENT, source, 0);
		assertContentType(JSSourceConfiguration.JS_MULTILINE_COMMENT, source, 1);
		assertContentType(JSSourceConfiguration.JS_MULTILINE_COMMENT, source, 2);
		assertContentType(JSSourceConfiguration.JS_MULTILINE_COMMENT, source, 45);
		assertContentType(JSSourceConfiguration.JS_MULTILINE_COMMENT, source, 46);
		assertContentType(JSSourceConfiguration.JS_MULTILINE_COMMENT, source, 47);
		assertContentType(JSSourceConfiguration.DEFAULT, source, 48);
	}
	
}
