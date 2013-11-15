/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.js.core.parsing;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.test.performance.GlobalTimePerformanceTestCase;
import org.eclipse.test.performance.Performance;

import com.aptana.core.util.IOUtil;
import com.aptana.js.core.JSCorePlugin;
import com.aptana.js.core.tests.ITestFiles;

public class JSParserPerformanceTest extends GlobalTimePerformanceTestCase
{

	private JSParser fParser;

	/**
	 * assertLocalFiles
	 * 
	 * @param root
	 * @throws Exception
	 */
	public void assertLocalFiles(File root) throws Exception
	{
		List<File> files = collectFiles(root);

		for (File file : files)
		{
			if (fPerformanceMeter != null)
			{
				fPerformanceMeter.dispose();
			}

			Performance performance = Performance.getDefault();
			fPerformanceMeter = performance.createPerformanceMeter(file.getAbsolutePath());

			FileInputStream fis = new FileInputStream(file);
			String source = getSource(fis);
			fis.close();
			timeParse(file.getName(), source, 10);

			commitMeasurements();
			assertPerformance();
		}
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
			if (fPerformanceMeter != null)
			{
				fPerformanceMeter.dispose();
			}

			Performance performance = Performance.getDefault();
			fPerformanceMeter = performance.createPerformanceMeter(resourceName);

			timeParse(resourceName, numRuns);

			commitMeasurements();
			assertPerformance();
		}
	}

	/**
	 * collectFiles
	 * 
	 * @param root
	 * @return
	 */
	private List<File> collectFiles(File root)
	{
		List<File> result = new ArrayList<File>();
		final Queue<File> directories = new LinkedList<File>();

		directories.offer(root);

		while (!directories.isEmpty())
		{
			File directory = directories.poll();
			File[] files = directory.listFiles(new FileFilter()
			{
				public boolean accept(File pathname)
				{
					if (pathname.isDirectory())
					{
						directories.add(pathname);
						return false;
					}

					return pathname.getName().toLowerCase().endsWith(".js");
				}
			});

			result.addAll(Arrays.asList(files));
		}

		return result;
	}

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
		assertParse(1000, ITestFiles.DOJO_FILES);
	}

	public void testDojoMinified() throws Exception
	{
		assertParse(1000, "performance/dojo.js.minified.js");
	}

	/**
	 * testExt
	 * 
	 * @throws Exception
	 */
	public void testExt() throws Exception
	{
		assertParse(15, ITestFiles.EXT_FILES);
	}

	/**
	 * testJaxerFiles
	 * 
	 * @throws Exception
	 */
	public void testJaxerFiles() throws Exception
	{
		assertParse(50, ITestFiles.JAXER_FILES);
	}

	/**
	 * testTiMobile
	 * 
	 * @throws Exception
	 */
	public void testTiMobile() throws Exception
	{
		assertParse(375, ITestFiles.TIMOBILE_FILES);
	}

	/**
	 * testTinyMce
	 * 
	 * @throws Exception
	 */
	public void testTinyMce() throws Exception
	{
		assertParse(430, ITestFiles.TINY_MCE_FILES);
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
		JSParseState parseState = new JSParseState(src);

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
