/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.html.parsing;

import java.io.InputStream;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.junit.experimental.categories.Category;

import com.aptana.core.tests.GlobalTimePerformanceTestCase;
import com.aptana.core.util.IOUtil;
import com.aptana.editor.epl.tests.EditorTestHelper;
import com.aptana.parsing.IParseState;
import com.aptana.testing.categories.PerformanceTests;

/**
 * @author cwilliams
 */
@Category({ PerformanceTests.class })
public class HTMLParserPerformanceTest extends GlobalTimePerformanceTestCase
{

	private HTMLParser fParser;

	@Override
	public void setUp() throws Exception
	{
		super.setUp();
		fParser = new HTMLParser();
	}

	@Override
	protected void tearDown() throws Exception
	{
		fParser = null;
		super.tearDown();
	}

	public void testAmazonFrontPage() throws Exception
	{
		parseTest("amazon.html", 1500);
	}

	public void testReddit() throws Exception
	{
		parseTest("reddit.html", 120);
	}

	public void testRedditNoCSSNoJS() throws Exception
	{
		parseTest("reddit-no-css-no-js.html", 175);
	}

	public void testBigHTML() throws Exception
	{
		parseTest("BigHTML.html", 10);
	}

	private void parseTest(String fileName, int iterations) throws Exception
	{
		InputStream stream = FileLocator.openStream(Platform.getBundle("com.aptana.editor.html.tests"),
				Path.fromOSString("performance/" + fileName), false);
		String src = IOUtil.read(stream);

		EditorTestHelper.joinBackgroundActivities();

		for (int i = 0; i < iterations; i++)
		{
			IParseState parseState = new HTMLParseState(src);
			startMeasuring();
			fParser.parse(parseState);
			stopMeasuring();
		}
		commitMeasurements();
		assertPerformance();
	}
}
