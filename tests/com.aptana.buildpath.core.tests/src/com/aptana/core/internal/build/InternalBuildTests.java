package com.aptana.core.internal.build;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestResult;
import junit.framework.TestSuite;

public class InternalBuildTests extends TestCase
{

	public static Test suite()
	{
		TestSuite suite = new TestSuite(InternalBuildTests.class.getName())
		{
			@Override
			public void runTest(Test test, TestResult result)
			{
				System.err.println("Running test: " + test.toString());
				super.runTest(test, result);
			}
		};
		// $JUnit-BEGIN$
		suite.addTestSuite(BuildParticipantWorkingCopyTest.class);
		suite.addTestSuite(BuildParticipantManagerTest.class);
		suite.addTestSuite(IndexBuildParticipantTest.class);
		// $JUnit-END$
		return suite;
	}

}
