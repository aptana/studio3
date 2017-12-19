/**
 * Aptana Studio
 * Copyright (c) 2005-2013 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.css.core.parsing;

import java.io.InputStream;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;

import com.aptana.core.tests.GlobalTimePerformanceTestCase;
import com.aptana.core.util.IOUtil;

import beaver.Symbol;

public class CSSScannerPerformanceTest extends GlobalTimePerformanceTestCase
{
	private CSSFlexScanner fScanner;

	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		fScanner = new CSSFlexScanner();
	}

	@Override
	protected void tearDown() throws Exception
	{
		fScanner = null;
		super.tearDown();
	}

	public void testWordpressAdminCSS() throws Exception
	{
		timeScan("wp-admin.css", 50);
	}

	public void testWordpressAdminDev() throws Exception
	{
		timeScan("wp-admin.dev.css", 50);
	}

	public void testFromMetadata() throws Exception
	{
		timeScan("from-metadata.css", 50);
	}

	public void testGithubFormatted() throws Exception
	{
		timeScan("github-formatted.css", 50);
	}

	public void testGithubMinimized() throws Exception
	{
		timeScan("github-minimized.css", 50);
	}

	/**
	 * timeScan
	 * 
	 * @param resourceName
	 * @param src
	 * @param numRuns
	 * @throws Exception
	 */
	private void timeScan(String resourceName, int numRuns) throws Exception
	{
		InputStream stream = FileLocator.openStream(Platform.getBundle("com.aptana.css.core.tests"),
				Path.fromPortableString("performance/" + resourceName), false);
		String src = IOUtil.read(stream);

		for (int i = 0; i < numRuns; i++)
		{
			startMeasuring();

			fScanner.setSource(src);

			Symbol symbol = fScanner.nextToken();

			while (symbol != null && symbol.getId() != 0)
			{
				symbol = fScanner.nextToken();
			}

			stopMeasuring();
		}

		commitMeasurements();
		assertPerformance();
	}
}
