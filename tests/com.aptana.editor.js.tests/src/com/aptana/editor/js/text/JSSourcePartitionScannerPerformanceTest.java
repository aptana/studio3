/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.js.text;

import java.io.IOException;
import java.io.InputStream;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentPartitioner;
import org.eclipse.test.performance.GlobalTimePerformanceTestCase;

import com.aptana.core.util.IOUtil;
import com.aptana.editor.common.CommonEditorPlugin;
import com.aptana.editor.common.ExtendedFastPartitioner;
import com.aptana.editor.common.IPartitioningConfiguration;
import com.aptana.editor.epl.tests.EditorTestHelper;
import com.aptana.editor.js.JSSourceConfiguration;
import com.aptana.js.core.JSCorePlugin;

public class JSSourcePartitionScannerPerformanceTest extends GlobalTimePerformanceTestCase
{
	private IDocumentPartitioner fPartitioner;
	private IPartitioningConfiguration configuration;

	@Override
	protected void setUp() throws Exception
	{
		super.setUp();

		configuration = JSSourceConfiguration.getDefault();
		fPartitioner = new ExtendedFastPartitioner(new JSSourcePartitionScannerJFlex(), configuration.getContentTypes());
	}

	@Override
	protected void tearDown() throws Exception
	{
		fPartitioner = null;
		configuration = null;

		super.tearDown();
	}

	public void testPartitionUncompressedDojo() throws Exception
	{
		perfPartition("dojo.js.uncompressed.js", 100);
	}

	public void testPartitionMinifiedDojo() throws Exception
	{
		perfPartition("dojo.js.minified.js", 70);
	}

	public void testPartitionTiMobile() throws Exception
	{
		perfPartition("timobile.js", 30);
	}

	public void testPartitionTinyMCE() throws Exception
	{
		perfPartition("tiny_mce.js", 45);
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

			// fPartitioner.computePartitioning(0, docLength);

			stopMeasuring();
		}
		commitMeasurements();
		assertPerformance();
	}

	protected String readFile(String fileName) throws IOException
	{
		InputStream stream = FileLocator.openStream(Platform.getBundle(JSCorePlugin.PLUGIN_ID),
				Path.fromPortableString("performance/" + fileName), false);
		return IOUtil.read(stream);
	}
}
