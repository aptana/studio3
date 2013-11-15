package com.aptana.editor.common.text;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestResult;
import junit.framework.TestSuite;

public class TextTests extends TestCase
{

	public static Test suite()
	{
		TestSuite suite = new TestSuite(TextTests.class.getName())
		{
			@Override
			public void runTest(Test test, TestResult result)
			{
				System.err.println("Running test: " + test.toString());
				super.runTest(test, result);
			}
		};
		//$JUnit-BEGIN$
		suite.addTestSuite(RubyRegexpAutoIndentStrategyTest.class);
		suite.addTestSuite(SingleTokenScannerTest.class);
		//$JUnit-END$
		return suite;
	}

}
