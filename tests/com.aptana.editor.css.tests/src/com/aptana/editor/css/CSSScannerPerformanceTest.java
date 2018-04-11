/**
 * Aptana Studio
 * Copyright (c) 2005-2013 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.css;

import java.io.InputStream;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.junit.experimental.categories.Category;

import com.aptana.core.tests.GlobalTimePerformanceTestCase;
import com.aptana.core.util.IOUtil;
import com.aptana.css.core.parsing.CSSTokenType;
import com.aptana.editor.css.parsing.CSSScanner;
import com.aptana.testing.categories.PerformanceTests;

@Category({ PerformanceTests.class })
public class CSSScannerPerformanceTest extends GlobalTimePerformanceTestCase
{

	private CSSScanner fScanner;

	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		fScanner = new CSSScanner();
	}

	@Override
	protected void tearDown() throws Exception
	{
		fScanner = null;
		super.tearDown();
	}

	public void testScanningWordpressAdminCSS() throws Exception
	{
		InputStream stream = FileLocator.openStream(Platform.getBundle("com.aptana.editor.css.tests"),
				Path.fromPortableString("performance/wp-admin.css"), false);
		String src = IOUtil.read(stream);

		for (int i = 0; i < 5; i++)
		{
			startMeasuring();
			fScanner.setSource(src);
			while (fScanner.nextToken().getId() != CSSTokenType.EOF.getShort())
			{
			}
			stopMeasuring();
		}
		commitMeasurements();
		assertPerformance();
	}
}
