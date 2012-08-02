package com.aptana.theme.tests;

import java.text.MessageFormat;

import junit.framework.Test;
import junit.framework.TestResult;
import junit.framework.TestSuite;

import com.aptana.core.logging.IdeLog;
import com.aptana.theme.ThemePerformanceTest;
import com.aptana.theme.ThemePlugin;

public class PerformanceTests
{
	public static Test suite()
	{
		TestSuite suite = new TestSuite(PerformanceTests.class.getName())
		{
			@Override
			public void runTest(Test test, TestResult result)
			{
				String msg = MessageFormat.format("Running test: {0}", test.toString());
				IdeLog.logError(ThemePlugin.getDefault(), msg);
				System.out.println(msg);
				super.runTest(test, result);
			}
		};
		// $JUnit-BEGIN$
		suite.addTestSuite(ThemePerformanceTest.class);
		// $JUnit-END$
		return suite;
	}
}
