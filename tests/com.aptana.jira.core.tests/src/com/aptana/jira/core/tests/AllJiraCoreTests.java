package com.aptana.jira.core.tests;

import org.junit.runners.Suite.SuiteClasses;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import com.aptana.jira.core.JiraManagerTest;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestResult;
import junit.framework.TestSuite;

@RunWith(Suite.class)
@SuiteClasses({JiraManagerTest.class, })
public class AllJiraCoreTests
{

//	public static Test suite()
//	{
//		TestSuite suite = new TestSuite(AllJiraCoreTests.class.getName())
//		{
//			@Override
//			public void runTest(Test test, TestResult result)
//			{
//				System.err.println("Running test: " + test.toString());
//				super.runTest(test, result);
//			}
//		};
//		// $JUnit-BEGIN$
//		suite.addTestSuite(JiraManagerTest.class);
//		// $JUnit-END$
//		return suite;
//	}
}
