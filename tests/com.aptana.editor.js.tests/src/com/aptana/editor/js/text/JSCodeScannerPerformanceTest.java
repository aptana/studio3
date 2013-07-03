/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
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
import org.eclipse.jface.text.rules.ITokenScanner;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.test.performance.GlobalTimePerformanceTestCase;

import com.aptana.core.util.IOUtil;
import com.aptana.editor.epl.tests.EditorTestHelper;
import com.aptana.js.core.JSCorePlugin;

public class JSCodeScannerPerformanceTest extends GlobalTimePerformanceTestCase
{
	private ITokenScanner fScanner;

	@Override
	protected void setUp() throws Exception
	{
		super.setUp();

		fScanner = new JSCodeScanner();
	}

	@Override
	protected void tearDown() throws Exception
	{
		fScanner = null;

		super.tearDown();
	}

	public void testScanUncompressedDojo() throws Exception
	{
		perfScan("dojo.js.uncompressed.js", 50);
	}

	public void testScanMinifiedDojo() throws Exception
	{
		perfScan("dojo.js.minified.js", 70);
	}

	public void testScanTiMobile() throws Exception
	{
		perfScan("timobile.js", 15);
	}

	public void testScanTinyMCE() throws Exception
	{
		perfScan("tiny_mce.js", 20);
	}

	protected void perfScan(String fileName, int iterations) throws IOException, CoreException
	{
		IDocument document = createDocument(fileName);

		EditorTestHelper.joinBackgroundActivities();
		int docLength = document.getLength();
		// Ok now actually scan the thing, the real work
		for (int i = 0; i < iterations; i++)
		{
			startMeasuring();
			fScanner.setRange(document, 0, docLength);
			while (fScanner.nextToken() != Token.EOF)
			{
				fScanner.getTokenOffset();
				fScanner.getTokenLength();
			}
			stopMeasuring();
		}
		commitMeasurements();
		assertPerformance();
	}

	protected IDocument createDocument(String fileName) throws IOException
	{
		InputStream stream = FileLocator.openStream(Platform.getBundle(JSCorePlugin.PLUGIN_ID),
				Path.fromPortableString("performance/" + fileName), false);
		return new Document(IOUtil.read(stream));
	}
}
