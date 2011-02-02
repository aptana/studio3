/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.js.contentassist;

import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentPartitioner;

import com.aptana.editor.common.ExtendedFastPartitioner;
import com.aptana.editor.common.IExtendedPartitioner;
import com.aptana.editor.common.NullPartitionerSwitchStrategy;
import com.aptana.editor.common.text.rules.CompositePartitionScanner;
import com.aptana.editor.common.text.rules.NullSubPartitionScanner;
import com.aptana.editor.js.JSSourceConfiguration;

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
			JSSourceConfiguration.getDefault().createSubPartitionScanner(),
			new NullSubPartitionScanner(),
			new NullPartitionerSwitchStrategy()
		);
		IDocumentPartitioner partitioner = new ExtendedFastPartitioner(
			partitionScanner,
			JSSourceConfiguration.getDefault().getContentTypes()
		);
		partitionScanner.setPartitioner((IExtendedPartitioner) partitioner);
		
		final IDocument document = new Document(source);
		partitioner.connect(document);
		document.setDocumentPartitioner(partitioner);
		
		return document;
	}
}
