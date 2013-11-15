package com.aptana.index.core;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestResult;
import junit.framework.TestSuite;

public class IndexCoreTests extends TestCase
{

	public static Test suite()
	{
		TestSuite suite = new TestSuite(IndexCoreTests.class.getName())
		{
			@Override
			public void runTest(Test test, TestResult result)
			{
				System.err.println("Running test: " + test.toString());
				super.runTest(test, result);
			}
		};
		//$JUnit-BEGIN$
		suite.addTestSuite(FileStoreBuildContextTest.class);
		suite.addTestSuite(IndexContainerJobTest.class);
		suite.addTestSuite(IndexTest.class);
		//$JUnit-END$
		return suite;
	}

}
