/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.js.parsing;

import java.io.IOException;
import java.io.InputStream;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.test.performance.PerformanceTestCase;

import com.aptana.core.util.IOUtil;
import com.aptana.editor.js.JSPlugin;

public class JSParserPerformanceTest extends PerformanceTestCase
{

	private JSParser fParser;

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
		InputStream stream = FileLocator.openStream(Platform.getBundle(JSPlugin.PLUGIN_ID), new Path(resourceName),
				false);
		return getSource(stream);
	}

	/*
	 * (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		fParser = new JSParser();
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
	 * testDojo
	 * 
	 * @throws Exception
	 */
	public void testDojo() throws Exception
	{
		assertParse(10, ITestFiles.DOJO_FILES);
	}

	/**
	 * testExt
	 * 
	 * @throws Exception
	 */
	public void testExt() throws Exception
	{
		assertParse(10, ITestFiles.EXT_FILES);
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
	 * testTinyMce
	 * 
	 * @throws Exception
	 */
	public void testTinyMce() throws Exception
	{
		assertParse(10, ITestFiles.TINY_MCE_FILES);
	}

	/**
	 * testJaxerFiles
	 * 
	 * @throws Exception
	 */
	public void testJaxerFiles() throws Exception
	{
		assertParse(5, ITestFiles.JAXER_FILES);
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
	 * time
	 * 
	 * @param resourceName
	 * @throws Exception
	 */
	private void timeParse(String resourceName, String src, int numRuns) throws Exception
	{
		// apply to parse state
		JSParseState parseState = new JSParseState();
		parseState.setEditState(src, src, 0, 0);
		parseState.setAttachComments(false);
		parseState.setCollectComments(false);

		for (int i = 0; i < numRuns; i++)
		{
			startMeasuring();
			try
			{
				fParser.parse(parseState);
			}
			catch (Exception e)
			{
				fail(e.getMessage());
			}
			stopMeasuring();
		}
	}
}
