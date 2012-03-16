/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.xml.parsing;

import java.io.InputStream;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.test.performance.PerformanceTestCase;

import com.aptana.core.util.IOUtil;
import com.aptana.parsing.IParseState;
import com.aptana.parsing.ParseState;

public class XMLParserPerformanceTest extends PerformanceTestCase
{

	private XMLParser fParser;

	@Override
	protected void setUp() throws Exception
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
		InputStream stream = FileLocator.openStream(Platform.getBundle("com.aptana.editor.xml.tests"),
				Path.fromPortableString("performance/dom_2.xml"), false);
		String src = IOUtil.read(stream);

		for (int i = 0; i < 50; i++)
		{
			IParseState parseState = new ParseState();
			parseState.setEditState(src);
			startMeasuring();
			fParser.parse(parseState);
			stopMeasuring();
		}
		commitMeasurements();
		assertPerformance();
	}
}
