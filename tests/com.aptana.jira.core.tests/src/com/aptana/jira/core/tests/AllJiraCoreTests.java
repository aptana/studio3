package com.aptana.jira.core.tests;

import com.aptana.jira.core.JiraManagerTest;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class AllJiraCoreTests extends TestCase
{

	public static Test suite()
	{
		TestSuite suite = new TestSuite(AllJiraCoreTests.class.getName());
		// $JUnit-BEGIN$
		suite.addTestSuite(JiraManagerTest.class);
		// $JUnit-END$
		return suite;
	}
}
