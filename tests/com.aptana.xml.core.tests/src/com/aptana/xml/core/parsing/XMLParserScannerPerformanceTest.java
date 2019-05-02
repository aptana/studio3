/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.xml.core.parsing;

import java.io.InputStream;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.junit.experimental.categories.Category;

import com.aptana.core.tests.GlobalTimePerformanceTestCase;
import com.aptana.core.util.IOUtil;
import com.aptana.dtd.core.parsing.Terminals;
import com.aptana.testing.categories.PerformanceTests;

@Category({ PerformanceTests.class })
public class XMLParserScannerPerformanceTest extends GlobalTimePerformanceTestCase
{

	private XMLScanner fScanner;

	@Override
	public void setUp() throws Exception
	{
		super.setUp();
		fScanner = new XMLScanner();
	}

	@Override
	protected void tearDown() throws Exception
	{
		fScanner = null;
		super.tearDown();
	}

	public void testDOM2XMLMetadata() throws Exception
	{
		InputStream stream = FileLocator.openStream(Platform.getBundle("com.aptana.xml.core.tests"),
				Path.fromPortableString("performance/dom_2.xml"), false);
		String src = IOUtil.read(stream);

		for (int i = 0; i < 500; i++)
		{
			startMeasuring();
			fScanner.setSource(src);
			while (fScanner.nextToken().getId() != Terminals.EOF)
			{
			}
			stopMeasuring();
		}
		commitMeasurements();
		assertPerformance();
	}
}
