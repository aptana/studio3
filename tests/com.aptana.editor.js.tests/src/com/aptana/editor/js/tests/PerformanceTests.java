/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.js.tests;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.aptana.editor.js.JSCodeScannerPerformanceTest;
import com.aptana.editor.js.contentassist.JSIndexingPerformanceTest;
import com.aptana.editor.js.parsing.JSParserPerformanceTest;

public class PerformanceTests
{

	public static Test suite()
	{
		TestSuite suite = new TestSuite("Performance tests for com.aptana.editor.js plugin");
		// $JUnit-BEGIN$
		suite.addTestSuite(JSCodeScannerPerformanceTest.class);
		suite.addTestSuite(JSIndexingPerformanceTest.class);
		suite.addTestSuite(JSParserPerformanceTest.class);
		// $JUnit-END$
		return suite;
	}
}
