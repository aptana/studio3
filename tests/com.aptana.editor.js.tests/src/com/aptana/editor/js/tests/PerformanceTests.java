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

import com.aptana.editor.js.contentassist.JSBuildPerformanceTest;
import com.aptana.editor.js.contentassist.JSContentAssistProcessorPerformanceTest;
import com.aptana.editor.js.contentassist.JSIndexingPerformanceTest;
import com.aptana.editor.js.tests.performance.JSLintValidatorPerformanceTest;
import com.aptana.editor.js.tests.performance.JSParserValidatorPerformanceTest;
import com.aptana.editor.js.tests.performance.JSStyleValidatorPerformanceTest;
import com.aptana.editor.js.tests.performance.OpenJSEditorTest;
import com.aptana.editor.js.text.JSCodeScannerPerformanceTest;
import com.aptana.editor.js.text.JSSourcePartitionScannerPerformanceTest;

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
				System.err.println(msg);
				super.runTest(test, result);
			}
		};
		// $JUnit-BEGIN$
		// content assist
		suite.addTestSuite(JSBuildPerformanceTest.class);
		suite.addTestSuite(JSContentAssistProcessorPerformanceTest.class);
		suite.addTestSuite(JSIndexingPerformanceTest.class);
		// Text/Coloring/Partitioning
		suite.addTestSuite(JSCodeScannerPerformanceTest.class);
		suite.addTestSuite(JSSourcePartitionScannerPerformanceTest.class);
		// General/Validation
		suite.addTestSuite(JSLintValidatorPerformanceTest.class);
		suite.addTestSuite(JSParserValidatorPerformanceTest.class);
		suite.addTestSuite(JSStyleValidatorPerformanceTest.class);
		suite.addTest(OpenJSEditorTest.suite());
		// $JUnit-END$
		return suite;
	}
}
