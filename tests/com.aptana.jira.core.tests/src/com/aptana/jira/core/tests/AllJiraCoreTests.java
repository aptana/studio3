package com.aptana.jira.core.tests;

import com.aptana.jira.core.JiraManagerTest;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestResult;
import junit.framework.TestSuite;

public class AllJiraCoreTests extends TestCase
{

	public static Test suite()
	{
		TestSuite suite = new TestSuite(AllJiraCoreTests.class.getName())
		{
			@Override
			public void runTest(Test test, TestResult result)
			{
				System.err.println("Running test: " + test.toString());
				super.runTest(test, result);
			}
		};
		// $JUnit-BEGIN$
		suite.addTestSuite(JiraManagerTest.class);
		// $JUnit-END$
		return suite;
	}
}
