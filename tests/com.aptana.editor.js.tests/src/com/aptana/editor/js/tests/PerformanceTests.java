/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.js.tests;

import java.text.MessageFormat;

import junit.framework.Test;
import junit.framework.TestResult;
import junit.framework.TestSuite;

import com.aptana.core.logging.IdeLog;
import com.aptana.editor.js.JSCodeScannerPerformanceTest;
import com.aptana.editor.js.JSPlugin;
import com.aptana.editor.js.contentassist.JSBuildPerformanceTest;
import com.aptana.editor.js.contentassist.JSIndexingPerformanceTest;
import com.aptana.editor.js.parsing.JSParserPerformanceTest;
import com.aptana.editor.js.parsing.JSScannerPerformanceTest;
import com.aptana.editor.js.sdoc.parsing.SDocParserPerformanceTest;
import com.aptana.editor.js.tests.performance.OpenJSEditorTest;

public class PerformanceTests
{

	public static Test suite()
	{
		TestSuite suite = new TestSuite("Performance tests for com.aptana.editor.js plugin")
		{
			@Override
			public void runTest(Test test, TestResult result)
			{
				String msg = MessageFormat.format("Running test: {0}", test.toString());
				IdeLog.logError(JSPlugin.getDefault(), msg);
				System.out.println(msg);
				super.runTest(test, result);
			}
		};
		// $JUnit-BEGIN$
		suite.addTestSuite(JSBuildPerformanceTest.class);
		suite.addTestSuite(JSCodeScannerPerformanceTest.class);
		suite.addTestSuite(JSIndexingPerformanceTest.class);
		suite.addTestSuite(JSParserPerformanceTest.class);
		suite.addTestSuite(JSScannerPerformanceTest.class);
		suite.addTestSuite(SDocParserPerformanceTest.class);
		suite.addTest(OpenJSEditorTest.suite());
		// $JUnit-END$
		return suite;
	}
}
