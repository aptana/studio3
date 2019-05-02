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
import com.aptana.parsing.IParseState;
import com.aptana.parsing.ParseState;
import com.aptana.xml.core.tests.PerformanceTests;

@Category({ PerformanceTests.class })
public class XMLParserPerformanceTest extends GlobalTimePerformanceTestCase
{

	private XMLParser fParser;

	@Override
	public void setUp() throws Exception
	{
		super.setUp();
		fParser = new XMLParser();
	}

	@Override
	protected void tearDown() throws Exception
	{
		fParser = null;
		super.tearDown();
	}

	public void testDOM2XMLMetadata() throws Exception
	{
		InputStream stream = FileLocator.openStream(Platform.getBundle("com.aptana.xml.core.tests"),
				Path.fromPortableString("performance/dom_2.xml"), false);
		String src = IOUtil.read(stream);

		for (int i = 0; i < 50; i++)
		{
			IParseState parseState = new ParseState(src);
			startMeasuring();
			fParser.parse(parseState);
			stopMeasuring();
		}
		commitMeasurements();
		assertPerformance();
	}
}
