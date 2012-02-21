/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.css.tests;

import java.text.MessageFormat;

import junit.framework.Test;
import junit.framework.TestResult;
import junit.framework.TestSuite;

import com.aptana.core.logging.IdeLog;
import com.aptana.editor.css.CSSCodeScannerPerformanceTest;
import com.aptana.editor.css.CSSParserPerformanceTest;
import com.aptana.editor.css.CSSPlugin;
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
				IdeLog.logError(CSSPlugin.getDefault(), msg);
				System.out.println(msg);
				super.runTest(test, result);
			}
		};
		// $JUnit-BEGIN$
		suite.addTestSuite(CSSCodeScannerPerformanceTest.class);
		suite.addTestSuite(CSSParserPerformanceTest.class);
		suite.addTest(OpenCSSEditorTest.suite());
		// $JUnit-END$
		return suite;
	}
}
