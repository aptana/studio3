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
import com.aptana.parsing.IParseState;
import com.aptana.parsing.ParseState;

public class CSSParserPerformanceTest extends GlobalTimePerformanceTestCase
{

	private CSSParser fParser;

	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		fParser = new CSSParser();
	}

	@Override
	protected void tearDown() throws Exception
	{
		fParser = null;
		super.tearDown();
	}

	public void testWordpressAdminCSS() throws Exception
	{
		parseTest("wp-admin.css", 330);
	}

	public void testWordpressAdminDev() throws Exception
	{
		parseTest("wp-admin.dev.css", 1250);
	}

	public void testFromMetadata() throws Exception
	{
		parseTest("from-metadata.css", 50);
	}

	public void testGithubFormatted() throws Exception
	{
		parseTest("github-formatted.css", 25);
	}

	public void testGithubMinimized() throws Exception
	{
		parseTest("github-minimized.css", 25);
	}

	private void parseTest(String fileName, int iterations) throws Exception
	{
		InputStream stream = FileLocator.openStream(Platform.getBundle("com.aptana.css.core.tests"),
				Path.fromPortableString("performance/" + fileName), false);
		String src = IOUtil.read(stream);

		for (int i = 0; i < iterations; i++)
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
