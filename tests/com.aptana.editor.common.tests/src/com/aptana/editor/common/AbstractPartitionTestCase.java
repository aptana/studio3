/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.common;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.rules.FastPartitioner;
import org.eclipse.jface.text.rules.IPartitionTokenScanner;

import com.aptana.core.util.StringUtil;

public abstract class AbstractPartitionTestCase
{

	protected abstract IPartitionTokenScanner createPartitionScanner();

	protected abstract String[] getContentTypes();

	protected void assertPartitions(IDocument document, String... expected) throws BadLocationException
	{
		String lastContentType = null;

		List<String> found = new ArrayList<String>();
		StringBuilder builder = new StringBuilder();
		int length = document.getLength();
		for (int i = 0; i < length; i++)
		{
			String contentType = document.getContentType(i);
			if (lastContentType == null)
			{
				// On the first content type found, start the string:
				// "__css_string_single:0:"
				builder.append("\"").append(contentType).append(':').append(i).append(':');
			}
			else if (!lastContentType.equals(contentType))
			{
				// When the content type changes it, finish the string: "__css_string_single:0:5", add it
				// and start a new one (i.e.: "__css_string_single:0:5", "__dftl_partition_content_type:5:")
				builder.append(i).append("\",");
				found.add(builder.toString());

				builder = new StringBuilder();
				builder.append("\"").append(contentType).append(':').append(i).append(':');
			}

			lastContentType = contentType;
		}
		builder.append("\",");
		found.add(builder.toString());

		// Now, transform the expected in a list to be transformed in a string with new-lines (which
		// will be used to actually do the comparison).

		List<String> expectedList = new ArrayList<String>();
		for (String expectedToken : expected)
		{
			expectedList.add("\"" + expectedToken + "\",");
		}

		assertEquals(StringUtil.join("\n", expectedList), StringUtil.join("\n", found));
	}

	protected void assertPartitions(String content, String... expected) throws BadLocationException
	{
		IDocument document = configDoc(content);
		assertPartitions(document, expected);
	}

	protected IDocument configDoc(String content)
	{
		IDocument document = new Document(content);
		String[] contentTypes = getContentTypes();

		FastPartitioner partitioner = new FastPartitioner(createPartitionScanner(), contentTypes);
		partitioner.connect(document);
		document.setDocumentPartitioner(partitioner);
		return document;
	}

}
