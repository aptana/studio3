package com.aptana.js.core.build;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestResult;
import junit.framework.TestSuite;

public class CoreBuildTests extends TestCase
{

	public static Test suite()
	{
		TestSuite suite = new TestSuite(CoreBuildTests.class.getName())
		{
			@Override
			public void runTest(Test test, TestResult result)
			{
				System.err.println("Running test: " + test.toString());
				super.runTest(test, result);
			}
		};
		//$JUnit-BEGIN$
		suite.addTestSuite(JSTaskDetectorTest.class);
		//$JUnit-END$
		return suite;
	}

}
