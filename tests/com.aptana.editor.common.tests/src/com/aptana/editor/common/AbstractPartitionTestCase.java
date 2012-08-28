/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.common;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.rules.FastPartitioner;
import org.eclipse.jface.text.rules.IPartitionTokenScanner;

import com.aptana.core.util.StringUtil;

public abstract class AbstractPartitionTestCase extends TestCase
{

	protected abstract IPartitionTokenScanner createPartitionScanner();

	protected abstract String[] getContentTypes();

	protected void assertPartitions(IDocument document, String... expected) throws BadLocationException
	{
		String lastContentType = null;

		List<String> found = new ArrayList<String>();
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < document.getLength(); i++)
		{
			String contentType = document.getContentType(i);
			if (lastContentType == null)
			{

				builder.append("\"");
				builder.append(contentType);
				builder.append(':');
				builder.append(i);
				builder.append(':');

			}
			else if (!lastContentType.equals(contentType))
			{
				builder.append(i);
				builder.append("\",");
				found.add(builder.toString());
				builder = new StringBuilder();
				builder.append("\"");
				builder.append(contentType);
				builder.append(':');
				builder.append(i);
				builder.append(':');
			}

			lastContentType = contentType;
		}
		builder.append("\",");
		found.add(builder.toString());

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
