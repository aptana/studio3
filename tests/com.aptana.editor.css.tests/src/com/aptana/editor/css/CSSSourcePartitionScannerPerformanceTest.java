/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.css;

import java.io.IOException;
import java.io.InputStream;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentPartitioner;
import org.junit.experimental.categories.Category;

import com.aptana.core.tests.GlobalTimePerformanceTestCase;
import com.aptana.core.util.IOUtil;
import com.aptana.editor.common.CommonEditorPlugin;
import com.aptana.editor.common.ExtendedFastPartitioner;
import com.aptana.editor.common.IPartitioningConfiguration;
import com.aptana.editor.epl.tests.EditorTestHelper;
import com.aptana.testing.categories.PerformanceTests;

@Category({ PerformanceTests.class })
public class CSSSourcePartitionScannerPerformanceTest extends GlobalTimePerformanceTestCase
{
	private IDocumentPartitioner fPartitioner;
	private IPartitioningConfiguration configuration;

	@Override
	protected void setUp() throws Exception
	{
		super.setUp();

		configuration = CSSSourceConfiguration.getDefault();
		fPartitioner = new ExtendedFastPartitioner(new CSSSourcePartitionScannerJFlex(),
				configuration.getContentTypes());
	}

	@Override
	protected void tearDown() throws Exception
	{
		fPartitioner = null;
		configuration = null;

		super.tearDown();
	}

	public void testScanningFromMetadata() throws Exception
	{
		perfPartition("from-metadata.css", 10);
	}

	public void testGithubFormatted() throws Exception
	{
		perfPartition("github-formatted.css", 20);
	}

	public void testGithubMinimized() throws Exception
	{
		perfPartition("github-minimized.css", 20);
	}

	public void testScanningWordpressAdminCSS() throws Exception
	{
		perfPartition("wp-admin.css", 80);
	}

	public void testScanningWordpressAdminDevCSS() throws Exception
	{
		perfPartition("wp-admin.dev.css", 30);
	}

	public void testScanningYuiCSS() throws Exception
	{
		perfPartition("yui.css", 80);
	}

	protected void perfPartition(String fileName, int iterations) throws IOException, CoreException
	{
		String src = readFile(fileName);
		// int docLength = src.length();

		EditorTestHelper.joinBackgroundActivities();

		// Ok now actually scan the thing, the real work
		for (int i = 0; i < iterations; i++)
		{
			IDocument document = new Document(src);

			startMeasuring();

			// Stolen from JSDocumentProvider
			fPartitioner.connect(document);
			document.setDocumentPartitioner(fPartitioner);
			CommonEditorPlugin.getDefault().getDocumentScopeManager().registerConfiguration(document, configuration);

			stopMeasuring();
		}
		commitMeasurements();
		assertPerformance();
	}

	protected String readFile(String fileName) throws IOException
	{
		InputStream stream = FileLocator.openStream(Platform.getBundle("com.aptana.editor.css.tests"),
				Path.fromPortableString("performance/" + fileName), false);
		return IOUtil.read(stream);
	}
}
