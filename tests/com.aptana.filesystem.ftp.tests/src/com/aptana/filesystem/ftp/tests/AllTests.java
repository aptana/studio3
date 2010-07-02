package com.aptana.filesystem.ftp.tests;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests
{

	public static Test suite()
	{
		TestSuite suite;
		// Run in parallel locally, not on unit test build...
//		String user = System.getenv("USER");
//		if (user != null && user.equals("hudson"))
//		{
//			suite = new TestSuite(AllTests.class.getName());
//		}
//		else
//		{
			suite = new TestSuite(AllTests.class.getName());
//		}
		// $JUnit-BEGIN$
		suite.addTestSuite(FTPConnectionTest.class);
		suite.addTestSuite(FTPConnectionWithBasePathTest.class);
		suite.addTestSuite(FTPProxiedConnectionTest.class);
		// $JUnit-END$
		return suite;
	}
}
