package com.aptana.filesystem.secureftp.tests;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.kohsuke.junit.ParallelTestSuite;

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
			suite = new ParallelTestSuite(AllTests.class.getName(), 2);
//		}
		// $JUnit-BEGIN$
		suite.addTestSuite(SFTPConnectionTest.class);
		suite.addTestSuite(FTPSConnectionTest.class);
		suite.addTestSuite(FTPSConnectionWithBasePathTest.class);
		suite.addTestSuite(ImplicitFTPSConnectionTest.class);
		// $JUnit-END$
		return suite;
	}
}
