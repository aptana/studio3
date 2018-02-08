/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.html;

import java.io.InputStream;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.Token;
import org.junit.experimental.categories.Category;

import com.aptana.core.tests.GlobalTimePerformanceTestCase;
import com.aptana.core.util.IOUtil;
import com.aptana.testing.categories.PerformanceTests;

@Category({ PerformanceTests.class })
public class HTMLTagScannerPerformanceTest extends GlobalTimePerformanceTestCase
{

	private HTMLTagScanner fScanner;

	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		fScanner = new HTMLTagScanner()
		{
			@Override
			protected IToken createToken(String string)
			{
				return new Token(string);
			}
		};
	}

	@Override
	protected void tearDown() throws Exception
	{
		fScanner = null;
		super.tearDown();
	}

	public void testAmazonHTML() throws Exception
	{
		// read in the file
		InputStream stream = FileLocator.openStream(Platform.getBundle("com.aptana.editor.html.tests"),
				Path.fromPortableString("performance/amazon.html"), false);
		String src = IOUtil.read(stream);
		IDocument document = new Document(src);

		// Ok now actually scan the thing, the real work
		for (int i = 0; i < 35; i++)
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
