/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.html;

import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentPartitioner;

import com.aptana.editor.common.ExtendedFastPartitioner;
import com.aptana.editor.common.IExtendedPartitioner;
import com.aptana.editor.common.NullPartitionerSwitchStrategy;
import com.aptana.editor.common.contentassist.LexemeProvider;
import com.aptana.editor.common.text.rules.CompositePartitionScanner;
import com.aptana.editor.common.text.rules.NullSubPartitionScanner;
import com.aptana.editor.html.parsing.lexer.HTMLTokenType;

public class HTMLTestUtil
{
	/**
	 * createDocument
	 * 
	 * @param partitionType
	 * @param source
	 * @return
	 */
	public static IDocument createDocument(String source, boolean stripCursor)
	{
		if (stripCursor)
		{
			source = source.replaceAll("\\|", "");
		}

		CompositePartitionScanner partitionScanner = new CompositePartitionScanner(HTMLSourceConfiguration.getDefault()
				.createSubPartitionScanner(), new NullSubPartitionScanner(), new NullPartitionerSwitchStrategy());
		IDocumentPartitioner partitioner = new ExtendedFastPartitioner(partitionScanner, HTMLSourceConfiguration
				.getDefault().getContentTypes());
		partitionScanner.setPartitioner((IExtendedPartitioner) partitioner);

		final IDocument document = new Document(source);
		partitioner.connect(document);
		document.setDocumentPartitioner(partitioner);

		return document;
	}

	/**
	 * createLexemeProvider
	 * 
	 * @param document
	 * @param offset
	 * @return
	 */
	public static LexemeProvider<HTMLTokenType> createLexemeProvider(IDocument document, int offset)
	{
		int documentLength = document.getLength();

		// account for last position returning an empty IDocument default partition
		int lexemeProviderOffset = (offset >= documentLength) ? documentLength - 1 : offset;

		return new LexemeProvider<HTMLTokenType>(document, lexemeProviderOffset, new HTMLScopeScanner())
		{
			@Override
			protected HTMLTokenType getTypeFromData(Object data)
			{
				return HTMLTokenType.get((String) data);
			}
		};
	}

	/**
	 * Finds the cursor position in the specific text
	 * 
	 * @param text
	 * @return
	 */
	public static int findCursorOffset(String text)
	{
		return text.indexOf("|");
	}

}
