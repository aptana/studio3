package com.aptana.js.core.tests;

import java.text.MessageFormat;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestResult;
import junit.framework.TestSuite;

import com.aptana.core.logging.IdeLog;
import com.aptana.js.core.JSCorePlugin;
import com.aptana.js.core.parsing.JSFlexScannerPerformanceTest;
import com.aptana.js.core.parsing.JSParserPerformanceTest;
import com.aptana.js.internal.core.parsing.sdoc.SDocParserPerformanceTest;

public class PerformanceTests extends TestCase
{

	public static Test suite()
	{
		TestSuite suite = new TestSuite("JS Core performance tests")
		{
			@Override
			public void runTest(Test test, TestResult result)
			{
				String msg = MessageFormat.format("Running test: {0}", test.toString());
				IdeLog.logError(JSCorePlugin.getDefault(), msg);
				System.out.println(msg);
				super.runTest(test, result);
			}
		};

		// $JUnit-BEGIN$
		suite.addTestSuite(JSFlexScannerPerformanceTest.class);
		suite.addTestSuite(JSParserPerformanceTest.class);
		suite.addTestSuite(SDocParserPerformanceTest.class);
		// $JUnit-END$
		return suite;
	}

}
