package com.aptana.editor.markdown;

import junit.framework.TestCase;

import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;

import com.aptana.editor.common.ExtendedFastPartitioner;
import com.aptana.editor.common.NullPartitionerSwitchStrategy;
import com.aptana.editor.common.text.rules.CompositePartitionScanner;
import com.aptana.editor.common.text.rules.NullSubPartitionScanner;

public class MarkdownPartitionScannerTest extends TestCase
{

	private ExtendedFastPartitioner partitioner;

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
			CompositePartitionScanner partitionScanner = new CompositePartitionScanner(MarkdownSourceConfiguration
					.getDefault().createSubPartitionScanner(), new NullSubPartitionScanner(),
					new NullPartitionerSwitchStrategy());
			partitioner = new ExtendedFastPartitioner(partitionScanner, MarkdownSourceConfiguration.getDefault()
					.getContentTypes());
			partitionScanner.setPartitioner(partitioner);
			partitioner.connect(document);
			document.setDocumentPartitioner(partitioner);
		}
		return partitioner.getContentType(offset);
	}

	public void testLevel1ATXHeader()
	{
		String source = "#  HTML5 ";

		assertContentType(MarkdownSourceConfiguration.HEADING, source, 0);
		assertContentType(MarkdownSourceConfiguration.HEADING, source, source.length() - 1);
	}

	public void testLevel2ATXHeader()
	{
		String source = "## License:";

		assertContentType(MarkdownSourceConfiguration.HEADING, source, 0);
		assertContentType(MarkdownSourceConfiguration.HEADING, source, source.length() - 1);
	}

	public void testLevel3ATXHeader()
	{
		String source = "### v.0.9.1 : August 13th, 2010";

		assertContentType(MarkdownSourceConfiguration.HEADING, source, 0);
		assertContentType(MarkdownSourceConfiguration.HEADING, source, source.length() - 1);
	}

	public void testLevel5ATXHeader()
	{
		String source = "##### Thanks:";

		assertContentType(MarkdownSourceConfiguration.HEADING, source, 0);
		assertContentType(MarkdownSourceConfiguration.HEADING, source, source.length() - 1);
	}

	public void testAsteriskList()
	{
		String source = "* Modernizr: MIT/BSD license";

		assertContentType(MarkdownSourceConfiguration.UNNUMBERED_LIST, source, 0);
		assertContentType(MarkdownSourceConfiguration.UNNUMBERED_LIST, source, source.length() - 1);
	}

	public void testMultiplePartitions()
	{
		String source = "#  HTML5 Boilerplate [http://html5boilerplate.com](http://html5boilerplate.com)\n" + "\n\n"
				+ "## License:\n" + "\n" + "Major components:\n" + "\n" + "* Modernizr: MIT/BSD license";
		// HTML 5 heading
		assertContentType(MarkdownSourceConfiguration.HEADING, source, 0);
		assertContentType(MarkdownSourceConfiguration.HEADING, source, 78);
		// newline
		 assertContentType(MarkdownSourceConfiguration.DEFAULT, source, 80);
		// License heading
		assertContentType(MarkdownSourceConfiguration.HEADING, source, 82);
		assertContentType(MarkdownSourceConfiguration.HEADING, source, 92);
		// newline
		 assertContentType(MarkdownSourceConfiguration.DEFAULT, source, 94);
		// Major components
		assertContentType(MarkdownSourceConfiguration.DEFAULT, source, 95);
		assertContentType(MarkdownSourceConfiguration.DEFAULT, source, 111);
		// Modernizr list item
		assertContentType(MarkdownSourceConfiguration.UNNUMBERED_LIST, source, 114);
		assertContentType(MarkdownSourceConfiguration.UNNUMBERED_LIST, source, 141);
	}
}
