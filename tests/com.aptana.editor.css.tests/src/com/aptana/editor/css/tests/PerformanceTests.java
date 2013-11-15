/**
 * Aptana Studio
 * Copyright (c) 2005-2013 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.css.tests;

import java.text.MessageFormat;

import junit.framework.Test;
import junit.framework.TestResult;
import junit.framework.TestSuite;

import com.aptana.editor.css.CSSCodeScannerPerformanceTest;
import com.aptana.editor.css.tests.performance.OpenCSSEditorTest;

public class PerformanceTests
{

	public static Test suite()
	{
		TestSuite suite = new TestSuite("Performance Tests for com.aptana.editor.css plugin")
		{
			@Override
			public void runTest(Test test, TestResult result)
			{
				String msg = MessageFormat.format("Running test: {0}", test.toString());
				System.err.println(msg);
				super.runTest(test, result);
			}
		};
		// $JUnit-BEGIN$
		suite.addTestSuite(CSSCodeScannerPerformanceTest.class);
		suite.addTest(OpenCSSEditorTest.suite());
		// $JUnit-END$
		return suite;
	}
}
