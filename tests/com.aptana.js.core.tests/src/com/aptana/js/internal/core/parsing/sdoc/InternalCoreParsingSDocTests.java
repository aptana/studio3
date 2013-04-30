package com.aptana.js.internal.core.parsing.sdoc;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestResult;
import junit.framework.TestSuite;

public class InternalCoreParsingSDocTests extends TestCase
{

	public static Test suite()
	{
		TestSuite suite = new TestSuite(InternalCoreParsingSDocTests.class.getName())
		{
			@Override
			public void runTest(Test test, TestResult result)
			{
				System.err.println("Running test: " + test.toString());
				super.runTest(test, result);
			}
		};
		// $JUnit-BEGIN$
		suite.addTestSuite(SDocFlexScannerTest.class);
		suite.addTestSuite(SDocParserTest.class);
		suite.addTestSuite(SDocTypeTokenScannerTest.class);
		// $JUnit-END$
		return suite;
	}

}
