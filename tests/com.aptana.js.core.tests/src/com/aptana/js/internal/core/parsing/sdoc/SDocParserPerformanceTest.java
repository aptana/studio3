/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.js.internal.core.parsing.sdoc;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.junit.experimental.categories.Category;

import com.aptana.core.tests.GlobalTimePerformanceTestCase;
import com.aptana.core.util.IOUtil;
import com.aptana.js.core.JSCorePlugin;
import com.aptana.js.core.tests.ITestFiles;
import com.aptana.testing.categories.PerformanceTests;

@Category({ PerformanceTests.class })
public class SDocParserPerformanceTest extends GlobalTimePerformanceTestCase
{
	private static final Pattern COMMENT = Pattern.compile("/\\*\\*.*?\\*/", Pattern.DOTALL);
	private SDocParser fParser;

	/**
	 * getSource
	 * 
	 * @param stream
	 * @return
	 * @throws IOException
	 */
	private String getSource(InputStream stream) throws IOException
	{
		return IOUtil.read(stream);
	}

	/**
	 * getSource
	 * 
	 * @param resourceName
	 * @return
	 * @throws IOException
	 */
	private String getSource(String resourceName) throws IOException
	{
		InputStream stream = FileLocator.openStream(Platform.getBundle(JSCorePlugin.PLUGIN_ID), new Path(resourceName),
				false);
		return getSource(stream);
	}

	/*
	 * (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	@Override
	public void setUp() throws Exception
	{
		super.setUp();

		fParser = new SDocParser();
	}

	/*
	 * (non-Javadoc)
	 * @see junit.framework.TestCase#tearDown()
	 */
	@Override
	protected void tearDown() throws Exception
	{
		fParser = null;
		super.tearDown();
	}

	/**
	 * testTiMobile
	 * 
	 * @throws Exception
	 */
	public void testTiMobile() throws Exception
	{
		assertParse(10, ITestFiles.TIMOBILE_FILES);
	}

	/**
	 * assertParse
	 * 
	 * @param resourceName
	 * @throws Exception
	 */
	private void assertParse(int numRuns, String... resources) throws Exception
	{
		for (String resourceName : resources)
		{
			timeParse(resourceName, numRuns);
		}
		commitMeasurements();
		assertPerformance();
	}

	/**
	 * time
	 * 
	 * @param resourceName
	 * @param numRuns
	 * @throws Exception
	 */
	private void timeParse(String resourceName, int numRuns) throws Exception
	{
		this.timeParse(resourceName, getSource(resourceName), numRuns);
	}

	/**
	 * collectComments
	 * 
	 * @param src
	 * @return
	 */
	protected List<String> collectComments(String src)
	{
		List<String> result = new ArrayList<String>();
		Matcher m = COMMENT.matcher(src);

		while (m.find())
		{
			result.add(m.group());
		}

		return result;
	}

	/**
	 * time
	 * 
	 * @param resourceName
	 * @throws Exception
	 */
	private void timeParse(String resourceName, String src, int numRuns) throws Exception
	{
		List<String> comments = collectComments(src);

		// apply to parse state
		for (int i = 0; i < numRuns; i++)
		{
			startMeasuring();

			for (String comment : comments)
			{
				try
				{
					fParser.parse(comment);
				}
				catch (Exception e)
				{
					// fail(e.getMessage());
					// System.out.println("Failed\n" + comment);
				}
			}

			stopMeasuring();
		}
	}
}
