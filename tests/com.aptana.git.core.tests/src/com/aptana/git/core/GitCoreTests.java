package com.aptana.git.core;

import junit.framework.Test;
import junit.framework.TestSuite;

public class GitCoreTests
{

	public static Test suite()
	{
		TestSuite suite = new TestSuite(GitCoreTests.class.getName());
		//$JUnit-BEGIN$
		suite.addTestSuite(GitMoveDeleteIntegrationTest.class);
		suite.addTestSuite(GitMoveDeleteHookTest.class);
		//$JUnit-END$
		return suite;
	}

}
