package com.aptana.editor.css.contentassist;

import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentPartitioner;

import com.aptana.editor.common.ExtendedFastPartitioner;
import com.aptana.editor.common.IExtendedPartitioner;
import com.aptana.editor.common.NullPartitionerSwitchStrategy;
import com.aptana.editor.common.text.rules.CompositePartitionScanner;
import com.aptana.editor.common.text.rules.NullSubPartitionScanner;
import com.aptana.editor.css.CSSSourceConfiguration;

public class TestUtil
{
	/**
	 * TestUtil
	 */
	private TestUtil()
	{
	}
	
	/**
	 * createDocument
	 * 
	 * @param partitionType
	 * @param source
	 * @return
	 */
	static IDocument createDocument(String source)
	{
		CompositePartitionScanner partitionScanner = new CompositePartitionScanner(
			CSSSourceConfiguration.getDefault().createSubPartitionScanner(),
			new NullSubPartitionScanner(),
			new NullPartitionerSwitchStrategy()
		);
		IDocumentPartitioner partitioner = new ExtendedFastPartitioner(
			partitionScanner,
			CSSSourceConfiguration.getDefault().getContentTypes()
		);
		partitionScanner.setPartitioner((IExtendedPartitioner) partitioner);
		
		final IDocument document = new Document(source);
		partitioner.connect(document);
		document.setDocumentPartitioner(partitioner);
		
		return document;
	}
}
