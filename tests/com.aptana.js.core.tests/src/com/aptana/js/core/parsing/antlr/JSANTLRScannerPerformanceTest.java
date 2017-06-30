/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.js.core.parsing.antlr;

import java.io.IOException;
import java.io.InputStream;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.Token;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.test.performance.GlobalTimePerformanceTestCase;

import com.aptana.core.util.IOUtil;
import com.aptana.js.core.JSCorePlugin;
import com.aptana.js.core.parsing.antlr.JSLexer;
import com.aptana.js.core.tests.ITestFiles;

public class JSANTLRScannerPerformanceTest extends GlobalTimePerformanceTestCase
{
	private JSLexer fScanner;

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
	@SuppressWarnings("resource")
	private String getSource(String resourceName) throws IOException
	{
		InputStream stream = FileLocator.openStream(Platform.getBundle(JSCorePlugin.PLUGIN_ID), new Path(resourceName),
				false);
		return getSource(stream);
	}

	/*
	 * (non-Javadoc)
	 * @see junit.framework.TestCase#tearDown()
	 */
	@Override
	protected void tearDown() throws Exception
	{
		fScanner = null;

		super.tearDown();
	}

	/**
	 * testDojo
	 * 
	 * @throws Exception
	 */
	public void testDojo() throws Exception
	{
		assertScan(75, ITestFiles.DOJO_FILES);
	}

	public void testDojoMinified() throws Exception
	{
		assertScan(500, "performance/dojo.js.minified.js");
	}

	/**
	 * testExt
	 * 
	 * @throws Exception
	 */
	public void testExt() throws Exception
	{
		assertScan(30, ITestFiles.EXT_FILES);
	}

	/**
	 * testTiMobile
	 * 
	 * @throws Exception
	 */
	public void testTiMobile() throws Exception
	{
		assertScan(50, ITestFiles.TIMOBILE_FILES);
	}

	/**
	 * testTinyMce
	 * 
	 * @throws Exception
	 */
	public void testTinyMce() throws Exception
	{
		assertScan(150, ITestFiles.TINY_MCE_FILES);
	}

	/**
	 * testJaxerFiles
	 * 
	 * @throws Exception
	 */
	public void testJaxerFiles() throws Exception
	{
		assertScan(15, ITestFiles.JAXER_FILES);
	}

	/**
	 * assertParse
	 * 
	 * @param resourceName
	 * @throws Exception
	 */
	private void assertScan(int numRuns, String... resources) throws Exception
	{
		for (String resourceName : resources)
		{
			timeScan(resourceName, numRuns);
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
	private void timeScan(String resourceName, int numRuns) throws Exception
	{
		this.timeScan(resourceName, getSource(resourceName), numRuns);
	}

	/**
	 * time
	 * 
	 * @param resourceName
	 * @throws Exception
	 */
	private void timeScan(String resourceName, String src, int numRuns) throws Exception
	{
		// re-use same charstream...
		CharStream stream = CharStreams.fromString(src);

		for (int i = 0; i < numRuns; i++)
		{
			startMeasuring();

			fScanner = new JSLexer(stream);

			Token symbol = fScanner.nextToken();

			while (symbol != null && symbol.getType() != Token.EOF)
			{
				symbol = fScanner.nextToken();
			}

			stopMeasuring();
		}
		stream.seek(0);
	}
}
