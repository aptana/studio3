package com.aptana.editor.erb;

import junit.framework.TestCase;

import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;

import com.aptana.editor.common.ExtendedFastPartitioner;
import com.aptana.editor.common.TextUtils;
import com.aptana.editor.common.text.rules.CompositePartitionScanner;
import com.aptana.editor.html.HTMLSourceConfiguration;
import com.aptana.editor.ruby.RubySourceConfiguration;

public class RHTMLSourcePartitionScannerTest extends TestCase
{

	private ExtendedFastPartitioner partitioner;

	@Override
	protected void tearDown() throws Exception
	{
		partitioner = null;
		super.tearDown();
	}

	public void testPartition()
	{
		String source = "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">\n" //$NON-NLS-1$
				+ "<html>\n" //$NON-NLS-1$
				+ "<head><script><% print \"Hello, world!\" %></script></head>\n" //$NON-NLS-1$
				+ "<body>\n" //$NON-NLS-1$
				+ "<p>The current time is <%= Time.now -%>.</p>\n" //$NON-NLS-1$
				+ "</body></html>"; //$NON-NLS-1$
		// DOCTYPE
		assertContentType(HTMLSourceConfiguration.HTML_DOCTYPE, source, 0);
		// html tag
		assertContentType(HTMLSourceConfiguration.HTML_TAG, source, 110); // '<'html
		// inline JS script
		assertContentType(HTMLSourceConfiguration.HTML_SCRIPT, source, 123); // '<'script
		// ruby start switch
		assertContentType(CompositePartitionScanner.START_SWITCH_TAG, source, 131); // '<'
		assertContentType(CompositePartitionScanner.START_SWITCH_TAG, source, 132); // '%'
		// inline Ruby inside the script
		assertContentType(RubySourceConfiguration.DEFAULT, source, 133); // ' 'print
		assertContentType(RubySourceConfiguration.DEFAULT, source, 155); // "' '
		// ruby end switch
		assertContentType(CompositePartitionScanner.END_SWITCH_TAG, source, 156); // '%'
		assertContentType(CompositePartitionScanner.END_SWITCH_TAG, source, 157); // '>'
		// back to html
		assertContentType(HTMLSourceConfiguration.HTML_TAG, source, 158); // '<'html
		// a different ruby start switch
		assertContentType(CompositePartitionScanner.START_SWITCH_TAG, source, 205); // '<'
		assertContentType(CompositePartitionScanner.START_SWITCH_TAG, source, 207); // %'='
		// inline Ruby inside HTML
		assertContentType(RubySourceConfiguration.DEFAULT, source, 208); // ' 'Time
		assertContentType(RubySourceConfiguration.DEFAULT, source, 217); // now' '
		// a different ruby end switch
		assertContentType(CompositePartitionScanner.END_SWITCH_TAG, source, 218); // '-'
		assertContentType(CompositePartitionScanner.END_SWITCH_TAG, source, 220); // '>'
		// back to html
		assertContentType(HTMLSourceConfiguration.DEFAULT, source, 221); // '\n'
	}

	private void assertContentType(String contentType, String code, int offset)
	{
		assertEquals("Content type doesn't match expectations for: " + code.charAt(offset), contentType, //$NON-NLS-1$
				getContentType(code, offset));
	}

	private String getContentType(String content, int offset)
	{
		if (partitioner == null)
		{
			IDocument document = new Document(content);
			CompositePartitionScanner partitionScanner = new CompositePartitionScanner(HTMLSourceConfiguration
					.getDefault().createSubPartitionScanner(), RubySourceConfiguration.getDefault()
					.createSubPartitionScanner(), ERBPartitionerSwitchStrategy.getDefault());
			partitioner = new ExtendedFastPartitioner(partitionScanner, TextUtils.combine(new String[][] {
					CompositePartitionScanner.SWITCHING_CONTENT_TYPES,
					HTMLSourceConfiguration.getDefault().getContentTypes(),
					RubySourceConfiguration.getDefault().getContentTypes() }));
			partitionScanner.setPartitioner(partitioner);
			partitioner.connect(document);
			document.setDocumentPartitioner(partitioner);
		}
		return partitioner.getContentType(offset);
	}
}
