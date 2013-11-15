/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.json;

import java.io.InputStream;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.test.performance.GlobalTimePerformanceTestCase;

import com.aptana.core.util.IOUtil;

/**
 * JSONScannerPerformanceTest
 */
public class JSONScannerPerformanceTest extends GlobalTimePerformanceTestCase
{
	private JSONSourceScanner fScanner;

	@Override
	protected void setUp() throws Exception
	{
		super.setUp();

		fScanner = new JSONSourceScanner();
	}

	@Override
	protected void tearDown() throws Exception
	{
		fScanner = null;

		super.tearDown();
	}

	public void testLongOneLiner() throws Exception
	{
		// read in the file
		InputStream stream = FileLocator.openStream(Platform.getBundle("com.aptana.editor.json.tests"), //$NON-NLS-1$
				Path.fromPortableString("performance/api-aptana-format.json"), false); //$NON-NLS-1$
		String src = IOUtil.read(stream);
		IDocument document = new Document(src);

		// Ok now actually scan the thing, the real work
		for (int i = 0; i < 15; i++)
		{
			startMeasuring();
			fScanner.setRange(document, 0, src.length());
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
}
