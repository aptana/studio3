/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.json;

import static org.junit.Assert.assertEquals;

import java.text.MessageFormat;

import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentPartitioner;
import org.eclipse.jface.text.rules.FastPartitioner;
import org.junit.After;
import org.junit.Test;

import com.aptana.editor.common.CommonEditorPlugin;
import com.aptana.editor.common.IPartitioningConfiguration;

/**
 * JSONSourcePartitionerScannerTest
 */
public class JSONSourcePartitionerScannerTest
{
	private IDocumentPartitioner _partitioner;

	/**
	 * assertContentType
	 * 
	 * @param expectedContentType
	 * @param source
	 * @param offset
	 */
	protected void assertContentType(String expectedContentType, String source, int offset)
	{
		char c = source.charAt(offset);
		String actualContentType = this.getContentType(source, offset);

		// HACK: Not sure how to force the default content type of our Document to
		// JSSourceConfiguration.DEFAULT, so we map those values to IDocument.DEFAULT_CONTENT_TYPE
		// as a workaround
		if (expectedContentType.equals(JSONSourceConfiguration.DEFAULT))
		{
			expectedContentType = IDocument.DEFAULT_CONTENT_TYPE;
		}

		assertEquals(
				MessageFormat.format("Content types do not match: {0}({1})", c, offset), expectedContentType, actualContentType); //$NON-NLS-1$
	}

	/**
	 * assertContentType
	 * 
	 * @param expectedContentType
	 * @param source
	 * @param startingOffset
	 * @param endingOffset
	 */
	protected void assertContentType(String expectedContentType, String source, int startingOffset, int endingOffset)
	{
		for (int offset = startingOffset; offset <= endingOffset; offset++)
		{
			assertContentType(expectedContentType, source, offset);
		}
	}

	/**
	 * getContentType
	 * 
	 * @param content
	 * @param offset
	 * @return
	 */
	private String getContentType(String content, int offset)
	{
		if (this._partitioner == null)
		{
			// NOTE: the following is based on SimpleDocumentProvider#connect(Object)
			IDocument document = new Document(content);
			IPartitioningConfiguration configuration = JSONSourceConfiguration.getDefault();

			this._partitioner = new FastPartitioner(new JSONPartitionScanner(), configuration.getContentTypes());
			this._partitioner.connect(document);
			document.setDocumentPartitioner(this._partitioner);

			CommonEditorPlugin.getDefault().getDocumentScopeManager().registerConfiguration(document, configuration);
		}

		return this._partitioner.getContentType(offset);
	}

	@After
	public void tearDown() throws Exception
	{
		this._partitioner = null;
	}

	/**
	 * testDefaultPartition
	 */
	@Test
	public void testDefaultPartition()
	{
		String source = "{ } [ ] , : true false null 10"; //$NON-NLS-1$

		this.assertContentType(JSONSourceConfiguration.DEFAULT, source, 0, source.length() - 1);
	}

	/**
	 * testDoubleQuotedStringPartition
	 */
	@Test
	public void testDoubleQuotedStringPartition()
	{
		String source = "\"hello\""; //$NON-NLS-1$

		this.assertContentType(JSONSourceConfiguration.STRING_DOUBLE, source, 0, source.length() - 1);
	}

	/**
	 * testSingleQuotedStringPartition
	 */
	@Test
	public void testSingleQuotedStringPartition()
	{
		String source = "'hello'"; //$NON-NLS-1$

		this.assertContentType(JSONSourceConfiguration.STRING_SINGLE, source, 0, source.length() - 1);
	}

	/**
	 * testPropertyPartition
	 */
	@Test
	public void testPropertyPartition()
	{
		String source = "'hello':"; //$NON-NLS-1$

		this.assertContentType(JSONSourceConfiguration.PROPERTY, source, 0, source.length() - 2);
		this.assertContentType(JSONSourceConfiguration.DEFAULT, source, source.length() - 1);
	}
}
